/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.irg.iss.ui.view.log;

import gov.nasa.arc.irg.iss.ui.IssLogEntryMonitor;
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.iss.ui.view.log.internal.IssLogEntry;
import gov.nasa.arc.irg.iss.ui.view.log.internal.IssLogModelChangeListener;
import gov.nasa.arc.irg.iss.ui.view.log.internal.LogViewComparator;
import gov.nasa.arc.irg.util.log.IrgLevel;
import gov.nasa.util.StrUtil;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class IssLogView  extends ViewPart implements ILogEntryChangedListener {

	public static final int ROW_HEIGHT = 16;
	private final static Logger logger = Logger.getLogger(IssLogView.class);

	protected Table m_table;
	protected TableViewer m_tableViewer;

	protected int[] m_widths = {170, 45, 350};
	
	private LogViewComparator m_comparator;
	
	protected IssLogModelChangeListener m_issLogModelChangeListener = new IssLogModelChangeListener();
	
	public IssLogView() {
		m_comparator = new LogViewComparator();
	}

	@Override
	public void createPartControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = bars.getToolBarManager();
		toolBarManager.removeAll();

		IMenuManager    mm  = bars.getMenuManager();
        mm.removeAll();
		
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				setContentDescription("Log File: " + IssLogEntryMonitor.INSTANCE.getLogFile().getAbsolutePath());
			}
		});
        
		createViewer(composite);
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				composite.layout(true);
//				composite.redraw();
//				composite.update();
//			}
//		});
//		
		getSite().getPage().addPartListener(m_issLogModelChangeListener);
		IssLogEntryMonitor.INSTANCE.addLogEntryChangedListener(this);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removePartListener(m_issLogModelChangeListener);
		IssLogEntryMonitor.INSTANCE.removeLogEntryChangedListener(this);
	}

	private void createViewer(Composite parent) {
		
		m_table = new Table(parent, SWT.NO_FOCUS | SWT.FULL_SELECTION);
		m_table.setLinesVisible(true);
		m_table.setHeaderVisible(true);
		m_table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// create the columns
		String[] titles = {"GPS", "Ack", "Description"};
		int[] alignment = {SWT.LEFT, SWT.CENTER, SWT.LEFT};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (m_table, SWT.NONE);
			column.setText (titles[i]);
			column.setAlignment(alignment[i]);
			column.setWidth(m_widths[i]);
			column.setMoveable(false);
			column.setResizable(false);
		}
		
		
		m_tableViewer = new TableViewer(m_table);
		m_tableViewer.setLabelProvider(new LogLabelProvider());
		m_tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return IssLogEntryMonitor.INSTANCE.getElementsFromEntry();
			}
			
		});
		
		m_tableViewer.setComparator(m_comparator);
		m_tableViewer.setInput(IssLogEntryMonitor.INSTANCE.getElementsFromEntry());
		m_tableViewer.refresh();
	}
	

	public static List<IssLogEntry> getPreProcessedErrors() {
		return IssLogEntryMonitor.INSTANCE.getIssLogEntryList();
	}
	
	protected class LogLabelProvider extends OwnerDrawLabelProvider { // implements ITableLabelProvider {

		public static final int VERTICAL_INDENT = 3;
		public static final int LINE_WRAP_WIDTH = 40;

		@Override
		protected void measure(Event event, Object element) {
			int lines = 1;
			IssLogEntry line = (IssLogEntry) element;
			if (line.getDescription() != null){
				event.width = m_table.getColumn(2).getWidth();
				lines = StrUtil.computeNumberOfLines(line.getDescription(), LINE_WRAP_WIDTH);
				if (lines <= 1){
					event.height = ROW_HEIGHT;
				} else {
					event.height = ROW_HEIGHT * lines + ROW_HEIGHT + (VERTICAL_INDENT * 2);
				}
			} else {
				event.height = ROW_HEIGHT;
			}
		}

		@Override
		protected void paint(Event event, Object element) {
			if (event.gc.isDisposed()){
				return;
			}
			IssLogEntry entry = (IssLogEntry) element;
			String text = "";
			Image image = null;
			switch (event.index){
			case 0:
				text = ((IssLogEntry) element).getTime();
				text = text.substring(0, text.length()-4);
				break;
			case 1:
				if (entry.getLevel().isGreaterOrEqual(Level.WARN)) {
					if (((IssLogEntry) element).isAcknowledged()) {
						//text =  "X";  // Shown an "X" when it's been acked
						event.gc.drawText("X", event.x + 20, event.y + VERTICAL_INDENT);
						return;
					}
				}
				break;
			case 2:
				if (entry.getLevel().equals(Level.INFO)) { 
					image = IssUiActivator.getImageFromRegistry("info_16");
				} else if (entry.getLevel().equals(Level.ERROR)) { 
					image = IssUiActivator.getImageFromRegistry("fail_10");
				} else if (entry.getLevel().equals(Level.WARN) || entry.getLevel().equals(IrgLevel.ALERT)) {
					image = IssUiActivator.getImageFromRegistry("attention_16");
				}
				text =  StrUtil.insertNewlines(entry.getDescription(), LINE_WRAP_WIDTH);
				if (text.contains("Subscri")) {
					event.height = 58;
				}
				
			}
			if (image != null){
				event.gc.drawImage(image, event.x, event.y + VERTICAL_INDENT);
				event.gc.drawText(text, event.x + 20, event.y + VERTICAL_INDENT, true);
			} else {
				event.gc.drawText(text, event.x + 5, event.y + VERTICAL_INDENT, true);
			}
		}

	}




	@Override
	public void entryChanged(final IssLogEntry entry) {
		if (!m_tableViewer.getTable().isDisposed() && m_tableViewer.getTable().isVisible()){
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					m_tableViewer.update(entry, null);
				}
			});
		}
	}
	
	@Override
	public void entryAdded(IssLogEntry entry) {
		if (!m_tableViewer.getTable().isDisposed() && m_tableViewer.getTable().isVisible()){
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					m_tableViewer.setInput(IssLogEntryMonitor.INSTANCE.getElementsFromEntry());
//					m_tableViewer.refresh(true);
				}
			
			});
		}
	}
	
	
}
