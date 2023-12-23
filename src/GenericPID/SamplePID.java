package GenericPID;

import java.awt.Color;

import GenericPID.Implementations.FullForce;
import GenericPID.Implementations.LinearSegment;
import GenericPID.Testing.ArtificialMotor;
import GenericPID.Testing.Graph;
import GenericPID.Testing.Graph.*;

public class SamplePID {
    ArtificialMotor m;
    PIDMotorFollow pmf;
    Path path;
    Graph graph;
    private double t;
    private double dt = 0.005;
    public void init() {
        //Simulation motor force
        double force = 50;
        //Simulation motor
        this.m = new ArtificialMotor(5, 1, 1, force);
        //Random pid config for example
        var conf = new PIDConfig(Math.random(), Math.random(), Math.random());
        //Pid follow path
        this.path = new Path();
        //Segment inserts; quite convenient! automatic. x1 y1 x2 y2
        // path.insertSegment(new LinearSegment(0, 0, 5, 0));
        // path.insertSegment(new LinearSegment(5, 0, 10, 5));
        // path.insertSegment(new LinearSegment(10, 5, 20, 5));
        // path.insertSegment(new LinearSegment(20, 5, 25, 0));
        // path.insertSegment(new LinearSegment(25, 0, 30, 0));
        path.insertSegment(new LinearSegment(0, 0, 10, 0));
        path.insertSegment(new LinearSegment(10, 10, 30, 10));
        //Motor control profile, how to convert control effect to motor effect
        var controlProfile = new MotorControlProfile(MotorControlProfile.ControlLevel.VELOCITY, MotorControlProfile.ControlLevel.ACCELERATION, new FullForce(50));
        //Highest-level controller; pid following a path with a motor control profile and granular dt
        this.pmf = new PIDMotorFollow(path, conf, controlProfile, dt);
        //A graph to see results
        GraphConfig gc = new GraphConfig(0, 30, -10, 30, 5, 5, 5);
        this.graph = new Graph(gc);
        //Realtime simulation variable
        t = 0;
        //Make two plots
        graph.addPlot(Color.RED);
        graph.addPlot(Color.BLUE);
    }
    public boolean loop() {
        //Change realtime by a random small amount
        double rtdt = Math.random() / 100;
        //Exert force in the correct direction using the paramters current position, current velocity, and current time (simulation needs realtime dt to simulate)
        m.exert(pmf.motorOutputNow(m.p(), m.v(), t), rtdt);
        //Show this on graph, and target on graph
        graph.addPoint(t, m.p(), false, 0);
        graph.addPoint(t, path.y(t), false, 1);
        //Increment realtime
        t += rtdt;
        //Quit after 30 seconds
        if (t < 30) {
            return true;
        }
        //Then show graph and wait
        graph.init(960, 960, "Position vs Time");
        try {
            Thread.sleep(5000, 0);
        } catch (Exception e) {
            ;
        }
        return false;
    }
    public static void main(String[] args) {
        while(true) {
            SamplePID A = new SamplePID();
            A.init();
            while(A.loop());
        }
    }
}
