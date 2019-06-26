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

import java.util.Iterator;
import java.util.LinkedHashMap;

import rapid.DataType;
import rapid.KeyTypeValueTriple;
import rapid.KeyTypeValueTripleSeq;
import rapid.Mat33f;
import rapid.ParameterUnion;
import rapid.ParameterUnionSeq;
import rapid.Vec3d;

/**
 * Utility to put and get KeyTypeValueTriples
 */
public class KeyTypeValueHelper {
    protected final LinkedHashMap<String,ParameterUnion> m_params = new LinkedHashMap<String,ParameterUnion>();

    /**
     * Initialize this ParameterList with parameters
     * @see init(KeyTypeValueTripleSeq parameters)
     */
    public KeyTypeValueHelper(KeyTypeValueTripleSeq parameters) {
        if(parameters != null) 
            init(parameters);
    }

    /**
     * create an empty parameter list
     */
    public KeyTypeValueHelper() {
    }

    /**
     * @return index of key, else -1 if key is not in list
     */
    public int indexOf(String key) {
        int index = 0;
        for(String k : m_params.keySet()) {
            if(k.equals(key)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Clears current values in list and copies values from KeyTypeValueTripleSeq.
     * @param parameters
     * @return this for chaining
     */
    public KeyTypeValueHelper init(KeyTypeValueTripleSeq parameters) {
        m_params.clear();
        for(int i = 0; i < parameters.size(); i++) {
            KeyTypeValueTriple src = (KeyTypeValueTriple)parameters.get(i);
            ParameterUnion value = new ParameterUnion();
            value.copy_from(src.value);
            m_params.put(src.key, value);
        }
        return this;
    }

    public int size() {
        return m_params.size();
    }

    /**
     * clear current list of parameters
     */
    public KeyTypeValueHelper clear() {
        m_params.clear();
        return this;
    }

    /**
     * Sets the values in this parameter list to those in values. 
     * @param values
     * @return this for chaining
     * @throws IllegalStateException if size of values does not match current parameter list
     */
    public KeyTypeValueHelper set(ParameterUnionSeq values) throws IllegalStateException {
        if(m_params.size() != values.size()) {
            throw new IllegalStateException("value list (size="+values.size()+") does not match parameter list ("+m_params.size()+")");
        }
        int index = 0;
        Iterator<ParameterUnion> it = m_params.values().iterator();
        while(it.hasNext()) {
            ParameterUnion param = it.next();
            ParameterUnion src = (ParameterUnion)values.get(index);
            param.copy_from(src);
            index++;
        }
        return this;
    }

    /**
     * Assign values to retVal. Results in shallow copy of the ParameterUnions held in this object.
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
     * Store a shallow copy of this helper's contents in retVal
     * @param retVal if null, a new KeyTypeValueTripleSeq will be created
     * @return retVal
     */
    public KeyTypeValueTripleSeq assign(KeyTypeValueTripleSeq retVal) {
        if(retVal == null) {
            retVal = new KeyTypeValueTripleSeq();
        }
        retVal.clear();
        Iterator<String> it = m_params.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            ParameterUnion value = m_params.get(key);
            DataType type = value._d;
            KeyTypeValueTriple ktvt = new KeyTypeValueTriple();
            ktvt.key = key;
            ktvt.value._d = type;
            ktvt.value = value;
            retVal.add(ktvt);
        }
        return retVal;
    }

    /**
     * add a new parameter to the list
     */
    protected ParameterUnion add(String name, DataType dataType) {  
        ParameterUnion param = new ParameterUnion();
        param._d = dataType;
        m_params.put(name, param);
        return param;
    }


    public KeyTypeValueHelper put(String name, boolean value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_BOOL);
        }
        if(param._d.equals(DataType.RAPID_BOOL)) 
            param.b = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public KeyTypeValueHelper put(String name, double value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_DOUBLE);
        }
        if(param._d.equals(DataType.RAPID_DOUBLE)) 
            param.d = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public KeyTypeValueHelper put(String name, float value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_FLOAT);
        }
        if(param._d.equals(DataType.RAPID_FLOAT)) 
            param.f = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public KeyTypeValueHelper put(String name, int value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_INT);
        }
        if(param._d.equals(DataType.RAPID_INT)) 
            param.i = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public KeyTypeValueHelper put(String name, long value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_LONGLONG);
        }
        if(param._d.equals(DataType.RAPID_LONGLONG)) 
            param.ll = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public KeyTypeValueHelper put(String name, String value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_STRING);
        }
        if(param._d.equals(DataType.RAPID_STRING)) 
            param.s = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public KeyTypeValueHelper put(String name, Vec3d value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_VEC3d);
        }
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
    public KeyTypeValueHelper put(String name, double x, double y, double z) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_VEC3d);
        }
        if(param._d.equals(DataType.RAPID_VEC3d)) {
            param.vec3d.userData[0] = x;
            param.vec3d.userData[1] = y;
            param.vec3d.userData[2] = z;
        }
        else throw new UncheckedInvalidParameterException(name, param, x);
        return this;
    }

    public KeyTypeValueHelper put(String name, Mat33f value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) {
            param = add(name, DataType.RAPID_MAT33f);
        }
        if(param._d.equals(DataType.RAPID_MAT33f)) {
            for(int i = 0; i < 9; i++) {
                param.mat33f.userData[i] = value.userData[i];
            }
        }
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }
    
    public boolean hasParam(String name) {
        if(m_params.get(name) == null) {
            return false;
        }
        return true;
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
        if(param._d.equals(DataType.RAPID_FLOAT))
            return param.f;
        if(param._d.equals(DataType.RAPID_INT))
            return param.i;
        if(param._d.equals(DataType.RAPID_LONGLONG))
            return param.ll;
        throw new UncheckedInvalidParameterException(name, param);
    }

    public float getFloat(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_DOUBLE))
            return (float)param.d;
        if(param._d.equals(DataType.RAPID_FLOAT))
            return param.f;
        if(param._d.equals(DataType.RAPID_INT))
            return param.i;
        if(param._d.equals(DataType.RAPID_LONGLONG))
            return param.ll;
        throw new UncheckedInvalidParameterException(name, param);
    }

    public int getInteger(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_INT))
            return param.i;
        throw new UncheckedInvalidParameterException(name, param);
    }

    public long getLong(String name) throws UncheckedInvalidParameterException {
        ParameterUnion param = getParam(name);
        if(param._d.equals(DataType.RAPID_INT))
            return param.i;
        if(param._d.equals(DataType.RAPID_LONGLONG))
            return param.ll;
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

    public void print() {
        int index = 0;
        for(String key : m_params.keySet()) {
            System.out.println(String.format("%2d: %25s = %s", index, key, ParamHelper.valueString(m_params.get(key))));
            index++;
        }
    }
}
