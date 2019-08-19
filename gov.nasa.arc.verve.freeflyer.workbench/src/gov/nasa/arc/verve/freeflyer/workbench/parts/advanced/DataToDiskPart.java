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
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.freeflyer.rapid.state.RosTopicsList;
import gov.nasa.arc.irg.freeflyer.rapid.state.RosTopicsList.ARosTopic;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.irg.util.ui.ColorProvider;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.DATA;
import rapid.ext.astrobee.DATA_METHOD_SET_DATA_TO_DISK;
import rapid.ext.astrobee.DATA_METHOD_START_RECORDING;
import rapid.ext.astrobee.DATA_METHOD_START_RECORDING_PARAM_DESCRIPTION;
import rapid.ext.astrobee.DATA_METHOD_STOP_RECORDING;

public class DataToDiskPart implements AstrobeeStateListener, IRapidMessageListener {
//	private static final Logger logger = Logger.getLogger(DataToDiskPart.class);
	private CommandPublisher commandPublisher;
	private String titleString = "Data to Disk";
	private final String DATA_TO_DISK_LOG_STRING = "Upload Data to Disk Configuration";
	protected Agent agent;
	protected MessageType[] sampleType;
	private Label agentNameLabel;
	private int entireWidth = 3;
	private int[] dataToDiskSettingsTreeWidths = {300, 150, 40};
	private int[] topicsTreeWidths = {300};
	private final int NUM_FAVORITES = 3;
	private Combo[] configureCombo = new Combo[NUM_FAVORITES];
	private CommandButton[] configureButton = new CommandButton[NUM_FAVORITES];
	private CommandButton startRecordButton, stopRecordButton;
	private final String START_RECORD_STRING = "Start Recording Data";
	private final String STOP_RECORD_STRING = "Stop Recording Data";
	private String[] configureFilesNames;
	private File[] configureFiles;
	protected String participantId = Rapid.PrimaryParticipant;
	protected boolean waitingToSendSetCommand = false;
	@Inject
	IEclipseContext context;
	protected Text startRecordDescriptionText;

	protected AstrobeeStateManager astrobeeStateManager;
	protected String myId;

	private DecimalFormat formatter = new DecimalFormat("#.######");
	int graylevel = 220;
	protected final Color gray1 = ColorProvider.get(graylevel,graylevel,graylevel);

	protected Table allTopicsTable;
	protected Table topicSettingsTable;
	protected String[] topicSettingsHeaders = {
			"Topic                                    ",
			"Downlink       ", 
			"Freq (Hz)"};
	protected Display savedDisplay;
	protected Label recordingNameLabel;
	protected String notRecordingString = "Not recording ";
	protected String recordingString = "Recording ";
	
	@Inject
	public DataToDiskPart(Composite parent) {
		savedDisplay = Display.getDefault();
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		GridLayout gl = new GridLayout(3, false);
		parent.setLayout(gl);

		GridData gdThreeWide = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdThreeWide.horizontalSpan = 3;
		parent.setLayoutData(gdThreeWide);
		myId = Agent.getEgoAgent().name();

		GuiUtils.makeHorizontalSeparator(parent);
		createAgentNameLabel(parent);
		recordingNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		recordingNameLabel.setLayoutData(data);
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<16;i++) {
			sb.append("\t");
		}
		recordingNameLabel.setText(sb.toString());

		makeConfigureDataSection(parent);

		createAllTopicsTableSection(parent);
		createTopicSettingsTableSection(parent);

		sampleType = new MessageType[] {
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
		};
	}

	public void createAllTopicsTableSection(Composite parent) {
		Composite c = setupNarrowTreeSectionComposite(parent, 1);

		allTopicsTable = new Table(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		allTopicsTable.setLinesVisible(true);
		allTopicsTable.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		allTopicsTable.setLayoutData(gd);

		TableColumn col1 = new TableColumn(allTopicsTable, SWT.LEFT);
		col1.setText("Published Topics              ");
		col1.setWidth(topicsTreeWidths[0]);
		col1.addListener(SWT.Selection, new SortListener(allTopicsTable,1));
		allTopicsTable.getColumn(0).pack();
	}

	public void createTopicSettingsTableSection(Composite parent) {
		Composite c = setupTableSectionComposite(parent, 2, true);

		topicSettingsTable = new Table(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		topicSettingsTable.setHeaderVisible(true);
		topicSettingsTable.setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		topicSettingsTable.setLayoutData(gd);

		for(int i=0; i< topicSettingsHeaders.length; i++) {
			final TableColumn col = new TableColumn(topicSettingsTable, SWT.LEFT);
			col.setText(topicSettingsHeaders[i]);
			col.setWidth(dataToDiskSettingsTreeWidths[i]);
			col.addListener(SWT.Selection, new SortListener(topicSettingsTable,topicSettingsHeaders.length));
			topicSettingsTable.getColumn(i).pack();
		}
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

	private Composite setupTableSectionComposite(Composite parent, int width, boolean grabVertical) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		c.setLayout(gl);
		GridData data;
		if(grabVertical) {
			data = new GridData(SWT.FILL, SWT.FILL, true, true);
		} else {
			data = new GridData(SWT.FILL, SWT.TOP, true, false);
		}
		data.horizontalSpan = width;
		c.setLayoutData(data);
		return c;
	}

	private Composite setupConfigComposite(Composite parent, int width) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2,false);
		c.setLayout(gl);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
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
		GridData data = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
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

		@SuppressWarnings("unchecked")
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

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(configureButton[0] == null || configureButton[0].isDisposed()) {
					return;
				}

				updateTables();

				if(!aggregateState.getAccessControl().equals(myId)) {
					disableConfigs();
					return;
				}
				
				if(aggregateState.isRecordingData()) {
					startRecordButton.setCompositeEnabled(false);
					stopRecordButton.setCompositeEnabled(true);
					recordingNameLabel.setText(recordingString + aggregateState.getRecordingName());
				} else {
					startRecordButton.setCompositeEnabled(true);
					stopRecordButton.setCompositeEnabled(false);
					recordingNameLabel.setText(notRecordingString + aggregateState.getRecordingName());
				}
				
				AstrobeeStateGds astrobeeState = aggregateState.getAstrobeeState();
				AstrobeeStateGds.OperatingState opState = astrobeeState.getOperatingState();
				if(opState == null) {
					return;
				}
				switch(opState) {
				case READY:
					if(astrobeeState.getSubMobilityState() == 0){
						enableConfigs();
					}
					break;
				case TELEOPERATION:
					enableConfigs();
					break;
				default:
					disableConfigs();
				}

			}
		});
	}

	private void enableConfigs(){
		for(int i=0;i<NUM_FAVORITES;i++) {
			configureButton[i].setCompositeEnabled(true);
		}
	}

	private void disableConfigs() {
		for(int i=0;i<NUM_FAVORITES;i++) {
			configureButton[i].setCompositeEnabled(false);
		}
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

	protected void makeConfigureDataSection(Composite parent) {
		Composite configureComposite = setupTableSectionComposite(parent, 3, false);
		GuiUtils.giveGridLayout(configureComposite, 3);	
		initConfigurationFiles();

		for(int i=0;i<NUM_FAVORITES;i++) {
			Composite comp = setupConfigComposite(configureComposite, 1);
			comp.setBackground(gray1);
			makeConfigureCombo(comp, i);
			makeConfigureButton(comp, i);
		}

		createStartRecordingButton(configureComposite);
		createStopRecordingButton(configureComposite);
	}

	protected void initConfigurationFiles() {
		configureFiles = ConfigFileWrangler.getInstance().getDataToDiskFiles();
		configureFilesNames = new String[configureFiles.length];
		for(int i = 0; i < configureFiles.length; i ++) {
			configureFilesNames[i] = configureFiles[i].getName();
		}
	}

	protected void makeConfigureCombo(Composite parent, int num) {
		configureCombo[num] = new Combo(parent, SWT.READ_ONLY);
		configureCombo[num].setItems(configureFilesNames);
		configureCombo[num].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	protected void makeConfigureButton(Composite parent, int num) {
		configureButton[num] = new CommandButton(parent, SWT.NONE);
		configureButton[num].setText("Configure Data");
		configureButton[num].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton[num].setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		configureButton[num].addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(configureCombo[num].getSelectionIndex() == -1) {
					//print a message - nothing selected
					System.out.println("no configuration selected");
					return;
				}

				File selected = configureFiles[configureCombo[num].getSelectionIndex()];
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

	private void createStartRecordingButton(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2,true));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);

		GuiUtils.giveGridLayout(parent, 3);	
		comp.setBackground(gray1);

		startRecordDescriptionText = new Text(comp, SWT.BORDER);
		startRecordDescriptionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		startRecordButton = new CommandButton(comp, SWT.NONE);
		startRecordButton.setText(START_RECORD_STRING);
		startRecordButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		startRecordButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		startRecordButton.setCompositeEnabled(true);
		startRecordButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String param = startRecordDescriptionText.getText();
				commandPublisher.sendGenericOneStringCommand(
						DATA_METHOD_START_RECORDING.VALUE,
						DATA.VALUE, 
						DATA_METHOD_START_RECORDING_PARAM_DESCRIPTION.VALUE, 
						param);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no-op
			}
		});
	}

	private void createStopRecordingButton(Composite parent) {
		stopRecordButton = new CommandButton(parent, SWT.NONE);
		stopRecordButton.setText(STOP_RECORD_STRING);
		stopRecordButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		stopRecordButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		stopRecordButton.setCompositeEnabled(true);
		stopRecordButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commandPublisher.sendGenericNoParamsCommand(
						DATA_METHOD_STOP_RECORDING.VALUE,
						DATA.VALUE);
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
		data.horizontalSpan = entireWidth-1;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		unsubscribe();
	}

	public void onConfigUpdate(Object config) {
		// TODO Auto-generated method stub
	}
	private void updateTables(){
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if(allTopicsTable.isVisible()){
					Collection<ARosTopic> allTopics = RosTopicsList.getTopicsList().getTopics();
					//										if(!apkFromAstrobee.isEmpty()){
					allTopicsTable.removeAll();
					//										}
					for(final ARosTopic topic : allTopics){
						final TableItem item = new TableItem(allTopicsTable, SWT.NONE);
						item.setText(new String[]{topic.topicName});
					}
					if(allTopicsTable.getSortColumn() != null){
						for(Listener listener: allTopicsTable.getSortColumn().getListeners(SWT.Selection)){
							if(listener instanceof SortListener){
								SortListener list = (SortListener)listener;
								list.refresh(allTopicsTable.getSortColumn());
							}
						}
					}	
				}

				//update bottom table
				Collection<ARosTopic> selTopics = RosTopicsList.getDataToDiskSettings().getTopics();

				topicSettingsTable.removeAll();
				for(final ARosTopic topic : selTopics) {
					final TableItem item = new TableItem(topicSettingsTable, SWT.NONE);
					item.setText(new String[]{topic.topicName, topic.getDownlink().toString(),Float.toString(topic.getFrequency())});
				}

				if(topicSettingsTable.getSortColumn() != null){
					for(Listener listener: topicSettingsTable.getSortColumn().getListeners(SWT.Selection)){
						if(listener instanceof SortListener){
							SortListener list = (SortListener)listener;
							list.refresh(topicSettingsTable.getSortColumn());
						}
					}
				}	
			}

		});
	}

	class SortListener implements Listener{
		final Table table;
		final int size;
		SortListener(final Table table,int size){
			this.table = table;
			this.size = size;
		}

		public void refresh(final TableColumn tableColumn){
			updateTable(tableColumn, (table.getSortDirection() == SWT.UP) ? SWT.DOWN : SWT.UP );
		}
		private void updateTable(final TableColumn tableColumn,final int sortDirection){
			final TableColumn sortColumn = tableColumn;
			final int byteFlipper = (sortDirection == SWT.UP) ?  1 : -1;
			final TableItem[] items = table.getItems();

			Arrays.sort(items, new Comparator<TableItem>() {
				@Override
				public int compare(final TableItem o1, final TableItem o2) {
					return byteFlipper * o1.getText().compareTo(o2.getText());
				}
			});
			for(final TableItem item : items){
				List<String> values = new ArrayList<String>();
				for(int i = 0;i < size; i++){
					values.add(item.getText(i));
				}
				item.dispose();
				final TableItem row = new TableItem(table, SWT.NONE);
				row.setText(values.toArray(new String[size]));
			}
			table.setSortColumn(sortColumn);
			table.setSortDirection((sortDirection == SWT.UP) ? SWT.DOWN: SWT.UP);
		}

		@Override
		public void handleEvent(final Event arg0) {
			updateTable(((TableColumn)arg0.widget),table.getSortDirection());
		}

	}
}
