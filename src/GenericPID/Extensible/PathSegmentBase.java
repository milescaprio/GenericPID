package GenericPID.Extensible;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public abstract class PathSegmentBase {
    //IMPORTANT:
    //assumes that the segment follows a function and can be chopped into smaller segments without superclass intervention
    //therefore, structure your class so the members don't need to be changed based on the location of the segment.
    public double x1;
    public double x2;

    public abstract double y(double x); //hehe2
    
    public PathSegmentBase chopEnd(double x) {
        /*
                 /
               /
             /
           /
         /
        |_ _ _ _|x
        */
        var s = duplicate();
        s.x2 = x;
        return s;
    }
    public PathSegmentBase chopBeginning(double x) {
        /*
                 /
               /
             /
           /
         /
         x|_ _ _ _|
        */
        var s = duplicate();
        s.x1 = x;
        return s;
    }
    public ArrayList<PathSegmentBase> sliceChunk(double x1, double x2) {
        /*
                 /
               /
             /
           /
         /
        |_ _|x x|_|
        */
        ArrayList<PathSegmentBase> ret = new ArrayList<PathSegmentBase>();
        ret.add(chopBeginning(x2));
        ret.add(chopEnd(x1));
        return ret;
    }
    
    public abstract double derivative(double x);

    private PathSegmentBase duplicate() {
        //Gets the instance of the extended class and returns a copy
        try {
            Class<? extends PathSegmentBase> sub = this.getClass(); 
            Constructor<? extends PathSegmentBase> constructor = sub.getDeclaredConstructor(sub);
            constructor.setAccessible(true);
            return constructor.newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}