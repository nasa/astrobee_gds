/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.utils;

import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;

import java.util.ArrayList;
import java.util.List;

public class DiscoveredTopicsTree  {
	List<DiscoveredTopicsElement> m_elements = new ArrayList<DiscoveredTopicsElement>();
	
	public Object[] getElements() {
		return  m_elements.toArray();
	}
	
	public void buildTheTree() {
		m_elements.clear();
		
		for (String participantId : DdsEntityFactory.getValidParticipantIds()) {
			List<DiscoveredTopicsElement> partitions = new ArrayList<DiscoveredTopicsElement>();
			
			for (String partition : DiscoveredAgentRepository.INSTANCE.getDiscoveredPartitions(participantId)) {
				List<DiscoveredTopicsElement> topics = new ArrayList<DiscoveredTopicsElement>();
				
				for (String topic : DiscoveredAgentRepository.INSTANCE.getTopicsFor(partition)) {
					topics.add(new DiscoveredTopicsElement(topic, null, partition));
				}
				
				partitions.add(new DiscoveredTopicsElement(partition, topics, participantId));
				
			}
			m_elements.add(new DiscoveredTopicsElement(participantId, partitions, null));
		}
	}

	public class DiscoveredTopicsElement {
		private String m_name;
		private List<DiscoveredTopicsElement> m_children;
		private String m_parent;

		public DiscoveredTopicsElement(String name, List<DiscoveredTopicsElement> children, String parent) {
			m_name = name;
			m_children = children;
			m_parent = parent;
		}

		public String getName() {
			return m_name;
		}
		
		public Object[] getChildren() {
			return  m_children.toArray();
		}

		public String getParent() {
			return m_parent;
		}

		public boolean hasChildren() {
			if(m_children == null) {
				return false;
			}
			return !m_children.isEmpty();
		}
	}
}