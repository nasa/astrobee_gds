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
package gov.nasa.rapid.v2.e4.message.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ListenerList<E> {
    public final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ArrayList<E> listeners = new ArrayList<E>();
    
    public int size() {
        return listeners.size();
    }
    
    /**
     * only allow a listener to be inserted once
     * @param in
     * @return
     */
    public boolean add(E in) {
        if(!listeners.contains(in))
            return listeners.add(in);
        return false;
    }
    
    public boolean remove(E in) {
        return listeners.remove(in);
    }
    
    public boolean contains(E in) {
        return listeners.contains(in);
    }
    
    public Collection<E> values() {
        return listeners;
    }
}
