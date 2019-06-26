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
package gov.nasa.arc.verve.freeflyer.workbench.dialog;


import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateAdapter.StateTableRow;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.utils.HealthComparator;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/** Called from the Details button in the HealthAndStatusPart */
public class DetailedHealthAndStatusDialog extends Dialog implements AstrobeeStateListener {
	protected Tree healthAndStatusTree;
	protected TreeViewer healthAndStatusViewer;
	protected int[] widths = { 150, 150 };
	protected int[] alignment = { SWT.LEFT, SWT.LEFT };
	protected AstrobeeStateManager astrobeeStateManager;
	protected boolean commConnected;
	protected MApplication application;
	protected HealthTreeLabelProvider healthAndStatusTreeLabelProvider;
	protected Agent agent;

	@Inject
	public DetailedHealthAndStatusDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parent, MApplication mapp) {
		super(parent);
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS| SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
		application = mapp;
	}

	public void forceActive() {
		getShell().forceActive();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = setupTreeSectionComposite(parent);

		healthAndStatusTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		healthAndStatusTree.setLayoutData(gd);
		healthAndStatusTree.setLinesVisible(true);

		healthAndStatusViewer = new TreeViewer(healthAndStatusTree);

		createColumns(healthAndStatusTree);

		healthAndStatusTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn();
			}
		});

		healthAndStatusViewer.setContentProvider(new HealthTreeContentProvider());
		healthAndStatusTreeLabelProvider = new HealthTreeLabelProvider(commConnected);
		healthAndStatusViewer.setComparator(new HealthComparator());
		healthAndStatusViewer.setLabelProvider(healthAndStatusTreeLabelProvider);
		if(agent != null) {
			healthAndStatusTreeLabelProvider.onAgentSelected(agent);
		}

		if(astrobeeStateManager != null) {
			healthAndStatusViewer.setInput(getMyInput());
		}
		return parent;
	}
	
	public List<StateTableRow> getMyInput() {
		return astrobeeStateManager.getAdapter().getDetailedHealthAndStatusData();
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (healthAndStatusViewer != null && !healthAndStatusTree.isDisposed()) {
					
					// have to do TWICE to reorder dynamically
					healthAndStatusViewer.refresh();
					healthAndStatusViewer.refresh();
				}
			}
		});
	}

	protected void createColumns(Tree planTree) {
		TreeColumn col;
		for (int i = 0; i < widths.length; i++) {
			col = new TreeColumn(planTree, alignment[i]);
			col.setWidth(widths[i]);
		}
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		astrobeeStateManager.addListener(this);

		if(healthAndStatusViewer !=  null) {
			healthAndStatusViewer.setInput(astrobeeStateManager.getAdapter().getDetailedHealthAndStatusData());
		}
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		astrobeeStateManager.removeListener(this);
		application.getContext().set(DetailedHealthAndStatusDialog.class, null);
	}

	private Composite setupTreeSectionComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		c.setLayout(gl);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return c;
	}

	protected void fillColumn() {
		// calculate widths
		int columnsWidth = 0;
		for (int i = 0; i < healthAndStatusTree.getColumnCount() - 1; i++) {
			columnsWidth += healthAndStatusTree.getColumn(i).getWidth();
		}

		Point size = healthAndStatusTree.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = healthAndStatusTree.getVerticalBar();
		if (verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = healthAndStatusTree
				.getColumn(healthAndStatusTree.getColumnCount() - 1);
		if (columnsWidth + widths[widths.length - 1]
				+ healthAndStatusTree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth
					- healthAndStatusTree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if (lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Health and Status Details");
		newShell.setEnabled(true);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button - cancel doesn't mean anything for this dialog
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		// XXX doesn't this put us in a bad state if we save null as the agent?
		agent = a;
		if(a == null) {
			return;
		}
		if(healthAndStatusTreeLabelProvider != null) {
			healthAndStatusTreeLabelProvider.onAgentSelected(a);
			refreshViewer();
		}
	}

//	@Inject 
//	public void acceptCommConnected(@Named(FreeFlyerStrings.COMM_CONNECTED)boolean connected) {
//		commConnected = connected;
//		if(healthAndStatusTreeLabelProvider != null) {
//			healthAndStatusTreeLabelProvider.acceptCommConnected(connected);
//			refreshViewer();
//		}
//	}

	protected void refreshViewer()  {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (healthAndStatusTree.isDisposed()) {
					return;
				}
				healthAndStatusViewer.refresh();
			}
		});
	}
}
