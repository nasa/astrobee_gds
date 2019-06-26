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
package gov.nasa.arc.irg.util.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * non-ui specific utility to inform a ui that a ui target has changed,
 * e.g. a new robot has been selected and the ui should be updated to
 * monitor the new robot
 * @author mallan
 */
public class UiTarget {      
    //private final static Logger logger = Logger.getLogger(UiTarget.class);

    public final static List<WeakReference<UiTargetListener>> listeners =    new ArrayList<WeakReference<UiTargetListener>>();
    private static String s_lastTarget = "";

    public static void targetChanged(String targetName) {
        targetChanged(targetName, false);
    }
    
    public static void targetChanged(String targetName, boolean forceUpdate) {
        //logger.debug("targetChanged("+targetName+")");
        if(targetName != null) {
            int nameSep = targetName.indexOf(':');
            if(nameSep >= 0) { 
                targetName = targetName.substring(0,nameSep);
            }
            if(forceUpdate || !targetName.equals(s_lastTarget)) {
                s_lastTarget = targetName;
                List<WeakReference<UiTargetListener>> deathRow = new ArrayList<WeakReference<UiTargetListener>>();
                for(WeakReference<UiTargetListener> lr : listeners) {
                    UiTargetListener listener = lr.get();
                    if(listener != null) {
                        listener.targetChanged(targetName);
                    } else {
                        deathRow.add(lr);
                    }
                }
                for (WeakReference<UiTargetListener> lr : deathRow){
                	 listeners.remove(lr);
                }
            }
        }
    }



    public static String getLastTargetName() {
        return s_lastTarget;
    }

    public static void addListener(UiTargetListener listener) {
        listeners.add(new WeakReference<UiTargetListener>(listener));
    }

    public static void removeListener(UiTargetListener listener) {
    	List<WeakReference<UiTargetListener>> deathRow = new ArrayList<WeakReference<UiTargetListener>>();
        for(WeakReference<UiTargetListener> lr : listeners) {
            UiTargetListener l = lr.get();
            if(l != null) {
                if(l == listener) {
                    deathRow.add(lr);
                }
            }
            else {
                deathRow.add(lr);
            }
        }
        for (WeakReference<UiTargetListener> ref : deathRow){
        	listeners.remove(ref);
        }
    }
}
