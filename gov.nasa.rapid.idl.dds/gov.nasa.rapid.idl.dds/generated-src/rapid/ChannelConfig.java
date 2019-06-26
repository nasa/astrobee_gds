

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
* The file queue channel configuration. The file queue can have multiple channels. Bandwidth is split up
* between the channels through a token system.
*/

public class ChannelConfig   implements Copyable, Serializable{

    /** Name of the channel. */
    public String name=  "" ; /* maximum length = (32) */
    /** Number of tokens that channel has. */
    public short numTokens= 0;

    public ChannelConfig() {

        /** Name of the channel. */
        /** Number of tokens that channel has. */

    }
    public ChannelConfig (ChannelConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ChannelConfig self;
        self = new  ChannelConfig();
        self.clear();
        return self;

    }

    public void clear() {

        /** Name of the channel. */
        name=  ""; 
        /** Number of tokens that channel has. */
        numTokens= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ChannelConfig otherObj = (ChannelConfig)o;

        /** Name of the channel. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Number of tokens that channel has. */
        if(numTokens != otherObj.numTokens) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Name of the channel. */
        __result += name.hashCode(); 
        /** Number of tokens that channel has. */
        __result += (int)numTokens;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ChannelConfigTypeSupport</code>
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

        ChannelConfig typedSrc = (ChannelConfig) src;
        ChannelConfig typedDst = this;

        /** Name of the channel. */
        typedDst.name = typedSrc.name;
        /** Number of tokens that channel has. */
        typedDst.numTokens = typedSrc.numTokens;

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

        /** Name of the channel. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Number of tokens that channel has. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numTokens: ").append(numTokens).append("\n");  

        return strBuffer.toString();
    }

}
