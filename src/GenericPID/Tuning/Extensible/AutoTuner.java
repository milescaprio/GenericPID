package GenericPID.Tuning.Extensible;

// import PIDConfig;

import GenericPID.PIDConfig;

public interface AutoTuner {
    public static enum Behavior {
        STABLE,
        REPEATING,
        DIVERGENT,
        UNKNOWN,
    }
    
    public PIDConfig next(PIDConfig last, double successHeuristic, boolean isStable);
    public double successHeuristic();
    public Behavior checkStability();
}