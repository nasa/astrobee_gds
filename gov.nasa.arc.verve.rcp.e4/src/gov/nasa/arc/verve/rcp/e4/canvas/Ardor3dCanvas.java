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
package gov.nasa.arc.verve.rcp.e4.canvas;

import gov.nasa.arc.verve.common.SceneHack;
import gov.nasa.arc.verve.common.VervePreferences;
import gov.nasa.arc.verve.common.ardor3d.framework.VerveBucketType;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;

import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.swt.SwtFocusWrapper;
import com.ardor3d.input.swt.SwtKeyboardWrapper;
import com.ardor3d.input.swt.SwtMouseWrapper;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.queue.OpaqueRenderBucket;
import com.ardor3d.renderer.queue.OrthoRenderBucket;
import com.ardor3d.renderer.queue.RenderBucket;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.queue.TransparentRenderBucket;

/**
 * 
 * @author mallan
 *
 */
public class Ardor3dCanvas extends GLCanvas  implements com.ardor3d.framework.Canvas {
    private static Logger logger = Logger.getLogger(Ardor3dCanvas.class);

    /** shared Ardor3D canvas */ 
    protected static Ardor3dCanvas  s_shareCanvas = null;
  
    
    private final CanvasRenderer m_canvasRenderer;
    private final GLData         m_glData;

    //public static final ReadOnlyColorRGBA DEFAULT_BACKGROUND_COLOR = new ColorRGBA(0.1f, 0.1f, 0.3f, 1);
    
    private static final ColorRGBA s_backgroundColor = VervePreferences.getDefaultBackgroundColor();
    //private static final ColorRGBA s_backgroundColor = new ColorRGBA(0.5f, 0.5f, 0.6f, 1);
    
    private final PhysicalLayer  m_physicalLayer;
    private final LogicalLayer   m_logicalLayer;

    private final GLCanvas       m_shareCanvas;
    private final AtomicBoolean  m_initialized = new AtomicBoolean(false);
    
    //private final ArrayList<IArdor3dCanvasListener> m_listeners = new ArrayList<IArdor3dCanvasListener>();

    private Runnable             m_onInitRunnable;

    private static LinkedList<WeakReference<Ardor3dCanvas>> s_instances = new LinkedList<WeakReference<Ardor3dCanvas>>();
    
    /**
     * 
     * @param scene
     * @param parent
     * @param style
     * @param data
     * @param onInitRunnable runnable to be called at the end of init(). May be null.
     */
    public Ardor3dCanvas(CanvasRenderer canvasRenderer, Composite parent, int style, GLData data) {
        super(parent, style, data);
        m_glData = data;
        s_instances.add(new WeakReference(this));
        
        setCurrent();
        m_canvasRenderer = canvasRenderer;
        m_shareCanvas = data.shareContext;

        //-- setup the input adapter
        m_logicalLayer  = new LogicalLayer();
        m_physicalLayer = new PhysicalLayer(
                new SwtKeyboardWrapper(this), 
                new SwtMouseWrapper(this), 
                new SwtFocusWrapper(this));
        m_logicalLayer.registerInput(this, m_physicalLayer);
    }

    /**
     * Set a runnable to be executed at the end of init()
     * If init() has already been called, execute it immediately.
     * @param r
     */
    public synchronized void setOnInitRunnable(Runnable r) {
        if(m_initialized.get()) {
            r.run();
        }
        else {
            m_onInitRunnable = r;
        }
    }

    /**
     * Draw the scenegraph
     */
    @Override
    public void draw(CountDownLatch latch) {
        if(!m_initialized.get()) {
            init();
        }
        if (!isDisposed() && isVisible()) {
            setCurrent();
            if(m_canvasRenderer.draw()) {
                swapBuffers();
            }
        }
        latch.countDown();
    }

    /**
     * @return
     */
    public PhysicalLayer getPhysicalLayer() {
        return m_physicalLayer;
    }

    /**
     * @return
     */
    public LogicalLayer getLogicalLayer() {
        return m_logicalLayer;
    }

    /**
     * @returns canvasRenderer
     */
    @Override
    public CanvasRenderer getCanvasRenderer() {
        return m_canvasRenderer;
    }

    /**
     * sets background color of this canvas
     * @param color
     */
    public void setCanvasBackgroundColor(ReadOnlyColorRGBA color) {
        if(m_initialized.get()) {
            //s_backgroundColor.set(color);
            setCurrent();
            m_canvasRenderer.makeCurrentContext();
            m_canvasRenderer.getRenderer().setBackgroundColor(color);
        }
    }

    public ReadOnlyColorRGBA getCanvasBackgroundColor() {
        return m_canvasRenderer.getRenderer().getBackgroundColor();
    }

    public static  ReadOnlyColorRGBA getDefaultBackgroundColor() {
        return s_backgroundColor;
    }

    public static  void setDefaultBackgroundColor(ReadOnlyColorRGBA color) {
        s_backgroundColor.set(color);
        Iterator<WeakReference<Ardor3dCanvas>> it = s_instances.iterator();
        while(it.hasNext()) {
            WeakReference<Ardor3dCanvas> weakRef = it.next();
            Ardor3dCanvas ref = weakRef.get();
            if(ref == null) {
                it.remove();
            }
            else if(ref.isDisposed()) {
                it.remove();
            }
            else {
                ref.setCanvasBackgroundColor(color);
            }
        }
    }

    /**
     * initialize Ardor3D rendering surface
     */
    @Override
    public synchronized void init() {
        if(!m_initialized.get()) {
            getParent().layout();
            final Point size = getSize();
            setCurrent();

            CanvasRenderer shareCanvasRenderer = null;
            if(m_shareCanvas != null && m_shareCanvas instanceof com.ardor3d.framework.Canvas) {
                com.ardor3d.framework.Canvas canvas = (com.ardor3d.framework.Canvas)m_shareCanvas;
                shareCanvasRenderer = canvas.getCanvasRenderer();
                SceneHack.setShareCanvasRenderer(shareCanvasRenderer);
            }
            else {
                SceneHack.setShareCanvasRenderer(m_canvasRenderer);
            }
            final int     width       = size.x;
            final int     height      = size.y;
            final int     colorDepth  = 0;
            final int     frequency   = 0;
            final int     alphaBits   = m_glData.alphaSize;
            final int     depthBits   = m_glData.depthSize;
            final int     stencilBits = m_glData.stencilSize;
            final int     samples     = m_glData.samples;
            final boolean fullScreen  = false;
            final boolean stereo      = false;
            final DisplaySettings settings = new DisplaySettings(width, height, 
                    colorDepth, frequency, alphaBits, depthBits, stencilBits,
                    samples, fullScreen, stereo, shareCanvasRenderer);
            m_canvasRenderer.init(settings, false);
            m_canvasRenderer.getRenderer().setBackgroundColor(s_backgroundColor);
            m_canvasRenderer.getCamera().setFrustumNear(1);
            m_canvasRenderer.getCamera().setFrustumFar(20000);
            setupBuckets(m_canvasRenderer);

            if(m_onInitRunnable != null) {
                m_onInitRunnable.run();
                m_onInitRunnable = null;
            }

            m_initialized.set(true);
        }
    }
    

    protected void setupBuckets(CanvasRenderer canvasRenderer) {
        logger.debug("setupBuckets");
        RenderBucketType[] renderBucketTypes = new RenderBucketType[9];
        RenderBucket[] buckets               = new RenderBucket[9];
        int i = -1;
        i++; renderBucketTypes[i] = VerveBucketType.PreBucket;    buckets[i] = new OpaqueRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.Shadow;       buckets[i] = new OpaqueRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.Opaque;       buckets[i] = new OpaqueRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.Transparent;  buckets[i] = new TransparentRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.Transparent1; buckets[i] = new TransparentRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.Transparent2; buckets[i] = new TransparentRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.PreOrtho;     buckets[i] = new OpaqueRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.Ortho;        buckets[i] = new OrthoRenderBucket();
        i++; renderBucketTypes[i] = VerveBucketType.PostBucket;   buckets[i] = new OpaqueRenderBucket();
        canvasRenderer.getRenderer().getQueue().setupBuckets(renderBucketTypes, buckets);
    }


    @Override
    public void dispose() {
        try {
            //logger.debug("dispose: this="+this+" m_shareCanvas="+m_shareCanvas);
        } 
        finally {
            super.dispose();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            //logger.debug("finalize: this="+this+" m_shareCanvas="+m_shareCanvas);
        } 
        finally {
            super.finalize();
        }
    }

    /**
     * used to detect when shared context is destroyed 
     */
    public class Ardor3dCanvasDisposeListener implements DisposeListener 
    {
        public void widgetDisposed(DisposeEvent e) {
            if(e.widget == s_shareCanvas) {
                //logger.debug("The primary OpenGL context has been destroyed.");
                s_shareCanvas = null;
            }
        }
    }
    
}
