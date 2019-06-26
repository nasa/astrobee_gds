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
import gov.nasa.arc.irg.freeflyer.rapid.frequent.ComponentStateHolder;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.FrequentTelemetryListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.SingleComponent;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
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

public class ComponentsPart implements FrequentTelemetryListener {
	protected Agent agent;
	private Label agentNameLabel;
	private String titleString = "Component States";
	private String uninitializedString = WorkbenchConstants.UNINITIALIZED_STRING;
	private int entireWidth = 5;
	// when we turn this into a table, we won't need this guess
	private int numComponents = 8;
	private String[] componentNameLabel, componentPresentLabel, componentPoweredLabel, componentTempLabel, componentCurrentLabel;
	private final String YES_STRING = "Yes", NO_STRING = "No";
	private int[] widths = {150, 75, 75, 75, 75};
	private ComponentStateHolder componentStateHolder;
	
	private Tree tree;
	private TreeViewer treeViewer;
	private DecimalFormat formatter = new DecimalFormat("#.###");
	private Color orange = new Color(Display.getCurrent(), 255, 165, 0);
	private final int TEMP_LIMIT = 40;
	private final int CURR_LIMIT = 3;
	
	@Inject 
	public ComponentsPart(Composite parent) {
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		GridLayout gl = new GridLayout(3, false);
		parent.setLayout(gl);

		GridData gdThreeWide = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdThreeWide.horizontalSpan = 3;
		parent.setLayoutData(gdThreeWide);

		GuiUtils.makeHorizontalSeparator(parent);
		createAgentNameLabel(parent);
		
		makeComponentsDisplay(parent);
	}
	
	private void makeComponentsDisplay(Composite parent) {
		Composite c = setupTreeSectionComposite(parent);
				
		tree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		tree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		tree.setLayoutData(gd);

		treeViewer = new TreeViewer(tree);

		TreeColumn col1 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		col1.setText("Component");
		col1.setWidth(150);

		TreeColumn col2 = new TreeColumn(tree, SWT.LEFT);
		col2.setText("Present");
		col2.setWidth(75);

		TreeColumn col3 = new TreeColumn(tree, SWT.LEFT);
		col3.setText("Powered");
		col3.setWidth(75);
		
		TreeColumn col4 = new TreeColumn(tree, SWT.LEFT);
		col4.setText("Temp (C)");
		col4.setWidth(75);
		
		TreeColumn col5 = new TreeColumn(tree, SWT.LEFT);
		col5.setText("Current (A)");
		col5.setWidth(75);
		
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
		if(tree == null || tree.isDisposed()) {
			return;
		}
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
		gl.verticalSpacing = 0;
		gl.marginWidth = 5;
		c.setLayout(gl);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.verticalSpan = 3;
		c.setLayoutData(data);
		return c;
	}
	
	private boolean isPresent(int id) {
		return componentPresentLabel[id].equals("Yes");
	}
	
	class TableLabelProvider implements ITableLabelProvider, ITableColorProvider{

		public Image getColumnImage(Object element, int columnIndex){ return null; }

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
				case 0: return componentNameLabel[(Integer) element];
				case 1: return componentPresentLabel[(Integer) element];
				case 2: return isPresent((Integer) element) ? 
						componentPoweredLabel[(Integer) element] : uninitializedString;
				case 3: return isPresent((Integer) element) ? 
						componentTempLabel[(Integer) element] : uninitializedString;
				case 4: return isPresent((Integer) element) ?
						componentCurrentLabel[(Integer) element] : uninitializedString;
				default: return null;
			}
		}

		public void addListener(ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(Object element, String property){ return false; }

		public void removeListener(ILabelProviderListener listener) { /**/ }

		@Override
		public Color getForeground(Object element, int columnIndex) {
			if(columnIndex == 3) {
				if(Integer.parseInt(componentTempLabel[(Integer) element]) > TEMP_LIMIT) {
					return orange;
				}
			} else if(columnIndex == 4) {
				if(Float.parseFloat(componentCurrentLabel[(Integer) element]) > CURR_LIMIT) {
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
	
	protected void updateComponentsLabels() {		
		componentNameLabel = new String[numComponents];
		componentPresentLabel = new String[numComponents];
		componentPoweredLabel = new String[numComponents];
		componentTempLabel = new String[numComponents];
		componentCurrentLabel = new String[numComponents];
	}
	
	@Inject @Optional
	public void acceptComponentHolder(ComponentStateHolder componentStateHolder) {
		this.componentStateHolder = componentStateHolder;
		componentStateHolder.addListener(this);
	}
	
	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; 
		agentNameLabel.setText(agent.name() + " " + titleString );
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
	
	public void onSampleUpdate(Object sample) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(agentNameLabel == null || agentNameLabel.isDisposed()) {
					return;
				}
				updateDiskStatus();
			}
		});
	}

	public void onConfigUpdate(Object config) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateDiskStatus() {
		Vector<SingleComponent> components = componentStateHolder.getComponents();
		if(components == null) {
			return;
		}
		
		numComponents = components.size();
		updateComponentsLabels();
		
		int i = 0;
		for(SingleComponent single : components) {			
			componentNameLabel[i] = single.getName();
			componentPresentLabel[i] = single.isPresent() ? YES_STRING : NO_STRING;
			componentPoweredLabel[i] = single.isPowered() ? YES_STRING : NO_STRING;
			componentTempLabel[i] = Math.round(single.getTemperature()) + "";
			componentCurrentLabel[i] = formatter.format(single.getCurrent());
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
	
	@PreDestroy
	public void preDestroy() {
		componentStateHolder.removeListener(this);
	}
}
