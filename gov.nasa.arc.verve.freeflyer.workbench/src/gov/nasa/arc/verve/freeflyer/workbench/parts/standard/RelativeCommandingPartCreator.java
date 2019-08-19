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

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.utils.TrackVisibleBeeCommandingSubtab;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;



public class RelativeCommandingPartCreator extends BeeCommandingPartOnTeleoperateTabCreator {

	protected final String ROLL_TOOLTIP = "Rotation about Bee Forward Axis";
	protected final String PITCH_TOOLTIP = "Rotation about Bee Starboard Axis";
	protected final String YAW_TOOLTIP = "Rotation about Bee Nadir Axis";
	
	protected final String[] ROTATION_TOOLTIP = {ROLL_TOOLTIP, PITCH_TOOLTIP, YAW_TOOLTIP};
	
	protected final String X_TOOLTIP = "Bee Forward Coordinate";
	protected final String Y_TOOLTIP = "Bee Right Coordinate";
	protected final String Z_TOOLTIP = "Bee Downward Coordinate";
	
	protected final String[] TRANSLATION_TOOLTIP = {X_TOOLTIP, Y_TOOLTIP, Z_TOOLTIP};
	
	int graylevel = 210;
	protected final Color gray1 = ColorProvider.get(graylevel,graylevel,graylevel);
	
	@Override
	protected String getMoveButtonName() {
		return "Move Relative";
	}
	
	@Override
	protected void createManualInputsInnerComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(parent, 7, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);
		innerComposite.setBackground(gray1);

		fillManualInputsInnerComposite(innerComposite);
	}
	
	@Override
	protected void createCommandsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(innerComposite);

		innerComposite.setBackground(gray1);
		createCommandsInnerComposite(innerComposite);
	}
	
	@Override
	protected void createMoveButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		commandingPart.moveButton = new CommandButton(innerComposite, SWT.NONE);
		commandingPart.moveButton.setText(getMoveButtonName());
		commandingPart.moveButton.setToolTipText(MOVE_TOOLTIP);
		commandingPart.moveButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		commandingPart.moveButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		commandingPart.moveButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double x = commandingPart.translationInput[0].getNumber();
				double y = commandingPart.translationInput[1].getNumber();
				double z = commandingPart.translationInput[2].getNumber();

				double roll = commandingPart.rotationInput[0].getNumber();
				double pitch = commandingPart.rotationInput[1].getNumber();
				double yaw = commandingPart.rotationInput[2].getNumber();

				commandingPart.commandPublisher.sendTranslateRotateCommandInRelativeCoordinates(
						x, y, z, roll/RAD_TO_DEG, pitch/RAD_TO_DEG, yaw/RAD_TO_DEG);
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		commandingPart.moveButton.setCompositeEnabled(false);
	}
	
	protected void createShowPreviewButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		commandingPart.showPreviewButton = new EnlargeableButton(innerComposite, SWT.TOGGLE);
		commandingPart.showPreviewButton.setText(commandingPart.SHOW_PREVIEW_STRING);
		commandingPart.showPreviewButton.setToolTipText(commandingPart.SHOW_PREVIEW_TOOLTIP);
		commandingPart.showPreviewButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.showPreviewButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.showPreviewButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				TrackVisibleBeeCommandingSubtab.INSTANCE.setRelativePreviewShowing(btn.getSelection());
				commandingPart.freeFlyerScenario.showRelativePreview(btn.getSelection());
				if(btn.getSelection()) {
					commandingPart.showPreviewButton.setText(commandingPart.HIDE_PREVIEW_STRING);
					commandingPart.showPreviewButton.setToolTipText(commandingPart.HIDE_PREVIEW_TOOLTIP);
				} else {
					commandingPart.showPreviewButton.setText(commandingPart.SHOW_PREVIEW_STRING);
					commandingPart.showPreviewButton.setToolTipText(commandingPart.SHOW_PREVIEW_TOOLTIP);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

}

