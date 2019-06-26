

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* PointSample describes the position and attributes of a point in the cloud. Note: Attributes are included
* in this struct because we get them for free on the wire due to word alignment.
*/
/** PointCloudSample message sends data in point cloud format. */

public class PointCloudSample  extends rapid.Message implements Copyable, Serializable{

    /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
    public rapid.Vec3d origin = (rapid.Vec3d)rapid.Vec3d.create();
    /**
    * Per-element scale factor to convert point units to a meaningful value. Following conversion,
    * spatial coordinates should be in METERS and angular values should be in RADIANS.
    */
    public rapid.Vec3d xyzScale = (rapid.Vec3d)rapid.Vec3d.create();
    /** Sequence of points. */
    public rapid.PointSampleSequence points = (rapid.PointSampleSequence)rapid.PointSampleSequence.create();
    /**
    * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
    *
    * <code>
    * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
    * </code>
    *
    * Otherwise,
    *
    * <code>
    * SUM(rowLengths) == samples.length().
    * </code>
    */
    public rapid.ShortSequence128 rowLengths = (rapid.ShortSequence128)rapid.ShortSequence128.create();

    public PointCloudSample() {

        super();

        /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
        /**
        * Per-element scale factor to convert point units to a meaningful value. Following conversion,
        * spatial coordinates should be in METERS and angular values should be in RADIANS.
        */
        /** Sequence of points. */
        /**
        * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
        *
        * <code>
        * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
        * </code>
        *
        * Otherwise,
        *
        * <code>
        * SUM(rowLengths) == samples.length().
        * </code>
        */

    }
    public PointCloudSample (PointCloudSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        PointCloudSample self;
        self = new  PointCloudSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
        if (origin != null) {
            origin.clear();
        }
        /**
        * Per-element scale factor to convert point units to a meaningful value. Following conversion,
        * spatial coordinates should be in METERS and angular values should be in RADIANS.
        */
        if (xyzScale != null) {
            xyzScale.clear();
        }
        /** Sequence of points. */
        if (points != null) {
            points.clear();
        }
        /**
        * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
        *
        * <code>
        * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
        * </code>
        *
        * Otherwise,
        *
        * <code>
        * SUM(rowLengths) == samples.length().
        * </code>
        */
        if (rowLengths != null) {
            rowLengths.clear();
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

        PointCloudSample otherObj = (PointCloudSample)o;

        /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
        if(!origin.equals(otherObj.origin)) {
            return false;
        }
        /**
        * Per-element scale factor to convert point units to a meaningful value. Following conversion,
        * spatial coordinates should be in METERS and angular values should be in RADIANS.
        */
        if(!xyzScale.equals(otherObj.xyzScale)) {
            return false;
        }
        /** Sequence of points. */
        if(!points.equals(otherObj.points)) {
            return false;
        }
        /**
        * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
        *
        * <code>
        * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
        * </code>
        *
        * Otherwise,
        *
        * <code>
        * SUM(rowLengths) == samples.length().
        * </code>
        */
        if(!rowLengths.equals(otherObj.rowLengths)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
        __result += origin.hashCode(); 
        /**
        * Per-element scale factor to convert point units to a meaningful value. Following conversion,
        * spatial coordinates should be in METERS and angular values should be in RADIANS.
        */
        __result += xyzScale.hashCode(); 
        /** Sequence of points. */
        __result += points.hashCode(); 
        /**
        * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
        *
        * <code>
        * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
        * </code>
        *
        * Otherwise,
        *
        * <code>
        * SUM(rowLengths) == samples.length().
        * </code>
        */
        __result += rowLengths.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PointCloudSampleTypeSupport</code>
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

        PointCloudSample typedSrc = (PointCloudSample) src;
        PointCloudSample typedDst = this;
        super.copy_from(typedSrc);
        /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
        typedDst.origin = (rapid.Vec3d) typedDst.origin.copy_from(typedSrc.origin);
        /**
        * Per-element scale factor to convert point units to a meaningful value. Following conversion,
        * spatial coordinates should be in METERS and angular values should be in RADIANS.
        */
        typedDst.xyzScale = (rapid.Vec3d) typedDst.xyzScale.copy_from(typedSrc.xyzScale);
        /** Sequence of points. */
        typedDst.points = (rapid.PointSampleSequence) typedDst.points.copy_from(typedSrc.points);
        /**
        * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
        *
        * <code>
        * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
        * </code>
        *
        * Otherwise,
        *
        * <code>
        * SUM(rowLengths) == samples.length().
        * </code>
        */
        typedDst.rowLengths = (rapid.ShortSequence128) typedDst.rowLengths.copy_from(typedSrc.rowLengths);

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

        /** Origin of points (i.e., offset from frame 0,0,0 in meters). */
        strBuffer.append(origin.toString("origin ", indent+1));
        /**
        * Per-element scale factor to convert point units to a meaningful value. Following conversion,
        * spatial coordinates should be in METERS and angular values should be in RADIANS.
        */
        strBuffer.append(xyzScale.toString("xyzScale ", indent+1));
        /** Sequence of points. */
        strBuffer.append(points.toString("points ", indent+1));
        /**
        * Descriptions of rows from the same scan-line. If all points belong to the same point cloud,
        *
        * <code>
        * rowLengths.length() == 0 (or rowLengths.length()==1 && rowLengths[0] == samples.length() )
        * </code>
        *
        * Otherwise,
        *
        * <code>
        * SUM(rowLengths) == samples.length().
        * </code>
        */
        strBuffer.append(rowLengths.toString("rowLengths ", indent+1));

        return strBuffer.toString();
    }

}
