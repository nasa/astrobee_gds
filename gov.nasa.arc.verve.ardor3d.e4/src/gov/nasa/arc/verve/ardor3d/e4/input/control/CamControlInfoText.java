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
package gov.nasa.arc.verve.ardor3d.e4.input.control;

import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.Text2D;

import com.ardor3d.math.Matrix3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.controller.SpatialController;
import com.ardor3d.scenegraph.hint.CullHint;


public class CamControlInfoText extends Node {

    protected final IVerveCanvasView m_canvasView;
    protected Text2D  m_text;
    private   String  m_thisString = "Camera";

    public CamControlInfoText(IVerveCanvasView canvasView) {
        m_canvasView = canvasView;
        m_text = new Text2D("CameraInfo", m_thisString, BMFontManager.sansSmall());
        m_text.getSceneHints().setCullHint(CullHint.Never);
        addController(new CameraControlInfoController());
        attachChild(m_text);
        CullState cs = new CullState();
        cs.setCullFace(CullState.Face.None);
        setRenderState(cs);

        Matrix3 rot = new Matrix3( 
                1, 0, 0, 
                0, 0, 1, 
                0,-1, 0);
        m_text.setRotation(rot);
        m_text.setTextColor( 1, 1, 1, 0.35f);
    }
    
    public synchronized void setCameraString(String text) {
        m_thisString = text;
    }
    public synchronized String getCameraString() {
        return m_thisString;
    }

    public class CameraControlInfoController implements SpatialController<CamControlInfoText> {
        public int    m_x = 0;
        public int    m_y = 0;
        public String m_lastString = "";
        @Override
        public void update(double time, CamControlInfoText caller) {
            AbstractCamControl acc = m_canvasView.getCameraControl();
            Camera cam = acc.getCamera();
            int x = cam.getWidth() - 159;
            int y = cam.getHeight() - m_text.getFont().getLineHeight();
            if(!(y == m_y && x == m_x) ) {
                m_text.setTranslation(x, y, 0);
                m_x = x;
                m_y = y;
            }
            
            String string = getCameraString();
            // do a reference comparison for speed's sake
            if(m_lastString != string) {
                m_text.setText(string);
                m_lastString = string;
            }
        }
    }
}
