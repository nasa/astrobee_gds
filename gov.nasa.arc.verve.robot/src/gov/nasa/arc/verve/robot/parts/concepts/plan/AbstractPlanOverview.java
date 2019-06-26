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
package gov.nasa.arc.verve.robot.parts.concepts.plan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.ardor3d.scenegraph.Node;

public abstract class AbstractPlanOverview {

    protected String m_planName = "unnamedPlan";
    protected HashMap<Object,Task>                 m_taskCache = new HashMap<Object,Task>();
    protected List<Task>                           m_taskList  = new LinkedList<Task>();
    protected HashMap<Object,TaskCollection>       m_tcCache   = new HashMap<Object,TaskCollection>();
    protected List<TaskCollection>                 m_tcList    = new LinkedList<TaskCollection>();

    protected final IPlanTaskFactory  m_taskFactory;
    protected double                  m_stationTolerance = 1;

    protected final Node m_node;

    public enum Verbosity {
        Quiet,
        Normal,
        Loud
    }
    protected Verbosity m_verbosity;

    public AbstractPlanOverview(String planName, IPlanTaskFactory taskFactory) {
        m_planName = planName;
        m_taskFactory = taskFactory;
        m_node = new Node(planName);
    }

    public void setPlanName(String name) {
        m_planName = name;
    }
    public String getPlanName() {
        return m_planName;
    }

    public Node getNode() {
        return m_node;
    }

    /**
     * assign Tasks to TaskCollections
     */
    public abstract void compilePlan(PlanState planState);

    public Task getTask(Object key) {
        return m_taskCache.get(key);
    }

    public TaskCollection getTaskCollection(Object key) {
        return m_tcCache.get(key);
    }

    public TaskCollection addTaskCollection(Object key, TaskCollection tc) {
        m_tcCache.put(key, tc);
        m_tcList.add(tc);
        m_node.attachChild(tc.getNode());
        return tc;
    }
    
    public void clearTaskCollectionList() {
        clearTaskList();
        m_node.detachAllChildren();
        m_tcList.clear();
    }
    
    public void clearTaskCollectionCache() {
        for(TaskCollection tc : m_tcCache.values()) {
            tc.clearTasks();
        }
        m_tcCache.clear();
        m_tcList.clear();
        m_node.detachAllChildren();
    }
    
    public void clearTaskCache() {
        for(TaskCollection tc : m_tcCache.values()) {
            tc.clearTasks();
        }
        for(Task task : m_taskCache.values()) {
            task.setParent(null);
            task.getNode().removeFromParent();
        }
        m_taskCache.clear();
        m_taskList.clear();
    }

    public void clearTaskList() {
        for(Task task : m_taskList) {
            task.setParent(null);
            task.getNode().removeFromParent();
        }
        m_taskList.clear();
    }

    /**
     * 
     */
    public Task addTask(Object key, Object taskData, PlanState planState, Object parentKey) {
        Task task = m_taskCache.get(key);
        if(task == null) {
            task = m_taskFactory.createTask(taskData, planState, m_verbosity, parentKey);
            m_taskCache.put(key, task);
        }
        else {
            task.init();
            task.updateTask(taskData, planState);
        }
        m_taskList.add(task);
        return task;
    }

    public void updateTask(Object key, Object taskData, PlanState planState) {
        Task task = m_taskCache.get(key);
        if(task == null) {
            throw new IllegalStateException("No task with key ["+key.toString()+"] exists. Tasks must be added before they can be updated");
        }
        else if(!task.canUpdate(taskData)) {
            throw new IllegalStateException("Task "+task.getName()+" cannot be updated with "+taskData);
        }
        task.updateTask(taskData, planState);
    }

    public void setVerbosity(Verbosity verbosity) {
        m_verbosity = verbosity;
    }
    public Verbosity getVerbosity() {
        return m_verbosity;
    }

}
