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
package gov.nasa.arc.verve.freeflyer.workbench.parts.teleop;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MiscellaneousCommandsPart implements SelectedAgentConnectedListener {
	private static final Logger logger = Logger.getLogger(MiscellaneousCommandsPart.class);
	private	int horizontalSpan = 3;
	private CommandPublisher commandPublisher;
	private List<AbstractTeleopCommandConfig> commandConfigs;
	private Composite parent;
	private AstrobeeStateManager astrobeeStateManager;
	private Agent agent;
	
	@Inject 
	public MiscellaneousCommandsPart(Composite inParent) {
		final ScrolledComposite sc1 = new ScrolledComposite(inParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Composite c1 = new Composite(sc1, SWT.NONE);
		sc1.setContent(c1);
		parent = c1;
	}

	@PostConstruct
	public void postConstruct() {
		SelectedAgentConnectedRegistry.addListener(this);
		makeComposite();
	}
	
	public void makeComposite() {
		GridLayout gridLayout = new GridLayout(horizontalSpan, true);
		parent.setLayout(gridLayout);
		try {
			TeleopCommandsConfigList list = TeleopCommandsConfigListLoader.getStandardConfig();
			commandConfigs = list.getTeleopCommandConfigs();

			for(AbstractTeleopCommandConfig atcc : commandConfigs) {
				atcc.createWidget(parent);
				if(astrobeeStateManager != null) {
					atcc.setAstrobeeStateManager(astrobeeStateManager);
				}
				if(commandPublisher != null) {
					atcc.setCommandPublisher(commandPublisher);
				}
			}
		} catch (Exception e) {
			logger.error("Error Reading Teleop Command Config File " + e.getMessage());
		}
		
		repackTheParent();
	}

	private void repackTheParent() {
		parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		parent.pack();
		parent.layout();
		parent.update();
	}
	
	protected void makeHorizontalSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData data =  new GridData(SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = horizontalSpan;
		separator.setLayoutData(data);
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(commandConfigs == null) {
			agent = a;
			return;
		}
		if(a != null) {
			commandPublisher = CommandPublisher.getInstance(a);
			
			for(AbstractTeleopCommandConfig atcc : commandConfigs) {
				atcc.setCommandPublisher(commandPublisher);
				if(Agent.GenericSim.equals(a)) {
					atcc.forceEnabled();
				}
			}
		}
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		if(commandConfigs == null) {
			astrobeeStateManager = asm;
			return;
		}
		for(AbstractTeleopCommandConfig atcc : commandConfigs) {
			atcc.setAstrobeeStateManager(asm);
		}
	}

	@Override
	public void onSelectedAgentConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSelectedAgentDisconnected() {
		if(commandConfigs == null) {
			return;
		}
		for(AbstractTeleopCommandConfig atcc : commandConfigs) {
			atcc.onDisconnect();
		}
	}

	@PreDestroy
	public void preDestroy() {
		if(commandConfigs == null) {
			return;
		}
		for(AbstractTeleopCommandConfig atcc : commandConfigs) {
			atcc.preDestroy();
		}
	}

	protected void showErrorDialog(Shell shell, final String title, final String errorMsg)  {
		// create a dialog with ok and cancel buttons and a question icon
		if(shell == null) {
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
}
