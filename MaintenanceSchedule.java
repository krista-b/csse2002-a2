package bms.floor;

import bms.room.Room;
import bms.room.RoomState;
import bms.room.RoomType;
import bms.util.TimedItem;
import bms.util.Encodable;
import bms.util.TimedItemManager;

import java.util.List;
import java.util.StringJoiner;

/**
 * Carries out maintenance on a list of rooms in a given floor.
 */
public class MaintenanceSchedule implements TimedItem, Encodable {
    /**
     * List of rooms on which to perform maintenance, in order
     */
    private List<Room> roomOrder;

    /**
     * Room currently in the process of being maintained.
     */
    private Room currentRoom;

    /**
     * Index of room currently in the process of being maintained.
     */
    private int currentRoomIndex;

    /**
     * Time elapsed maintaining current room
     */
    private int timeElapsed;

    /**
     * Creates a new maintenance schedule for a floor's list of rooms.
     * The new maintenance schedule should be registered as a timed item with
     * the timed item manager.
     *
     * @param roomOrder list of rooms on which to perform maintenance, in order
     * @require roomOrder != null && roomOrder.size() > 0
     */
    public MaintenanceSchedule(List<Room> roomOrder) {
        this.roomOrder = roomOrder;
        TimedItemManager.getInstance().registerTimedItem(this);
        currentRoomIndex = 0;
        currentRoom = roomOrder.get(currentRoomIndex);
        this.currentRoom.setMaintenance(true);
        this.timeElapsed = 0;
    }

    /**
     * Returns the time taken to perform maintenance on the given room,
     * in minutes.
     *
     * @param room room on which to perform maintenance
     * @return room's maintenance time in minutes
     */
    public int getMaintenanceTime(Room room) {
        double baseTime = 5;
        if (room.getArea() > Room.getMinArea()) {
            baseTime = baseTime + (0.2 * (room.getArea() - Room.getMinArea()));
        }
        if (room.getType() == RoomType.STUDY) {
            return (int) Math.round(baseTime);
        } else if (room.getType() == RoomType.OFFICE) {
            return (int) Math.round(baseTime * 1.5);
        } else {
            return (int) Math.round(baseTime * 2);
        }
    }

    /**
     * Returns the room which is currently in the process of being maintained.
     *
     * @return room currently in maintenance
     */
    public Room getCurrentRoom(){
        return this.currentRoom;
    }

    /**
     * Returns the number of minutes that have elapsed while maintaining
     * the current room
     *
     * @return time elapsed maintaining current room
     */
    public int getTimeElapsedCurrentRoom() {
        return this.timeElapsed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void elapseOneMinute() {
        if (this.currentRoom.evaluateRoomState() != RoomState.EVACUATE) {
            this.timeElapsed++;
            if (this.getTimeElapsedCurrentRoom()
                    == this.getMaintenanceTime(currentRoom)) {
                this.currentRoom.setMaintenance(false);
                this.currentRoomIndex++;
                if (this.currentRoomIndex >= this.roomOrder.size()) {
                    this.currentRoomIndex = 0;
                }
                this.currentRoom = roomOrder.get(currentRoomIndex);
                this.currentRoom.setMaintenance(true);
                this.timeElapsed = 0;
            }
        }
    }

    /**
     * Stops the in-progress maintenance of the current room and
     * progresses to the next room.
     */
    public void skipCurrentMaintenance() {
        this.currentRoom.setMaintenance(false);
        this.currentRoomIndex++;
        if (this.currentRoomIndex >= this.roomOrder.size()) {
            this.currentRoomIndex = 0;
        }
        this.currentRoom = roomOrder.get(currentRoomIndex);
        this.currentRoom.setMaintenance(true);
        this.timeElapsed = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("MaintenanceSchedule: currentRoom=%d, " +
                                     "currentElapsed=%d",
                             getCurrentRoom().getRoomNumber(),
                             getTimeElapsedCurrentRoom());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode() {
        StringJoiner joiner = new StringJoiner(",");
        for (Room room : this.roomOrder) {
            joiner.add(String.format("%d", room.getRoomNumber()));
        }
        return joiner.toString();
    }
}
