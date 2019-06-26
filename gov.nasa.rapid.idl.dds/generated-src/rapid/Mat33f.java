

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

public class Mat33f   implements Copyable, Serializable{

    public float [] userData=  new float [9];

    public Mat33f() {

    }
    public Mat33f (Mat33f other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Mat33f self;
        self = new  Mat33f();
        self.clear();
        return self;

    }

    public void clear() {

        for(int i1__ = 0; i1__< 9; ++i1__){

            userData[i1__] =  0;
        }

    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Mat33f otherObj = (Mat33f)o;

        for(int i1__ = 0; i1__< 9; ++i1__){

            if(userData[i1__] != otherObj.userData[i1__]) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        for(int i1__ = 0; i1__< 9; ++i1__){

            __result += (int)userData[i1__];
        }

        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>Mat33fTypeSupport</code>
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

        Mat33f typedSrc = (Mat33f) src;
        Mat33f typedDst = this;

        System.arraycopy(typedSrc.userData,0,
        typedDst.userData,0,
        typedSrc.userData.length); 

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
        strBuffer.append("userData: ");
        for(int i1__ = 0; i1__< 9; ++i1__){

            strBuffer.append(userData[i1__]).append(", ");
        }

        strBuffer.append("\n");

        return strBuffer.toString();
    }

}
