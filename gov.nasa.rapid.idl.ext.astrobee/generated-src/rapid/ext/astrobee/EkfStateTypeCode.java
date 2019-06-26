
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  EkfStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[13];

        sm[__i]=new  ValueMember("pose", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Transform3DTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("velocity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("omega", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("gyro_bias", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("accel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("accel_bias", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("cov_diag", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.FloatSequence15TypeCode.VALUE,7 , false);__i++;
        sm[__i]=new  ValueMember("confidence", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,8 , false);__i++;
        sm[__i]=new  ValueMember("status", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,9 , false);__i++;
        sm[__i]=new  ValueMember("of_count", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,10 , false);__i++;
        sm[__i]=new  ValueMember("ml_count", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,11 , false);__i++;
        sm[__i]=new  ValueMember("hr_global_pose", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Transform3DTypeCode.VALUE,12 , false);__i++;
        sm[__i]=new  ValueMember("ml_mahal_dists", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.FloatSequence50TypeCode.VALUE,13 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::astrobee::EkfState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

