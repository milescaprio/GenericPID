package GenericPID.ApproximationUtils;

import GenericPID.Extensible.DescentRunnableInjection;
import GenericPID.Extensible.DoubleFunction;
import GenericPID.Implementations.Match;
import GenericPID.Implementations.NoInjection;
import GenericPID.Implementations.NoJump;
import GenericPID.Implementations.Zero;
import GenericPID.Testing.Graph;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.awt.Color;


/**
  * Creates a new Gradient Descent approximation.
  * <p><b><h3>Gradient Descent Methods:</h3></b>
  * <p><b>Alpha:</b> Add the gradient times a constant alpha value to the x each iteration. This method can get stuck 
  * at exact plateaus, but it's rare. Best for finding normal minima in a volatile function
  * <p><b>Projection:</b> Add the inverse of the gradient times the target change in y value to the x each iteration, 
  * quickly calculating where the error point is (error function should go to zero). This is good for functions that
  * have gradients that tend to go towards extrema at the bottom of the error. This calculation is also capped at a certain
  * delta x value so it won't go too far in high-elevation low-gradients. This method does not work well with plateaus.
  * This method is better for quickly finding minima in a predictable function with known bottom error values.
  * <P><b>ProjectionAlpha:</b> Add a projection value times a constant alpha to the x value.
  * <p><h3><b>Gradient Descent Ending Methods:</b></h3>
  * <p><b>Iterations/CutOff:</b> Repeat the descent a certain amount of times, and then a few more, checking that the
  * descent was successful and is not diverging.
  * <p><b>Convergence/Deadline:</b> Repeat the descent until an acceptable error is reached, or until the maxIters are reached.
  * (If max iters are reached, they or the acceptable error are probably too low or the algorithm is diverging.)
  */
public class GradientDescent {
    public Integer startIters = null;
    public Integer checkIters = null;
    public Double alpha = null;
    public Double acceptableError = null;
    public Double maxDx = null;
    public Integer maxIters = null;
    public EndMethod endMethod = null;
    public ConvergeMethod convergeMethod = null;
    public DoubleFunction errorFunc = new Zero();
    public DoubleFunction dsquish = new Match();
    public DoubleFunction derivativeFunc = new Zero();
    public DerivativeMethod derivativeMethod = null;

    /**
     * Constructs a GradientDescentYT object, which will be evaluated with descend(). 
     * See the GradientDescentYTarget doc comment for an explanation of how to choose your constructor.
     */
    public static GradientDescent AlphaConverge(            DoubleFunction error, DoubleFunction dsquish, DoubleFunction derrordt, int    maxIters,                   double acceptable,double alpha) {
        var ret = new GradientDescent();
        ret.acceptableError = acceptable;
        ret.alpha = alpha;
        ret.errorFunc = error;
        ret.dsquish = dsquish;
        ret.derivativeFunc = derrordt;
        ret.maxIters = maxIters;
        ret.convergeMethod = ConvergeMethod.ALPHA;
        ret.endMethod = EndMethod.CUTOFF;
        ret.derivativeMethod = DerivativeMethod.ABSOLUTE;
        return ret;
    }
    /**
    * Constructs a GradientDescentYT object, which will be evaluated with descend(). 
    * See the GradientDescentYTarget doc comment for an explanation of how to choose your constructor.
    */
    public static GradientDescent ProjectionConverge(       DoubleFunction error, DoubleFunction dsquish, DoubleFunction derrordt, int    maxIters,   double maxDx,     double acceptable) {
        var ret = new GradientDescent();
        ret.acceptableError = acceptable;
        ret.errorFunc = error;
        ret.dsquish = dsquish;
        ret.derivativeFunc = derrordt;
        ret.maxIters = maxIters;
        ret.maxDx = maxDx;
        ret.convergeMethod = ConvergeMethod.PROJECT;
        ret.endMethod = EndMethod.CUTOFF;
        ret.derivativeMethod = DerivativeMethod.ABSOLUTE;
        return ret;
    }
    /**
    * Constructs a GradientDescentYT object, which will be evaluated with descend(). 
    * See the GradientDescentYTarget doc comment for an explanation of how to choose your constructor.
    */
    public static GradientDescent ProjectionAlphaConverge(  DoubleFunction error, DoubleFunction dsquish, DoubleFunction derrordt, int    maxIters,   double maxDx,     double acceptable,double alpha) {
        var ret = new GradientDescent();
        ret.maxIters = maxIters;
        ret.acceptableError = acceptable;
        ret.errorFunc = error;
        ret.dsquish = dsquish;
        ret.derivativeFunc = derrordt;
        ret.maxIters = maxIters;
        ret.alpha = alpha;
        ret.maxDx = maxDx;
        ret.convergeMethod = ConvergeMethod.PROJECTALPHA;
        ret.endMethod = EndMethod.CUTOFF;
        ret.derivativeMethod = DerivativeMethod.ABSOLUTE;
        return ret;
    }
    /**
    * Constructs a GradientDescentYT object, which will be evaluated with descend(). 
    * See the GradientDescentYTarget doc comment for an explanation of how to choose your constructor.
    */
    public static GradientDescent AlphaIterations(          DoubleFunction error, DoubleFunction dsquish, DoubleFunction derrordt, int    startIters, int    checkIters,double acceptable,double alpha) {
        var ret = new GradientDescent();
        ret.startIters = startIters;
        ret.checkIters = checkIters;
        ret.acceptableError = acceptable;
        ret.alpha = alpha;
        ret.errorFunc = error;
        ret.dsquish = dsquish;
        ret.derivativeFunc = derrordt;
        ret.convergeMethod = ConvergeMethod.ALPHA;
        ret.endMethod = EndMethod.DEADLINE;
        ret.derivativeMethod = DerivativeMethod.ABSOLUTE;
        return ret;
    }
    /**
    * Constructs a GradientDescentYT object, which will be evaluated with descend(). 
    * See the GradientDescentYTarget doc comment for an explanation of how to choose your constructor.
    */
    public static GradientDescent ProjectionIterations(     DoubleFunction error, DoubleFunction dsquish, DoubleFunction derrordt, int    startIters, int    checkIters,double acceptable) {
        var ret = new GradientDescent();
        ret.startIters = startIters;
        ret.checkIters = checkIters;
        ret.acceptableError = acceptable;
        ret.errorFunc = error;
        ret.dsquish = dsquish;
        ret.derivativeFunc = derrordt;
        ret.convergeMethod = ConvergeMethod.PROJECT;
        ret.endMethod = EndMethod.DEADLINE;
        ret.derivativeMethod = DerivativeMethod.ABSOLUTE;
        return ret;
    } 
    /**
    * Constructs a GradientDescentYT object, which will be evaluated with descend(). 
    * See the GradientDescentYTarget doc comment for an explanation of how to choose your constructor.
    */
    public static GradientDescent ProjectionAlphaIterations(DoubleFunction error, DoubleFunction dsquish, DoubleFunction derrordt, int    startIters, int    checkIters,double acceptable,double alpha) {
        var ret = new GradientDescent();
        ret.startIters = startIters;
        ret.checkIters = checkIters;
        ret.acceptableError = acceptable;
        ret.errorFunc = error;
        ret.dsquish = dsquish;
        ret.derivativeFunc = derrordt;
        ret.alpha = alpha;
        ret.convergeMethod = ConvergeMethod.PROJECTALPHA;
        ret.endMethod = EndMethod.DEADLINE;
        ret.derivativeMethod = DerivativeMethod.ABSOLUTE;
        return ret;
    }
    /**
     * Runs the gradient descent method, with all the internal construction.
     */
    public Descent descend(double startX) {
        return descend(startX, new NoInjection());
    }

    /**
     * Runs the gradient descent method, with all the internal construction..
     * @param injection If extra output or feedback in the method is wanted, it can be put here.
      Takes the current error and outputs an extra offset. Great for using as a port for debugging.
     */
    public Descent descend(double startX, DescentRunnableInjection injection) {

        DoubleFunction d;
        DoubleFunction error = this.errorFunc;
        DoubleFunction next;
        double x = startX;
        ArrayList<Double> xHistory = new ArrayList<Double>();
        xHistory.add(x);

        if (derivativeMethod == DerivativeMethod.ABSOLUTE) {
            d = new DoubleFunction() {
                public double eval(double x) {
                    if (Math.abs(x - 0.0) < 0.001) {
                        return 0.01;
                    }
                    return dsquish.eval(derivativeFunc.eval(x));
                }
            };
        } else {/*(derivativeMethod == DerivativeMethod.ESTIMATE)*/
            throw new RuntimeException("Derivative.ESTIMATE is not implemented yet in the GenericPID Library", null);
        }

        if (convergeMethod == ConvergeMethod.ALPHA) {
            next = new DoubleFunction() {
                public double eval(double x) {
                    injection.eval(x, error.eval(x), d.eval(x));
                    return x + -d.eval(x) * alpha;
                }
            };
        } else if (convergeMethod == ConvergeMethod.PROJECT) {
            next = new DoubleFunction() {
                public double eval(double x) {
                    injection.eval(x, error.eval(x), d.eval(x)); 
                    return x + Functions.clamp(-error.eval(x) / d.eval(x), -maxDx, maxDx); //target Dy * dx / dy
                }
            };
        } else { //if (convergeMethod == ConvergeMethod.PROJECTALPHA)
            next = new DoubleFunction () {
                public double eval(double x) {
                    injection.eval(x, error.eval(x), d.eval(x)); 
                    return x + Functions.clamp(-error.eval(x) / d.eval(x) * alpha, -maxDx, maxDx); //target Dy * dx / dy
                }
            };
        }
        if (endMethod == EndMethod.DEADLINE) {
            for (int i = 0; i < startIters; i++) {
                x = next.eval(x);
                xHistory.add(x);
            }
            double lastDiff = 0.0;
            boolean stable = true;
            for (int i = 0; i < checkIters; i++) {
                double temp = x;
                x = next.eval(x);
                lastDiff = temp - x;
                if (lastDiff < 0) {
                    stable = false;
                    break;
                }
            }
            Descent descent = new Descent();
            if (stable) {
                if (lastDiff > acceptableError) { //todo: fix this heuristic, good enough for now
                    descent.result = Result.CONVERGING;
                } else {
                    descent.result = Result.CONVERGED;
                }
            } else {
                descent.result = Result.DIVERGED;
            }
            descent.value = x;
            descent.xHistory = xHistory;
            return descent;
        } else { /*(endMethod == EndMethod.CUTOFF)*/
            Descent descent = new Descent();
            int i = 0;
            while (Math.abs(error.eval(x)) > acceptableError && i < maxIters) {
                x = next.eval(x);
                xHistory.add(x);
                i++;
            }
            if (i == maxIters) {
                descent.result = Result.DIVERGED;
            } else {
                if (error.eval(x) > acceptableError) {
                    descent.result = Result.KNOWN_PUDDLE;
                }
                descent.result = Result.CONVERGED;
            }
            descent.xHistory = xHistory;
            descent.value = x;
            return descent;
        }
    }
    public enum EndMethod {
        DEADLINE,
        CUTOFF,
    }
    public enum ConvergeMethod {
        ALPHA,
        PROJECT,
        PROJECTALPHA,
    }
    public enum DerivativeMethod {
        ABSOLUTE,
        ESTIMATE
    }
    public enum Result {
        CONVERGED,
        CONVERGING,
        DIVERGED,
        KNOWN_PUDDLE,
    }
    public static class Descent {
        public double value;
        public Result result; 
        public ArrayList<Double> xHistory;
        public Descent(double v, Result r, ArrayList<Double> xHistory) {
            value = v;
            result = r;
            this.xHistory = xHistory;
        }
        public Descent() {}
    }
    public static void main(String args[]) {
        test();
    }
    public static void test() {
        while (true) {
            //New graph
            Graph.GraphConfig gc = new Graph.GraphConfig();
            gc.y2max = 70;
            gc.y1min = -70;
            gc.x2max = 10;
            gc.x1min = -10;
            Graph g = new Graph(gc);
            //Configuring descent of graph
            double dx = 0.01;
            double alpha = 0.1;//1 / (Math.random() * 5 + 5);
            //Function to descend
            DoubleFunction traversable = new DoubleFunction() {
                public double eval(double x) {
                    return .1*(x*x*x*x*x*x) + .1*(x*x*x*x*x) + x*x*x*x + x*x*x - 9*x*x -.1*x + 24.80689294955; //.1x6, .1x5, x4, x3, -9x2, .-1x1, makes two puddles, one goes down to zero
                }
            };
            //Derivative of function to descent
            DoubleFunction dtraversabledr = new DoubleFunction () {
                public double eval(double x) {
                    return .1*(x*x*x*x*x)*6 + .1*(x*x*x*x)*5 + 4*(x*x*x) + 3*(x*x) - 9*x*2 - .1;
                }
            };
            //Add plots and plot the function
            g.addPlot(Color.RED);
            g.plot(-10,10, traversable, new NoJump(), dx, 0);
            g.addPlot(Color.BLUE);
            g.addPlot(Color.GREEN);
            g.addPlot(Color.ORANGE);
            g.addPlot(Color.BLACK);
            //Tangent lines
            double tangentlinelen = 0.5;
            double xh = 2;
            double xw = 0.25;
            double start = Math.exp(Math.random() / 2);
            //Create gradient descent algorithm
            GradientDescent gd = GradientDescent.AlphaIterations(traversable, new Match() /*new DoubleFunction() {
                public double eval(double x) {
                    //return Math.log(Math.abs(x)) * Functions.spaceship(0, x);
                    return Math.sqrt(Math.abs(x)) * Functions.spaceship(0,x) * (Math.random() * 0.3 + 1);
                }
            }*/, dtraversabledr, 12, 5, 0.001, alpha);
            class ClosedInt {
                public int n = 0;
                public void set(int val) {
                    n = val;
                }
            }
            class ClosedDouble {
                public double n = 0;
                public void set(double val) {
                    n = val;
                }
            }
            ClosedInt n = new ClosedInt();
            ClosedDouble lastX = new ClosedDouble();
            g.init(1600, 900, "descent!");
            Descent d = gd.descend(start, new DescentRunnableInjection() {
                public void eval(double oldx, double error, double derrordt) {
                    g.addPoint(lastX.n, traversable.eval(lastX.n), false, 3);
                    g.addPoint(oldx, traversable.eval(lastX.n), false, 3);
                    lastX.set(oldx);
                    g.addPoint(oldx - tangentlinelen / 2, traversable.eval(oldx) - tangentlinelen * derrordt / 2, true, 1);
                    g.addPoint(oldx + tangentlinelen / 2, traversable.eval(oldx) + tangentlinelen * derrordt / 2, false, 1);
                    g.addPoint(oldx, traversable.eval(oldx), true ,2);
                    g.addPoint(oldx, traversable.eval(oldx) - error, false ,2);
                    g.addPoint(oldx + xw / 2, traversable.eval(oldx) + xh / 2, false, 4);
                    g.addPoint(oldx - xw / 2, traversable.eval(oldx) - xh / 2, false, 4);
                    g.addPoint(oldx - xw / 2, traversable.eval(oldx) + xh / 2, true, 4);
                    g.addPoint(oldx + xw / 2, traversable.eval(oldx) - xh / 2, false, 4);
                    System.out.printf("Iteration %d: at x %f with derivative %f and error %f\n", n.n, oldx, derrordt, error);
                    n.set(n.n + 1);
                    try {
                        TimeUnit.MILLISECONDS.sleep(350);
                    } 
                    catch (InterruptedException e) {
                        
                    }
                    g.drawagain();
                    g.popLastPoint(4);
                    g.popLastPoint(4);
                    g.popLastPoint(4);
                    g.popLastPoint(4);
                    g.popPoint(0, 3);
                    g.popPoint(0 ,3);
                }
            });
        }
    }
}