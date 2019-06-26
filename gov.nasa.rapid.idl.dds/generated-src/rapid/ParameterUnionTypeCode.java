
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  ParameterUnionTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        UnionMember um[]=new UnionMember[8];

        um[__i]=new  UnionMember("b", false, new int[] { (rapid.DataType.RAPID_BOOL).ordinal()}, (TypeCode) TypeCode.TC_BOOLEAN,1 );__i++;
        um[__i]=new  UnionMember("d", false, new int[] { (rapid.DataType.RAPID_DOUBLE).ordinal()}, (TypeCode) TypeCode.TC_DOUBLE,2 );__i++;
        um[__i]=new  UnionMember("f", false, new int[] { (rapid.DataType.RAPID_FLOAT).ordinal()}, (TypeCode) TypeCode.TC_FLOAT,3 );__i++;
        um[__i]=new  UnionMember("i", false, new int[] { (rapid.DataType.RAPID_INT).ordinal()}, (TypeCode) TypeCode.TC_LONG,4 );__i++;
        um[__i]=new  UnionMember("ll", false, new int[] { (rapid.DataType.RAPID_LONGLONG).ordinal()}, (TypeCode) TypeCode.TC_LONGLONG,5 );__i++;
        um[__i]=new  UnionMember("s", false, new int[] { (rapid.DataType.RAPID_STRING).ordinal()}, (TypeCode) rapid.String128TypeCode.VALUE,6 );__i++;
        um[__i]=new  UnionMember("vec3d", false, new int[] { (rapid.DataType.RAPID_VEC3d).ordinal()}, (TypeCode) rapid.Vec3dTypeCode.VALUE,7 );__i++;
        um[__i]=new  UnionMember("mat33f", false, new int[] { (rapid.DataType.RAPID_MAT33f).ordinal()}, (TypeCode) rapid.Mat33fTypeCode.VALUE,8 );__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_union_tc("rapid::ParameterUnion",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, rapid.DataTypeTypeCode.VALUE,-1, um);        
        return tc;
    }
}

