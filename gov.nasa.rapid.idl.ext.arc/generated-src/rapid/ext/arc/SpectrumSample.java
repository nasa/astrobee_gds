

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class SpectrumSample  extends rapid.Message implements Copyable, Serializable{

    /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
    public rapid.LongSequence1K spectrumData = (rapid.LongSequence1K)rapid.LongSequence1K.create();
    /** Specifies any spectrum instrument specific information */
    public rapid.ValueSequence64 values = (rapid.ValueSequence64)rapid.ValueSequence64.create();

    public SpectrumSample() {

        super();

        /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
        /** Specifies any spectrum instrument specific information */

    }
    public SpectrumSample (SpectrumSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SpectrumSample self;
        self = new  SpectrumSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
        if (spectrumData != null) {
            spectrumData.clear();
        }
        /** Specifies any spectrum instrument specific information */
        if (values != null) {
            values.clear();
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

        SpectrumSample otherObj = (SpectrumSample)o;

        /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
        if(!spectrumData.equals(otherObj.spectrumData)) {
            return false;
        }
        /** Specifies any spectrum instrument specific information */
        if(!values.equals(otherObj.values)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
        __result += spectrumData.hashCode(); 
        /** Specifies any spectrum instrument specific information */
        __result += values.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SpectrumSampleTypeSupport</code>
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

        SpectrumSample typedSrc = (SpectrumSample) src;
        SpectrumSample typedDst = this;
        super.copy_from(typedSrc);
        /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
        typedDst.spectrumData = (rapid.LongSequence1K) typedDst.spectrumData.copy_from(typedSrc.spectrumData);
        /** Specifies any spectrum instrument specific information */
        typedDst.values = (rapid.ValueSequence64) typedDst.values.copy_from(typedSrc.values);

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

        /** Spectrum Data, specify name and index range in SpectrumConfig.spectrumNameRange use each element as the bin */
        strBuffer.append(spectrumData.toString("spectrumData ", indent+1));
        /** Specifies any spectrum instrument specific information */
        strBuffer.append(values.toString("values ", indent+1));

        return strBuffer.toString();
    }

}
