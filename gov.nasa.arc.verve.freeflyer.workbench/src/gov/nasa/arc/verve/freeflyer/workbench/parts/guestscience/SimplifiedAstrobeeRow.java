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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeState;
import gov.nasa.rapid.v2.e4.agent.Agent;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SimplifiedAstrobeeRow extends AdvancedAstrobeeRow {

	private boolean summaryAPKActivated = false;

	public SimplifiedAstrobeeRow(Composite parent, Agent agent, IEclipseContext context, String contextString) {
		super(parent, agent, context, contextString);
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateAstrobeeState) {
		if(!(aggregateAstrobeeState instanceof GuestScienceAstrobeeState)) {
			return;
		} else {
			abridgedAstrobeeState = (GuestScienceAstrobeeState) aggregateAstrobeeState;
		}

		if(!myAgentIsAlive) {
			return;
		}

		updateAllTextFieldsFromAbridgedAstrobeeState();
	}

	@Override
	protected void updateAllTextFieldsFromAbridgedAstrobeeState() {
		super.updateAllTextFieldsFromAbridgedAstrobeeState();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(summaryLabel != null && !summaryLabel.isDisposed()) {
					summaryLabel.setBackground(colorWhite);
					if(!summaryAPKActivated)
						summaryLabel.setText(abridgedAstrobeeState.getCurrenPlanStepString());
				}
			}
		});
	}

	@Override
	protected int getCellsAcross() {
		return GSSpaceHelper.simpleCellsAcross;
	}

	@Override
	protected void createComposite(Composite parent) {
		createSelectorButton(parent);

		createCommLed(parent);
		createHealthLed(parent);
		createBattLabel(parent);
		createControlLabel(parent);

		createSummaryLabel(parent);
		createPlanAndPlanStatusLabels(parent);
	}

	@Override
	public void setAgentDisconnected() {
		myAgentIsAlive = false;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				commLed.setImage(cyanImage);
				commLed.setToolTipText(CYAN_TOOLTIP);
				
				if(!controlLabel.getText().trim().isEmpty())
					controlLabel.setBackground(colorCyan);
				else
					controlLabel.setBackground(colorGray);
				controlLabel.setToolTipText(CYAN_TOOLTIP);

				if(!battLabel.getText().trim().isEmpty())
					battLabel.setBackground(colorCyan);
				else
					battLabel.setBackground(colorGray);
				battLabel.setToolTipText(CYAN_TOOLTIP);

				if(summaryLabel != null) {
					if(!summaryLabel.getText().trim().isEmpty())
						summaryLabel.setBackground(colorCyan);
					else
						summaryLabel.setBackground(colorGray);
					summaryLabel.setToolTipText(CYAN_TOOLTIP);
				}

				if(planLabel != null) {
					if(!planLabel.getText().trim().isEmpty())
						planLabel.setBackground(colorCyan);
					else
						planLabel.setBackground(colorGray);
					planLabel.setToolTipText(CYAN_TOOLTIP);
				}

				if(planStatusLabel != null) {
					if(!planStatusLabel.getText().trim().isEmpty())
						planStatusLabel.setBackground(colorCyan);
					else
						planStatusLabel.setBackground(colorGray);
					planStatusLabel.setToolTipText(CYAN_TOOLTIP);
				}

				healthLed.setImage(cyanImage);
				healthLed.setToolTipText(CYAN_TOOLTIP);
			}
		});
	}

	@Override
	public void setAgentConnected() {
		myAgentIsAlive = true;

		updateAllTextFieldsFromAbridgedAstrobeeState();
	}

	@Override
	public void onGuestScienceConfigChange(GuestScienceStateManager manager) {
		this.manager = manager;
	}

	@Override
	public void onGuestScienceStateChange(GuestScienceStateManager manager) {

		this.manager = manager;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final String[] allAPK = manager.getApkNamesForAstrobee(agent);
				for(final String apkName : allAPK){
					String summaryText = manager.getASummary(agent, apkName);
					if(!summaryText.trim().equals("")) {
						summaryAPKActivated = true;
						summaryLabel.setText(summaryText);
						summaryLabel.setBackground(colorWhite);
					}
				}
			}
		});
	}
}
