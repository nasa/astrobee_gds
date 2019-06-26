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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class TopBarNoSelector extends TopBar {

	@Inject
	public TopBarNoSelector(Composite parent, Display display, UISynchronize sync) {
		super(parent, display, sync);
	}

	@Override
	protected void createComposite(Composite parent) {
		int cellsAcross = 3;
		parent.setLayout(new GridLayout(cellsAcross,false));
		
		createCenterSpacer(parent);
		createRightSideFields(parent);
	}
	
	protected void createRightSideFields(Composite parent) {
		createDockCommCircle(parent);
		createGpsLabel(parent);
	}
	
	@Override
	public void onConnect() {
		Display display = Display.getDefault();
		if (display != null && !display.isDisposed()){
			display.asyncExec(new Runnable() {
				public void run() {
					if (TopBarDataHolder.getInstance().getSavedBee() != null) {
						application.getContext().set(FreeFlyerStrings.PRIMARY_BEE,
								null);
						application.getContext().set(FreeFlyerStrings.PRIMARY_BEE,
								TopBarDataHolder.getInstance().getSavedBee());
					}
					updateDockLed();
				}
			});
		}
	}

	@Override
	public void onDisconnect() {
		Display display = Display.getDefault();
		if (display != null && !display.isDisposed()){
			display.asyncExec(new Runnable() {
				public void run() {
					forceUpdateOfControlField = false;
					forceUpdateOfBattField = false;
					updateDockLed();
				}
			});
		}
	}

	
	protected void updateController(String newController) {
		// no control label
		forceUpdateOfControlField = false;
	}

	protected void updateBattField(float newBattValue) {
		// no batt label
		forceUpdateOfBattField = false;
	}
	
	@Override
	public void activeAgentSetChanged() {
		updateDockLed();
	}
	
	@Override
	protected void setAgentFromTopBarDataHolderInCombo() {
		// no combo to set agent in
	}
	
}
