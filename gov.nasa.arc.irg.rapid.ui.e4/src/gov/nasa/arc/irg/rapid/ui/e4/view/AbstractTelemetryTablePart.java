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
package gov.nasa.arc.irg.rapid.ui.e4.view;

import gov.nasa.arc.irg.rapid.ui.e4.MessageBundle;
import gov.nasa.arc.irg.util.NameValue;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import rapid.AgentConfig;

public abstract class AbstractTelemetryTablePart extends AbstractTelemetryPart {
	private static final Logger logger = Logger.getLogger(AbstractTelemetryTablePart.class);
	protected Table m_table;
	protected TableViewer m_tableViewer;
	protected IContentProvider m_contentProvider;
	protected ITableLabelProvider m_labelProvider;

	public String[] m_titles = {"Name","Value"};
	public int[] m_widths = {135, 135};
	public int[] m_alignments = {SWT.LEFT, SWT.RIGHT};


	public AbstractTelemetryTablePart(Composite parent, EPartService eps) {
		super();
		createPartControl(parent);
	}

	@Override
	public void createPartControl(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2,false));

		createPreTableParts(container);
		m_table = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION);
		m_table.setLinesVisible(true);
		m_table.setHeaderVisible(true);
		//			m_table.setFont(FontHelper.getTableFont());
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

		// XXX please tell me we don't need this
		// resize the row height using a MeasureItem listener
		//			m_table.addListener(SWT.MeasureItem, new Listener() {
		//				public void handleEvent(Event event) {
		//					if (m_tableFontHeightChanged){
		//						event.height = FontHelper.getTableFontHeight();
		//						m_tableFontHeightChanged = false;
		//					}
		//				}
		//			});

		//			createPostTableParts(container);
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

		//			public Color getForeground(Object element, int columnIndex) {
		//				return FontHelper.getTableFontColor();
		//			}
		//
		//			public Font getFont(Object element, int columnIndex) {
		//				return FontHelper.getTableFont();
		//			}
		//
		//			@Override
		//			public Color getBackground(Object element, int columnIndex) {
		//				return null;
		//			}
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

	protected String[] getTitles() {
		return m_titles;
	}

	protected int[] getAlignments() {
		return m_alignments;
	}
	
	@Override
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
	
	protected int[] getWidths() {
		return m_widths;
	}

}
