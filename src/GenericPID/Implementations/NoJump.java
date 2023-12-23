package GenericPID.Implementations;


import GenericPID.Extensible.BooleanFunction;

public class NoJump implements BooleanFunction {
    public boolean eval(double x) {
        return false;
    }
}
