
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  GpsSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[9];

        sm[__i]=new  ValueMember("xyz", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("sigmaXyz", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3dTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("utmZone", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_OCTET,3 , false);__i++;
        sm[__i]=new  ValueMember("utmDesig", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_CHAR,4 , false);__i++;
        sm[__i]=new  ValueMember("mode", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_OCTET,5 , false);__i++;
        sm[__i]=new  ValueMember("numSats", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_OCTET,6 , false);__i++;
        sm[__i]=new  ValueMember("diffAge", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONGLONG,7 , false);__i++;
        sm[__i]=new  ValueMember("solAge", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONGLONG,8 , false);__i++;
        sm[__i]=new  ValueMember("undulation", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,9 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::GpsSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

