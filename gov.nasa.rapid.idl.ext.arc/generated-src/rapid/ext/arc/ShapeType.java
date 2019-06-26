

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

public class ShapeType  extends Enum {

    public static final ShapeType ARROW = new ShapeType("ARROW", 0);
    public static final int _ARROW = 0;
    public static final ShapeType CUBE = new ShapeType("CUBE", 1);
    public static final int _CUBE = 1;
    public static final ShapeType SPHERE = new ShapeType("SPHERE", 2);
    public static final int _SPHERE = 2;
    public static final ShapeType CYLINDER = new ShapeType("CYLINDER", 3);
    public static final int _CYLINDER = 3;
    public static final ShapeType LINE_STRIP = new ShapeType("LINE_STRIP", 4);
    public static final int _LINE_STRIP = 4;
    public static final ShapeType LINE_LIST = new ShapeType("LINE_LIST", 5);
    public static final int _LINE_LIST = 5;
    public static final ShapeType CUBE_LIST = new ShapeType("CUBE_LIST", 6);
    public static final int _CUBE_LIST = 6;
    public static final ShapeType SPHERE_LIST = new ShapeType("SPHERE_LIST", 7);
    public static final int _SPHERE_LIST = 7;
    public static final ShapeType POINTS = new ShapeType("POINTS", 8);
    public static final int _POINTS = 8;
    public static final ShapeType TEXT_VIEW_FACING = new ShapeType("TEXT_VIEW_FACING", 9);
    public static final int _TEXT_VIEW_FACING = 9;
    public static final ShapeType MESH_RESOURCE = new ShapeType("MESH_RESOURCE", 10);
    public static final int _MESH_RESOURCE = 10;
    public static final ShapeType TRIANGLE_LIST = new ShapeType("TRIANGLE_LIST", 11);
    public static final int _TRIANGLE_LIST = 11;
    public static ShapeType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ShapeType.ARROW;
            case 1: return ShapeType.CUBE;
            case 2: return ShapeType.SPHERE;
            case 3: return ShapeType.CYLINDER;
            case 4: return ShapeType.LINE_STRIP;
            case 5: return ShapeType.LINE_LIST;
            case 6: return ShapeType.CUBE_LIST;
            case 7: return ShapeType.SPHERE_LIST;
            case 8: return ShapeType.POINTS;
            case 9: return ShapeType.TEXT_VIEW_FACING;
            case 10: return ShapeType.MESH_RESOURCE;
            case 11: return ShapeType.TRIANGLE_LIST;

        }
        return null;
    }

    public static ShapeType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[12];

        values[i] = ARROW.ordinal();
        i++;
        values[i] = CUBE.ordinal();
        i++;
        values[i] = SPHERE.ordinal();
        i++;
        values[i] = CYLINDER.ordinal();
        i++;
        values[i] = LINE_STRIP.ordinal();
        i++;
        values[i] = LINE_LIST.ordinal();
        i++;
        values[i] = CUBE_LIST.ordinal();
        i++;
        values[i] = SPHERE_LIST.ordinal();
        i++;
        values[i] = POINTS.ordinal();
        i++;
        values[i] = TEXT_VIEW_FACING.ordinal();
        i++;
        values[i] = MESH_RESOURCE.ordinal();
        i++;
        values[i] = TRIANGLE_LIST.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ShapeType create() {

        return valueOf(0);
    }

    /**
    * Print Method
    */     
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();

        CdrHelper.printIndent(strBuffer, indent);

        if (desc != null) {
            strBuffer.append(desc).append(": ");
        }

        strBuffer.append(this);
        strBuffer.append("\n");              
        return strBuffer.toString();
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(ordinal());
    }

    private ShapeType(String name, int ordinal) {
        super(name, ordinal);
    }
}

