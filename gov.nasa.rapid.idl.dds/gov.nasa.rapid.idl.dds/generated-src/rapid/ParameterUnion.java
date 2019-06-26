

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

import com.rti.dds.util.Union;

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

public class ParameterUnion  extends Union implements Copyable, Serializable{

    private static final rapid.DataType _default = getDefaultDiscriminator();

    public rapid.DataType _d  = rapid.DataType.create();

    public boolean b= false;
    public double d= 0;
    public float f= 0;
    public int i= 0;
    public long ll= 0;
    public String s=  "" ; /* maximum length = (128) */
    public rapid.Vec3d vec3d = (rapid.Vec3d)rapid.Vec3d.create();
    public rapid.Mat33f mat33f = (rapid.Mat33f)rapid.Mat33f.create();

    public ParameterUnion() {

        _d = getDefaultDiscriminator();
    }
    public ParameterUnion (ParameterUnion other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ParameterUnion self;
        self = new  ParameterUnion();
        self.clear();
        return self;

    }

    public void clear() {

        _d = getDefaultDiscriminator();
        if (_d == ((rapid.DataType.RAPID_BOOL))){
            b= false;
        } else if (_d == ((rapid.DataType.RAPID_DOUBLE))){
            d= 0;
        } else if (_d == ((rapid.DataType.RAPID_FLOAT))){
            f= 0;
        } else if (_d == ((rapid.DataType.RAPID_INT))){
            i= 0;
        } else if (_d == ((rapid.DataType.RAPID_LONGLONG))){
            ll= 0;
        } else if (_d == ((rapid.DataType.RAPID_STRING))){
            s=  ""; 
        } else if (_d == ((rapid.DataType.RAPID_VEC3d))){
            if (vec3d != null) {
                vec3d.clear();
            }
        } else if (_d == ((rapid.DataType.RAPID_MAT33f))){
            if (mat33f != null) {
                mat33f.clear();
            }
        }
    }

    public static rapid.DataType getDefaultDiscriminator() {
        return rapid.DataType.valueOf(0);
    }    
    public rapid.DataType discriminator() {
        return _d;
    }

    private void verifyb(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_BOOL)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public  boolean  b() {
        verifyb(_d);
        return b;
    }

    public void b( boolean  __value) {

        _d = (rapid.DataType.RAPID_BOOL);
        b = __value;
    }

    private void verifyd(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_DOUBLE)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public  double  d() {
        verifyd(_d);
        return d;
    }

    public void d( double  __value) {

        _d = (rapid.DataType.RAPID_DOUBLE);
        d = __value;
    }

    private void verifyf(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_FLOAT)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public  float  f() {
        verifyf(_d);
        return f;
    }

    public void f( float  __value) {

        _d = (rapid.DataType.RAPID_FLOAT);
        f = __value;
    }

    private void verifyi(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_INT)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public  int  i() {
        verifyi(_d);
        return i;
    }

    public void i( int  __value) {

        _d = (rapid.DataType.RAPID_INT);
        i = __value;
    }

    private void verifyll(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_LONGLONG)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public  long  ll() {
        verifyll(_d);
        return ll;
    }

    public void ll( long  __value) {

        _d = (rapid.DataType.RAPID_LONGLONG);
        ll = __value;
    }

    private void verifys(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_STRING)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public  String  s() {
        verifys(_d);
        return s;
    }

    public void s( String  __value) {

        _d = (rapid.DataType.RAPID_STRING);
        s = __value;
    }

    private void verifyvec3d(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_VEC3d)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public rapid.Vec3d   vec3d() {
        verifyvec3d(_d);
        return vec3d;
    }

    public void vec3d(rapid.Vec3d   __value) {

        _d = (rapid.DataType.RAPID_VEC3d);
        vec3d = __value;
    }

    private void verifymat33f(rapid.DataType discriminator) {

        if ( discriminator != (rapid.DataType.RAPID_MAT33f)) {

            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public rapid.Mat33f   mat33f() {
        verifymat33f(_d);
        return mat33f;
    }

    public void mat33f(rapid.Mat33f   __value) {

        _d = (rapid.DataType.RAPID_MAT33f);
        mat33f = __value;
    }

    private void verifyDefault(rapid.DataType discriminator) {
        if (!(discriminator != (rapid.DataType.RAPID_BOOL)  &&
        discriminator != (rapid.DataType.RAPID_DOUBLE)  &&
        discriminator != (rapid.DataType.RAPID_FLOAT)  &&
        discriminator != (rapid.DataType.RAPID_INT)  &&
        discriminator != (rapid.DataType.RAPID_LONGLONG)  &&
        discriminator != (rapid.DataType.RAPID_STRING)  &&
        discriminator != (rapid.DataType.RAPID_VEC3d)  &&
        discriminator != (rapid.DataType.RAPID_MAT33f) )) {
            throw new RETCODE_ILLEGAL_OPERATION();
        }
    }

    public void __default(rapid.DataType discriminator) {
        verifyDefault(discriminator);
        _d = discriminator;
    }

    public void __default() {
        if (_default ==  null) throw new RETCODE_ILLEGAL_OPERATION();
        _d = _default;

    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ParameterUnion otherObj = (ParameterUnion)o;

        if(_d != otherObj._d) {
            return false;
        }
        if (otherObj._d == ((rapid.DataType.RAPID_BOOL))){
            if(b != otherObj.b) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_DOUBLE))){
            if(d != otherObj.d) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_FLOAT))){
            if(f != otherObj.f) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_INT))){
            if(i != otherObj.i) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_LONGLONG))){
            if(ll != otherObj.ll) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_STRING))){
            if(!s.equals(otherObj.s)) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_VEC3d))){
            if(!vec3d.equals(otherObj.vec3d)) {
                return false;
            }
        } else if (otherObj._d == ((rapid.DataType.RAPID_MAT33f))){
            if(!mat33f.equals(otherObj.mat33f)) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        if (_d == ((rapid.DataType.RAPID_BOOL))){
            __result += (b == true)?1:0;
        } else if (_d == ((rapid.DataType.RAPID_DOUBLE))){
            __result += (int)d;
        } else if (_d == ((rapid.DataType.RAPID_FLOAT))){
            __result += (int)f;
        } else if (_d == ((rapid.DataType.RAPID_INT))){
            __result += (int)i;
        } else if (_d == ((rapid.DataType.RAPID_LONGLONG))){
            __result += (int)ll;
        } else if (_d == ((rapid.DataType.RAPID_STRING))){
            __result += s.hashCode(); 
        } else if (_d == ((rapid.DataType.RAPID_VEC3d))){
            __result += vec3d.hashCode(); 
        } else if (_d == ((rapid.DataType.RAPID_MAT33f))){
            __result += mat33f.hashCode(); 
        }
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ParameterUnionTypeSupport</code>
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
    public Object copy_from(Object other) {

        ParameterUnion typedSrc = (ParameterUnion) other;
        ParameterUnion typedDst = this;

        typedDst._d = (rapid.DataType) typedDst._d.copy_from(typedSrc._d);

        if (_d == ((rapid.DataType.RAPID_BOOL))){
            typedDst.b = typedSrc.b;
        } else if (_d == ((rapid.DataType.RAPID_DOUBLE))){
            typedDst.d = typedSrc.d;
        } else if (_d == ((rapid.DataType.RAPID_FLOAT))){
            typedDst.f = typedSrc.f;
        } else if (_d == ((rapid.DataType.RAPID_INT))){
            typedDst.i = typedSrc.i;
        } else if (_d == ((rapid.DataType.RAPID_LONGLONG))){
            typedDst.ll = typedSrc.ll;
        } else if (_d == ((rapid.DataType.RAPID_STRING))){
            typedDst.s = typedSrc.s;
        } else if (_d == ((rapid.DataType.RAPID_VEC3d))){
            typedDst.vec3d = (rapid.Vec3d) typedDst.vec3d.copy_from(typedSrc.vec3d);
        } else if (_d == ((rapid.DataType.RAPID_MAT33f))){
            typedDst.mat33f = (rapid.Mat33f) typedDst.mat33f.copy_from(typedSrc.mat33f);
        }

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

        strBuffer.append(_d.toString("_d ", indent+1));
        if (_d == ((rapid.DataType.RAPID_BOOL))){
            CdrHelper.printIndent(strBuffer, indent+1);        
            strBuffer.append("b: ").append(b).append("\n");  
        } else if (_d == ((rapid.DataType.RAPID_DOUBLE))){
            CdrHelper.printIndent(strBuffer, indent+1);        
            strBuffer.append("d: ").append(d).append("\n");  
        } else if (_d == ((rapid.DataType.RAPID_FLOAT))){
            CdrHelper.printIndent(strBuffer, indent+1);        
            strBuffer.append("f: ").append(f).append("\n");  
        } else if (_d == ((rapid.DataType.RAPID_INT))){
            CdrHelper.printIndent(strBuffer, indent+1);        
            strBuffer.append("i: ").append(i).append("\n");  
        } else if (_d == ((rapid.DataType.RAPID_LONGLONG))){
            CdrHelper.printIndent(strBuffer, indent+1);        
            strBuffer.append("ll: ").append(ll).append("\n");  
        } else if (_d == ((rapid.DataType.RAPID_STRING))){
            CdrHelper.printIndent(strBuffer, indent+1);        
            strBuffer.append("s: ").append(s).append("\n");  
        } else if (_d == ((rapid.DataType.RAPID_VEC3d))){
            strBuffer.append(vec3d.toString("vec3d ", indent+1));
        } else if (_d == ((rapid.DataType.RAPID_MAT33f))){
            strBuffer.append(mat33f.toString("mat33f ", indent+1));
        }

        return strBuffer.toString();
    }

}
