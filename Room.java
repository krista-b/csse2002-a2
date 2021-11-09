package bms.room;

import bms.exceptions.DuplicateSensorException;
import bms.hazardevaluation.HazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.sensors.*;
import bms.util.Encodable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a room on a floor of a building.
 * <p>
 * Each room has a room number (unique for this floor, ie. no two rooms on the
 * same floor can have the same room number), a type to indicate its intended
 * purpose, and a total area occupied by the room in square metres.
 * <p>
 * Rooms also need to record whether a fire drill is currently taking place in
 * the room.
 * <p>
 * Rooms can have one or more sensors to monitor hazard levels
 * in the room.
 * @ass1
 */
public class Room implements Encodable {

    /**
     * Unique room number for this floor.
     */
    private int roomNumber;

    /**
     * The type of room. Different types of rooms can be used for different
     * activities.
     */
    private RoomType type;

    /**
     * List of sensors located in the room. Rooms may only have up to one of
     * each type of sensor. Alphabetically sorted by class name.
     */
    private List<Sensor> sensors;

    /**
     * This rooms hazard evaluator.
     */
    private HazardEvaluator evaluator;

    /**
     * Area of the room in square metres.
     */
    private double area;

    /**
     * Minimum area of all rooms, in square metres.
     * (Note that dimensions of the room are irrelevant).
     * Defaults to 5.
     */
    private static final int MIN_AREA = 5;

    /**
     * Records whether there is currently a fire drill.
     */
    private boolean fireDrill;

    /**
     * Records whether there is currently maintenance ongoing.
     */
    private boolean maintenance;

    /**
     * Creates a new room with the given room number.
     *
     * @param roomNumber the unique room number of the room on this floor
     * @param type the type of room
     * @param area the area of the room in square metres
     * @ass1
     */
    public Room(int roomNumber, RoomType type, double area) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.area = area;

        this.sensors = new ArrayList<>();
        this.fireDrill = false;
        this.maintenance = false;
        this.evaluator = null;
    }

    /**
     * Returns room number of the room.
     *
     * @return the room number on the floor
     * @ass1
     */
    public int getRoomNumber() {
        return this.roomNumber;
    }

    /**
     * Returns area of the room.
     *
     * @return the room area in square metres
     * @ass1
     */
    public double getArea() {
        return this.area;
    }

    /**
     * Returns the minimum area for all rooms.
     * <p>
     * Rooms must be at least 5 square metres in area.
     *
     * @return the minimum room area in square metres
     * @ass1
     */
    public static int getMinArea() {
        return MIN_AREA;
    }

    /**
     * Returns the type of the room.
     *
     * @return the room type
     * @ass1
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Returns whether there is currently a fire drill in progress.
     *
     * @return current status of fire drill
     * @ass1
     */
    public boolean fireDrillOngoing() {
        return this.fireDrill;
    }

    /**
     * Returns whether there is currently maintenance in progress.
     *
     * @return current status of maintenance
     */
    public boolean maintenanceOngoing() {
        return this.maintenance;
    }

    /**
     * Returns the list of sensors in the room.
     * <p>
     * The list of sensors stored by the room should always be in alphabetical
     * order, by the sensor's class name.
     * <p>
     * Adding or removing sensors from this list should not affect the room's
     * internal list of sensors.
     *
     * @return list of all sensors in alphabetical order of class name
     * @ass1
     */
    public List<Sensor> getSensors() {
        return new ArrayList<>(this.sensors);
    }

    /**
     * Change the status of the fire drill to the given value.
     *
     * @param fireDrill whether there is a fire drill ongoing
     * @ass1
     */
    public void setFireDrill(boolean fireDrill) {
        this.fireDrill = fireDrill;
    }

    /**
     * Change the status of maintenance to the given value.
     *
     * @param maintenance whether there is maintenance ongoing
     */
    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    /**
     * Returns this room's hazard evaluator, or null if none exists.
     *
     * @return room's hazard evaluator
     */
    public HazardEvaluator getHazardEvaluator() {
        return this.evaluator;
    }

    /**
     * Sets the room's hazard evaluator to a new hazard evaluator.
     *
     * @param hazardEvaluator new hazard evaluator for the room to use
     */
    public void setHazardEvaluator(HazardEvaluator hazardEvaluator) {
        this.evaluator = hazardEvaluator;
    }

    /**
     * Return the given type of sensor if there is one in the list of sensors;
     * return null otherwise.
     *
     * @param sensorType the type of sensor which matches the class name
     *                   returned by the getSimpleName() method,
     *                   e.g. "NoiseSensor" (no quotes)
     * @return the sensor in this room of the given type; null if none found
     * @ass1
     */
    public Sensor getSensor(String sensorType) {
        for (Sensor s : this.getSensors()) {
            if (s.getClass().getSimpleName().equals(sensorType)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Adds a sensor to the room if a sensor of the same type is not
     * already in the room.
     * <p>
     * The list of sensors should be sorted after adding the new sensor, in
     * alphabetical order by simple class name ({@link Class#getSimpleName()}).
     *
     * @param sensor the sensor to add to the room
     * @throws DuplicateSensorException if the sensor to add is of the
     * same type as a sensor already in this room
     * @ass1
     */
    public void addSensor(Sensor sensor)
            throws DuplicateSensorException {
        for (Sensor s : sensors) {
            if (s.getClass().equals(sensor.getClass())) {
                throw new DuplicateSensorException(
                        "Duplicate sensor of type: "
                                + s.getClass().getSimpleName());
            }
        }
        sensors.add(sensor);
        sensors.sort(Comparator.comparing(s -> s.getClass().getSimpleName()));
        this.setHazardEvaluator(null);
    }

    /**
     * Evaluates the room status based upon current information.
     *
     * @return current room status
     */
    public RoomState evaluateRoomState() {
        for (Sensor sensor : this.sensors) {
            if (sensor instanceof TemperatureSensor) {
                if (((TemperatureSensor) sensor).getHazardLevel() == 100) {
                    return RoomState.EVACUATE;
                }
            }
        }
        if (this.fireDrillOngoing()) {
            return RoomState.EVACUATE;
        } else if (this.maintenanceOngoing() && !this.fireDrillOngoing()) {
            return RoomState.MAINTENANCE;
        } else {
            return RoomState.OPEN;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Room)) {
            return false;
        }
        Room room = (Room) obj;
        return this.roomNumber == room.getRoomNumber()
                && this.type == room.getType()
                && Math.abs(this.area - room.getArea()) <= 0.001
                && new HashSet<>(this.sensors)
                .equals(new HashSet<>(room.sensors));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashcode = 0;
        for (Sensor sensor : this.sensors) {
            hashcode += sensor.hashCode();
        }
        return this.roomNumber * this.type.hashCode () * (int) this.area * this.sensors.size()
                * hashcode;
    }

    /**
     * Returns the human-readable string representation of this room.
     * <p>
     * The format of the string to return is
     * "Room #'roomNumber': type='roomType', area='roomArea'm^2,
     * sensors='numSensors'"
     * without the single quotes, where 'roomNumber' is the room's unique
     * number, 'roomType' is the room's type, 'area' is the room's type,
     * 'numSensors' is the number of sensors in the room.
     * <p>
     * The room's area should be formatted to two (2) decimal places.
     * <p>
     * For example:
     * "Room #42: type=STUDY, area=22.50m^2, sensors=2"
     *
     * @return string representation of this room
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("Room #%d: type=%s, area=%.2fm^2, sensors=%d",
                this.roomNumber,
                this.type,
                this.area,
                this.sensors.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode() {
        StringJoiner joinerFirst = new StringJoiner(":");
        joinerFirst.add(String.format("%d:%s:%.2f:%d", this.roomNumber,
                                  this.type, this.area,
                                      this.getSensors().size()));
        if (this.getHazardEvaluator() != null) {
            joinerFirst.add(this.evaluator.toString());
        }
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add(joinerFirst.toString());
        for (int i = 0; i < getSensors().size(); i++) {
            TimedSensor timedSensor = (TimedSensor) getSensors().get(i);
            if (this.evaluator == null
                    || this.evaluator.getClass().getSimpleName()
                    .equals("RuleBasedHazardEvaluator")) {
                joiner.add(timedSensor.encode());
            } else {
                WeightingBasedHazardEvaluator weightingBased =
                        (WeightingBasedHazardEvaluator) this.evaluator;
                joiner.add(timedSensor.encode() + "@" +
                                   weightingBased.getWeightings().get(i));
            }
        }
        return joiner.toString();
    }
}
