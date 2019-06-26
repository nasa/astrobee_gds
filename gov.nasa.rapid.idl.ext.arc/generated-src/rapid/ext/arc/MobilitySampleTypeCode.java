
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  MobilitySampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[7];

        sm[__i]=new  ValueMember("goalName", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("commandFrame", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String128TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("xyt", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("xytTolerance", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("hintedSpeed", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  ValueMember("navAlgo", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("keyedValues", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.KeyTypeValueSequence8TypeCode.VALUE,7 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::MobilitySample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

