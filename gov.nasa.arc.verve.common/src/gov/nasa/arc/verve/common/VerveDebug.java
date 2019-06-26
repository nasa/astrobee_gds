/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.common;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.AxisLines;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.Transform;
import com.ardor3d.math.TransformException;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.Constants;

public class VerveDebug {
    //private static final Logger logger = Logger.getLogger(VerveDebug.class);
    
    private static final Object s_lock = new Object();
    private static VerveDebug    s_instance = null;
    public  static Set<Spatial>  s_drawDebugBound = new HashSet<Spatial>();
    public  static Set<Spatial>  s_drawSelectBound = new HashSet<Spatial>();

    //-- Debug bounds
    public static void setDebugBound(Spatial add) {
        s_drawDebugBound.clear();
        if(add != null)
            s_drawDebugBound.add(add);
    }
    public static void setDebugBounds(List<Spatial> add) {
        s_drawDebugBound.clear();
        for(Spatial s : add) {
            s_drawDebugBound.add(s);
        }
    }
    public static void addDebugBound(Spatial add) {
        s_drawDebugBound.add(add);
    }
    public static Set<Spatial> getDebugBoundList() {
        return s_drawDebugBound;
    }

    //-- Selected bounds
    public static void setSelectBound(Spatial add) {
        s_drawSelectBound.clear();
        if(add != null)
            s_drawSelectBound.add(add);
    }
    public static void setSelectBounds(List<Spatial> add) {
        s_drawSelectBound.clear();
        for(Spatial s : add) {
            s_drawSelectBound.add(s);
        }
    }
    public static void addSelectBound(Spatial add) {
        s_drawSelectBound.add(add);
    }
    public static Set<Spatial> getSelectBoundList() {
        return s_drawSelectBound;
    }

    //-------------------------------------------------------------------------
    private final BoundingBox       measureBox       = new BoundingBox();
    private final AxisLines         axis             = new AxisLines("debug_axis", 10);
    private boolean                 drawDebugAxisUseBoundCenter;

    protected VerveDebug() {
        axis.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        axis.setRenderState(new ZBufferState());
        axis.updateWorldRenderStates(false);
        drawDebugAxisUseBoundCenter = VervePreferences.isDebugAxisUseBoundCenter();
    }

    public static VerveDebug instance() {
        if(s_instance == null) {
            synchronized(s_lock) {
                if(s_instance == null) {
                    s_instance = new VerveDebug();
                }
            }
        }
        return s_instance;
    }

    public static void setDrawDebugAxisUseBoundCenter(boolean state) {
        instance().drawDebugAxisUseBoundCenter = state;
    }

    public static void drawAxis(final Spatial spat, final Renderer r) {
        drawAxis(spat, r, true, false);
    }

    public static void drawAxis(final Spatial spat, final Renderer r, final boolean drawChildren, final boolean drawAll) {
        instance().drawAxisImpl(spat, r, drawChildren, drawAll);
    }

    public void drawAxisImpl(final Spatial spat, final Renderer r, final boolean drawChildren, final boolean drawAll) {
        if (drawAll || (spat instanceof Mesh)) {
            try {
                if (spat.getWorldBound() != null) {
                    double rSize;
                    //                if(spat instanceof Mesh) {
                    //                    Mesh mesh = (Mesh)spat;
                    //                    final BoundingVolume bnd = mesh.getModelBound();
                    //                    double mv = bnd.getVolume();
                    //                }
                    final BoundingVolume vol = spat.getWorldBound();
                    if (vol != null) {
                        measureBox.setCenter(vol.getCenter());
                        measureBox.setXExtent(0);
                        measureBox.setYExtent(0);
                        measureBox.setZExtent(0);
                        measureBox.mergeLocal(vol);
                        rSize = 2 * ((measureBox.getXExtent() + measureBox.getYExtent() + measureBox.getZExtent()) / 3);
                        if(Double.isNaN(rSize)  || Double.isInfinite(rSize) || rSize < 0.001) {
                            rSize = 0.001;
                        }
                    } 
                    else {
                        rSize = 1.0;
                    }
                    if(drawDebugAxisUseBoundCenter) {
                        axis.setTranslation(spat.getWorldBound().getCenter());
                    }
                    else {
                        axis.setTranslation(spat.getWorldTranslation());
                    }
                    axis.setScale(rSize);
                } 
                else {
                    axis.setTranslation(spat.getWorldTranslation());
                    //axis.setScale(spat.getWorldScale());
                }
                axis.setRotation(spat.getWorldRotation());
                axis.updateGeometricState(0, false);

                axis.draw(r);
            }
            catch(TransformException t) {
                //logger.warn("error drawing spatial bound", t);
                // FIXME: objects in the ortho bin put the debug transform into a bad state
                axis.setTransform(new Transform());
            }
        }

        if ((spat instanceof Node) && drawChildren) {
            final Node n = (Node) spat;
            if (n.getNumberOfChildren() == 0) {
                return;
            }
            for (int x = 0, count = n.getNumberOfChildren(); x < count; x++) {
                drawAxis(n.getChild(x), r, drawChildren, drawAll);
            }
        }
    }

    /**
     * get a string that shows the Ardor3D Constants values
     * @param prefix
     * @return
     */
    public static String getConstantsString(String prefix) {
        Field[] fields = Constants.class.getDeclaredFields();
        StringBuilder builder = new StringBuilder((prefix==null) ? "" : prefix);
        builder.append("\n"+Constants.class.getName()+":");
        for(Field field : fields) {
            Class<?> type = field.getType();
            String name = field.getName();
            String val = null;
            try {
                val = field.get(null).toString();
            }
            catch(Throwable t) {
                val = t.getMessage();
            }

            builder.append(String.format("\n    %-10s %-25s = %s", type.toString(), name, val));
        }

        return builder.toString();
    }
}
