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

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.freeflyer.rapid.state.InertialPropertiesGds;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.ui.io.InertiaConfigListLoader;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class InertiaPart implements AstrobeeStateListener {
	private static final Logger logger = Logger.getLogger(InertiaPart.class);
	protected Agent agent;
	private Label agentNameLabel;
	private String titleString = "Inertia Properties";
	protected String selectString = "Select Inertia Configuration ...";
	private Label configNameLabel;
	private Label massLabel;
	private Label[] matrixLabel;
	private InertiaConfigList inertiaConfigList;
	private CommandButton configureButton;
	private Combo optionsCombo;
	protected CommandPublisher commandPublisher;
	protected String myId;
	private AstrobeeStateManager astrobeeStateManager;

	private InertialPropertiesGds savedProperties;

	protected GridData fillData = new GridData(SWT.FILL, SWT.FILL, true, false);

	@Inject 
	public InertiaPart(Composite parent, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		GuiUtils.makeHorizontalSeparator(parent);
		createAgentNameLabel(parent);
		GuiUtils.giveGridLayout(parent, 1);
		configNameLabel = new Label(parent, SWT.None);
		configNameLabel.setLayoutData(fillData);
		configNameLabel.setText("Config Name:");
		massLabel = new Label(parent, SWT.NONE);
		massLabel.setLayoutData(fillData);
		massLabel.setText("Mass:");

		matrixLabel = new Label[3];
		for(int i=0; i<3; i++) {
			matrixLabel[i] = new Label(parent, SWT.None);
			matrixLabel[i].setLayoutData(fillData);
			matrixLabel[i].setText("?, ?, ?");
		}
		savedProperties = new InertialPropertiesGds();

		try {
			inertiaConfigList = InertiaConfigListLoader.getStandardConfig();
		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Inertia Config File", e.getMessage());
		}

		optionsCombo = new Combo(parent, SWT.READ_ONLY);
		optionsCombo.setItems( makeConfigsList());
		optionsCombo.setText(selectString);
		optionsCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		myId = Agent.getEgoAgent().name();

		configureButton = new CommandButton(parent, SWT.NONE);
		configureButton.setText("Configure Inertia");
		configureButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectString.equals(optionsCombo.getText())) {
					return;
				}
				InertiaConfig option = inertiaConfigList.getConfigNamed(optionsCombo.getText());
				if (option != null) {
					commandPublisher.sendSetInertiaCommand(
							option.getName(), option.getMass(), option.getMatrix());
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	private void showErrorDialog(Shell shell, final String title, final String errorMsg)  {
		// create a dialog with ok and cancel buttons and a question icon
		if(shell == null) {
			logger.error("No shell injected");
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText(title);
				dialog.setMessage(errorMsg);

				// open dialog and await user selection
				int returnCode = dialog.open(); 

			}
		});
	}
	
	@PreDestroy
	public void preDestroy() {
		SelectedAgentConnectedRegistry.removeListener(configureButton);
		astrobeeStateManager.removeListener(this);
	}

	protected String[] makeConfigsList() {
		if(inertiaConfigList == null) {
			return new String[]{"error"};
		}
		String[] justNames = inertiaConfigList.getArrayOfNames();
		int arrayLength = justNames.length;

		String[] configsStrings = new String[arrayLength + 1];
		configsStrings[0] = selectString;

		if (arrayLength > 0) {
			for (int i = 1; i < justNames.length + 1; i++) {
				configsStrings[i] = justNames[i - 1];
			}
		}
		return configsStrings;
	}

	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		asm.addListener(this, MessageTypeExtAstro.INERTIAL_PROPERTIES_TYPE);

		astrobeeStateManager = asm;
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; 
		agentNameLabel.setText(a.name() + " " + titleString );
		commandPublisher = CommandPublisher.getInstance(agent);
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(configNameLabel == null || configNameLabel.isDisposed()) {
					return;
				}
				if(!configNameLabel.getText().equals(stateKeeper.getInertialProperties().getName())) {
					updateInertiaProperties(stateKeeper);
				}
				
				if(!stateKeeper.getAccessControl().equals(myId)) {
					configureButton.setCompositeEnabled(false);
					optionsCombo.setEnabled(false);
					return;
				} 
				AstrobeeStateGds.OperatingState opState = stateKeeper.getAstrobeeState().getOperatingState();
				if(opState == null) {
					return;
				}
				switch(opState) {
				case READY:
					configureButton.setCompositeEnabled(true);
					optionsCombo.setEnabled(true);
				break;
				}
			}
		});
	}

	private void updateInertiaProperties(AggregateAstrobeeState stateKeeper) {
		configNameLabel.setText(stateKeeper.getInertiaConfigName());
		massLabel.setText("Mass: " + stateKeeper.getMass() + " kg");
		float[] mat = stateKeeper.getInertiaMatrix();

		for(int i=0; i<3; i++) {
			matrixLabel[i].setText(mat[i*3] + "\t" + mat[i*3+1] + "\t" + mat[i*3+2]);
		}

		savedProperties = stateKeeper.getInertialProperties();
	}

	private void createAgentNameLabel(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);
		Font bigFont = GuiUtils.makeBigFont(parent, agentNameLabel);
		agentNameLabel.setFont(bigFont);
	}
}
