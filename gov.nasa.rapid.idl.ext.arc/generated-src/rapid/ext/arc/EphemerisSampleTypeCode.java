
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  EphemerisSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[5];

        sm[__i]=new  ValueMember("configIdx", false, (short)-1, true,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,1 , false);__i++;
        sm[__i]=new  ValueMember("solutionTime", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONGLONG,2 , false);__i++;
        sm[__i]=new  ValueMember("lat", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_DOUBLE,3 , false);__i++;
        sm[__i]=new  ValueMember("lon", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_DOUBLE,4 , false);__i++;
        sm[__i]=new  ValueMember("vec", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Vec3fTypeCode.VALUE,5 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::EphemerisSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

