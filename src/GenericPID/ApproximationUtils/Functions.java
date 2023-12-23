package GenericPID.ApproximationUtils;

public class Functions {
    public static double clamp(double n, double a, double b) {
        if (n < a) {
            return a;
        }
        if (n > b) {
            return b;
        }
        return n;
    }
    public static double spaceship(double n1, double n2) {
        if (n1 == n2) {
            return 0;
        }
        return Math.abs(n2 - n1) / (n2 - n1);
    }
    public static double max(double n1, double n2) {
        if (n1 > n2) {
            return n1;
        }
        return n2;
    }
    public static double min(double n1, double n2) {
        if (n1 < n2) {
            return n1;
        }
        return n2;
    }
    public static double goodmod(double a, double b) {
        if (b < 0) b = -b;
        if (a < 0) {
            double n = Math.ceil(-a / b); //doesnt need to be a double
            a += n * b;
        }
        return a % b;
    }
}
