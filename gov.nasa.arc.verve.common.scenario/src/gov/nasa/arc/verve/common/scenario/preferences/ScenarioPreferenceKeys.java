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

/**
 * 
 * @author mallan
 *
 */
public class ScenarioPreferenceKeys {

    //-- Mouse behavior
    public static String MOUSE_WHEEL_INVERT = "MouseWheelInvert";

    //--- Site Frame
    public static String P_SITE_FRAME_LOCATION   = "SiteFrameLocation";
    public static String P_SITE_FRAME_ALTITUDE   = "SiteFrameAltitude";
    public static String P_SITE_LATLON_DEFAULTS  = "SiteFrameLatLonDefaults";
    public static String P_SITE_LATLON_LOCATIONS = "SiteFrameLatLonPositions";

    //public static String P_MAX_KML_AUTOLOADS     = "MaxKmlAutoLoads";
    //public static String kmlAutoLoad(int index) { return "KmlAutoLoad."+index;  }
    //public static String P_KML_XYZ_OFFSET        = "KmlXyzOffset";

    //--- Flat Grid
    public static String P_MAX_FLAT_GRIDS   = "MaxFlatGrids";
    public static String P_GRID_DEPTH_WRITE = "GridDepthWrite";
    public static String gridEnabled(int index) { return "Grid."+index+".Enabled";	}
    public static String gridSize(int index)	{ return "Grid."+index+".Size";		}
    public static String gridOffset(int index)	{ return "Grid."+index+".Offset";	}
    public static String gridGridTextureFile(int index){ return "Grid."+index+".GridTexFile";	}
    public static String gridBaseTextureFile(int index){ return "Grid."+index+".BaseTexFile";	}
    public static String gridColor(int index)	{ return "Grid."+index+".Color"; 	}

}
