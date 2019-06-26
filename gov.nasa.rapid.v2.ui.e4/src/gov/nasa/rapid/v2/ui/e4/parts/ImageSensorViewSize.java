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
package gov.nasa.rapid.v2.ui.e4.parts;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class ImageSensorViewSize {
    public int width;
    public int height;
    public void set(int w, int h) {
        width = w;
        height = h;
    }
    
    public void createSizeListener(final IImageReshower imageView, final Composite composite) {
        final ImageSensorViewSize viewAreaSize = this;
        composite.addControlListener(new ControlListener() {
            public void controlMoved(ControlEvent e) {
                // ignore
            }
            public void controlResized(ControlEvent e) {
                Rectangle rect = composite.getClientArea();
                synchronized(viewAreaSize) {
                    viewAreaSize.set(rect.width, rect.height);
                    imageView.reshowImage();
                }
            }
        });
    }
}
