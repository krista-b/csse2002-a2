package bms.util;

import bms.building.Building;
import bms.floor.Floor;
import bms.room.Room;
import bms.room.RoomState;
import bms.room.RoomType;
import bms.sensors.ComfortSensor;
import bms.sensors.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class that provides a recommendation for a study room in a building.
 */
public class StudyRoomRecommender {
    public StudyRoomRecommender() {
    }

    /**
     * Searches a list and return s a list of all items that meet the filter
     * criteria.
     *
     * @param items list of items to be searched
     * @param filter criteria used to select items to be returned
     *
     * @return list of all items that meet filter criteria
     */
    private static <T> List<T> filterList(List<T> items, Predicate<T> filter) {
        List<T> filteredItems = new ArrayList<>();
        for (T item : items) {
            if (filter.test(item)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    /**
     * Calculate the given rooms average comfort level using the average of
     * the comfort levels of each room's available comfort sensors.
     *
     * @param room the room which comfort level is to be calculated
     * @return the average comfort level
     */
    private static int calculateComfortLevel(Room room) {
        int total = 0;
        int count = 0;
        if (room == null) {
            return 0;
        }
        for (Sensor sensor : room.getSensors()) {
            if (sensor instanceof ComfortSensor) {
                total += ((ComfortSensor) sensor).getComfortLevel();
                count++;
            }
        }
        if (count == 0) {
            return 0;
        } else {
            return total / count;
        }
    }

    /**
     * Finds the most appropriate room within a floor
     *
     * @param floor floor to be searched
     * @return most appropriate room
     */
    private static Room bestRoomInFloor(Floor floor) {
        Room bestRoom = null;
        List<Room> rooms = floor.getRooms();
        rooms = filterList(rooms, room -> room.evaluateRoomState()
                == RoomState.OPEN);
        rooms = filterList(rooms, room -> room.getType() == RoomType.STUDY);
        if (!rooms.isEmpty()) {
            // there are remaining rooms
            bestRoom = rooms.get(0);
            for (Room room : rooms) {
                if (calculateComfortLevel(room)
                        > calculateComfortLevel(bestRoom)) {
                    bestRoom = room;
                }
            }
        }
        return bestRoom;
    }

    /**
     * Returns a room in the given building that is most suitable for
     * study purposes.
     *
     * @param building building in which to search for a study room
     * @return the most suitable study room in the building;
     * null if there are none
     */
    public static Room recommendStudyRoom(Building building) {
        Room bestRoom = null;
        if (building.getFloors() == null) {
            return null;
        }
        List<Floor> floors = filterList(building.getFloors(),
                                        floor -> floor.getRooms() != null);
        if (floors.isEmpty()) {
            return null;
        }
        for (Floor floor : floors) {
            if (calculateComfortLevel(bestRoomInFloor(floor))
                    > calculateComfortLevel(bestRoom)) {
                bestRoom = bestRoomInFloor(floor);
            }
        }
        return bestRoom;
    }
}

