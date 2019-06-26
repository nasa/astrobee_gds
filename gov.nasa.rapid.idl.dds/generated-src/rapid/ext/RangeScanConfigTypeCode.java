
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  RangeScanConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[9];

        sm[__i]=new  ValueMember("referenceFrame", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String128TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("scanLengths", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ShortSequence64TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("scanDirection", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.RangeScanDirectionTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("scanAzimuth", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ShortSequence64TypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("scanAzimuthScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  ValueMember("scanElevation", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ShortSequence64TypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("scanElevationScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,7 , false);__i++;
        sm[__i]=new  ValueMember("rangeScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,8 , false);__i++;
        sm[__i]=new  ValueMember("intensityScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,9 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::RangeScanConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

