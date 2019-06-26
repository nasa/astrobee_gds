
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  MessageTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[1];

        sm[__i]=new  ValueMember("hdr", false, (short)-1, true,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.HeaderTypeCode.VALUE,0 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::Message",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,TypeCode.TC_NULL, sm);        
        return tc;
    }
}

