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
package gov.nasa.arc.verve.freeflyer.workbench.widget;

import gov.nasa.arc.irg.freeflyer.utils.converters.BayNumberToIntConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.IntToBayNumberConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.IntToWallConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.WallToIntConverter;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap.Wall;
import gov.nasa.arc.irg.plan.model.modulebay.Module;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class ModuleBayWidget implements PropertyChangeListener {
	Combo moduleCombo, bayNumberCombo, wallOneCombo, wallTwoCombo, orientationCombo;
	Button centerOneCheckbox, centerTwoCheckbox, ignoreOrientationCheckbox;
	IncrementableText wallOneOffsetText, wallTwoOffsetText;
	List data;
	private int wholeWidth = 5;
	IntToWallConverter intToWallOneConverter,intToWallTwoConverter;
	WallToIntConverter wallOneToIntConverter, wallTwoToIntConverter;
	IntToBayNumberConverter intToBayNumberConverter;
	BayNumberToIntConverter bayNumberToIntConverter;
	double minOffset = 0, maxOffset = 4;
	String validString = "          ", overriddenString = "Overridden";
	StationAndPointWidget parent;
	Label validLabel;
	
	int WALL_AND_ORIENTATION_COMBO_WIDTH = 65;
	int CHECKBOX_WIDTH = 60;
	int MODULE_AND_BAY_COMBO_WIDTH = WALL_AND_ORIENTATION_COMBO_WIDTH + CHECKBOX_WIDTH + 5;
	int M_WIDTH = 30;
	
	public Composite createModuleBayComposite(StationAndPointWidget parent, Composite container,
			IntToWallConverter intToWallOneConverter,
			IntToWallConverter intToWallTwoConverter,
			WallToIntConverter wallOneToIntConverter,
			WallToIntConverter wallTwoToIntConverter, 
			IntToBayNumberConverter intToBayNumberConverter,
			BayNumberToIntConverter bayNumberToIntConverter) {
		Composite tabComposite = new Composite(container, SWT.NONE);
		this.parent = parent;
		this.intToWallOneConverter = intToWallOneConverter;
		this.intToWallTwoConverter = intToWallTwoConverter;
		this.wallOneToIntConverter = wallOneToIntConverter;
		this.wallTwoToIntConverter = wallTwoToIntConverter;
		this.intToBayNumberConverter = intToBayNumberConverter;
		this.bayNumberToIntConverter = bayNumberToIntConverter;
		initUI(tabComposite);
		return tabComposite;
	}

	private void initUI(Composite container) {
		GridLayout layout = new GridLayout(wholeWidth, false);
		container.setLayout(layout);

		new Label(container, SWT.NONE).setText("Module");
		setupModule(container);

		validLabel = new Label(container, SWT.NONE);
		updateValidLabel();
		new Label(container, SWT.NONE);

		new Label(container, SWT.NONE).setText("Bay");
		setupBay(container);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		new Label(container, SWT.NONE).setText("Offset Wall 1");
		setupWallOne(container);

		new Label(container, SWT.NONE).setText("Offset Wall 2");
		setupWallTwo(container);

		new Label(container, SWT.NONE).setText("Orientation");
		setupOrientation(container);
	}

	void updateValidLabel() {
		ModuleBayStation mbs = (ModuleBayStation) parent.getModel();
		if(mbs != null && mbs.getCoordinate().isModuleBayValid()) {
			validLabel.setText(validString);
		} else {
			validLabel.setText(overriddenString);
			if(bayNumberCombo != null) {
				bayNumberCombo.setEnabled(false);
			}
		}
	}
	
	private void setupModule(Composite container) {
		moduleCombo = setupCombo(container);
		moduleCombo.setItems(toStringArray(ModuleName.values()));	

		GridData gdata = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gdata.horizontalSpan = 2;
		gdata.widthHint = MODULE_AND_BAY_COMBO_WIDTH;
		//gdata.grabExcessHorizontalSpace = true;
		moduleCombo.setLayoutData(gdata);	
		moduleCombo.setEnabled(true);
		moduleCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String moduleName = moduleCombo.getText();
				ModuleBayPoint point = ((ModuleBayStation) parent.getModel()).getCoordinate();
				Module.ModuleName changeTo = Module.ModuleName.fromString(moduleName);
				
				if(!point.isModuleBayValid() || !point.getModule().equals(changeTo)) {
					point.setModule(changeTo);
				}

				String[] bays = getBays(moduleName);
				
				bayNumberCombo.setItems(bays);
				bayNumberCombo.setEnabled(true);
				
				// if there is no bay already, set the bay in the point itself
				// otherwise, don't overwrite the bay.
				if(point.getBayNumber() == null) {
					point.setBayNumber(BayNumber.fromOrdinal(bays.length/2));
				}
			}
		});
	}

	private void setupBay(Composite container) {
		bayNumberCombo = setupCombo(container);
		GridData gdata = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gdata.horizontalSpan = 2;
		gdata.widthHint = MODULE_AND_BAY_COMBO_WIDTH;
		bayNumberCombo.setLayoutData(gdata);	
		bayNumberCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String[] validWalls = LocationMap.getInstance().wallsToStringArray(LocationMap.getInstance().getValidWalls(moduleCombo.getText()));
				setWallOneComboItems(validWalls);
				setWallTwoComboItems(new String[0]);
			}
		});
	}

	private void setupWallOne(Composite container) {
		centerOneCheckbox = new Button(container, SWT.CHECK);
		centerOneCheckbox.setText("Center");
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gd.widthHint = this.CHECKBOX_WIDTH;
		centerOneCheckbox.setLayoutData(gd);	
		centerOneCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// enable the right things
				boolean centerOne = centerOneCheckbox.getSelection();
				wallOneCombo.setEnabled(!centerOne);
				wallOneOffsetText.setEnabled(!centerOne);
				
				// set a default wall if none has been set yet
				if(!centerOne) {
					if(wallOneCombo.getText().equals("")) {
						wallOneCombo.setText(wallOneCombo.getItem(0));
						((ModuleBayStation) parent.getModel()).getCoordinate().setWallOne(Wall.fromString(wallOneCombo.getItem(0)));
					}
				}
				
				centerTwoCheckbox.setEnabled(!centerOneCheckbox.getSelection());
				wallTwoCombo.setEnabled(!centerOneCheckbox.getSelection() && !centerTwoCheckbox.getSelection());
				wallTwoOffsetText.setEnabled(!centerOneCheckbox.getSelection() && !centerTwoCheckbox.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }		
		});
		centerOneCheckbox.setSelection(true);
		
		wallOneCombo = setupCombo(container);
		GridData gdCombo = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gdCombo.widthHint = this.WALL_AND_ORIENTATION_COMBO_WIDTH;
		wallOneCombo.setLayoutData(gdCombo);	
		wallOneCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setWallTwoComboItems(getValidWallsForWallTwo());
			}
		});

		// databound
		wallOneOffsetText = setupOffsetText(container);

		setupMLabel(container);	
	}

	private void setupWallTwo(Composite container) {
		centerTwoCheckbox = new Button(container, SWT.CHECK);
		centerTwoCheckbox.setText("Center");
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gd.widthHint = this.CHECKBOX_WIDTH;
		centerTwoCheckbox.setLayoutData(gd);	
		centerTwoCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean centerTwo = centerTwoCheckbox.getSelection();
				wallTwoCombo.setEnabled(!centerTwo);
				wallTwoOffsetText.setEnabled(!centerTwo);
				
				// set a default wall if necessary
				if(!centerTwo) {
					if(wallTwoCombo.getText().equals("")) {
						wallTwoCombo.setText(wallTwoCombo.getItem(0));
						((ModuleBayStation) parent.getModel()).getCoordinate().setWallTwo(Wall.fromString(wallTwoCombo.getItem(0)));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }		
		});
		centerTwoCheckbox.setSelection(true);
		centerTwoCheckbox.setEnabled(false);
		
		// databound
		wallTwoCombo = setupCombo(container);
		GridData gdCombo = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gdCombo.widthHint = this.WALL_AND_ORIENTATION_COMBO_WIDTH;
		wallTwoCombo.setLayoutData(gdCombo);	
		
		wallTwoOffsetText = setupOffsetText(container);

		setupMLabel(container);
		
	}

	private void setupMLabel(Composite container) {
		Label mLabel = new Label(container, SWT.NONE);
		mLabel.setText("m");
		GridData mgd = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		mgd.widthHint = M_WIDTH;
		mLabel.setLayoutData(mgd);
	}
	
	private void setupOrientation(Composite container) {
		ignoreOrientationCheckbox = new Button(container, SWT.CHECK);
		ignoreOrientationCheckbox.setText("N/A");
		ignoreOrientationCheckbox.setSelection(true);
		ignoreOrientationCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				orientationCombo.setEnabled(!ignoreOrientationCheckbox.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }		
		});
		
		orientationCombo = setupCombo(container);
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, true);
		gd.widthHint = this.WALL_AND_ORIENTATION_COMBO_WIDTH;
		orientationCombo.setLayoutData(gd);	
		orientationCombo.setItems(LocationMap.getInstance().allWallsAsStringArray());
		
		orientationCombo.setEnabled(false);
	}

	public void enableTheRightCombos() {
		wallOneCombo.setEnabled(!centerOneCheckbox.getSelection());
		wallOneOffsetText.setEnabled(!centerOneCheckbox.getSelection());
		
		centerTwoCheckbox.setEnabled(!centerOneCheckbox.getSelection());
		
		wallTwoCombo.setEnabled(!centerTwoCheckbox.getSelection());
		wallTwoOffsetText.setEnabled(!centerTwoCheckbox.getSelection());
		
		orientationCombo.setEnabled(!ignoreOrientationCheckbox.getSelection());
	}

	private void setWallOneComboItems(String[] arr) {
		wallOneCombo.setItems(arr);
		intToWallOneConverter.setItems(arr);
		wallOneToIntConverter.setItems(arr);
	}

	private void setWallTwoComboItems(String[] arr) {
		wallTwoCombo.setItems(arr);
		intToWallTwoConverter.setItems(arr);
		wallTwoToIntConverter.setItems(arr);
	}

	private IncrementableText setupOffsetText(Composite container) {
		IncrementableText distanceText = new IncrementableText(container, 0.0, 0.1);
		distanceText.setAllowableRange(minOffset, maxOffset);
		
		distanceText.setEnabled(false);
		distanceText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
		return distanceText;
	}

	private Combo setupCombo(Composite parent) {
		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setEnabled(false);
		return combo;
	}

	private String[] getBays(String module) {
		ModuleName moduleName = LocationMap.getInstance().getModule(module);
		int numBays = LocationMap.getInstance().numBays(moduleName);
		int minBayNum = LocationMap.getInstance().getMinBayNum(moduleName);
		intToBayNumberConverter.setStartBay(minBayNum);
		bayNumberToIntConverter.setStartBay(minBayNum);

		if(numBays < 0) {
			throw new IllegalArgumentException();
		}
		
		if(numBays == 0) {
			return new String[0];
		}

		String[] bays = new String[numBays * 2 - 1];

		int start = minBayNum * 2;
		for(int i = 0; i < numBays * 2 - 1; i++ ){
			bays[i] = BayNumber.values()[start + i].getDescriptor();
		}

		return bays;
	}

	private String[] toStringArray(ModuleName[] in) {
		String[] out = new String[in.length];

		int i=0;
		for(ModuleName mod : in) {
			out[i++] = mod.getName();
		}

		return out;
	}

	private String[] getValidWallsForWallTwo() {
		// get valid walls for moduleCombo
		ArrayList<Wall> validWalls = LocationMap.getInstance().getValidWalls(moduleCombo.getText());

		Wall w1 = LocationMap.getInstance().getWall(wallOneCombo.getText());

		switch(w1) {
		case FWD:
		case AFT:
			validWalls.remove(Wall.FWD);
			validWalls.remove(Wall.AFT);
			break;
		case STBD:
		case PORT:
			validWalls.remove(Wall.STBD);
			validWalls.remove(Wall.PORT);
			break;
		case DECK:
		case OVHD:
			validWalls.remove(Wall.DECK);
			validWalls.remove(Wall.OVHD);
			break;
		default:
			throw new IllegalArgumentException();
		}

		String[] strings = new String[validWalls.size()];
		for(int i = 0; i < validWalls.size(); i++) {
			strings[i] = validWalls.get(i).getName();
		}

		return strings;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		updateValidLabel();
	}
	
	public void dispose() {
		((ModuleBayStation) parent.getModel()).getCoordinate().removePropertyChangeListener("moduleBayValid", this);
	}
}
