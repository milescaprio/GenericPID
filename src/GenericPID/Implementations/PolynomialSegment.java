package GenericPID.Implementations;

import GenericPID.Extensible.PathSegmentBase;

import java.util.ArrayList;


//untested
public class PolynomialSegment extends PathSegmentBase {
    private ArrayList<Double> coeffs; //the index represents the x degree, so {1,2,3} = 3x^2 + 2x + 1
    public PolynomialSegment(ArrayList<Double> coeffs) {
        this.coeffs = coeffs;
    }
    public double y(double x) {
        double ret = 0;
        for (int i = 0; i < coeffs.size(); i++) {
            ret += coeffs.get(i) * Math.pow(x, i);
        }
        return ret;
    }
    public double derivative(double x) {
        double ret = 0;
        for (int i = 1; i < coeffs.size(); i++) {
            if (i == 1) {
                ret += coeffs.get(i);
            } else {
                ret += coeffs.get(i) * i * Math.pow(x, i - 1);
            }
        }
        return ret;
    }
    public void setCoeff(int power, double value) {
        coeffs.set(power, value);
    }
    public void setDegree(int degree) {
        while (degree + 1 < coeffs.size()) { //Plus one for difference in degree-to-terms count
            coeffs.remove(coeffs.size() - 1);
        }
        while (degree + 1 > coeffs.size() ) {
            coeffs.add(0.0);
        }
    }
    final class Pair {
        public double a;
        public double b;
        public Pair(double a, double b) {
            this.a = a;
            this.b = b;
        }
    }
}