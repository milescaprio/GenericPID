package GenericPID.Implementations;


import GenericPID.Extensible.ControlStrategy;
import GenericPID.MotorControlProfile.*;

/**
 * Exert full force towards the setpoint
 */
public class FullForce implements ControlStrategy {
    private double F;
    public FullForce(double maxForce) {
        F = maxForce;
    }
    public double calculate(double effectv, ControlLevel given, ControlLevel needed, double currv) throws UnknownControlStrategyException{
        if (given.ordinal() + 1 != needed.ordinal()) {
            throw new UnknownControlStrategyException("Conversion not compatible with this control strategy");
        }
        if (currv < effectv) {
            return F;
        } else if (currv > effectv) {
            return -F;
        } else {
            return 0;
        }
    }
}
