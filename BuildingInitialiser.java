package bms.building;

import bms.exceptions.DuplicateRoomException;
import bms.exceptions.DuplicateSensorException;
import bms.exceptions.FileFormatException;
import bms.exceptions.InsufficientSpaceException;
import bms.floor.Floor;
import bms.hazardevaluation.HazardEvaluator;
import bms.hazardevaluation.RuleBasedHazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.room.Room;
import bms.room.RoomType;
import bms.sensors.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class which manages the initialisation and saving of buildings by reading
 * and writing data to a file.
 */
public class BuildingInitialiser {

    /**
     * Reads the sensor line tokens and returns the sensor if appropriate type.
     *
     * @param sensorLine array of strings to be read
     * @return sensor that is read
     * @throws FileFormatException if the line is not an appropriate sensor
     * type
     */
    private static TimedSensor readSensorType(String[] sensorLine)
            throws FileFormatException {

        int updateFreq, capacity, idealVal, varLim;
        String[] readingsString = sensorLine[1].split(",");
        int[] readings = new int[readingsString.length];
        TimedSensor sensor;
        try {
            for(int i = 0; i < readings.length; i++) {
                readings[i] = Integer.parseInt(readingsString[i]);
            }

            switch (sensorLine[0]) {
                case "CarbonDioxideSensor":
                    updateFreq = Integer.parseInt(sensorLine[2]);
                    idealVal = Integer.parseInt(sensorLine[3]);
                    varLim = Integer.parseInt(sensorLine[4]);
                    sensor = new CarbonDioxideSensor(
                                    readings, updateFreq, idealVal, varLim);
                    break;
                case "NoiseSensor":
                    updateFreq = Integer.parseInt(sensorLine[2]);
                    sensor = new NoiseSensor(readings, updateFreq);
                    break;
                case "OccupancySensor":
                    updateFreq = Integer.parseInt(sensorLine[2]);
                    capacity = Integer.parseInt(sensorLine[3]);
                    sensor = new OccupancySensor(readings, updateFreq
                            , capacity);
                    break;
                case "TemperatureSensor":
                    sensor = new TemperatureSensor(readings);
                    break;
                default:
                    throw new FileFormatException();
            }
        } catch (Exception e) {
            throw new FileFormatException();
        }
        return sensor;
    }

    /**
     * Reads lines from the reader to get all sensors in the room and adds them
     * to the room.
     *
     * @param reader buffered reader to read from
     * @param room room that sensors are to be added to
     * @param numSensors number of sensors within the room
     * @throws FileFormatException if file is invalid
     */
    private static void readSensors(BufferedReader reader, Room room,
                                    int numSensors)
            throws FileFormatException {
        for (int i = 1; i <= numSensors; i++) {
            try {
                String line = reader.readLine();
                Sensor sensor;

                String[] sensorLine = line.split(":");

                sensor = readSensorType(sensorLine);
                room.addSensor(sensor);
            } catch (Exception e) {
                throw new FileFormatException();
            }
        }
    }

    /**
     * Reads lines from the reader to get all rule based sensors in the
     * room and adds them to the room.
     *
     * @param reader buffered reader to read from
     * @param room room that sensors are to be added to
     * @param numSensors number of sensors within the room
     * @return list of rule based sensors
     * @throws FileFormatException if file is invalid
     */
    private static List<HazardSensor> readSensorsRule(BufferedReader reader,
    Room room, int numSensors) throws FileFormatException {

        List<HazardSensor> sensors = new ArrayList<>();

        for (int i = 1; i <= numSensors; i++) {
            try {
                String line = reader.readLine();
                HazardSensor sensor;

                String[] sensorLine = line.split(":");

                sensor = (HazardSensor) readSensorType(sensorLine);
                room.addSensor(sensor);
                sensors.add(sensor);
            } catch (Exception e) {
                throw new FileFormatException();
            }
        }
        return sensors;
    }

    /**
     * Reads lines from the reader to get all weighting based sensors in the
     * room and adds them to the room.
     *
     * @param reader buffered reader to read from
     * @param room room that sensors are to be added to
     * @param numSensors number of sensors within the room
     * @return mapping of the weighting based sensor to its weighting
     * @throws FileFormatException if file is invalid
     */
    private static Map<HazardSensor, Integer> readSensorsWeighting(
            BufferedReader reader, Room room, int numSensors)
            throws FileFormatException {
        final Map<HazardSensor, Integer> map = new HashMap<>();
        Integer weighting;
        List<HazardSensor> sensors = new ArrayList<>();
        List<Integer> weightings = new ArrayList<>();

        for (int i = 1; i <= numSensors; i++) {
            try {
                String line = reader.readLine();
                HazardSensor sensor;

                weighting =
                        Integer.parseInt(line.substring(
                                line.indexOf('@') + 1));
                line = line.substring(0, line.indexOf('@'));

                String[] sensorLine = line.split(":");
                sensor = (HazardSensor) readSensorType(sensorLine);
                room.addSensor(sensor);
                sensors.add(sensor);
                weightings.add(weighting);
            } catch (Exception e) {
                throw new FileFormatException();
            }
        }
        for (int i = 0; i < sensors.size(); i++) {
            map.put(sensors.get(i), weightings.get(i));
        }
        return map;
    }

    /**
     * Reads room lines from the buffered reader and returns the
     * corresponding room.
     *
     * @param reader buffered reader to be read from
     * @return room that is read
     * @throws FileFormatException if the file is invalid
     */
    private static Room readRoom(BufferedReader reader)
            throws FileFormatException {
        Room room;
        int roomNum, numSensors;
        RoomType roomType;
        double area;
        try {
            String line = reader.readLine();
            String[] roomLine = line.split(":");

            if (roomLine.length < 4 || roomLine.length > 5) {
                throw new FileFormatException();
            }
            roomNum = Integer.parseInt(roomLine[0]);
            area = Double.parseDouble(roomLine[2]);
            numSensors = Integer.parseInt(roomLine[3]);
            roomType = RoomType.valueOf(roomLine[1]);
            room = new Room(roomNum, roomType, area);
            if (numSensors != 0) {
                if (roomLine.length == 5) {
                    if (roomLine[4].equals("RuleBased")) {
                        room.setHazardEvaluator(new RuleBasedHazardEvaluator(
                                readSensorsRule(reader, room, numSensors)));
                    } else if (roomLine[4].equals("WeightingBased")) {
                        room.setHazardEvaluator(
                                new WeightingBasedHazardEvaluator(
                                        readSensorsWeighting(
                                                reader, room, numSensors)));
                    } else {
                        throw new FileFormatException();
                    }
                } else {
                    readSensors(reader, room, numSensors);
                }
            }
        } catch (Exception e) {
            throw new FileFormatException();
        }
        return room;
    }

    /**
     * Reads floor lines from the buffered reader and returns the
     * corresponding floor.
     *
     * @param reader buffered reader to be read from
     * @return floor that is read
     * @throws FileFormatException if the file is invalid
     */
    private static Floor readFloor(BufferedReader reader)
            throws FileFormatException {
        Floor floor;
        int floorNum, numRooms;
        double width, length;
        try {
            String line = reader.readLine();
            String[] floorLine = line.split(":");

            if (floorLine.length < 4 || floorLine.length > 5) {
                throw new FileFormatException();
            }
            floorNum = Integer.parseInt(floorLine[0]);
            width = Double.parseDouble(floorLine[1]);
            length = Double.parseDouble(floorLine[2]);
            numRooms = Integer.parseInt(floorLine[3]);

            floor = new Floor(floorNum, width, length);
            for (int i = 0; i < numRooms; i++) {
                floor.addRoom(readRoom(reader));
            }
            if (floorLine.length == 5) {
                String[] roomsString = floorLine[4].split(",");
                List<Room> rooms = new ArrayList<>();
                for (String room : roomsString) {
                    rooms.add(floor.getRoomByNumber(Integer.parseInt(room)));
                }
                floor.createMaintenanceSchedule(rooms);
            }
        } catch (Exception e) {
            throw new FileFormatException();
        }
        return floor;
    }

    /**
     * Loads a list of buildings from a save file with the given filename.
     * Save files have the following structure. Square brackets indicate
     * that the data inside them is optional. See the demo save file for an
     * example (uqstlucia.txt).
     *
     * @param filename path of the file from which to load a list of buildings
     * @return a list containing all the buildings loaded from the file
     * @throws IOException if an IOException is encountered when calling any
     * IO methods
     * @throws FileFormatException if the file format of the given file is
     * invalid according to the rules above
     */
    public static List<Building> loadBuildings(String filename)
            throws IOException, FileFormatException {
        BufferedReader reader = new BufferedReader(new FileReader(
                "saves/" + filename));
        List<Building> buildings = new ArrayList<>();
        String buildingName;
        int numFloors;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                buildingName = line;
                Building building = new Building(buildingName);

                try {
                    line = reader.readLine();
                    numFloors = Integer.parseInt(line);
                } catch (NumberFormatException numberFormatException) {
                    throw new FileFormatException();
                }
                for (int i = 1; i <= numFloors; i++) {
                    try {
                        building.addFloor(readFloor(reader));
                    } catch (Exception e) {
                        throw new FileFormatException();
                    }
                }
                buildings.add(building);
            }
        } catch (IOException ioException) {
            throw new IOException();
        }
        return buildings;
    }
}
