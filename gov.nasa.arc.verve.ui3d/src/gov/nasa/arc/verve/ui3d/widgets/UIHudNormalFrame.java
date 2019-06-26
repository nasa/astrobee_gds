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

import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.input.InputState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class UIHudNormalFrame extends UIFrame implements IVerveCanvasListener {
    private static final Logger logger = Logger.getLogger(UIHudNormalFrame.class);
    double  lastWidth = 0;
    double  lastHeight = 0;
    Vector3 unclamped = new Vector3(Double.NaN, Double.NaN, Double.NaN);

    public UIHudNormalFrame(String title) {
        super(title, EnumSet.of(FrameButtons.CLOSE));
        //super(title, EnumSet.of(FrameButtons.CLOSE, FrameButtons.HELP, FrameButtons.MINIMIZE, FrameButtons.MAXIMIZE));
        //        setDecorated(false);
        //        setDraggable(false);
        //        setResizeable(false);
        //        getContentPanel().setBorder(new EmptyBorder());
        //        getContentPanel().setLayout(new RowLayout(false));
        //        getContentPanel().setBackdrop(null);
        //        getBasePanel().setBackdrop(null);
        //        setBackdrop(null);
        applySuperSkin();
    }

    @Override
    public boolean mouseClicked(final MouseButton button, final InputState state) {
        logger.debug("clicked");
        return super.mouseClicked(button,  state);
    }

    protected void applySuperSkin() {
        super.applySkin();
    }

    @Override
    protected void applySkin() {
        //
    }
    
    public void setCanvasSize(int width, int height) {
        lastWidth = width;
        lastHeight = height;
    }

    /**
     * update unclamped when we are dragged
     */
    @Override
    public void addTranslation(final double x, final double y, final double z) {
        super.addTranslation(x,y,z);
        ReadOnlyVector3 t = getTranslation();
        unclamped.set(t);
        // align to pixel for sharper text
        super.setTranslation(Math.floor(t.getX()),
                             Math.floor(t.getY()),
                             Math.floor(t.getZ()));
    }
    
    @Override
    public void canvasResized(int width, int height) {  
        if(lastWidth == 0 || lastHeight == 0) {
            int x = (width - this.getContentWidth()) / 2;
            int y = (height - this.getContentHeight()) / 2;
            this.setLocalXY(x, y);
        }
        else {
            if(!Vector3.isValid(unclamped))
                unclamped.set(getTranslation());
            double wr =  width/lastWidth;
            double hr = height/lastHeight;
            double cx = wr*unclamped.getX();
            double cy = hr*unclamped.getY();
            double cz = unclamped.getZ();
            unclamped.set(cx, cy, cz);
            if(cx < 0) cx = 0;
            if(cx > width-getContentWidth()) cx = width-getContentWidth();
            if(cy < 0) cy = 0;
            if(cy > height-getContentHeight()) cy = height-getContentHeight();
            // align to pixel for sharper text
            setTranslation(Math.floor(cx), 
                           Math.floor(cy), 
                           Math.floor(cz));
        }
        lastWidth = width;
        lastHeight = height;
        updateGeometricState(0);
    }
}
