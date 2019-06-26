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
 * This class will be deprecated soon
 * The KeyTypeValueTriple data type is not valid any more since 
 * Value was changed from String to Parameter. 
 */
@Deprecated
public class ExtrasHelper {
    //private static final Logger logger = Logger.getLogger(ExtrasHelper.class);

    protected final LinkedHashMap<String,ParameterUnion> m_params = new LinkedHashMap<String,ParameterUnion>();

    /**
     * Initialize this ParameterList with parameters
     * @see init(KeyTypeValueTripleSeq parameters)
     */
    public ExtrasHelper(KeyTypeValueTripleSeq parameters) {
        if(parameters != null) 
            init(parameters);
    }

    /**
     * create an empty parameter list
     */
    public ExtrasHelper() {
    }

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
     * Clears current parameter list and initializes from KeyTypeValueTripleSeq.
     * Values of the parameters are uninitialized.
     * @param parameters
     * @return this for chaining
     */
    public ExtrasHelper init(KeyTypeValueTripleSeq parameters) {
        m_params.clear();
        for(int i = 0; i < parameters.size(); i++) {
            KeyTypeValueTriple ktvt = (KeyTypeValueTriple)parameters.get(i);
            ParameterUnion param = new ParameterUnion();
            param._d = ktvt.value._d;
            m_params.put(ktvt.key, param);
            //logger.debug(String.format("%d: \"%s\" %s", i, ktvt.key, ParamHelper.toString(ktvt.type)));
        }
        return this;
    }

    public int size() {
        return m_params.size();
    }

    /**
     * clear current list of parameters
     */
    public ExtrasHelper clear() {
        m_params.clear();
        return this;
    }

    /**
     * Sets the values in this parameter list to those in values. 
     * @param values
     * @return this for chaining
     * @throws IllegalStateException if size of values does not match current parameter list
     */
    public ExtrasHelper set(ParameterUnionSeq values) throws IllegalStateException {
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
        
        //        if(m_params.size() != values.size()) {
        //            throw new IllegalStateException("value list (size="+values.size()+") does not match parameter list ("+m_params.size()+")");
        //        }
        //        Iterator<ParameterUnion> it = m_params.values().iterator();
        //        for(int i = 0; i < values.size(); i++) {
        //            ParameterUnion param = it.next();
        //            param.copy_from(values.get(i));
        //        }
        //        return this;
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
     * Creates a sequence of new KeyTypeValueTriples 
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
            DataType type = m_params.get(key)._d;
            KeyTypeValueTriple ktvt = new KeyTypeValueTriple();
            ktvt.key = key;
            ktvt.value._d = type;
            retVal.add(ktvt);
        }
        return retVal;
    }

    /**
     * add a new parameter to the list
     * @param name
     * @param dataType
     * @return this for chaining
     */
    public ExtrasHelper add(String name, DataType dataType) {  
        ParameterUnion param = new ParameterUnion();
        param._d = dataType;
        m_params.put(name, param);
        return this;
    }


    public ExtrasHelper set(String name, boolean value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_BOOL)) 
            param.b = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ExtrasHelper set(String name, double value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_DOUBLE)) 
            param.d = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ExtrasHelper set(String name, float value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_FLOAT)) 
            param.f = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ExtrasHelper set(String name, int value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_INT)) 
            param.i = value;
        else throw new UncheckedInvalidParameterException(name, param, value);
        return this;
    }

    public ExtrasHelper set(String name, long value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        if(param._d.equals(DataType.RAPID_LONGLONG)) 
            param.ll = value;
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
    public ExtrasHelper set(String name, String value) throws UncheckedInvalidParameterException {
        ParameterUnion param = m_params.get(name);
        if(param == null) throw new UncheckedInvalidParameterException(name);
        try {
            switch(param._d.ordinal()) {
            case DataType._RAPID_BOOL:    param.b  = Boolean.valueOf(value); break;
            case DataType._RAPID_DOUBLE:  param.d  = Double.valueOf(value);  break;
            case DataType._RAPID_FLOAT:   param.f  = Float.valueOf(value);   break;
            case DataType._RAPID_INT:     param.i  = Integer.valueOf(value); break;
            case DataType._RAPID_LONGLONG:param.ll = Long.valueOf(value); break;
            case DataType._RAPID_STRING:  param.s  = value; break;
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

    public ExtrasHelper set(String name, Vec3d value) throws UncheckedInvalidParameterException {
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
    public ExtrasHelper set(String name, double x, double y, double z) throws UncheckedInvalidParameterException {
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

    public ExtrasHelper set(String name, Mat33f value) throws UncheckedInvalidParameterException {
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

    public void print() {
        int index = 0;
        for(String key : m_params.keySet()) {
            System.out.println(String.format("%2d: %25s = %s", index, key, ParamHelper.valueString(m_params.get(key))));
            index++;
        }
    }
}
