

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
/**
* DataType is used in KeyTypeValue triples to describe the type of data contained in the string value.
* <ul>
*   <li>RAPID_BOOL:
*   <li>RAPID_DOUBLE:
*   <li>RAPID_FLOAT:
*   <li>RAPID_INT:
*   <li>RAPID_LONGLONG:
*   <li>RAPID_STRING:
*   <li>RAPID_VEC3d:
*   <li>RAPID_MAT33f:
* </ul>
*/

public class DataType  extends Enum {

    public static final DataType RAPID_BOOL = new DataType("RAPID_BOOL", 0);
    public static final int _RAPID_BOOL = 0;
    public static final DataType RAPID_DOUBLE = new DataType("RAPID_DOUBLE", 1);
    public static final int _RAPID_DOUBLE = 1;
    public static final DataType RAPID_FLOAT = new DataType("RAPID_FLOAT", 2);
    public static final int _RAPID_FLOAT = 2;
    public static final DataType RAPID_INT = new DataType("RAPID_INT", 3);
    public static final int _RAPID_INT = 3;
    public static final DataType RAPID_LONGLONG = new DataType("RAPID_LONGLONG", 4);
    public static final int _RAPID_LONGLONG = 4;
    public static final DataType RAPID_STRING = new DataType("RAPID_STRING", 5);
    public static final int _RAPID_STRING = 5;
    public static final DataType RAPID_VEC3d = new DataType("RAPID_VEC3d", 6);
    public static final int _RAPID_VEC3d = 6;
    public static final DataType RAPID_MAT33f = new DataType("RAPID_MAT33f", 7);
    public static final int _RAPID_MAT33f = 7;
    public static DataType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return DataType.RAPID_BOOL;
            case 1: return DataType.RAPID_DOUBLE;
            case 2: return DataType.RAPID_FLOAT;
            case 3: return DataType.RAPID_INT;
            case 4: return DataType.RAPID_LONGLONG;
            case 5: return DataType.RAPID_STRING;
            case 6: return DataType.RAPID_VEC3d;
            case 7: return DataType.RAPID_MAT33f;

        }
        return null;
    }

    public static DataType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[8];

        values[i] = RAPID_BOOL.ordinal();
        i++;
        values[i] = RAPID_DOUBLE.ordinal();
        i++;
        values[i] = RAPID_FLOAT.ordinal();
        i++;
        values[i] = RAPID_INT.ordinal();
        i++;
        values[i] = RAPID_LONGLONG.ordinal();
        i++;
        values[i] = RAPID_STRING.ordinal();
        i++;
        values[i] = RAPID_VEC3d.ordinal();
        i++;
        values[i] = RAPID_MAT33f.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static DataType create() {

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

    private DataType(String name, int ordinal) {
        super(name, ordinal);
    }
}

