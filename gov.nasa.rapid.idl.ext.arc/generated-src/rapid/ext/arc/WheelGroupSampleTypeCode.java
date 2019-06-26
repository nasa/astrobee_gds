
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  WheelGroupSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[15];

        sm[__i]=new  ValueMember("targetTime", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONGLONG,1 , false);__i++;
        sm[__i]=new  ValueMember("curvature", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  ValueMember("curvatureRate", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  ValueMember("speed", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;
        sm[__i]=new  ValueMember("crabAngle", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  ValueMember("crabRate", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,6 , false);__i++;
        sm[__i]=new  ValueMember("targetCurvature", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,7 , false);__i++;
        sm[__i]=new  ValueMember("targetCurvatureRate", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,8 , false);__i++;
        sm[__i]=new  ValueMember("targetCrabRate", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,9 , false);__i++;
        sm[__i]=new  ValueMember("targetSpeed", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,10 , false);__i++;
        sm[__i]=new  ValueMember("targetCrabAngle", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,11 , false);__i++;
        sm[__i]=new  ValueMember("motors", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.arc.MotorStateSequence32TypeCode.VALUE,12 , false);__i++;
        sm[__i]=new  ValueMember("motorStatus", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.LongSequence32TypeCode.VALUE,13 , false);__i++;
        sm[__i]=new  ValueMember("currents", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence32TypeCode.VALUE,14 , false);__i++;
        sm[__i]=new  ValueMember("temperatures", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence32TypeCode.VALUE,15 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::WheelGroupSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

