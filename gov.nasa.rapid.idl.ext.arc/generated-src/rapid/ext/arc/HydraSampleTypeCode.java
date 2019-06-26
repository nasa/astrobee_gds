
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  HydraSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[8];

        sm[__i]=new  ValueMember("soh", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_USHORT,1 , false);__i++;
        sm[__i]=new  ValueMember("sns", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_USHORT,2 , false);__i++;
        sm[__i]=new  ValueMember("cds", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_USHORT,3 , false);__i++;
        sm[__i]=new  ValueMember("cmr", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_USHORT,4 , false);__i++;
        sm[__i]=new  ValueMember("sn", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(32, TypeCode.TC_USHORT),5 , false);__i++;
        sm[__i]=new  ValueMember("cd", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(32, TypeCode.TC_USHORT),6 , false);__i++;
        sm[__i]=new  ValueMember("reading", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(89, TypeCode.TC_USHORT),7 , false);__i++;
        sm[__i]=new  ValueMember("values", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ValueSequence32TypeCode.VALUE,8 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::HydraSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

