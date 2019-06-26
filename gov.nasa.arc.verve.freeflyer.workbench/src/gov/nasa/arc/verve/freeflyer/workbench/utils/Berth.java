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
package gov.nasa.arc.verve.freeflyer.workbench.utils;

import gov.nasa.arc.irg.plan.util.PlanConstants;

import java.util.ArrayList;
import java.util.List;

import rapid.ext.astrobee.DockState;

public class Berth{
	private String berthOneName = "";
	private String berthTwoName = "";
	private boolean berthOneOccupied = false;
	private boolean berthTwoOccupied = false;
	private boolean berthOneAwake = false;
	private boolean berthTwoAwake = false;
	private String selectedBerth = "";
	
	public Berth(){
		berthOneName = PlanConstants.BERTH_ONE;
		berthTwoName = PlanConstants.BERTH_TWO;
	}

	public List<String> asList(){
		List<String> list = new ArrayList<String>();
		if(!berthOneAwake && berthOneOccupied)
			list.add(berthOneName);
		if(!berthTwoAwake && berthTwoOccupied)
			list.add(berthTwoName);
		return list;
	}
 
	public void ingestDockState(DockState dock) {
		berthOneName = dock.berthOne.astrobeeName.isEmpty() ? PlanConstants.BERTH_ONE : dock.berthOne.astrobeeName ;
		berthTwoName = dock.berthTwo.astrobeeName.isEmpty() ? PlanConstants.BERTH_TWO : dock.berthTwo.astrobeeName ;
		berthOneOccupied = dock.berthOne.occupied;
		berthTwoOccupied = dock.berthTwo.occupied;
		berthOneAwake = dock.berthOne.awake;
		berthTwoAwake = dock.berthTwo.awake;
	}

	public void selectedBerth(String bn) {
		selectedBerth = bn;
	}
	
	public boolean isAvailable(){
		if(berthOneName.equals(selectedBerth)){
			return berthOneOccupied; 
		}else if(berthTwoName.equals(selectedBerth)){
			return berthTwoOccupied;
		}
		return false;
	}
	
	public void disableBerth(){
		if(selectedBerth.equals(berthOneName)){
			berthOneOccupied = false;
		}else if(selectedBerth.equals(berthTwoName)){
			berthTwoOccupied = true;
		}
	}
	
	public int getSelectedBerthNum(){
		if(berthOneName.equals(selectedBerth)){
			return 1; 
		}else if(berthTwoName.equals(selectedBerth)){
			return 2;
		}
		return 0;
	}
	
	public int whichBerthNum(String name){
		if(berthOneOccupied && berthOneName.equals(name))
			return 1;
		else if(berthTwoOccupied && berthTwoName.equals(name))
			return 2;
		return 0;
	}
	
	public boolean equals(Berth b){
		return selectedBerth.equals(b.selectedBerth);
	}
	
	public String getSelectedBerth(){
		return selectedBerth;
	}
}

