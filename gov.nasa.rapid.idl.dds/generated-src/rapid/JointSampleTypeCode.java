
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  JointSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[8];

        sm[__i]=new  ValueMember("anglePos", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence64TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("angleVel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence64TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("angleAcc", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence64TypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("current", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence64TypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("torque", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence64TypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("temperature", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FloatSequence64TypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("status", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.LongSequence64TypeCode.VALUE,7 , false);__i++;
        sm[__i]=new  ValueMember("auxFloat", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.NFSeqSequence16TypeCode.VALUE,8 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::JointSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

