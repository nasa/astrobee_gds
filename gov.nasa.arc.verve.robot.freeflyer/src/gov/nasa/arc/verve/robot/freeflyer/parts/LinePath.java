package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPathController;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector4;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.geom.BufferUtils;
import com.ardor3d.util.resource.URLResourceSource;

/**
 * A single Line strip with shifting texture matrix 
 * to indicate direction of motion. The texture coordinates
 * repeat every meter. 
 * @author mallan
 */
public class LinePath extends Line {
    //private static final  Logger logger = Logger.getLogger(DirectionalPath.class);
    private static int	  s_instance = 0;
    private final int     m_instance;

    IntBuffer             m_idxBuffer  = null;
    FloatBuffer           m_vtxBuffer  = null;
    FloatBuffer           m_texBuffer  = null;
    FloatBuffer           m_clrBuffer  = null;
    FloatBufferData		  m_texCoords  = null;

    boolean               m_useColors  = false;
    int					  m_numVerts   = 0;
    Texture2D             m_tex        = null;
    Matrix4               m_texMatrix  = new Matrix4();
    Vector4               m_texOffset  = new Vector4(0,0,0,1);
    double                m_speedMps   = 0.5;
    int                   m_texOffsetSpeed = (int)(1000 / m_speedMps);

    final Object		  m_queueLock    = new Object();
    final LinkedList<ReadOnlyVector3>   m_queuedVerts  = new LinkedList<ReadOnlyVector3>();
    final LinkedList<ReadOnlyColorRGBA> m_queuedColors = new LinkedList<ReadOnlyColorRGBA>();
    boolean				  m_dirty		 = false;

    LinePathController m_controller = new LinePathController();

    public enum Texture {
    	Blank,
        ChaseDash,
        ChaseDashDot,
        ChaseDashHalf,
        ChaseDotDot,
        ChaseDotDotAlpha,
        ChaseDotDotGrey,
        ChaseDotDotPale,
        ChaseLight;

        Texture2D texture = null;
    }

    public LinePath(String name) {
        super(name);
        m_instance = s_instance++;
        setName((name != null) ? name : this.getClass().getSimpleName()+m_instance);

        m_numVerts = 0;
        setLineWidth(2);
        setAntialiased(true);
        setDefaultColor(ColorRGBA.WHITE);
        
//		MaterialState mtl;
//		ReadOnlyColorRGBA col = getAColor();
//		mtl = new MaterialState();
//		mtl.setShininess(5); // was 5
//		mtl.setDiffuse (col);
//		mtl.setSpecular(col);
//		mtl.setEmissive(col);
//		mtl.setAmbient (col);
//
//        setRenderState(mtl);
        
        getMeshData().setIndexMode(IndexMode.LineStrip);
        getSceneHints().setLightCombineMode(LightCombineMode.Inherit);

        getSceneHints().setAllPickingHints(false);
        setModelBound(new BoundingBox());
        setTexture(Texture.ChaseLight);

        addController(m_controller);
    }

    protected ReadOnlyColorRGBA getAColor() {
    	Random r = new Random();
    	 ColorRGBA c = new ColorRGBA(0,0,0,1);
//        ColorRGBA c = new ColorRGBA(0.5f+r.nextFloat()*0.5f,
//                        0.5f+r.nextFloat()*0.5f,
//                        0.5f+r.nextFloat()*0.5f,1);
    	return c;
    }

    public void setTexture(Texture chaseTex) {
        setTexture(getTexture(chaseTex, MagnificationFilter.Bilinear));
    }

    public void setTexture(Texture chaseTex, MagnificationFilter magFilter) {
        setTexture(getTexture(chaseTex, magFilter));
    }

    protected Texture2D getTexture(Texture tex, MagnificationFilter magFilter) {
        if(tex.texture == null) {
            final String imageFilename = tex.name()+".png";
            URLResourceSource rs;
            rs = new URLResourceSource(AbstractRobot.class.getResource(imageFilename));
            tex.texture = (Texture2D)TextureManager.load(rs, MinificationFilter.BilinearNearestMipMap, false);
            tex.texture.setWrap(WrapMode.Repeat);
            tex.texture.setMagnificationFilter(magFilter);
            tex.texture.setAnisotropicFilterPercent(1.0f);
        }
        return tex.texture;
    }

    public boolean isDirty() {
        return m_dirty;
    }
    public void setDirty(boolean state) {
        m_dirty = state;
    }

    public boolean isUseColors() {
        return m_useColors;
    }
    public void setUseColors(boolean state) {
        m_useColors = state;
    }

    @Override
    public void draw(Renderer r) {
        if(m_numVerts > 1) // XXX HACK XXX
            super.draw(r);
    }

    public Texture2D getTexture() {
        return m_tex;
    }
//
    public void setTexture(Texture2D t) {
        m_tex = t;
        TextureState ts = new TextureState();
        ts.setTexture(m_tex, 0);
        ts.setEnabled(true);
        setRenderState(ts);
    }


    /**
     * provide (unsafe) access to internal queued data list
     * @return
     */
    public LinkedList<ReadOnlyVector3> getQueuedData() {
        return m_queuedVerts;
    }

    /**
     * provide (unsafe) access to internal queued colors list
     * @return
     */
    public LinkedList<ReadOnlyColorRGBA> getQueuedColors() {
        return m_queuedColors;
    }

    /**
     * copies Vector3 objects from verts to internal list. 
     * @param verts
     */
    public void queueUpdateData(List<? extends ReadOnlyVector3> verts) {
        queueUpdateData(verts, false);
    }

    /**
     * moves Vector3 objects from verts to internal list. 
     * @param verts
     */
    public void queueUpdateData(List<? extends ReadOnlyVector3> verts, boolean reverseOrder) {
        synchronized(m_queueLock) {
            m_queuedVerts.clear();
            if(verts.size() > 0) {
                ListIterator<? extends ReadOnlyVector3> iterator;
                if(reverseOrder) {
                    iterator = verts.listIterator(verts.size()-1);
                    while(iterator.hasPrevious()) {
                        m_queuedVerts.add(iterator.previous());
                    }
                }
                else {
                    for(ReadOnlyVector3 vert : verts) {
                        m_queuedVerts.add(vert);
                    }
                }
            }
            m_dirty = true;
        }
    }

    /**
     * moves Vector3 objects from verts to internal list. 
     * @param verts
     */
    public void queueUpdateData(List<? extends ReadOnlyVector3> verts, List<ReadOnlyColorRGBA> colors, boolean reverseOrder) {
        synchronized(m_queueLock) {
            m_queuedVerts.clear();
            m_queuedColors.clear();
            if(verts.size() > 0) {
                if(reverseOrder) {
                    ListIterator<? extends ReadOnlyVector3> vertIt;
                    ListIterator<ReadOnlyColorRGBA> colorIt;
                    vertIt  = verts.listIterator(verts.size()-1);
                    while(vertIt.hasPrevious()) {
                        m_queuedVerts.add(vertIt.previous());
                    }
                    colorIt = colors.listIterator(colors.size()-1);
                    while(colorIt.hasPrevious()) {
                        m_queuedColors.add(colorIt.previous());
                    }
                }
                else {
                    for(ReadOnlyVector3 vert : verts) {
                        m_queuedVerts.add(vert);
                    }
                    for(ReadOnlyColorRGBA clr : colors) {
                        m_queuedColors.add(clr);
                    }
                }
            }
            m_dirty = true;
        }
    }

    public void handleUpdate(long time) {
        m_texOffset.setY( -((double)(time%m_texOffsetSpeed))/m_texOffsetSpeed );
        m_texMatrix.setRow(3, m_texOffset);

        if(m_tex != null) {
            m_tex.setTextureMatrix(m_texMatrix);
        }
        if(m_dirty) {
            synchronized(m_queueLock) {
                updateData(m_queuedVerts, m_queuedColors);
                m_queuedVerts.clear();
                m_queuedColors.clear();
                m_dirty = false;
            }
        }
    }

    /**
     * This must be called from the scenegraph update thread
     */
    public void updateData(List<? extends ReadOnlyVector3> verts, List<ReadOnlyColorRGBA> colors) {
        int numVerts = verts.size();
        checkBuffers(numVerts);
        float dist = 0;
        int i = 0;
        ReadOnlyVector3 l = null;
        for(ReadOnlyVector3 v : verts) {
            m_idxBuffer.put(i);
            m_vtxBuffer.put(v.getXf());
            m_vtxBuffer.put(v.getYf());
            m_vtxBuffer.put(v.getZf());
            if(l != null) {
                float a = v.getXf()-l.getXf();
                float b = v.getYf()-l.getYf();
                float c = v.getZf()-l.getZf();
                float d = (float)Math.sqrt(a*a+b*b+c*c);
                dist += d;
            }
            m_texBuffer.put(0.5f).put(dist);
            l = v;
            i++;
        }
        m_numVerts = numVerts;
        getMeshData().updateVertexCount();
        updateModelBound();
        if(m_useColors) {
            for(ReadOnlyColorRGBA c : colors) {
                m_clrBuffer.put(c.getRed()).put(c.getGreen()).put(c.getBlue()).put(c.getAlpha());
            }
        }
    }

    //    /**
    //     * Update vertex data from vData array
    //     * This must be called from the scenegraph update thread
    //     * @param verts a List of Vector3
    //     */
    //    public void updateData(List<Vector3> verts) {
    //        int numVerts = verts.size();
    //        checkBuffers(numVerts);
    //        float dist = 0;
    //        int i = 0;
    //        ReadOnlyVector3 l = null;
    //        for(ReadOnlyVector3 v : verts) {
    //            m_idxBuffer.put(i);
    //            m_vtxBuffer.put(v.getXf());
    //            m_vtxBuffer.put(v.getYf());
    //            m_vtxBuffer.put(v.getZf());
    //            if(l != null) {
    //                float a = v.getXf()-l.getXf();
    //                float b = v.getYf()-l.getYf();
    //                float c = v.getZf()-l.getZf();
    //                float d = (float)Math.sqrt(a*a+b*b+c*c);
    //                dist += d;
    //            }
    //            m_texBuffer.put(0.5f).put(dist);
    //            l = v;
    //            i++;
    //        }
    //        m_numVerts = numVerts;
    //        getMeshData().updateVertexCount();
    //        updateModelBound();
    //    }


    /**
     * Update vertex data from vData array
     * This must be called from the scenegraph update thread
     * @param numVerts number of vertices contained in array
     * @param vData array of vertex data
     */
    public void updateData(int numVerts, float[] vData, float off[]) {
        checkBuffers(numVerts);
        float dist = 0;
        int idx = 0;
        for(int i = 0; i < numVerts; i++) {
            idx = i*3;
            m_idxBuffer.put(i);
            m_vtxBuffer.put(vData[idx+0] + off[0]);
            m_vtxBuffer.put(vData[idx+1] + off[1]);
            m_vtxBuffer.put(vData[idx+2] + off[2]);
            if(i > 0) {
                float a = vData[idx+0]-vData[idx-3];
                float b = vData[idx+1]-vData[idx-2];
                float c = vData[idx+2]-vData[idx-1];
                float d = (float)Math.sqrt(a*a+b*b+c*c);
                dist += d;
            }
            m_texBuffer.put(0.5f).put(dist);
        }
        m_numVerts = numVerts;
        getMeshData().updateVertexCount();
    }

    protected void checkBuffers(int nVerts) {
        final int chunkSize = 32;
        final int chunks = 1 + (nVerts / chunkSize);
        final int required = chunks * chunkSize;
        if (m_idxBuffer == null || m_idxBuffer.capacity() < required) {
            m_vtxBuffer = BufferUtils.createVector3Buffer(required);
            m_texBuffer = BufferUtils.createVector2Buffer(required);
            m_idxBuffer = BufferUtils.createIntBuffer(required);
            m_texCoords = new FloatBufferData(m_texBuffer,2);
            getMeshData().setVertexBuffer(m_vtxBuffer);
            getMeshData().setIndexBuffer (m_idxBuffer);
            getMeshData().setTextureCoords(m_texCoords, 0);
        }
        m_vtxBuffer.limit(nVerts * 3).rewind();
        m_texBuffer.limit(nVerts * 2).rewind();
        m_idxBuffer.limit(nVerts).rewind();
        if(m_useColors) {
            if(m_clrBuffer == null || m_clrBuffer.capacity() < 4*required) {
                m_clrBuffer = BufferUtils.createColorBuffer(required);
                getMeshData().setColorBuffer(m_clrBuffer);
            }
            m_clrBuffer.limit(nVerts * 4).rewind();
        }
    }
}
