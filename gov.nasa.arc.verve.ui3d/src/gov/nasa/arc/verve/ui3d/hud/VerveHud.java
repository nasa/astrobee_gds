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
package gov.nasa.arc.verve.ui3d.hud;

import gov.nasa.arc.verve.common.ardor3d.framework.IVerveCanvasView;

import com.ardor3d.extension.ui.UIHud;

/**
 * Doesn't do much of anything right now
 * It's here for future extension
 * @author mallan
 */
public class VerveHud extends UIHud {    
    protected final IVerveCanvasView m_canvasView;

    public VerveHud(String hudId, IVerveCanvasView canvasView) {
        super();
        m_canvasView = canvasView;
        setName(hudId);
        getTooltip().getLabel().setStyledText(true);
        VerveHudRegistry.add(hudId, this);
    }

    public IVerveCanvasView getCanvasView() {
        return m_canvasView;
    }
    
}
