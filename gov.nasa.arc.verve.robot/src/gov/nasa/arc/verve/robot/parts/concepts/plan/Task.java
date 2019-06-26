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

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.robot.parts.concepts.plan.AbstractPlanOverview.Verbosity;
import gov.nasa.util.Colors;

import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.Node;

public abstract class Task {

    public enum TaskStatus {
        Pending  (Ardor3D.color(Colors.X11.Gray80)),
        Executing(Ardor3D.color(Colors.X11.GreenYellow)),
        Paused   (Ardor3D.color(Colors.X11.CornflowerBlue)),
        Completed(Ardor3D.color(Colors.X11.Gray20)),
        Aborted  (Ardor3D.color(Colors.X11.Indigo)),
        Failed   (Ardor3D.color(Colors.X11.IndianRed)),
        Unknown  (Ardor3D.color(Colors.X11.BurlyWood))
        ;
        
        public final ReadOnlyColorRGBA color;
        TaskStatus(ReadOnlyColorRGBA color) {
            this.color = color;
        }
    }
    
    protected String         m_taskName     = "task";
    protected Transform      m_begXfm       = new Transform();
    protected Transform      m_endXfm       = new Transform();
    protected TaskStatus     m_taskStatus   = TaskStatus.Unknown;
    protected Verbosity      m_verbosity    = Verbosity.Normal;
    protected TaskCollection m_parent       = null;
    protected Object         m_parentKey    = null;
    protected final Node     m_node;
    
    public Task(String taskName, Object data, PlanState planState, Object parentKey) {
        m_taskName = taskName;
        m_node = new Node(taskName);
        m_parentKey = parentKey;
    }
    
    public void init() {
        m_begXfm.setIdentity();
        m_endXfm.setIdentity();
        m_taskStatus = TaskStatus.Unknown;
        m_node.setTransform(Transform.IDENTITY);
    }
    
    /** generic update member - to be implemented in subclasses */
    public abstract void    updateTask(Object data, PlanState planState);
    /** test whether the task data is compatible with this visualization */
    public abstract boolean canUpdate(Object data);
    
    /** get graphical representation of this task */ 
    public Node getNode() {
        return m_node;
    }
    
    public void setName(String name) {
        m_node.setName(name);
        m_taskName = name;
    }
    public String getName() {
        return m_taskName;
    }
    
    public void setBeginningXfm(ReadOnlyTransform loc) {
        m_begXfm.set(loc);
    }
    public ReadOnlyTransform getBeginningTransform() {
        return m_begXfm;
    }
    
    public void setEndXfm(ReadOnlyTransform loc) {
        m_endXfm.set(loc);
    }
    public ReadOnlyTransform getEndTransform() {
        return m_endXfm;
    }
    
    public void setParent(TaskCollection parent) {
        m_parent = parent;
    }
    public TaskCollection getParent() {
        return m_parent;
    }
    
    public Object getParentKey() {
        return m_parentKey;
    }
    
    public void setVerbosity(Verbosity verbosity) {
        m_verbosity = verbosity;
    }
    public Verbosity getVerbosity() {
        return m_verbosity;
    }
    
    public void setStatus(TaskStatus status) {
        m_taskStatus = status;
    }
    public TaskStatus getStatus() {
        return m_taskStatus;
    }
}
