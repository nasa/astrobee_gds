

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

/** Direct copy of ROS type */

public class Marker   implements Copyable, Serializable{

    /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
    public String ns=  "" ; /* maximum length = (32) */
    /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
    public int id= 0;
    /** Type of object */
    public rapid.ext.arc.ShapeType type = (rapid.ext.arc.ShapeType)rapid.ext.arc.ShapeType.create();
    /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
    public rapid.ext.arc.Action action = (rapid.ext.arc.Action)rapid.ext.arc.Action.create();
    /** Pose of the object */
    public rapid.Transform3D pose = (rapid.Transform3D)rapid.Transform3D.create();
    /** Scale of the object 1,1,1 means default (usually 1 meter square) */
    public rapid.Vec3f scale = (rapid.Vec3f)rapid.Vec3f.create();
    /** RGBA color [0.0-1.0] */
    public rapid.Color4f color = (rapid.Color4f)rapid.Color4f.create();
    /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
    public long lifetime= 0;
    /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
    public boolean frame_locked= false;
    /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */
    public rapid.ext.arc.PointSequence1K points = (rapid.ext.arc.PointSequence1K)rapid.ext.arc.PointSequence1K.create();

    public rapid.ext.arc.ColorSequence1K colors = (rapid.ext.arc.ColorSequence1K)rapid.ext.arc.ColorSequence1K.create();
    /**  NOTE: only used for text markers */
    public String text=  "" ; /* maximum length = (32) */
    /**  NOTE: only used for MESH_RESOURCE markers */
    public String mesh_resource=  "" ; /* maximum length = (32) */
    public boolean mesh_use_embedded_materials= false;

    public Marker() {

        /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
        /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
        /** Type of object */
        /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
        /** Pose of the object */
        /** Scale of the object 1,1,1 means default (usually 1 meter square) */
        /** RGBA color [0.0-1.0] */
        /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
        /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
        /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */

        /**  NOTE: only used for text markers */
        /**  NOTE: only used for MESH_RESOURCE markers */

    }
    public Marker (Marker other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Marker self;
        self = new  Marker();
        self.clear();
        return self;

    }

    public void clear() {

        /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
        ns=  ""; 
        /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
        id= 0;
        /** Type of object */
        type = rapid.ext.arc.ShapeType.create();
        /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
        action = rapid.ext.arc.Action.create();
        /** Pose of the object */
        if (pose != null) {
            pose.clear();
        }
        /** Scale of the object 1,1,1 means default (usually 1 meter square) */
        if (scale != null) {
            scale.clear();
        }
        /** RGBA color [0.0-1.0] */
        if (color != null) {
            color.clear();
        }
        /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
        lifetime= 0;
        /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
        frame_locked= false;
        /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */
        if (points != null) {
            points.clear();
        }

        if (colors != null) {
            colors.clear();
        }
        /**  NOTE: only used for text markers */
        text=  ""; 
        /**  NOTE: only used for MESH_RESOURCE markers */
        mesh_resource=  ""; 
        mesh_use_embedded_materials= false;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Marker otherObj = (Marker)o;

        /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
        if(!ns.equals(otherObj.ns)) {
            return false;
        }
        /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
        if(id != otherObj.id) {
            return false;
        }
        /** Type of object */
        if(!type.equals(otherObj.type)) {
            return false;
        }
        /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
        if(!action.equals(otherObj.action)) {
            return false;
        }
        /** Pose of the object */
        if(!pose.equals(otherObj.pose)) {
            return false;
        }
        /** Scale of the object 1,1,1 means default (usually 1 meter square) */
        if(!scale.equals(otherObj.scale)) {
            return false;
        }
        /** RGBA color [0.0-1.0] */
        if(!color.equals(otherObj.color)) {
            return false;
        }
        /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
        if(lifetime != otherObj.lifetime) {
            return false;
        }
        /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
        if(frame_locked != otherObj.frame_locked) {
            return false;
        }
        /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */
        if(!points.equals(otherObj.points)) {
            return false;
        }

        if(!colors.equals(otherObj.colors)) {
            return false;
        }
        /**  NOTE: only used for text markers */
        if(!text.equals(otherObj.text)) {
            return false;
        }
        /**  NOTE: only used for MESH_RESOURCE markers */
        if(!mesh_resource.equals(otherObj.mesh_resource)) {
            return false;
        }
        if(mesh_use_embedded_materials != otherObj.mesh_use_embedded_materials) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
        __result += ns.hashCode(); 
        /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
        __result += (int)id;
        /** Type of object */
        __result += type.hashCode(); 
        /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
        __result += action.hashCode(); 
        /** Pose of the object */
        __result += pose.hashCode(); 
        /** Scale of the object 1,1,1 means default (usually 1 meter square) */
        __result += scale.hashCode(); 
        /** RGBA color [0.0-1.0] */
        __result += color.hashCode(); 
        /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
        __result += (int)lifetime;
        /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
        __result += (frame_locked == true)?1:0;
        /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */
        __result += points.hashCode(); 

        __result += colors.hashCode(); 
        /**  NOTE: only used for text markers */
        __result += text.hashCode(); 
        /**  NOTE: only used for MESH_RESOURCE markers */
        __result += mesh_resource.hashCode(); 
        __result += (mesh_use_embedded_materials == true)?1:0;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MarkerTypeSupport</code>
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

        Marker typedSrc = (Marker) src;
        Marker typedDst = this;

        /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
        typedDst.ns = typedSrc.ns;
        /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
        typedDst.id = typedSrc.id;
        /** Type of object */
        typedDst.type = (rapid.ext.arc.ShapeType) typedDst.type.copy_from(typedSrc.type);
        /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
        typedDst.action = (rapid.ext.arc.Action) typedDst.action.copy_from(typedSrc.action);
        /** Pose of the object */
        typedDst.pose = (rapid.Transform3D) typedDst.pose.copy_from(typedSrc.pose);
        /** Scale of the object 1,1,1 means default (usually 1 meter square) */
        typedDst.scale = (rapid.Vec3f) typedDst.scale.copy_from(typedSrc.scale);
        /** RGBA color [0.0-1.0] */
        typedDst.color = (rapid.Color4f) typedDst.color.copy_from(typedSrc.color);
        /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
        typedDst.lifetime = typedSrc.lifetime;
        /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
        typedDst.frame_locked = typedSrc.frame_locked;
        /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */
        typedDst.points = (rapid.ext.arc.PointSequence1K) typedDst.points.copy_from(typedSrc.points);

        typedDst.colors = (rapid.ext.arc.ColorSequence1K) typedDst.colors.copy_from(typedSrc.colors);
        /**  NOTE: only used for text markers */
        typedDst.text = typedSrc.text;
        /**  NOTE: only used for MESH_RESOURCE markers */
        typedDst.mesh_resource = typedSrc.mesh_resource;
        typedDst.mesh_use_embedded_materials = typedSrc.mesh_use_embedded_materials;

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

        /** Namespace to place this object in... used in conjunction with id to create a unique name for the object */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("ns: ").append(ns).append("\n");  
        /** object ID useful in conjunction with the namespace for manipulating and deleting the object later */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("id: ").append(id).append("\n");  
        /** Type of object */
        strBuffer.append(type.toString("type ", indent+1));
        /** 0 add/modify an object, 1 (deprecated), 2 deletes an object, 3 deletes all objects */
        strBuffer.append(action.toString("action ", indent+1));
        /** Pose of the object */
        strBuffer.append(pose.toString("pose ", indent+1));
        /** Scale of the object 1,1,1 means default (usually 1 meter square) */
        strBuffer.append(scale.toString("scale ", indent+1));
        /** RGBA color [0.0-1.0] */
        strBuffer.append(color.toString("color ", indent+1));
        /** NS How long (in nanoseconds) the object should last before being automatically deleted.  0 means forever */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lifetime: ").append(lifetime).append("\n");  
        /** If this marker should be frame-locked, i.e. retransformed into its frame every timestep */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frame_locked: ").append(frame_locked).append("\n");  
        /** Only used if the type specified has some use for them (eg. POINTS, LINE_STRIP, ...) */
        strBuffer.append(points.toString("points ", indent+1));

        strBuffer.append(colors.toString("colors ", indent+1));
        /**  NOTE: only used for text markers */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("text: ").append(text).append("\n");  
        /**  NOTE: only used for MESH_RESOURCE markers */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("mesh_resource: ").append(mesh_resource).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("mesh_use_embedded_materials: ").append(mesh_use_embedded_materials).append("\n");  

        return strBuffer.toString();
    }

}
