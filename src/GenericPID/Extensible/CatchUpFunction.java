package GenericPID.Extensible;

/**
 * Implement your own function for what to do when the time is far behind the normal dt. 
 * (Catch all the way up? Partition the catchup slowly?)
 * @param timeBehind An input for how far behind the time is, not including the dt
 * which was going to be added in the first place.
 * @param timesBehind An input for how long this problem has been going in a row, counted.
 * @param normalDt The expected amount of time used incrementally and given to catch up.
 */
public interface CatchUpFunction {
    public double dt(double timeBehind, int timesBehind, double normalDt); //timebehind is not behind target, but behind "on track" which includes dt
}