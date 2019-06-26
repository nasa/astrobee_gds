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
package gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks;

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.ardor3d.scenegraph.extension.BillboardNode;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.parts.concepts.plan.Task;
import gov.nasa.arc.verve.robot.parts.concepts.plan.TaskCollection;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.DirectionalPath;
import gov.nasa.util.Colors;

import java.util.ArrayList;
import java.util.List;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;

public class RapidCmdStation extends TaskCollection {
    //private static final Logger logger = Logger.getLogger(RapidCmdStation.class);
    protected BMText                  m_text;
    protected BillboardNode           m_billboard;
    protected DirectionalPath         m_path;
    protected List<ReadOnlyVector3>   m_pathPoints = new ArrayList<ReadOnlyVector3>();
    protected List<ReadOnlyColorRGBA> m_pathColors = new ArrayList<ReadOnlyColorRGBA>();
    protected double                  m_zOff = -0.4;
 
    public RapidCmdStation(String name) {
        super(name, null, null, null);
        float textHeight = 1.25f;
        m_text = new BMText(name+"-text",
                            name,
                            BMFontManager.sansSmall(),
                            BMText.Align.South,
                            BMText.Justify.Center);
        Matrix3 rot = new Matrix3();
        rot.fromAngleNormalAxis(-Math.PI/2, Vector3.UNIT_X);
        m_text.setAutoRotate(false);
        m_text.setFixedOffset(0, 0);
        m_text.setRotation(rot);
        m_text.setTranslation(0, textHeight, textHeight);
        m_billboard = new BillboardNode("billboard");
        m_billboard.attachChild(m_text);
        m_node.attachChild(m_billboard);
        
        m_path = new DirectionalPath("path");
        m_path.setDefaultColor(Ardor3D.color(Colors.X11.LightGray));
        m_path.setUseColors(true);
    }

    @Override
    public void updateTask(Object data, PlanState planState) {
        // nothing
    }

    @Override
    public boolean canUpdate(Object data) {
        return false;
    }    

    public String getDisplayName() {
        String retVal = getName();
        int idx = retVal.indexOf("STN");
        if(idx >= 0) {
            try {
                String sNum = retVal.substring(idx+3, idx+5);
                int iNum = Integer.valueOf(sNum);
                retVal = Integer.toString(iNum);
            }
            catch(Throwable t) {
                // ignore
            }
            return retVal;
        }
        return "";
    }

    @Override
    public void compile(PlanState planState) {
        //logger.debug(this.getClass().getSimpleName()+" compile");
        //float zOff = -0.2f;
        int numTasks = m_tasks.size();
        int numNotComplete = 0;
        String text = getDisplayName();
        for(int i = 0; i < numTasks; i++) {
            Task task = m_tasks.get(i);
            if(task.getStatus().equals(TaskStatus.Executing)) {
                //text += "\n"+task.getName();
            }
            if(!task.getStatus().equals(TaskStatus.Completed)) {
                numNotComplete++;
            }
        }
        if(numNotComplete == 0) {
            //text = "";
        }
        m_text.setText(text);
        
        boolean hasPath = false;
        ReadOnlyVector3 pos = m_node.getTranslation();
        Vector3 vert = null;
        for(int i = 0; i < numTasks; i++) {
            Task task = m_tasks.get(i);
            ReadOnlyVector3 beg = task.getBeginningTransform().getTranslation();
            ReadOnlyVector3 end = task.getEndTransform().getTranslation();
            if(beg.distanceSquared(end) > 0.001) {
                hasPath = true;
                if(vert == null) {
                    vert = beg.subtract(pos, new Vector3());
                    vert.addLocal(0,0,m_zOff);
                    m_pathPoints.add(vert);
                    m_pathColors.add(task.getStatus().color);
                }
                vert = end.subtract(pos, new Vector3());
                vert.addLocal(0,0,m_zOff);
                m_pathPoints.add(vert);
                m_pathColors.add(task.getStatus().color);
           }
        }
        if(hasPath) {
            if(m_path.getParent() == null) {
                m_node.attachChild(m_path);
            }
            m_path.updateData(m_pathPoints, m_pathColors);
        }

    }



}
