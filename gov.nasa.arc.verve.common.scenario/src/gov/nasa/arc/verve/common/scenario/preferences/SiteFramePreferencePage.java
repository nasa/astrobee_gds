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
import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.arc.irg.georef.coordinates.util.UtmLatLongConverter;
import gov.nasa.arc.verve.common.scenario.ScenarioActivator;
import gov.nasa.util.ui.jface.preference.DoubleFieldEditor;

import java.util.List;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SiteFramePreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

    protected LatLong m_initialLatLong;
    protected UTM     m_initialUtm;
    protected Text    m_locationText;
    protected Button  m_editLocationButton;
    protected LatLongPreferenceBrowserDialog m_editLocationDialog = null;

    public SiteFramePreferencePage() {
        super(GRID);
        setPreferenceStore(ScenarioActivator.getDefault().getPreferenceStore());
        this.setDescription("Specify Site Frame Location");
    }

    @Override
    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();        

        Composite locationEditingComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        locationEditingComposite.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.horizontalSpan = 3;
        locationEditingComposite.setLayoutData(gd);

        Label locationLabel = new Label(locationEditingComposite, SWT.NONE);
        locationLabel.setText("Location (Lat/Long and UTM):");
        gd = new GridData();
        gd.horizontalSpan = 2;
        locationLabel.setLayoutData(gd);

        int height = 40;
        m_locationText = new Text(locationEditingComposite, SWT.MULTI | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = height;
        m_locationText.setLayoutData(gd);

        initializeLocation();

        m_editLocationButton = new Button(locationEditingComposite, SWT.PUSH);
        m_editLocationButton.setText("Edit Location");
        m_editLocationButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e){
                if (m_editLocationDialog == null){
                    Shell shell = Display.getCurrent().getActiveShell();
                    m_editLocationDialog = new LatLongPreferenceBrowserDialog(shell);
                    m_editLocationDialog.setBlockOnOpen(true);
                    m_editLocationDialog.setLatLong(m_initialLatLong.clone());
                    m_editLocationDialog.setUTM(m_initialUtm.clone());
                    m_editLocationDialog.setPreferenceStore(getPreferenceStore());
                    m_editLocationDialog.setSavedLocation(ScenarioPreferenceInitializer.s_storedLocations);
                } else {
                    m_editLocationDialog.create();
                }
                if (m_editLocationDialog.open() == Window.OK){
                    m_initialLatLong.fillValues(m_editLocationDialog.getLatLong());
                    m_initialUtm.fillValues(m_editLocationDialog.getUTM());
                    // change value
                    refreshLocationText();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e){
                //
            }
        });

        DoubleFieldEditor dfe = new DoubleFieldEditor(ScenarioPreferenceKeys.P_SITE_FRAME_ALTITUDE, "Site Altitude:", parent);
        addField(dfe);

        //        StringFieldEditor sfe = new StringFieldEditor(ScenarioPreferenceKeys.P_KML_XYZ_OFFSET,"XYZ offset (for manual adjustment):", parent);
        //        addField(sfe);
        //        
        //        sfe = new StringFieldEditor (ScenarioPreferenceKeys.P_KML_SPATIAL_Z_TRACK,"Track Z value of spatial:", parent);
        //        sfe.getLabelControl(parent).setToolTipText("Enter the name of a spatial to track for Z value. \ne.g. \"K10Red:ModelRoot/.../k10base\"");
        //        addField(sfe);
        //
        //		FileFieldEditor ffe;
        //		for(int i = 0; i < ScenarioPreferences.getNumKmlAutoLoads(); i++) {
        //		    ffe = new FileFieldEditor(ScenarioPreferenceKeys.kmlAutoLoad(i), "Load KML at startup ("+i+"):", parent);
        //		    ffe.setFileExtensions(new String[] { "*.kml", "*" });
        //		    addField(ffe);
        //		}
    }

    protected void initializeLocation(){
        String defaultOffset = getPreferenceStore().getString(ScenarioPreferenceKeys.P_SITE_FRAME_LOCATION);
        if (defaultOffset == null || defaultOffset.length() == 0){
            defaultOffset = getPreferenceStore().getString(ScenarioPreferenceKeys.P_SITE_LATLON_DEFAULTS);
        }
        List<LatLong> latLongList = LatLong.toLatLong(defaultOffset);
        if (latLongList != null && !latLongList.isEmpty()){
            m_initialLatLong = latLongList.get(0);
            m_initialUtm = UtmLatLongConverter.toUTM(m_initialLatLong, EllipsoidReference.WGS_84.getEllipsoid());
            refreshLocationText();
        }
    }

    protected void refreshLocationText() {
        if (m_initialUtm != null) {
            StringBuffer locationTextContents = new StringBuffer(m_initialLatLong.toString());
            locationTextContents.append("\n");
            locationTextContents.append(m_initialUtm.toString());
            m_locationText.setText(locationTextContents.toString());
        } else if (m_initialLatLong != null){
            m_locationText.setText(m_initialLatLong.toString());
        }
    }

    @Override
    public void init(IWorkbench workbench) { 
        /* noop */
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        if (m_editLocationDialog != null && m_editLocationDialog.getLatLong() != null){
            getPreferenceStore().setValue(ScenarioPreferenceKeys.P_SITE_FRAME_LOCATION, m_editLocationDialog.getLatLong().toString());
        }
        return super.performOk();
    }

    @Override
    protected void performApply() {
        if (m_editLocationDialog != null && m_editLocationDialog.getLatLong() != null){
            getPreferenceStore().setValue(ScenarioPreferenceKeys.P_SITE_FRAME_LOCATION, m_editLocationDialog.getLatLong().toString());
        }
        super.performApply();
    }
}
