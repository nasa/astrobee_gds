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
package gov.nasa.arc.verve.ui3d.widgets;

import com.ardor3d.extension.ui.AbstractLabelUIComponent;
import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIState;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;

public class IconState extends UIState {
    public static final ReadOnlyColorRGBA DEFAULT_TINT = new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f);
    public static final ReadOnlyColorRGBA ACTIVE_TINT  = ColorRGBA.WHITE;
    public static final ReadOnlyColorRGBA PRESSED_TINT = new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f);
    
    protected int m_posOffset = 0;
    protected int m_sizeOffset = 0;
    protected ReadOnlyColorRGBA m_tint = DEFAULT_TINT;
    
    public IconState() {
        //
    }
    
    public IconState(int posOffset, int sizeOffset, ReadOnlyColorRGBA tint) {
        m_posOffset  = posOffset;
        m_sizeOffset = sizeOffset;
        m_tint = tint;
    }
    
    public IconState(int posOffset, int sizeOffset) {
        m_posOffset  = posOffset;
        m_sizeOffset = sizeOffset;
    }
    
    public ReadOnlyColorRGBA getTint() {
        return m_tint;
    }
    
    public int getPosisionOffset() {
        return m_posOffset;
    }

    public void setPositionOffset(int offset) {
        m_posOffset = offset;
    }

    public int getSizeOffset() {
        return m_sizeOffset;
    }

    public void setSizeOffset(int offset) {
        m_sizeOffset = offset;
    }

    @Override
    public void setupAppearance(final UIComponent component) {
        super.setupAppearance(component);
        if (component instanceof AbstractLabelUIComponent) {
            //final UIIconButton iconButton = (UIIconButton)component;
        }
    }

}
