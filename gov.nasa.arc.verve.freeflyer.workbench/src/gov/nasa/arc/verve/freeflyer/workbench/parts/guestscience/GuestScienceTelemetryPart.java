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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class GuestScienceTelemetryPart  implements GuestScienceStateListener{
	protected GuestScienceStateManager guestScienceStateManager;
	protected IEclipseContext context;
	final List<String> items = new ArrayList<String>();
	final HashMap<String,List<Table>> topTables = new HashMap<String, List<Table>>();
	final HashMap<String,List<Table>> bottomTables = new HashMap<String,List<Table>>();
	final int NUM_TABLES = 2;
	
	@Inject
	public GuestScienceTelemetryPart(final Composite parent, final GuestScienceStateManager gssm, final MApplication app) {
		for(Agent a : AgentsFromCommandLine.INSTANCE.getAgentsList()){
			items.add(a.name());
		}
		guestScienceStateManager = gssm;
		context = app.getContext();
		iniTab(parent);
		guestScienceStateManager.addListener(this);
	}
	
	void iniTab(final Composite parent){
		if(guestScienceStateManager == null)
			return;
		
		final GridLayout gl = new GridLayout(NUM_TABLES, true);
		parent.setLayout(gl);
		
		for(int t = 0 ; t < NUM_TABLES ;t++){
			
			final Group group = new Group(parent, SWT.NONE);
			group.setLayout(new GridLayout(1, true));
			group.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			final Combo combo = new Combo(group, SWT.READ_ONLY);
			combo.setItems(items.toArray(new String[items.size()]));
		
			final Table topTable = new Table(group, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
			        | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
			topTable.setLinesVisible(true);
		    topTable.setHeaderVisible(true);
		    topTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		    
			final String[] topHeaders = {"APK                           ","Status    "};
			for(int i = 0; i < topHeaders.length; i++){
				final TableColumn headers = new TableColumn(topTable, SWT.NONE);
				headers.setText(topHeaders[i]);
				headers.addListener(SWT.Selection, new SortListener(topTable,2));
				topTable.getColumn(i).pack();
			}
			topTable.setVisible(false);
			
			
			
			final Table bottomTable = new Table(group, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
			        | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
			bottomTable.setLinesVisible(true);
			bottomTable.setHeaderVisible(true);
			bottomTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		   
			final String[] bottomHeaders = {"APK                           ","Topic        ","Label        ","Value                  "};
			for(int i = 0; i < bottomHeaders.length; i++){
				final TableColumn headers = new TableColumn(bottomTable, SWT.NONE);
				headers.setText(bottomHeaders[i]);
				headers.addListener(SWT.Selection, new SortListener(bottomTable,4));
				bottomTable.getColumn(i).pack();
			}
			bottomTable.setVisible(false);
			
			
			
			combo.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					
					final String item = combo.getItem(combo.getSelectionIndex());
					
					for(final String key : topTables.keySet()){
						if(topTables.containsKey(key)){
							if(topTables.get(key) != null && topTables.get(key).contains(topTable)){
								topTables.get(key).remove(topTable);
							}
						}
					}
					
					if(!topTables.containsKey(item)){
						topTables.put(item, new ArrayList<Table>());
					}
					if(!topTables.get(item).contains(topTable))
						topTables.get(item).add(topTable);
					
					topTable.setVisible(true);
					topTable.removeAll();
					
					for(final String key : bottomTables.keySet()){
						if(bottomTables.containsKey(key)){
							if(bottomTables.get(key) != null && bottomTables.get(key).contains(bottomTable)){
								bottomTables.get(key).remove(bottomTable);
							}
						}
					}
					if(!bottomTables.containsKey(item)){
						bottomTables.put(item, new ArrayList<Table>());
					}
					if(!bottomTables.get(item).contains(bottomTable))
						bottomTables.get(item).add(bottomTable);
					
					bottomTable.setVisible(true);
					bottomTable.removeAll();
					
					updateTables();
				}
	
				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {

				}
			});
		}
	}

	private void updateTables(){
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				final Iterator<String> iterator = topTables.keySet().iterator();
				while(iterator.hasNext()){
					final String key = iterator.next();
					
					//update top table
					for(final Table table : topTables.get(key)){
						if(table.isVisible()){
							List<GuestScienceApkGdsRunning> apkFromAstrobee = guestScienceStateManager.getApkInfoFromAstrobee(items.indexOf(key));
							if(!apkFromAstrobee.isEmpty()){
								table.removeAll();
							}
							for(final GuestScienceApkGdsRunning apk : apkFromAstrobee){
								final TableItem item = new TableItem(table, SWT.NONE);
								item.setText(new String[]{apk.getShortName(),apk.getStatusString()});
							}
							if(table.getSortColumn() != null){
								for(Listener listener: table.getSortColumn().getListeners(SWT.Selection)){
									if(listener instanceof SortListener){
										SortListener list = (SortListener)listener;
										list.refresh(table.getSortColumn());
									}
								}
							}	
						}
					}

					//update bottom table
					for(final Table tableMore : bottomTables.get(key)){
						if(tableMore.isVisible()){
							Map<String, TreeMap<String, TreeMap<String, String>>> allData = guestScienceStateManager.getGuestScienceDataFromAstrobee(items.indexOf(key));
							if(allData == null) {
								return;
							}
							if(!allData.isEmpty())
								tableMore.removeAll();
							for(Entry<String, TreeMap<String, TreeMap<String, String>>> apkNameEntry : allData.entrySet()) {
								String apkName = apkNameEntry.getKey();
								for(Entry<String, TreeMap<String, String>> topicEntry : apkNameEntry.getValue().entrySet()) {
									String topic = topicEntry.getKey();
									for(Entry<String,String> pairEntry: topicEntry.getValue().entrySet()) {
										 final TableItem item = new TableItem(tableMore,SWT.NONE);
										 item.setText(new String[]{apkName, topic,pairEntry.getKey(),pairEntry.getValue()});
									}
								}
							}
							if(tableMore.getSortColumn() != null){
								for(Listener listener: tableMore.getSortColumn().getListeners(SWT.Selection)){
									if(listener instanceof SortListener){
										SortListener list = (SortListener)listener;
										list.refresh(tableMore.getSortColumn());
									}
								}
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
}
