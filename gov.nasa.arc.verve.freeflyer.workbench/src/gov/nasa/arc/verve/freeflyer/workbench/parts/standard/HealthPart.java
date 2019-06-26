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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateAdapter.StateTableRow;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.utils.HealthComparator;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class HealthPart implements AstrobeeStateListener, SelectedAgentConnectedListener {
	private static final Logger logger = Logger.getLogger(HealthPart.class);
	protected Agent agent = null;
	protected AstrobeeStateManager astrobeeStateManager;
	@Inject
	protected MApplication application;

	protected String[] titles = {"Item", "Status" };
	protected int[] widths = { 200, 150 };
	protected int[] alignment = { SWT.LEFT, SWT.LEFT };
	protected Tree healthTree;
	protected TreeViewer healthViewer;
	protected Composite composites;
	protected Color orange = new Color(Display.getCurrent(), 255, 165, 0);
	protected Color cyan = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	protected boolean dataStale = true;

	@Inject
	public HealthPart(Composite parent, Display display, MApplication mapp) {
		application = mapp;
		build(parent);
		SelectedAgentConnectedRegistry.addListener(this);
	}

	public void build(Composite parent) {
		int cellsAcross = 2;
		GridLayout gridLayout = new GridLayout(cellsAcross, true);
		parent.setLayout(gridLayout);
		createTreeArea(parent);
	}

	protected void createTreeArea(Composite parent) {
		composites = setupTreeSectionComposite(parent);

		healthTree = new Tree(composites, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		healthTree.setLayoutData(gd);
		healthTree.setLinesVisible(true);
		healthTree.setHeaderVisible(true);

		healthViewer = new TreeViewer(healthTree);

		createColumns(healthTree);

		healthTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn();
			}
		});

		healthViewer.setContentProvider(new CreateTreeContentProvider());
		healthViewer.setLabelProvider(new TableLabelProvider());
		healthViewer.setComparator(new HealthComparator());
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		SelectedAgentConnectedRegistry.removeListener(this);
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		healthViewer.setInput(astrobeeStateManager.getAdapter().getStandardHealthAndStatusData());
		astrobeeStateManager.addListener(this);
	}

	protected class CreateTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { //
		} 

		@Override
		public Object[] getElements(Object inputElement) {
			return ((List<StateTableRow>) inputElement).toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(Object element) {
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}

	class TableLabelProvider implements ITableLabelProvider,
	ITableColorProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			StateTableRow stRow;
			if (element instanceof StateTableRow) {
				stRow = (StateTableRow)element;
				if(!stRow.showBecauseHighPriority()) {
					return null;
				}
			} else {
				return WorkbenchConstants.UNINITIALIZED_STRING;
			}
			switch (columnIndex) {
			case 0:
				return stRow.getLabel();
			case 1:
				if(stRow.getVerbatim()) {
					return stRow.getValue();
				}
				return GuiUtils.toTitleCase(stRow.getValue());
			}
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof StateTableRow) {
				StateTableRow stRow = (StateTableRow)element;
				if(!stRow.showBecauseHighPriority()) {
					return null;
				}

				switch (columnIndex) {
				case 1:
					if(dataStale) {
						return cyan;
					}
				}
				if (stRow.colorOrangeBecauseFault()) {
					return orange;
				}
			} 
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
			//
		}

		public void dispose() {
			//
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			//
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			return null;
		}
	}

	@Inject
	@Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a;
	}

	public void onAgentDeselected(){
		agent = null;
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (healthTree.isDisposed()) {
					return;
				}

				// have to do TWICE to reorder dynamically
				healthViewer.refresh();
				healthViewer.refresh();
			}
		});
	}

	protected void createColumns(Tree planTree) {
		TreeColumn col;
		for (int i = 0; i < widths.length; i++) {
			col = new TreeColumn(planTree, alignment[i]);
			col.setText(titles[i]);
			col.setWidth(widths[i]);
		}
	}

	private Composite setupTreeSectionComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		c.setLayout(gl);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 3;
		c.setLayoutData(gd);
		return c;
	}

	protected void fillColumn() {
		// calculate widths
		int columnsWidth = 0;
		for (int i = 0; i < healthTree.getColumnCount() - 1; i++) {
			columnsWidth += healthTree.getColumn(i).getWidth();
		}

		Point size = healthTree.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = healthTree.getVerticalBar();
		if (verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = healthTree
				.getColumn(healthTree.getColumnCount() - 1);
		if (columnsWidth + widths[widths.length - 1]
				+ healthTree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth
					- healthTree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if (lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}

	protected String getLabelString() {
		return "Health and Status";
	}

	@Override
	public void onSelectedAgentConnected() {
		if(!dataStale) {
			return;
		}
		dataStale = false;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (healthTree.isDisposed()) {
					return;
				}
				healthViewer.refresh();
			}
		});
	}

	@Override
	public void onSelectedAgentDisconnected() {
		if(dataStale) {
			return;
		}
		dataStale = true;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (healthTree.isDisposed()) {
					return;
				}
				healthViewer.refresh();
			}
		});
	}

	
}
