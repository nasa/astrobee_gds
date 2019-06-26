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
package gov.nasa.dds.rti.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.rti.dds.infrastructure.PropertySeq;
import com.rti.dds.infrastructure.Property_t;

/**
 * PropertyQosStore holds properties for use in the participant's PropertyQos
 * @author mallan
 *
 */
public class PropertyQosStore {
    
    protected HashMap<String,String>  m_propertyMap  = new HashMap<String,String>();
    protected HashMap<String,Boolean> m_propagateMap = new HashMap<String,Boolean>();
    
    /**
     * Initialize property store from a PropertySeq, which can be 
     * obtained through: 
     * ParticipantQos.property.value
     * @param propertySequence
     */
    public PropertyQosStore(PropertySeq propertySequence) {
        set(propertySequence);
    }
    
    /**
     * @return sorted array of keys
     */
    public String[] keys() {
        int len = m_propertyMap.keySet().size();
        String[] retVal = m_propertyMap.keySet().toArray(new String[len]);
        Arrays.sort(retVal);
        return retVal;
    }
    
    public String get(String key) {
        return m_propertyMap.get(key);
    }
    
    public boolean isPropagate(String key) {
        return m_propagateMap.get(key);
    }
    
    /**
     * clears the map, then adds all properties in the sequence
     * @param propertySequence
     */
    public void set(PropertySeq propertySequence) {
        clear();
        if(propertySequence != null)
            add(propertySequence);
    }
    
    /**
     * adds all properties in the sequence to the map
     * @param propertySequence
     */
    public void add(PropertySeq propertySequence) {
        Iterator<Property_t> it = propertySequence.iterator();
        while(it.hasNext()) {
            Property_t property = it.next();
            m_propertyMap.put(property.name, property.value);
            m_propagateMap.put(property.name, property.propagate);
        }
    }
    
    /**
     * put property into map, propagate defaults to false
     * @param name
     * @param value
     */
    public void put(String name, String value) {
        put(name, value, false);
    }
    
    public void put(String name, Integer value) {
        put(name, value.toString(), false);
    }
    
    /**
     * put property into map
     * @param name
     * @param value
     * @param propagate
     */
    public void put(String name, String value, boolean propagate) {
        m_propertyMap.put(name, value);
        m_propagateMap.put(name, propagate);
    }
    
    /**
     * clear map
     */
    public void clear() {
        m_propertyMap.clear();
        m_propagateMap.clear();
    }
    
    /**
     * return the cumulative string length of all keys and values
     * @return
     */
    public int calculateCumulativeSize() {
        int total = 0;
        for(final String key : m_propertyMap.keySet()) {
            final String val = m_propertyMap.get(key);
            total += key.length();
            total += val.length();
        }
        return total;
    }

    /**
     * sets the values in this store into propertySequence
     * @param propertySequence if null, a new PropertySeq will be created
     * @return propertySequence with new values
     */
    public PropertySeq assign(PropertySeq propertySequence) {
        if(propertySequence == null) {
            propertySequence = new PropertySeq();
        }
        propertySequence.clear();
        for(String key : keys()) {
            Property_t item = new Property_t(key, get(key), isPropagate(key));
            propertySequence.add(item);
        }
        return propertySequence;
    }
}
