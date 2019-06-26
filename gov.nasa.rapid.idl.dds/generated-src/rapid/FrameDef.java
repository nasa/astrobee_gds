

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

/** Frame Definition */

public class FrameDef   implements Copyable, Serializable{

    /** The name of the frame. */
    public String name=  "" ; /* maximum length = (32) */
    /**
    * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
    * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
    * there for starters.
    */
    public String parent=  "" ; /* maximum length = (256) */
    /**
    * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
    * change with updates of the acutal value, but only contains the start configuration.
    */
    public rapid.Transform3D transform = (rapid.Transform3D)rapid.Transform3D.create();
    /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */
    public rapid.KeyTypeValueSequence8 extras = (rapid.KeyTypeValueSequence8)rapid.KeyTypeValueSequence8.create();

    public FrameDef() {

        /** The name of the frame. */
        /**
        * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
        * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
        * there for starters.
        */
        /**
        * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
        * change with updates of the acutal value, but only contains the start configuration.
        */
        /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */

    }
    public FrameDef (FrameDef other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FrameDef self;
        self = new  FrameDef();
        self.clear();
        return self;

    }

    public void clear() {

        /** The name of the frame. */
        name=  ""; 
        /**
        * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
        * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
        * there for starters.
        */
        parent=  ""; 
        /**
        * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
        * change with updates of the acutal value, but only contains the start configuration.
        */
        if (transform != null) {
            transform.clear();
        }
        /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */
        if (extras != null) {
            extras.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        FrameDef otherObj = (FrameDef)o;

        /** The name of the frame. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /**
        * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
        * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
        * there for starters.
        */
        if(!parent.equals(otherObj.parent)) {
            return false;
        }
        /**
        * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
        * change with updates of the acutal value, but only contains the start configuration.
        */
        if(!transform.equals(otherObj.transform)) {
            return false;
        }
        /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */
        if(!extras.equals(otherObj.extras)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** The name of the frame. */
        __result += name.hashCode(); 
        /**
        * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
        * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
        * there for starters.
        */
        __result += parent.hashCode(); 
        /**
        * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
        * change with updates of the acutal value, but only contains the start configuration.
        */
        __result += transform.hashCode(); 
        /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */
        __result += extras.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FrameDefTypeSupport</code>
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

        FrameDef typedSrc = (FrameDef) src;
        FrameDef typedDst = this;

        /** The name of the frame. */
        typedDst.name = typedSrc.name;
        /**
        * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
        * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
        * there for starters.
        */
        typedDst.parent = typedSrc.parent;
        /**
        * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
        * change with updates of the acutal value, but only contains the start configuration.
        */
        typedDst.transform = (rapid.Transform3D) typedDst.transform.copy_from(typedSrc.transform);
        /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */
        typedDst.extras = (rapid.KeyTypeValueSequence8) typedDst.extras.copy_from(typedSrc.extras);

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

        /** The name of the frame. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /**
        * The name of the parent frame. This can contain a (partial) path to ensure uniqueness. An empty
        * parent name denotes a root frame. The C++ implementation will always put the fully qualified path in
        * there for starters.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("parent: ").append(parent).append("\n");  
        /**
        * The coordinate frame transform. For frames updated by telemetry (...Sample messages), this does not
        * change with updates of the acutal value, but only contains the start configuration.
        */
        strBuffer.append(transform.toString("transform ", indent+1));
        /** Sequence of extra values stored with the frame, such as lat/lon for UTM-grid frames. */
        strBuffer.append(extras.toString("extras ", indent+1));

        return strBuffer.toString();
    }

}
