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

import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontal;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rapid.ACCESSCONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.MOBILITY;
import rapid.MOBILITY_METHOD_STOPALLMOTION;

public class BeeCommandingPartOnTeleoperateTabCreator {
	private static Logger logger = Logger.getLogger(BeeCommandingPartOnTeleoperateTabCreator.class);
	
	protected BeeCommandingPartOnTeleoperateTab beeCommandingPartOnTeleoperateTab;
	protected double translationRadius = 20;
	protected final double MIN_ROTATION = -180, MAX_ROTATION = 180;
	
	protected final double RAD_TO_DEG = 180.0 / Math.PI;
	private final Color colorOrange = ColorProvider.get(238,118,0);
	private String NO_BOOKMARKS_FOUND_STRING = "No Bookmarks Found";

	private final String BOOKMARKS_TOOLTIP = "Enter Premade Set of Coordinates into Manual Inputs";
	private final String MOVE_TOOLTIP = "Move Astrobee to Pose Specified in Manual Inputs";
	private final String APPLY_OPTIONS_TOOLTIP = "Send Selected Options to Astrobee";

	private final String ROLL_TOOLTIP = "Rotation about ISS Forward Axis";
	private final String PITCH_TOOLTIP = "Rotation about ISS Starboard Axis";
	private final String YAW_TOOLTIP = "Rotation about ISS Nadir Axis";
	
	private final String[] ROTATION_TOOLTIP = {ROLL_TOOLTIP, PITCH_TOOLTIP, YAW_TOOLTIP};
	
	private final String X_TOOLTIP = "ISS Forward-Aft Axis Coordinate";
	private final String Y_TOOLTIP = "ISS Starboard-Port Axis Coordinate";
	private final String Z_TOOLTIP = "ISS Nadir-Zenith Axis Coordinate";
	
	private final String[] TRANSLATION_TOOLTIP = {X_TOOLTIP, Y_TOOLTIP, Z_TOOLTIP};
	
	@Inject
	protected MApplication application;
	

	protected void createMainTab(BeeCommandingPartOnTeleoperateTab beeCommandingPartOnTeleoperateTab, Composite parent) {
		this.beeCommandingPartOnTeleoperateTab = beeCommandingPartOnTeleoperateTab;

		Composite translateTab = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(7, false);
		gl.marginHeight = 0;
		translateTab.setLayout(gl);

		createInitializationWithBookmarkComposite(translateTab);

		Label verticalSeparator = new Label(translateTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator);

		createManualInputsComposite(translateTab);

		Label verticalSeparator2 = new Label(translateTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator2);

		createOptionsComposite(translateTab);

		Label verticalSeparator1 = new Label(translateTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator1);

		createCommandsComposite(translateTab);
	}

	protected void createInitializationWithBookmarkComposite(Composite parent) {
		Composite innerComposite = createInitializationComposite(parent);
		createBookmarkLine(innerComposite);
	}

	protected void createManualInputsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(parent, 7, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		Label spacer1 = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2,1).applyTo(spacer1);
		
		Label l = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(3,1).applyTo(l);
		
		l.setText("Manual Move Inputs");

		Label unitsLabel = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.END).grab(true, false).span(4,1).applyTo(unitsLabel);
		unitsLabel.setText("Meters");
		
		Label spacer = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.END).grab(true, false).applyTo(spacer);
		
		Label unitsLabeld = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.END).grab(true, false).span(2,1).applyTo(unitsLabeld);
		unitsLabeld.setText("Degrees         ");
		
		Composite leftParent = GuiUtils.setupInnerCompositeEvenSpacing(innerComposite, 4, GridData.FILL_BOTH);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).span(4,1).applyTo(leftParent);
		
		Composite rightParent = GuiUtils.setupInnerCompositeEvenSpacing(innerComposite, 3, GridData.FILL_BOTH);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).span(4,1).applyTo(rightParent);
		
		for(int i=0; i<3; i++) {
			createTranslationInput(leftParent, i);
		}
		
		for(int i=0; i<3; i++) {
			createRotationInput(rightParent, i);
		}
	}
	
	protected void createOptionsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 2, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).span(2,1).applyTo(innerInnerComposite);
		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Options");

		// Do in 2 columns so the checkmarks align vertically
		createCheckboxesColumn(innerComposite);
		createCheckmarksColumn(innerComposite);
		
		createApplyOptionsButton(innerComposite);
	}
	
	protected void createCommandsComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true,false).applyTo(l);
		l.setText("Commands");

		createStopButton(innerComposite);
		createMoveButton(innerComposite);
		
		
		beeCommandingPartOnTeleoperateTab.moveDisabledExplanationLabel = new Label(innerComposite, SWT.None); // spacer
		beeCommandingPartOnTeleoperateTab.moveDisabledExplanationLabel.setText(beeCommandingPartOnTeleoperateTab.LONG_BLANK_STRING);
		beeCommandingPartOnTeleoperateTab.moveDisabledExplanationLabel.setForeground(colorOrange);
	}
	
	protected Composite createInitializationComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		
		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
		l.setText("Initialization");

		createGrabControlButton(innerComposite);
		
		Label hsep = new Label(innerComposite, SWT.SEPARATOR | SWT.HORIZONTAL); // spacer
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true).applyTo(hsep);
		
		Composite innerComposite2 = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_BOTH);
		Label locationBookmarkLabel = new Label(innerComposite2, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(locationBookmarkLabel);
		locationBookmarkLabel.setText("Locations");
		
		return innerComposite;
	}
	
	private void createBookmarkLine(Composite parent) {
		loadLocationBookmarkList();

		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo = new Combo(innerComposite, SWT.READ_ONLY);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.setEnabled(false);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.setLayoutData(gd);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.setItems(beeCommandingPartOnTeleoperateTab.bookmarkNames);
		//		bookmarksCombo.setEnabled(false);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.setText(beeCommandingPartOnTeleoperateTab.NO_BOOKMARK_SELECTED_STRING);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.setToolTipText(BOOKMARKS_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (beeCommandingPartOnTeleoperateTab.NO_BOOKMARK_SELECTED_STRING.equals(beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.getText())) {
					return;
				}

				StationBookmark bookmark = BookmarkListBuilder.getStaticBookmarkList().getBookmarkFromName(beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.getText());
				if(bookmark == null) {
					logger.error("Chosen Location " + beeCommandingPartOnTeleoperateTab.locationBookmarksCombo.getText() + " was null");
					return;
				}
				
				beeCommandingPartOnTeleoperateTab.justSetLocationBookmark = true;

				beeCommandingPartOnTeleoperateTab.getDraggablePreview().setX(bookmark.getLocation().getX());
				beeCommandingPartOnTeleoperateTab.getDraggablePreview().setY(bookmark.getLocation().getY());
				beeCommandingPartOnTeleoperateTab.getDraggablePreview().setZ(bookmark.getLocation().getZ());

				beeCommandingPartOnTeleoperateTab.getDraggablePreview().setRoll((int)bookmark.getLocation().getRoll());
				beeCommandingPartOnTeleoperateTab.getDraggablePreview().setPitch((int)bookmark.getLocation().getPitch());
				beeCommandingPartOnTeleoperateTab.getDraggablePreview().setYaw((int)bookmark.getLocation().getYaw());

				// so you can still see what bookmark you selected
				new Thread( new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(200);
							beeCommandingPartOnTeleoperateTab.justSetLocationBookmark = false;
						} catch (InterruptedException e) {
							//
						}
					}
				}).start();
			}
		});
	}
	
	private void loadLocationBookmarkList() {
		beeCommandingPartOnTeleoperateTab.locationBookmarks = BookmarkListBuilder.getStaticBookmarkList();

		if(beeCommandingPartOnTeleoperateTab.locationBookmarks == null) {
			beeCommandingPartOnTeleoperateTab.bookmarkNames = new String[]{NO_BOOKMARKS_FOUND_STRING};
		} else {
			String[] justBookmarks = beeCommandingPartOnTeleoperateTab.locationBookmarks.getArrayOfNames();
			beeCommandingPartOnTeleoperateTab.bookmarkNames = new String[justBookmarks.length + 1];
			beeCommandingPartOnTeleoperateTab.bookmarkNames[0] = beeCommandingPartOnTeleoperateTab.NO_BOOKMARK_SELECTED_STRING;
			for(int i=0; i<justBookmarks.length; i++) {
				beeCommandingPartOnTeleoperateTab.bookmarkNames[i+1] = justBookmarks[i];
			}
		}
	}
	
	protected void createGrabControlButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		beeCommandingPartOnTeleoperateTab.grabControlButtonOnMainTab = new CommandButton(innerComposite, SWT.NONE);
		beeCommandingPartOnTeleoperateTab.grabControlButtonOnMainTab.setText("Grab Control");
		beeCommandingPartOnTeleoperateTab.grabControlButtonOnMainTab.setToolTipText(WorkbenchConstants.GRAB_CONTROL_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.grabControlButtonOnMainTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.grabControlButtonOnMainTab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.grabControlButtonOnMainTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				beeCommandingPartOnTeleoperateTab.astrobeeStateManager.startRequestingControl();

				beeCommandingPartOnTeleoperateTab.commandPublisher.sendGenericNoParamsCommand(
						ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE,
						ACCESSCONTROL.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	private void createTranslationInput(Composite parent, int index) {
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).span(4,1).applyTo(parent);
		Label leftLabel = new Label(parent, SWT.None);
		leftLabel.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
		leftLabel.setText(beeCommandingPartOnTeleoperateTab.translationInputLabel[index][0]);
		beeCommandingPartOnTeleoperateTab.translationInput[index] = new IncrementableTextHorizontal(parent, 0.0, 0.05);
		beeCommandingPartOnTeleoperateTab.translationInput[index].setAllowableRange(-translationRadius, translationRadius);
		beeCommandingPartOnTeleoperateTab.translationInput[index].setToolTipText(TRANSLATION_TOOLTIP[index]);
		beeCommandingPartOnTeleoperateTab.translationInput[index].setArrowToolTipText("m");
		GridData gd = new GridData(SWT.CENTER, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		
		beeCommandingPartOnTeleoperateTab.translationInput[index].setLayoutData(gd);
	
		Label rightLabel = new Label(parent, SWT.NONE);
		rightLabel.setText(beeCommandingPartOnTeleoperateTab.translationInputLabel[index][1]);
		rightLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, true));
	}

	private void createRotationInput(Composite parent, int index) {
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).span(3,1).applyTo(parent);
		Label rotationAxisLabel = new Label(parent, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).applyTo(rotationAxisLabel);
		rotationAxisLabel.setText(beeCommandingPartOnTeleoperateTab.rotationInputLabel[index]);
		rotationAxisLabel.setToolTipText(ROTATION_TOOLTIP[index]);
		rotationAxisLabel.setForeground(beeCommandingPartOnTeleoperateTab.rotationColor[index]);
		
		beeCommandingPartOnTeleoperateTab.rotationInput[index] = new IncrementableTextHorizontalInt(parent, 0, 15);
		beeCommandingPartOnTeleoperateTab.rotationInput[index].setToolTipText(ROTATION_TOOLTIP[index]);
		beeCommandingPartOnTeleoperateTab.rotationInput[index].setArrowToolTipText("deg");
		
		GridData gd = new GridData(SWT.CENTER, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		beeCommandingPartOnTeleoperateTab.rotationInput[index].setLayoutData(gd);
		beeCommandingPartOnTeleoperateTab.rotationInput[index].setAllowableRange(MIN_ROTATION, MAX_ROTATION);
	}
	
	protected void createCheckboxesColumn(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);
		createFaceForwardButton(innerComposite);
		createCheckObstaclesButton(innerComposite);
		createCheckKeepoutsButton(innerComposite);
	}
	
	protected void createCheckmarksColumn(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		beeCommandingPartOnTeleoperateTab.faceForwardCheckmark = new Label(innerComposite, SWT.None);
		beeCommandingPartOnTeleoperateTab.faceForwardCheckmark.setImage(beeCommandingPartOnTeleoperateTab.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(beeCommandingPartOnTeleoperateTab.faceForwardCheckmark);
		
		beeCommandingPartOnTeleoperateTab.checkObstaclesCheckmark = new Label(innerComposite, SWT.None);
		beeCommandingPartOnTeleoperateTab.checkObstaclesCheckmark.setImage(beeCommandingPartOnTeleoperateTab.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(beeCommandingPartOnTeleoperateTab.checkObstaclesCheckmark);
		
		beeCommandingPartOnTeleoperateTab.checkKeepoutsCheckmark = new Label(innerComposite, SWT.None);
		beeCommandingPartOnTeleoperateTab.checkKeepoutsCheckmark.setImage(beeCommandingPartOnTeleoperateTab.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(beeCommandingPartOnTeleoperateTab.checkKeepoutsCheckmark);
	}
	
	protected void createFaceForwardButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		beeCommandingPartOnTeleoperateTab.faceForwardButton = new Button(innerComposite, SWT.CHECK);
		beeCommandingPartOnTeleoperateTab.faceForwardButton.setText(beeCommandingPartOnTeleoperateTab.FACE_FORWARD_BUTTON_STRING);
		beeCommandingPartOnTeleoperateTab.faceForwardButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.faceForwardButton.setToolTipText(beeCommandingPartOnTeleoperateTab.FACE_FORWARD_BUTTON_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.faceForwardButton.setSelection(true);
	}

	protected void createCheckObstaclesButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		beeCommandingPartOnTeleoperateTab.checkObstaclesButton = new Button(innerComposite, SWT.CHECK);
		beeCommandingPartOnTeleoperateTab.checkObstaclesButton.setText(beeCommandingPartOnTeleoperateTab.CHECK_OBSTACLES_BUTTON_STRING);
		beeCommandingPartOnTeleoperateTab.checkObstaclesButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.checkObstaclesButton.setToolTipText(beeCommandingPartOnTeleoperateTab.CHECK_OBSTACLES_BUTTON_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.checkObstaclesButton.setSelection(true);
	}

	protected void createCheckKeepoutsButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		beeCommandingPartOnTeleoperateTab.checkKeepoutsButton = new Button(innerComposite, SWT.CHECK);
		beeCommandingPartOnTeleoperateTab.checkKeepoutsButton.setText(beeCommandingPartOnTeleoperateTab.CHECK_KEEPOUTS_BUTTON_STRING);
		beeCommandingPartOnTeleoperateTab.checkKeepoutsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.checkKeepoutsButton.setToolTipText(beeCommandingPartOnTeleoperateTab.CHECK_KEEPOUTS_BUTTON_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.checkKeepoutsButton.setSelection(true);
	}

	protected void createMoveButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		beeCommandingPartOnTeleoperateTab.moveButton = new CommandButton(innerComposite, SWT.NONE);
		beeCommandingPartOnTeleoperateTab.moveButton.setText("Move");
		beeCommandingPartOnTeleoperateTab.moveButton.setToolTipText(MOVE_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.moveButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		beeCommandingPartOnTeleoperateTab.moveButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		beeCommandingPartOnTeleoperateTab.moveButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double x = beeCommandingPartOnTeleoperateTab.translationInput[0].getNumber();
				double y = beeCommandingPartOnTeleoperateTab.translationInput[1].getNumber();
				double z = beeCommandingPartOnTeleoperateTab.translationInput[2].getNumber();

				double roll = beeCommandingPartOnTeleoperateTab.rotationInput[0].getNumber();
				double pitch = beeCommandingPartOnTeleoperateTab.rotationInput[1].getNumber();
				double yaw = beeCommandingPartOnTeleoperateTab.rotationInput[2].getNumber();

				//System.out.println("building ABSOLUTE cmd " + x + ", " + y + ", " + z + "; " + roll/RAD_TO_DEG +", " + pitch/RAD_TO_DEG + ", " + yaw/RAD_TO_DEG);
				beeCommandingPartOnTeleoperateTab.commandPublisher.sendTranslateRotateCommandFromAbsoluteCoordinates
					(x, y, z, roll/RAD_TO_DEG, pitch/RAD_TO_DEG, yaw/RAD_TO_DEG);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		beeCommandingPartOnTeleoperateTab.moveButton.setCompositeEnabled(false);
	}
	
	protected void createApplyOptionsButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		
		GridDataFactory.fillDefaults().grab(true, false).span(2,1).applyTo(innerComposite);
		
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab = new CommandButton(innerComposite, SWT.NONE);
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab.setText("Apply Options");
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab.setToolTipText(APPLY_OPTIONS_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(beeCommandingPartOnTeleoperateTab.faceForwardButton.getSelection()) {
					beeCommandingPartOnTeleoperateTab.commandPublisher.sendEnableHolonomicCommand(false);
				} else {
					beeCommandingPartOnTeleoperateTab.commandPublisher.sendEnableHolonomicCommand(true);
				}

				if(beeCommandingPartOnTeleoperateTab.checkObstaclesButton.getSelection()) {
					beeCommandingPartOnTeleoperateTab.commandPublisher.sendCheckObstaclesCommand(true);
				} else {
					beeCommandingPartOnTeleoperateTab.commandPublisher.sendCheckObstaclesCommand(false);
				}

				if(beeCommandingPartOnTeleoperateTab.checkKeepoutsButton.getSelection()) {
					beeCommandingPartOnTeleoperateTab.commandPublisher.sendCheckKeepoutsCommand(true);
				} else {
					beeCommandingPartOnTeleoperateTab.commandPublisher.sendCheckKeepoutsCommand(false);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		beeCommandingPartOnTeleoperateTab.applyOptionsOnMainTab.setCompositeEnabled(false);
	}

	protected void createStopButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		beeCommandingPartOnTeleoperateTab.stopButtonOnMainTab = new CommandButton(innerComposite, SWT.NONE);
		beeCommandingPartOnTeleoperateTab.stopButtonOnMainTab.setText(WorkbenchConstants.STOP_BUTTON_TEXT);
		beeCommandingPartOnTeleoperateTab.stopButtonOnMainTab.setToolTipText(WorkbenchConstants.STOP_TOOLTIP);
		beeCommandingPartOnTeleoperateTab.stopButtonOnMainTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		beeCommandingPartOnTeleoperateTab.stopButtonOnMainTab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		beeCommandingPartOnTeleoperateTab.stopButtonOnMainTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				beeCommandingPartOnTeleoperateTab.commandPublisher.sendGenericNoParamsCommand(
						MOBILITY_METHOD_STOPALLMOTION.VALUE,
						MOBILITY.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
}

