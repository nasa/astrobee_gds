
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

/**
* ProcessConfig
*/
/**
* ProcessManagerConfig
*/

public class  ProcessManagerConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[2];

        sm[__i]=new  ValueMember("configName", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String64TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("processes", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.ProcessConfigSequenceTypeCode.VALUE,2 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::ProcessManagerConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

