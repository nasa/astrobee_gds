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
package gov.nasa.arc.irg.freeflyer.rapid.frequent;

import gov.nasa.arc.irg.freeflyer.rapid.state.ComponentStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.SingleComponent;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.Vector;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;

import rapid.ext.astrobee.ComponentConfig;
import rapid.ext.astrobee.ComponentState;

public class ComponentStateHolder extends AbstractFrequentTelemetryHolder {
	protected ComponentStateGds components;
	protected ComponentState savedComponentState;

	@Inject
	public ComponentStateHolder(MApplication application) {
		configType = MessageTypeExtAstro.COMPONENT_CONFIG_TYPE;
		sampleType = MessageTypeExtAstro.COMPONENT_STATE_TYPE;
		
		topContext = application.getContext();
		
		init();
	}

	@Override
	protected void init() {
		super.init();
		topContext.set(ComponentStateHolder.class, this);
	}
	
	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.COMPONENT_STATE_TYPE)) { 
			ingestComponentState((ComponentState) msgObj);
		}
		else if(msgType.equals(MessageTypeExtAstro.COMPONENT_CONFIG_TYPE)) {
			ingestComponentConfig((ComponentConfig) msgObj);
		}
	}

	public void ingestComponentConfig(ComponentConfig componentConfig) {
		components = new ComponentStateGds(componentConfig);
		if(savedComponentState != null) {
			components.updateComponentState(savedComponentState);
		}
	}

	public void ingestComponentState(ComponentState componentState) {
		if(components == null) {
			savedComponentState = componentState;
			return;
		}
		components.updateComponentState(componentState);
	}

	protected ComponentState getSavedComponentState() {
		return savedComponentState;
	}

	public Vector<SingleComponent> getComponents() {
		if(components != null) 	{
			return components.getComponents();
		} else {
			return null;
		}
	}
}
