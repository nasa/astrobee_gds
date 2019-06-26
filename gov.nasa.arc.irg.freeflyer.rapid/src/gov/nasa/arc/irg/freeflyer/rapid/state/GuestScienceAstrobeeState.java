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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import gov.nasa.arc.irg.plan.freeflyer.config.ZonesConfig;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.rapid.v2.e4.message.MessageType;
import rapid.ext.astrobee.CompressedFile;

public class GuestScienceAstrobeeState extends AggregateAstrobeeState {
	protected ModuleBayPlan plan; // AggregateAstrobeeState doesn't track this
	protected MessageType[] sampleType;
	protected ZonesConfig zonesConfig;
	private static GuestScienceAstrobeeState INSTANCE;
	
	public static AggregateAstrobeeState getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new GuestScienceAstrobeeState();
		}
		return INSTANCE;
	}
	
	public GuestScienceAstrobeeState() {
		super();
	}
	
	@Override
	public ModuleBayPlan ingestCurrentPlanCompressedFile(CompressedFile compressedFile) {
		plan = super.ingestCurrentPlanCompressedFile(compressedFile);
		return plan;
	}
	
	public ModuleBayPlan getPlan() {
		return plan;
	}
	
	public String getCurrenPlanStepString() {
		if(plan != null && currentPointCommand != null) {
			Sequenceable seq = plan.getSequenceableByRapidNumber(currentPointCommand.point);
			if(seq == null) {
				return "Out of Range";
			}

			if(currentPointCommand.command < 0) {
				if(seq instanceof Station) {
					return seq.getName() + " Station";
				}
				if(seq instanceof Segment) {
					return seq.getName() + " Segment";
				}
			} else {
				if(seq instanceof SequenceHolder) {
					SequenceHolder seqHolder = (SequenceHolder)seq;
					try {
						Sequenceable cmd = seqHolder.getSequenceable(currentPointCommand.command);
						return cmd.getName();
					} catch(IndexOutOfBoundsException e) {
						System.out.println(seqHolder.getName() + " doesn't have cmd #" + currentPointCommand.command);
						return "Unknown Command #" + currentPointCommand.command;
					}
					
				}
			}
		}
		return uninitialized;
	}
}
