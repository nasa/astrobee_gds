package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexQuad;
import gov.nasa.arc.verve.ardor3d.scenegraph.util.SpatialPath;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public class RobotPartBreadCrumb extends AbstractRobotPart{
    protected Node          m_poseNode  = null;
    //Node          m_headNode  = null;
    protected int           m_histSize  = 50;
    //ColorRGBA     m_headClr   = null;
    double        m_resetDist = 10; // if distance between two updates is greater than this value, reset the pose history  

    protected final Transform	m_headAddXfm = new Transform();
    protected final Transform	m_flipY      = new Transform();
    protected final Transform	m_poseOffset = new Transform();

    protected Vector3       m_lastPos	= null;
    float		  m_poseDistThresh = 0.1f; // 1m 
    float         m_poseMarkerSize = 0.1f; // 0.6
    float         m_headMarkerSize = 0.25f; // 0.25

    protected int			  m_markerCount = 0;

    protected SpatialPath   m_nodePath = new SpatialPath();

    boolean       m_addPositionMarker = false;
    boolean       m_doClear = false;

    protected ArrayList<Vector3> m_pathHistory     = new ArrayList<Vector3>();
    protected int                     m_pathHistoryMax  = 20000;
    protected boolean                 m_showPathHistory = true;
    protected LinePath         		m_pathHistoryPath = null;
    protected boolean                 m_doUpdatePathHistory = false;


    public class Transforms {
        public Transform pose = new Transform();
        public Transform heading = new Transform();
    }
    protected List<Transforms> m_pendingPoses = Collections.synchronizedList(new LinkedList<Transforms>());
    protected AtomicBoolean 	 m_doReset = new AtomicBoolean(false);

    /**
     * 
     * @param partId
     * @param parent
     */
    //=========================================================================
    public RobotPartBreadCrumb(String partName, AbstractRobot parent) {
        super(partName, parent);
        m_poseOffset.setIdentity();
        //        // rotation from wheel frame to rover frame
        //        Matrix3 rot = new Matrix3( 
        //                0, 0,-1, 
        //                1, 0, 0,
        //                0,-1, 0);
        //        m_headAddXfm.setRotation(rot);
        //
        //        rot.setIdentity();
        //        rot.fromAngleNormalAxis(Math.PI, Vector3.UNIT_Y);
        //        m_flipY.setRotation(rot);
    }

    public int getHistorySize() {
        return m_histSize;
    }
    public void setHistorySize(int histSize) {
        m_histSize = histSize;
    }

    public Transform getPoseMarkerOffset() {
        return m_poseOffset;
    }

    public boolean isShowPathHistory() {
        return m_showPathHistory;
    }
    public void setShowPathHistory(boolean state) {
        m_showPathHistory = state;
        if(m_pathHistoryPath != null) {
            if(state) 
                m_pathHistoryPath.getSceneHints().setCullHint(CullHint.Inherit);
            else 
                m_pathHistoryPath.getSceneHints().setCullHint(CullHint.Always);
        }
        addPathHistorySample(null);
    }

    public int getPathHistorySize() {
        return m_pathHistoryMax;
    }
    public void setPathHistorySize(int historySize) {
        m_pathHistoryMax = historySize;
    }


    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_poseNode = new Node("Pose", Collections.synchronizedList(new LinkedList<Spatial>()));

        //m_headNode = new Node("Heading", Collections.synchronizedList(new LinkedList<Spatial>()));
        //m_headClr = new ColorRGBA(1,1,0,1);

        m_node = new Node(getPartName()+"PoseHistory");
        initRenderStates(m_node);

        m_node.attachChild(m_poseNode);
        //m_node.attachChild(m_headNode);

        m_pathHistoryPath = new LinePath("posePathHistory");
        m_node.attachChild(m_pathHistoryPath);
        //m_pathHistoryPath.setSpeed(0.5);
        m_pathHistoryPath.setDefaultColor(getAColor());
        m_pathHistoryPath.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        m_pathHistoryPath.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
        m_pathHistoryPath.setTexture(LinePath.Texture.Blank, MagnificationFilter.NearestNeighbor);
        //m_pathHistoryPath.initTexture("images/ChaseDashHalf.png", MagnificationFilter.NearestNeighbor);
        m_pathHistoryPath.setLineWidth(2);
        m_pathHistoryPath.setAntialiased(true);
        m_pathHistoryPath.setTranslation(0, 0, 0);
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setTestEnabled(true);
        m_pathHistoryPath.setRenderState(bs);
        // show or hide by default?
        setShowPathHistory(true);

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }
    
    protected ReadOnlyColorRGBA getAColor() {
    	Random r = new Random();
        ColorRGBA c = new ColorRGBA(0.5f+r.nextFloat()*0.5f,
                        0.5f+r.nextFloat()*0.5f,
                        0.5f+r.nextFloat()*0.5f,1);
    	return c;
    }

    public Transform getPoseOffset() {
        return m_poseOffset;
    }

    /**
     * 
     * @param thisPos
     * @return
     */
    protected boolean checkEuclideanDistance(ReadOnlyVector3 thisPos) {
        if(m_lastPos == null) {
            m_lastPos = new Vector3(thisPos);
            return false;
        }
        else {
            final double dist = m_lastPos.distance(thisPos);
            if(dist > m_poseDistThresh) {
                if(dist > m_resetDist) {
                    reset();
                    return false;
                }
                else {
                    m_lastPos.set(thisPos);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param pos will be copied and added to history
     */
    protected void addPathHistorySample(ReadOnlyVector3 pos) {
        if(pos != null) {
            if(m_pathHistory.size() >= m_pathHistoryMax) {
                ArrayList<Vector3> old = new ArrayList<Vector3>();
                old.ensureCapacity(m_pathHistoryMax);
                old.addAll(m_pathHistory.subList(m_pathHistoryMax/2, m_pathHistory.size()-1));
                m_pathHistory = old;
            }
            m_pathHistory.add(new Vector3(pos.getX(), pos.getY(), pos.getZ()+0.02));
        }
        if(m_showPathHistory) {
            m_pathHistoryPath.queueUpdateData(m_pathHistory);
        }
        m_doUpdatePathHistory = true;
    }

    /**
     * Create a new pose marker to attach as child 
     * @param transform
     * @return
     */
    protected Spatial newPoseMarker(Transform transform) {
        String name = String.format("Pose%04d", m_markerCount);
        TexQuad quad = new TexQuad(name, m_poseMarkerSize, true);
        quad.setDefaultColor(getRobot().getIconColor());
        quad.setTransform(transform);
        return quad;
    }

    /**
     * Create a new heading marker to attach as child 
     * @param transform
     * @return
     */
    Spatial newHeadingMarker(Transform transform) {
        String name = String.format("Heading%04d",m_markerCount);
        TexQuad quad = new TexQuad(name, m_headMarkerSize, true);
        //quad.setDefaultColor(m_headClr);
        quad.setTransform(transform);
        return quad;
    }

    protected void addPoseMarker(boolean forward) {
        //-- add to path history

        Transforms tq;
        Transform tmpXfmA = Transform.fetchTempInstance();
        Transform tmpXfmB = Transform.fetchTempInstance();
        ReadOnlyTransform robotPose = getRobot().getPoseProvider().getTransform();
        tq = new Transforms();
        robotPose.multiply(m_poseOffset, tq.pose);

        tmpXfmA = m_nodePath.getTransform(tmpXfmA);
        tmpXfmB = robotPose.multiply(tmpXfmA, tmpXfmB);
        tq.heading = tmpXfmB.multiply(m_headAddXfm, tq.heading);
        if(!forward) {
            tmpXfmA.set(tq.heading);
            tq.heading = tmpXfmA.multiply(m_flipY, tq.heading);
        }        
// xxx       m_pendingPoses.add(tq);

        addPathHistorySample(tq.pose.getTranslation());

        Transform.releaseTempInstance(tmpXfmA);
        Transform.releaseTempInstance(tmpXfmB);
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(m_doReset.get()) {
            m_lastPos = null;
            m_pendingPoses.clear();
            m_poseNode.detachAllChildren();
            //m_headNode.detachAllChildren();
            m_doReset.set(false);
        }
        else {
            Transforms tq;

            if( checkEuclideanDistance(getRobot().getPoseProvider().getXyz()) ) {
                addPoseMarker(true);
            }

            //-- update the pose history 
            if(m_poseNode != null) {
                // insert any pending pose markers
                while(m_pendingPoses.size() > 0) {
                    m_markerCount++;
                    tq = m_pendingPoses.remove(0);
                    m_poseNode.attachChildAt(newPoseMarker(tq.pose), 0);
                    //m_headNode.attachChildAt(newHeadingMarker(tq.heading), 0);
                    //                    if(false) {
                    //                        AxisRods ar = new AxisRods("heading", true, 0.5, 0.05);
                    //                        ar.setTransform(tq.heading);
                    //                        m_node.attachChild(ar);
                    //                    }
                }
                // remove extraneous markers from end of list
                int total;
                while( (total = m_poseNode.getNumberOfChildren()) > m_histSize) {
                    m_poseNode.detachChildAt(total-1);
                    //m_headNode.detachChildAt(total-1);
                }
            }
        }
        if(m_doUpdatePathHistory) {
            m_pathHistoryPath.handleUpdate(currentTime);
            m_doUpdatePathHistory = false;
        }
    }

    @Override
    public void reset() {
        m_doReset.set(true);
        m_pathHistory.clear();
        if(m_pathHistoryPath != null) {
            m_pathHistoryPath.queueUpdateData(m_pathHistory);
        }
    }

    @Override
    public void connectTelemetry() {
        // no telemetry connection required
    }

    @Override
    public void disconnectTelemetry() {
        // no telemetry connection required
    }

    //===================================================================
    protected static Texture2D    arrowTex     = null;
    protected static TextureState textureState = new TextureState();
    protected static BlendState   blendState   = new BlendState();
    protected static CullState    cullState    = new CullState();

    protected void initRenderStates(Spatial spatial) throws IllegalStateException {
        synchronized(textureState) {
            if(arrowTex == null) {
                try {
                    Texture2D texture = null;
                    URLResourceSource rs = new URLResourceSource(AbstractRobot.class.getResource("arrowRight.png"));
                    texture = (Texture2D)TextureManager.load(rs, MinificationFilter.Trilinear, false);
                    texture.setMagnificationFilter(MagnificationFilter.Bilinear);
                    texture.setAnisotropicFilterPercent(1.0f);
                    arrowTex = texture;
                    //                    arrowTex = DataBundleHelper.loadTexture("common", 
                    //                                                            "images/icon/arrowRight.png",
                    //                                                            //Texture.WrapMode.Clamp, 
                    //                                                            Texture.WrapMode.Repeat,
                    //                                                            Texture.MinificationFilter.Trilinear, 
                    //                                                            Texture.MagnificationFilter.Bilinear, 
                    //                                                            1.0f);
                } 
                catch (Throwable e) {
                    throw new IllegalStateException("Could not load texture for PoseHistory icon", e);
                }
            }
            textureState.setTexture(arrowTex, 0);
            spatial.setRenderState(textureState);

            blendState.setBlendEnabled(true);
            blendState.setTestEnabled(true);
            blendState.setTestFunction(BlendState.TestFunction.GreaterThan);
            blendState.setReference(0.4f);
            spatial.setRenderState(blendState);

            cullState.setCullFace(Face.None);
            spatial.setRenderState(cullState);

            spatial.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);
            spatial.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        }
    }
}
