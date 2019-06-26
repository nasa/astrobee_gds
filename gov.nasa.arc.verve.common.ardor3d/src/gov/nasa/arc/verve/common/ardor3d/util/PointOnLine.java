package gov.nasa.arc.verve.common.ardor3d.util;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

/**
 * Find the closest point on a line given a test point
 * Adapted from a compact solution found here: http://www.ogre3d.org/tikiwiki/Nearest+point+on+a+line
 * with the exception of clamping to endpoints which can be 
 * efficiently done using tS
 */
public class PointOnLine {
    private final Vector3 a = new Vector3(); // point 1 of line
    private final Vector3 b = new Vector3(); // point 1 of line
    private final Vector3 A = new Vector3(); // vector from a to test point
    private final Vector3 B = new Vector3(); // vector from a to b
    private final Vector3 u = new Vector3(); // normalized vector from a to b 
    private final Vector3 V = new Vector3(); // vector along line to give point
    private double tS;
    private double len;
    private final Vector3 result = new Vector3();
        
    public void setLine(ReadOnlyVector3 begin, ReadOnlyVector3 end) {
        a.set(begin);
        b.set(end);
        len = a.distance(b);
    }

    /**
     * give line which has already been set, get closest point on line
     * @param tp
     * @return
     */
    public ReadOnlyVector3 closestPointTo(ReadOnlyVector3 testPoint) {
        testPoint.subtract(a, A);
        b.subtract(a, B);
        B.normalize(u);
        tS = A.dot(u);
        u.multiply(tS, V);
        if(tS < 0) { // 
            result.set(a);
        }
        else if(tS > len) {
            result.set(b);
        }
        else {
            a.add(V, result);
        }

        return result;
    }
}