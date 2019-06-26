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
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;

import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.scenegraph.Node;

public class OffsetQuad extends Node {
    TexQuad m_texQuad;

    public OffsetQuad(String name, float size) {
        this(name, size, size);
    }
        
    public OffsetQuad(String name, float sizeX, float sizeY) {
        super(name);
        m_texQuad = new TexQuad(name+"Quad", sizeX, sizeY, false);
        attachChild(m_texQuad);
    }
        
    public TexQuad getQuad() {
        return m_texQuad;
    }
    
    public void setSize(float size) {
        m_texQuad.setSize(size);
    }
    
    public void setSize(float x, float y) {
        m_texQuad.setSize(x, y);
    }

    /** @return average of x and y size */
    public float getSize() {
        return m_texQuad.getSize();
    }

    public float getXSize() {
        return m_texQuad.getXSize();
    }
    
    public float getYSize() {
        return m_texQuad.getYSize();
    }
    
    
    public void setDefaultColor(ReadOnlyColorRGBA color) {
        m_texQuad.setDefaultColor(color);
    }
}
