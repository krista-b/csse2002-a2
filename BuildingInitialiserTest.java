package bms.building;

import bms.exceptions.FileFormatException;
import bms.floor.Floor;
import bms.hazardevaluation.RuleBasedHazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.room.Room;
import bms.room.RoomType;
import bms.sensors.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingInitialiserTest {
    private List<Building> buildings;

    @Before
    public void setUp() {
        buildings = new ArrayList<>();
    }

    private List<Building> makeBuilding() {
        List<Building> workingBuildings = new ArrayList<>();
        Building building1 = new Building("Working Building");
        Floor floor1 = new Floor(1, 10, 10);
        Floor floor2 = new Floor(2, 10, 10);
        Floor floor3 = new Floor(3, 10, 8);

        Room room1 = new Room(101, RoomType.STUDY, 20);
        Room room2 = new Room(102, RoomType.STUDY, 15);
        Room room3 = new Room(201, RoomType.OFFICE, 50);
        Room room4 = new Room(301, RoomType.LABORATORY, 30);

        try {
            floor1.addRoom(room1);
            floor1.addRoom(room2);
            floor2.addRoom(room3);
            floor3.addRoom(room4);
        } catch (Exception ignored) {
        }

        OccupancySensor sensor1 = new OccupancySensor(new int[]{13, 24, 28, 15, 6}, 4
                , 30);
        NoiseSensor sensor2 = new NoiseSensor(new int[]{55, 62, 69, 63}, 3);
        TemperatureSensor sensor3 = new TemperatureSensor(new int[]{28, 29,
                26, 24});

        try {
            room1.addSensor(sensor1);
            room3.addSensor(sensor2);
            room3.addSensor(sensor3);
        } catch (Exception ignored) {
        }

        List<Room> schedule = new ArrayList<>();
        schedule.add(room1);
        schedule.add(room2);
        floor1.createMaintenanceSchedule(schedule);

        List<HazardSensor> sensors = new ArrayList<>();
        sensors.add(sensor2);
        sensors.add(sensor3);

        RuleBasedHazardEvaluator evaluator =
                new RuleBasedHazardEvaluator(sensors);
        room3.setHazardEvaluator(evaluator);

        Map<HazardSensor, Integer> map = new HashMap<>();
        map.put(sensors.get(0), 25);
        map.put(sensors.get(1), 75);
        WeightingBasedHazardEvaluator evaluator1 =
                new WeightingBasedHazardEvaluator(map);
        room4.setHazardEvaluator(evaluator1);
        try {
            building1.addFloor(floor1);
            building1.addFloor(floor2);
            building1.addFloor(floor3);
        } catch (Exception ignored) {
        }
        workingBuildings.add(building1);
        return workingBuildings;
    }

    @Test(expected = IOException.class)
    public void fileDoesNotExistTest() throws FileFormatException,
                                              IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/thisFileDoesNotExist.txt");
    }

    @Test(expected = FileFormatException.class)
    public void numFloorsTest() throws FileFormatException, IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/numFloorsTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void numRoomsTest() throws FileFormatException, IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/numRoomsTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void numSensorsTest() throws FileFormatException, IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/numSensorsTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void roomDoesNotExistTest() throws FileFormatException,
                                              IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/roomDoesNotExistTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void invalidMaintenanceTest() throws FileFormatException,
                                              IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/invalidMaintenanceTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void duplicateFloorsTest() throws FileFormatException,
                                                IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/duplicateFloorsTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void floorTooSmallTest() throws FileFormatException,
                                             IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/floorTooSmallTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void floorTooLargeTest() throws FileFormatException,
                                           IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/floorTooLargeTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void duplicateRoomsTest() throws FileFormatException,
                                             IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/duplicateRoomsTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void insufficientSpaceTest() throws FileFormatException,
                                            IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/insufficientSpaceTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void roomTypeTest() throws FileFormatException,
                                               IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/roomTypeTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void roomTooSmallTest() throws FileFormatException,
                                      IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/roomTooSmallTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void invalidEvaluatorTest() throws FileFormatException,
                                          IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/invalidEvaluatorTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void invalidWeightingTest() throws FileFormatException,
                                              IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/invalidWeightingTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void duplicateSensorTest() throws FileFormatException,
                                              IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/duplicateSensorTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void sensorTypeTest() throws FileFormatException,
                                             IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/sensorTypeTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void invalidFreqTest() throws FileFormatException,
                                        IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/invalidFreqTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void invalidCarbonDioxideTest() throws FileFormatException,
                                         IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/invalidCarbonDioxideTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void numColonsTest() throws FileFormatException,
                                                  IOException {
        buildings =  BuildingInitialiser.loadBuildings(
                "tests/numColonsTest.txt");
    }

    @Test(expected = FileFormatException.class)
    public void emptyLineTest() throws FileFormatException,
                                       IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/emptyLineTest.txt");
    }

    @Test
    public void loadBuildingsTest() throws FileFormatException,
                                       IOException {
        buildings = BuildingInitialiser.loadBuildings(
                "tests/workingTest.txt");
        Assert.assertEquals(makeBuilding(), buildings);
    }

    @After
    public void tearDown() {
        buildings = null;
    }
}
