package GenericPID.Testing;

import GenericPID.ApproximationUtils.ApproximateIntegral;
import GenericPID.Debug;

import java.lang.Math;
import java.awt.Color;
/**
  *An artificial motor class used for simulation
  *the motor can absolutely control acceleration, and therefore can resultingly control velocity and position.
  *therefore, the motor has behavior to target the velocity (because there's no overshoot)
  *position should be controlled with a separate PID controller, because that's what it's for
  *additionally, you should confirm that you start with firstEffect which omits the derivative and integral terms because they are not calculatable yet.
  *Make sure all operations are done in the dt range AFTER the controleffect is calculated.
*/
public class ArtificialMotor {
    //graph of how the pid time should update
    //t .          .          .
    //   |________| |________|
    //    motor i0   motor i1    (you handle)
    //  di0  di1        di2      (internal) 
    //   i0        i1       i2   (internal)
    //   p0        p1       p2   (internal)
    
    private static final boolean debug = Debug.debug_ArtificialMotor;
    
    
    
    private final double mass; //SI, kg
    private final double kdrag; //SI, unitless
    private final double kkdrag; //SI, units?
    private final double force; //SI, N
    
    //The artificial motor is simulated with tangential physics, not rotational,
    //so anytime the motor has a force applied, kdrag * v is subtracted, and
    //acceleration is calculated by mass 
    
    private ApproximateIntegral velocity = new ApproximateIntegral(0, 0); //SI, integrated; m/s
    private ApproximateIntegral position = new ApproximateIntegral(0, 0); //SI, integrated; m
    private double t;
    
    public static void main(String[] args) {
        test();
    }
    public static void test() {
        final int n = 6;
        ArtificialMotor[] m = new ArtificialMotor[n];
        Color[] c = new Color[]{Color.BLUE, Color.RED, Color.GREEN, Color.BLUE.darker(), Color.RED.darker(), Color.GREEN.darker()};
        for (int i = 0; i < n/2; i++) {
            m[i] = new ArtificialMotor(1, i * 0.2, 0, 0.5);
        }
        for (int i = n/2; i < n; i++) {
            m[i] = new ArtificialMotor(1, 0, (i-n/2) * 0.2, 0.5);
        }
        Graph g = new Graph(new Graph.GraphConfig());
        g.init(1000,1000, "Motor to 1 m/s");
        
        for(int i = 0; i < n; i++) {
            g.addPlot(c[i]);
            double dt = 0.01;
            while (m[i].t() < 10) {
                m[i].exert(0.5, dt);
                g.addPoint(m[i].t(), m[i].p(), false, i);
            }
            // while (m[i].t() < 10) {
            //     m[i].direct(1, dt);
            //     g.addPoint(m[i].t(), m[i].p(), i);
            // }
            // while (m[i].t() < 15) {
            //     m[i].slow(dt);
            //     g.addPoint(m[i].t(), m[i].p(), i);
            // }
        }
    }

    public double v() {
        return velocity.val();
    }
    public double p() {
        return position.val();
    }
    public double t() {
        return t;
    }
    public double maxF() {
        return force;
    }
    public void exert(double F, double dt) {
        if (Math.abs(F) > force) { //cap it
            F = force * Math.abs(F) / F;
        }
        double fnet = F - kdrag * velocity.val() - kkdrag * velocity.val() * Math.abs(velocity.val());
        velocity.iterateAccumulationManual( (fnet) / mass, dt);
        position.iterateAccumulationManual(velocity.val(), dt);
        if(debug) System.out.printf("motor-side t up by %f!, v is now %f\n", dt, velocity.val());
        t += dt;
    }
    //all constructors
    public ArtificialMotor() {
        this(2,0.2,0.1,2);
    }
    public ArtificialMotor(double mass, double kdrag, double kkdrag, double force) {
        this.mass = mass;
        this.kdrag = kdrag;
        this.kkdrag = kkdrag;
        this.force = force;
    }
    public ArtificialMotor(double t0, double v0, double p0) {
        this();
        this.t = t0;
        this.velocity.resetIteratorManual(t0, v0);
        this.position.resetIteratorManual(t0, p0);
    }
    public ArtificialMotor(double mass, double kdrag, double kkdrag, double force, double t0, double v0, double p0) {
        this(mass, kdrag, kkdrag, force);
        this.t = t0;
        this.velocity.resetIteratorManual(t0, v0);
        this.position.resetIteratorManual(t0, p0);
    }

    public static class Builder {
        private double mass = 2;
        private double kdrag = 0.2;
        private double kkdrag = 0.1;
        private double force = 2;
        private double t0 = 0;
        private double v0 = 0;
        private double p0 = 0;

        public Builder mass(double mass) {
            this.mass = mass;
            return this;
        }
        public Builder kdrag(double kdrag) {
            this.kdrag = kdrag;
            return this;
        }
        public Builder kkdrag(double kkdrag) {
            this.kkdrag = kkdrag;
            return this;
        }
        public Builder force(double force) {
            this.force = force;
            return this;
        }
        public Builder t0(double t0) {
            this.t0 = t0;
            return this;
        }
        public Builder v0(double v0) {
            this.v0 = v0;
            return this;
        }
        public Builder p0(double p0) {
            this.p0 = p0;
            return this;
        }
        public ArtificialMotor build() {
            return new ArtificialMotor(mass, kdrag, kkdrag, force, t0, v0, p0);
        }
    }
}