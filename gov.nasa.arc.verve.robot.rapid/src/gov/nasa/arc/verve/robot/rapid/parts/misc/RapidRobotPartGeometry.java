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
package gov.nasa.arc.verve.robot.rapid.parts.misc;

import gov.nasa.arc.verve.common.ardor3d.util.geom.NormalGenerator;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IDdsReaderStatusListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.ReaderStatus;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import rapid.OctetSequence128K;
import rapid.ext.arc.GeometryConfig;
import rapid.ext.arc.GeometryIndexMode;
import rapid.ext.arc.GeometryMeshSample;
import rapid.ext.arc.OctetSequence170K;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.geom.BufferUtils;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartGeometry extends RapidRobotPart implements IDdsReaderStatusListener {
    private static Logger logger = Logger.getLogger(RapidRobotPartGeometry.class);
    protected final MessageType SAMPLE_TYPE;
    protected final MessageType CONFIG_TYPE;

    protected final Map<Integer,Mesh>                 m_meshMap = new HashMap<Integer,Mesh>();
    protected final Map<Integer,GeometryConfig>     m_configMap = new HashMap<Integer,GeometryConfig>();
    protected final Map<Integer,GeometryMeshSample> m_sampleMap = new HashMap<Integer,GeometryMeshSample>();

    protected boolean m_doGenerateNormals = true;
    protected boolean m_wireframe = false;
    
    protected WireframeState m_wireframeState = new WireframeState();
    protected MaterialState  m_materialState  = new MaterialState();
    protected ColorRGBA      m_color = new ColorRGBA(ColorRGBA.GRAY);

    public RapidRobotPartGeometry(String partName, RapidRobot parent, String participantId, MessageType msgType) {
        super(partName, parent, participantId);
        SAMPLE_TYPE = msgType;
        CONFIG_TYPE = MessageType.valueOf(SAMPLE_TYPE.getConfigName());
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName());

        CullState cs = new CullState();
        cs.setCullFace(Face.None);
        m_node.setRenderState(cs);

        m_materialState.setDiffuse(m_color);
        m_materialState.setAmbient(ColorRGBA.DARK_GRAY);
        m_materialState.setSpecular(ColorRGBA.DARK_GRAY);
        m_materialState.setShininess(20f);
        m_node.setRenderState(m_materialState);
        
        m_wireframeState.setAntialiased(false);
        m_wireframeState.setLineWidth(3);
        if(m_wireframe == true) {
            m_node.setRenderState(m_wireframeState);
        }
        
        BlendState bs = new BlendState();
        bs.setEnabled(true);
        bs.setBlendEnabled(true);
        m_node.setRenderState(bs);
        m_node.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    public boolean isGenerateNormals() {
        return m_doGenerateNormals;
    }

    public void setGenerateNormals(boolean state) {
        m_doGenerateNormals = state;
        if(m_doGenerateNormals) {
            m_node.getSceneHints().setLightCombineMode(LightCombineMode.Inherit);
        }
        else {
            m_node.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        }
        // lighting is not updating automatically, try triggering by resetting render state
        setWireframe(isWireframe());
    }

    public boolean isWireframe() {
        return m_wireframe;
    }
    
    public void setWireframe(boolean state) {
        m_wireframe = state;
        if(m_node != null) {
            if(m_wireframe) {
                m_node.setRenderState(m_wireframeState);
            }
            else {
                m_node.clearRenderState(StateType.Wireframe);
            }
        }
    }
    
    public ReadOnlyColorRGBA getColor() {
        return m_color;
    }
    
    public void setColor(ReadOnlyColorRGBA color) {
        m_color.set(color);
        m_materialState.setDiffuse(m_color);
        for(Mesh mesh : m_meshMap.values()) {
            mesh.setDefaultColor(m_color);
        }
        setDirty(true);
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
            synchronized(m_sampleMap) {
                GeometryMeshSample sample = (GeometryMeshSample)eventObj;
                m_sampleMap.put(sample.geometryId, sample);
                if(isVisible()) {
                    setDirty(true);
                }
            }
        }
        else if(type == CONFIG_TYPE) {
            GeometryConfig config = (GeometryConfig)eventObj;
            m_configMap.put(config.geometryId, config);
            logger.debug("put config with key: "+config.geometryId);
        }
    }

    /**
     * 
     */
    @Override
    public void setVisible(boolean visible) {
        setDirty(true);
        super.setVisible(visible);
    }

    /**
     */
    @Override
    public void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            if(m_sampleMap.values().size() > 0) {
                processGeometrySamples();
            }
        }
    }

    public void processGeometrySamples() {
        Set<Integer> keys;
        synchronized(m_sampleMap) {
            keys = m_sampleMap.keySet();
        }
        for(Integer key : keys) {
            GeometryConfig     config = m_configMap.get(key);
            GeometryMeshSample sample = m_sampleMap.get(key);
            Mesh               mesh   = m_meshMap.get(key);
            if(sample != null && config != null) {
                m_sampleMap.remove(key); // remove sample if we have a config
                if(mesh == null) {
                    mesh = new Mesh(config.geometryName);
                    m_meshMap.put(key, mesh);
                    m_node.attachChild(mesh);
                }
                if(mesh.getParent() == null) {
                    m_node.attachChild(mesh);
                }
                final MeshData mdata = mesh.getMeshData();

                mesh.setDefaultColor(m_color);
                
                //-- indexes
                IntBuffer idxs = (IntBuffer)mdata.getIndexBuffer();
                if(idxs == null || idxs.capacity() < sample.indexData.userData.size()) {
                    idxs = BufferUtils.createIntBuffer(sample.indexData.userData.size());
                    mdata.setIndexBuffer(idxs);
                }
                idxs.limit(sample.indexData.userData.size());
                idxs.rewind();
                final int numIndexes = sample.indexData.userData.size();
                //logger.debug("  numIndexes="+numIndexes);
                for(int i = 0; i < numIndexes; i++) {
                    int idx = 0xffff&sample.indexData.userData.getShort(i);
                    idxs.put(idx);
                    //logger.debug("  indexes["+i+"]="+idx);
                }
                IndexMode[] idxModes = new IndexMode[sample.indexModes.userData.size()];
                final int numIndexModes = sample.indexModes.userData.size();
                //logger.debug("  numIndexModes="+numIndexModes);
                if(numIndexModes < 1) {
                    logger.error("ERROR: No index modes have been specified for Geometry. Unable to process Geometry.");
                    mesh.removeFromParent();
                    return;
                }
                for(int i = 0; i < numIndexModes; i++) {
                    IndexMode im = getIndexMode((GeometryIndexMode)sample.indexModes.userData.get(i));
                    idxModes[i] = im;
                    //logger.debug("  indexMode["+i+"]="+im.toString());
                }
                mdata.setIndexModes(idxModes);
                final int numIndexLens = sample.indexLengths.userData.size();
                //logger.debug("  numIndexLens="+numIndexLens);
                if(numIndexLens < 1) {
                    logger.error("ERROR: No index lengths have been specified for Geometry. Unable to process Geometry.");
                    mesh.removeFromParent();
                    return;
                }
                if(numIndexLens != numIndexModes) {
                    logger.error("ERROR: number of index modes and index lengths do not match. Unable to process Geometry.");
                    mesh.removeFromParent();
                    return;
                }
                int[] idxLens = new int[numIndexLens];
                for(int i = 0; i < numIndexLens; i++) {
                    int il = sample.indexLengths.userData.getInt(i);
                    idxLens[i] = il;
                    //logger.debug("  indexLengths["+i+"]="+il);
                }
                mdata.setIndexLengths(idxLens);

                //-- vertices
                FloatBuffer verts = mdata.getVertexBuffer();
                if(verts == null || verts.capacity() < sample.vertexData.userData.size()) {
                    verts = BufferUtils.createFloatBuffer(sample.vertexData.userData.size());
                    mdata.setVertexBuffer(verts);
                }
                verts.limit(sample.vertexData.userData.size());
                verts.rewind();
                for(int i = 0; i < sample.vertexData.userData.size(); i++) {
                    final short sv = sample.vertexData.userData.getShort(i);
                    verts.put(sv * sample.vertexScale);
                }

                //-- normals
                final OctetSequence128K normData = sample.normalData;
                if(normData.userData.size() == 0) {
                    mdata.setNormalBuffer(null);
                }
                else {
                    FloatBuffer norms = mdata.getNormalBuffer();
                    if(norms == null || norms.capacity() < sample.normalData.userData.size()) {
                        norms = BufferUtils.createFloatBuffer(sample.normalData.userData.size());
                        mdata.setNormalBuffer(norms);
                    }
                    norms.limit(sample.normalData.userData.size());
                    norms.rewind();
                    for(int i = 0; i < sample.normalData.userData.size(); i++) {
                        final byte bv = sample.normalData.userData.getByte(i);
                        norms.put( bv * sample.normalScale);
                    }
                }

                //-- colors
                final OctetSequence170K clrData = sample.colorData;
                if(clrData.userData.size() == 0) {
                    mdata.setColorBuffer(null);
                }
                else {
                    FloatBuffer clrs = mdata.getColorBuffer();
                    if(clrs == null || clrs.capacity() < sample.colorData.userData.size()) {
                        clrs = BufferUtils.createFloatBuffer(sample.colorData.userData.size());
                        mdata.setColorBuffer(clrs);
                    }
                    clrs.limit(sample.colorData.userData.size());
                    clrs.rewind();
                    for(int i = 0; i < sample.colorData.userData.size(); i++) {
                        final byte bv = sample.colorData.userData.getByte(i);
                        clrs.put( (0xff&bv) * sample.colorScale);
                    }
                }
                // TODO: tex coords

                if(m_doGenerateNormals) {
                    // XXX this is very slow
                    NormalGenerator ng = new NormalGenerator();
                    ng.generateNormals(mesh,60f);
                }

                //-- finish
                mdata.updateVertexCount();
            }
        }
    }

    IndexMode getIndexMode(GeometryIndexMode in) {
        switch(in.ordinal()) {
        case GeometryIndexMode._GIM_TRIANGLES: return IndexMode.Triangles;
        case GeometryIndexMode._GIM_TRIFAN:    return IndexMode.TriangleFan;
        case GeometryIndexMode._GIM_TRISTRIP:  return IndexMode.TriangleStrip;
        case GeometryIndexMode._GIM_QUADS:     return IndexMode.Quads;
        case GeometryIndexMode._GIM_LINES:     return IndexMode.Lines;
        case GeometryIndexMode._GIM_LINE_STRIP:return IndexMode.LineStrip;
        case GeometryIndexMode._GIM_LINE_LOOP: return IndexMode.LineLoop;
        case GeometryIndexMode._GIM_POINTS:    return IndexMode.Points;
        }
        return IndexMode.Points;
    }

    @Override
    public void reset() {
        for(Mesh mesh : m_meshMap.values()) {
            mesh.removeFromParent();
        }
    }

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
