

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

/** Single (or multiple) line range scan. */

public class RangeScanSample  extends rapid.Message implements Copyable, Serializable{

    /**
    * Horizontal shot angles of samples in a scan-line for those sensors
    * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
    * spacing, this sequence will be empty and the corresponding member in
    * RangeScanConfig will be used.
    */
    public rapid.ShortSequence64 scanAzimuth = (rapid.ShortSequence64)rapid.ShortSequence64.create();
    /**
    * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
    * vertical spacing between scan lines. For sensors with static vertical
    * spacing, this sequence will be empty and the corresponding member in
    * RangeScanConfig will be used.
    */
    public rapid.ShortSequence64 scanElevation = (rapid.ShortSequence64)rapid.ShortSequence64.create();
    /** Vector of 16 bit data samples. */
    public rapid.ShortSequence2K rangeData = (rapid.ShortSequence2K)rapid.ShortSequence2K.create();
    public rapid.OctetSequence2K intensityData = (rapid.OctetSequence2K)rapid.OctetSequence2K.create();

    public RangeScanSample() {

        super();

        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        /**
        * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
        * vertical spacing between scan lines. For sensors with static vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        /** Vector of 16 bit data samples. */

    }
    public RangeScanSample (RangeScanSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        RangeScanSample self;
        self = new  RangeScanSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        if (scanAzimuth != null) {
            scanAzimuth.clear();
        }
        /**
        * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
        * vertical spacing between scan lines. For sensors with static vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        if (scanElevation != null) {
            scanElevation.clear();
        }
        /** Vector of 16 bit data samples. */
        if (rangeData != null) {
            rangeData.clear();
        }
        if (intensityData != null) {
            intensityData.clear();
        }
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

        RangeScanSample otherObj = (RangeScanSample)o;

        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        if(!scanAzimuth.equals(otherObj.scanAzimuth)) {
            return false;
        }
        /**
        * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
        * vertical spacing between scan lines. For sensors with static vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        if(!scanElevation.equals(otherObj.scanElevation)) {
            return false;
        }
        /** Vector of 16 bit data samples. */
        if(!rangeData.equals(otherObj.rangeData)) {
            return false;
        }
        if(!intensityData.equals(otherObj.intensityData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        __result += scanAzimuth.hashCode(); 
        /**
        * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
        * vertical spacing between scan lines. For sensors with static vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        __result += scanElevation.hashCode(); 
        /** Vector of 16 bit data samples. */
        __result += rangeData.hashCode(); 
        __result += intensityData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>RangeScanSampleTypeSupport</code>
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

        RangeScanSample typedSrc = (RangeScanSample) src;
        RangeScanSample typedDst = this;
        super.copy_from(typedSrc);
        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        typedDst.scanAzimuth = (rapid.ShortSequence64) typedDst.scanAzimuth.copy_from(typedSrc.scanAzimuth);
        /**
        * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
        * vertical spacing between scan lines. For sensors with static vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        typedDst.scanElevation = (rapid.ShortSequence64) typedDst.scanElevation.copy_from(typedSrc.scanElevation);
        /** Vector of 16 bit data samples. */
        typedDst.rangeData = (rapid.ShortSequence2K) typedDst.rangeData.copy_from(typedSrc.rangeData);
        typedDst.intensityData = (rapid.OctetSequence2K) typedDst.intensityData.copy_from(typedSrc.intensityData);

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

        /**
        * Horizontal shot angles of samples in a scan-line for those sensors
        * that have <b>dynamic</b> horizontal spacing. For sensors with static horizontal
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        strBuffer.append(scanAzimuth.toString("scanAzimuth ", indent+1));
        /**
        * Vertical shot angles of samples for those sensors that have <b>dynamic<b>
        * vertical spacing between scan lines. For sensors with static vertical
        * spacing, this sequence will be empty and the corresponding member in
        * RangeScanConfig will be used.
        */
        strBuffer.append(scanElevation.toString("scanElevation ", indent+1));
        /** Vector of 16 bit data samples. */
        strBuffer.append(rangeData.toString("rangeData ", indent+1));
        strBuffer.append(intensityData.toString("intensityData ", indent+1));

        return strBuffer.toString();
    }

}
