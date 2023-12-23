package GenericPID.Implementations;


import GenericPID.Extensible.ControlStrategy;
import GenericPID.MotorControlProfile.*;

public class NoAdapter implements ControlStrategy {
    public double calculate(double effectv, ControlLevel given, ControlLevel needed, double currv) throws UnknownControlStrategyException {
        throw new UnknownControlStrategyException("No adapter defined");
    }
}
