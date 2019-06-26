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
import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceAstrobeeStateManager;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateAdapter.StateTableRow;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.utils.HealthComparator;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class StandardMultiHealthAndStatusDialog extends Dialog implements AstrobeeStateListener, IActiveAgentSetListener {
	protected MApplication application;
	protected int numTrees;
	protected int[] widths = { 150, 150 };
	protected int[] alignment = { SWT.LEFT, SWT.LEFT };

	protected String[] titles = {"Item", "Status" };
	protected Vector<Agent> selectedGuestScience;
	protected Vector<GuestScienceAstrobeeStateManager> manager;
	protected Vector<Tree> healthAndStatusTree;
	protected Vector<TreeViewer> healthAndStatusViewer;
	protected Map<Agent, HealthTreeLabelProvider> labelProviders;

	@Inject
	public StandardMultiHealthAndStatusDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parent,
			MApplication mapp, 
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent selected1,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent selected2,
			@Optional @Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent selected3,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1) GuestScienceAstrobeeStateManager manager1,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2) GuestScienceAstrobeeStateManager manager2,
			@Optional @Named(FreeFlyerStrings.GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3) GuestScienceAstrobeeStateManager manager3
			) {
		super(parent);
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS| SWT.BORDER | SWT.TITLE);

		setUpTheSelectedAgents(selected1, selected2, selected3, manager1, manager2, manager3); 

		setBlockOnOpen(false);
		application = mapp;
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Health Details");
	}

	protected void setUpTheSelectedAgents(Agent selected1, Agent selected2, Agent selected3,
			GuestScienceAstrobeeStateManager manager1, GuestScienceAstrobeeStateManager manager2, GuestScienceAstrobeeStateManager manager3) {
		selectedGuestScience = new Vector<Agent>();
		manager = new Vector<GuestScienceAstrobeeStateManager>();

		if(selected1 != null) {
			selectedGuestScience.add(selected1);
			manager.add(manager1);
			manager1.addListener(this);
		}
		if(selected2 != null) {
			selectedGuestScience.add(selected2);
			manager.add(manager2);
			manager2.addListener(this);
		}
		if(selected3 != null) {
			selectedGuestScience.add(selected3);
			manager.add(manager3);
			manager3.addListener(this);
		}
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		for(GuestScienceAstrobeeStateManager man : manager) {
			if(man != null) {
				man.removeListener(this);
			}
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		healthAndStatusTree = new Vector<Tree>();
		healthAndStatusViewer = new Vector<TreeViewer>();
		labelProviders = new HashMap<Agent, HealthTreeLabelProvider>();

		Composite inner = GuiUtils.setupInnerCompositeEvenSpacing(parent, selectedGuestScience.size(), GridData.HORIZONTAL_ALIGN_CENTER);

		for(Agent selected : selectedGuestScience) {
			Composite innerInner = GuiUtils.setupInnerCompositeEvenSpacing(inner, 1, GridData.HORIZONTAL_ALIGN_CENTER);

			Label l = new Label(innerInner, SWT.None);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(l);
			l.setText(selected.name());
			createATree(innerInner, selected);
		}
		return parent;
	}

	protected Control createATree(Composite parent, Agent agent) {
		Composite c = setupTreeSectionComposite(parent);

		Tree healthAndStatusTreeSingle = new Tree(c, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		healthAndStatusTreeSingle.setLayoutData(gd);
		healthAndStatusTreeSingle.setLinesVisible(true);
		healthAndStatusTreeSingle.setHeaderVisible(true);

		TreeViewer healthAndStatusViewerSingle = new TreeViewer(healthAndStatusTreeSingle);

		createColumns(healthAndStatusTreeSingle);

		healthAndStatusTreeSingle.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(healthAndStatusTreeSingle);
			}
		});

		healthAndStatusViewerSingle.setContentProvider(new HealthTreeContentProvider());

		HealthTreeLabelProvider healthAndStatusTreeLabelProvider;
		if(ActiveAgentSet.contains(agent)) {
			healthAndStatusTreeLabelProvider = new HealthTreeLabelProvider(true);
		} else {
			healthAndStatusTreeLabelProvider = new HealthTreeLabelProvider(false);
		}

		healthAndStatusViewerSingle.setLabelProvider(healthAndStatusTreeLabelProvider);
		healthAndStatusViewerSingle.setComparator(new HealthComparator());

		int index = selectedGuestScience.indexOf(agent);
		GuestScienceAstrobeeStateManager mgr = manager.get(index);

		if(mgr != null) {
			healthAndStatusViewerSingle.setInput(getMyInput(mgr));
		}

		healthAndStatusViewer.add(healthAndStatusViewerSingle);
		healthAndStatusTree.add(healthAndStatusTreeSingle);
		labelProviders.put(agent, healthAndStatusTreeLabelProvider);

		return parent;
	}

	public List<StateTableRow> getMyInput(GuestScienceAstrobeeStateManager mgr) {
		return mgr.getAdapter().getStandardHealthAndStatusData();
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		refreshViewers();
	}

	protected void refreshViewers()  {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				for(int i=0; i< healthAndStatusTree.size(); i++) {
					if (healthAndStatusTree.get(i).isDisposed()) {
						return;
					}
					// have to do TWICE to reorder dynamically
					TreeViewer healthAndStatusViewerSingle = healthAndStatusViewer.get(i);

					healthAndStatusViewerSingle.refresh();
					healthAndStatusViewerSingle.refresh();
				}
			}
		});
	}


	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button - cancel doesn't mean anything for this dialog
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
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
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return c;
	}

	protected void fillColumn(Tree healthAndStatusTreeSingle) {
		// calculate widths
		int columnsWidth = 0;
		for (int i = 0; i < healthAndStatusTreeSingle.getColumnCount() - 1; i++) {
			columnsWidth += healthAndStatusTreeSingle.getColumn(i).getWidth();
		}

		Point size = healthAndStatusTreeSingle.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = healthAndStatusTreeSingle.getVerticalBar();
		if (verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = healthAndStatusTreeSingle
				.getColumn(healthAndStatusTreeSingle.getColumnCount() - 1);
		if (columnsWidth + widths[widths.length - 1]
				+ healthAndStatusTreeSingle.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth
					- healthAndStatusTreeSingle.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if (lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}


	@Override
	public void activeAgentSetChanged() {
		if(labelProviders != null) {
			refreshTreeViewersCommStatus();
		}
	}

	protected void refreshTreeViewersCommStatus() {
		for(Agent agent : selectedGuestScience) {
			HealthTreeLabelProvider lp = labelProviders.get(agent);

			if(ActiveAgentSet.contains(agent)) {
				lp.acceptCommConnected(true);
			} else {
				lp.acceptCommConnected(false);
			}
		}
		refreshViewers();
	}

	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}
}
