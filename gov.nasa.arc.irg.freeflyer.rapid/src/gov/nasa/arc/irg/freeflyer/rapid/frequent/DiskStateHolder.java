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
package gov.nasa.arc.irg.freeflyer.rapid.frequent;

import gov.nasa.arc.irg.freeflyer.rapid.state.DiskInfoGds;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.Vector;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.MApplication;

import rapid.ext.astrobee.DiskConfig;
import rapid.ext.astrobee.DiskInfo;
import rapid.ext.astrobee.DiskInfoConfig;
import rapid.ext.astrobee.DiskState;

public class DiskStateHolder extends AbstractFrequentTelemetryHolder {
	private static final Logger logger = Logger.getLogger(DiskStateHolder.class);
	protected boolean diskConfigReceived = false;
	protected Vector<DiskInfoGds> diskInfo = new Vector<DiskInfoGds>();
	protected DiskState storedDiskState;
	protected boolean hasStoredDiskState = false;
	
	@Inject
	public DiskStateHolder(MApplication application) {
		sampleType = MessageTypeExtAstro.DISK_STATE_TYPE;
		configType = MessageTypeExtAstro.DISK_CONFIG_TYPE;

		topContext = application.getContext();

		init();
	}

	@Override
	protected void init() {
		super.init();
		topContext.set(DiskStateHolder.class, this);
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.DISK_STATE_TYPE)) { // FAST
			DiskState diskState = (DiskState) msgObj;
			ingestDiskState(diskState);
		}
		else if(msgType.equals(MessageTypeExtAstro.DISK_CONFIG_TYPE)) {
			DiskConfig diskConfig = (DiskConfig) msgObj;
			ingestDiskConfig(diskConfig);
		}
	}

	public synchronized void ingestDiskState(DiskState diskState) {
		if(!diskConfigReceived) {
			// store for later
			storedDiskState = diskState;
			hasStoredDiskState = true;
			return;
		}
		int numDisks = diskState.filesystems.userData.size();

		if(numDisks > diskInfo.size()) {
			logger.error("DiskState size does not match DiskConfig size");
			return;
		}

		for(int i=0; i<numDisks; i++) {
			diskInfo.get(i).update((DiskInfo)diskState.filesystems.userData.get(i));
		}
	}

	public synchronized void ingestDiskConfig(DiskConfig diskConfig) {
		diskInfo.clear();
		for(int i=0; i<diskConfig.filesystems.userData.size(); i++) {
			diskInfo.add(new DiskInfoGds((DiskInfoConfig)diskConfig.filesystems.userData.get(i)));
		}
		if(storedDiskState != null) {
			ingestDiskState(storedDiskState);
		}
		diskConfigReceived = true;
	}
	
	public Vector<DiskInfoGds> getDiskInfo() {
		return diskInfo;
	}
}
