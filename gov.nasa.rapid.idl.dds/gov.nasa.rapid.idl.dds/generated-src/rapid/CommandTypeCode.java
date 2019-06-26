
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  CommandTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[7];

        sm[__i]=new  ValueMember("cmdName", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String64TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("cmdId", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String64TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("cmdSrc", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("subsysName", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("arguments", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ParameterSequence16TypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("cmdAction", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.QueueActionTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("targetCmdId", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String64TypeCode.VALUE,7 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::Command",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

