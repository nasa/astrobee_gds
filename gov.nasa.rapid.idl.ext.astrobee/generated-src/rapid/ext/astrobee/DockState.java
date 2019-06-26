

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* State message for the dock. Used to tell which
* Astrobee is berthed, in which berth, and if
* the Astrobee is hibernated or awake.
*/

public class DockState  extends rapid.Message implements Copyable, Serializable{

    /** State of berth 1 */
    public rapid.ext.astrobee.BerthState berthOne = (rapid.ext.astrobee.BerthState)rapid.ext.astrobee.BerthState.create();
    /** State of berth 2*/
    public rapid.ext.astrobee.BerthState berthTwo = (rapid.ext.astrobee.BerthState)rapid.ext.astrobee.BerthState.create();

    public DockState() {

        super();

        /** State of berth 1 */
        /** State of berth 2*/

    }
    public DockState (DockState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DockState self;
        self = new  DockState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** State of berth 1 */
        if (berthOne != null) {
            berthOne.clear();
        }
        /** State of berth 2*/
        if (berthTwo != null) {
            berthTwo.clear();
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

        DockState otherObj = (DockState)o;

        /** State of berth 1 */
        if(!berthOne.equals(otherObj.berthOne)) {
            return false;
        }
        /** State of berth 2*/
        if(!berthTwo.equals(otherObj.berthTwo)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** State of berth 1 */
        __result += berthOne.hashCode(); 
        /** State of berth 2*/
        __result += berthTwo.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DockStateTypeSupport</code>
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

        DockState typedSrc = (DockState) src;
        DockState typedDst = this;
        super.copy_from(typedSrc);
        /** State of berth 1 */
        typedDst.berthOne = (rapid.ext.astrobee.BerthState) typedDst.berthOne.copy_from(typedSrc.berthOne);
        /** State of berth 2*/
        typedDst.berthTwo = (rapid.ext.astrobee.BerthState) typedDst.berthTwo.copy_from(typedSrc.berthTwo);

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

        /** State of berth 1 */
        strBuffer.append(berthOne.toString("berthOne ", indent+1));
        /** State of berth 2*/
        strBuffer.append(berthTwo.toString("berthTwo ", indent+1));

        return strBuffer.toString();
    }

}
