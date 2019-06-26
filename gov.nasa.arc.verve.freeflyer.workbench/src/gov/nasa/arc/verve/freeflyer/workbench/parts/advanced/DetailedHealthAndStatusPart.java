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
package gov.nasa.arc.verve.freeflyer.workbench.parts.advanced;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.HealthPart;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DetailedHealthAndStatusPart extends HealthPart implements AstrobeeStateListener {
	protected String healthAndStatusString = "Detailed Health and Status";
	
	@Inject
	public DetailedHealthAndStatusPart(Composite parent, Display display, MApplication mapp) {
		super(parent, display, mapp);
		//build(parent);
	}

	@Override
	public void build(Composite parent) {
		int cellsAcross = 2;
		GridLayout gridLayout = new GridLayout(cellsAcross, true);
		parent.setLayout(gridLayout);

		GuiUtils.makeHorizontalSeparator(parent, cellsAcross);

		createTreeArea(parent);
	}
	
	@Override
	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		healthViewer.setInput(astrobeeStateManager.getAdapter().getDetailedHealthAndStatusData());
		astrobeeStateManager.addListener(this);
	}
	
	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (healthTree.isDisposed()) {
					return;
				}
				
				// have to do TWICE to reorder dynamically
				healthViewer.refresh();
				healthViewer.refresh();
			}
		});
	}
	
	@Override
	@Inject
	@Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		super.onAgentSelected(a);
	}
	
	@Override
	public void onAgentDeselected(){
		super.onAgentDeselected();
	}
	
	@Override
	protected String getLabelString() {
		return "Detailed Health and Status";
	}
	
}
