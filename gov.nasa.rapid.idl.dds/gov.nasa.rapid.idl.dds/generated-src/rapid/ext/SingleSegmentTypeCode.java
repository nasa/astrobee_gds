
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
* SegmentType describes the type of segment that is being represented.
*/
/**
* SingleSegment holds the info for an "atomic" segment.
*
*/

public class  SingleSegmentTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[7];

        sm[__i]=new  ValueMember("type", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.SegmentTypeTypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  ValueMember("distance", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,1 , false);__i++;
        sm[__i]=new  ValueMember("length", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  ValueMember("angleOffset", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  ValueMember("start", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Transform3DTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("end", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Transform3DTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("velocity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,6 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::SingleSegment",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,TypeCode.TC_NULL, sm);        
        return tc;
    }
}

