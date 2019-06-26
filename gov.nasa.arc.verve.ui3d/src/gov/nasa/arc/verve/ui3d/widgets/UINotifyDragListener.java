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

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.extension.ui.event.FrameDragListener;

public class UINotifyDragListener extends FrameDragListener {

    public UINotifyDragListener(UIFrame uiFrame) {
        super(uiFrame);
    }

    @Override
    public void startDrag(final int mouseX, final int mouseY) {
        final UINotifyFrame frame = (UINotifyFrame)uiFrame;
        if(!frame.isExpired()) {
            super.startDrag(mouseX, mouseY);
            frame.startedDrag();
        }
    }

    @Override
    public void drag(final int mouseX, final int mouseY) {
        final UINotifyFrame frame = (UINotifyFrame)uiFrame;
        if(!frame.isExpired()) {
            super.drag(mouseX, mouseY);
            frame.dragged();
        }
    }

    @Override
    public void endDrag(final UIComponent component, final int mouseX, final int mouseY) {
        final UINotifyFrame frame = (UINotifyFrame)uiFrame;
        if(!frame.isExpired()) {
            super.endDrag(component, mouseX, mouseY);
            frame.endedDrag();
        }
    }

}
