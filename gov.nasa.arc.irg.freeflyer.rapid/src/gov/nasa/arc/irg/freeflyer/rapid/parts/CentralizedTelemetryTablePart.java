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
package gov.nasa.arc.irg.freeflyer.rapid.parts;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.rapid.ui.e4.MessageBundle;
import gov.nasa.arc.irg.util.NameValue;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class CentralizedTelemetryTablePart implements IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(CentralizedTelemetryTablePart.class);
	protected String m_selectedPartition = "";
	protected Table m_table;
	protected TableViewer m_tableViewer;
	protected IContentProvider m_contentProvider;
	protected ITableLabelProvider m_labelProvider;
	
	public String[] m_titles = {"Name","Value"};
	public int[] m_widths = {135, 135};
	public int[] m_alignments = {SWT.LEFT, SWT.RIGHT};
	
	protected HashMap<MessageType, MessageBundle> m_messageCache = new HashMap<MessageType, MessageBundle>();
	private String m_participantId = Rapid.PrimaryParticipant;
    protected Agent         m_agent           = null;
    private Label m_agentLabel;
    
	protected Timer m_timer = new Timer(false); // this is the timer that will run the throttled updates
	protected TimerTask m_updateTask = null;   // this is the actual update

	/**
	 * generally this is for formatting numbers, which should be right aligned if they are in a table.
	 */
	public static DecimalFormat s_decimalFormat = new DecimalFormat("###.##"); 
	{
		s_decimalFormat.setDecimalSeparatorAlwaysShown(true);
		s_decimalFormat.setMinimumFractionDigits(2);
		s_decimalFormat.setMaximumFractionDigits(2);
		s_decimalFormat.setPositivePrefix(" ");
	}
	
	protected abstract Object getConfigObject();
	protected abstract void setConfigObject(Object config);
	protected abstract Class getConfigClass();
	protected abstract String getMementoKey();
	protected abstract NameValue[] getNameValues(Object input);
	protected abstract boolean configIdMatchesSampleId(Object configObj, Object eventObj);

	protected abstract MessageType getSampleType();
	protected abstract MessageType getConfigType();
	
	public CentralizedTelemetryTablePart(Composite parent, EPartService eps) {
		createPartControl(parent);
	}
	
	protected void onCreatePartControlComplete() {
		setupTimerTask();
	}
	
	protected void createPreTableParts(Composite container) {
		m_agentLabel = new Label(container, SWT.None);
		m_agentLabel.setText(FreeFlyerStrings.UNCONNECTED_STRING);
	}
	
	public void createPartControl(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1,false));
		
		createPreTableParts(container);
		m_table = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION);
		m_table.setLinesVisible(true);
		m_table.setHeaderVisible(true);
//		m_table.setFont(FontHelper.getTableFont());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		m_table.setLayoutData(data);
		
		Listener tableListener = getTableListener();
		if(tableListener != null){
			m_table.addListener(SWT.Dispose, tableListener);
			m_table.addListener(SWT.KeyDown, tableListener);
			m_table.addListener(SWT.MouseMove, tableListener);
			m_table.addListener(SWT.MouseHover, tableListener);
		}
		
		String[] titles = getTitles();
		int[] widths = getWidths();
		int[] alignments = getAlignments();
		for(int i = 0; i<titles.length; i++){
			TableColumn column = new TableColumn(m_table, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(widths[i]);
			column.setMoveable(true);
			if(alignments != null){
				column.setAlignment(alignments[i]);
			}
		}
		
		m_tableViewer = new TableViewer(m_table);
		m_tableViewer.setLabelProvider(getLabelProvider());
		m_tableViewer.setContentProvider(getContentProvider());
		
//		KnRoverUI LOOK into this once you finish the rest  Talk to Tamer about this
	
		// XXX please tell me we don't need this
		// resize the row height using a MeasureItem listener
//		m_table.addListener(SWT.MeasureItem, new Listener() {
//			public void handleEvent(Event event) {
//				if (m_tableFontHeightChanged){
//					event.height = FontHelper.getTableFontHeight();
//					m_tableFontHeightChanged = false;
//				}
//			}
//		});
		
//		createPostTableParts(m_container);
//		
        onCreatePartControlComplete();
	}
	
	protected ITableLabelProvider getLabelProvider() {
		if(m_labelProvider == null){
			m_labelProvider = new SubscriptionLabelProvider();
		}
		return m_labelProvider;
	}

	protected IContentProvider getContentProvider() {
		if(m_contentProvider == null){
			m_contentProvider = new SubscriptionContentProvider();
		}
		return m_contentProvider;
	}
	
	private Listener getTableListener() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected class SubscriptionLabelProvider implements ITableLabelProvider {//, ITableColorProvider, ITableFontProvider{

		@Override
		public void addListener(ILabelProviderListener listener) {
			// noop
		}

		@Override
		public void dispose() {
			//noop
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// noop

		}

//		public Color getForeground(Object element, int columnIndex) {
//			return FontHelper.getTableFontColor();
//		}
//
//		public Font getFont(Object element, int columnIndex) {
//			return FontHelper.getTableFont();
//		}
//
//		@Override
//		public Color getBackground(Object element, int columnIndex) {
//			return null;
//		}
//
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if(element != null && element instanceof NameValue){
				NameValue nameValue = (NameValue)element;
				switch(columnIndex){
				case 0:
					return nameValue.name;
				case 1:
					if(nameValue.format){
						try {
							if (nameValue.value instanceof Number){
								return format((Number)nameValue.value);
							}
						} catch (ClassCastException cce){
							return nameValue.value.toString();
						}
							
					} else {
						return nameValue.value.toString();
					}
				}
			}
			return null;
		}
	}

	// Content provider
	protected class SubscriptionContentProvider implements IStructuredContentProvider{

		protected NameValue[] m_itemsList = null;

		@Override
		public void dispose() {
			m_itemsList = null;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			m_itemsList = null;
			if (newInput != null){
				m_itemsList = getNameValues(newInput);
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return m_itemsList;
		}
	}
	
	protected String[] makePartitionsList() {
		Agent[] agents = ActiveAgentSet.asArray();
		
		String[] agentStrings = new String[agents.length];
		for(int i=0; i<agents.length; i++) {
			agentStrings[i] = agents[i].name();
		}
		return agentStrings;
	}
	


	protected String[] getTitles() {
		return m_titles;
	}
	
	protected int[] getAlignments() {
		return m_alignments;
	}
	
	/**
	 * Format a float to ###.##
	 * @param value
	 * @return
	 */
	public String format(Number value){
		String result = s_decimalFormat.format(value);
		return result;
	}

	
	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object eventObj, Object configObj) {
		if (msgType.equals(getConfigType())){
			if (eventObj != null && eventObj.getClass().equals(getConfigClass())){
				setConfigObject(eventObj);
			}
		} else {
			if (configObj != null && configObj.getClass().equals(getConfigClass())){
				setConfigObject(configObj);
			}
			
			m_messageCache.put(msgType,  new MessageBundle(agent, msgType, eventObj, configObj)); 
		}
	}
	
	public void subscribe() {
		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}
		
		final Agent agent = getAgent();
		final String id = getParticipantId();
		
		DdsTask.dispatchExec(new Runnable() {
			@Override
			public void run() {
				for(MessageType mt : getMessageTypes()) {
					if (mt == null) continue;
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							mt, 
							CentralizedTelemetryTablePart.this);
				}
			}
		});
	}
	
	private List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		ret.add(getSampleType());
		ret.add(getConfigType());
		return ret;
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}
	/**
	 * create and schedule the 1 second update
	 * Be sure to call this at creatPartControl
	 */
	protected void setupTimerTask() {
		m_updateTask = new TimerTask() {
			// grab display because OSX chokes if Display.getDefault() is called during shutdown
			final Display display = Display.getDefault();
			@Override
			public void run() {
				try {
					// this gives org.eclipse.swt.SWTException: Invalid thread access
					//Display display = Display.getDefault();
					if (!display.isDisposed()){
						display.asyncExec(new Runnable() {
							public void run() {
								try {
									call();
								} catch (Exception e) {
									logger.error("call", e);
								}
							}
						});
					}
				} catch (SWTException e){
                    logger.error("SWTException", e);
				}
			}
		};
		m_timer.schedule(m_updateTask, 0, 1000 * getRefreshSeconds());
	}
	
	public Boolean call() throws Exception {
		MessageBundle latestEvent = null;
		if (getAgent() != null){
			latestEvent = getLastMessageToProcess(getSampleType());
		}

		if(latestEvent != null){
			if(latestEvent.configObj == null){
				logger.warn(" - SubscriptionView received message with no config");
				return Boolean.FALSE;
			}

			// Setup the content if things match up
			if (configIdMatchesSampleId(latestEvent.configObj, latestEvent.eventObj)){
				m_tableViewer.setInput(latestEvent.eventObj);
				m_contentProvider.inputChanged(m_tableViewer, null, latestEvent.eventObj);
				m_tableViewer.refresh();
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Retrieve the last unprocessed message bundle of this type.  
	 * This also removes it from the cache.
	 * @param messageType
	 * @return
	 */
	 protected MessageBundle getLastMessageToProcess(MessageType messageType){
		 MessageBundle found = m_messageCache.remove(messageType);
//		 if (found != null){
//			 System.out.println("FOUND " + found.toString());
//		 }
		return found;
	}
	
	@Inject @Optional
	public void followedAgentChanged(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent followed) {
		if(followed == null) {
			return;
		}
		unsubscribe();
		m_agent = followed;
		m_agentLabel.setText(FreeFlyerStrings.CONNECTED_STRING + followed.toString());
		subscribe();
	}
	
    public String getParticipantId() {
        return m_participantId;
    }
    
    public Agent getAgent() {
        return m_agent;
    }
    
	/**
	 * @return how many seconds between UI refresh
	 */
	protected int getRefreshSeconds() {
		return 1;
	}
	
	protected int[] getWidths() {
		return m_widths;
	}
}
