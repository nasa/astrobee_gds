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

import gov.nasa.arc.irg.georef.coordinates.LabeledLatLong;
import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.arc.verve.common.scenario.ScenarioActivator;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.common.ardor3d.shape.grid.GridTexture;
import gov.nasa.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

public class ScenarioPreferenceInitializer extends
AbstractPreferenceInitializer {


    // aggregate stored locations which include ones the user has saved.
    public static List<LabeledLatLong> s_storedLocations = new ArrayList<LabeledLatLong>();

    //hardcoded stored locations which will always be in the list
    public static List<LabeledLatLong> s_defaultLocations = new ArrayList<LabeledLatLong>();
    {
    	// don't need these and they are annoying me debugging
//        s_defaultLocations.add(new LabeledLatLong("Ames Marscape",         new LatLong( 37.416434790814,  -122.065765062141 )));
//        s_defaultLocations.add(new LabeledLatLong("Ames Roverscape",       new LatLong( 37.4196294362941, -122.0651151211069 )));
//        s_defaultLocations.add(new LabeledLatLong("JSC Rockyard",          new LatLong( 29.5648,          -95.0813          )));
//        s_defaultLocations.add(new LabeledLatLong("JPL",                   new LatLong( 34.20075,         -118.172655       )));
//        s_defaultLocations.add(new LabeledLatLong("HMP 2007",              new LatLong( 75.4332498203692, -89.8626936918380)));
//        s_defaultLocations.add(new LabeledLatLong("Moses Lake 2008",       new LatLong( 47.0712376476242, -119.2786230119466)));
//        s_defaultLocations.add(new LabeledLatLong("BlackPoint 2009",       new LatLong( 35.6907750993905, -111.4664424164273)));
//        s_defaultLocations.add(new LabeledLatLong("BlackPoint DRATS 2010", new LatLong( 35.687514,        -111.464121)));
//        //s_defaultLocations.add(new LabeledLatLong("PLRP",                  new LatLong( 50.868364,        -121.741521)));
//        s_defaultLocations.add(new LabeledLatLong("Basalt Hills 2011",     new LatLong( 37.0211162212131, -121.0975087332005)));
//        s_defaultLocations.add(new LabeledLatLong("MVP 2014",              new LatLong( 35.17766803991196,-116.1932787487913)));
//        //s_defaultLocations.add(new LabeledLatLong("Mojave Placeholder",    new LatLong( 35.2005210903, -115.8721469234)));
    }

    /**
     * This method is called by the preference initializer to initialize default
     * preference values. Clients should get the correct node for their 
     * bundle and then set the default values on it. For example:
     * <pre>
     *			public void initializeDefaultPreferences() {
     *				Preferences node = new DefaultScope().getNode("my.bundle.id");
     *				node.put(key, value);
     *			}
     * </pre>
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ScenarioActivator.getDefault().getPreferenceStore();

        store.setDefault(ScenarioPreferenceKeys.MOUSE_WHEEL_INVERT, false);
        store.setDefault(ScenarioPreferenceKeys.P_MAX_FLAT_GRIDS, 3);
        store.setDefault(ScenarioPreferenceKeys.P_GRID_DEPTH_WRITE, true);

        // initialize from previously stored positions
        s_storedLocations.addAll(s_defaultLocations);
        List<LabeledLatLong> storedPositions = LabeledLatLong.toLabeledLatLong(store.getString(ScenarioPreferenceKeys.P_SITE_LATLON_LOCATIONS));
        for (LabeledLatLong lll : storedPositions){
            if (!s_storedLocations.contains(lll)){
                s_storedLocations.add(lll);
            }
        }

        final String LATLON_MARSCAPE   = "37.416434790814 -122.065765062141";
        store.setDefault(ScenarioPreferenceKeys.P_SITE_FRAME_LOCATION, LATLON_MARSCAPE);
        store.setDefault(ScenarioPreferenceKeys.P_SITE_FRAME_ALTITUDE, 0);

        double sz;
        double[] offset = new double[3];
        //--- Flat Grid
        for(int i = 0; i < ScenarioPreferences.getNumFlatGrids(); i++) {
            if(i == 0)	store.setDefault(ScenarioPreferenceKeys.gridEnabled(i), "true");
            else 		store.setDefault(ScenarioPreferenceKeys.gridEnabled(i), "false");
            store.setDefault(ScenarioPreferenceKeys.gridSize(i), 100);
            store.setDefault(ScenarioPreferenceKeys.gridOffset(i),  "-50, -50, 0");
            store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i), GridTexture.Style.ThinWhiteOutline.toString());            
            store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i), "");
            store.setDefault(ScenarioPreferenceKeys.gridColor(i),           "100,100,100");
        }

        int i = 0;
        if(true) {
            // plain grid
            sz = 1000;
            offset[0] = -sz/2;
            offset[1] = -sz/2;
            offset[2] = -0.1;
            store.setDefault(ScenarioPreferenceKeys.gridSize(i),    sz);
            store.setDefault(ScenarioPreferenceKeys.gridOffset(i),  StrUtil.arrayToString(offset, ", "));
            store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i), GridTexture.Style.ThickAlphaOutline.toString());			
            store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i), "");
            store.setDefault(ScenarioPreferenceKeys.gridColor(i), 		    "45,45,90");
            i++;
        }

        //        if(false) {
        //            // marscape base tex has site frame in center
        //            sz = 0.3 * 2048;
        //            offset[0] = -sz/2;
        //            offset[1] = -sz/2;
        //            offset[2] =  0;
        //            store.setDefault(ScenarioPreferenceKeys.gridSize(i), 		 sz);
        //            store.setDefault(ScenarioPreferenceKeys.gridOffset(i), 	 StrUtil.arrayToString(offset, ", "));
        //            store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i), GridTexture.Style.ThinWhiteOutline.toString());			
        //            store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i), "marscape_0.3mpp.jpg");
        //            store.setDefault(ScenarioPreferenceKeys.gridColor(i), 		 "245,245,245");
        //            i++;
        //        }
        //
        //        //------------------------------------------------
        //        if(false) {			
        //            //-- Moses Lake 1 default
        //            if( i < ScenarioPreferences.getNumFlatGrids()) {
        //                // moses lake base tex has site frame in upper right
        //                sz = 0.6 * 2048;
        //                offset[0] = -sz - 1;
        //                offset[1] = -sz - 18;
        //                offset[2] =  345;
        //                store.setDefault(ScenarioPreferenceKeys.gridSize(i), 		sz);
        //                store.setDefault(ScenarioPreferenceKeys.gridOffset(i), 	StrUtil.arrayToString(offset, ", "));
        //                store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i), GridTexture.Style.ThinWhiteOutline.toString());			
        //                store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i),"MosesLake-2048-0.6mpp-sfUR+0+0.png");
        //                store.setDefault(ScenarioPreferenceKeys.gridColor(i), 		"235,235,235");
        //                i++;
        //            }
        //            //--Moses Lake 2 default
        //            if( i < ScenarioPreferences.getNumFlatGrids()) {
        //                sz = 0.6 * 2048;
        //                offset[0] = offset[0] - sz;
        //                store.setDefault(ScenarioPreferenceKeys.gridSize(i), 		 sz);
        //                store.setDefault(ScenarioPreferenceKeys.gridOffset(i), 	StrUtil.arrayToString(offset, ", "));
        //                store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i), GridTexture.Style.ThinWhiteOutline.toString());				
        //                store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i),"MosesLake-2048-0.6mpp-sfUR-1228.8+0.png");
        //                store.setDefault(ScenarioPreferenceKeys.gridColor(i), 		"235,235,235");
        //                i++;
        //            }
        //        }
        //
        //        //------------------------------------------------
        //		if(false) {
        //			//-- Haughton basecamp default
        //			if( i < ScenarioPreferences.getNumFlatGrids()) {
        //				// moses lake base tex has site frame in upper right
        //				sz = 0.6 * 2048;
        //				offset[0] = -sz/2;
        //				offset[1] = -sz/2;
        //				offset[2] = 225.0;
        //				store.setDefault(ScenarioPreferenceKeys.gridSize(i), 		sz);
        //				store.setDefault(ScenarioPreferenceKeys.gridOffset(i), 	StrUtil.arrayToString(offset, ", "));
        //				store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i),"grid_alphaOutline.png");			
        //				store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i),"HMP_basecamp_sharp70.png");
        //				store.setDefault(ScenarioPreferenceKeys.gridColor(i), 		"255,255,255");
        //				i++;
        //			}
        //			//-- Haughton drill hill defaults
        //			if( i < ScenarioPreferences.getNumFlatGrids()) {
        //				sz = 0.6 * 2048;
        //				offset[0] = -sz/2 + -1524.0;
        //				offset[1] = -sz/2 +  2840.0;
        //				offset[2] = 165.0;
        //
        //				store.setDefault(ScenarioPreferenceKeys.gridSize(i), 		sz);
        //				store.setDefault(ScenarioPreferenceKeys.gridOffset(i), 	StrUtil.arrayToString(offset, ", "));
        //				store.setDefault(ScenarioPreferenceKeys.gridGridTextureFile(i),"grid_alphaOutline.png");			
        //				store.setDefault(ScenarioPreferenceKeys.gridBaseTextureFile(i),"HMP_drillhill_sharp60.png");
        //				store.setDefault(ScenarioPreferenceKeys.gridColor(i), 		"255,255,255");
        //				i++;
        //			}
        //		}

    }

    /**
     * Store the saved positions back into the preference store
     */
    public static void storeSavedLocations(List<LabeledLatLong> positions) {

        List<LabeledLatLong> workingList = new ArrayList<LabeledLatLong>();
        for (LabeledLatLong lll : positions){
            if (!s_defaultLocations.contains(lll)){
                workingList.add(lll);
            }
        }
        String value = LabeledLatLong.toString(workingList);
        ScenarioActivator.getDefault().getPreferenceStore().setValue(ScenarioPreferenceKeys.P_SITE_LATLON_LOCATIONS, value);
        try {
            InstanceScope.INSTANCE.getNode(ScenarioActivator.PLUGIN_ID).flush();
            // FIXME : the call above is correct for newer versions of Eclipse. 
            // FIXME : the deprecated call below is used for Ensemble compatibility :(
            //(new InstanceScope()).getNode(ScenarioActivator.PLUGIN_ID).flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
}
