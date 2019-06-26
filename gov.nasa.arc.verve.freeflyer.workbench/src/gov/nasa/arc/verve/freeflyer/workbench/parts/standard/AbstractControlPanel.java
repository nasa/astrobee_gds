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
package gov.nasa.arc.verve.freeflyer.workbench.parts.standard;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

import rapid.ext.astrobee.util.RapidExtAstroBeeTypeSupportUtil;

public abstract class AbstractControlPanel implements AstrobeeStateListener {
	protected AstrobeeStateManager astrobeeStateManager;
	protected String myId;
	protected StandardControls standardControls;
	
	protected Agent agent = null;
	protected boolean agentValid = false;
	protected CommandPublisher commandPublisher;
	
	protected AbstractControlPanel(AstrobeeStateManager astrobeeStateManager) {
		this.astrobeeStateManager = astrobeeStateManager;
		myId = Agent.getEgoAgent().name();
		//-- Make the RAPID Astrobee types visible 
		TypeSupportUtil.addImpl(new RapidExtAstroBeeTypeSupportUtil());
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		if(standardControls != null) {
			standardControls.onAgentSelected(a);
		}
		agent = a; // have to do this because we might be the other control panel
		if(a != null) {
			agentValid = true;
		}
		
		commandPublisher = CommandPublisher.getInstance(agent);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.ACCESSCONTROL_STATE_TYPE);
//		} else {
//			System.err.println("AbstractControlPanel does not have an AstrobeeStateManager");
		}
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		standardControls.deregister();
	}

	public abstract void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper);
	
}
