package GenericPID.Implementations;

import java.util.ArrayList;

import GenericPID.Extensible.ControlStrategy;
import GenericPID.MotorControlProfile.*;

/**
 *Exert full force towards the setpoint, with estimated mass and dt known
 *so it won't overshoot in the time slice
 * Estimates because of drag and other factors, but almost always better than not
 */
public class DtFullForce implements ControlStrategy {
    private double F;
    private double dt;
    private double m;
    public DtFullForce(double maxForce, double dt, double m) {
        F = maxForce;
        this.dt = dt;
        this.m = m;
    }
    public double calculate(double effectv, ControlLevel given, ControlLevel needed, double currv) throws UnknownControlStrategyException {
        if (given.ordinal() + 1 != needed.ordinal()) {
            throw new UnknownControlStrategyException("Conversion not compatible with this control strategy");
        }
        double betterF = F;
        if (Math.abs(currv - effectv) < dt * F / m) { //if the error is less than the projected Dv
            betterF = Math.abs(currv - effectv) / dt * m; //Reverse the dv/dt calculation to derive the required force
        }
        if (currv < effectv) {
            return betterF;
        } else if (currv > effectv) {
            return -betterF;
        } else {
            return 0;
        }
    }
}
