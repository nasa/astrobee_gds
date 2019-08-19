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
import gov.nasa.arc.irg.freeflyer.rapid.frequent.CommStateHolder;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.FrequentTelemetryListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.freeflyer.rapid.state.CommStateGds;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rapid.ADMIN;
import rapid.ADMIN_METHOD_NOOP;
import rapid.ext.astrobee.ADMIN_METHOD_WIPE_HLP;

public class AdminControlPanel extends AbstractControlPanel implements FrequentTelemetryListener {
	private CommandButton noopButton, wipeHlpButton;//, stopButton;

	private String connectedString = "Connected";
	private String disconnectedString = "Disconnected";
	private String blankDash = "-";

	private String wirelessConnectedLabel = "Wireless Connected";
	private String wirelessConnectedData = blankDash;
	private String apNameLabel = "AP Name";
	private String apNameData = blankDash;
	private String bssidLabel = "BSSID";
	private String bssidData = blankDash;
	private String rssiLabel = "RSSI";
	private String rssiData = blankDash;
	private String frequencyLabel = "Frequency";
	private String frequencyData = blankDash;
	private String channelLabel = "Channel";
	private String channelData = blankDash;
	private String lanConnectedLabel = "LAN Connected";
	private String lanConnectedData = blankDash;

	private int numComms = 7;
	private int[] wirelessWidths = {250, 200};

	private Tree wirelessStatusTree;
	private TreeViewer wirelessStatusTreeViewer;

	private CommStateHolder commStateHolder;

	@Inject 
	public AdminControlPanel(MApplication application, Display display, AstrobeeStateManager astrobeeStateManager) {
		super(astrobeeStateManager);
	}
	
	@Inject 
	public void acceptAstrobeeStateManager(AstrobeeStateManager astrobeeStateManager) {
		this.astrobeeStateManager = astrobeeStateManager;
		if(standardControls != null) {
			standardControls.acceptAstrobeeStateManager(astrobeeStateManager);
		}
	}

	@PostConstruct
	public void createControls(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);

		Composite topComposite = new Composite(parent, SWT.None);
		standardControls = new StandardControls(topComposite, astrobeeStateManager);
		if(agent != null) {
			standardControls.onAgentSelected(agent);
		}
		makeAdminButtons(topComposite);
		makeCommStateDisplay(topComposite);
	}

	private void makeAdminButtons(Composite parent) {
		noopButton = new CommandButton(parent, SWT.NONE);
		noopButton.setText("No-Op");
		noopButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		noopButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		noopButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(ADMIN_METHOD_NOOP.VALUE, ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});

		wipeHlpButton = new CommandButton(parent, SWT.NONE);
		wipeHlpButton.setText("Wipe High Level Processor");
		wipeHlpButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		wipeHlpButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		wipeHlpButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(ADMIN_METHOD_WIPE_HLP.VALUE, ADMIN.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});

	}

	public void makeCommStateDisplay(Composite parent) {
		wirelessStatusTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		wirelessStatusTree.setLayoutData(gd);
		
		wirelessStatusTreeViewer = new TreeViewer(wirelessStatusTree);

		TreeColumn col1 = new TreeColumn(wirelessStatusTree, SWT.LEFT);
		wirelessStatusTree.setLinesVisible(true);
		col1.setWidth(wirelessWidths[0]);

		TreeColumn col2 = new TreeColumn(wirelessStatusTree, SWT.RIGHT);
		col2.setWidth(wirelessWidths[1]);

		wirelessStatusTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				GuiUtils.fillColumn(wirelessStatusTree, wirelessWidths);
			}
		});

		wirelessStatusTreeViewer.setContentProvider( new WirelessTreeContentProvider());
		wirelessStatusTreeViewer.setLabelProvider(new WirelessTableLabelProvider());

		Integer[] nums = new Integer[numComms];
		for(int i = 0; i < numComms; i++) {
			nums[i] = i;
		}

		wirelessStatusTreeViewer.setInput(nums);
	}

	class WirelessTableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){ return null; }

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
			case 0:
				switch((Integer) element) {
				case 0:	return wirelessConnectedLabel;
				case 1: return apNameLabel;
				case 2: return bssidLabel;
				case 3: return rssiLabel;
				case 4: return frequencyLabel;
				case 5: return channelLabel;
				case 6: return lanConnectedLabel;
				}
				//$FALL-THROUGH$
			case 1: 
				switch((Integer) element) {
				case 0: return wirelessConnectedData;
				case 1: return apNameData;
				case 2: return bssidData;
				case 3: return rssiData;
				case 4: return frequencyData;
				case 5: return channelData;
				case 6: return lanConnectedData;
				}
				//$FALL-THROUGH$
			default: return null;
			}
		}

		public void addListener(ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(Object element, String property){ return false; }

		public void removeListener(ILabelProviderListener listener) { /**/ }
	}	

	protected class WirelessTreeContentProvider implements ITreeContentProvider {
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

	@Override
	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		super.onAgentSelected(a);

		if(wipeHlpButton != null) {
			noopButton.setCompositeEnabled(true);
			wipeHlpButton.setCompositeEnabled(true);
		}
	}

	@Inject @Optional
	public void acceptCommStateHolder(CommStateHolder commStateHolder) {
		this.commStateHolder = commStateHolder;
		commStateHolder.addListener(this);
	}

	public void onSampleUpdate(Object sample) {
		updateCommData();
	}

	private void updateCommData() {
		CommStateGds commState = commStateHolder.getCommStateGds();

		if(commState == null || commState.isNull()) {
			return;
		}

		wirelessConnectedData = commState.isWirelessConnected() ? connectedString : disconnectedString;
		apNameData = commState.getApName();
		bssidData = commState.getBssid();
		rssiData = Float.toString(commState.getRssi());
		frequencyData = Float.toString(commState.getFrequency());
		channelData = Integer.toString(commState.getChannel());
		lanConnectedData = commState.isLanConnected() ? connectedString : disconnectedString;


		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(wirelessStatusTreeViewer != null && !wirelessStatusTreeViewer.getControl().isDisposed()) {
					wirelessStatusTreeViewer.refresh();	
				}
			}
		});
	}


	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(noopButton == null || noopButton.isDisposed()) {
					return;
				}

				if(!aggregateState.getAccessControl().equals(myId)) {
					noopButton.setCompositeEnabled(false);
					wipeHlpButton.setCompositeEnabled(false);
					return;
				}

				AstrobeeStateGds.OperatingState os = aggregateState.getAstrobeeState().getOperatingState();
				if(os == null) {
					return;
				}

				switch(os) {
				case PLAN_EXECUTION: 
				case TELEOPERATION: 
				case AUTO_RETURN:
					noopButton.setCompositeEnabled(true);
					wipeHlpButton.setCompositeEnabled(false);
					break;
				case FAULT: // false
					noopButton.setCompositeEnabled(true);
					wipeHlpButton.setCompositeEnabled(false);
					break;
				case READY: // false
					noopButton.setCompositeEnabled(true);
					wipeHlpButton.setCompositeEnabled(true);
				}

			}
		});
	}

	public void onConfigUpdate(Object config) {
		// TODO Auto-generated method stub
	}
}
