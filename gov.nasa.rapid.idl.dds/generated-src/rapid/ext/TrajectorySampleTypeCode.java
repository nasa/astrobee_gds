
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
/**
* TrajectorySample is a message that holds the current sequence of geometric trajectory segments that the robot plans to follow.
* The receiver will need to convert this to whatever form they need to identify potential collisions or to render it in a GUI
*/

public class  TrajectorySampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[1];

        sm[__i]=new  ValueMember("geometricSegments", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.SegmentSequenceTypeCode.VALUE,1 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::TrajectorySample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

