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
package gov.nasa.arc.verve.common;

import gov.nasa.arc.verve.common.node.INodeChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;

/**
 * 
 * @author mallan
 */
public class SceneHack {
    protected static IVerveScene s_scene = null;
    protected static Set<ISceneLoadListener> s_listeners = new HashSet<ISceneLoadListener>();
    protected static List<Texture2D>          s_texUpdate = new ArrayList<Texture2D>();
    protected static HashMap<String,Runnable> s_sceneUpdateRunnables = new HashMap<String,Runnable>();
    protected static HashMap<String,INodeChangedListener> s_nodeChangedListeners = new HashMap<String,INodeChangedListener>();
    protected static CanvasRenderer           s_shareCanvasRenderer = null;
    protected static Map<Camera,Vector3>      s_targetLocation = new HashMap<Camera,Vector3>();

    // This was a hack to set the maximum shadow distance for PSSM
    // shadows. The scene needs to estimate the minimum distance to 
    // the max shadow distance in order for shadows to work well. 
    // It's not clear how to do this robustly. This needs to be done
    // per-scene draw, not for the scenario
    //public static double distance; 
    
    public static IVerveScene getMainScene() {
        return s_scene;
    }
    
    public synchronized static void setMainScene(IVerveScene scene) {
        s_scene = scene;
       	for (ISceneLoadListener listener : s_listeners){
       		listener.onSceneLoaded(s_scene);
       	}
    }

    public synchronized static void addSceneLoadListener(ISceneLoadListener listener){
        s_listeners.add(listener);
        if(s_scene != null) {
            listener.onSceneLoaded(s_scene);
        }
    }

    public synchronized static void removeSceneLoadListener(ISceneLoadListener listener){
        s_listeners.remove(listener);
    }

    public static void addSceneUpdateRunnable(String id, Runnable runnable) {
        synchronized(s_sceneUpdateRunnables) {
            s_sceneUpdateRunnables.put(id, runnable);
        }
    }
    
    public static void triggerSceneUpdate() {
        synchronized(s_sceneUpdateRunnables) {
            for(Runnable run : s_sceneUpdateRunnables.values()) {
                run.run();
            }
        }
    }
    
    public static void addNodeChangeListener(String id, INodeChangedListener listener){
    	synchronized (s_nodeChangedListeners){
    		s_nodeChangedListeners.put(id, listener);
    	}
    }

    public static void triggerNodeUpdate(Node node){
    	 synchronized(s_nodeChangedListeners) {
             for(INodeChangedListener updater : s_nodeChangedListeners.values()) {
                 updater.nodeChanged(node.getParent());
             }
         }
    	
    }
    public static void setShareCanvasRenderer(CanvasRenderer canvasRenderer) {
        s_shareCanvasRenderer = canvasRenderer;
    }

    public static CanvasRenderer getShareCanvasRenderer() {
        return s_shareCanvasRenderer;
    }

    /**
     * this is a hack to work around Ardor3d's lack of auto-update
     * on dirty texture data
     * @param tex
     */
    public static void addUpdateTexture(Texture2D tex) {
        synchronized(s_texUpdate) {
            s_texUpdate.add(tex);
        }
    }
    public static void clearUpdateTextures() {
        synchronized(s_texUpdate) {
            s_texUpdate.clear();
        }
    }
    public static Texture2D[] getUpdateTextures() {
        return s_texUpdate.toArray(new Texture2D[s_texUpdate.size()]);
    }
    public static boolean hasUpdateTextures() {
        return s_texUpdate.size() > 0;
    }
    
    public static void setTargetLocation(Camera camera, ReadOnlyVector3 location) {
        Vector3 loc = s_targetLocation.get(camera);
        if(loc == null) 
            s_targetLocation.put(camera, loc = new Vector3());
        loc.set(location);
    }
    
    public static ReadOnlyVector3 getTargetLocation(Camera camera) {
        return s_targetLocation.get(camera);
    }

    public static double getTargetDistance(Camera camera) {
        Vector3 loc = s_targetLocation.get(camera);
        if(loc == null) {
            return 50;
        }
        return camera.getLocation().distance(loc);
    }
}
