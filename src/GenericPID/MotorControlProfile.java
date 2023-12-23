package GenericPID;


import GenericPID.Extensible.ControlStrategy;
import GenericPID.Implementations.NoAdapter;

/**PID doesn't always yield direct results to a motor. This class holds functions you can write so your
  *motor control can skip right to the important part. If the motor has built-in second PID to get to a certain
   *velocity, great! use that and the adapter does nothing. If the motor needs a controlled voltage, problem! 
   *let's convert it here.
   */
public class MotorControlProfile {
    private ControlLevel given = ControlLevel.VELOCITY; //final
    private ControlStrategy converter; //should be final
    private ControlLevel motorEffect = ControlLevel.ACCELERATION;
    public static MotorControlProfile none() {
        return new MotorControlProfile(ControlLevel.VELOCITY, ControlLevel.VELOCITY, new NoAdapter());
    }
    public MotorControlProfile(ControlLevel controlEffect, ControlLevel motorEffect, ControlStrategy adapter) {
        this.given = controlEffect;
        this.motorEffect = motorEffect;
        converter = adapter;
    }
    public static class ControlLevelException extends RuntimeException { 
        public ControlLevelException(String errorMessage) {
            super(errorMessage);
        }
    }
    public static class UnknownControlStrategyException extends RuntimeException { 
        public UnknownControlStrategyException(String errorMessage) {
            super(errorMessage);
        }
    }
    private double calculate(double effect, ControlLevel needed, double curreffect) throws UnknownControlStrategyException {
        if (this.given == needed) {
            return effect;
        }
        return converter.calculate(effect, this.given, needed, curreffect);
    }
    public double mEffect(double effect, double curreffect) throws UnknownControlStrategyException {
        return calculate(effect, this.motorEffect, curreffect);
    }
    /**
     * An enum that represents which derivative level of control is being represented. 
     * Does not necessarily signify units used or calculation (Force would be tied to number 2, accel, because it's just the differentiation level)
     */
    public static enum ControlLevel {
        ABSERK,
        ABSELERATION,
        ABSITY,
        ABSEMENT,
        POSITION,
        VELOCITY,
        ACCELERATION,
        JERK,
        SNAP,
    }
}
