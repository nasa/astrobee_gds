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
package gov.nasa.rapid.v2.e4.message.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import rapid.DataType;
import rapid.KeyTypePair;
import rapid.KeyTypePairSeq;
import rapid.Mat33f;
import rapid.ParameterUnion;
import rapid.ParameterUnionSeq;
import rapid.Vec3d;

/**
 * ParameterList assists in setting and getting parameters split 
 * across KeyTypePair sequences (in *Config messages) and 
 * ParameterUnion sequences (in *Sample or *State messages). After the 
 * parameter list is initialized from a KeyTypePairSeq, the values are 
 * uninitialized. The values may be set individually, or by passing 
 * a ParameterUnionSeq that matches the KeyTypePairSeq. 
 * @author mallan
 *
 */
public class ParameterList {

    protected final LinkedHashMap<String,ParameterUnion> m_params = new LinkedHashMap<String,ParameterUnion>(8);

    /**
     * Initialize this ParameterList with parameters
     * @see init(KeyTypePairSeq parameters)
     */
    public ParameterList(KeyTypePairSeq parameters) {
        if(parameters != null) 
            init(parameters);
    }
    
    /**
     * create an empty parameter list
     */
    public ParameterList() {
    }
    
    /**
     * @return names of all parameters
     */
    public List<String> names() {
        ArrayList<String> retVal = new ArrayList<String>(m_params.size());
        for(String key : m_params.keySet()) {
            retVal.add(key);
        }
        return retVal;
    }
    
    /**
     * @return current values of parameter list
     */
    public List<ParameterUnion> values() {
        ArrayList<ParameterUnion> retVal = new ArrayList<ParameterUnion>(m_params.size());
        for(ParameterUnion pu : m_params.values()) {
            retVal.add(pu);
        }
        return retVal;
    }

    /**
     * Clears current parameter list and initializes from KeyTypePairSeq.
     * Values of the parameters are uninitialized.
     * @param parameters
     * @return this for chaining
     */
    public ParameterList init(KeyTypePairSeq parameters) {
        m_params.clear();
        for(int i = 0; i < parameters.size(); i++) {
            KeyTypePair ktp = (KeyTypePair)parameters.get(i);
            ParameterUnion param = new ParameterUnion();
            param._d = ktp.type;
            m_params.put(ktp.key, param);
        }
        return this;
    }
    
    public int size() {
        return m_params.size();
    }
    
    /**
     * clear current list of parameters
     */
    public ParameterList clear() {
        m_params.clear();
        return this;
    }

    /**
     * Sets the values in this parameter list to those in values. 
     * @param values
     * @return this for chaining
     * @throws IllegalStateException if size of values does not match current parameter list
     */
    public ParameterList set(ParameterUnionSeq values) throws IllegalStateException {
        if(m_params.size() != values.size()) {
            throw new IllegalStateException("value list (size="+values.size()+") does not match parameter list (size="+m_params.size()+")");
        }
        Iterator<ParameterUnion> it = m_params.values().iterator();
        for(int i = 0; i < values.size(); i++) {
            ParameterUnion param = it.next();
            param.copy_from(values.get(i));
        }
        return this;
    }

    /**
     * Assign the values in this ParameterList to retVal
     * @param retVal if null, a new ParameterUnionSeq will be created
     * @return retVal
     */
    public ParameterUnionSeq assign(ParameterUnionSeq retVal) {
        if(retVal == null) 
            retVal = new ParameterUnionSeq();
        retVal.clear();
        Iterator<ParameterUnion> it = m_params.values().iterator();
        while(it.hasNext()) {
            retVal.add(it.next());
        }
        return retVal;
    }
    
    /**
     * Creates a sequence of new KeyTypePairs from this ParameterList
     * @param retVal if null, a new KeyTypePairSeq will be created
     * @return retVal
     */
    public KeyTypePairSeq assign(KeyTypePairSeq retVal) {
        if(retVal == null) {
            retVal = new KeyTypePairSeq();
        }
        retVal.clear();
        Iterator<String> it = m_params.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            DataType type = m_params.get(key)._d;
            KeyTypePair ktp = new KeyTypePair();
            ktp.key = key;
            ktp.type = type;
            retVal.add(ktp);
        }
        return retVal;
    }
    
    /**
     * add a new parameter to the list
     * @param name
     * @param dataType
     * @return this for chaining
     */
    public ParameterList add(String name, DataType dataType) {  
        ParameterUnion param = new ParameterUnion();
        param._d = dataType;
        m_params.put(name, param);
        return this;
    }

    
    public ParameterList set(String name, boolean value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_BOOL)) 
            param.b = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ParameterList set(String name, double value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_DOUBLE)) 
            param.d = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ParameterList set(String name, float value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_FLOAT)) 
            param.f = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ParameterList set(String name, int value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_INT)) 
            param.i = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    /**
     * Set String ParameterUnion to value. Also supports setting primitive
     * types using valueOf conversions. 
     * @param name
     * @param value
     * @return
     * @throws UncheckedInvalidParameterException
     */
    public ParameterList set(String name, String value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        try {
            switch(param._d.ordinal()) {
            case DataType._RAPID_BOOL:   param.b = Boolean.valueOf(value); break;
            case DataType._RAPID_DOUBLE: param.d = Double.valueOf(value);  break;
            case DataType._RAPID_FLOAT:  param.f = Float.valueOf(value);   break;
            case DataType._RAPID_INT:    param.i = Integer.valueOf(value); break;
            case DataType._RAPID_STRING: param.s = value; break;
            case DataType._RAPID_MAT33f:
            case DataType._RAPID_VEC3d:
            default:
                throw new UnsupportedOperationException("Cannot convert String to "+param._d.toString());
            }
        }
        catch(Throwable t) {
            throw new UncheckedInvalidParameterException(name, param, value, t);
        }
        return this;
    }

    public ParameterList set(String name, Vec3d value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_VEC3d)) {
            param.vec3d.userData[0] = value.userData[0];
            param.vec3d.userData[1] = value.userData[1];
            param.vec3d.userData[2] = value.userData[2];
        }
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    /**
     * Set a Vec3d parameter from x,y,z values
     * @return this for chaining
     * @throws UncheckedInvalidParameterException
     */
    public ParameterList set(String name, double x, double y, double z) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_VEC3d)) {
            param.vec3d.userData[0] = x;
            param.vec3d.userData[1] = y;
            param.vec3d.userData[2] = z;
        }
        else throw new UncheckedInvalidParameterException(name, param, x);
        return this;
    }

    public ParameterList set(String name, Mat33f value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_MAT33f)) {
            for(int i = 0; i < 9; i++) {
                param.mat33f.userData[i] = value.userData[i];
            }
        }
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ParameterUnion getParam(String name) throws UncheckedInvalidParameterException  {
        ParameterUnion retVal = m_params.get(name);
        if(retVal == null) 
            throw new UncheckedInvalidParameterException(name);
        return retVal;
    }
    
    public boolean getBoolean(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_BOOL))
            return param.b;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
    public double getDouble(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_DOUBLE))
            return param.d;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
    public float getFloat(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_FLOAT))
            return param.f;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
    public int getInteger(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_INT))
            return param.i;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
    public String getString(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_STRING))
            return param.s;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
    public Vec3d getVec3d(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_VEC3d))
            return param.vec3d;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
    public Mat33f getMat33(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_MAT33f))
            return param.mat33f;
        throw new UncheckedInvalidParameterException(name, param);
    }
    
}
