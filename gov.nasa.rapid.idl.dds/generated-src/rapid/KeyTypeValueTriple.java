

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
/** KeyTypeValueTriple defines a key, its data type and its value. */

public class KeyTypeValueTriple   implements Copyable, Serializable{

    public String key=  "" ; /* maximum length = (32) */
    /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */
    public int padding= 0;
    public rapid.ParameterUnion value = (rapid.ParameterUnion)rapid.ParameterUnion.create();

    public KeyTypeValueTriple() {

        /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */

    }
    public KeyTypeValueTriple (KeyTypeValueTriple other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        KeyTypeValueTriple self;
        self = new  KeyTypeValueTriple();
        self.clear();
        return self;

    }

    public void clear() {

        key=  ""; 
        /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */
        padding= 0;
        if (value != null) {
            value.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        KeyTypeValueTriple otherObj = (KeyTypeValueTriple)o;

        if(!key.equals(otherObj.key)) {
            return false;
        }
        /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */
        if(padding != otherObj.padding) {
            return false;
        }
        if(!value.equals(otherObj.value)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += key.hashCode(); 
        /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */
        __result += (int)padding;
        __result += value.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>KeyTypeValueTripleTypeSupport</code>
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

        KeyTypeValueTriple typedSrc = (KeyTypeValueTriple) src;
        KeyTypeValueTriple typedDst = this;

        typedDst.key = typedSrc.key;
        /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */
        typedDst.padding = typedSrc.padding;
        typedDst.value = (rapid.ParameterUnion) typedDst.value.copy_from(typedSrc.value);

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
        strBuffer.append("key: ").append(key).append("\n");  
        /** Place-holder to keep binary layout of the struct backward compatible. Data type should be set in ParameterUnion */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("padding: ").append(padding).append("\n");  
        strBuffer.append(value.toString("value ", indent+1));

        return strBuffer.toString();
    }

}
