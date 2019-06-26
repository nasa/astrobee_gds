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
import com.ardor3d.math.type.ReadOnlyVector3;

public class NotifyLinearSlide implements INotifyAnimation {
    final VerveNotifyHud hud;
    final UINotifyFrame  frame;
    final Vector3        to   = new Vector3();
    final Vector3        from = new Vector3();
    final Vector3        now  = new Vector3();

    final double targetSpeed;
    double current = 0;


    public NotifyLinearSlide(VerveNotifyHud hud, UINotifyFrame frame, ReadOnlyVector3 targetLoc) {
        this(hud,  frame, targetLoc, 1.2);
    }
    
    public NotifyLinearSlide(VerveNotifyHud hud, UINotifyFrame frame, ReadOnlyVector3 targetLoc, double targetSpeed) {
        this.hud = hud; 
        this.frame = frame;
        this.to.set(targetLoc);
        this.from.set(frame.getHudX(), frame.getHudY(), 0);
        this.targetSpeed = targetSpeed;
    }

    @Override
    public UINotifyFrame getNoticeFrame() {
        return frame;
    }

    @Override
    public void updateAnimation(long currentTime, double timeSinceLastFrame) {
        if(frame.isPositionManaged()) {
            current += targetSpeed*timeSinceLastFrame;
            if(current > 1)
                current = 1;
            from.lerp(to, current, now);
            frame.setHudXY((int)Math.rint(now.getX()), 
                           (int)Math.rint(now.getY()));
        }
        if(current >= 1) {
            hud.removeAnimation(this);
        }
    }
}
