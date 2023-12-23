package GenericPID.ApproximationUtils;


import GenericPID.Extensible.DoubleFunction;

/**
 * An object that accumulates area under a curve, either with an internally stored function or with
 * manual supplement of the data points. ("Manual")
 */
public class ApproximateIntegral {
    private double x1;
    private double x;
    private double Y;
    private DoubleFunction f;


    public ApproximateIntegral(double x1, double C) {
        this.resetIteratorManual(x1, C);
    }
    
    public ApproximateIntegral(DoubleFunction f, double x1, double C) {
        this.resetIterator(f, x1, C);
    }

    public double x1() {
        return x1;
    }
    public double val() {
        return Y;
    }

    public double iteratorX() {
        return this.x;
    }

    public void resetIteratorManual(double x, double C) {
        this.x = x;
        this.x1 = x;
        this.Y = C;
    }
    public void resetIterator(DoubleFunction f, double x, double C) {
        this.x = x;
        this.x1 = x;
        this.Y = C;
        this.f = f;
    }

    /**
     * Does not return because of nature of the integral which needs to run many functions to calculate at a point; unlike derivative
     */

    public void iterateAccumulation(double h) {
        Y += f.eval(x) * h;
        x += h;
    }

    public void iterateAccumulationManual(double h, double f_x_plus_h) {
        x += h;
        Y += f_x_plus_h * h;
    }

    public void integrate(DoubleFunction f, double dx, double x2) {
        this.f = f;
        while (x < x2) {
            iterateAccumulation(dx);
        }   
    }
}