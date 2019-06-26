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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

import gov.nasa.arc.irg.freeflyer.rapid.state.GuestScienceApkGdsRunning;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AgentsFromCommandLine;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class GuestScienceTelemetryPartFragment implements
GuestScienceStateListener {

	protected GuestScienceStateManager guestScienceStateManager;
	protected IEclipseContext context;
	final List<String> availableAstrobees = new ArrayList<String>();
	Table topTable, bottomTable;
	String currentlySelectedAstrobee;
	private int[] topTableColWidths = {150, 250};
	private int[] bottomTableColWidths = {150, 100, 100, 250};

	@Inject private MPart part;

	@Inject
	public GuestScienceTelemetryPartFragment(final Composite parent, final GuestScienceStateManager gssm, final MApplication app) {
		for(Agent a : AgentsFromCommandLine.INSTANCE.getAgentsList()){
			availableAstrobees.add(a.name());
		}
		guestScienceStateManager = gssm;
		context = app.getContext();
		iniTab(parent);
		guestScienceStateManager.addListener(this);
	}

	void iniTab(final Composite parent){
		if(guestScienceStateManager == null)
			return;

		final GridLayout gl = new GridLayout(1, true);
		parent.setLayout(gl);


		final Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, true));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Combo combo = new Combo(group, SWT.READ_ONLY);
		combo.setItems(availableAstrobees.toArray(new String[availableAstrobees.size()]));

		topTable = new Table(group, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		topTable.setLinesVisible(true);
		topTable.setHeaderVisible(true);
		topTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final String[] topHeaders = {"APK","Status"};
		for(int i = 0; i < topHeaders.length; i++){
			final TableColumn headers = new TableColumn(topTable, SWT.NONE);
			headers.setText(topHeaders[i]);
			headers.setWidth(topTableColWidths[i]);
			headers.addListener(SWT.Selection, new SortListener(topTable,2));
		}
		topTable.setVisible(false);
		
		topTable.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(topTable, topTableColWidths);
			}
		});



		bottomTable = new Table(group, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		bottomTable.setLinesVisible(true);
		bottomTable.setHeaderVisible(true);
		bottomTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		final String[] bottomHeaders = {"APK","Topic","Label","Value"};
		
		for(int i = 0; i < bottomHeaders.length; i++){
			final TableColumn headers = new TableColumn(bottomTable, SWT.NONE);
			headers.setText(bottomHeaders[i]);
			headers.setWidth(bottomTableColWidths[i]);
			headers.addListener(SWT.Selection, new SortListener(bottomTable,4));
		}
		bottomTable.setVisible(false);

		bottomTable.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(bottomTable, bottomTableColWidths);
			}
		});


		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {

				currentlySelectedAstrobee = combo.getItem(combo.getSelectionIndex());
				part.setLabel(currentlySelectedAstrobee + " Telemetry");

				topTable.setVisible(true);
				topTable.removeAll();

				bottomTable.setVisible(true);
				bottomTable.removeAll();

				updateTables();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

	}

	private void updateTables(){
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if(topTable != null && !topTable.isDisposed() && topTable.isVisible()){
					List<GuestScienceApkGdsRunning> apkFromAstrobee = guestScienceStateManager.getApkInfoFromAstrobee(availableAstrobees.indexOf(currentlySelectedAstrobee));
					if(!apkFromAstrobee.isEmpty()){
						topTable.removeAll();
					}
					for(final GuestScienceApkGdsRunning apk : apkFromAstrobee){
						final TableItem item = new TableItem(topTable, SWT.NONE);
						item.setText(new String[]{apk.getShortName(),apk.getStatusString()});
					}
					if(topTable.getSortColumn() != null){
						for(Listener listener: topTable.getSortColumn().getListeners(SWT.Selection)){
							if(listener instanceof SortListener){
								SortListener list = (SortListener)listener;
								list.refresh(topTable.getSortColumn());
							}
						}
					}	
				}


				//update bottom table
				if(bottomTable != null && !bottomTable.isDisposed() && bottomTable.isVisible()){
					Map<String, TreeMap<String, TreeMap<String, String>>> allData = guestScienceStateManager.getGuestScienceDataFromAstrobee(availableAstrobees.indexOf(currentlySelectedAstrobee));
					if(allData == null) {
						return;
					}
					if(!allData.isEmpty())
						bottomTable.removeAll();
					for(Entry<String, TreeMap<String, TreeMap<String, String>>> apkNameEntry : allData.entrySet()) {
						String apkName = apkNameEntry.getKey();
						for(Entry<String, TreeMap<String, String>> topicEntry : apkNameEntry.getValue().entrySet()) {
							String topic = topicEntry.getKey();
							for(Entry<String,String> pairEntry: topicEntry.getValue().entrySet()) {
								final TableItem item = new TableItem(bottomTable,SWT.NONE);
								item.setText(new String[]{apkName, topic,pairEntry.getKey(),pairEntry.getValue()});
							}

						}
					}
					if(bottomTable.getSortColumn() != null){
						for(Listener listener: bottomTable.getSortColumn().getListeners(SWT.Selection)){
							if(listener instanceof SortListener){
								SortListener list = (SortListener)listener;
								list.refresh(bottomTable.getSortColumn());
							}
						}
					}	
				}
			}
		});
	}

	@Override
	public void onGuestScienceStateChange(final GuestScienceStateManager manager) {
		updateTables();
	}

	public void onGuestScienceConfigChange(final GuestScienceStateManager manager) {
		updateTables();
	}

	public void onGuestScienceDataChange(final GuestScienceStateManager manager, String apkName, String topic) {
		updateTables();
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
					if(sortColumn.getText().contains("STATUS")){
						if(o1.getText(1).contains("Running") && o2.getText(1).contains("Idle")){
							return 1*byteFlipper;
						}else if(o1.getText(1).contains("Idle") && o2.getText(1).contains("Running")){
							return -1*byteFlipper;
						}
					}else if(sortColumn.getText().contains("Label")){
						return o1.getText(2).compareTo(o2.getText(2))*byteFlipper;
					}else if(sortColumn.getText().contains("Value")){
						return o1.getText(3).compareTo(o2.getText(3))*byteFlipper;
					}else if(sortColumn.getText().contains("APK")){
						return o1.getText(0).compareTo(o2.getText(0))*byteFlipper;
					}else{ 
						return o1.getText(1).compareTo(o2.getText(1))*byteFlipper;
					}
					return 0;
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
	
	protected void fillColumn(Table table, int[] widths) {
		// calculate dataTreeWidths
		int columnsWidth = 0;
		for (int i = 0; i < table.getColumnCount() - 1; i++) {
			columnsWidth += table.getColumn(i).getWidth();
		}

		Point size = table.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = table.getVerticalBar();
		if(verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TableColumn lastColumn = table.getColumn(table.getColumnCount() - 1);
		if(columnsWidth + widths[widths.length - 1] + table.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth - table.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if(lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}
}
