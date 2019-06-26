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

import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.shape.sensors.PointCloud;
import gov.nasa.arc.verve.robot.scenegraph.task.SetPartVisibleTask;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IDdsReaderStatusListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.ReaderStatus;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.e4.message.holders.PointCloudHolder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;

import rapid.PointCloudConfig;
import rapid.PointCloudSample;
import rapid.PointSample;
import rapid.PointSampleAttributeMode;
import rapid.PointSampleXyzMode;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartPointCloud extends RapidRobotPart implements IDdsReaderStatusListener {
    private static Logger logger = Logger.getLogger(RapidRobotPartPointCloud.class);

    protected class SampleHolder {
        public SampleHolder(PointCloudHolder pcHolder, ReadOnlyTransform roXfm) {
            holder = pcHolder;
            xfm.set(roXfm);
        }
        public PointCloudHolder holder;
        public Transform xfm = new Transform();
    }
    protected final Deque<SampleHolder> m_data = new ArrayDeque<SampleHolder>();
    protected int               m_historySize       = 20;
    protected int               m_historyIndex      = 0;
    protected PointCloud[]      m_pointClouds       = new PointCloud[m_historySize];
    protected ColorRGBA[]       m_pointCloudColor   = new ColorRGBA[m_historySize];
    protected ColorRGBA         m_newColor          = new ColorRGBA(ColorRGBA.WHITE);
    protected ColorRGBA         m_oldColor          = new ColorRGBA(ColorRGBA.DARK_GRAY);
    protected float             m_pointSize         = 1.0f;

    protected int               m_intensityIndex    = -1;
    protected boolean           m_useIntensity      = true;

    protected boolean           m_applyHistoryColor = true;

    protected final MessageType SAMPLE_TYPE;
    protected final MessageType CONFIG_TYPE;

    protected boolean           m_doDebug = false;
    private   FileOutputStream  d_fout;
    private   PrintStream       d_ps;

    protected static boolean    s_paranoid = true;

    public RapidRobotPartPointCloud(String partName, RapidRobot parent, String participantId, MessageType msgType) {
        super(partName, parent, participantId);
        SAMPLE_TYPE = msgType;
        CONFIG_TYPE = MessageType.valueOf(SAMPLE_TYPE.getConfigName());
        //        try {
        //            String filename = String.format("%s/PointCloudSample-timestamps.csv", System.getProperty("user.home"));
        //            d_fout = new FileOutputStream (filename);
        //            d_ps = new PrintStream(d_fout);
        //        }
        //        catch(Exception e) { /**/ }
    }

    public boolean isUseIntensity() {
        return m_useIntensity;
    }

    public void setUseIntensity(boolean status) {
        m_useIntensity = status;
    }

    public boolean isWriteDebugFiles() {
        return m_doDebug;
    }

    public void setWriteDebugFiles(boolean state) {
        m_doDebug = state;
    }

    @Override
    public void finalize() {
        if(d_fout != null) {
            try { d_fout.close(); } 
            catch(Exception e) { /**/ }
        }
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName());
        allocatePointClouds(m_historySize, m_node, null);
        getRobot().getRobotNode().getSensorsNode().attachChild(m_node);
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[] { CONFIG_TYPE, SAMPLE_TYPE };
    }

    @Override
    public void connectTelemetry() throws TelemetryException {
        if(isTelemetryEnabled()) {
            super.connectTelemetry();
            RapidMessageCollector.instance().addDdsReaderStatusListener(getParticipantId(), 
                                                                        getRapidRobot().getAgent().toString(), 
                                                                        SAMPLE_TYPE, 
                                                                        this);
        }
    }

    @Override
    public void disconnectTelemetry() throws TelemetryException {
        RapidMessageCollector.instance().removeDdsReaderStatusListener(getParticipantId(), 
                                                                       getRapidRobot().getAgent().toString(), 
                                                                       SAMPLE_TYPE, 
                                                                       this);
        super.disconnectTelemetry();
    }


    @Override
    public void onRapidMessageReceived(Agent agent, MessageType type, Object eventObj, Object configObj) {
        //logger.debug("onRapidMessageReceived : "+type.toString());
        if(type == SAMPLE_TYPE) {
            if(isVisible()) {
                PointCloudHolder pcHolder = new PointCloudHolder(eventObj, configObj);
                SampleHolder holder = new SampleHolder(pcHolder, getRobot().getPoseProvider().getTransform());
                m_data.add(holder);
                setDirty(true);

                if(d_ps != null) {
                    d_ps.println(pcHolder.sample.hdr.timeStamp);
                }
            }
        }
        // FIXME: referenceFrame is ignored
        if(type == CONFIG_TYPE) {
            PointCloudConfig config = (PointCloudConfig)eventObj;
            for(int i = 0; i < 2; i++) {
                if(config.attributesMode[i] == PointSampleAttributeMode.PS_INTENSITY) {
                    m_intensityIndex = i;
                    break;
                }
            }
        }
    }

    public int getHistorySize() {
        return m_historySize;
    }
    public synchronized void setHistorySize(int historySize) {
        PointCloud[] oldClouds = null;
        if(m_node != null) { // create array to hold old PointClouds, ordered oldest to newest
            int oldSize  = m_historySize;
            int startIdx = (m_historyIndex+1)%oldSize;
            oldClouds = new PointCloud[oldSize];
            for(int i = 0; i < oldSize; i++) {
                oldClouds[i] = m_pointClouds[(startIdx+i)%oldSize];
            }
        }

        m_historyIndex = 0;
        if(historySize < 1) {
            historySize = 1;
        }
        m_historySize  = historySize;

        if(m_node != null) {
            m_node.detachAllChildren();
            allocatePointClouds(m_historySize, m_node, oldClouds);
        }
        //logger.debug("setHistorySize( "+historySize+" )");
    }

    private synchronized void allocatePointClouds(int historySize, Node node, PointCloud[] oldClouds) {
        m_pointClouds       = new PointCloud[historySize];
        int newNeeded = historySize;
        if(oldClouds != null) {
            newNeeded = historySize - oldClouds.length;
            for(int i = 0; i < oldClouds.length; i++) {
                m_pointClouds[i%historySize] = oldClouds[i];
            }
            m_historyIndex = oldClouds.length%historySize;
        }
        // allocate new PointClouds, if necessary
        if(newNeeded > 0) {
            for(int i = 0; i < historySize; i++) {
                int idx = (m_historyIndex+i)%historySize;
                m_pointClouds[idx] = new PointCloud("PointCloud");
                m_pointClouds[idx].setPointSize(m_pointSize);
                m_pointClouds[idx].setUseVertexColors(false);
                node.attachChildAt(m_pointClouds[i], 0);
            }
        }
        else if(newNeeded < -historySize/2) {
            System.gc();
        }
        // standardize names and attach to node
        for(int i = 0; i < historySize; i++) {
            m_pointClouds[i].setName(String.format("%s.PointCloud%03d", getPartName(), i));
            node.attachChildAt(m_pointClouds[i], i);
        }
        allocateColors(historySize);
    }


    /** get the color for the newest point cloud */
    public ReadOnlyColorRGBA getNewestCloudColor() {
        return m_newColor;
    }
    public void setNewestCloudColor(ReadOnlyColorRGBA color) {
        if(color != null) {
            m_newColor.set(color);
        }
        allocateColors(m_historySize);
    }

    /** get the color for the oldest point cloud */
    public ReadOnlyColorRGBA getOldestCloudColor() {
        return m_oldColor;
    }
    public void setOldestCloudColor(ReadOnlyColorRGBA color) {
        if(color != null) {
            m_oldColor.set(color);
        }
        allocateColors(m_historySize);
    }

    public float getPointSize() {
        return m_pointSize;
    }

    public void setPointSize(float pointSize) {
        m_pointSize = pointSize;
        for(PointCloud pc : m_pointClouds) {
            pc.setPointSize(m_pointSize);
        }
    }

    protected synchronized void allocateColors(int historySize) {
        ReadOnlyColorRGBA colorA = m_newColor;
        ReadOnlyColorRGBA colorB = m_oldColor;
        m_pointCloudColor = new ColorRGBA[historySize];
        float lerp;
        if(historySize == 1) {
            m_pointCloudColor[0] = new ColorRGBA(colorA);
        }
        else {
            for(int i = 0; i < historySize; i++) {
                lerp = 1.0f - i/(historySize-1.f);
                m_pointCloudColor[i] = new ColorRGBA();
                ColorRGBA.lerp(colorA, colorB, lerp, m_pointCloudColor[i]);
            }
        }
    }

    /**
     * 
     * @param cloud
     * @param robotXfm transform of robot at time data sample was received
     */
    public void updateTransform(PointCloud cloud, ReadOnlyTransform robotXfm) {
        // empty; here to be overridden in subclasses
    }

    int d_noConfig = 0;
    int d_write = 0;
    static final int BBSS = 67; // bounding box sub sample
    /**
     * Update point cloud history if we have new telemetry. The point 
     * cloud bounding boxes are approximate (only a subset of the points are used)
     */
    @Override
    public synchronized void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            try {
                while(m_data.size() > 0) {
                    SampleHolder holder = m_data.removeFirst();
                    PointCloudHolder dataHolder = holder.holder;
                    PointCloudSample sample = dataHolder.sample;
                    PointCloudConfig config = dataHolder.config;
                    if(config == null) {
                        if(++d_noConfig%10 == 1) {
                            logger.debug("received "+d_noConfig+" "+SAMPLE_TYPE.name()+"s with no config");
                        }
                        continue;
                    }
                    Object[] samples = sample.points.userData.toArray();

                    if(samples.length < 2) { // short circuit if bad point cloud
                        logger.debug("received point cloud with only "+samples.length+" points");
                        continue;
                    }
                    float[] scale = new float[] {
                                                 (float)sample.xyzScale.userData[0],
                                                 (float)sample.xyzScale.userData[1],
                                                 (float)sample.xyzScale.userData[2] };
                    float[] origin = new float[] { 
                                                  (float)sample.origin.userData[0], 
                                                  (float)sample.origin.userData[1], 
                                                  (float)sample.origin.userData[2] };
                    if(s_paranoid) {
                        if(Float.isNaN(origin[0]) || Float.isNaN(origin[1]) || Float.isNaN(origin[2])) {
                            logger.error("PointCloud origin is invalid - "+SAMPLE_TYPE.getTopicName());
                            String filename = writeDebugFile(samples, scale, origin);
                            origin[0] = 0;
                            origin[1] = 0;
                            origin[2] = 0;
                            VerveTask.asyncExec(new SetPartVisibleTask(this, false));
                            IrgUI.errorDialog("Invalid Point Cloud", 
                                              "A PointCloud with invalid origin was received.\n"
                                                      +"Visibility of the point cloud has been turned off \n"
                                                      +"and a debug file has been written here: "+filename);
                        }
                        if(Float.isNaN(scale[0]) || Float.isNaN(scale[1]) || Float.isNaN(scale[2])) {
                            logger.error("PointCloud point scale value is invalid - "+SAMPLE_TYPE.getTopicName());
                            String filename = writeDebugFile(samples, scale, origin);
                            scale[0] = 0.1f;
                            scale[1] = 0.1f;
                            scale[2] = 0.1f;
                            VerveTask.asyncExec(new SetPartVisibleTask(this, false));
                            IrgUI.errorDialog("Invalid Point Cloud", 
                                              "A PointCloud with invalid scale was received.\n"
                                                      +"Visibility of the point cloud has been turned off \n"
                                                      +"and a debug file has been written here: "+filename);
                        }
                    }

                    PointCloud pointCloud = m_pointClouds[m_historyIndex];
                    m_historyIndex = (m_historyIndex+1)%m_historySize;
                    updateTransform(pointCloud, holder.xfm);

                    if(m_applyHistoryColor && !m_useIntensity) {
                        for(int i = 0; i < m_historySize; i++) {
                            int pc = (i+m_historyIndex)%m_historySize;
                            m_pointClouds[pc].setPointColor(m_pointCloudColor[i]);
                        }
                    }
                    float[] min = new float[] {  Float.MAX_VALUE,  Float.MAX_VALUE,  Float.MAX_VALUE };
                    float[] max = new float[] { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
                    final int bbStep = 1 + samples.length / BBSS;
                    int bbSamples = 0;
                    final boolean doIntensity = m_useIntensity && m_intensityIndex >= 0;
                    pointCloud.setUseVertexColors(doIntensity);
                    pointCloud.ensureSize(samples.length);
                    FloatBuffer verts = pointCloud.getMeshData().getVertexBuffer();

                    // if xy theta, z's are all zero
                    final boolean noZ = config.xyzMode == PointSampleXyzMode.PS_XYt;

                    if(config.xyzMode == PointSampleXyzMode.PS_XYZ ||
                            config.xyzMode == PointSampleXyzMode.PS_XYt ) {
                        if(verts != null) {
                            verts.rewind();
                            float val;
                            float[] xyz = new float[3];
                            PointSample ps;
                            for(int i = 0; i < samples.length; i++) {
                                ps = (PointSample)samples[i];
                                xyz[0] = ps.xyz[0]*scale[0] + origin[0];
                                xyz[1] = ps.xyz[1]*scale[1] + origin[1];
                                xyz[2] = noZ ? 0 : ps.xyz[2]*scale[2] + origin[2];
                                verts.put(xyz[0]).put(xyz[1]).put(xyz[2]);

                                if(i%bbStep == 0) {
                                    boolean valid = true;
                                    for(int j = 0; j < 3; j++) {
                                        val = xyz[j];
                                        // verify that sample is not NaN
                                        if(val == val) {
                                            if(val < min[j]) min[j] = val;
                                            if(val > max[j]) max[j] = val;
                                        }
                                        else {
                                            valid = false;
                                        }
                                    }
                                    if(valid) {
                                        bbSamples++;
                                    }
                                }
                            }
                            if(doIntensity) {
                                FloatBuffer colors = pointCloud.getMeshData().getColorBuffer();
                                colors.rewind();
                                final float div = 1f/255f;
                                for(int i = 0; i < samples.length; i++) {
                                    ps = (PointSample)samples[i];
                                    float pi = (ps.attributes[m_intensityIndex]&0xFF)*div;
                                    colors.put(pi*m_newColor.getRed()).put(pi*m_newColor.getGreen()).put(pi*m_newColor.getBlue()).put(1);
                                }
                            }
                        }
                    }
                    else {
                        logger.debug( "Unsupported PointCloud XYZ mode: "+config.xyzMode.toString());
                        break;
                    }

                    if(m_doDebug) {
                        writeDebugFile(samples, scale, origin);
                    }

                    // update bounding box
                    boolean isValid = (bbSamples > 2);
                    BoundingBox bb = pointCloud.getBoundingBox();
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
                        bb.setCenter(origin[0], origin[1], origin[2]);
                        bb.setXExtent(1);
                        bb.setYExtent(1);
                        bb.setZExtent(1);
                    }
                    pointCloud.markDirty(DirtyType.Bounding);
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

    //    @Override
    //    public void setVisible(boolean visible) {
    //        //reset();
    //        super.setVisible(visible);
    //    }

    @Override
    public void reset() {
        for(int i = 0; i < m_historySize; i++) {
            m_pointClouds[i].ensureSize(0);
        }
        m_data.clear();
    }

    @SuppressWarnings("unused")
    private String  writeDebugFile(Object[] samples, float[] scale, float[] origin) {
        String filename = String.format("%s/"+getRapidRobot().getAgent().toString()+"-"+getPartName()+"-%03d.csv", System.getProperty("user.home"), debugFileIndex++);
        logger.debug("PointCloud filename="+filename);
        try {
            FileOutputStream fout = new FileOutputStream (filename);
            PrintStream ps = new PrintStream(fout);
            StringBuilder sb = new StringBuilder();
            ps.println("#index,xyz[0],xyz[1],xyz[2],x,y,z,scale[0],scale[1],scale[2],origin[0],origin[1],origin[2],attribute[0],attribute[1]");
            for(int i = 0; i < samples.length; i++) {
                PointSample sample = (PointSample)samples[i];
                sb.setLength(0);
                sb.append(i); sb.append(",");
                sb.append(sample.xyz[0]); sb.append(",");
                sb.append(sample.xyz[1]); sb.append(",");
                sb.append(sample.xyz[2]); sb.append(",");
                sb.append(sample.xyz[0]*scale[0]+origin[0]); sb.append(",");
                sb.append(sample.xyz[1]*scale[1]+origin[1]); sb.append(",");
                sb.append(sample.xyz[2]*scale[2]+origin[2]); sb.append(",");
                sb.append(scale[0]); sb.append(",");
                sb.append(scale[1]); sb.append(",");
                sb.append(scale[2]); sb.append(",");
                sb.append(origin[0]); sb.append(",");
                sb.append(origin[1]); sb.append(",");
                sb.append(origin[2]); sb.append(",");
                sb.append(sample.attributes[0]); sb.append(",");
                sb.append(sample.attributes[1]); sb.append(",");
                ps.println(sb.toString());
            }
            fout.close();       
        }
        catch (IOException e) {
            logger.debug("IOException writing debug file ("+filename+") : "+e.getMessage());
        }
        return filename;
    }
    private int debugFileIndex = 0;

    @Override
    public void onReaderStatusReceived(String partition, MessageType msgType, ReaderStatus statusType, Object statusObj) {
        if(statusType.equals(ReaderStatus.SampleLost)) {
            SampleLostStatus status = (SampleLostStatus)statusObj;
            logger.debug(msgType.name()+" : "+statusType.toString()+":"+status.total_count+" - last_reason="+status.last_reason.toString());
        }
        else if(statusType.equals(ReaderStatus.SampleRejected)) {
            SampleRejectedStatus status = (SampleRejectedStatus)statusObj;
            logger.debug(msgType.name()+" : "+statusType.toString()+":"+status.total_count+" - last_reason="+status.last_reason.toString());
        }
        else {
            logger.debug(msgType.name()+" : "+statusType.toString());
        }

    }

}
