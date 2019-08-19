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
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.ADMIN_METHOD_NOOP;
import rapid.ext.astrobee.ADMIN;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_ARTAGS;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_HANDRAIL;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_MAPPED_LANDMARKS;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_NONE;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_PERCH;
import rapid.ext.astrobee.ADMIN_LOCALIZATION_MODE_TRUTH;
import rapid.ext.astrobee.ADMIN_METHOD_INITIALIZE_BIAS;
import rapid.ext.astrobee.ADMIN_METHOD_REACQUIRE_POSITION;
import rapid.ext.astrobee.ADMIN_METHOD_RESET_EKF;
import rapid.ext.astrobee.ADMIN_METHOD_SWITCH_LOCALIZATION;
import rapid.ext.astrobee.ADMIN_METHOD_SWITCH_LOCALIZATION_PARAM_MODE;
import rapid.ext.astrobee.MOBILITY;
import rapid.ext.astrobee.MOBILITY_METHOD_IDLE_PROPULSION;

public class LocalizationCommandsPart {
	private static final Logger logger = Logger.getLogger(LocalizationCommandsPart.class);
	private CommandPublisher commandPublisher;
	private String titleString = "Localization Commands";
	protected Agent agent;
	protected MessageType[] sampleType;
	private Label agentNameLabel;
	private int entireWidth = 3;
	private Combo localizationModeCombo;
	private CommandButton ekfButton, biasButton, reacqButton, switchLocButton;
	private CommandButton idlePropulsionButton, noopButton;
	private final String ekfString = "Reset EKF";
	private final String biasString = "Initialize Bias";
	private final String reacqString = "Reacquire Position";
	private final String switchLocString = "Switch Localization";
	private final String idlePropulsionString = "Idle Propulsion";
	private final String noopString = "No-Op";
	int graylevel = 220;
	protected final Color gray1 = ColorProvider.get(graylevel,graylevel,graylevel);
	
	private String[] localizationModes = {
			ADMIN_LOCALIZATION_MODE_NONE.VALUE,
			ADMIN_LOCALIZATION_MODE_MAPPED_LANDMARKS.VALUE,
			ADMIN_LOCALIZATION_MODE_ARTAGS.VALUE,
			ADMIN_LOCALIZATION_MODE_HANDRAIL.VALUE,
			ADMIN_LOCALIZATION_MODE_PERCH.VALUE,
			ADMIN_LOCALIZATION_MODE_TRUTH.VALUE
	};
	protected String participantId = Rapid.PrimaryParticipant;
	@Inject
	IEclipseContext context;

	protected AstrobeeStateManager astrobeeStateManager;
	protected String myId;

	protected Display savedDisplay;

	@Inject 
	public LocalizationCommandsPart(Composite parent) {
		savedDisplay = Display.getDefault();
		GridLayout gl = new GridLayout(2, true);
		parent.setLayout(gl);

		GridData gdThreeWide = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdThreeWide.horizontalSpan = 2;
		parent.setLayoutData(gdThreeWide);
		myId = Agent.getEgoAgent().name();

		GuiUtils.makeHorizontalSeparator(parent);
		createAgentNameLabel(parent);

		makeLocalizationModeSection(parent);
		createEkfButton(parent);
		createBiasButton(parent);
		createReacqButton(parent);
		
		GuiUtils.makeHorizontalSeparator(parent);
		createOtherHelpfulSection(parent);
	}
	
	protected void createOtherHelpfulSection(Composite parent) {
		Label otherHelpful = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = entireWidth;
		otherHelpful.setLayoutData(data);
		otherHelpful.setText("Other Helpful Commands");
		createIdlePropulsionButton(parent);
		createNoopButton(parent);
	}
	
	private void createEkfButton(Composite parent) {
		ekfButton = new CommandButton(parent, SWT.NONE);
		ekfButton.setText(ekfString);
		ekfButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		ekfButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		ekfButton.setCompositeEnabled(true);
		ekfButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						ADMIN_METHOD_RESET_EKF.VALUE,
						ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	private void createBiasButton(Composite parent) {
		biasButton = new CommandButton(parent, SWT.NONE);
		biasButton.setText(biasString);
		biasButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		biasButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		biasButton.setCompositeEnabled(true);
		biasButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						ADMIN_METHOD_INITIALIZE_BIAS.VALUE,
						ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	private void createReacqButton(Composite parent) {
		reacqButton = new CommandButton(parent, SWT.NONE);
		reacqButton.setText(reacqString);
		reacqButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		reacqButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		reacqButton.setCompositeEnabled(true);
		reacqButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						ADMIN_METHOD_REACQUIRE_POSITION.VALUE,
						ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}


	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; 
		agentNameLabel.setText(agent.name() + " " + titleString );
		commandPublisher = CommandPublisher.getInstance(agent);
	}


	public Agent getAgent() {
		return agent;
	}

	protected void makeLocalizationModeSection(Composite parent) {
		Composite comp = setupConfigComposite(parent, 2);
		GuiUtils.giveGridLayout(parent, 2);	
		comp.setBackground(gray1);
		makeLocalizationModeCombo(comp);
		makeSwitchLocalizationModeButton(comp);
	}

	protected void makeLocalizationModeCombo(Composite parent) {
		localizationModeCombo = new Combo(parent, SWT.READ_ONLY);
		localizationModeCombo.setItems(localizationModes);
		localizationModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	protected void makeSwitchLocalizationModeButton(Composite parent) {
		switchLocButton = new CommandButton(parent, SWT.NONE);
		switchLocButton.setText(switchLocString);
		switchLocButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		switchLocButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		switchLocButton.setCompositeEnabled(true);
		switchLocButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(localizationModeCombo.getSelectionIndex() == -1) {
					//print a message - nothing selected
					System.out.println("no localization mode selected");
					return;
				}

				String selected = localizationModes[localizationModeCombo.getSelectionIndex()];
				commandPublisher.sendGenericOneStringCommand(
						ADMIN_METHOD_SWITCH_LOCALIZATION.VALUE,
						ADMIN.VALUE, 
						ADMIN_METHOD_SWITCH_LOCALIZATION_PARAM_MODE.VALUE,
						selected); 
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	private void createIdlePropulsionButton(Composite parent) {
		idlePropulsionButton = new CommandButton(parent, SWT.NONE);
		idlePropulsionButton.setText(idlePropulsionString);
		idlePropulsionButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idlePropulsionButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idlePropulsionButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					commandPublisher.sendGenericNoParamsCommand(
							MOBILITY_METHOD_IDLE_PROPULSION.VALUE,
							MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	private void createNoopButton(Composite parent) {
		noopButton = new CommandButton(parent, SWT.NONE);
		noopButton.setText(noopString);
		noopButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		noopButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		noopButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(ADMIN_METHOD_NOOP.VALUE, ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}


	private void createAgentNameLabel(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = entireWidth;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);
		Font bigFont = GuiUtils.makeBigFont(parent, agentNameLabel);
		agentNameLabel.setFont(bigFont);
	}
	
	private Composite setupConfigComposite(Composite parent, int width) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2,false);
//		gl.marginWidth = 0;
//		gl.verticalSpacing = 0;
		c.setLayout(gl);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = width;
		c.setLayoutData(data);
		return c;
	}

}
