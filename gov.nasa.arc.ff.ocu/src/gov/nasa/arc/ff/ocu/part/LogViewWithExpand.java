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
package gov.nasa.arc.ff.ocu.part;

import gov.nasa.arc.irg.freeflyer.rapid.ILogPosterListener;
import gov.nasa.arc.irg.freeflyer.rapid.LogEntry;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class LogViewWithExpand implements ILogPosterListener {
	protected Composite  m_composite;
	protected Label		 m_dataLabel;
	
	protected Table m_table;
	protected TableViewer m_tableViewer;
	protected IContentProvider m_contentProvider;
	protected ITableLabelProvider m_labelProvider;

	public String[] m_titles = {"Timestamp","Message","Category"};
	public int[] m_widths = {70, 235, 70};
	public int[] m_alignments = {SWT.LEFT, SWT.LEFT, SWT.LEFT};
	
	@Inject
	public LogViewWithExpand(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);
		
		m_table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		m_table.setLinesVisible(true);
		m_table.setHeaderVisible(true);
		//			m_table.setFont(FontHelper.getTableFont());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		m_table.setLayoutData(data);

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
		m_tableViewer.setInput(LogPoster.getLogEntries());
		LogPoster.addListener(this);
	}
	
	 @PostConstruct
	  public void createControls(EMenuService menuService) {
	    // register context menu on the table
	    menuService.registerContextMenu(m_tableViewer.getControl(), 
	        "gov.nasa.arc.ff.ocu.popupmenu.popupmenulabel");
	  }

	protected String[] getTitles() {
		return m_titles;
	}

	protected int[] getAlignments() {
		return m_alignments;
	}

	protected int[] getWidths() {
		return m_widths;
	}
	protected ITableLabelProvider getLabelProvider() {
		if(m_labelProvider == null){
			m_labelProvider = new SubscriptionLabelProvider();
		}
		return m_labelProvider;
	}

	protected IContentProvider getContentProvider() {		
		return ArrayContentProvider.getInstance();
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
			if(element != null && element instanceof LogEntry){
				LogEntry logEntry = (LogEntry)element;
				switch(columnIndex){
				case 0:
					return logEntry.getTimestamp();
				case 1:
					return logEntry.getEntry();
				case 2:
					return logEntry.getCategory();
				}
			}
			return null;
		}
	}

	public void postedToLog(String post) {
		// show the new thing
		if(m_tableViewer != null) {
			Display.getDefault().asyncExec(new Runnable() {
	            public void run() {
	            	m_tableViewer.setInput(LogPoster.getLogEntries());
	    			m_tableViewer.refresh();
	            }
			});
		}
	}
}
