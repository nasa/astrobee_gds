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
package gov.nasa.arc.verve.ui3d.hud.animation;

import gov.nasa.arc.verve.ui3d.hud.VerveNotifyHud;
import gov.nasa.arc.verve.ui3d.widgets.UINotifyFrame;

public class NotifyExpired implements INotifyAnimation {
    //private static final Logger logger = Logger.getLogger(NotifyExpired.class);

    final VerveNotifyHud hud;
    final UINotifyFrame  frame;

    public NotifyExpired(VerveNotifyHud hud, UINotifyFrame frame) {
        this.hud = hud; 
        this.frame = frame;
    }

    @Override
    public UINotifyFrame getNoticeFrame() {
        return frame;
    }

    @Override
    public void updateAnimation(long currentTime, double timeSinceLastFrame) {   
        if(frame.isExpired()) {
            int move = 25;
            int x = frame.getHudX();
            if(x+frame.getLocalComponentWidth() > 0) {
                frame.setHudX(x-move);
            }
            else {
                //logger.debug("remove Animation:"+frame.getTitle());
                hud.removeAnimation(this);
                frame.close();
                //hud.remove(frame);
            }
        }
        else {
            hud.removeAnimation(this);
        }
    }
}
