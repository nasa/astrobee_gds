
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  ChannelStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[3];

        sm[__i]=new  StructMember("status", false, (short)-1,  false,(TypeCode) rapid.ChannelStatusTypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("queuedDataVolume", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,1 , false);__i++;
        sm[__i]=new  StructMember("queuedFiles", false, (short)-1,  false,(TypeCode) TypeCode.TC_SHORT,2 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ChannelState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

