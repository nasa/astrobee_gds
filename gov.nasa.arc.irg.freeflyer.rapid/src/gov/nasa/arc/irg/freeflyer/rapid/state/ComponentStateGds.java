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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import java.util.Vector;

import rapid.ext.astrobee.ComponentConfig;
import rapid.ext.astrobee.ComponentInfo;
import rapid.ext.astrobee.ComponentInfoConfig;
import rapid.ext.astrobee.ComponentState;

public class ComponentStateGds {
	private Vector<SingleComponent> components = new Vector<SingleComponent>();

	public ComponentStateGds() {
	}

	public ComponentStateGds(ComponentConfig cs) {
		for(int i=0; i<cs.components.userData.size(); i++) {
			ComponentInfoConfig cic = (ComponentInfoConfig)cs.components.userData.get(i);
			components.add(new SingleComponent(cic.name));
		}
	}

	public void updateComponentState(ComponentState cs) {
		for(int i=0; i<cs.components.userData.size(); i++) {
			ComponentInfo ci = (ComponentInfo)cs.components.userData.get(i);
			components.get(i).update(ci);
		}
	}

	public Vector<SingleComponent> getComponents() {
		return components;
	}

	public ComponentStateGds copyFrom(Vector<SingleComponent> originalVector) {
		components.clear();
		if(originalVector != null) {
			for(SingleComponent sc : originalVector) {
				if(sc != null) {
					components.add(new SingleComponent(sc));
				}
			}
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((components == null) ? 0 : components.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof ComponentStateGds) {
			ComponentStateGds other = (ComponentStateGds)obj;
			if (components == null) {
				if (other.getComponents() != null) {
					return false;
				}
			} else if (!components.equals(other.getComponents())) {
				return false;
			}
			return true;
		}
		return false;
	}
}
