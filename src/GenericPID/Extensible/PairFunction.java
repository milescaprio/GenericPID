package GenericPID.Extensible;

// import Pair;

import GenericPID.Pair;

/**
 * Simple Pair supplier
 */
public interface PairFunction {
    public Pair eval(double T);
}