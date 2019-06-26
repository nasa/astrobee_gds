
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  NavMapConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[6];

        sm[__i]=new  ValueMember("referenceFrame", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String128TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("offset", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(new int[] {2}, TypeCode.TC_FLOAT),2 , false);__i++;
        sm[__i]=new  ValueMember("cellSize", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(new int[] {2}, TypeCode.TC_FLOAT),3 , false);__i++;
        sm[__i]=new  ValueMember("numCells", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(new int[] {2}, TypeCode.TC_SHORT),4 , false);__i++;
        sm[__i]=new  ValueMember("shortLayerNames", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.ShortMapLayerNameSequenceTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("octetLayerNames", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.OctetMapLayerNameSequenceTypeCode.VALUE,6 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::NavMapConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

