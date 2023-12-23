package GenericPID;



import GenericPID.Extensible.CatchUpFunction;
import GenericPID.Extensible.DoubleFunction;
import GenericPID.Implementations.FullCatchUp;
import GenericPID.Implementations.FullForce;
import GenericPID.Implementations.LinearSegment;
import GenericPID.Testing.ArtificialMotor;
import GenericPID.MotorControlProfile.*;
import GenericPID.Testing.Graph;

import java.awt.Color;
import java.util.Random;

/**An object which drives together a pid controller with a path and a controleffect profile for fully driving the motor.
 * <p> The path is not recommended to be a actual intricate path to follow, but more like things to do over time.
 * The motor does NOT use cross-track error, although that is planning to be added as an abstraction.
 * The motor evalues the error with the current position on the path, or one offsetted one ahead of time defined by "prediction" function abstraction parameter.
*/
public class PIDMotorFollow {
    //todo: implement cross-track error or a compensation method so the motor can reasonably follow a real path.
    //todo: split into motor on top of lower level version connects path and pid??
    //todo: maybe make this more similar to frc code and compiles down?
    boolean debug = Debug.debug_MotorFollow;
    boolean debug2 = Debug.debug_MotorFollow2;
    
    private Path path;
    private PID pid;
    private MotorControlProfile motor;

    public CatchUpFunction catchUpDt; //customize this if you want by just swapping its value with another CatchUpFunction
    private int timesBehind = 0;
    public double dt;
    
    private double lastEffect = 0;

    public PIDMotorFollow(Path path, PIDConfig config, MotorControlProfile motorprofile, double dt) {
        this.path = path;
        this.pid = new PID(config);
        this.catchUpDt = new FullCatchUp();
        this.dt = dt;
        this.motor = motorprofile;
    }

    /**
     * Outputs motor effect and consumes it (but if called again without need for poll, it will hit the cache)
     * <p>The PID's time tracker aims to be from 0 to dt above the current time.
     * <p>If it falls behind, it will add one dt.
     * <p>If it falls farther than one dt behind, it will use the CatchUpFunction implementation to decide how to catch up.
     */
    public double motorOutputNow(double x, double v, double t) {
        double ret = motor.mEffect(rawControlEffectNow(x, v, t), v);
        if (debug && ret > 200) {
            System.out.printf("bruh we be extremin, lasteff = %f, v = %f, ret = %f\n", lastEffect, v, ret);
        }
        return ret;
    }

    /**
     * Outputs raw control effect and consumes it (but if called again without need for poll, it will hit the cache)
     * motorOutputNow() reccomended instead, goes through motor control abstraction layer.
     * <p>The PID's time tracker aims to be from 0 to dt above the current time.
     * <p>If it falls behind, it will add one dt.
     * <p>If it falls farther than one dt behind, it will use the CatchUpFunction implementation to decide how to catch up.
     */
    public double rawControlEffectNow(double x, double v, double t) {
        //Decides whether or not to redetermine control effect
        //Converts to motor output using the controleffectprofile's adapater
        if(debug2) System.out.printf("Realtime: %f PID time: %f ", t, pid.t());
        if (t <= pid.t()) {
            if(debug2) System.out.println("PID time already covered!");
            timesBehind = 0;
            return lastEffect;
        } else {
            if (t > pid.t() + dt) {
                double timeBehind = t - pid.t() - dt;
                timesBehind++;
                double newdt = catchUpDt.dt(timeBehind, timesBehind, dt);
                if(debug2) System.out.printf("PID time behind, using CatchUpFunction to catchup! Time Behind: %f Times Behind: %d New dt: %f Rejected dt: %f\n", timeBehind, timesBehind, newdt, dt);
                // if (path.findJunction(t + newdt) > t) 
                //     pid.skipNextDTerm(t, t, newdt);
                double effect = nextControlEffect(x, newdt);
                lastEffect = effect;
                return lastEffect;
            } else { /*(t >= pid.t())*/
                if(debug2) System.out.printf("PID time on track!n", t, pid.t());
                timesBehind = 0;
                double effect = nextControlEffect(x, dt);
                lastEffect = effect;
                return lastEffect;
            }
        }
    }

    public double nextControlEffect(double x, double dt) {
        //it's okay for derivative to track change in path too because it just works; 
        //will start dampening when slope is greater than n instead of 0
        double pidt = pid.t();
        double target = path.y(pidt);
        if (path.findJunctionX(pid.t() + dt) > pidt) { //If the path round-down of t + dt is greater than t itself, we're crossing
            if (debug) System.out.println("Skipping D Term");
            pid.skipNextDTerm(target, x);
        }
        double ret = pid.controlEffect(target, x, dt);
        pid.nextT(dt);
        return ret;
    }





























    
    
    public static void main(String[] args) {
        test();
    }
    
    public static void test() {
        //a test and example for a pidmotorfollower

        //simulation for time variance in processing

        //make the path and put segments in it
        var p = new Path();
        p.insertSegment(new LinearSegment(0,0,10,0));
        p.insertSegment(new LinearSegment(10,5,30,5));
        double dt = 0.005;
        double realdt = 0.05;

        //simulation for the motor
        double maxf = 100;
        double mass = 1;
        var m = new ArtificialMotor(mass, 0.01, 0, maxf);
        var mc = new MotorControlProfile(ControlLevel.VELOCITY, ControlLevel.ACCELERATION, new FullForce(maxf));

        //follower class, takes a path, pid config, and target dt
        double kP = 20;
        double kI = 0.00;
        double kD = 10; 
        var g = new PIDMotorFollow(p, new PIDConfig(kP,kI,kD), mc, dt);

        //realtime simulation variable
        double t = 0;
        
        //graph for testing
        var gc = new Graph.GraphConfig();
        gc.x2 = 31;
        gc.x1 = 0;
        gc.y1 = -5;
        gc.y2 = 5;
        Graph G = new Graph(gc);
        G.addPlot(Color.RED);
        G.addPlot(Color.GREEN);
        G.addPlot(Color.MAGENTA);
        //go for thirty fake seconds of following the control effect at target intervals, and graph it
        while (t < 30) {
            double controlEff = g.rawControlEffectNow(m.p(), m.v(), t);
            double motorEff = g.motorOutputNow(m.p(), m.v(), t);
            m.exert(motorEff, realdt);
            G.addPoint(t, m.p(), false, 0);
            G.addPoint(t, motorEff / 100, false, 1);
            G.addPoint(t, controlEff / 10, false, 2);
            t += realdt;
        }

        //plot path for comparison
        G.addPlot(Color.BLUE);
        DoubleFunction F = new DoubleFunction() {
            public double eval(double x) {
                Double ret = p.y(x);
                if (ret == null) {
                    return 0;
                } else {
                    return ret;
                }
            }
        };
        G.plot(0, 30, F, dt, 3);

        //output graph
        G.init(1000,1000, "folow");
    }

    public static void testCrazy() {
        //a test and example for a pidmotorfollower

        //simulation for time variance in processing
        Random random = new Random();

        //make the path and put segments in it
        var p = new Path();
        // p.insertSegment(new LinearSegment(0,1,15,16));
        // p.insertSegment(new LinearSegment(15,16,30,1));
        p.insertSegment(new LinearSegment(0,0,10,0));
        p.insertSegment(new LinearSegment(10,5,30,5));
        
        //target control effect grain
        double dt = 0.005;
        //bug: chaos when 0.05???? and that random loop
        //bug: when target dt is lower than the increasing time we get stuck
        //bug: yeah crazy patterns hm

        //simulation for the motor
        double maxf = 100;
        double mass = 1;
        var m = new ArtificialMotor(mass, 0.01, 0, maxf);
        var mc = new MotorControlProfile(ControlLevel.VELOCITY, ControlLevel.ACCELERATION, new FullForce(maxf));

        //follower class, takes a path, pid config, and target dt
        double kP = 10;
        double kI = 0.00;
        double kD = 1; 
        var g = new PIDMotorFollow(p, new PIDConfig(kP,kI,kD), mc, dt);

        //realtime simulation variable
        double t = 0;
        
        //graph for testing
        var gc = new Graph.GraphConfig();
        gc.x2 = 31;
        gc.x1 = 0;
        gc.y1 = -1;
        gc.y2 = 5;
        Graph G = new Graph(gc);
        G.addPlot(Color.RED);
        G.addPlot(Color.GREEN);
        //go for thirty fake seconds of following the control effect at target intervals, and graph it
        while (t < 30) {
            double rtdt = random.nextDouble() / 1000;
            double motorEff = g.motorOutputNow(m.p(), m.v(), t);
            m.exert(motorEff, rtdt);
            G.addPoint(t, motorEff/100, false, 1);
            G.addPoint(t, m.p(), false, 0);
            t += /*1/100 - 1/2000 + */ rtdt;
        }

        //plot path for comparison
        G.addPlot(Color.BLUE);
        DoubleFunction F = new DoubleFunction() {
            public double eval(double x) {
                Double ret = p.y(x);
                if (ret == null) {
                    return 0;
                } else {
                    return ret;
                }
            }
        };
        G.plot(0, 30, F, dt, 2);

        //output graph
        G.init(1000,1000, "folow");
    }
}