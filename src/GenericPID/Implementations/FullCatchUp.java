package GenericPID.Implementations;


import GenericPID.Extensible.CatchUpFunction;

/**
 * Catches up as fast as possible, instantly, sacrificing much accuracy if it's very far behind
 */
public class FullCatchUp implements CatchUpFunction {
    public double dt(double timeBehind, int timesBehind, double normalDt) {
        return timeBehind + normalDt * 1.5;
    }
}