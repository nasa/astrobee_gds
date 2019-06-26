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
package gov.nasa.arc.simulator.freeflyer.publishers;

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceConfigList;
import gov.nasa.arc.irg.plan.ui.io.GuestScienceConfigListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.GuestScienceApk;

public class SimulatorGuestScienceManager {
	private static final Logger logger = Logger.getLogger(SimulatorGuestScienceManager.class);
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	private static SimulatorGuestScienceManager instance;
	
	protected Vector<GuestScienceApk> apkStates = new Vector<GuestScienceApk>();
	protected Vector<Boolean> runningInfo = new Vector<Boolean>();

	public static SimulatorGuestScienceManager getInstance(){
		if(instance == null){
			instance = new SimulatorGuestScienceManager();
		}
		return instance;
	}
	
	public SimulatorGuestScienceManager() {
		putConfigsFromFileIntoVector();
	}
	
	public void putConfigsFromFileIntoVector() {	
		GuestScienceConfigList guestScienceConfigList;
		try {
			guestScienceConfigList = GuestScienceConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "GuestScienceConfigurations.json").getAbsolutePath());
			for(GuestScienceApkGds config: guestScienceConfigList.getGuestScienceConfigs()) {
				apkStates.add(config.asGuestScienceApk());
				runningInfo.add(false);
			}
		} catch (Exception e) {
			System.out.println("***** Problem loading GuestScienceApkGds file *****");
		}
	}
	
	private int getApkIndex(String apkName) {
		for(GuestScienceApk info : apkStates) {
			if(info.apkName.equals(apkName)) {
				return apkStates.indexOf(info);
			}
		}
		logger.error("APK " + apkName + " does not exist");
		return -1;
	}

	public void startApk(String apkName) {
		int index = getApkIndex(apkName);
		runningInfo.set(index, true);
	}
	
	public void stopApk(String apkName) {
		int index = getApkIndex(apkName);
		runningInfo.set(index, false);
	}
	
	public Vector<GuestScienceApk> getApkStates() {
		return apkStates;
	}
	
	public Vector<Boolean> getRunningInfo() {
		return runningInfo;
	}
}
