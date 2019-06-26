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
package gov.nasa.arc.verve.ui3d.hud;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VerveHudRegistry {
    protected static final HashMap<String,List<VerveHud>> s_map = new HashMap<String,List<VerveHud>>();
    
    public static boolean add(String hudId, VerveHud hud) {
        List<VerveHud> list = s_map.get(hudId);
        if(list == null) {
            list = new LinkedList<VerveHud>();
            s_map.put(hudId, list);
        }
        return list.add(hud);
    }
    
    public static boolean remove(VerveHud hud) {
        final String hudId = hud.getName();
        List<VerveHud> list = s_map.get(hudId);
        if(list != null) {
            return list.remove(hud);
        }
        return false;
    }
    
    public static List<VerveHud> get(String hudId) {
        return s_map.get(hudId);
    }
    
    public static Collection<VerveHud> getAll() {
        LinkedList<VerveHud> retVal = new LinkedList<VerveHud>();
        for(List<VerveHud> huds : s_map.values()) {
            for(VerveHud hud : huds) {
                retVal.add(hud);
            }
        }
        return retVal;
    }
}
