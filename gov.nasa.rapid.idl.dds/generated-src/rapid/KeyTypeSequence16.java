

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

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
/** Arguments for commands will be specified with these types. */
/** KeyTypePair defines a key with data type only. */

public class KeyTypeSequence16   implements Copyable, Serializable{

    public rapid.KeyTypePairSeq userData =  new rapid.KeyTypePairSeq(16);

    public KeyTypeSequence16() {

    }
    public KeyTypeSequence16 (KeyTypeSequence16 other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        KeyTypeSequence16 self;
        self = new  KeyTypeSequence16();
        self.clear();
        return self;

    }

    public void clear() {

        if (userData != null) {
            userData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        KeyTypeSequence16 otherObj = (KeyTypeSequence16)o;

        if(!userData.equals(otherObj.userData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += userData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>KeyTypeSequence16TypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        KeyTypeSequence16 typedSrc = (KeyTypeSequence16) src;
        KeyTypeSequence16 typedDst = this;

        typedDst.userData.copy_from(typedSrc.userData);

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("userData:\n");
        for(int i__ = 0; i__ < userData.size(); ++i__) {
            strBuffer.append(((rapid.KeyTypePair)userData.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
