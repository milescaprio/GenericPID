package GenericPID.ApproximationUtils;


import GenericPID.Debug;
import GenericPID.Extensible.DoubleFunction;

/**
 * An object that can track an ongoing derivative of a function with either internal storage of the function
 * Or manual supplement of the data points. ("Manual")
 * 
 * <p> Can also do derivative calculations in static functions.
 */
public class ApproximateDerivative {
    //a sometimes static class which takes the derivative of a certain point of a function
    //just use the derivative method, and a double function
    //or track ongoing derivative by using nextDerivative()
    //use either DoubleFunction unit or manual evaluation, either f or ylast
    private static final boolean debug = Debug.debug_Approximations;
    
    private double xlast;
    private double ylast;
    private DoubleFunction f;

    public ApproximateDerivative(DoubleFunction f, double x) {
        this.resetIterator(f, x);
        ylast = 0;
    }
    public ApproximateDerivative(double x, double y) {
        this.xlast = x;
        this.ylast = y;
    }
    public ApproximateDerivative() {
        this.xlast = 0;
        ylast = 0;
    }

    /**
     * Takes the slope of F using the formula (f(x+h) - f(x)) / h
     */
    public static double derivative_front_h(DoubleFunction f, double x, double h) {
        return (f.eval(x + h) - f.eval(x)) / h;
    }
    /**
     * Takes the slope of F using the formula (f(x) - f(x-h)) / h
     */
    public static double derivative_back_h(DoubleFunction f, double x, double h) {
        return (f.eval(x) - f.eval(x - h)) / h;
    }

    public static double slope(double x1, double y1, double x2, double y2) {
        if (debug)System.out.printf("derivative (%f-%f)/(%f-%f)\n", y2, y1, x2, x1);
        return (y2 - y1) / (x2 - x1); //hehe
    }


    public static double slope(double y1, double y2, double Dx) {
        if(debug)System.out.printf("derivative (%f-%f)/%f\n", y2, y1, Dx);
        return (y2 - y1) / Dx;
    }

    
    /**
     * View the next derivative in the next dx window, using the final calculation from the last as the beginning.
     */
    public double iterateDerivative(double dx) {
        return slope(xlast, ylast, xlast = xlast + dx, ylast = f.eval(xlast + dx));
    }

    /**
     * View the next derivative in the next dx window, using the final calculation from the last as the beginning.
     */
    public double iterateDerivativeManual(double dx, double f_x_plus_h) {
        return slope(xlast, ylast, xlast = xlast + dx, f_x_plus_h);
    }

    public boolean isSynced(double x, double tol) { //range is usually equal to dt but depends on checking situation
        return (Math.abs(x - this.xlast) > tol);     
    }

    public double iteratorX() {
        return this.xlast;
    }

    public void resetIterator(DoubleFunction f, double x1) {
        this.f = f;
        this.xlast = x1;
        this.ylast = f.eval(x1);
    }
    public void resetIterator(DoubleFunction f, double x1, double y1) {
        this.f = f;
        this.xlast = x1;
        this.ylast = y1;
    }
    public void resetIteratorManual(double x1, double y1) {
        this.xlast = x1;
        this.ylast = y1;
    }
}