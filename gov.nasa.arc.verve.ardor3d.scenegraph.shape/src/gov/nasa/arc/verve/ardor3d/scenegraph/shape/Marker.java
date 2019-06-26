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
package gov.nasa.arc.verve.ardor3d.scenegraph.shape;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.LightCombineMode;

public class Marker extends Node {
    LathedCylinder m_XRod;
    LathedCylinder m_YRod;
    LathedCylinder m_ZRod;

    public Marker(String name, double size) {
        super(name);
        ctor(size, 0.03);
    }
    public Marker(String name, double size, double thick) {
        super(name);
        ctor(size, thick);
    }

    private void ctor(double size, double thick) {        
        float c = 0.9f;
        float b = 0.3f;
        float r = (float)(size*thick);
        float e = (float)size/2f;
        m_XRod = new Rod("X", r, e);
        m_XRod.setMajorAxis(LathedCylinder.MajorAxis.X);
        m_XRod.setDefaultColor(new ColorRGBA(c, b, b, 1));
        m_XRod.initialize();

        m_YRod = new Rod("Y", r, e);
        m_YRod.setMajorAxis(LathedCylinder.MajorAxis.Y);
        m_YRod.setDefaultColor(new ColorRGBA(b, c, b, 1));
        m_YRod.initialize();

        m_ZRod = new Rod("Z", r, e);
        m_ZRod.setMajorAxis(LathedCylinder.MajorAxis.Z);
        m_ZRod.setDefaultColor(new ColorRGBA(b, b, c, 1));
        m_ZRod.initialize();

        attachChild(m_XRod);
        attachChild(m_YRod);
        attachChild(m_ZRod);

        getSceneHints().setLightCombineMode(LightCombineMode.Off);
    }

    class Rod extends LathedCylinder {
        public Rod(String name, float radius, float e) {
            super(name, radius);
            setTesselation(5, 3);
            setExtents(-e, e);
            setModelBound(new BoundingSphere());
            getSceneHints().setAllPickingHints(false);
        }
        @Override
        protected void lathe(Vector3 vert, Vector2 tex, float rowpos, float colpos) {
            //
        }
    }
}
