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

import gov.nasa.arc.verve.common.ardor3d.framework.IVerveCanvasListener;

import java.util.EnumSet;

import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.extension.ui.backdrop.EmptyBackdrop;
import com.ardor3d.extension.ui.border.EmptyBorder;
import com.ardor3d.extension.ui.layout.RowLayout;

public class UIHudFixedFrame extends UIFrame implements IVerveCanvasListener {

    public UIHudFixedFrame(String title) {
        super(title, EnumSet.noneOf(FrameButtons.class));
        setDecorated(false);
        setDraggable(false);
        setResizeable(false);
        getContentPanel().setBorder(new EmptyBorder());
        getContentPanel().setLayout(new RowLayout(true));
        EmptyBackdrop backdrop = new EmptyBackdrop();
        getContentPanel().setBackdrop(backdrop);
        getBasePanel().setBackdrop(backdrop);
        setBackdrop(backdrop);
        applySuperSkin();
    }

    protected void applySuperSkin() {
        super.applySkin();
    }

    @Override
    protected void applySkin() {
        //
    }

    @Override
    public void canvasResized(int width, int height) {  
        //
    }
}
