package GenericPID;

import java.awt.Color;

import GenericPID.ApproximationUtils.ApproximateDerivative;
import GenericPID.ApproximationUtils.ApproximateIntegral;
import GenericPID.Testing.ArtificialMotor;
import GenericPID.Testing.Graph;

/**
  *A basic pid controller class, that lets the motor handle tracking position, velocity, and max of these. 
  *For this functionality, extend this class (could override controlEffect).
  *The max effective target velocity should be implicitly in line with the tuning, and the motor should cap itself. This object is quite basic.
  *The motor should listen to pid after each control effect, and keep track of its own time value too (this can be a real life time value)
  */
  public class PID {
      //graph of how the pid time should update
      // .          .          .
      //  |________| |________|
      //   motor i0   motor i1    (you handle)
      // di0  di1        di2      (internal) 
      //  i0        i1       i2   (internal)
      //  p0        p1       p2   (internal)

    private static final boolean debug = Debug.debug_PID;

    private PIDConfig conf;
    private double t;
    private ApproximateDerivative dedt;
    private ApproximateIntegral E;

    public PID(PIDConfig config) {
        this.conf = config;
        this.t = 0;
        this.dedt = new ApproximateDerivative(0, 0);
        this.E = new ApproximateIntegral(0, 0);
    }
    
    public void skipNextDTerm(double target, double current) {
        dedt.resetIteratorManual(t, target - current);
    }   

    private double firstEffect(double target, double current, double dt) {
        double curre = target - current;
        dedt.resetIteratorManual(0, curre);
        double eff = conf.kP * curre; //No accumulation, unknown derivative
        if(debug)System.out.printf("First Effect! %f Current Error! %f\n" , eff, curre);
        return eff;
    }

    public double controlEffect(double target, double current, double dt) {
        //if we haven't evaluated a controleffect yet, uses firstEffect
        if (t == 0) {
            return firstEffect(target, current, dt);
        }
        //standard PID logic
        double curre = target - current;
        double currdedt = dedt.iterateDerivativeManual(t + dt, curre);
        E.iterateAccumulationManual(curre, dt);
        double currE = E.val();
        double P = conf.kP * curre;
        double I = conf.kI * currE;
        double D = conf.kD * currdedt;
        double FF = conf.ff.eval(t);
        double eff = P + I + D + FF;
        if(debug)System.out.printf("Time: %f Effect! %f Current E! %f Current IE! %f Current DE! %f, Current FF! %f\n" , this.t, eff, P, I, D, FF);
        return eff;
    }
    public void nextT(double dt) {
        t += dt;
    }
    public boolean isSynced(double t, double tol) { //range is usually equal to dt but depends on checking situation
        return (Math.abs(t - this.t) > tol);     
    }
    public double t() {
        return t;
    }





























    public static void main(String[] args) {
        test();
    }

    public static void test() {
        final double l = 3;
        PIDConfig c = new PIDConfig();
        c.kP = 1.0;//1.7;
        c.kI = 0.0;//0.0001;
        c.kD = 0.01;//0.06;
        PID p = new PID(c);
        //ArtificialMotor m = new ArtificialMotor(2, 0.1, 0, 2, 0, 0, 0);
        ArtificialMotor m2 = new ArtificialMotor.Builder().v0(2).build();
        ArtificialMotor m = new ArtificialMotor(1, 0.00, 0, 1);
        double dt = 0.005;
        double target = 5;//2;
        // Graph g = new Graph(new Graph.GraphConfig.Builder.y2(100).build());
        Graph.GraphConfig gc = new Graph.GraphConfig();
        gc.x1 = 0;
        gc.x2 = l;//20;
        gc.y1 = -1;//-3;
        gc.y2 = 7; //3;
        Graph g = new Graph(gc);
        g.addPlot(Color.BLUE);
        g.addPlot(Color.RED);
        g.addPlot(Color.GREEN);
        for (double ti = 0; ti < l; ti+=dt) {
            //first the control effect is updated (including with the derivative of currrent to last),
            //then the motor is directed towards this velocity, and the motor updates its position and known time.
            //then the pid updates its known time, should be in sync with the motor's known time
            //see graph atop this class
            
            //assume this will just be in sync, use reset if needed
            double ce = p.controlEffect(target, m.p(), dt);
            double f; //manual handling with PID, PIDMotorFollow includes this abstraction for you
            if (ce > m.v()) {
                f = m.maxF();
            } else if (ce < m.v()) {
                f = -m.maxF();
            } else {
                f = 0;
            }
            m.exert(f ,dt); 
            p.nextT(dt);
            assert p.isSynced(m.t(), 0.0001);
            if(debug)System.out.printf("t:%f p:%f v:%f\n", ti, m.p(), m.v());
            g.addPoint(ti, m.p(), false, 0);
            g.addPoint(ti, m.v(), false, 1);
        }
        g.init(1000,1000,"PID Test");
    }
}