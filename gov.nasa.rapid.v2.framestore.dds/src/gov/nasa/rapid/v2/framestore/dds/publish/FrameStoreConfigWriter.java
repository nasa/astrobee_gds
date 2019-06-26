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
package gov.nasa.rapid.v2.framestore.dds.publish;

import gov.nasa.dds.system.IDdsRestartListener;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.framestore.tree.visitors.AbstractFrameVisitor;
import gov.nasa.rapid.v2.framestore.tree.visitors.IFrameVisitor;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;
import rapid.FrameDef;
import rapid.FrameStoreConfig;
import rapid.FrameStoreConfigDataWriter;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Write a FrameStoreConfig using existing local 
 * frame store as data source
 * @author mallan
 */
public class FrameStoreConfigWriter implements IDdsRestartListener {
	protected int m_serialId = 0;
	
	private final String       m_participant;
	private final Agent        m_agent;
	
    protected FrameStoreConfig           m_config  = new FrameStoreConfig();
    protected FrameStoreConfigDataWriter m_configWriter;
    protected InstanceHandle_t           m_configInstance;

	
	public FrameStoreConfigWriter(String participantId, Agent agent) {
	    m_participant = participantId;
	    m_agent = agent;
	}
	
	/**
	 * Write a FrameStoreConfig for agent by traversing the 
	 * frame store tree, starting at root. 
	 * @param agent
	 * @param root
	 */
	public void write(FrameTreeNode root) {
		
		IFrameVisitor visitor = newVisitor(m_config);
		root.traversePreOrder(visitor);
		
		RapidUtil.setHeader(m_config.hdr, m_agent.name(), m_agent.name(), m_serialId);

		if(m_configWriter != null) {
		    m_configWriter.write(m_config, m_configInstance);
		}
	}
	
    @Override
    public void onDdsStarted() throws Exception {
        m_configWriter = (FrameStoreConfigDataWriter)
                RapidEntityFactory.createDataWriter(m_participant,
                                                    MessageType.FRAMESTORE_CONFIG_TYPE,
                                                    m_agent.name());
        RapidUtil.setHeader(m_config.hdr, m_agent.name(), m_agent.name(), m_serialId);
        m_configInstance = m_configWriter.register_instance(m_config);
    }

    @Override
    public void onDdsAboutToStop() throws Exception {
        m_configWriter   = null;
        m_configInstance = null;
        
    }

    @Override
    public void onDdsStopped() throws Exception {
        // nothing
    }

	
	private static IFrameVisitor newVisitor(final FrameStoreConfig config) {
		return new AbstractFrameVisitor() {
			final FrameStoreConfig fsConfig = config;
			final Transform xfm  = new Transform();
			final Vector3  trans = new Vector3();
			@Override
			public boolean visit(FrameTreeNode node) {
				Frame frame;
				frame = node.getFrame();
				frame.getTransform(xfm);
				trans.set(xfm.getTranslation());
				FrameDef frameDef = new FrameDef();
				FrameTreeNode parent = node.getParent();
				frameDef.name = frame.getName();
				frameDef.parent = FrameStore.getFullNameOf(parent);
				
				frameDef.transform.xyz.userData[0] = trans.getX();
				frameDef.transform.xyz.userData[1] = trans.getY();
				frameDef.transform.xyz.userData[2] = trans.getZ();
				
				final ReadOnlyMatrix3 rot = xfm.getMatrix();
				frameDef.transform.rot.userData[0] = (float) rot.getM00();
				frameDef.transform.rot.userData[1] = (float) rot.getM01();
				frameDef.transform.rot.userData[2] = (float) rot.getM02();
				frameDef.transform.rot.userData[3] = (float) rot.getM10();
				frameDef.transform.rot.userData[4] = (float) rot.getM11();
				frameDef.transform.rot.userData[5] = (float) rot.getM12();
				frameDef.transform.rot.userData[6] = (float) rot.getM20();
				frameDef.transform.rot.userData[7] = (float) rot.getM21();
				frameDef.transform.rot.userData[8] = (float) rot.getM22();
								
				fsConfig.frames.userData.add(frameDef);
				return false;
			}
		};
	}

}
