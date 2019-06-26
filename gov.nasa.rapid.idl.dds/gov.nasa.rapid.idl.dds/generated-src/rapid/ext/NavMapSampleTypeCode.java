
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  NavMapSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[4];

        sm[__i]=new  ValueMember("tileId", false, (short)-1, true,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(new int[] {2}, TypeCode.TC_LONG),1 , false);__i++;
        sm[__i]=new  ValueMember("location", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Transform3DTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("shortLayers", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.ShortMapLayerSequenceTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("octetLayers", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.OctetMapLayerSequenceTypeCode.VALUE,4 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::NavMapSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

