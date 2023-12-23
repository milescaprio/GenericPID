package GenericPID.Extensible;


import GenericPID.MotorControlProfile;

/**
 * <p> Make your own method for how one control effect level is turned into another. <p>
 * Does not have to be in SI units or the same calculation (eg, FullForce returns a force but is at the level of Acceleration.)
 */
public interface ControlStrategy {
    /**
      *Given the control effect and context info and a conversion requirement, determine how to output to the motor.
      */
    public double calculate(double effect, MotorControlProfile.ControlLevel given, MotorControlProfile.ControlLevel needed, double curreffect) throws MotorControlProfile.UnknownControlStrategyException;
}
