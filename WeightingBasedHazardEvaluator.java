package bms.hazardevaluation;

import bms.sensors.HazardSensor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the hazard level of a location using weightings for the
 * sensor values.
 */
public class WeightingBasedHazardEvaluator implements HazardEvaluator {
    /**
     * The map of sensors to their respective weighting in the evaluator
     */
    private Map<HazardSensor, Integer> sensors;

    /**
     * Creates a new weighting-based hazard evaluator with the given sensors
     * and weightings.
     *
     * @param sensors mapping of sensors to their respective weighting
     * @throws IllegalArgumentException if any weighting is below 0 or above
     * 100; or if the sum of all weightings is not equal to 100
     */
    public WeightingBasedHazardEvaluator(Map<HazardSensor, Integer> sensors)
            throws IllegalArgumentException {

        this.sensors = sensors;
        int weightingTotal = 0;
        for (Integer weighting : sensors.values()) {
            if (weighting < 0 || weighting > 100) {
                throw new IllegalArgumentException();
            }
            weightingTotal += weighting;
        }
        if (weightingTotal != 100) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int evaluateHazardLevel() {
        float total = 0;
        float div = 0;
        for (Map.Entry<HazardSensor,Integer> entry : this.sensors.entrySet()) {
            total += (entry.getKey().getHazardLevel() * entry.getValue());
            div += entry.getValue();
        }
        return Math.round(total / div);
    }

    /**
     * Returns a list containing the weightings associated with all of the
     * sensors monitored by this hazard evaluator.
     *
     * @return weightings
     */
    public List<Integer> getWeightings() {
        return Collections.list(Collections.enumeration(
                this.sensors.values()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "WeightingBased";
    }
}
