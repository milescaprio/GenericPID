package GenericPID.Tuning;

import GenericPID.Extensible.DoubleFunction;
import GenericPID.MotorControlProfile;
import GenericPID.PIDConfig;
import GenericPID.Testing.Graph;
import GenericPID.Testing.Graph.*;

import java.util.ArrayList;


public class Tuner {
    private static final double Graph = 0;
    private ArrayList<PIDConfig> trials;

    public Tuner(PIDConfig initial, DoubleFunction runMotorControl, MotorControlProfile control, ArrayList<Double> targets) {
        trials.add(initial);
        GraphConfig gc = new GraphConfig();
        Graph G = new Graph(gc);
    }
    public void beginCLI() {

    }

}
