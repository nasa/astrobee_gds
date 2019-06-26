
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  CommStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[7];

        sm[__i]=new  ValueMember("wirelessConnected", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,1 , false);__i++;
        sm[__i]=new  ValueMember("apName", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String64TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("bssid", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("rssi", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;
        sm[__i]=new  ValueMember("frequency", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  ValueMember("channel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,6 , false);__i++;
        sm[__i]=new  ValueMember("lanConnected", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,7 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::astrobee::CommState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

