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
import gov.nasa.arc.irg.freeflyer.utils.converters.IntToModuleConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.IntToWallConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.ModuleToIntConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.WallToIntConverter;
import gov.nasa.arc.irg.plan.converters.BookmarkToIntConverter;
import gov.nasa.arc.irg.plan.converters.IntToBookmarkConverter;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CreateValueStrategy;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsListener;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

// This class lives here because it needs access to DelegateCommandStack

// This class is symbiotic with MakeAddCommandSection, CoordinateWidget, BookmarksWidget, and ModuleBayWidget
// They should all be one class, except it would be too long, so it's split up
public class StationAndPointWidget extends AbstractDatabindingWidget implements PlanEditsListener {
//	private Logger logger = Logger.getLogger(StationAndPointWidget.class);

	protected List<IncrementableText> m_incrementables = new ArrayList<IncrementableText>();	// list of child controls
	protected Label titleLabel;
	protected Text toleranceText;
	protected CoordinateWidget coordinatesWidget;
	protected Combo subcommandCombo;
	protected IncrementableText xText, yText, zText;
	protected IncrementableTextHorizontalInt rollText, pitchText, yawText;
	protected Button orientationCheckbox;
	protected ModuleBayWidget moduleBayWidget;
	protected BookmarksWidget bookmarksWidget;
	protected MakeAddCommandSection subcommandsWidget;
	
	protected IntToWallConverter intToWallOneConverter = new IntToWallConverter();
	protected IntToWallConverter intToWallTwoConverter = new IntToWallConverter();
	protected WallToIntConverter wallOneToIntConverter = new WallToIntConverter();
	protected WallToIntConverter wallTwoToIntConverter = new WallToIntConverter();
	protected IntToBayNumberConverter intToBayNumberConverter = new IntToBayNumberConverter();
	protected BayNumberToIntConverter bayNumberToIntConverter = new BayNumberToIntConverter();
	protected boolean showEngineeringConfig = false;
	
	protected String moduleName = "coordinate.module";
	protected String bayNumberName = "coordinate.bayNumber";
	protected String wallOneName = "coordinate.wallOne";
	protected String wallTwoName = "coordinate.wallTwo";
	protected String orientationWallName = "coordinate.orientationWall";
	protected String bookmarkName = "coordinate.bookmark";
	
	public StationAndPointWidget(Composite parent, int style) {
		super(parent, style);
		
		showEngineeringConfig = WorkbenchConstants.isFlagPresent(WorkbenchConstants.SHOW_ENGINEERING_CONFIGURATION_STRING);
		createControls(parent);
		PlanEditsRegistry.addListener(this);
	}

	/**
	 * Actually create the UI components
	 * @param container
	 */
	public void createControls(Composite container) {
		if (m_dataBindingContext == null){
			m_dataBindingContext = new DataBindingContext();
		}
		GuiUtils.giveGridLayout(this, 3);

		setupTitleData();

		TabFolder tabFolder = setupTabFolder();

		makeModuleBayTab(tabFolder);
		makeCoordinatesTab(tabFolder);		
		makeBookmarksTab(tabFolder);		

		makeCommandsTab(tabFolder);
	}
	
	protected void makeCommandsTab(TabFolder tabFolder) {
		TabItem commandsTab = new TabItem(tabFolder, SWT.None);
		commandsTab.setText("Commands");
		
		setLayout(new GridLayout(3, false));

		MakeAddCommandSection commandsWidget = new MakeAddCommandSection();
		Composite top = commandsWidget.createCommandArea(this, tabFolder);

		commandsTab.setControl(top);
	}
	
	protected void makeModuleBayTab(TabFolder tabFolder) {
		TabItem coordinatesTab = new TabItem(tabFolder, SWT.NONE);
		coordinatesTab.setText("Location Based");

		moduleBayWidget = new ModuleBayWidget();
		Composite otherComposite = moduleBayWidget.createModuleBayComposite(this, tabFolder,
				intToWallOneConverter, intToWallTwoConverter, 
				wallOneToIntConverter, wallTwoToIntConverter,
				intToBayNumberConverter, bayNumberToIntConverter);
		coordinatesTab.setControl(otherComposite);
	}
	
	protected void makeCoordinatesTab(TabFolder tabFolder) {
		TabItem coordinatesTab = new TabItem(tabFolder, SWT.NONE);
		coordinatesTab.setText("Coordinate Based");

		setLayout(new GridLayout(3, false));

		coordinatesWidget = new CoordinateWidget();
		Composite top = coordinatesWidget.createCoordinateArea(this, tabFolder);

		coordinatesTab.setControl(top);
	}	
	
	protected void makeBookmarksTab(TabFolder tabFolder) {
		TabItem bookmarksTab = new TabItem(tabFolder, SWT.NONE);
		bookmarksTab.setText("Bookmarks");

		setLayout(new GridLayout(3, false));

		bookmarksWidget = new BookmarksWidget();
		Composite top = bookmarksWidget.createBookmarksArea(this, tabFolder);

		bookmarksTab.setControl(top);
	}	
	
	protected TabFolder setupTabFolder() {
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		GridData threeData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		threeData.horizontalSpan = 3;
		tabFolder.setLayoutData(threeData);
		tabFolder.setSize(300, 250);
		return tabFolder;
	}

	protected void setupTitleData() {
		GridData titleData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		titleData.horizontalSpan = 1;

		titleLabel = new Label(this, SWT.BORDER);
		titleLabel.setLayoutData(titleData);

		Font bigFont = GuiUtils.makeBigFont(this, titleLabel, 14);
		titleLabel.setFont( bigFont );
		addChildLabel(titleLabel);
		updateNonBoundFields();
	}

	@Override
	public void dispose() {
		PlanEditsRegistry.removeListener(this);

		for(IncrementableText it : m_incrementables) {
			it.dispose();
		}
		moduleBayWidget.dispose();
		bookmarksWidget.dispose();
		super.dispose();
	}

	@Override
	public boolean bindUI(Realm realm) {

		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bind("coordinate.x", xText.getTextControl());
		result &= bind("coordinate.y", yText.getTextControl());
		result &= bind("coordinate.z", zText.getTextControl());

		result &= bind("coordinate.roll", rollText.getTextControl());
		result &= bind("coordinate.pitch", pitchText.getTextControl());
		result &= bind("coordinate.yaw", yawText.getTextControl());
		result &= bindBoolean("coordinate.ignoreOrientation", orientationCheckbox);
		result &= bind("tolerance", toleranceText);
		
		result &= bind(moduleName, moduleBayWidget.moduleCombo);
		result &= bind(bayNumberName, moduleBayWidget.bayNumberCombo);
		result &= bindBoolean("coordinate.centerOne", moduleBayWidget.centerOneCheckbox);
		result &= bind(wallOneName, moduleBayWidget.wallOneCombo);
		result &= bind("coordinate.wallOneOffset", moduleBayWidget.wallOneOffsetText.getTextControl());
		result &= bindBoolean("coordinate.centerTwo", moduleBayWidget.centerTwoCheckbox);
		result &= bind(wallTwoName, moduleBayWidget.wallTwoCombo);
		result &= bind("coordinate.wallTwoOffset", moduleBayWidget.wallTwoOffsetText.getTextControl());
		result &= bindBoolean("coordinate.ignoreOrientation", moduleBayWidget.ignoreOrientationCheckbox);
		result &= bind(orientationWallName, moduleBayWidget.orientationCombo);

		result &= bind(bookmarkName, bookmarksWidget.bookmarksCombo);
		
		moduleBayWidget.enableTheRightCombos();
		setBound(result);
		layout(true,true);
		return result;
	}
	
	protected boolean bindBoolean(String feature, final Button buttonWidget) {
		if(buttonWidget == null) {
			return false;
		}
		
		ISWTObservableValue moduleTarget = WidgetProperties.selection().observe(buttonWidget);
		IObservableValue moduleModel = BeanProperties.value(ModuleBayStation.class, feature).observe(getModel());
		m_dataBindingContext.bindValue(moduleTarget, moduleModel);

		return true;
	}
	
	@Override
	protected UpdateValueStrategy getModelToTargetStrategy(String feature){
		if(feature.equals(moduleName)) {
			return getUpdateValueStrategyFromConverter(new ModuleToIntConverter());
		}
		else if(feature.equals(bayNumberName)) {
			return getUpdateValueStrategyFromConverter(bayNumberToIntConverter);
		}
		else if(feature.equals(wallOneName)) {
			return getUpdateValueStrategyFromConverter(wallOneToIntConverter);
		}
		else if(feature.equals(wallTwoName)) {
			return getUpdateValueStrategyFromConverter(wallTwoToIntConverter);
		}
		else if(feature.equals(orientationWallName)) {
			return getUpdateValueStrategyFromConverter(new WallToIntConverter());
		}
		else if(feature.equals(bookmarkName)) {
			return getUpdateValueStrategyFromConverter(new BookmarkToIntConverter());
		}
		return null;
	}
	
	@Override
	protected UpdateValueStrategy getTargetToModelStrategy(String feature){
		if(feature.equals(moduleName)) {
			return getUpdateValueStrategyFromConverter(new IntToModuleConverter());
		}
		else if(feature.equals(bayNumberName)) {
			return getUpdateValueStrategyFromConverter(intToBayNumberConverter);
		}
		else if(feature.equals(wallOneName)) {
			return getUpdateValueStrategyFromConverter(intToWallOneConverter);
		}
		else if(feature.equals(wallTwoName)) {
			return getUpdateValueStrategyFromConverter(intToWallTwoConverter);
		}
		else if(feature.equals(orientationWallName)) {
			return getUpdateValueStrategyFromConverter(new IntToWallConverter());
		}
		else if(feature.equals(bookmarkName)) {
			return getUpdateValueStrategyFromConverter(new IntToBookmarkConverter());
		}
		
		return CreateValueStrategy.getTargetToModelStrategy(feature);
	}
	
	protected UpdateValueStrategy getUpdateValueStrategyFromConverter(IConverter converter) {
		UpdateValueStrategy moduleTargetStrategy = new UpdateValueStrategy();
		return moduleTargetStrategy.setConverter(converter);
	}
	
	// called from commands in DelegateCommandStack
	public void onStationMoved(Station moved) {
		xText.setTextString(moved.getCoordinate().getX());
		yText.setTextString(moved.getCoordinate().getY());
		zText.setTextString(moved.getCoordinate().getZ());
	}

	// XXX only called from defunct reposition feature?
	public void moveTo(double x, double y, double z) {
		xText.setTextString(x);
		yText.setTextString(y);
		zText.setTextString(z);
	}

	/** in degrees */
	// XXX not called?
	public void setRotationTo(double roll, double pitch, double yaw) {
		rollText.setTextString(roll);
		pitchText.setTextString(pitch);
		yawText.setTextString(yaw);
	}

	public void enableCoordinateRollPitchYaw() {
		orientationCheckbox.setSelection(false);
		rollText.enable();
		pitchText.enable();
		yawText.enable();
	}
	
	public void disableCoordinateRollPitchYaw() {
		orientationCheckbox.setSelection(true);
		rollText.disable();
		pitchText.disable();
		yawText.disable();
	}
	
	private void updateNonBoundFields() {
		if(getModel() instanceof Station) {		
			Station me = (Station) getModel();
			titleLabel.setText(me.getName() + " Station");
		}
	}

	@Override
	public void setModel(Object obj) {
		if (obj == null ){
			return;
		}

		unbindUI();
		if(obj != m_model) {
			if(m_model != null) {
				ModuleBayPoint mbpOld = ((ModuleBayStation) m_model).getCoordinate();
				mbpOld.removePropertyChangeListener("moduleBayValid", moduleBayWidget);
				mbpOld.removePropertyChangeListener(bookmarksWidget);
			}
			if(obj != null) {
				ModuleBayPoint mbpNew = ((ModuleBayStation) obj).getCoordinate();
				mbpNew.addPropertyChangeListener("moduleBayValid", moduleBayWidget);
				mbpNew.addPropertyChangeListener(bookmarksWidget);
			}
		}
		m_model = obj;
		
		moduleBayWidget.updateValidLabel();
		bookmarksWidget.updateValidLabel();
		coordinatesWidget.setModel(obj);
		bindUI(getRealm());
	}
	
	@Override
	// this is just so MakeAddSubcommand can access it
	protected void addChildLabel(Label label){
		m_labels.add(label);
	}

	@Override
	// this is just so MakeAddSubcommand can access it
	protected void addChildControl(Control control){
		m_controls.add(control);
	}

	protected void addIncrementable(IncrementableText text) {
		m_incrementables.add(text);
	}

	public void onAppend(Sequenceable appended) {/**/}
	public void onInsert(Sequenceable inserted) {/**/}
	public void onDelete(Sequenceable deleted) {/**/}
	public void onMoveUp(Sequenceable moved) {/**/}
	public void onMoveDown(Sequenceable moved) {/**/}
}
