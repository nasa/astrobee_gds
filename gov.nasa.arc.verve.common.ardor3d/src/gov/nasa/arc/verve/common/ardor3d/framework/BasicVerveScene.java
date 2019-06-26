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
package gov.nasa.arc.verve.common.ardor3d.framework;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.common.IScreenShotDelegate;
import gov.nasa.arc.verve.common.IVerveScene;
import gov.nasa.arc.verve.common.PickInfo;
import gov.nasa.arc.verve.common.SceneHack;
import gov.nasa.arc.verve.common.ScenePickListener;
import gov.nasa.arc.verve.common.VerveDebug;
import gov.nasa.arc.verve.common.VerveDebugBoundsRenderSpatial;
import gov.nasa.arc.verve.common.VervePreferences;
import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.common.ardor3d.pssm.ParallelSplitShadowMapPass;
import gov.nasa.arc.verve.common.ardor3d.shape.CrosshairIcon;
import gov.nasa.arc.verve.common.ardor3d.shape.MeasureLine;
import gov.nasa.arc.verve.common.ardor3d.shape.PickIcon;
import gov.nasa.arc.verve.common.util.LightManager;
import gov.nasa.arc.viz.scenegraph.Selectable;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.image.Image;
import com.ardor3d.image.Texture2D;
import com.ardor3d.intersection.IntersectionRecord;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.Pickable;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.light.Light;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.RenderContext;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.StereoCamera;
import com.ardor3d.renderer.pass.BasicPassManager;
import com.ardor3d.renderer.pass.RenderPass;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.ColorMaskState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.geom.BufferUtils;
import com.ardor3d.util.geom.Debugger;

/**
 * 
 */
public final class BasicVerveScene implements IVerveScene {
    private static final Logger logger = Logger.getLogger(BasicVerveScene.class);
    protected final Node m_scenarioRoot;
    //protected final Node m_scenarioMarkup; 
    protected final Node m_markupRoot;
    protected final Node m_uiRoot;

    protected double     m_distanceToInterestPoint = 10;

    private static int s_sceneCount = 0;
    private final int  m_sceneIndex;

    private InteractManager m_interactManager = null;

    List<ScenePickListener> m_scenePickListeners = new ArrayList<ScenePickListener>();

    Spatial       m_pickedSpatial     = null;
    int           m_pickedShowFrames  = 0;
    PickData      m_lastPick          = null;

    final MeasureLine m_distLine;
    int           m_distLineShowFrames = 0;
    int           m_distLineShowFramesDefault = 30;

    boolean       m_measureMode      = false;
    boolean       m_debugShowNormals = false;
    boolean       m_shadowMapping    = false;

    long          m_currentTime;

    int           m_screenShots = 0;
    int           m_screenCount = 0;
    File          m_screenDir = new File(System.getProperty("user.home"));
    long          m_screenShotBeginTime = 0;
    int           m_screenShotTotal = 0;
    static IScreenShotDelegate s_screenShotDelegate = null;

    final Vector3 m_pickPoint = new Vector3();
    final Vector3 m_lastPoint = new Vector3();
    final Vector3 m_heightPoint = new Vector3();

    final static ArrayList<Class> s_pickIgnores = new ArrayList<Class>();

    VerveDebugBoundsRenderSpatial m_boundsIcon = new VerveDebugBoundsRenderSpatial();
    PickIcon      m_pickIcon;
    CrosshairIcon m_crosshairIcon;

    int           m_crosshairShowFrames  = 0;
    
    final String[] keywords = {"station", "segment", "Handrail", "KeepoutBox", "Quad"};

    /**
     * ctor
     * @param root
     */
    public BasicVerveScene(Node scenarioRoot) {
        this(scenarioRoot, null, null, null);
    }
    
    /**
     * ctor
     * @param root
     */
    public BasicVerveScene(Node scenarioRoot, Node scenarioMarkup) {
        this(scenarioRoot, scenarioMarkup, null, null);
    }

    /**
     * ctor
     * @param root
     */
    public BasicVerveScene(Node scenarioRoot, Node scenarioMarkup, Node uiRoot, Node markupRoot) {
        m_sceneIndex = s_sceneCount++;

        if(scenarioRoot == null) {
            m_scenarioRoot = new Node("root");
        }
        else {
            m_scenarioRoot = scenarioRoot;
        }

        if(scenarioMarkup == null) {
            //m_scenarioMarkup = new Node("markup");
        }
        else {
            //m_scenarioMarkup = scenarioMarkup;
        }

        if(uiRoot == null) {
            m_uiRoot = new Node("ui");
        }
        else {
            m_uiRoot = uiRoot;
        }

        if(markupRoot == null) {
            m_markupRoot = new Node("markup");
            BlendState bs = new BlendState();
            bs.setBlendEnabled(true);
            m_markupRoot.setRenderState(bs);
        }
        else {
            m_markupRoot = markupRoot;
        }

        m_pickIcon = new PickIcon();
        m_pickIcon.getSceneHints().setCullHint(CullHint.Always);
        m_markupRoot.attachChild(m_pickIcon);

        m_crosshairIcon = new CrosshairIcon("crosshair", 40, false);
        m_crosshairIcon.getSceneHints().setCullHint(CullHint.Always);
        m_markupRoot.attachChild(m_crosshairIcon);

        // XXX HACK XXX
        SceneHack.setMainScene(this);

        // XXX HACK XXX 
        // print out coords in rover frame
        Matrix3 rot = new Matrix3(   
                                  0,  1,  0,
                                  1,  0,  0,
                                  0,  0, -1);
        m_distLine = new MeasureLine("DistanceLine");
        m_distLine.setCoordFrame(rot);

        WireframeState ws = new WireframeState();
        ws.setAntialiased(false);
        ws.setFace(WireframeState.Face.FrontAndBack);
        ws.setEnabled(false);
        m_scenarioRoot.setRenderState(ws);
    }

    @Override
    public InteractManager getInteractManager() {
        return m_interactManager;
    }
    
    @Override
    public void setInteractManager(InteractManager mgr) {
        m_interactManager = mgr;
    }
    
    public PickIcon getPickIcon() {
        return m_pickIcon;
    }

    public VerveDebugBoundsRenderSpatial getBoundsIcon() {
        return m_boundsIcon;
    }

    @Override
    public Node getRoot() {
        return m_scenarioRoot;
    }

    @Override
    public Node getUiRoot() {
        return m_uiRoot;
    }

    @Override
    public Node getMarkupRoot() {
        return m_markupRoot;
    }

    @Override
    public boolean isMeasureMode() {
        return m_measureMode;
    }

    @Override
    public void setMeasureMode(boolean status) {
        m_measureMode = status;
        if(status) {
            m_distLine.setAlpha(1);
            m_distLine.setEndPoints(m_lastPoint, m_pickPoint);
        }
    }

    @Override
    public void toggleMeasureMode() {
        setMeasureMode(!m_measureMode);
    }

    public void toggleShowNormals() {
        m_debugShowNormals = !m_debugShowNormals;
    }

    public void toggleShadowMapping() {
        m_shadowMapping = !m_shadowMapping;
    }
    
    public void setShadowMapping(boolean state) {
        m_shadowMapping = state;
    }
    
    public boolean isShadowMapping() {
        return m_shadowMapping;
    }

    public static void addPickIgnoreClass(Class clazz) {
        if(!s_pickIgnores.contains(clazz)) {
            s_pickIgnores.add(clazz);
        }
    }

    public static void addPickIgnoreClasses(Class... clazzes) {
        for(Class clazz : clazzes) {
            if(!s_pickIgnores.contains(clazz)) {
                s_pickIgnores.add(clazz);
            }
        }
    }

    public static void setScreenShotDelegate(IScreenShotDelegate delegate) {
        s_screenShotDelegate = delegate;
    }

    @Override
    public void setScreenShot(int numShots, File directory) {
        if(directory != null) 
            m_screenDir = directory;
        m_screenShots = m_screenShotTotal = numShots;
        m_screenShotBeginTime = currentTimeMillis();
        if(numShots > 1) {
            m_screenCount = 0;
        }
    }

    boolean doShowSelectBound(Spatial spatial) {
        for(Class clazz : s_pickIgnores) {
            if(clazz.isInstance(spatial))
                return false;
        }
        return true;
    }

    /**
     * 
     * @param renderer
     */
    @SuppressWarnings("cast") // suppress stupid warning 
    public void renderDebug(final Renderer renderer) {
        if(m_pickedShowFrames > 0) {
            --m_pickedShowFrames;
            if(m_pickedSpatial != null) {
                if(doShowSelectBound(m_pickedSpatial))
                    VerveDebug.setSelectBound(m_pickedSpatial);
                m_pickIcon.getSceneHints().setCullHint(CullHint.Inherit);
            }
            if(m_pickedShowFrames == 0) {
                VerveDebug.setSelectBound(null);
                m_pickIcon.getSceneHints().setCullHint(CullHint.Always);
            }
        }

        if(m_crosshairShowFrames > 0) {
            --m_crosshairShowFrames;
            if(m_crosshairShowFrames == 0) {
                m_crosshairIcon.getSceneHints().setCullHint(CullHint.Always);
            }
        }

        m_boundsIcon.draw(renderer);
        //        if (VerveDebug.getDebugBoundList().size() > 0) {
        //            for(Spatial spatial : VerveDebug.getDebugBoundList()) {
        //                VerveDebug.drawAxis(spatial, renderer, false, true);
        //                VerveDebug.drawBounds(spatial, renderer, false);
        //            }
        //        }
        if (m_debugShowNormals) {
            Debugger.drawNormals(m_scenarioRoot, renderer);
            //Debugger.drawTangents(m_parent, renderer);
        }
        if(m_measureMode) {
            m_distLineShowFrames = m_distLineShowFramesDefault;
        }
        if(m_distLineShowFrames > 0) {
            m_distLine.draw(renderer);
            if(m_distLineShowFrames < m_distLineShowFramesDefault) {
                m_distLine.setAlpha(0.5f * (float)m_distLineShowFrames / (float)m_distLineShowFramesDefault);
            }
            --m_distLineShowFrames;
        }
    }

    /**
     * System.currentTimeMillis() is a relatively expensive call, so keep the 
     * value from the beginning of renderUnto() for use by other components
     * @return currentTimeMillis() from the beginning of the renderUnto call. 
     */
    @Override
    public long currentTimeMillis() {
        return m_currentTime;
    }

    long           lastDebugTime = System.currentTimeMillis();
    long           thisDebugTime = lastDebugTime;
    long           diffDebugTime = 5000;
    StringBuilder  stringBuilder = new StringBuilder();

    /**
     * main render call
     * 
     * 
     */
    @MainThread
    @Override
    public boolean renderUnto(final Renderer renderer) {
        long lastTime = m_currentTime;
        m_currentTime = System.currentTimeMillis();
        double elapsed = (m_currentTime-lastTime)/1000.0;

        // Execute renderQueue items
        final RenderContext renderContext = ContextManager.getCurrentContext();
        final GameTaskQueueManager gtqManager = GameTaskQueueManager.getManager(renderContext);
        gtqManager.getQueue(GameTaskQueue.RENDER).execute(renderer);

        int renderMode = m_shadowMapping ? 1 : 0;
        final Camera camera = Camera.getCurrentCamera();
        if(camera instanceof StereoCamera) {
            renderMode = 2;
        }

        switch(renderMode) {
        case 0: renderUntoNormal(camera, renderer, elapsed);         break;
        case 1: renderUntoShadows(camera, renderer, elapsed);        break;
        case 2: renderUntoStereoAnaglyph(camera, renderer, elapsed); break;
        }

        VerveTask.getRenderQueue().execute(renderer);
        
        if(m_screenShots > 0) {
            doScreenShot(renderer);
        }

        ContextGarbageCollector.doRuntimeCleanup(renderer);

        // XXX this is a hack to update GPR textures. Work with Josh to implement a proper solution
        if(SceneHack.hasUpdateTextures()) {
            Texture2D[] updateTextures = SceneHack.getUpdateTextures();
            for(Texture2D tex : updateTextures) {
                Image img = tex.getImage();
                ByteBuffer imgBuffer = img.getData(0);
                renderer.updateTexture2DSubImage(tex, 0, 0, img.getWidth(), img.getHeight(),
                                                 imgBuffer, 0, 0, img.getWidth());
            }
            SceneHack.clearUpdateTextures();
        }

        if(Constants.trackDirectMemory) {
            thisDebugTime = currentTimeMillis();
            if(thisDebugTime-lastDebugTime > diffDebugTime) {
                lastDebugTime = thisDebugTime;
                stringBuilder.setLength(0);
                BufferUtils.printCurrentDirectMemory(stringBuilder);
                logger.debug("* DirectMemory:\n"+stringBuilder.toString());
            }
        }
        return true;
    }

    /**
     * render scene for normal camera
     * @param renderer
     * @param elapsed
     * @return
     */
    public void renderUntoNormal(final Camera camera, final Renderer renderer, double elapsed) {
        m_markupRoot.updateGeometricState(elapsed, true);
        m_markupRoot.updateWorldRenderStates(true);
        m_uiRoot.updateGeometricState(elapsed, true);
        m_uiRoot.updateWorldRenderStates(true);

        try {
            renderer.draw(m_scenarioRoot);
            renderDebug(renderer);

            renderer.renderBuckets();
            
            if(m_interactManager != null) {
                m_interactManager.render(renderer);
                renderer.renderBuckets();
            }
            m_markupRoot.draw(renderer);
            m_uiRoot.draw(renderer);
            renderer.renderBuckets();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return;
    }

    //== PSSM Shadow ==========================================================
    private BasicPassManager           m_passManager;
    private ParallelSplitShadowMapPass m_pssmPass;
    private RenderPass                 m_rootPass;
    public void renderUntoShadows(final Camera camera, final Renderer renderer, double elapsed) {
        if(m_passManager == null) {
            initPssmShadow(renderer);
        }

        m_markupRoot.updateGeometricState(elapsed, true);
        m_markupRoot.updateWorldRenderStates(true);
        m_uiRoot.updateGeometricState(elapsed, true);
        m_uiRoot.updateWorldRenderStates(true);
        try {
            // FIXME TODO: better shadow frustum calc
            // this should be updated by current distance to target
            m_pssmPass.setMaxShadowDistance( SceneHack.getTargetDistance(camera)*2 );
            //m_pssmPass.setMaxShadowDistance( 100 );
            m_passManager.renderPasses(renderer);
            renderDebug(renderer);
            renderer.renderBuckets();
            if(m_interactManager != null) {
                m_interactManager.render(renderer);
                renderer.renderBuckets();
            }
            m_markupRoot.draw(renderer);
            m_uiRoot.draw(renderer);
            renderer.renderBuckets();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }


        return;
    }

    private void initPssmShadow(final Renderer renderer) 
    {
        int       mapSize     = VervePreferences.getShadowMapSize();
        int       splits      = 4;
        Light     sunLight    = LightManager.instance().getSun();
        ColorRGBA shadowColor = new ColorRGBA(0.0f, 0.0f, 0.0f, .85f);
        m_pssmPass = new ParallelSplitShadowMapPass(sunLight, mapSize, splits);
        
        m_pssmPass.setUseObjectCullFace(true);
        //m_pssmPass.setUseObjectCullFace(false);
        
        // offset used when generating shadow maps
        m_pssmPass.getShadowOffsetState().setFactor(-1.8f);
        m_pssmPass.getShadowOffsetState().setUnits(1.5f);
        // offset used during shadow overlay pass
        m_pssmPass.getShadowPassOffsetState().setFactor(-1.0f);
        m_pssmPass.getShadowPassOffsetState().setUnits(4.0f);

        m_pssmPass.setShadowColor(shadowColor);
        m_pssmPass.setUseSceneTexturing(true);
        m_pssmPass.setFiltering(ParallelSplitShadowMapPass.Filter.Pcf);
        m_pssmPass.setUpdateMainCamera(true);
        m_pssmPass.setDrawShaderDebug(false);
        //m_pssmPass.setDrawDebug(false);
        m_pssmPass.init(renderer);
        m_pssmPass.add(m_scenarioRoot);
        // occluders added through ShadowCasters singleton. Ugly, I know, but loosens coupling
        //m_pssmPass.addOccluder(m_scenarioRoot);

        m_rootPass = new RenderPass();
        m_rootPass.add(m_scenarioRoot);

        m_passManager = new BasicPassManager();
        m_passManager.add(m_rootPass);
        m_passManager.add(m_pssmPass);
    }

    //== Stereo Anaglyph ======================================================
    ColorMaskState redMask;
    ColorMaskState cyanMask;
    public void renderUntoStereoAnaglyph(final Camera camera, final Renderer renderer, double elapsed) {
        if(redMask == null) {
            redMask = new ColorMaskState();
            redMask.setAll(true);
            redMask.setBlue(false);
            redMask.setGreen(false);
            cyanMask = new ColorMaskState();
            cyanMask.setAll(true);
            cyanMask.setRed(false);
        }

        m_markupRoot.updateGeometricState(elapsed, true);
        m_markupRoot.updateWorldRenderStates(true);
        m_uiRoot.updateGeometricState(elapsed, true);
        m_uiRoot.updateWorldRenderStates(true);

        try {
            StereoCamera stereoCam = (StereoCamera)camera;
            stereoCam.updateLeftRightCameraFrames();

            //-- left
            if(true) {
                renderer.clearBuffers(Renderer.BUFFER_COLOR | Renderer.BUFFER_DEPTH);
                ContextManager.getCurrentContext().enforceState(redMask);
                stereoCam.switchToLeftCamera(renderer);

                renderer.draw(m_scenarioRoot);
                renderDebug(renderer);
                renderer.renderBuckets();
               
                if(m_interactManager != null) {
                    m_interactManager.render(renderer);   
                    renderer.renderBuckets();
                }      
            }

            //-- right
            if(true) {
                renderer.clearBuffers(Renderer.BUFFER_DEPTH);
                ContextManager.getCurrentContext().enforceState(cyanMask);
                stereoCam.switchToRightCamera(renderer);

                renderer.draw(m_scenarioRoot);
                renderDebug(renderer);
                renderer.renderBuckets();
                
                if(m_interactManager != null) {
                    m_interactManager.render(renderer);   
                    renderer.renderBuckets();
                }      
            }
            
            if(true) {
                renderer.clearBuffers(Renderer.BUFFER_DEPTH);
                ContextManager.getCurrentContext().clearEnforcedStates();
                
                m_markupRoot.draw(renderer);
                m_uiRoot.draw(renderer);
                renderer.renderBuckets();
            }

            ContextManager.getCurrentContext().clearEnforcedState(StateType.ColorMask);
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return;
    }

    @Override
    public void addScenePickListener(ScenePickListener listener) {
        m_scenePickListeners.add(listener);
    }

    @Override
    public PickResults doPick(Ray3 pickRay, int x, int y) {
        //m_crosshairIcon.setTranslation(x, y, 0);
        return doPick(pickRay);
    }
    
    @Override
    public PickResults doPick(Ray3 pickRay) {
        m_pickedSpatial = null;
        m_pickedShowFrames = 100;

        try {
            PickResults pickResults = new PrimitivePickResults();
            pickResults.setCheckDistance(true);

            PickingUtil.findPick(m_scenarioRoot, pickRay, pickResults);
            PickData relevantPick = findRelevantPick(pickResults);

            if(relevantPick != null) {
                m_lastPick = relevantPick;

                if(m_measureMode == false) {
                    for(ScenePickListener listener : m_scenePickListeners) {
                        listener.processPick(new PickInfo(relevantPick));
                    }
                }

//                // Don't put the transparent polyhedron around a selected thing
//                if(relevantPick.getTarget() instanceof Spatial) {
//                    m_pickedSpatial = (Spatial)relevantPick.getTarget();
//                }

                final IntersectionRecord ir = relevantPick.getIntersectionRecord();
                int closest = ir.getClosestIntersection();
                if( closest >= 0) {
                    m_lastPoint.set(m_pickPoint);
                    m_pickPoint.set(ir.getIntersectionPoint(closest));
                    m_pickIcon.setTranslation(m_pickPoint.getX(), m_pickPoint.getY(), m_pickPoint.getZ());
                    double dist = m_pickPoint.distance(m_lastPoint);
                    logger.debug("pick point: "+m_pickPoint);
                    logger.info("distance between last two picks was "+dist+" meters.");

                    if(m_measureMode == true) {
                        m_distLineShowFrames = m_distLineShowFramesDefault;
                        m_distLine.setEndPoints(m_lastPoint, m_pickPoint);
                        m_distLine.setAlpha(1);
                    }
                }
            }

            logger.debug("    m_pickedSpatial = "+m_pickedSpatial);
            if(m_pickedSpatial != null) {
                logger.debug("      translation = "+m_pickedSpatial.getTranslation());
                if(m_pickedSpatial instanceof Selectable) {
                    ((Selectable)m_pickedSpatial).setSelected(true);
                }
            }

            //-- only return the main pick
            pickResults.clear();
            if(relevantPick != null) {
                pickResults.addPickData(relevantPick);
            }

            return pickResults;
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    
    /** if there is a Station, Segment, handrail, or keepout zone in pick results, return that
     * Otherwise, return the closest pick.
     */
    protected PickData findRelevantPick(PickResults pickResults) {
    	 PickData closestPick = null;
    	 String   allHitsString = "";
         int numHits = pickResults.getNumber();

         for(int i = 0; i < numHits; i++) {
             PickData pick = pickResults.getPickData(i);
             Pickable pickable = pick.getTarget();
             if(pickable instanceof Spatial) {
                 Spatial target = (Spatial)pickable;
                 
                 boolean isVisible = !target.getSceneHints().getCullHint().equals(CullHint.Always);
                 
                 if(targetNameStartsWithKeyword(target.getName())) {
                	 return pick;
                 }
                 
                 if(isVisible) {
                     String name = null;
                     name = target.getName();
                     if(name == null) {
                         name = "["+pickable.getClass().getSimpleName()+"]";
                     }
                     allHitsString = allHitsString + "("+ i + " " + name + "[";
                     IntersectionRecord record = pick.getIntersectionRecord();
                     double dist = Double.NaN;
                     if(record != null) {
                         dist = record.getClosestDistance();
                         if(closestPick == null) {
                             if(dist < Double.POSITIVE_INFINITY) {
                                 closestPick = pick;
                             }
                         }
                     }
                     allHitsString = allHitsString + String.format("%.2f", dist);
                     allHitsString = allHitsString + "], ";
                     
                    
                 }
             }
         }
         return closestPick;
    }
    
    
    /** returns true if name is of something we care about
     */
    protected boolean targetNameStartsWithKeyword(String name) {
    	for(int i=0; i<keywords.length; i++) {
    		if(name.startsWith(keywords[i])) {
        		return true;
        	}
    	}
    	return false;
    }

    public static final String SHOTFILENAME = "verve-v%02dimg%06d";
    public static final String SHOTEXAMPLE  = "verve-v%02dimg%%06d.%s";
    public static final String SHOTFILEEXT  = "jpg";
    protected synchronized void doScreenShot(Renderer renderer) {
        if(s_screenShotDelegate == null) {
            logger.warn("No ScreenshotDelegate has been set");
            m_screenShots = 0;
        }
        else {
            String basename = String.format("verve-v%02dimg%06d", m_sceneIndex, m_screenCount);
            try {
                s_screenShotDelegate.doScreenShot(renderer, m_screenDir, basename, SHOTFILEEXT);
            }
            catch(Throwable t) {
                logger.error("Error taking screen shot", t);
            }
            m_screenShots--;
            if(m_screenShots == 0) {
                logger.info("Screen capture complete. Files are in "+m_screenDir.getAbsolutePath());
                if(m_screenShotTotal > 2) {
                    double seconds = (currentTimeMillis()-m_screenShotBeginTime)/1000.0;
                    int fps = (int)((m_screenShotTotal/seconds)+0.5);
                    String shotString = String.format(SHOTEXAMPLE, m_sceneIndex, SHOTFILEEXT);
                    System.err.println("******************************************************");
                    System.err.println("** If you have ffmpeg installed, you can            **");
                    System.err.println("** create a video by running the following commands **");
                    System.err.println("******************************************************");
                    System.err.println("  cd "+m_screenDir.getAbsolutePath());
                    System.err.println("  ffmpeg -r "+fps+" -vframes "+m_screenShotTotal+" -b 2111000 -i \""+shotString+"\" -y verve-movie.mp4");
                    System.err.println("");
                }
            }
            m_screenCount++;
        }
    }

    @Override
    public void showCrosshair(int x, int y, int numFrames) {
        m_crosshairShowFrames = numFrames;
        if(m_crosshairShowFrames < 1) {
            m_crosshairShowFrames = 1;
        }
        m_crosshairIcon.getSceneHints().setCullHint(CullHint.Never);
        m_crosshairIcon.setTranslation(x, y, 0);
    }
}
