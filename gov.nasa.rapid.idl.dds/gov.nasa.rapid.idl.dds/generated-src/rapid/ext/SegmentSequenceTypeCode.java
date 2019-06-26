
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

public class  SegmentSequenceTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_alias_tc("rapid::ext::SegmentSequence", new TypeCode(32, rapid.ext.SingleSegmentTypeCode.VALUE),  false);        
        return tc;
    }
}

