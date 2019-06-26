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

import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButtonForGuestScienceTab;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SimplifiedGuestScienceSidePart extends
		AdvancedGuestScienceSidePart {

	@Inject
	public SimplifiedGuestScienceSidePart(Composite parent, GuestScienceStateManager gssm, MApplication app) {
		super(parent, gssm, app);
	}
	
	@Override
	protected void constructComposite(Composite parent) {
		GridLayout gl0 = new GridLayout(1, false);
		parent.setLayout(gl0);

		createTopTitleAndButtons(parent);
		createPlanGroup(parent);
		createManualCommandingGroup(parent);
	}
	
	@Override
	protected void createManualCommandingGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Payload Commanding");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(group);
		
		GridLayout gl = new GridLayout(2, true);
		group.setLayout(gl);

		new Label(group, SWT.NONE).setText("Guest Science Application");
		new Label(group, SWT.NONE).setText("Command");

		commandApkCombo = new Combo(group, SWT.READ_ONLY);
		commandApkCombo.setToolTipText(GUEST_SCIENCE_COMBO_TOOLTIP);
		commandApkCombo.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(commandApkCombo);
		commandApkCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateAndEnableTemplateCombo();
			}
		});

		templateCombo = new Combo(group, SWT.READ_ONLY);
		templateCombo.setEnabled(false);
		templateCombo.setToolTipText(COMMAND_COMBO_TOOLTIP);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(templateCombo);
		templateCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableSendCustomApkCommandButton();
			}
		});
		
		createSendGuestScienceCommandButton(group);
	}
	
	@Override
	protected void createSendGuestScienceCommandButton(Composite group) {
		sendGuestScienceCommandButton = new CommandButtonForGuestScienceTab(group, SWT.NONE);
		sendGuestScienceCommandButton.setText("Send Command");
		sendGuestScienceCommandButton.setToolTipText(SEND_COMMAND_BUTTON_TOOLTIP);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(sendGuestScienceCommandButton);
		sendGuestScienceCommandButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		sendGuestScienceCommandButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(int i=0; i<selected.length; i++) {
					if(selected[i] != null) {
						String apkName = guestScienceStateManager.getApkLongName(selected[i], commandApkCombo.getText());
						String template = guestScienceStateManager.getCommandBody(getASelectedAgent(), commandApkCombo.getText(), templateCombo.getText());
						commandPublishers[i].sendGuestScienceCommand(apkName, template);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
}
