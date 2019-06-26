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
import gov.nasa.arc.irg.freeflyer.rapid.CompressedFilePublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.DiskStateHolder;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.FrequentTelemetryListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.freeflyer.rapid.state.DiskInfoGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.RosTopicsList;
import gov.nasa.arc.irg.freeflyer.rapid.state.RosTopicsList.ARosTopic;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.DATA;
import rapid.ext.astrobee.DATA_METHOD_CLEAR_DATA;
import rapid.ext.astrobee.DATA_METHOD_DOWNLOAD_DATA;
import rapid.ext.astrobee.DATA_METHOD_SET_DATA_TO_DISK;
import rapid.ext.astrobee.DATA_METHOD_STOP_DOWNLOAD;

public class DataToDiskPart implements AstrobeeStateListener, FrequentTelemetryListener, IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(DataToDiskPart.class);
	private CommandButton downloadButton, stopDownloadButton, clearDataButton;
	private CommandPublisher commandPublisher;
	private boolean agentValid = false;
	private String titleString = "Data to Disk";
	private final String DATA_TO_DISK_LOG_STRING = "Upload Data to Disk Configuration";
	protected Agent agent;
	protected MessageType[] sampleType;
	private Label agentNameLabel;
	private int entireWidth = 3;
	private int[] dataSizeTreeWidths = {150, 150, 150};
	private int[] dataToDiskSettingsTreeWidths = {150, 150, 40};
	private int[] topicsTreeWidths = {250};
	private Combo configureCombo;
	private CommandButton configureButton;
	private String[] configureFilesNames;
	private File[] configureFiles;
	private DiskStateHolder diskStateHolder;
	protected String participantId = Rapid.PrimaryParticipant;
	protected boolean waitingToSendSetCommand = false;
	
	@Inject
	IEclipseContext context;

	protected AstrobeeStateManager astrobeeStateManager;
	private String[] diskNameLabel, dataSizeLabel, diskSizeLabel;
	private int maxNumDisks;
	protected String myId;

	private Tree dataSizeTree;
	private TreeViewer dataSizeViewer;
	private DecimalFormat formatter = new DecimalFormat("#.######");

	protected Tree dataToDiskSettingsTree;
	protected TreeViewer dataToDiskSettingsTreeViewer;
	protected Tree topicsTree;
	protected TreeViewer topicsTreeViewer;
	protected Display savedDisplay;

	@Inject 
	public DataToDiskPart(Composite parent) {
		savedDisplay = Display.getDefault();
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		GridLayout gl = new GridLayout(3, false);
		parent.setLayout(gl);

		GridData gdThreeWide = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdThreeWide.horizontalSpan = 3;
		gdThreeWide.verticalSpan = 6;
		parent.setLayoutData(gdThreeWide);
		myId = Agent.getEgoAgent().name();

		GuiUtils.makeHorizontalSeparator(parent);
		createAgentNameLabel(parent);

		makeDataDownloadButtons(parent);
//		makeConfigureDataSection(parent);
		
		GuiUtils.makeHorizontalSeparator(parent);

		createTopicsTreeSection(parent);
		createDataToDiskSettingsTreeSection(parent);
		GuiUtils.makeHorizontalSeparator(parent);

		makeDiskDataDisplay(parent);
		GuiUtils.makeHorizontalSeparator(parent);

		sampleType = new MessageType[] {
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
		};
	}
	
	public void createTopicsTreeSection(Composite parent) {
		Composite c = setupNarrowTreeSectionComposite(parent, 1);

		topicsTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		topicsTree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		topicsTree.setLayoutData(gd);

		topicsTreeViewer = new TreeViewer(topicsTree);

		TreeColumn col1 = new TreeColumn(topicsTree, SWT.LEFT);
		topicsTree.setLinesVisible(true);
		col1.setText("Published Topics");
		col1.setWidth(topicsTreeWidths[0]);

		topicsTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(topicsTree);
			}
		});

		topicsTreeViewer.setContentProvider( new RosTopicTreeContentProvider());
		topicsTreeViewer.setLabelProvider(new RosTopicTableLabelProvider());

		topicsTreeViewer.setInput(RosTopicsList.getTopicsList().getTopics());
		topicsTreeViewer.expandToLevel(2);
	}

	public void createDataToDiskSettingsTreeSection(Composite parent) {
		Composite c = setupTreeSectionComposite(parent, 2);

		dataToDiskSettingsTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		dataToDiskSettingsTree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataToDiskSettingsTree.setLayoutData(gd);

		dataToDiskSettingsTreeViewer = new TreeViewer(dataToDiskSettingsTree);

		TreeColumn col1 = new TreeColumn(dataToDiskSettingsTree, SWT.LEFT);
		dataToDiskSettingsTree.setLinesVisible(true);
		col1.setText("Topic");
		col1.setWidth(dataToDiskSettingsTreeWidths[0]);

		TreeColumn col2 = new TreeColumn(dataToDiskSettingsTree, SWT.LEFT);
		col2.setText("Downlink");
		col2.setWidth(dataToDiskSettingsTreeWidths[1]);

		TreeColumn col3 = new TreeColumn(dataToDiskSettingsTree, SWT.LEFT);
		col3.setText("Freq (Hz)");
		col3.setWidth(dataToDiskSettingsTreeWidths[2]);

		dataToDiskSettingsTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn();
			}
		});

		dataToDiskSettingsTreeViewer.setContentProvider( new RosTopicTreeContentProvider());
		dataToDiskSettingsTreeViewer.setLabelProvider(new RosTopicTableLabelProvider());

		dataToDiskSettingsTreeViewer.setInput(RosTopicsList.getDataToDiskSettings().getTopics());
		dataToDiskSettingsTreeViewer.expandToLevel(2);
	}

	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		// none of these should be sent very frequently
		astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
		astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
		astrobeeStateManager.addListener(this, MessageTypeExtAstro.DATA_TO_DISK_STATE_TYPE);
		astrobeeStateManager.addListener(this, MessageTypeExtAstro.DATA_TOPICS_LIST_TYPE);
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agentValid = (a != null);
		agent = a; 
		agentNameLabel.setText(agent.name() + " " + titleString );
		commandPublisher = CommandPublisher.getInstance(agent);
		subscribe();
	}
	
	public void unsubscribe() {
		if(getAgent() != null) {
			RapidMessageCollector.instance().removeRapidMessageListener(participantId, getAgent(), this);
		}
	}

	public void subscribe() {
		if (getAgent() == null){
			return;
		}
		
		final Agent agent = getAgent();
		final String id = participantId;
		
		DdsTask.dispatchExec(new Runnable() {
			@Override
			public void run() {
				for(MessageType mt : getMessageTypes()) {
					if (mt == null) continue;
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							mt, 
							DataToDiskPart.this);
				}
			}
		});
	}
	
	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		for(int i=0; i<getSampleType().length; i++) {
			ret.add(getSampleType()[i]);
		}
		return ret;
	}
	
	protected MessageType[] getSampleType() {
		return sampleType;
	}

	public Agent getAgent() {
		return agent;
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgObj instanceof CompressedFileAck){
			// assume it is the right one - can put in checks later
			if(waitingToSendSetCommand) {
				sendSetDataToDiskConfigurationCmd();
				waitingToSendSetCommand = false;
			}
		}
	}
	
	private void sendSetDataToDiskConfigurationCmd() {
		commandPublisher.sendGenericNoParamsCommand(
				DATA_METHOD_SET_DATA_TO_DISK.VALUE, 
				DATA.VALUE);
	}

	public void makeDiskDataDisplay(Composite parent) {
		Composite c = setupTreeSectionComposite(parent, 3);

		dataSizeTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		dataSizeTree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataSizeTree.setLayoutData(gd);

		dataSizeViewer = new TreeViewer(dataSizeTree);

		TreeColumn col1 = new TreeColumn(dataSizeTree, SWT.LEFT);
		dataSizeTree.setLinesVisible(true);
		col1.setText("Disk");
		col1.setWidth(dataSizeTreeWidths[0]);

		TreeColumn col2 = new TreeColumn(dataSizeTree, SWT.LEFT);
		col2.setText("Data Size");
		col2.setWidth(dataSizeTreeWidths[1]);

		TreeColumn col3 = new TreeColumn(dataSizeTree, SWT.LEFT);
		col3.setText("Disk Size");
		col3.setWidth(dataSizeTreeWidths[2]);

		dataSizeTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(dataSizeTree);
			}
		});

		dataSizeViewer.setContentProvider( new DataTreeContentProvider());
		dataSizeViewer.setLabelProvider(new DataTableLabelProvider());
	}

	private void updateInput() {
		if(dataSizeTree == null || dataSizeTree.isDisposed()) {
			return;
		}
		Integer[] nums = new Integer[maxNumDisks];
		for(int i = 0; i < maxNumDisks; i++) {
			nums[i] = i;
		}

		dataSizeViewer.setInput(nums);
		dataSizeViewer.refresh();
		dataSizeTree.getParent().redraw();
	}

	private void updateDataToDiskTree() {
		if(dataToDiskSettingsTree == null || dataToDiskSettingsTree.isDisposed()) {
			return;
		}
		Collection<ARosTopic> c = RosTopicsList.getDataToDiskSettings().getTopics();
//		logger.error("Data To Disk Settings =");
//		for(ARosTopic art : c) {
//			logger.error(art.toString());
//		}
		dataToDiskSettingsTreeViewer.setInput(c);
		dataToDiskSettingsTreeViewer.refresh();
		dataToDiskSettingsTree.getParent().redraw();
	}	
	
	private void updateTopicsTree() {
		if(topicsTree == null || topicsTree.isDisposed()) {
			return;
		}
		Collection<ARosTopic> list = RosTopicsList.getTopicsList().getTopics();
		topicsTreeViewer.setInput(list);
		topicsTreeViewer.refresh();
		topicsTree.getParent().redraw();
	}	

	private Composite setupTreeSectionComposite(Composite parent, int width) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		c.setLayout(gl);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 200;
		data.horizontalSpan = width;
		c.setLayoutData(data);
		return c;
	}
	
	private Composite setupNarrowTreeSectionComposite(Composite parent, int width) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		c.setLayout(gl);
		GridData data = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		data.heightHint = 200;
		data.horizontalSpan = width;
		c.setLayoutData(data);
		return c;
	}

	protected class RosTopicTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) { /**/ }

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Collection<?>) {
				return ((Collection<ARosTopic>)inputElement).toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {	
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(Object element) { 
			return null;
		}

		@Override
		public boolean hasChildren(Object element) { 
			return false;
		}
	}

	class RosTopicTableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){ return null; }

		public String getColumnText(Object element, int columnIndex){
			switch(columnIndex) {
			case 0:
				if(element instanceof ARosTopic) {
					return ((ARosTopic)element).topicName;
				}
				return null;
			case 1:
				if(element instanceof ARosTopic) {
					return GuiUtils.toTitleCase(((ARosTopic)element).getDownlink().toString());
				}
				return null;
			case 2:
				if(element instanceof ARosTopic) {
					return Float.toString(((ARosTopic)element).getFrequency());

				}
				return null;
			default:
				return null;
			}
		}

		public void addListener(ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(Object element, String property){ return false; }

		public void removeListener(ILabelProviderListener listener) { /**/ }
	}	

	class DataTableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){ return null; }

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
			case 0: return diskNameLabel[(Integer) element];
			case 1: return dataSizeLabel[(Integer) element];
			case 2: return diskSizeLabel[(Integer) element];
			default: return null;
			}
		}

		public void addListener(ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(Object element, String property){ return false; }

		public void removeListener(ILabelProviderListener listener) { /**/ }
	}	

	protected class DataTreeContentProvider implements ITreeContentProvider {
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

	protected void updateDiskDataLabels() {
		diskNameLabel = new String[maxNumDisks];
		dataSizeLabel = new String[maxNumDisks];
		diskSizeLabel = new String[maxNumDisks];
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(downloadButton == null || downloadButton.isDisposed()) {
					return;
				}
				
				updateTopicsTree();
				updateDataToDiskTree();
				
				if(!aggregateState.getAccessControl().equals(myId)) {
					downloadButton.setCompositeEnabled(false);
					stopDownloadButton.setCompositeEnabled(false);
					clearDataButton.setCompositeEnabled(false);
					configureButton.setCompositeEnabled(false);
					configureCombo.setEnabled(false);
					return;
				} 
				AstrobeeStateGds astrobeeState = aggregateState.getAstrobeeState();
				AstrobeeStateGds.OperatingState opState = astrobeeState.getOperatingState();
				if(opState == null) {
					return;
				}
				switch(opState) {
				case READY:
					if(astrobeeState.getSubMobilityState() == 0){
						downloadButton.setCompositeEnabled(true);
						stopDownloadButton.setCompositeEnabled(true);
						clearDataButton.setCompositeEnabled(true);
						configureButton.setCompositeEnabled(true);
						configureCombo.setEnabled(true);
					}else{
						downloadButton.setCompositeEnabled(false);
						stopDownloadButton.setCompositeEnabled(false);
						clearDataButton.setCompositeEnabled(false);
					}
					break;
				case TELEOPERATION:
					configureCombo.setEnabled(true);
					configureButton.setCompositeEnabled(true);
					break;
				default:
					configureButton.setCompositeEnabled(false);
					downloadButton.setCompositeEnabled(false);
					stopDownloadButton.setCompositeEnabled(false);
					clearDataButton.setCompositeEnabled(false);
					configureCombo.setEnabled(false);
				}

			}
		});
	}

	private void updateDiskStatus() {
		Vector<DiskInfoGds> diskInfo = diskStateHolder.getDiskInfo();
		if(diskInfo == null) {
			return;
		}

		maxNumDisks = diskInfo.size();
		updateDiskDataLabels();

		int i = 0;		
		for(DiskInfoGds info: diskInfo) {
			diskNameLabel[i] = info.getName();
			dataSizeLabel[i] = humanReadable(info.getDataSize());
			diskSizeLabel[i] = humanReadable(info.getDiskSize());
			i++;
		}
		updateInput();
	}
	
	public static String humanReadable(double size) {
		String[] bytes = new String[] { "B", "KB", "MB", "GB"};
		if(size <= 0){
			return "0";
		}
		//Math.log calculates how many zeroes in the entered number, and the division tells whether the output should be in bytes,
		//kilobytes, megabytes, etc. 	
	    int digitGroups = (int)(Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + bytes[digitGroups];
	}

	protected void makeDownloadButton(Composite downloadComposite) {
		downloadButton = new CommandButton(downloadComposite, SWT.NONE);
		downloadButton.setText("Download Data");
		downloadButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		downloadButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		downloadButton.setCompositeEnabled(agentValid);
		downloadButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// XXX ADD the immediate or delayed parameter
				commandPublisher.sendGenericNoParamsCommand(
						DATA_METHOD_DOWNLOAD_DATA.VALUE,
						DATA.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	protected void makeStopDownloadButton(Composite downloadComposite) {
		stopDownloadButton = new CommandButton(downloadComposite, SWT.NONE);
		stopDownloadButton.setText("Stop Data Download");
		stopDownloadButton.setCompositeEnabled(agentValid);
		stopDownloadButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		stopDownloadButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		stopDownloadButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// XXX ADD the immediate or delayed parameter
				commandPublisher.sendGenericNoParamsCommand(
						DATA_METHOD_STOP_DOWNLOAD.VALUE,
						DATA.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	protected void makeClearDataButton(Composite downloadComposite) {
		clearDataButton = new CommandButton(downloadComposite, SWT.NONE);
		clearDataButton.setText("Clear Data");
		clearDataButton.setCompositeEnabled(agentValid);
		clearDataButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		clearDataButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		clearDataButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						DATA_METHOD_CLEAR_DATA.VALUE,
						DATA.VALUE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}
	
	protected void makeDataDownloadButtons(Composite parent) {
		Composite downloadComposite = new Composite(parent, SWT.LEFT);
		GuiUtils.giveGridLayout(downloadComposite, 5);
		
		GridData gdThreeWide = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdThreeWide.horizontalSpan = 3;
		gdThreeWide.verticalSpan = 6;
		downloadComposite.setLayoutData(gdThreeWide);

		makeDownloadButton(downloadComposite);
		makeStopDownloadButton(downloadComposite);
		makeClearDataButton(downloadComposite);
		
		initConfigurationFiles();
		makeConfigureCombo(downloadComposite);
		makeConfigureButton(downloadComposite);
		
	}

//	protected void makeConfigureDataSection(Composite parent) {
//		Composite configureComposite = new Composite(parent, SWT.LEFT);
//		GuiUtils.giveGridLayout(configureComposite, 2);	
//		initConfigurationFiles();
//		makeConfigureCombo(configureComposite);
//		makeConfigureButton(configureComposite);
//	}

	protected void initConfigurationFiles() {
		configureFiles = ConfigFileWrangler.getInstance().getDataToDiskFiles();
		configureFilesNames = new String[configureFiles.length];
		for(int i = 0; i < configureFiles.length; i ++) {
			configureFilesNames[i] = configureFiles[i].getName();
		}
	}

	protected void makeConfigureCombo(Composite parent) {
		configureCombo = new Combo(parent, SWT.READ_ONLY);
		configureCombo.setItems(configureFilesNames);
		configureCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureCombo.setEnabled(false);
	}

	protected void makeConfigureButton(Composite parent) {
		configureButton = new CommandButton(parent, SWT.NONE);
		configureButton.setText("Configure Data");
		configureButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(configureCombo.getSelectionIndex() == -1) {
					//print a message - nothing selected
					System.out.println("no configuration selected");
					return;
				}

				File selected = configureFiles[configureCombo.getSelectionIndex()];
				System.out.println(selected.getAbsolutePath());
				if(selected.exists()){
					try {
						CompressedFilePublisher.getInstance(agent).compressAndSendFile(
								DATA_TO_DISK_LOG_STRING, 
								MessageTypeExtAstro.DATA_TO_DISK_COMPRESSED_TYPE, 
								selected);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				waitingToSendSetCommand = true;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
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
		// calculate dataTreeWidths
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
		if(columnsWidth + dataSizeTreeWidths[dataSizeTreeWidths.length - 1] + tree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth - tree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if(lastColumn.getWidth() != dataSizeTreeWidths[dataSizeTreeWidths.length - 1]) {
				lastColumn.setWidth(dataSizeTreeWidths[dataSizeTreeWidths.length - 1]);
			}
		}
	}

	protected void fillColumn() {
		// calculate widths
		int columnsWidth = 0;
		for (int i = 0; i < dataToDiskSettingsTree.getColumnCount() - 1; i++) {
			columnsWidth += dataToDiskSettingsTree.getColumn(i).getWidth();
		}

		Point size = dataToDiskSettingsTree.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = dataToDiskSettingsTree.getVerticalBar();
		if (verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = dataToDiskSettingsTree.getColumn(dataToDiskSettingsTree.getColumnCount() - 1);
		if ((columnsWidth + dataToDiskSettingsTreeWidths[dataToDiskSettingsTreeWidths.length - 1]
				+ dataToDiskSettingsTree.getBorderWidth() * 2) < (size.x - scrollBarWidth)) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth
					- dataToDiskSettingsTree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if (lastColumn.getWidth() != dataToDiskSettingsTreeWidths[dataToDiskSettingsTreeWidths.length - 1]) {
				lastColumn.setWidth(dataToDiskSettingsTreeWidths[dataToDiskSettingsTreeWidths.length - 1]);
			}
		}
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		unsubscribe();
	}

	@Inject @Optional
	public void acceptComponentHolder(DiskStateHolder diskStateHolder) {
		this.diskStateHolder = diskStateHolder;
		diskStateHolder.addListener(this);
	}

	public void onSampleUpdate(Object sample) {
		if(savedDisplay.isDisposed()) {
			return;
		}
		savedDisplay.asyncExec(new Runnable() {
			public void run() {
				if(agentNameLabel != null && !agentNameLabel.isDisposed()) {
					updateDiskStatus();
				}
			}
		});
	}

	public void onConfigUpdate(Object config) {
		// TODO Auto-generated method stub
	}
}
