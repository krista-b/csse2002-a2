package bms.hazardevaluation;

import bms.sensors.HazardSensor;
import bms.sensors.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates the hazard level of a location using a rule based system.
 */
public class RuleBasedHazardEvaluator implements HazardEvaluator {
    /**
     * The list of sensors to be used in the hazard level calculation.
     */
    private List<HazardSensor> sensors;

    /**
     * Creates a new rule-based hazard evaluator with the given list of
     * sensors.
     *
     * @param sensors sensors to be used in the hazard level calculation
     */
    public RuleBasedHazardEvaluator(List<HazardSensor> sensors) {
        this.sensors = sensors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int evaluateHazardLevel() {
        int totalHazardLevel = 0;
        int occupancyLevel = 0;
        if (this.sensors.isEmpty()) {
            return 0;
        } else if (this.sensors.size() == 1) {
            return this.sensors.get(0).getHazardLevel();
        } else {
            for (HazardSensor sensor : sensors) {
                if (sensor.getClass().getSimpleName() != "OccupancySensor") {
                    if (sensor.getHazardLevel() == 100) {
                        return 100;
                    }
                    totalHazardLevel += 1;
                } else {
                    occupancyLevel = sensor.getHazardLevel();
                }
            }
            if (occupancyLevel != 0) {
                return (totalHazardLevel / sensors.size())
                        * (occupancyLevel / 100);
            } else {
                return (totalHazardLevel / sensors.size());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RuleBased";
    }
}
