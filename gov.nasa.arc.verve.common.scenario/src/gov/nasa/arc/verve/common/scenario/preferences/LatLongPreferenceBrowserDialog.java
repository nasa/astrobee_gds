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
package gov.nasa.arc.verve.common.scenario.preferences;

import gov.nasa.arc.irg.georef.coordinates.EllipsoidReference;
import gov.nasa.arc.irg.georef.coordinates.LabeledLatLong;
import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.arc.irg.georef.coordinates.util.UtmLatLongConverter;
import gov.nasa.arc.irg.georef.ui.dialogs.EditLatLongUtmDialog;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class LatLongPreferenceBrowserDialog extends EditLatLongUtmDialog {

	protected List<LabeledLatLong> m_savedLocations;
	protected Table m_table;
	protected TableViewer m_tableViewer;
	
	protected IPreferenceStore m_preferenceStore;
	
	public LatLongPreferenceBrowserDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		container.setLayout(gridLayout);
		
		super.createDialogArea(container);
		
		Composite right = new Composite(container, SWT.NONE);
		gridLayout = new GridLayout(1, false);
		right.setLayout(gridLayout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.verticalSpan = 2;
		right.setLayoutData(data);
		
		Label label = new Label(right, SWT.NONE);
		label.setText("Click on a row to select a stored location.\nPress delete to remove a stored location.");
		
		m_table = new Table(right, SWT.SINGLE | SWT.FULL_SELECTION);
		m_table.setLinesVisible (true);
		m_table.setHeaderVisible (true);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 250;
		data.widthHint = 465;
		m_table.setLayoutData(data);
		
		m_table.addKeyListener(new KeyListener(){

		    @Override
			public void keyPressed(KeyEvent e) {
			    //
			}

		    @Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL){
					deleteSelectedLocation();
				} else if (e.keyCode == 8){
					String osName = System.getProperty("os.name").toLowerCase();
					if (osName.indexOf("mac") != -1){
						deleteSelectedLocation();
					}
				}
			}
			
		});
		
		String[] titles = {"Name", "Latitude", "Longitude"};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (m_table, SWT.NONE);
			column.setText (titles [i]);
			column.setWidth(150);
			column.setAlignment(SWT.RIGHT);
		}	
		
		m_tableViewer = new TableViewer(m_table);
		m_tableViewer.setLabelProvider(new TableLabelProvider());
		m_tableViewer.setContentProvider(new StoredLocationsContentProvider());
		m_tableViewer.setInput(m_savedLocations);
		m_tableViewer.getTable().setLinesVisible(true);
		
		m_tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

		    @Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				LabeledLatLong lll = (LabeledLatLong)selection.getFirstElement();
				if (lll != null){
					if (!m_editingLatLong){
						UTM utm = UtmLatLongConverter.toUTM(lll.getLatLong(), EllipsoidReference.WGS_84.getEllipsoid());
						updateUTM(utm);
					}
					// This will first have updated the lat long with a converted version from the UTM conversion, not exactly accurate.
					// shove the real lat long back in the lat long values after the fact.
					updateLatLong(lll.getLatLong());
				}
			}
			
		});
		
		// delete popup menu
		final Action a = new Action("Delete") {
			@Override
			public void run() {
				deleteSelectedLocation();
			}
		};
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		mgr.addMenuListener(new IMenuListener() {

		    @Override
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) m_tableViewer.getSelection();
				if (!selection.isEmpty()) {
					mgr.add(a);
				}
			}
		});
		m_tableViewer.getControl().setMenu(mgr.createContextMenu(m_tableViewer.getControl()));

		
		Composite addComposite = new Composite(container, SWT.NONE);
		gridLayout = new GridLayout(3, false);
		addComposite.setLayout(gridLayout);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		addComposite.setLayoutData(data);

		label = new Label(addComposite, SWT.NONE);
		label.setText("Enter a name and press Save to store location.");
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		
		label = new Label(addComposite, SWT.NONE);
		label.setText("Name:");
		
		final Text text = new Text(addComposite, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = 150;
		text.setLayoutData(data);
		
		Button addButton = new Button(addComposite, SWT.NONE);
		addButton.setText("Save");
		addButton.addSelectionListener(new SelectionListener(){

		    @Override
			public void widgetDefaultSelected(SelectionEvent e) {
			    //
			}

		    @Override
			public void widgetSelected(SelectionEvent e) {
				String name = text.getText();
				name = name.replace(" ", "_");
				LabeledLatLong lll = new LabeledLatLong(name, m_latLong.clone());
				m_savedLocations.add(lll);
				m_tableViewer.setInput(m_savedLocations);
				ScenarioPreferenceInitializer.storeSavedLocations(m_savedLocations);
			}

			
		});
		
		return container;
	}
	
	
	
	protected void deleteSelectedLocation(){
		IStructuredSelection iss = (IStructuredSelection)m_tableViewer.getSelection();
		if (iss != null && !iss.isEmpty()){
			LabeledLatLong lll = (LabeledLatLong)iss.getFirstElement();
			m_savedLocations.remove(lll);
			m_tableViewer.setInput(m_savedLocations);
			ScenarioPreferenceInitializer.storeSavedLocations(m_savedLocations);
		}
	}
	
	/**
	 * @return
	 */
	public List<LabeledLatLong> getSavedLocations() {
		return m_savedLocations;
	}

	/**
	 * @param savedLocations
	 */
	public void setSavedLocation(List<LabeledLatLong> savedLocations) {
		m_savedLocations = savedLocations;
	}
	
	/**
	 * @param preferenceStore
	 */
	public void setPreferenceStore(IPreferenceStore preferenceStore) {
		m_preferenceStore = preferenceStore;
	}
	
	/**
	 * Populate the table with the correct text
	 * @author tecohen
	 *
	 */
	protected class TableLabelProvider implements ITableLabelProvider {

	    @Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

	    @Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof LabeledLatLong){
				LabeledLatLong item = (LabeledLatLong)element;
				switch (columnIndex) {
				case 0:
					return item.getName();
				case 1:
				    double lat = item.getLatLong().getLatitude();
				    return LatLong.s_decimalFormat.format(lat);
				case 2:
				    double lon = item.getLatLong().getLongitude();
				    return LatLong.s_decimalFormat.format(lon);
				}
			}
			return null;
		}

	    @Override
		public void addListener(ILabelProviderListener listener) {
		    //
		}

	    @Override
		public void dispose() {
		    //
		}

	    @Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

	    @Override
		public void removeListener(ILabelProviderListener listener) {
		    //
		}
		
	}
	
	/**
	 * populate the table with the saved locations
	 * @author tecohen
	 *
	 */
	protected class  StoredLocationsContentProvider implements IStructuredContentProvider{
		
		
	    @Override
		public Object[] getElements(Object inputElement) {
			return m_savedLocations.toArray();
		}

	    @Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		    //
		}

	    @Override
		public void dispose() {
		    //
		}
	}
	
}
