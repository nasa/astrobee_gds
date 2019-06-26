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


import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.EpsStateHolder;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.FrequentTelemetryListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.EpsStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.EpsStateGds.BatteryInfoGds;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class EpsPart implements FrequentTelemetryListener {
	private static final Logger logger = Logger.getLogger(EpsPart.class);
	protected Agent agent;
	private Label agentNameLabel;
	private Label battTotalData;
	private String titleString = "Power State";
	private int entireWidth = 4;
	private int numComponents;
	private String[] slotNames = {"Top Left", "Top Right", "Bottom Left", "Bottom Right", "Unknown"};
	private String[] nameLabel, percentageLabel, temperatureLabel, voltageLabel, currentLabel, 
	remainingCapacityLabel, designedCapacityLabel, currentMaxCapacityLabel;
	private String blankDash = "-";
	private int[] widths = {150, 150, 150};
	private EpsStateHolder epsStateHolder;
	
	private final int BATT_LIMIT = 20;
	private final int TEMP_LIMIT = 40;
	private Color orange = new Color(Display.getCurrent(), 255, 165, 0);
	private Tree tree;
	private TreeViewer treeViewer;
	DecimalFormat formatter = new DecimalFormat("#.###");
	
	@Inject 
	public EpsPart(Composite parent) {
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);

		GridData gdThreeWide = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdThreeWide.horizontalSpan = 3;
		parent.setLayoutData(gdThreeWide);

		GuiUtils.makeHorizontalSeparator(parent);
		createAgentNameLabel(parent);
		makePowerHeader(parent);
//		makePowerLabels();
		makePowerTable(parent);
	}
	
	private void makePowerTable(Composite parent) {
		Composite c = setupTreeSectionComposite(parent);
		
		tree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		tree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		tree.setLayoutData(gd);

		treeViewer = new TreeViewer(tree);

		TreeColumn col1 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		col1.setText("Battery");
		col1.setWidth(150);

		TreeColumn col3 = new TreeColumn(tree, SWT.LEFT);
		col3.setText("Percentage");
		col3.setWidth(100);
		
		TreeColumn col4 = new TreeColumn(tree, SWT.LEFT);
		col4.setText("Voltage");
		col4.setWidth(100);
		
		TreeColumn col5 = new TreeColumn(tree, SWT.LEFT);
		col5.setText("Current");
		col5.setWidth(100);
		
		TreeColumn col6 = new TreeColumn(tree, SWT.LEFT);
		col6.setText("Remaining Cap.");
		col6.setWidth(100);
		
		TreeColumn col7 = new TreeColumn(tree, SWT.LEFT);
		col7.setText("Designed Cap.");
		col7.setWidth(100);
		
		TreeColumn col8 = new TreeColumn(tree, SWT.LEFT);
		col8.setText("Current Max Cap.");
		col8.setWidth(100);
		
		tree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(tree);
			}
		});

		treeViewer.setContentProvider( new CreateTreeContentProvider());
		treeViewer.setLabelProvider(new TableLabelProvider());
	}
	
	private void updateInput() {
		Integer[] nums = new Integer[numComponents];
		for(int i = 0; i < numComponents; i++) {
			nums[i] = i;
		}
		treeViewer.setInput(nums);
		treeViewer.refresh();
	}

	private Composite setupTreeSectionComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		gl.verticalSpacing = 0;
		c.setLayout(gl);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return c;
	}
	
	class TableLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider, ITableColorProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex){ return null; }

		@Override
		public String getColumnText(Object element, int columnIndex){
			TreeItem item = tree.getItem((Integer) element);
			
			switch (columnIndex){
				case 0: return nameLabel[(Integer) element];
				case 1: return percentageLabel[(Integer) element];
				case 2: return voltageLabel[(Integer) element];
				case 3: return currentLabel[(Integer) element];
				case 4: return remainingCapacityLabel[(Integer) element];
				case 5: return designedCapacityLabel[(Integer) element];
				case 6: return currentMaxCapacityLabel[(Integer) element];
				default: return null;
			}
		}

		@Override
		public void addListener(ILabelProviderListener listener) { /**/ }

		@Override
		public void dispose() { /**/ }

		@Override
		public boolean isLabelProperty(Object element, String property){ return false; }

		@Override
		public void removeListener(ILabelProviderListener listener) { /**/ }
		
		@Override
		public Color getForeground(Object element, int columnIndex) {
			if(columnIndex == 2) {
				if(Integer.parseInt(percentageLabel[(Integer) element]) < BATT_LIMIT) {
					return orange;
				}
			} else if (columnIndex == 3) {
				if(Integer.parseInt(temperatureLabel[(Integer) element]) > TEMP_LIMIT) {
					return orange;
				}
			}
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) { return null; }
	}	

	protected class CreateTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) { /**/ }

		@Override
		public Object[] getElements(Object inputElement) {
			return (Integer[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {	return null; }

		@Override
		public Object getParent(Object element) { return null; }

		@Override
		public boolean hasChildren(Object element) { return false; }
	}
	
	protected void makePowerHeader(Composite parent) {
		Composite commStateComposite = new Composite(parent, SWT.LEFT);
		GuiUtils.giveGridLayout(commStateComposite, 2);

		Label battTotalLabel = new Label(commStateComposite, SWT.LEFT);
		battTotalLabel.setText("Estimated Minutes Remaining: ");
		battTotalData = new Label(commStateComposite, SWT.LEFT);
		battTotalData.setText(blankDash);

	}
	
	protected void updatePowerLabels() {		
		nameLabel = new String[numComponents];
		percentageLabel = new String[numComponents];
		temperatureLabel = new String[numComponents];
		voltageLabel = new String[numComponents];
		currentLabel = new String[numComponents];
		remainingCapacityLabel = new String[numComponents];
		designedCapacityLabel = new String[numComponents];
		currentMaxCapacityLabel = new String[numComponents];
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
	
	@Inject @Optional
	public void acceptEpsStateHolder(EpsStateHolder epsStateHolder) {
		this.epsStateHolder = epsStateHolder;
		epsStateHolder.addListener(this);
	}
	
	@PreDestroy
	public void preDestroy() {
		epsStateHolder.removeListener(this);
	}
	
	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; 
		agentNameLabel.setText(agent.name() + " " + titleString );
	}

	private void updateDiskStatus() {
		EpsStateGds eps = epsStateHolder.getEpsState();
		if(eps == null) {
			return;
		}
		if(battTotalData == null || battTotalData.isDisposed()) {
			return;
		}
		battTotalData.setText(eps.getBatteryMinutesString());
		
		Vector<BatteryInfoGds> batts = eps.getBatteryInfo();
		numComponents = batts.size();
		updatePowerLabels();
		
		int i = 0;
		for(BatteryInfoGds batt : batts) {			
			//nameLabel[i] = batt.getSlot().toString();
			nameLabel[i] = slotNames[i];
			percentageLabel[i] = Math.round(batt.getPercentage()) + "";
			temperatureLabel[i] = Math.round(batt.getTemperature()) + "";
			voltageLabel[i] = Math.round(batt.getVoltage()) + "";
			currentLabel[i] = Math.round(batt.getCurrent()) + "";
			remainingCapacityLabel[i] = Math.round(batt.getRemainingCapacity()) + "";
			designedCapacityLabel[i] = Math.round(batt.getDesignedCapacity()) + "";
			currentMaxCapacityLabel[i] = Math.round(batt.getCurrentMaxCapacity()) + "";
			i++;
		}
		updateInput();
	}
	
	protected void fillColumn(Tree tree) {
		// calculate widths
		int columnsWidth = 0;
		for (int i = 0; i < tree.getColumnCount() - 1; i++) {
			columnsWidth += tree.getColumn(i).getWidth();
		}

		Point size = tree.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = tree.getVerticalBar();
		if(verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = tree.getColumn(tree.getColumnCount() - 1);
		if(columnsWidth + widths[widths.length - 1] + tree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth - tree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if(lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}

	public void onSampleUpdate(Object sample) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(battTotalData == null || battTotalData.isDisposed()) {
					return;
				}
				updateDiskStatus();
			}
		});
	}

	public void onConfigUpdate(Object config) {
		// TODO Auto-generated method stub
		
	}
}
