package GenericPID.Implementations;


import GenericPID.Extensible.DoubleFunction;

public class Zero implements DoubleFunction {
    public double eval(double x) {
        return 0;
    }
}