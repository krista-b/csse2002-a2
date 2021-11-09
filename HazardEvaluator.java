package bms.hazardevaluation;

/**
 * A component which takes all available hazard sensor readings and returns
 * a single hazard level.
 */
public interface HazardEvaluator {

    /**
     * Calculates a hazard level between 0 and 100.
     *
     * @return the hazard level, between 0 and 100 (inclusive)
     */
    int evaluateHazardLevel();
}
