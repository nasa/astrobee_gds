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
package gov.nasa.rapid.v2.framestore;

import java.util.HashMap;
import java.util.Set;

/**
 * Utility class to store associated pairs of Key and Value
 * Keys are a String identifier.
 * Values are of the supported type in the enumerated Type.
 * Convenience methods allow to set and get the various supported types.
 * 
 * @author Lorenzo Flueckiger
 * 
 */
public class KeyValueMap {

    protected HashMap<String, TypeValue> m_map;

    public enum Type {
        Boolean,
        Enum,
        Integer,
        Long,
        String,
        Float,
        Double,
        FloatArray,
        DoubleArray,
        Object,
        ;
    }

    protected class TypeValue {
        public final Type type;
        public Object     value;
        public TypeValue(Type type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

    public KeyValueMap() {
        // Create an empty map ready to receive few elements
        m_map = new HashMap<String, TypeValue>(5);
    }
    
    public KeyValueMap(String key, boolean val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }
    
    public KeyValueMap(String key, Enum val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, int val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, long val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, String val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, float val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, double val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, float[] val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, double[] val) {
        m_map = new HashMap<String, TypeValue>(1);
        set(key, val);
    }

    public KeyValueMap(String key, Object val) {
        m_map = new HashMap<String, TypeValue>(1);
        setObject(key, val);
    }

    public boolean contains(String key) {
        return m_map.containsKey(key);
    }

    public Set<String> keySet() {
        return m_map.keySet();
    }

    public Type getTypeOf(String key) {
        TypeValue tv = m_map.get(key);
        if(tv != null) 
            return tv.type;
        return null;
    }

    public TypeValue remove(String key) {
        return m_map.remove(key);
    }

    public void clear() {
        m_map.clear();
    }

    public Boolean getBoolean(String key) {
        return (Boolean)getObject(key);
    }

    public Enum getEnum(String key) {
        return (Enum)getObject(key);
    }

    public int getInteger(String key) {
        return (Integer)getObject(key);
    }

    public long getLong(String key) {
        return (Long)getObject(key);
    }

    public String getString(String key) {
        Object data = getObject(key);
        if ( data != null ) {
            return data.toString();
        }
        return null;
    }

    public double getDouble(String key) {
        return (Double)getObject(key);
    }

    public float getFloat(String key) {
        return (Float)m_map.get(key).value;
    }

    public double[] getDoubleArray(String key) {
        return (double[])getObject(key);
    }

    public float[] getFloatArray(String key) {
        return (float[])getObject(key);
    }

    public Object getObject(String key) {
        TypeValue tv = m_map.get(key);
        if ( tv == null ) {
            throw new IllegalArgumentException("Key [" + key + "] does not exist for KeyValueMap");
        }
        return tv.value;
    }

    public void set(String key, boolean val) {
        set(key, Type.Boolean, val);
    }

    public void set(String key, Enum val) {
        set(key, Type.Enum, val);
    }

    public void set(String key, double val) {
        set(key, Type.Double, val);
    }

    public void set(String key, float val) {
        set(key, Type.Float, val);
    }

    public void set(String key, long val) {
        set(key, Type.Long, val);
    }

    public void set(String key, int val) {
        set(key, Type.Integer, val);
    }

    public void set(String key, String val) {
        set(key, Type.String, val);
    }

    public void set(String key, double[] val) {
        set(key, Type.DoubleArray, val);
    }

    public void set(String key, float[] val) {
        set(key, Type.FloatArray, val);
    }

    public void setObject(String key, Object val) {
        set(key, Type.Object, val);
    }

    protected void set(String key, Type type, Object val) {
        TypeValue tv = m_map.get(key);
        if(tv == null) {
            m_map.put(key, new TypeValue(type, val));
        }
        else {
            if(type.equals(tv.type)) {
                tv.value = val;
            }
            else {
                throw new IllegalStateException("Types do not match: "+type.toString()+" != "+tv.type.toString());
            }
        }
    }

}
