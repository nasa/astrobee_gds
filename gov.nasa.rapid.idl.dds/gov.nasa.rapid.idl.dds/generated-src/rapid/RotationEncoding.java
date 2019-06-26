

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

/** A 3x3 rotation matrix in order: R11, R12, R13, R21, R22, R23, R31, R32, R33. */
/** RGBA color */
/** RGB color */
/**
* RotationEncoding specifies alternate rotation encodings for a Mat33f.
* The default interpretation of Mat33f is a 3x3 rotation matrix, and that is
* the preferred representation for rotations in RAPID. However, alternate
* encodings are available for simplicity, compactness, or to indicate
* whether the rotation matrix is invalid (i.e. RAPID_ROT_NONE).
* <ul>
*   <li>RAPID_ROT_NONE: matrix is invalid or uninitialized
*   <li>RAPID_ROT_M33: A 3x3 rotation matrix in order: R11, R12, R13, R21, R22, R23, R31, R32, R33
*   <li>RAPID_ROT_QUAT: quaternion, first 4 elements of Mat33 are X, Y, Z, W
*   <li>RAPID_ROT_XYZ: euler angles, first 3 elements of Mat33 are a rotation around X in radians, followed by rotation around Y, followed by rotation around Z
*   <li>RAPID_ROT_ZYX: euler angles, first 3 elements of Mat33 are a rotation around Z in radians, followed by rotation around Y, followed by rotation around X
*   <li>RAPID_ROT_ZYZ: euler angles, first 3 elements of Mat33 are a rotation around Z in radians, followed by rotation around Y, followed by rotation around Z
*   <li>RAPID_ROT_VEL: angular velocity, first 3 elements of Mat33 is the instantaneous axis of rotation
* </ul>
*/

public class RotationEncoding  extends Enum {

    public static final RotationEncoding RAPID_ROT_NONE = new RotationEncoding("RAPID_ROT_NONE", 0);
    public static final int _RAPID_ROT_NONE = 0;
    public static final RotationEncoding RAPID_ROT_M33 = new RotationEncoding("RAPID_ROT_M33", 1);
    public static final int _RAPID_ROT_M33 = 1;
    public static final RotationEncoding RAPID_ROT_QUAT = new RotationEncoding("RAPID_ROT_QUAT", 2);
    public static final int _RAPID_ROT_QUAT = 2;
    public static final RotationEncoding RAPID_ROT_XYZ = new RotationEncoding("RAPID_ROT_XYZ", 3);
    public static final int _RAPID_ROT_XYZ = 3;
    public static final RotationEncoding RAPID_ROT_ZYX = new RotationEncoding("RAPID_ROT_ZYX", 4);
    public static final int _RAPID_ROT_ZYX = 4;
    public static final RotationEncoding RAPID_ROT_ZYZ = new RotationEncoding("RAPID_ROT_ZYZ", 5);
    public static final int _RAPID_ROT_ZYZ = 5;
    public static final RotationEncoding RAPID_ROT_VEL = new RotationEncoding("RAPID_ROT_VEL", 6);
    public static final int _RAPID_ROT_VEL = 6;
    public static RotationEncoding valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return RotationEncoding.RAPID_ROT_NONE;
            case 1: return RotationEncoding.RAPID_ROT_M33;
            case 2: return RotationEncoding.RAPID_ROT_QUAT;
            case 3: return RotationEncoding.RAPID_ROT_XYZ;
            case 4: return RotationEncoding.RAPID_ROT_ZYX;
            case 5: return RotationEncoding.RAPID_ROT_ZYZ;
            case 6: return RotationEncoding.RAPID_ROT_VEL;

        }
        return null;
    }

    public static RotationEncoding from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[7];

        values[i] = RAPID_ROT_NONE.ordinal();
        i++;
        values[i] = RAPID_ROT_M33.ordinal();
        i++;
        values[i] = RAPID_ROT_QUAT.ordinal();
        i++;
        values[i] = RAPID_ROT_XYZ.ordinal();
        i++;
        values[i] = RAPID_ROT_ZYX.ordinal();
        i++;
        values[i] = RAPID_ROT_ZYZ.ordinal();
        i++;
        values[i] = RAPID_ROT_VEL.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static RotationEncoding create() {

        return valueOf(0);
    }

    /**
    * Print Method
    */     
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();

        CdrHelper.printIndent(strBuffer, indent);

        if (desc != null) {
            strBuffer.append(desc).append(": ");
        }

        strBuffer.append(this);
        strBuffer.append("\n");              
        return strBuffer.toString();
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(ordinal());
    }

    private RotationEncoding(String name, int ordinal) {
        super(name, ordinal);
    }
}

