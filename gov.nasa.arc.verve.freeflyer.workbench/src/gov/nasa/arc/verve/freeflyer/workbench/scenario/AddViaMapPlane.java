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
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.irg.plan.model.modulebay.Bay;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.model.modulebay.Point3D;
import gov.nasa.arc.verve.common.PickInfo;
import gov.nasa.arc.verve.common.ScenePickListener;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Quad;

public class AddViaMapPlane extends Node implements ScenePickListener{
	private static Logger logger = Logger.getLogger(AddViaMapPlane.class);
	private static AddViaMapPlane INSTANCE;
	protected String quadDownName = "QuadDown";
	protected String quadUpName = "QuadUp";
	
	private ArrayList<ModulePlane> planes = new ArrayList<ModulePlane>();
	
	boolean visible;
	
	@Inject
	protected IEclipseContext context;
	
	@Inject
	public AddViaMapPlane() {
		super("AddViaMapPlane");
		INSTANCE = this;
	//	init();
	}
	
	public static AddViaMapPlane getStaticInstance() {
		if(INSTANCE == null) {
			logger.error("AddViaMapPlane not created");
		}
		return INSTANCE;
	}
	
	@PostConstruct
	private void init() {
		LocationMap map = LocationMap.getInstance();
		String[] modules = map.allModulesAsStringArray();
		for(String module : modules) {
			ModuleName moduleName = map.getModule(module);
			List<Bay> bays = map.getAllBays(moduleName);
			Point3D[] dividers = map.getDividers(moduleName);
			Point3D offset = map.getModuleOffset(moduleName);
			double[][] keepins = map.getKeepinZones(moduleName);
			
			ModulePlane plane = new ModulePlane(module + "Plane", bays, dividers, offset, keepins);
			plane.hidePlane();
			attachChild(plane);
			planes.add(plane);
		}
	}
	
	public void showPlane() {
		for(ModulePlane plane : planes) {
			plane.showPlane();
		}
		
		visible = true;
	}
	
	public void hidePlane() {
		for(ModulePlane plane : planes) {
			plane.hidePlane();
		}
		
		visible = false;
	}
	
	@Override
	public void processPick(PickInfo pickInfo) {
		if(!visible) {
			return;
		}
		
		if(pickInfo.getSpatial() instanceof Quad) {
			//Adds two without this, one for quad up and quad down.
			if(pickInfo.getSpatial().getName().startsWith(quadDownName)) {
				return;
			}

			ReadOnlyVector3 picked = pickInfo.getPickPoint();	
			if(context != null) {
				context.set(ContextNames.ADD_VIA_MAP_LOCATION, picked);
			}
		}	
	}
}
