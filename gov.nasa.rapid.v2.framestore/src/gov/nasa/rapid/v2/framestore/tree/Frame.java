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
package gov.nasa.rapid.v2.framestore.tree;

import gov.nasa.rapid.v2.framestore.KeyValueMap;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

/** 
 * Class representing a named coordinate transform.
 */
public class Frame {
    protected final Transform   m_xfm  = new Transform();
    protected String            m_name = null;
    protected KeyValueMap       m_data = null;
    
    public enum Axis { 
        X    (new Vector3( 1,  0,  0)), 
        Y    (new Vector3( 0,  1,  0)), 
        Z    (new Vector3( 0,  0,  1)),
        NEG_X(new Vector3(-1,  0,  0)), 
        NEG_Y(new Vector3( 0, -1,  0)), 
        NEX_Z(new Vector3( 0,  0, -1));

        public final ReadOnlyVector3 vector;
        Axis(ReadOnlyVector3 vector) {
            this.vector = vector;
        }
    }
    
    /**
     * Create a named Frame with an identity transform
     * @param name Name of the Frame
     */
    public Frame(String name) {
        m_name = name;
        m_xfm.setIdentity();
    }

    /**
     * Create a named Frame and set to the given Transform
     * @param name Name of the Frame
     * @param transform Initial transform
     */
    public Frame(String name, ReadOnlyTransform transform) {
        m_name = name;
        if(transform == null) {
            m_xfm.setIdentity();
        }
        else {
            m_xfm.set(transform);
        }
    }

    /**
     * Create a copy of an existing frame
     * @param toCopy Source Frame to copy
     */
    public Frame(Frame toCopy) {
        m_name = toCopy.getName();
        m_xfm.set(toCopy.getTransform());
    }

    /**
     * Assignment
     * @param toCopy Frame to construct a new Frame from
     * @return this New Frame, copy of the source Frame
     */
    public Frame set(Frame toCopy) {
        m_name = toCopy.getName();
        toCopy.getTransform(m_xfm);
        return this;
    }

    /**
     * Accessor
     * @param name New name for the Frame
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * Accessor
     * @return Name of the Frame
     */
    public String getName() {
        return m_name;
    }

    /**
     * copies transform into local Transform store
     * @param transform New transform matrix. Note that if
     *          transform is null, then the Frame transform 
     *          is set to the identity matrix, following the 
     *          behavior of the constructor. 
     */
    public void setTransform(ReadOnlyTransform transform) {
        synchronized(m_xfm) {
            if (transform == null) {
                m_xfm.setIdentity();
            }
            else {
                m_xfm.set(transform);
            }
        }
    }

    public ReadOnlyTransform setRotation(ReadOnlyMatrix3 rotation) {
        synchronized(m_xfm) {
            if (rotation == null) {
                m_xfm.setRotation(Matrix3.IDENTITY);
            }
            else {
                m_xfm.setRotation(rotation);
            }
        }
        return m_xfm;
    }
    
    public ReadOnlyTransform setTranslation(ReadOnlyVector3 translation) {
        synchronized(m_xfm) {
            if ( translation == null ) {
                m_xfm.setTranslation(Vector3.ZERO);
            }
            else {
                m_xfm.setTranslation(translation);
            }
        }
        return m_xfm;
    }
    
    /** 
     * returns a reference to the Transform owned by this frame
     */
    public ReadOnlyTransform getTransform() {
        return m_xfm;
    }    
    
    /**
     * Sets retVal to Frame transform and returns it
     * @returns retVal
     */
    public Transform getTransform(Transform retVal) {
        if(retVal == null) {
            retVal = new Transform();
        }
        synchronized(m_xfm) {
            retVal.set(m_xfm);
        }
        return retVal;
    }

    /** 
     * Frames comparison
     * @return true if name and transform are equivalent
     */
    public boolean matches(Frame frame) {
        if( this == frame ) {
            return true;
        }
        boolean eqName = false;
        if(m_name == null && frame.getName() == null) {
            eqName = true;
        }
        else if(m_name != null && m_name.equals(frame.getName())) {
            eqName = true;
        }
        if( eqName && frame.getTransform().equals(m_xfm) ) {
            return true;
        }
        return false;
    }
    
    /**
     * Return the KeyValueMap associated with the Frame
     * @return  current KeyValueMap or null if none was assigned
     */
    public KeyValueMap getKeyValueMap() {
        return m_data;
    }
    
    /**
     * Assign the given KeyValueMap to the Frame 
     * @param map   KeyValueMap to associate with the Frame
     */
    public void setKeyValueMap(KeyValueMap map) {
        m_data = map;
    }
    
}
