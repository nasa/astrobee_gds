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
package gov.nasa.arc.verve.robot.rapid.scenegraph.plan;

import gov.nasa.arc.verve.robot.parts.concepts.plan.AbstractPlanOverview;
import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.parts.concepts.plan.Task;
import gov.nasa.arc.verve.robot.parts.concepts.plan.TaskCollection;
import gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks.RapidCmdSegment;
import gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks.RapidCmdStation;

import org.apache.log4j.Logger;

import com.ardor3d.math.type.ReadOnlyTransform;

public class RapidCmdTaskOverview extends AbstractPlanOverview {
    private static final Logger logger = Logger.getLogger(RapidCmdTaskOverview.class);

    final double stationThreshold = 1;

    public RapidCmdTaskOverview(String planName) {
        super(planName, new RapidCmdTaskFactory());
    }

    @Override
    public void compilePlan(PlanState planState) {
        clearTaskCollectionCache();
        //int segCount = 0;
        //int stnCount = 0;
        try { //tasks
            TaskCollection tc = null;
            //logger.debug("-- Compile Plan ------------------------");
            for(Task task : m_taskList) {
                final ReadOnlyTransform begXfm = task.getBeginningTransform();
                //final ReadOnlyTransform endXfm = task.getEndTransform();
                //final ReadOnlyVector3 beg = begXfm.getTranslation();
                //final ReadOnlyVector3 end = endXfm.getTranslation();
                //double drive = beg.distance(end);
                // if task has a parentKey (station name)
                if(task.getParentKey() != null && task.getParentKey() instanceof String) {
                    String stationName = (String)task.getParentKey();
                    if(stationName.contains("SEG")) {
                        tc = getTaskCollection(stationName);
                        if(tc == null) {
                            tc = new RapidCmdSegment(stationName);
                            addTaskCollection(tc.getName(), tc);
                            tc.setTranslation(begXfm.getTranslation());
                        }
                        tc.addTask(task);
                        tc = null;
                    }
                    else {
                        tc = getTaskCollection(stationName);
                        if(tc == null) {
                            tc = new RapidCmdStation(stationName);
                            addTaskCollection(tc.getName(), tc);
                            tc.setTranslation(begXfm.getTranslation());
                        }
                        tc.addTask(task);
                    }
                }
                else {
                    tc = getTaskCollection("");
                    if(tc == null) {
                        tc = new RapidCmdStation("");
                        addTaskCollection(tc.getName(), tc);
                    }
                    tc.addTask(task);
                }
//                else {
//                    //logger.warn("Task has no parentKey: "+task.getName());
//                    if(drive > stationThreshold) { // segment
//                        String name = "Segment"+segCount++;
//                        tc = new RapidCmdSegment(name);
//                        addTaskCollection(tc.getName(), tc);
//                        tc.setTranslation(begXfm.getTranslation());
//                        tc.addTask(task);
//                        tc = null;
//                    }
//                    else {
//                        if(tc == null) {
//                            String name = "Station"+stnCount++;
//                            tc = new RapidCmdStation(name);
//                            addTaskCollection(tc.getName(), tc);
//                            tc.setTranslation(begXfm.getTranslation());
//                        }
//                        tc.addTask(task);
//                    }
//                }
            }
        }
        catch(Throwable t) {
            logger.warn("Exception compiling plan", t);
        }
        for(TaskCollection tc : m_tcList) {
            //logger.debug("  "+tc.getName()+" - "+tc.getClass().getSimpleName());
            tc.compile(planState);
        }
    }
}
