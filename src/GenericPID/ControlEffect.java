package GenericPID;

import java.util.ArrayList;


/**A class that holds a control effect with the adapter*/
public class ControlEffect {

    private MotorControlProfile c;
    private double effect;

    public Double motorEffect(double currEffect) {
        try {
            return c.mEffect(effect, currEffect);
        }
        catch (MotorControlProfile.UnknownControlStrategyException e) {
            return null;
        }
    }
}