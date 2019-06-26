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
package gov.nasa.arc.verve.robot.rapid.parts.sensors;

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.ardor3d.scenegraph.shape.AxisLines;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.sensors.PointCloud;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.NotSubscribedException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import org.apache.log4j.Logger;

import rapid.ext.RangeScanConfig;
import rapid.ext.RangeScanSample;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Sphere;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartRangeScan extends RapidRobotPart  {
    private static Logger logger = Logger.getLogger(RapidRobotPartRangeScan.class);

    protected class SampleHolder {
        public SampleHolder(Object rsSample, Object rsConfig) {
            sample = (RangeScanSample)rsSample;
            config = (RangeScanConfig)rsConfig;
        }
        public SampleHolder(Object rsSample, Object rsConfig, ReadOnlyTransform roXfm) {
            sample = (RangeScanSample)rsSample;
            config = (RangeScanConfig)rsConfig;
            xfm.set(roXfm);
        }
        public RangeScanSample sample;
        public RangeScanConfig config;
        public Transform xfm = new Transform();
    }
    protected final Deque<SampleHolder> m_data      = new ArrayDeque<SampleHolder>();
    protected int               m_historySize       = 200;
    protected int               m_historyIndex      = 0;
    protected PointCloud[]      m_rangeScans        = new PointCloud[m_historySize];
    protected float             m_pointSize         = 2.0f;
    protected Random            m_rand              = new Random();
    protected float             m_minRange          = 0;
    protected ColorRGBA         m_color             = new ColorRGBA(1,1,1,1);
    protected String            m_frameName         = null;
    protected Transform         m_frameOffset       = new Transform();
    protected boolean           m_gotFrameOffset    = false;

    protected float             m_intnsMin   = 0;
    protected float             m_intnsMax   = 255; 
    protected short[]           m_scanAz = new short[0];
    protected short[]           m_scanEl = new short[0];

    protected Matrix3           t_azRot = new Matrix3();
    protected Matrix3           t_elRot = new Matrix3();
    protected Matrix3           t_rot = new Matrix3();
    protected Vector3           t_vec = new Vector3();

    protected Sphere            m_sphere;
    protected AxisLines         m_axis;
    protected boolean           m_showAxis = false;

    protected ColorRGBA[]       m_scanColorArray = null;


    enum PointColorMode {
        Intensity,
        NoIntensity,
        PerRowColor,
        PerColColor
    }
    protected PointColorMode m_pointColorMode = PointColorMode.Intensity;

    protected final MessageType SAMPLE_TYPE;
    protected final MessageType CONFIG_TYPE;

    /**
     * 
     * @param partName
     * @param parent
     * @param participantId
     * @param msgType
     * @param sensorFrameName frame name of velodyne in FrameStore
     */
    public RapidRobotPartRangeScan(String partName, RapidRobot parent, String participantId, MessageType msgType, String sensorFrameName) {
        super(partName, parent, participantId);
        SAMPLE_TYPE = msgType;
        CONFIG_TYPE = MessageType.valueOf(SAMPLE_TYPE.getConfigName());
        m_frameName = sensorFrameName;
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName());

        m_sphere = new Sphere("sphere", 6, 12, 0.1);
        m_axis   = new AxisLines("axis", 0.3f);

        m_sphere.setTransform(getRobot().getPoseProvider().getTransform().multiply(m_frameOffset, null));
        m_axis.setTransform(getRobot().getPoseProvider().getTransform().multiply(m_frameOffset, null));

        setShowFrame(m_showAxis);

        m_node.attachChild(m_sphere);
        m_node.attachChild(m_axis);

        getRobot().getRobotNode().getSensorsNode().attachChild(m_node);
        allocateRangeScans(m_historySize, m_node, null);

        if(m_frameName != null) {
            updateSensorOffsetTransform(m_frameName, m_frameOffset);
        }
    }

    public boolean isShowFrame() {
        return m_showAxis;
    }

    public void setShowFrame(boolean state) {
        m_showAxis = state;
        if(m_axis != null) {
            if(m_showAxis) {
                m_axis.getSceneHints().setCullHint(CullHint.Inherit);
                m_sphere.getSceneHints().setCullHint(CullHint.Inherit);
            }
            else {
                m_axis.getSceneHints().setCullHint(CullHint.Always);
                m_sphere.getSceneHints().setCullHint(CullHint.Always);
            }
        }
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[] { SAMPLE_TYPE };
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType type, Object eventObj, Object configObj) {
        //logger.debug(this.getClass().getSimpleName()+".rapidEventReceived : "+type.toString());
        if(isVisible() && type.equals(SAMPLE_TYPE)) {
            synchronized(m_data) {
                SampleHolder holder = new SampleHolder(eventObj, configObj);
                getRobot().getPoseProvider().getTransform().multiply(m_frameOffset, holder.xfm);
                m_data.add(holder);
                setDirty(true);
            }
        }
    }

    public PointColorMode getPointColorMode() {
        return m_pointColorMode;
    }

    public void setPointColorMode(PointColorMode newMode) {
        m_pointColorMode = newMode;
        m_scanColorArray = null;
    }

    public int getHistorySize() {
        return m_historySize;
    }

    public synchronized void setHistorySize(int historySize) {
        PointCloud[] oldScans = null;

        if(m_node != null) { // create array to hold old PointClouds
            int oldSize  = m_historySize;
            int startIdx = (m_historyIndex+1)%m_historySize;
            oldScans = new PointCloud[oldSize];
            for(int i = 0; i < m_historySize; i++) {
                oldScans[i] = m_rangeScans[(startIdx+i)%oldSize];
            }
        }

        m_historyIndex = 0;
        if(historySize < 1) {
            historySize = 1;
        }
        m_historySize  = historySize;

        if(m_node != null) {
            m_node.detachAllChildren();
            m_node.attachChild(m_axis);
            m_node.attachChild(m_sphere);
            allocateRangeScans(m_historySize, m_node, oldScans);
        }
    }

    private void allocateRangeScans(int historySize, Node node, PointCloud[] oldScans) {
        m_rangeScans  = new PointCloud[historySize];
        int newNeeded = historySize;
        if(oldScans != null) {
            newNeeded = historySize - oldScans.length;
            for(int i = 0; i < oldScans.length; i++) {
                m_rangeScans[i%historySize] = oldScans[i];
            }
            m_historyIndex = oldScans.length%historySize;
        }
        // allocate new PointClouds, if necessary
        if(newNeeded > 0) {
            for(int i = 0; i < newNeeded; i++) {
                int idx = (m_historyIndex+i)%historySize;
                m_rangeScans[idx] = new PointCloud("PointCloud");
                m_rangeScans[idx].setPointSize(m_pointSize);
                m_rangeScans[idx].setUseVertexColors(true);
            }
        }
        else if(newNeeded < -historySize/2) {
            System.gc();
        }

        // standardize names and attach to node
        for(int i = 0; i < historySize; i++) {
            m_rangeScans[i].setName(String.format("%s.RangeScan%03d", getPartName(), i));
            node.attachChildAt(m_rangeScans[i], i);
        }
    }


    public float getPointSize() {
        return m_pointSize;
    }

    public void setPointSize(float pointSize) {
        m_pointSize = pointSize;
        for(PointCloud pc : m_rangeScans) {
            pc.setPointSize(m_pointSize);
        }
    }

    public ReadOnlyColorRGBA getColor() {
        return m_color;
    }

    public void setColor(ReadOnlyColorRGBA clr) {
        m_color.set(clr);
    }


    /**
     * 
     * @param cloud
     * @param robotXfm transform of robot at time data sample was received
     */
    public void updateTransform(PointCloud cloud, ReadOnlyTransform robotXfm) {
        cloud.setTransform(robotXfm);
    }

    int d_noConfig = 0;
    int d_write = 0;
    static final int BBSS = 13; // bounding box sub sample
    /**
     * Update point cloud history if we have new telemetry. The point 
     * cloud bounding boxes are approximate (only a subset of the points are used)
     */
    @Override
    public synchronized void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            synchronized(m_data) {
                try {
                    while(m_data.size() > 0) {
                        SampleHolder holder = m_data.removeFirst();
                        RangeScanSample sample = holder.sample;
                        RangeScanConfig config = holder.config;

                        if(!m_gotFrameOffset) {
                            m_gotFrameOffset = updateSensorOffsetTransform(m_frameName, m_frameOffset);
                        }

                        if(config == null) { // make sure we have a config
                            try {
                                final String participant = getParticipantId();
                                final Agent agent = getRapidRobot().getAgent();
                                config = (RangeScanConfig)RapidMessageCollector.instance().getLastMessage(participant, agent, CONFIG_TYPE);
                                if(config == null) {
                                    if(++d_noConfig % 50 == 1) {
                                        logger.debug("received "+d_noConfig+" RangeScanSamples with no Config");
                                    }
                                    setDirty(false);
                                    continue;
                                }
                            }
                            catch(NotSubscribedException e) {
                                logger.debug(e.getMessage());
                                setDirty(false);
                                continue;
                            }
                        }

                        PointCloud rangeScan = m_rangeScans[m_historyIndex];
                        m_historyIndex = (m_historyIndex+1)%m_historySize;
                        updateTransform(rangeScan, holder.xfm);
                        m_axis.setTransform(holder.xfm);
                        m_sphere.setTransform(holder.xfm);

                        //-- get the azimuth and elevation angles
                        if(sample.scanAzimuth.userData.size() > 0) { // check if azimuth values are in sample
                            m_scanAz = sample.scanAzimuth.userData.toArrayShort(m_scanAz);
                        }
                        else { // if not, use azimuth values in config
                            if(m_scanAz.length == 0) {
                                m_scanAz = config.scanAzimuth.userData.toArrayShort(m_scanAz);
                            }
                        }
                        if(sample.scanElevation.userData.size() > 0) { // check if elevation values are in sample
                            m_scanEl = sample.scanElevation.userData.toArrayShort(m_scanEl);
                        }
                        else {
                            if(m_scanEl.length == 0) { // if not, use elevation values in config
                                m_scanEl = config.scanElevation.userData.toArrayShort(m_scanEl);
                            }
                        }
                        final int numRanges = sample.rangeData.userData.size();
                        final float scanAzScale = config.scanAzimuthScale;
                        final float scanElScale = config.scanElevationScale;
                        final float rangeScale  = config.rangeScale;
                        //final float intensScale = config.intensityScale;
                        // XXX Assuming here that all the scan lengths are the same. This is a reasonable 
                        // XXX assumption for the sensors we are currently using, but it should be validated
                        final int cols = config.scanLengths.userData.getShort(0);
                        final int rows = numRanges / cols;

                        // bounding box setup
                        float[] min = new float[] {  Float.MAX_VALUE,  Float.MAX_VALUE,  Float.MAX_VALUE };
                        float[] max = new float[] { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
                        final int bbStep = 1 + numRanges / BBSS;
                        int bbNext = 1 + m_rand.nextInt(bbStep);
                        int bbSamples = 0;

                        int expected = rows*cols;
                        int received = sample.rangeData.userData.size();
                        if(received != expected) {
                            logger.error("expected rangeData of size "+expected+", but got "+received);
                        }

                        rangeScan.ensureSize(numRanges);
                        FloatBuffer verts = rangeScan.getMeshData().getVertexBuffer();
                        FloatBuffer clrs = rangeScan.getMeshData().getColorBuffer();
                        verts.rewind();
                        clrs.rewind();
                        verts.limit(numRanges*3);
                        clrs.limit(numRanges*4);

                        int dataIdx;
                        int numVerts = 0;
                        float val;
                        float scanEl;
                        float scanAz;
                        float scanRange;

                        float intnsMin = 255;
                        float intnsMax = 0;
                        float intnsMul = 235f/(m_intnsMax - m_intnsMin);

                        boolean validPoint = true;
                        for(int r = 0; r < rows; r++) {
                            for(int c = 0; c < cols; c++) {
                                dataIdx = r*cols + c;
                                scanRange = rangeScale*sample.rangeData.userData.getShort(dataIdx);
                                if(scanRange > m_minRange) {
                                    validPoint = true;
                                    scanEl = m_scanEl[c]*scanElScale;
                                    scanAz = m_scanAz[r]*scanAzScale;
                                    t_azRot.fromAngleAxis(scanAz, Vector3.UNIT_Z);
                                    t_elRot.fromAngleAxis(scanEl, Vector3.UNIT_Y);
                                    t_azRot.multiply(t_elRot, t_rot);
                                    t_vec.set(1,0,0);
                                    t_rot.applyPost(t_vec, t_vec);
                                    t_vec.multiplyLocal(scanRange);
                                }
                                else {
                                    validPoint = false;
                                    t_vec.set(0,0,1);
                                }

                                // add valid points to the point cloud
                                if(validPoint) {
                                    numVerts++;
                                    verts.put(t_vec.getXf()).put(t_vec.getYf()).put(t_vec.getZf());

                                    ColorRGBA tmpClr;
                                    switch(m_pointColorMode) {
                                    case Intensity:
                                        tmpClr = new ColorRGBA(1,1,1,1);
                                        // calculate intensity for valid points only
                                        final float intensity = 0xFF & sample.intensityData.userData.getByte(dataIdx);
                                        if(intensity < intnsMin) intnsMin = intensity;
                                        if(intensity > intnsMax) intnsMax = intensity;
                                        //final float bi = (20 + intnsMul * (255 - intensity) ) / 255f;
                                        final float bi = (20 + intnsMul * intensity) / 255f;
                                        m_color.multiply( (bi > 1) ? 1 : bi, tmpClr); // clamp bi at 1
                                        clrs.put(tmpClr.getRed()).put(tmpClr.getGreen()).put(tmpClr.getBlue()).put(1);
                                        break;
                                    case NoIntensity:
                                        tmpClr = m_color;
                                        clrs.put(tmpClr.getRed()).put(tmpClr.getGreen()).put(tmpClr.getBlue()).put(1);
                                        break;
                                    case PerColColor:
                                        if(m_scanColorArray == null) {
                                            m_scanColorArray = createColorArray(cols);
                                        }
                                        tmpClr = m_scanColorArray[c];
                                        clrs.put(tmpClr.getRed()).put(tmpClr.getGreen()).put(tmpClr.getBlue()).put(1);
                                        break;
                                    case PerRowColor:
                                        if(m_scanColorArray == null) {
                                            m_scanColorArray = createColorArray(rows);
                                        }
                                        tmpClr = m_scanColorArray[r];
                                        clrs.put(tmpClr.getRed()).put(tmpClr.getGreen()).put(tmpClr.getBlue()).put(1);
                                        break;
                                    }
                                }

                                if(numVerts == bbNext) {
                                    bbNext += m_rand.nextInt(bbStep);
                                    val = t_vec.getXf();
                                    if(val < min[0]) min[0] = val;
                                    if(val > max[0]) max[0] = val;
                                    val = t_vec.getYf();
                                    if(val < min[1]) min[1] = val;
                                    if(val > max[1]) max[1] = val;
                                    val = t_vec.getZf();
                                    if(val < min[2]) min[2] = val;
                                    if(val > max[2]) max[2] = val;
                                    bbSamples++;
                                }
                            }
                        }
                        verts.limit(numVerts*3);
                        clrs.limit(numVerts*4);
                        // update running average intensity min/max
                        if(numVerts > 2) {
                            m_intnsMin = 0.99f*m_intnsMin + 0.01f*intnsMin;
                            m_intnsMax = 0.99f*m_intnsMax + 0.01f*intnsMax;
                        }

                        // update bounding box
                        boolean isValid = (bbSamples > 2);
                        BoundingBox bb = rangeScan.getBoundingBox();
                        if(isValid) {
                            float bbe[] = new float[] {(max[0]-min[0])/2f, (max[1]-min[1])/2f, (max[2]-min[2])/2f };
                            float bbc[] = new float[] { min[0]+bbe[0],      min[1]+bbe[1],      min[2]+bbe[2] };
                            bb.setCenter( bbc[0], bbc[1], bbc[2] );
                            bb.setXExtent(bbe[0]);
                            bb.setYExtent(bbe[1]);
                            bb.setZExtent(bbe[2]);
                            double vol = bb.getVolume();
                            if(Double.isInfinite(vol) || Double.isNaN(vol)) {
                                isValid = false;
                            }
                        }
                        if(!isValid) {
                            bb.setCenter(0, 0, 0);
                            bb.setXExtent(1);
                            bb.setYExtent(1);
                            bb.setZExtent(1);
                        }
                        rangeScan.markDirty(DirtyType.Bounding);
                    }
                    setDirty(false);
                }
                catch(Throwable t) {
                    //logger.error("Exception during Point Cloud update. Setting visibility to false.", t);
                    //setVisible(false);
                    logger.error("Exception during Point Cloud update. Resetting.", t);
                    reset();
                }
            }
        }
    }

    protected ColorRGBA[] createColorArray(int numColors) {
        ColorRGBA[] retVal = new ColorRGBA[numColors];
        for(int i = 0; i < numColors; i++) {
            retVal[i] = ColorRGBA.randomColor(new ColorRGBA());
        }
        return retVal;
    }

    public float getMinRange() {
        return m_minRange;
    }

    public void setMinRange(float val) {
        m_minRange = val;
    }

    @Override
    public void reset() {
        for(int i = 0; i < m_historySize; i++) {
            m_rangeScans[i].ensureSize(0);
        }
        m_data.clear();
    }

    boolean updateSensorOffsetTransform(String frameName, Transform retVal) {
        if(frameName != null) {
            FrameTreeNode source   = RapidFrameStore.get().lookup(frameName);
            FrameTreeNode wrtFrame = RapidFrameStore.get().lookup(getRapidRobot().getAgent().name());
            if(!(source == null || wrtFrame == null)) {
                Transform axfm = RapidFrameStore.get().getTransform(wrtFrame, source);
                retVal.set(axfm);
                logger.debug(getRobot().getName()+": got range scan frame transform - "+Ardor3D.format(axfm));
                return true;
            }
            else {
                // how should we warn?
            }
        }
        return false;
    }

}
