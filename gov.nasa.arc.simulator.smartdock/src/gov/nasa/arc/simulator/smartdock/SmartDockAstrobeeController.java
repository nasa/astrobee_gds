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
package gov.nasa.arc.simulator.smartdock;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.HashMap;
import java.util.Random;

import rapid.ext.astrobee.BerthState;
import rapid.ext.astrobee.DockState;


public class SmartDockAstrobeeController {

	private static SmartDockAstrobeeController instance;
	private DockState berth = new DockState();
	private HashMap<Integer, FreeFlyer> flyerMap = new HashMap<Integer, FreeFlyer>();
	private final String UNPOWERED_STRING = "off";
	private Random random = new Random();

	public static SmartDockAstrobeeController getInstance(){
		if(instance == null){
			instance = new SmartDockAstrobeeController();
		}
		return instance;
	}

	public void wakeBerth(int berthNum){
		if(berthNum == 1 ){
			berth.berthOne.awake = true;
			flyerMap.put(berthNum, new FreeFlyer(Agent.valueOf(berth.berthOne.astrobeeName)));
			flyerMap.get(berthNum).publishData();
		}else if(berthNum == 2){
			berth.berthTwo.awake = true;
			flyerMap.put(berthNum, new FreeFlyer(Agent.valueOf(berth.berthTwo.astrobeeName)));
			flyerMap.get(berthNum).publishData();
		}
		SmartDockPublisher.getInstance().publishData();
	}

	public void setBerthOne(String name) {
		BerthState berth1 = berth.berthOne;
		setBerthState(berth1, name);
	}
	
	public void setBerthTwo(String name) {
		BerthState berth2 = berth.berthTwo;
		setBerthState(berth2, name);
	}
	
	public void setBerthState(BerthState berthState, String name) {
		if(name != null) {
			if(!name.equals(UNPOWERED_STRING)) {
				berthState.astrobeeName = name;
				berthState.occupied = true;
				berthState.awake = false;
				berthState.numBatteries = (short) (random.nextInt(3) + 1);
				berthState.maxCapacity =  (short) random.nextInt(57);
				berthState.currentCapacity =  (short) random.nextInt(144);
			} 
			else { // it's there but unpowered
				berthState.occupied = true;
				berthState.awake = false;
			}
		}
		else  { // not there
			berthState.occupied = false;
			berthState.awake = false;
		}
	}

	public DockState getBerth(){
		return berth;
	}

	public void terminateBerth(String berthName){
		flyerMap.get(berthName);
	}
}
