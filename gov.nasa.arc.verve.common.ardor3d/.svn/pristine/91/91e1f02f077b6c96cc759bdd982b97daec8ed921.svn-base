package gov.nasa.arc.verve.common.ardor3d.util;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;

/**
 * Find the closest point on a line given a test point
 * Adapted from a compact solution found here: http://www.ogre3d.org/tikiwiki/Nearest+point+on+a+line
 * with the exception of clamping to endpoints which can be 
 * efficiently done using tS
 */
public class PointOnLine2D {
    private final Vector2 a = new Vector2(); // point 1 of line
    private final Vector2 b = new Vector2(); // point 1 of line
    private final Vector2 A = new Vector2(); // vector from a to test point
    private final Vector2 B = new Vector2(); // vector from a to b
    private final Vector2 u = new Vector2(); // normalized vector from a to b 
    private final Vector2 V = new Vector2(); // vector along line to give point
    private double tS;
    private double len;
    private final Vector2 result = new Vector2();
        
    public void setLine(ReadOnlyVector2 begin, ReadOnlyVector2 end) {
        a.set(begin);
        b.set(end);
        len = a.distance(b);
    }

    /**
     * give line which has already been set, get closest point on line
     * @param tp
     * @return
     */
    public ReadOnlyVector2 closestPointTo(ReadOnlyVector2 testPoint) {
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