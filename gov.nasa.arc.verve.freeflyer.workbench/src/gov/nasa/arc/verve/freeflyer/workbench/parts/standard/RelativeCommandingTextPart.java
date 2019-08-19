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
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class RelativeCommandingTextPart implements AstrobeeStateListener {
//	private Logger logger = Logger.getLogger(RelativeCommanding.class);
	protected CommandPublisher commandPublisher;
	private final int NUM_FAVORITES = 10;
	private Text[] poseInputText = new Text[NUM_FAVORITES];
	private CommandButton[] moveButton = new CommandButton[NUM_FAVORITES];
	private String hintString = "x, y, z, qx, qy, qz, qw\t";
	private String commandButtonLabel = "Move Relative";
	private Agent agent;
	protected AstrobeeStateManager astrobeeStateManager;
	protected String accessControlName = "";
	protected boolean iHaveControl = false;
	protected String myId = Agent.getEgoAgent().name();
	int graylevel = 220;
	protected final Color gray1 = ColorProvider.get(graylevel,graylevel,graylevel);

	@Inject 
	public RelativeCommandingTextPart(MApplication application, Composite parent) {

		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		inner.setLayout(gl);

		createInputCompositeOnRelativeTab(inner);
		
	}

	protected Composite createInputCompositeOnRelativeTab(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 2, GridData.FILL_HORIZONTAL);
		
		for(int i=0;i<NUM_FAVORITES;i++) {
			Composite comp = setupConfigComposite(innerInnerComposite, 1);
			comp.setBackground(gray1);
			makeConfigureCombo(comp, i);
			makeConfigureButton(comp, i);
		}
		
		return innerComposite;
	}
	
	protected void makeConfigureCombo(Composite parent, int num) {
		poseInputText[num] = new Text(parent, SWT.BORDER);
		poseInputText[num].setText(hintString);
		poseInputText[num].setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	protected void makeConfigureButton(Composite parent, int num) {
		moveButton[num] = new CommandButton(parent, SWT.NONE);
		moveButton[num].setText(commandButtonLabel);
		moveButton[num].setButtonLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		moveButton[num].setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	
		moveButton[num].addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String raw = poseInputText[num].getText();
				String tokens[] = raw.split(",");
				double x = Double.valueOf(tokens[0]);
				double y = Double.valueOf(tokens[1]);
				double z = Double.valueOf(tokens[2]);
				float qx = Float.valueOf(tokens[3]);
				float qy = Float.valueOf(tokens[4]);
				float qz = Float.valueOf(tokens[5]);
				float qw = Float.valueOf(tokens[6]);
				commandPublisher.sendRelative(x, y, z, qx, qy, qz, qw);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		if(agent != null) {
			astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
			astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		}
	}


	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; // have to do this because we might be the other control panel

		commandPublisher = CommandPublisher.getInstance(agent);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.addListener(this);
		} else {
			System.err.println("DockingPart does not have an AstrobeeStateManager");
		}
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		if(moveButton == null) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(agent.name().equals(Agent.SmartDock.name()))
					return;

				accessControlName = aggregateState.getAccessControl();
				
				enableButtonsForAccessControl();
			}
		});
	}

	/** returns false if no more buttons should be enabled. */
	protected boolean enableButtonsForAccessControl() {
		if(accessControlName == null) {
			return false;
		}

		if(!accessControlName.equals(myId)) {
			if(iHaveControl) {
				iHaveControl = false;
			}
			disableMoveButtons();
			return false;
		} else {
			if(!iHaveControl) {
				iHaveControl = true;
			}
			enableMoveButtons();
			return true;
		}
	}
	
	private Composite setupConfigComposite(Composite parent, int width) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2,false);
		c.setLayout(gl);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = width;
		c.setLayoutData(data);
		return c;
	}

	protected void enableMoveButtons() {
		for(int i=0; i<NUM_FAVORITES; i++) {
			moveButton[i].setCompositeEnabled(true);
		}
	}
	

	protected void disableMoveButtons() {
		for(int i=0; i<NUM_FAVORITES; i++) {
			moveButton[i].setCompositeEnabled(false);
		}
	}
}
