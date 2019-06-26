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

import gov.nasa.arc.irg.freeflyer.rapid.ILogPosterListener;
import gov.nasa.arc.irg.freeflyer.rapid.LogEntry;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.verve.freeflyer.workbench.utils.MyViewerComparator;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class LogDialog extends Dialog implements ILogPosterListener {
	protected Table m_table;
	protected TableViewer m_tableViewer;
	protected IContentProvider m_contentProvider;
	protected ITableLabelProvider m_labelProvider;
	private MyViewerComparator m_comparator;
	protected MApplication application;

	public String[] m_titles = {"Timestamp","Message","ID","Category","Agent","Error"};
	public int[] m_widths = {80, 300, 200, 80, 100, 300};
	public int[] m_alignments = {SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT};
	
	@Inject
	public LogDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell, Display display, MApplication mapp)
	{
		super(parentShell);
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS| SWT.BORDER | SWT.TITLE);
		application = mapp;
		LogPoster.addListener(this);
	}

	@Override
	public boolean close() {
		boolean returnValue = super.close();
		LogPoster.removeListener(this);
		application.getContext().set(LogDialog.class, null);
		return returnValue;
	}

	// Add column to log that contains the error message
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button - cancel doesn't mean anything for this dialog
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		setupTheTable(container);
		setupTheTableViewer();

		return container;
	}

	private void setupTheTableViewer() {
		m_tableViewer = new TableViewer(m_table);
		m_tableViewer.setLabelProvider(getLabelProvider());
		m_tableViewer.setContentProvider(getContentProvider());
		
		if(LogPoster.getLogEntries() != null) {
			m_tableViewer.setInput(LogPoster.getLogEntries());
		}
		
		// Set the sorter for the table
		m_comparator = new MyViewerComparator();
		m_tableViewer.setComparator(m_comparator);
	}

	private void setupTheTable(Composite container) {
		m_table = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION);
		m_table.setLinesVisible(true);
		m_table.setHeaderVisible(true);
		//			m_table.setFont(FontHelper.getTableFont());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 5;
		m_table.setLayoutData(data);

		String[] titles = getTitles();
		int[] widths = getWidths();
		int[] alignments = getAlignments();
		for(int colNumber = 0; colNumber<titles.length; colNumber++){
			TableColumn column = new TableColumn(m_table, SWT.NONE);
			column.setText(titles[colNumber]);
			column.setWidth(widths[colNumber]);
			column.setMoveable(true);
			if(alignments != null){
				column.setAlignment(alignments[colNumber]);
			}
			column.addSelectionListener(getSelectionAdapter(column, colNumber));
		}
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
					return logEntry.getCmdId();
				case 3:
					return logEntry.getCategory();
				case 4:
					return logEntry.getAgent();
				case 5:
					return logEntry.getAckMessage();
				}
			}
			return null;
		}
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_comparator.setColumn(index);
				int dir = m_comparator.getDirection();
				m_tableViewer.getTable().setSortDirection(dir);
				m_tableViewer.getTable().setSortColumn(column);
				m_tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Log Details");
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

	public void postedToLog(String post) {
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
