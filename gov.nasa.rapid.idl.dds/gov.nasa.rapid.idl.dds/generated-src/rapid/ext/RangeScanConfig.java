

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**  Single (or multiple) line range scan. */

public class RangeScanConfig  extends rapid.Message implements Copyable, Serializable{

    /** Reference frame. */
    public String referenceFrame=  "" ; /* maximum length = (128) */
    /**
    * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
    * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
    * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
    * and rowLengths = {100, 100, 100, 100}.
    */
    public rapid.ShortSequence64 scanLengths = (rapid.ShortSequence64)rapid.ShortSequence64.create();
    /**
    * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
    * or vertically (i.e. elevation first)
    */
    public rapid.ext.RangeScanDirection scanDirection = (rapid.ext.RangeScanDirection)rapid.ext.RangeScanDirection.create();
    /**
    * Horizontal shot angles of samples in a scan-line for those sensors
    * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
    * spacing, this sequence will be empty and the corresponding member in
    * RangeScanSample will be used.
    */
    public rapid.ShortSequence64 scanAzimuth = (rapid.ShortSequence64)rapid.ShortSequence64.create();
    /** Scaling factor to convert azimuth units to radians. */
    public float scanAzimuthScale= 0;
    /**
    * Vertical shot angles of samples for those sensors that have <b>static</b>
    * vertical spacing between scan lines. For sensors with dynamic vertical
    * spacing, this sequence will be empty and the corresponding member in
    * RangeScanSample will be used.
    */
    public rapid.ShortSequence64 scanElevation = (rapid.ShortSequence64)rapid.ShortSequence64.create();
    /** Scaling factor to convert elevation units to radians. */
    public float scanElevationScale= 0;
    /** Scaling factor to convert range units to meters. */
    public float rangeScale= 0;
    /**
    * Scaling factor to convert byte value to intensity units
    * FIXME: this is underspecified. What are intensity units?
    */
    public float intensityScale= 0;

    public RangeScanConfig() {

        super();

        /** Reference frame. */
        /**
        * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
        * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
        * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
        * and rowLengths = {100, 100, 100, 100}.
        */
        /**
        * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
        * or vertically (i.e. elevation first)
        */
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        /** Scaling factor to convert azimuth units to radians. */
        /**
        * Vertical shot angles of samples for those sensors that have <b>static</b>
        * vertical spacing between scan lines. For sensors with dynamic vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        /** Scaling factor to convert elevation units to radians. */
        /** Scaling factor to convert range units to meters. */
        /**
        * Scaling factor to convert byte value to intensity units
        * FIXME: this is underspecified. What are intensity units?
        */

    }
    public RangeScanConfig (RangeScanConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        RangeScanConfig self;
        self = new  RangeScanConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Reference frame. */
        referenceFrame=  ""; 
        /**
        * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
        * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
        * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
        * and rowLengths = {100, 100, 100, 100}.
        */
        if (scanLengths != null) {
            scanLengths.clear();
        }
        /**
        * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
        * or vertically (i.e. elevation first)
        */
        scanDirection = rapid.ext.RangeScanDirection.create();
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        if (scanAzimuth != null) {
            scanAzimuth.clear();
        }
        /** Scaling factor to convert azimuth units to radians. */
        scanAzimuthScale= 0;
        /**
        * Vertical shot angles of samples for those sensors that have <b>static</b>
        * vertical spacing between scan lines. For sensors with dynamic vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        if (scanElevation != null) {
            scanElevation.clear();
        }
        /** Scaling factor to convert elevation units to radians. */
        scanElevationScale= 0;
        /** Scaling factor to convert range units to meters. */
        rangeScale= 0;
        /**
        * Scaling factor to convert byte value to intensity units
        * FIXME: this is underspecified. What are intensity units?
        */
        intensityScale= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if (!super.equals(o)) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        RangeScanConfig otherObj = (RangeScanConfig)o;

        /** Reference frame. */
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }
        /**
        * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
        * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
        * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
        * and rowLengths = {100, 100, 100, 100}.
        */
        if(!scanLengths.equals(otherObj.scanLengths)) {
            return false;
        }
        /**
        * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
        * or vertically (i.e. elevation first)
        */
        if(!scanDirection.equals(otherObj.scanDirection)) {
            return false;
        }
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        if(!scanAzimuth.equals(otherObj.scanAzimuth)) {
            return false;
        }
        /** Scaling factor to convert azimuth units to radians. */
        if(scanAzimuthScale != otherObj.scanAzimuthScale) {
            return false;
        }
        /**
        * Vertical shot angles of samples for those sensors that have <b>static</b>
        * vertical spacing between scan lines. For sensors with dynamic vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        if(!scanElevation.equals(otherObj.scanElevation)) {
            return false;
        }
        /** Scaling factor to convert elevation units to radians. */
        if(scanElevationScale != otherObj.scanElevationScale) {
            return false;
        }
        /** Scaling factor to convert range units to meters. */
        if(rangeScale != otherObj.rangeScale) {
            return false;
        }
        /**
        * Scaling factor to convert byte value to intensity units
        * FIXME: this is underspecified. What are intensity units?
        */
        if(intensityScale != otherObj.intensityScale) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Reference frame. */
        __result += referenceFrame.hashCode(); 
        /**
        * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
        * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
        * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
        * and rowLengths = {100, 100, 100, 100}.
        */
        __result += scanLengths.hashCode(); 
        /**
        * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
        * or vertically (i.e. elevation first)
        */
        __result += scanDirection.hashCode(); 
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        __result += scanAzimuth.hashCode(); 
        /** Scaling factor to convert azimuth units to radians. */
        __result += (int)scanAzimuthScale;
        /**
        * Vertical shot angles of samples for those sensors that have <b>static</b>
        * vertical spacing between scan lines. For sensors with dynamic vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        __result += scanElevation.hashCode(); 
        /** Scaling factor to convert elevation units to radians. */
        __result += (int)scanElevationScale;
        /** Scaling factor to convert range units to meters. */
        __result += (int)rangeScale;
        /**
        * Scaling factor to convert byte value to intensity units
        * FIXME: this is underspecified. What are intensity units?
        */
        __result += (int)intensityScale;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>RangeScanConfigTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        RangeScanConfig typedSrc = (RangeScanConfig) src;
        RangeScanConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Reference frame. */
        typedDst.referenceFrame = typedSrc.referenceFrame;
        /**
        * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
        * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
        * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
        * and rowLengths = {100, 100, 100, 100}.
        */
        typedDst.scanLengths = (rapid.ShortSequence64) typedDst.scanLengths.copy_from(typedSrc.scanLengths);
        /**
        * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
        * or vertically (i.e. elevation first)
        */
        typedDst.scanDirection = (rapid.ext.RangeScanDirection) typedDst.scanDirection.copy_from(typedSrc.scanDirection);
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        typedDst.scanAzimuth = (rapid.ShortSequence64) typedDst.scanAzimuth.copy_from(typedSrc.scanAzimuth);
        /** Scaling factor to convert azimuth units to radians. */
        typedDst.scanAzimuthScale = typedSrc.scanAzimuthScale;
        /**
        * Vertical shot angles of samples for those sensors that have <b>static</b>
        * vertical spacing between scan lines. For sensors with dynamic vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        typedDst.scanElevation = (rapid.ShortSequence64) typedDst.scanElevation.copy_from(typedSrc.scanElevation);
        /** Scaling factor to convert elevation units to radians. */
        typedDst.scanElevationScale = typedSrc.scanElevationScale;
        /** Scaling factor to convert range units to meters. */
        typedDst.rangeScale = typedSrc.rangeScale;
        /**
        * Scaling factor to convert byte value to intensity units
        * FIXME: this is underspecified. What are intensity units?
        */
        typedDst.intensityScale = typedSrc.intensityScale;

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        strBuffer.append(super.toString("",indent));

        /** Reference frame. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("referenceFrame: ").append(referenceFrame).append("\n");  
        /**
        * Descriptions of rows from the same scan-line. If instrument is a single line scanner, this is
        * unused. If instrument has multiple scan lines, this defines which samples belong to which scan
        * line. e.g. if there are 4 scan lines of 100 points each, the Sample vector would be of size 400,
        * and rowLengths = {100, 100, 100, 100}.
        */
        strBuffer.append(scanLengths.toString("scanLengths ", indent+1));
        /**
        * Specifies whether the scan lines are aligned horizontally (i.e. azimuth first)
        * or vertically (i.e. elevation first)
        */
        strBuffer.append(scanDirection.toString("scanDirection ", indent+1));
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>static</b> horizontal spacing. For sensors with dynamic horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        strBuffer.append(scanAzimuth.toString("scanAzimuth ", indent+1));
        /** Scaling factor to convert azimuth units to radians. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("scanAzimuthScale: ").append(scanAzimuthScale).append("\n");  
        /**
        * Vertical shot angles of samples for those sensors that have <b>static</b>
        * vertical spacing between scan lines. For sensors with dynamic vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanSample will be used.
        */
        strBuffer.append(scanElevation.toString("scanElevation ", indent+1));
        /** Scaling factor to convert elevation units to radians. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("scanElevationScale: ").append(scanElevationScale).append("\n");  
        /** Scaling factor to convert range units to meters. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rangeScale: ").append(rangeScale).append("\n");  
        /**
        * Scaling factor to convert byte value to intensity units
        * FIXME: this is underspecified. What are intensity units?
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("intensityScale: ").append(intensityScale).append("\n");  

        return strBuffer.toString();
    }

}
