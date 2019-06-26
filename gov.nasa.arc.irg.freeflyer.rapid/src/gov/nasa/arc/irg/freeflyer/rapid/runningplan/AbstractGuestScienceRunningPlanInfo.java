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
package gov.nasa.arc.irg.freeflyer.rapid.runningplan;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;


public class AbstractGuestScienceRunningPlanInfo extends RunningPlanInfo {
	protected int ordinal;
	private IEclipseContext contextForGuestScience; // separate from context in superclass
	
	public AbstractGuestScienceRunningPlanInfo(IEclipseContext context, int ordinal) {
		// Do NOT give the context to super, or it will think it is a regular RunningPlanInfo
		// and insert itself into the context under the wrong name;
		super();
		this.ordinal = ordinal;
		
		MApplication application = context.get(MApplication.class);
		if(application == null) {
			this.contextForGuestScience = context;
		} else {
			this.contextForGuestScience = application.getContext();
		}
		
		if(contextForGuestScience != null) {
			contextForGuestScience.set(FreeFlyerStrings.GUEST_SCIENCE_PLAN_INFO[ordinal], this);
		}
	}
	
	@Inject @Optional
	public void acceptPlan(@Named(FreeFlyerStrings.FREE_FLYER_PLAN) FreeFlyerPlan plan) {
		// ignore this because we are not the main one
	}
	
	public void acceptGuestSciencePlan(FreeFlyerPlan plan) {
		// need to get the plan when it comes in as a compressed file
		// The GuestScienceAstrobeeStateManager listens to the plans, we need to register as a listener with him
		ingestPlan(plan);
		
		// then we need to give ourselves to the GuestSciencePlanTrace
		// GuestSciencePlan trace will listen to us and keep updated.
		
		if(contextForGuestScience != null) {
			contextForGuestScience.set(FreeFlyerStrings.GUEST_SCIENCE_PLAN_INFO[ordinal], null);
			contextForGuestScience.set(FreeFlyerStrings.GUEST_SCIENCE_PLAN_INFO[ordinal], this);
		}
	}
}
