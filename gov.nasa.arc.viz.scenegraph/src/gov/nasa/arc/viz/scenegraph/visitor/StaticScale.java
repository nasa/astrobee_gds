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
package gov.nasa.arc.viz.scenegraph.visitor;

import java.nio.FloatBuffer;

import org.apache.log4j.Logger;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 * Statically set scale on a subgraph. If 3x3 matrix is not rotation only, 
 * punt and try to extract the correct values. No guarantees this will
 * work as expected, and no attempt is made to support skew. 
 */
public class StaticScale  {
    private static final Logger logger = Logger.getLogger(StaticScale.class);
    private final static StaticScale INSTANCE = new StaticScale();

    public static void apply(final Spatial spatial) {
        INSTANCE.apply(spatial, Vector3.ONE);
    }

    protected void apply(final Spatial spatial, ReadOnlyVector3 currentScale) {
        try {
            Transform xfm = new Transform(spatial.getTransform());
            Vector3 scale = new Vector3(spatial.getTransform().getScale());
            Vector3 trans = new Vector3(spatial.getTransform().getTranslation());

            if(!xfm.isRotationMatrix()) { // punt 
                ReadOnlyMatrix3 m = xfm.getMatrix();
                double m00 = m.getValue(0,0);
                double m01 = m.getValue(0,1);
                double m02 = m.getValue(0,2);
                double m10 = m.getValue(1,0);
                double m11 = m.getValue(1,1);
                double m12 = m.getValue(1,2);
                double m20 = m.getValue(2,0);
                double m21 = m.getValue(2,1);
                double m22 = m.getValue(2,2);
                double sx = Math.sqrt(m00*m00 + m01*m01 + m02*m02);
                double sy = Math.sqrt(m10*m10 + m11*m11 + m12*m12);
                double sz = Math.sqrt(m20*m20 + m21*m21 + m22*m22);
                Matrix3 rot = new Matrix3();
                // make sure matrix is orthonormal
                Vector3 tmp = new Vector3();
                tmp.set(m.getValue(0,0)/sx, m.getValue(1,0)/sx, m.getValue(2,0)/sx);
                tmp.normalizeLocal();
                rot.setColumn(0,tmp);
                tmp.set(m.getValue(0,1)/sx, m.getValue(1,1)/sx, m.getValue(2,1)/sx);
                tmp.normalizeLocal();
                rot.setColumn(1,tmp);
                tmp.set(m.getValue(0,2)/sx, m.getValue(1,2)/sx, m.getValue(2,2)/sx);
                tmp.normalizeLocal();
                rot.setColumn(2,tmp);

                xfm.setIdentity();
                xfm.setTranslation(trans);
                xfm.setRotation(rot);
                xfm.setScale(scale.set(sx,sy,sz));
                spatial.setTransform(xfm);
            }

            scale.multiplyLocal(currentScale);
            trans.multiplyLocal(scale);
            spatial.setScale(1,1,1);
            spatial.setTranslation(trans);

            if(spatial instanceof Node) {
                Node node = (Node)spatial;
                for(Spatial child : node.getChildren()) {
                    apply(child, scale);
                }
            }
            else if(spatial instanceof Mesh) {
                Mesh mesh = (Mesh)spatial;
                FloatBufferData bufferData = mesh.getMeshData().getVertexCoords();
                FloatBuffer buffer = bufferData.getBuffer();
                float s = 0;
                float v = 0;
                for(int i = 0; i < buffer.limit(); i++) {
                    if     (i%3 == 0) s = currentScale.getXf();
                    else if(i%3 == 1) s = currentScale.getYf();
                    else if(i%3 == 2) s = currentScale.getZf();
                    v = s * buffer.get(i);
                    buffer.put(i, v);
                }
                mesh.getMeshData().setVertexCoords(bufferData);
                mesh.updateModelBound();
            }
        }
        catch(Throwable t) {
            logger.error("Error processing "+spatial.getName(), t);
        }
    }
}
