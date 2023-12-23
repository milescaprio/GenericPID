package GenericPID.Extensible;
/**
 * An interface to track how long something takes based on their position and velocity current and target values. 
 * Can be used for anything but PID is the main purpose. After tuning PID, test different distances and regress 
 * them to a function that can be encapsulated in an implementation of this interface.
 */
public interface EstimatedTimeArrival {
    double eta(double currx, double targetx, double currv, double targetv);
}