package gov.nasa.arc.verve.robot.rapid.parts.concepts;

import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.utils.rapid.RapidVerve;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.nio.FloatBuffer;

import org.apache.log4j.Logger;

import rapid.PointCloudConfig;
import rapid.PointCloudSample;
import rapid.PointSample;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;

public class RapidRobotPartLineSet extends RapidRobotPart {
    private static final Logger logger = Logger.getLogger(RapidRobotPartLineSet.class);

    protected Line                m_line;
    protected float               m_zOff      = 0;
    protected float               m_lineWidth = 1;
    protected final ColorRGBA     m_clr       = new ColorRGBA(ColorRGBA.WHITE);

    protected final MessageType   m_sampleType;
    protected PointCloudSample    m_sample;
    protected PointCloudConfig    m_config;

    protected int                 m_debug     = 0;

    /** hack alert! If true, treat theta as an intensity value */
    protected boolean             m_tIsIntensity = false;

    final Vector3 m_roverXyz = new Vector3();

    public RapidRobotPartLineSet(String partName, RapidRobot parent,String participantId, MessageType sampleType, float zOffset) {
        super(partName, parent, participantId);
        m_zOff       = zOffset;
        m_sampleType = sampleType;
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[] { m_sampleType };
    }

    public void setZOffset(float zOff) {
        m_zOff = zOff;
    }
    public float getZOffset() {
        return m_zOff;
    }

    public float getLineWidth() {
        return m_lineWidth;
    }
    public void setLineWidth(float lw) {
        m_lineWidth = lw;
        if(m_line != null) {
            m_line.setLineWidth(m_lineWidth);
        }
    }

    public int getDebug() {
        return m_debug;
    }
    public void setDebug(int level) {
        m_debug = level;
    }

    public ReadOnlyColorRGBA getColor() {
        return m_clr;
    }
    public void setColor(ReadOnlyColorRGBA clr) {
        m_clr.set(clr);
        if(m_line != null) {
            m_line.setDefaultColor(clr);
        }
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName());

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setTestEnabled(true);

        m_line = new Line("line");
        m_line.getSceneHints().setCullHint(CullHint.Always);
        m_line.getMeshData().setIndexMode(IndexMode.LineStrip);
        m_line.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        m_line.setAntialiased(true);
        m_line.setDefaultColor(m_clr);
        m_line.setLineWidth(m_lineWidth);
        m_line.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
        m_line.setRenderState(bs);

        m_node.attachChild(m_line);

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    @SuppressWarnings("null")
    @Override
    public void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            setDirty(false);
            getRobot().getPoseProvider().getXyz(m_roverXyz);
            boolean zIsTheta     = true;
            //if(m_config != null) {
            //    zIsTheta = m_config.xyzMode == PointSampleXyzMode.PS_XYt;
            //}
            int     numLines     = m_sample.rowLengths.userData.size();
            int[]   indexLengths = new int[numLines];
            for(int i = 0; i < numLines; i++) {
                indexLengths[i] = m_sample.rowLengths.userData.getShort(i);
            }
            m_line.getMeshData().setIndexLengths(indexLengths);
            Vector3 scale     = RapidVerve.toArdor(m_sample.xyzScale.userData, null);
            Vector3 origin    = RapidVerve.toArdor(m_sample.origin.userData, null);
            int     required  = m_sample.points.userData.size();
            FloatBuffer verts = m_line.getMeshData().getVertexBuffer();
            FloatBuffer clrs  = m_line.getMeshData().getColorBuffer();
            if(verts == null || verts.capacity() < required*3) {
                verts = BufferUtils.createVector3Buffer(required);
                m_line.getMeshData().setVertexBuffer(verts);
                if(m_tIsIntensity) {
                    clrs = BufferUtils.createColorBuffer(required);
                    m_line.getMeshData().setColorBuffer(clrs);
                }
            }
            verts.limit(required*3).rewind();
            if(m_tIsIntensity) {
                clrs.limit(required*4).rewind();
            }
            float lastIntensity = -1;
            //logger.debug("LineSet");
            for(int i = 0; i < m_sample.points.userData.size(); i++) {
                PointSample ps  = (PointSample)m_sample.points.userData.get(i);
                verts.put(scale.getXf()*ps.xyz[0]);
                verts.put(scale.getYf()*ps.xyz[1]);
                if(zIsTheta) {
                    verts.put(0);
                    if(m_tIsIntensity) {
                        float c = scale.getYf()*ps.xyz[2];
                        if( c < 0 ) {
                            clrs.put(0.7f);
                            clrs.put(0.1f);
                            clrs.put(0.1f);
                            clrs.put(1.0f);
                        }
                        else {
                            clrs.put(c*m_clr.getRed());
                            clrs.put(c*m_clr.getGreen());
                            clrs.put(c*m_clr.getBlue());
                            clrs.put(m_clr.getAlpha());
                        }
                        if(c != lastIntensity) {
                            //logger.debug("intensity = "+c);
                            lastIntensity = c;
                        }
                    }
                }
                else {
                    verts.put(scale.getZf()*ps.xyz[2] + m_zOff);
                }
            }
            if(m_debug > 0) {
                Vector3 thisPoint = null;
                Vector3 lastPoint = null;
                int ti = 0;
                StringBuilder sb = new StringBuilder(indexLengths.length+" arc lengths: ");
                double[] lengths = new double[indexLengths.length];
                for(int idx = 0; idx < indexLengths.length; idx++) {
                    lengths[idx] = 0;
                    thisPoint = lastPoint = null;
                    for(int i = 0; i < indexLengths[idx]; i++) {
                        thisPoint = new Vector3(0,0,0);
                        PointSample ps  = (PointSample)m_sample.points.userData.get(ti);
                        thisPoint.setX(scale.getXf()*ps.xyz[0]);
                        thisPoint.setY(scale.getYf()*ps.xyz[1]);
                        if(lastPoint != null) {
                            lengths[idx] += lastPoint.distance(thisPoint);
                        }
                        lastPoint = thisPoint;
                        ti++;
                    }
                    sb.append(String.format("%.2f, ", lengths[idx]));
                }
                logger.info(sb.toString());
            }
            if(m_line.getSceneHints().getCullHint().equals(CullHint.Always)) {
                m_line.getSceneHints().setCullHint(CullHint.Inherit);
            }
            if(zIsTheta)
                m_line.setTranslation(origin.getX(), origin.getY(), m_roverXyz.getZ()+m_zOff);
            else 
                m_line.setTranslation(origin.getX(), origin.getY(), origin.getZ()+m_zOff);
        }
    }

    //int count = 0;
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        if(msgType.equals(m_sampleType)) {
            if(cfgObj != null) {
                m_config = (PointCloudConfig)cfgObj;
                if(m_debug > 1)
                    logger.debug(m_config.toString("PointCloudConfig",0));
            }
            m_sample = (PointCloudSample)msgObj;
            if(m_debug > 1) {
                logger.debug("PointCloudSample has "+m_sample.points.userData.size()+" points");
            }
            setDirty(true);
            //count++;
            //String body = String.format("%d: New arc set with %d points", count, m_sample.points.userData.size());
            //NoticeState noticeState = NoticeStateHelper.assignNoticeState(agent, "Arcs", Saliency.Urgent, "New Arc Set", body, 0, null);
            //RapidMessageCollector.instance().injectMessage(getParticipantId(), agent, MessageTypeExtTraclabs.NOTICE_STATE_TYPE, noticeState);
        }
    }

    @Override
    public void reset() {
        m_line.getSceneHints().setCullHint(CullHint.Always);
    }

}
