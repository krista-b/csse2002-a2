package bms.floor;

import bms.room.Room;
import bms.room.RoomType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceScheduleTest {
    private Room roomStudy;
    private Room roomOffice;
    private Room roomLab;
    List<Room> roomOrder;
    private MaintenanceSchedule schedule;

    @Before
    public void setUp() throws Exception {
        roomOrder = new ArrayList<>();
        Floor floor = new Floor(1, 10, 10);
        roomStudy = new Room(101, RoomType.STUDY, 10);
        roomOffice = new Room(102, RoomType.OFFICE, 5);
        roomLab = new Room(103, RoomType.LABORATORY, 10);
        floor.addRoom(roomStudy);
        floor.addRoom(roomOffice);
        floor.addRoom(roomLab);
        roomOrder.add(roomStudy);
        roomOrder.add(roomOffice);
        roomOrder.add(roomLab);

        floor.createMaintenanceSchedule(roomOrder);
        schedule = floor.getMaintenanceSchedule();
    }

    @Test
    public void getMaintenanceTimeStudy() {
        Assert.assertEquals(6,
                            schedule.getMaintenanceTime(roomStudy));
    }

    @Test
    public void getMaintenanceTimeOffice() {
        Assert.assertEquals(8,
                            schedule.getMaintenanceTime(roomOffice));
    }

    @Test
    public void getMaintenanceTimeLab() {
        Assert.assertEquals(12,
                            schedule.getMaintenanceTime(roomLab));
    }

    @Test
    public void getCurrentRoom() {
        Assert.assertEquals(roomStudy, schedule.getCurrentRoom());
    }

    @Test
    public void getTimeElapsedCurrentRoom() {
        Assert.assertEquals(0, schedule.getTimeElapsedCurrentRoom());
    }

    @Test
    public void getTimeElapsedCurrentRoomAfterElapsed() {
        schedule.elapseOneMinute();
        Assert.assertEquals(1, schedule.getTimeElapsedCurrentRoom());
    }

    @Test
    public void elapseOneMinuteSameRoom() {
        schedule.elapseOneMinute();
        Assert.assertEquals(roomStudy, schedule.getCurrentRoom());
    }

    @Test
    public void elapseOneMinuteNextRoom() {
        for (int i = 0; i < schedule.getMaintenanceTime(roomStudy); i++) {
            schedule.elapseOneMinute();
        }
        Assert.assertEquals(roomOffice, schedule.getCurrentRoom());
    }

    @Test
    public void elapseOneMinuteAfterLastRoom() {
        for (int i = 0; i < (schedule.getMaintenanceTime(roomStudy)
                + schedule.getMaintenanceTime(roomOffice)
                + schedule.getMaintenanceTime(roomLab)); i++) {
            schedule.elapseOneMinute();
        }
        Assert.assertEquals(roomStudy, schedule.getCurrentRoom());
    }

    @Test
    public void elapseOneMinuteEvacuate() {
        final int currentTime = schedule.getTimeElapsedCurrentRoom();
        roomStudy.setFireDrill(true);
        schedule.elapseOneMinute();
        Assert.assertEquals(currentTime, schedule.getTimeElapsedCurrentRoom());
    }

    @Test
    public void skipCurrentMaintenanceNextRoom() {
        schedule.skipCurrentMaintenance();
        Assert.assertEquals(roomOffice, schedule.getCurrentRoom());
    }

    @Test
    public void skipCurrentMaintenanceAfterLastRoom() {
        for (int i = 0; i < roomOrder.size(); i++) {
            schedule.skipCurrentMaintenance();
        }
        Assert.assertEquals(roomStudy, schedule.getCurrentRoom());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("MaintenanceSchedule: currentRoom=101, " +
                                    "currentElapsed=0", schedule.toString());
    }

    @Test
    public void testToStringElapsedTime() {
        schedule.elapseOneMinute();
        Assert.assertEquals("MaintenanceSchedule: currentRoom=101, " +
                                    "currentElapsed=1", schedule.toString());
    }

    @Test
    public void encode() {
        Assert.assertEquals("101,102,103", schedule.encode());
    }

}
