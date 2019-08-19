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

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateAdapter.StateTableRow;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.ui.io.OperatingLimitsConfigListLoader;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.AbstractControlPanel;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class OperatingLimitsPart extends AbstractControlPanel implements AstrobeeStateListener {
	private static final Logger logger = Logger.getLogger(OperatingLimitsPart.class);
	protected GridData leftData = new GridData(SWT.FILL, SWT.FILL, true, false);
	protected GridData rightData = new GridData(SWT.RIGHT, SWT.CENTER,false,false);
	protected Agent agent;
	private Label agentNameLabel;
	private int entireWidth = 4;
	private int[] widths = {150, 150, 100};
	private String titleString = "Operating Limits";
	protected String selectString = "Select Operating Limits Configuration ...";

	private Tree tree;
	private TreeViewer treeViewer;
	protected CommandPublisher commandPublisher;
	private CommandButton configureButton;
	private Combo optionsCombo;
	private OperatingLimitsConfigList operatingLimitsConfigList;
	protected String myId;
	private String operatingLimitsName = "";
	private AstrobeeStateManager astrobeeStateManager;

	@Inject 
	public OperatingLimitsPart(Composite parent, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, AstrobeeStateManager manager) {
		super(manager);
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);
		
		GuiUtils.makeHorizontalSeparator(parent, 1);

		Composite inner = new Composite(parent, SWT.NONE);
		GridData gdThreeWide = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdThreeWide.horizontalSpan = 3;
		GridLayout gl1 = new GridLayout(1, false);
		inner.setLayout(gl1);
		inner.setLayoutData(gdThreeWide);

		createAgentNameLabel(inner);

		try {
			operatingLimitsConfigList = OperatingLimitsConfigListLoader.getStandardConfig();

			optionsCombo = new Combo(inner, SWT.READ_ONLY);
			optionsCombo.setItems( makeConfigsList());
			optionsCombo.setText(selectString);
			optionsCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			myId = Agent.getEgoAgent().name();

			configureButton = new CommandButton(inner, SWT.NONE);
			configureButton.setText("Configure Operating Limits");
			configureButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			configureButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			configureButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (selectString.equals(optionsCombo.getText())) {
						return;
					}
					OperatingLimitsConfig option = operatingLimitsConfigList.getConfigNamed(optionsCombo.getText());
					if (option != null) {
						commandPublisher.sendSetOperatingLimitsCommand(option.getProfileName(),
								option.getFlightMode(), option.getTargetLinearVelocity(),
								option.getTargetLinearAccel(), option.getTargetAngularVelocity(), 
								option.getTargetAngularAccel(),
								option.getCollisionDistance());
					}

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// no-op
				}
			});

			makeOperatingLimitsTable(inner);
		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Operating Limits File", e.getMessage());
		}

	}
	
	

	private void showErrorDialog(Shell shell, final String title, final String errorMsg)  {
		// create a dialog with ok and cancel buttons and a question icon
		if(shell == null) {
			logger.error("No shell injected");
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText(title);
				dialog.setMessage(errorMsg);

				// open dialog and await user selection
				int returnCode = dialog.open(); 

			}
		});
	}

	@PreDestroy
	public void preDestroy() {
		SelectedAgentConnectedRegistry.removeListener(configureButton);
		if(astrobeeStateManager != null) {
			astrobeeStateManager.removeListener(this);
		}
	}

	private void makeOperatingLimitsTable(Composite parent) {
		Composite c = setupTreeSectionComposite(parent);

		tree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		tree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		tree.setLayoutData(gd);

		treeViewer = new TreeViewer(tree);

		TreeColumn col1 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		col1.setWidth(widths[0]);
		col1.setText("Name");

		TreeColumn col2 = new TreeColumn(tree, SWT.LEFT);
		col2.setWidth(widths[1]);
		col2.setText("Value");

		TreeColumn col3 = new TreeColumn(tree, SWT.LEFT);
		col3.setWidth(widths[2]);
		col3.setText("Units");

		tree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(tree);
			}
		});

		treeViewer.setContentProvider( new CreateTreeContentProvider());
		treeViewer.setLabelProvider(new TableLabelProvider());
	}

	class TableLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider, ITableColorProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex){ return null; }

		@Override
		public String getColumnText(Object element, int columnIndex){
			StateTableRow s;
			if (element instanceof StateTableRow) {
				s = ((StateTableRow) element);
			} else {
				return WorkbenchConstants.UNINITIALIZED_STRING;
			}

			switch (columnIndex) {
			case 0:
				return s.getLabel();
			case 1:
				if(s.getVerbatim()) {
					return s.getValue();
				}
				return GuiUtils.toTitleCase(s.getValue());
			case 2:
				return s.getUnits();
			}
			return null;
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
			return ((List<StateTableRow>) inputElement).toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {	return null; }

		@Override
		public Object getParent(Object element) { return null; }

		@Override
		public boolean hasChildren(Object element) { return false; }
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

	private void createAgentNameLabel(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = entireWidth;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);
		Font bigFont = GuiUtils.makeBigFont(parent, agentNameLabel);
		agentNameLabel.setFont(bigFont);
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

	protected String[] makeConfigsList() {
		String[] justNames = operatingLimitsConfigList.getArrayOfNames();
		int arrayLength = justNames.length;

		String[] configsStrings = new String[arrayLength + 1];
		configsStrings[0] = selectString;

		if (arrayLength > 0) {
			for (int i = 1; i < justNames.length + 1; i++) {
				configsStrings[i] = justNames[i - 1];
			}
		}
		return configsStrings;
	}

	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		if(treeViewer == null) {
			return;
		}
		treeViewer.setInput(asm.getAdapter().getOperatingLimitsData());

		asm.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		astrobeeStateManager = asm;
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		super.onAgentSelected(a);
		agent = a; 
		agentNameLabel.setText(a.name() + " " + titleString );
		commandPublisher = CommandPublisher.getInstance(agent);
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(agentNameLabel == null || agentNameLabel.isDisposed()) {
					return;
				}
				String otherName = stateKeeper.getAstrobeeState().getProfileName();
				if(!operatingLimitsName.equals(otherName)) {
					if(otherName != null) {
						treeViewer.refresh();
						operatingLimitsName = stateKeeper.getAstrobeeState().getProfileName();
					}
				}

				if(!stateKeeper.getAccessControl().equals(myId)) {
					configureButton.setCompositeEnabled(false);
					optionsCombo.setEnabled(false);
					return;
				} 
				AstrobeeStateGds.OperatingState opState = stateKeeper.getAstrobeeState().getOperatingState();
				if(opState == null) {
					return;
				}
				switch(opState) {
				case READY:
					configureButton.setCompositeEnabled(true);
					optionsCombo.setEnabled(true);
					break;
				default:
					break;
				}
			}
		});
	}
}
