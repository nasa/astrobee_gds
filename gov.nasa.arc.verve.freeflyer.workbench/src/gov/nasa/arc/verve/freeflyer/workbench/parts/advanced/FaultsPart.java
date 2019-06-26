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

import gov.nasa.arc.irg.freeflyer.rapid.faults.GdsFault;
import gov.nasa.arc.irg.freeflyer.rapid.faults.GdsFaultState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.Collections;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class FaultsPart implements AstrobeeStateListener {
	private final Color colorWhite = ColorProvider.INSTANCE.white;
	private Tree tree;
	private TreeViewer treeViewer;
	private Label message;
	private int[] widths = {90, 200, 90, 90};
	private String[] titles = {"Fault ID", "Description", "Subsystem", "Node"};
	protected AstrobeeStateManager astrobeeStateManager;
	private String NO_MESSAGE_STRING = "Selected fault has no message";

	@Inject 
	public FaultsPart(Composite parent) {
		createDialogArea(parent);
	}

	protected Control createDialogArea(Composite parent) {

		Composite c = setupTreeSectionComposite(parent);

		tree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		//		gd.heightHint = 400;
		//		gd.widthHint = 410;
		tree.setLayoutData(gd);
		tree.setLinesVisible(true);

		treeViewer = new TreeViewer(tree);

		TreeColumn col0 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		col0.setWidth(widths[0]);
		col0.setText(titles[0]);

		TreeColumn col1 = new TreeColumn(tree, SWT.LEFT);
		col1.setWidth(widths[1]);
		col1.setText(titles[1]);

		TreeColumn col2 = new TreeColumn(tree, SWT.LEFT);
		col2.setWidth(widths[2]);
		col2.setText(titles[2]);

		TreeColumn col3 = new TreeColumn(tree, SWT.LEFT);
		col3.setWidth(widths[3]);
		col3.setText(titles[3]);

		//		publisher = CommandPublisher.getInstance((Agent)context.get(FreeFlyerStrings.PRIMARY_BEE));

		tree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(tree);
			}
		});

		treeViewer.setContentProvider( new CreateTreeContentProvider());
		treeViewer.setLabelProvider(new TableLabelProvider());

		new Label(c, SWT.NONE).setText("Message");
		message = new Label(c, SWT.NONE);
		message.setBackground(colorWhite);
		message.setText("Select a fault to view message.");

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				if (ts.getFirstElement() instanceof GdsFault) {
					GdsFault fault = (GdsFault) ts.getFirstElement();
					if(fault.getMessage() == null) {
						message.setText(NO_MESSAGE_STRING);
					} else {
						message.setText(fault.getMessage());
					}
					message.pack();
				}
			}
		});

		//	tree.getItem(0).setExpanded(true);
		treeViewer.refresh(true);

		return parent;
	}

	private Composite setupTreeSectionComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		c.setLayout(gl);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return c;
	}

	class TableLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {

		}

		@Override
		public void dispose() {

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {

		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public synchronized String getColumnText(Object element, int columnIndex) {
			//System.out.println(element.getClass());
			switch(columnIndex) {
			case 0:
				if(element instanceof GdsFault) {
					return Long.toString(((GdsFault) element).getFaultId());
				} else if(element instanceof String) {
					return (String)element;
				} else {
					return "";
				}
			case 1:
				if(element instanceof GdsFault) {

					return ((GdsFault) element).getDescription();
				} else {
					return "";
				}
			case 2:
				if(element instanceof GdsFault) {
					return ((GdsFault) element).getSubsystem();
				} else {
					return "";
				}
			case 3:
				if(element instanceof GdsFault) {
					return ((GdsFault) element).getNode();
				} else {
					return "";
				}
			default:
				return "";
			}
		}
	}

	protected class CreateTreeContentProvider implements ITreeContentProvider {
		GdsFaultState savedInput;

		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) { 
			if(newInput instanceof GdsFaultState) {
				savedInput = (GdsFaultState) newInput;
			}
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof GdsFaultState){
				return ((GdsFaultState)inputElement).getFaultCategories();
			} else 
				return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object[] getChildren(final Object parentElement) {
			if (parentElement instanceof String){
				if(parentElement.equals("Triggered")) {
					return savedInput.getTriggeredFaults().toArray();
				} else if(parentElement.equals("Not Triggered")) {
					return savedInput.getEnabledFaults().toArray();
				}
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(final Object element) {
			//			if (element instanceof Sequenceable){
			//				return ((Sequenceable)element).getParent();
			//			}
			return null;
		}

		@Override
		public boolean hasChildren(final Object element) {
			if (element instanceof String){
				return true;
			}
			return false;
		}
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

	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(tree != null && treeViewer != null) {
					if(stateKeeper.getGdsFaultState() != null) {
						treeViewer.setInput(stateKeeper.getGdsFaultState());
						treeViewer.expandAll();
					}
				}
			}
		});
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		astrobeeStateManager.addListener(this, MessageTypeExtAstro.FAULT_STATE_TYPE);
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
	}
}
