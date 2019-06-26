
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  CommandRecordTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[3];

        sm[__i]=new  ValueMember("cmd", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.CommandTypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  ValueMember("trResult", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ResultTypeTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("trStatus", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.StatusTypeTypeCode.VALUE,2 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::CommandRecord",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,TypeCode.TC_NULL, sm);        
        return tc;
    }
}

