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

import com.ardor3d.math.Vector3;

public class NotifyExponentialSlide implements INotifyAnimation {
    //private static final Logger logger = Logger.getLogger(NoticeExponentialSlide.class);
    
    final VerveNotifyHud hud;
    final UINotifyFrame  frame;
    final Vector3        targetLoc;
    
    public static double targetSpeed    = 0.1;
    public static int    stopThresh     = 1; // distance squared stop distance
    
    final Vector3 from = new Vector3();
    final Vector3 to   = new Vector3();
    final Vector3 now  = new Vector3();

    public NotifyExponentialSlide(VerveNotifyHud hud, UINotifyFrame frame, Vector3 targetLoc) {
        this.hud = hud; 
        this.frame = frame;
        this.targetLoc = targetLoc;
        this.from.set(frame.getHudX(), frame.getHudY(), 0);
    }

    @Override
    public UINotifyFrame getNoticeFrame() {
        return frame;
    }

    @Override
    public void updateAnimation(long currentTime, double timeSinceLastFrame) {        
        double dist = stopThresh;
        if(frame.isPositionManaged()) {
            //from.set(frame.getHudX(), frame.getHudY(), 0);
            final Vector3 toLoc = targetLoc;
            if(toLoc != null ) {
                to.set(toLoc);
                dist = from.distanceSquared(to);
                from.lerp(to, targetSpeed, now);
                frame.setHudXY((int)Math.rint(now.getX()), 
                               (int)Math.rint(now.getY()));
                from.set(now);
            }
        }
        if(dist < stopThresh) {
            frame.setHudXY((int)Math.rint(to.getX()), 
                           (int)Math.rint(to.getY()));
            hud.removeAnimation(this);
        }
    }

//    @Override
//    public void finalize() {
//        logger.debug("finalize");
//    }

}
