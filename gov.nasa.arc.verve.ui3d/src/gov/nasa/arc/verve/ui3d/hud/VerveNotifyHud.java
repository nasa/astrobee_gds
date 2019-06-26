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

import gov.nasa.arc.verve.common.ardor3d.framework.IVerveCanvasListener;
import gov.nasa.arc.verve.common.ardor3d.framework.IVerveCanvasView;
import gov.nasa.arc.verve.ui3d.hud.animation.INotifyAnimation;
import gov.nasa.arc.verve.ui3d.hud.animation.NotifyExpired;
import gov.nasa.arc.verve.ui3d.hud.animation.NotifyLinearSlide;
import gov.nasa.arc.verve.ui3d.notify.IconTex;
import gov.nasa.arc.verve.ui3d.widgets.UIIconButton;
import gov.nasa.arc.verve.ui3d.widgets.UINotifyFrame;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.SpatialController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * HUD that manages Notices
 * @author mallan
 */
public class VerveNotifyHud extends VerveHud implements IVerveCanvasListener {    
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(VerveNotifyHud.class);

    protected final List<UINotifyFrame> m_activeNotices = new LinkedList<UINotifyFrame>();
    protected Comparator<UINotifyFrame> m_notifyComparator = new NotifyComparator();

    Random random = new Random(System.currentTimeMillis());
    //protected final HashMap<UINoticeFrame,Integer> m_expiredNotices = Maps.newHashMap();
    //LinkedList<UINoticeFrame>                      m_expiredRemove  = Lists.newLinkedList();

    protected final HashMap<UINotifyFrame,INotifyAnimation> m_animations = Maps.newHashMap();
    protected final LinkedList<INotifyAnimation>            m_animRemove = Lists.newLinkedList();
    protected final LinkedList<INotifyAnimation>            m_animAddNew = Lists.newLinkedList();

    final boolean hasMinMaxPanel;
    
    final int EXPAND  = 0;
    final int COMPACT = 1;
    protected UIIconButton[] m_buttons = new UIIconButton[2];

    public VerveNotifyHud(String hudId, IVerveCanvasView canvasView, boolean showMinMaxPanel) {
        super(hudId, canvasView);
        canvasView.addListener(this);
        this.addController(new UpdateController());
        hasMinMaxPanel = showMinMaxPanel;
        if(showMinMaxPanel) {
            createMinimizeMaximizePanel();
        }
    }

    @Override
    public void add(UIComponent component) {
        if(component instanceof UINotifyFrame) {
            UINotifyFrame frame = (UINotifyFrame)component;
            boolean existed;
            // clear existing
            existed = m_activeNotices.remove(frame);
            m_animations.remove(frame);
            // put new
            m_activeNotices.add(0, frame);
            Collections.sort(m_activeNotices, m_notifyComparator); 
            int top = getHeight();
            frame.setExpired(false);
            if(!existed) {
                frame.setHudXY(0, top);
            }
            frame.updateWorldTransform(false);
        }
        super.add(component);
        layoutNotices();
        
        if(hasMinMaxPanel && getParent() != null) {
            bringToFront(m_buttons[EXPAND]);
            bringToFront(m_buttons[COMPACT]);
        }
    }

    public void expire(UINotifyFrame frame) {
        //logger.debug("expire "+frame.getTitle());
        if(m_activeNotices.contains(frame)) {
            m_activeNotices.remove(frame);
            frame.setExpired(true);
            addAnimation(new NotifyExpired(this, frame));
            layoutNotices();
        }
    }

    @Override
    public void remove(UIComponent component) {
        if(component instanceof UINotifyFrame) {
            UINotifyFrame frame = (UINotifyFrame)component;
            m_activeNotices.remove(frame);
            layoutNotices();
            super.remove(component);
        }
        else {
            super.remove(component);
        }
    }

    public void addAnimation(INotifyAnimation animation) {
        m_animAddNew.add(animation);
    }

    /**
     * remove animation before next animation update
     * @param animation
     * @return
     */
    public void removeAnimation(INotifyAnimation animation) {
        m_animRemove.add(animation);
    }

    public boolean removeActiveNotice(UINotifyFrame frame) {
        return m_activeNotices.remove(frame);
    }

    @Override
    public void canvasResized(int width, int height) {
        layoutNotices();
    }

    protected void addNewAnimations() {
        for(INotifyAnimation animation : m_animAddNew) {
            m_animations.put(animation.getNoticeFrame(), animation);
        }
        m_animAddNew.clear();
    }
    protected void removeDeadAnimations() {
        for(INotifyAnimation anim : m_animRemove) {
            m_animations.remove(anim.getNoticeFrame());
        }
        m_animRemove.clear();
    }

    int layoutCount = 0;
    public void layoutNotices() {
        layoutNotices(1.2);
    }
    public void layoutNoticesFast() {
        layoutNotices(10);
    }

    public void layoutNotices(double speed) {
        int top = getHeight();
        //int i = 0;
        //long lastTime = 0;
        //logger.debug("------------------");
        for(UINotifyFrame frame : m_activeNotices) {
            //logger.debug("frame "+ i++ +" : "+(lastTime < frame.getTimestamp())+" "+frame.getTimestamp());
            //lastTime = frame.getTimestamp();
            if(frame.isPositionManaged()) {
                int fh = frame.getLocalComponentHeight();
                top = top-fh;
                //addAnimation(new NoticeExponentialSlide(this, frame, new Vector3(0, top, 0)));
                addAnimation(new NotifyLinearSlide(this, frame, new Vector3(0, top, 0), speed));
            }
        }
    }

    protected void updateAnimations(double timeSinceLastFrame) {
        removeDeadAnimations();
        addNewAnimations();
        long currentTime = System.currentTimeMillis();
        for(INotifyAnimation anim : m_animations.values()) {
            anim.updateAnimation(currentTime, timeSinceLastFrame);
        }
        UINotifyFrame[] activeNotices = new UINotifyFrame[m_activeNotices.size()];
        m_activeNotices.toArray(activeNotices);
        for(UINotifyFrame frame : activeNotices) {
            if(frame.isPositionManaged()) {
                frame.updateTimeout(timeSinceLastFrame);
            }
        }
    }


    public class UpdateController implements SpatialController {
        double accumTime = 0;
        @Override
        public void update(double time, Spatial caller) {
            updateAnimations(time);
            accumTime += time;
            if(accumTime > 10) {
                accumTime = 0;
                int width = m_canvasView.getCanvasWidth();
                int fontSize = fontSizeForWidth(width);
                UIComponent.setDefaultFontSize(fontSize);
            }
        }
    }

    int fontSizeForWidth(int width) {
        if(width < 800)       return 12;
        else if(width < 1200) return 14;
        else                  return 16;
    }

    public class NotifyComparator implements Comparator<UINotifyFrame> {
        @Override
        public int compare(UINotifyFrame o1, UINotifyFrame o2) {
            final int o1Level = o1.getSortLevel().ordinal();
            final int o2Level = o2.getSortLevel().ordinal();
            if(o1Level == o2Level) {
                return (int)(o2.getSortTimestamp() - o1.getSortTimestamp());
            }
            else {
                return o1Level - o2Level;
            }
        }
    }

    public void createMinimizeMaximizePanel() {
        UIIconButton button;
        m_buttons[EXPAND] = new UIIconButton(IconTex.Icon.Plus.subtex);
        button = m_buttons[EXPAND];
        button.setDimensions(16, 16);
        this.add(button);
        button.setHudXY(5, 5);
        button.setTooltipText(" expand notice windows ");
        button.setOnLeftClickRunnable(new Runnable() {
            @Override
            public void run() {
                setAllMinimized(false);
            }
        });

        m_buttons[COMPACT] = new UIIconButton(IconTex.Icon.Minus.subtex);
        button = m_buttons[COMPACT];
        button.setDimensions(16, 16);
        this.add(button);
        button.setHudXY(25, 5);
        button.setTooltipText(" compact notice windows ");
        button.setOnLeftClickRunnable(new Runnable() {
            @Override
            public void run() {
                setAllMinimized(true);
            }
        });
    }

    public void setAllMinimized(boolean state) {
        UINotifyFrame.MINIMIZED_DEFAULT = state;
        for(UINotifyFrame frame : m_activeNotices) {
            frame.setMinimized(state);
        }
    }
}
