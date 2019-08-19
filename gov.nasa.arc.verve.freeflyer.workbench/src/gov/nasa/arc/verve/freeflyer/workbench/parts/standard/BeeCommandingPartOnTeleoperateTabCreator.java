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
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.utils.TrackVisibleBeeCommandingSubtab;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontal;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.MOBILITY;
import rapid.MOBILITY_METHOD_STOPALLMOTION;

public class BeeCommandingPartOnTeleoperateTabCreator {
	private static Logger logger = Logger.getLogger(BeeCommandingPartOnTeleoperateTabCreator.class);

	protected BeeCommandingPartOnTeleoperateTab commandingPart;
	protected double translationRadius = 20;
	protected final double MIN_ROTATION = -180, MAX_ROTATION = 180;

	protected final double RAD_TO_DEG = 180.0 / Math.PI;
	protected final Color colorOrange = ColorProvider.get(238,118,0);
	protected String NO_BOOKMARKS_FOUND_STRING = "No Bookmarks Found";

	protected final String BOOKMARKS_TOOLTIP = "Enter Premade Set of Coordinates into Manual Inputs";
	protected final String MOVE_TOOLTIP = "Move Astrobee to Pose Specified in Manual Inputs";
	protected final String APPLY_OPTIONS_TOOLTIP = "Send Selected Options to Astrobee";

	protected final String ROLL_TOOLTIP = "Rotation about ISS Forward Axis";
	protected final String PITCH_TOOLTIP = "Rotation about ISS Starboard Axis";
	protected final String YAW_TOOLTIP = "Rotation about ISS Nadir Axis";

	protected final String[] ROTATION_TOOLTIP = {ROLL_TOOLTIP, PITCH_TOOLTIP, YAW_TOOLTIP};

	protected final String X_TOOLTIP = "ISS Forward-Aft Axis Coordinate";
	protected final String Y_TOOLTIP = "ISS Starboard-Port Axis Coordinate";
	protected final String Z_TOOLTIP = "ISS Nadir-Zenith Axis Coordinate";

	protected final String[] TRANSLATION_TOOLTIP = {X_TOOLTIP, Y_TOOLTIP, Z_TOOLTIP};

	@Inject
	protected MApplication application;


	protected void createMainTab(BeeCommandingPartOnTeleoperateTab beeCommandingPartOnTeleoperateTab, Composite parent) {
		this.commandingPart = beeCommandingPartOnTeleoperateTab;

		Composite translateTab = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(7, false);
		gl.marginHeight = 0;
		translateTab.setLayout(gl);

		createBookmarksAndPreviewComposite(translateTab);

		Label verticalSeparator = new Label(translateTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator);

		createManualInputsInnerComposite(translateTab);

		Label verticalSeparator2 = new Label(translateTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator2);

		createOptionsComposite(translateTab);

		Label verticalSeparator1 = new Label(translateTab, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(verticalSeparator1);

		createCommandsComposite(translateTab);
	}

	protected void createBookmarksAndPreviewComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(innerComposite);

		Label l = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, false).applyTo(l);
		l.setText("Preview");

		createShowPreviewButton(innerComposite);
		createSnapToBeeButton(innerComposite);

		Label hsep = new Label(innerComposite, SWT.SEPARATOR | SWT.HORIZONTAL); // spacer
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(hsep);

		//Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l2 = new Label(innerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, false).applyTo(l2);
		l2.setText("Location Bookmarks");

		createCreateBookmarkButton(innerComposite);

		createBookmarkCombo(innerComposite);

	}

	protected void createManualInputsInnerComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerCompositeEvenSpacing(parent, 7, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(innerComposite);

		fillManualInputsInnerComposite(innerComposite);
	}

	protected void fillManualInputsInnerComposite(Composite innerComposite) {

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

		createCommandsInnerComposite(innerComposite);
	}

	protected void createCommandsInnerComposite(Composite innerComposite) {
		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);
		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true,false).applyTo(l);
		l.setText("Commands");

		createStopButton(innerComposite);
		createMoveButton(innerComposite);

		commandingPart.moveDisabledExplanationLabel = new Label(innerComposite, SWT.None); // spacer
		commandingPart.moveDisabledExplanationLabel.setText(commandingPart.LONG_BLANK_STRING);
		commandingPart.moveDisabledExplanationLabel.setForeground(colorOrange);
	}

	protected Composite createBookmarkComposite(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(innerComposite);

		Composite innerInnerComposite = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_HORIZONTAL);

		Label l = new Label(innerInnerComposite, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, false).applyTo(l);
		l.setText("Create Location Bookmark");

		createCreateBookmarkButton(innerComposite);

		Composite innerComposite2 = GuiUtils.setupInnerComposite(innerComposite, 1, GridData.FILL_BOTH);
		Label locationBookmarkLabel = new Label(innerComposite2, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(locationBookmarkLabel);
		locationBookmarkLabel.setText("Select Location Bookmark");

		createBookmarkCombo(innerComposite);

		return innerComposite;
	}

	protected void createCreateBookmarkButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		commandingPart.createBookmarkButton = new EnlargeableButton(innerComposite, SWT.TOGGLE);
		commandingPart.createBookmarkButton.setText(commandingPart.CREATE_BOOKMARK_BUTTON_STRING);
		commandingPart.createBookmarkButton.setToolTipText(commandingPart.CREATE_BOOKMARK_BUTTON_TOOLTIP);
		commandingPart.createBookmarkButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.createBookmarkButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.createBookmarkButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						"Save the location of the Teleop Preview as a Bookmark", "Enter a name for the bookmark", "<name>", null);
				if (dlg.open() == Window.OK) {
					String name = dlg.getValue();

					StationBookmark newSB = null;
					ModuleBayStation station = null;;

					IncrementableText[] pos = commandingPart.getCurrentPosition();
					IncrementableText[] rot = commandingPart.getCurrentRotation();

					station = new ModuleBayStation();
					station.setCoordinate(new ModuleBayPoint((float)pos[0].getNumber(),
							(float)pos[1].getNumber(),
							(float)pos[2].getNumber(),
							(float)rot[0].getNumber(),
							(float)rot[1].getNumber(),
							(float)rot[2].getNumber()));
					newSB = new StationBookmark(name, station.getCoordinate());

					BookmarkListBuilder.addBookmark(newSB);
					try {
						BookmarkListBuilder.saveBookmarksList();
					} catch (Exception e1) {
						return;
					}

					updateComboBoxes();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void updateComboBoxes() {
		loadLocationBookmarkList();
		commandingPart.locationBookmarksCombo.setItems(commandingPart.bookmarkNames);
	}

	private void createBookmarkCombo(Composite parent) {
		loadLocationBookmarkList();

		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);
		commandingPart.locationBookmarksCombo = new Combo(innerComposite, SWT.READ_ONLY);
		commandingPart.locationBookmarksCombo.setEnabled(false);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		commandingPart.locationBookmarksCombo.setLayoutData(gd);
		commandingPart.locationBookmarksCombo.setItems(commandingPart.bookmarkNames);
		//		bookmarksCombo.setEnabled(false);
		commandingPart.locationBookmarksCombo.setText(commandingPart.NO_BOOKMARK_SELECTED_STRING);
		commandingPart.locationBookmarksCombo.setToolTipText(BOOKMARKS_TOOLTIP);
		commandingPart.locationBookmarksCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (commandingPart.NO_BOOKMARK_SELECTED_STRING.equals(commandingPart.locationBookmarksCombo.getText())) {
					return;
				}

				StationBookmark bookmark = BookmarkListBuilder.getStaticBookmarkList().getBookmarkFromName(commandingPart.locationBookmarksCombo.getText());
				if(bookmark == null) {
					logger.error("Chosen Location " + commandingPart.locationBookmarksCombo.getText() + " was null");
					return;
				}

				commandingPart.justSetLocationBookmark = true;

				commandingPart.getDraggablePreview().setX(bookmark.getLocation().getX());
				commandingPart.getDraggablePreview().setY(bookmark.getLocation().getY());
				commandingPart.getDraggablePreview().setZ(bookmark.getLocation().getZ());

				commandingPart.getDraggablePreview().setRoll((int)bookmark.getLocation().getRoll());
				commandingPart.getDraggablePreview().setPitch((int)bookmark.getLocation().getPitch());
				commandingPart.getDraggablePreview().setYaw((int)bookmark.getLocation().getYaw());

				// so you can still see what bookmark you selected
				new Thread( new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(200);
							commandingPart.justSetLocationBookmark = false;
						} catch (InterruptedException e) {
							//
						}
					}
				}).start();
			}
		});
	}

	private void loadLocationBookmarkList() {
		commandingPart.locationBookmarks = BookmarkListBuilder.getStaticBookmarkList();

		if(commandingPart.locationBookmarks == null) {
			commandingPart.bookmarkNames = new String[]{NO_BOOKMARKS_FOUND_STRING};
		} else {
			String[] justBookmarks = commandingPart.locationBookmarks.getArrayOfNames();
			commandingPart.bookmarkNames = new String[justBookmarks.length + 1];
			commandingPart.bookmarkNames[0] = commandingPart.NO_BOOKMARK_SELECTED_STRING;
			for(int i=0; i<justBookmarks.length; i++) {
				commandingPart.bookmarkNames[i+1] = justBookmarks[i];
			}
		}
	}

	private void createTranslationInput(Composite parent, int index) {
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).span(4,1).applyTo(parent);
		Label leftLabel = new Label(parent, SWT.None);
		leftLabel.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
		leftLabel.setText(commandingPart.translationInputLabel[index][0]);
		commandingPart.translationInput[index] = new IncrementableTextHorizontal(parent, 0.0, 0.05);
		commandingPart.translationInput[index].setAllowableRange(-translationRadius, translationRadius);
		commandingPart.translationInput[index].setToolTipText(TRANSLATION_TOOLTIP[index]);
		commandingPart.translationInput[index].setArrowToolTipText("m");
		GridData gd = new GridData(SWT.CENTER, SWT.FILL, true, true);
		gd.horizontalSpan = 2;

		commandingPart.translationInput[index].setLayoutData(gd);

		Label rightLabel = new Label(parent, SWT.NONE);
		rightLabel.setText(commandingPart.translationInputLabel[index][1]);
		rightLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, true));
	}

	private void createRotationInput(Composite parent, int index) {
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).span(3,1).applyTo(parent);
		Label rotationAxisLabel = new Label(parent, SWT.None);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).applyTo(rotationAxisLabel);
		rotationAxisLabel.setText(commandingPart.rotationInputLabel[index]);
		rotationAxisLabel.setToolTipText(ROTATION_TOOLTIP[index]);
		rotationAxisLabel.setForeground(commandingPart.rotationColor[index]);

		commandingPart.rotationInput[index] = new IncrementableTextHorizontalInt(parent, 0, 15);
		commandingPart.rotationInput[index].setToolTipText(ROTATION_TOOLTIP[index]);
		commandingPart.rotationInput[index].setArrowToolTipText("deg");

		GridData gd = new GridData(SWT.CENTER, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		commandingPart.rotationInput[index].setLayoutData(gd);
		commandingPart.rotationInput[index].setAllowableRange(MIN_ROTATION, MAX_ROTATION);
	}

	protected void createCheckboxesColumn(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);
		createFaceForwardButton(innerComposite);
		createCheckObstaclesButton(innerComposite);
		createCheckKeepoutsButton(innerComposite);
	}

	protected void createCheckmarksColumn(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		commandingPart.faceForwardCheckmark = new Label(innerComposite, SWT.None);
		commandingPart.faceForwardCheckmark.setImage(commandingPart.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(commandingPart.faceForwardCheckmark);

		commandingPart.checkObstaclesCheckmark = new Label(innerComposite, SWT.None);
		commandingPart.checkObstaclesCheckmark.setImage(commandingPart.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(commandingPart.checkObstaclesCheckmark);

		commandingPart.checkKeepoutsCheckmark = new Label(innerComposite, SWT.None);
		commandingPart.checkKeepoutsCheckmark.setImage(commandingPart.unknownCheckedImage);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(commandingPart.checkKeepoutsCheckmark);
	}

	protected void createFaceForwardButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		commandingPart.faceForwardButton = new Button(innerComposite, SWT.CHECK);
		commandingPart.faceForwardButton.setText(commandingPart.FACE_FORWARD_BUTTON_STRING);
		commandingPart.faceForwardButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		commandingPart.faceForwardButton.setToolTipText(commandingPart.FACE_FORWARD_BUTTON_TOOLTIP);
		commandingPart.faceForwardButton.setSelection(true);
	}

	protected void createCheckObstaclesButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		commandingPart.checkObstaclesButton = new Button(innerComposite, SWT.CHECK);
		commandingPart.checkObstaclesButton.setText(commandingPart.CHECK_OBSTACLES_BUTTON_STRING);
		commandingPart.checkObstaclesButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		commandingPart.checkObstaclesButton.setToolTipText(commandingPart.CHECK_OBSTACLES_BUTTON_TOOLTIP);
		commandingPart.checkObstaclesButton.setSelection(true);
	}

	protected void createCheckKeepoutsButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_BOTH);

		commandingPart.checkKeepoutsButton = new Button(innerComposite, SWT.CHECK);
		commandingPart.checkKeepoutsButton.setText(commandingPart.CHECK_KEEPOUTS_BUTTON_STRING);
		commandingPart.checkKeepoutsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		commandingPart.checkKeepoutsButton.setToolTipText(commandingPart.CHECK_KEEPOUTS_BUTTON_TOOLTIP);
		commandingPart.checkKeepoutsButton.setSelection(true);
	}

	protected String getMoveButtonName() {
		return "Move Absolute";
	}

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

				commandingPart.commandPublisher.sendTranslateRotateCommandFromAbsoluteCoordinates
				(x, y, z, roll/RAD_TO_DEG, pitch/RAD_TO_DEG, yaw/RAD_TO_DEG);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		commandingPart.moveButton.setCompositeEnabled(false);
	}

	protected void createApplyOptionsButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);

		GridDataFactory.fillDefaults().grab(true, false).span(2,1).applyTo(innerComposite);

		commandingPart.applyOptionsOnMainTab = new CommandButton(innerComposite, SWT.NONE);
		commandingPart.applyOptionsOnMainTab.setText("Apply Options");
		commandingPart.applyOptionsOnMainTab.setToolTipText(APPLY_OPTIONS_TOOLTIP);
		commandingPart.applyOptionsOnMainTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		commandingPart.applyOptionsOnMainTab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		commandingPart.applyOptionsOnMainTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(commandingPart.faceForwardButton.getSelection()) {
					commandingPart.commandPublisher.sendEnableHolonomicCommand(false);
				} else {
					commandingPart.commandPublisher.sendEnableHolonomicCommand(true);
				}

				if(commandingPart.checkObstaclesButton.getSelection()) {
					commandingPart.commandPublisher.sendCheckObstaclesCommand(true);
				} else {
					commandingPart.commandPublisher.sendCheckObstaclesCommand(false);
				}

				if(commandingPart.checkKeepoutsButton.getSelection()) {
					commandingPart.commandPublisher.sendCheckKeepoutsCommand(true);
				} else {
					commandingPart.commandPublisher.sendCheckKeepoutsCommand(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
		commandingPart.applyOptionsOnMainTab.setCompositeEnabled(false);
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
				TrackVisibleBeeCommandingSubtab.INSTANCE.setAbsolutePreviewShowing(btn.getSelection());
				commandingPart.freeFlyerScenario.showAbsolutePreview(btn.getSelection());
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

	protected void createSnapToBeeButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		commandingPart.snapToBeeButton = new EnlargeableButton(innerComposite, SWT.PUSH);
		commandingPart.snapToBeeButton.setText(commandingPart.SNAP_TO_BEE_STRING);
		commandingPart.snapToBeeButton.setToolTipText(commandingPart.SNAP_TO_BEE_TOOLTIP);
		commandingPart.snapToBeeButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.snapToBeeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.snapToBeeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandingPart.snapToBee();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	protected void createStopButton(Composite parent) {
		Composite innerComposite = GuiUtils.setupInnerComposite(parent, 1, GridData.FILL_HORIZONTAL);
		commandingPart.stopButtonOnMainTab = new CommandButton(innerComposite, SWT.NONE);
		commandingPart.stopButtonOnMainTab.setText(WorkbenchConstants.STOP_BUTTON_TEXT);
		commandingPart.stopButtonOnMainTab.setToolTipText(WorkbenchConstants.STOP_TOOLTIP);
		commandingPart.stopButtonOnMainTab.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.stopButtonOnMainTab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		commandingPart.stopButtonOnMainTab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandingPart.commandPublisher.sendGenericNoParamsCommand(
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

