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

import java.util.ArrayList;
import java.util.Collection;

import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

public abstract class TaskCollection extends Task {
    //private static final Logger logger = Logger.getLogger(TaskCollection.class);
    
    protected final ArrayList<Task> m_tasks  = new ArrayList<Task>();
    protected final Transform       m_invXfm = new Transform();
    protected final Transform       t_xfm    = new Transform();
    
    public TaskCollection(String taskName, Object data, PlanState planState, Object parentKey) {
        super(taskName, data, planState, parentKey);
    }

    public abstract void compile(PlanState planState);

    /** to be treated as read-only */
    public ArrayList<Task> getTasks() {
        return m_tasks;
    }
    
    /** add a single task */
    public void addTask(Task task) {
        task.setParent(this);
        m_invXfm.multiply(task.getNode().getTransform(), t_xfm);
        //task.getNode().getTransform().multiply(m_invXfm, t_xfm);
        task.getNode().setTransform(t_xfm);
        m_node.attachChild(task.getNode());
        m_tasks.add(task);
    }
    
    /** clear current tasks and add new set */
    public void setTasks(Collection<Task> tasks) {
        clearTasks();
        for(Task task : m_tasks) {
            addTask(task);
        }
    }
    
    /** clear all tasks */
    public void clearTasks() {
        for(Task task : m_tasks) {
            task.setParent(null);
            m_node.detachChild(task.getNode());
        }
        m_tasks.clear();
    }    

    @Override
    public void setBeginningXfm(ReadOnlyTransform loc) {
        m_begXfm.set(loc);
        m_begXfm.invert(m_invXfm);
    }
    
    public void setTranslation(ReadOnlyVector3 xyz) {
        m_begXfm.setTranslation(xyz);
        m_begXfm.invert(m_invXfm);
        m_node.setTransform(m_begXfm);
    }
    
}
