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
package gov.nasa.arc.verve.robot.parts;

import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.exception.TelemetryException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.CullHint;

/**
 * Abstract implementation of IRobotPart
 */
public abstract class AbstractRobotPart implements IRobotPart {
    protected final PropertyChangeSupport m_propertyChangeSupport;
    protected final AbstractRobot m_parent;
    protected final String        m_name;
    private final AtomicBoolean   m_dirty = new AtomicBoolean(false);

    
    protected Node    m_node =     null;
    protected boolean m_isVisible = true;

    public AbstractRobotPart(String partName, AbstractRobot parent) {
        m_parent = parent;
        m_name = partName;
        m_propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public AbstractRobot getRobot() {
        return m_parent;
    }
    
    public Node getNode() {
        return m_node;
    }
    
    public String getPartName() {
        return m_name;
    }
    
    public abstract void    connectTelemetry() throws TelemetryException;
    public abstract void    disconnectTelemetry() throws TelemetryException;
    public boolean isTelemetryEnabled() {
        return getRobot().isTelemetryEnabled() && this.isVisible();
    }
    
    //-- scenegraph methods
    public abstract void attachToNodesIn(Node model) throws IllegalStateException;
    public abstract void handleFrameUpdate(long currentTime);
    public abstract void reset();
    /** call reset() in VerveTask.asyncExec() */
    public void resetAsync() {
        VerveTask.asyncExec(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                reset();
                return null;
            }
        });
    }
    
    public boolean isVisible() {
        return m_isVisible;
    }
    public void setVisible(boolean visible) {
        if(m_node != null) {
            m_isVisible = visible;
            if(visible) {
                //m_dirty.set(true); << test this
                m_node.getSceneHints().setCullHint(CullHint.Inherit);
            }
            else {
                m_node.getSceneHints().setCullHint(CullHint.Always);
            }
            setDirty(true);
        }
    }
    
    public void setDirty(boolean state) {
    	if(state) {
    		m_node.markDirty(DirtyType.Attached);
    	}
    	m_dirty.set(state);
    }
    public boolean isDirty() {
    	return m_dirty.get();
    }
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        m_propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        m_propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        m_propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        m_propertyChangeSupport.removePropertyChangeListener(propertyName,
                listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

}
