
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  GncFamCmdStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[10];

        sm[__i]=new  ValueMember("wrench", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.WrenchTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("accel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("alpha", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("status", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,4 , false);__i++;
        sm[__i]=new  ValueMember("position_error", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("position_error_integrated", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("attitude_error", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,7 , false);__i++;
        sm[__i]=new  ValueMember("attitude_error_integrated", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,8 , false);__i++;
        sm[__i]=new  ValueMember("attitude_error_mag", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,9 , false);__i++;
        sm[__i]=new  ValueMember("control_mode", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,10 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::astrobee::GncFamCmdState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

