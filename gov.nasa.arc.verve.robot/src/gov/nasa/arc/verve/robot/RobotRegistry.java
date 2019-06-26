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
package gov.nasa.arc.verve.robot;

import gov.nasa.arc.verve.robot.exception.UncheckedRobotException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * The RobotRegistry maintains a collection of robots that are active
 * in the scene. Robots should be registered after initialization. The
 * registry holds weak references to RobotRegistryListener instances.
 * @author mallan
 */
public class RobotRegistry {
    private static final Logger logger = Logger.getLogger(RobotRegistry.class);

    private static final HashMap<String,AbstractRobot> s_map = new HashMap<String,AbstractRobot>();
    private static final ArrayList<WeakReference<RobotRegistryListener>> s_listeners = new ArrayList<WeakReference<RobotRegistryListener>>();

    public static AbstractRobot get(String robotName) {
        return s_map.get(robotName);
    }

    public static String[] getRegisteredRobots() {
        String[] retVal = new String[s_map.size()];
        return s_map.keySet().toArray(retVal);
    }

    public static void clear() {
        String[] keys = s_map.keySet().toArray(new String[s_map.size()]);
        for(String key : keys) {
            try {
                AbstractRobot robot = s_map.get(key);
                deregister(key, robot);
            }
            catch(Throwable t) {
                logger.warn(t.getMessage());
            }
        }
    }

    /**
     * 
     * @param robotName
     * @param robot
     * @throws UncheckedRobotException if a robot with the same name already exists in registry
     */
    public static synchronized void register(String robotName, AbstractRobot robot) throws UncheckedRobotException {
        if(s_map.containsKey(robotName)) {
            throw new UncheckedRobotException("Robot with the id \""+robotName+"\" already exists in the registry.");
        }
        s_map.put(robotName, robot);
        for(WeakReference<RobotRegistryListener> ref : s_listeners) {
            RobotRegistryListener listener = ref.get();
            if(listener != null) {
                listener.robotRegistered(robotName, robot);
            }
        }
    }

    /**
     * Deregister robot. Listeners will be called before robot is removed from map
     * @param name
     * @param robot
     * @throws UncheckedRobotException
     */
    public static synchronized void deregister(String robotName, AbstractRobot robot) throws UncheckedRobotException {
        if(!s_map.containsKey(robotName)) {
            throw new UncheckedRobotException("Attempted to deregister \""+robotName+"\", but it does not exist in the registry.");
        }
        if(s_map.get(robotName) == robot) {
            for(WeakReference<RobotRegistryListener> ref : s_listeners) {
                RobotRegistryListener listener = ref.get();
                if(listener != null) {
                    listener.robotDeregistered(robotName, robot);
                }
            }
            s_map.remove(robotName);
        }
        else {
            throw new UncheckedRobotException("Attempted to deregister \""+robotName+"\", but object does not match.");
        }
    }

    /**
     * Add WeakReference to listener. <br><b>The listener's robotRegistered method 
     * will be immediately invoked for each robot that is already registered.</b>
     * Remember that the RobotRegistry holds WeakReferences to the listeners,
     * so simply adding a listener to the RobotRegistry will <b>not</b> prevent it 
     * from being garbage collected. 
     * @param listener
     */
    public static synchronized void addListener(RobotRegistryListener listener) {
        s_listeners.add(new WeakReference(listener));
        String[] robotNames = getRegisteredRobots();
        for(String robotName : robotNames) {
            listener.robotRegistered(robotName, s_map.get(robotName));
        }
    }
    public static synchronized void removeListener(RobotRegistryListener listener) {
        ArrayList<WeakReference<RobotRegistryListener>> remove = new  ArrayList<WeakReference<RobotRegistryListener>>();
        for(WeakReference<RobotRegistryListener> ref : s_listeners) {
            RobotRegistryListener l = ref.get();
            if(listener  == l || l == null) {
                remove.add(ref);
            }
        }
        s_listeners.removeAll(remove);
    }
}
