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

import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.ui3d.hud.VerveNotifyHud;
import gov.nasa.arc.verve.ui3d.notify.SaliencyLevel;
import gov.nasa.arc.verve.ui3d.task.LayoutNoticesTask;

import java.util.EnumSet;

import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.UIButton;
import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.extension.ui.backdrop.GradientBackdrop;
import com.ardor3d.extension.ui.backdrop.SolidBackdrop;
import com.ardor3d.extension.ui.border.EmptyBorder;
import com.ardor3d.extension.ui.layout.RowLayout;
import com.ardor3d.extension.ui.util.Dimension;
import com.ardor3d.extension.ui.util.Insets;
import com.ardor3d.input.InputState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;

public class UINotifyFrame extends UIFrame  {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(UINotifyFrame.class);
    protected boolean           m_isPositionManaged = true;
    protected String            m_titleString;
    protected SolidBackdrop     m_titleBackdrop;
    protected double            m_timeoutDbl = -1;
    protected int               m_timeoutInt = -1;
    protected boolean           m_expired    = false;
    
    public static boolean       MINIMIZED_DEFAULT = false;
    protected boolean           m_minimized       = MINIMIZED_DEFAULT;

    protected final String      m_itemId;
    protected String            m_noticeId;
    protected String            m_context;
    protected long              m_timestamp;
    protected long              m_sortTimestamp;
    private SaliencyLevel       m_currentLevel  = SaliencyLevel.LogNotice;
    private SaliencyLevel       m_sortLevel     = SaliencyLevel.LogNotice;
    

    private static int          titleSize;
    private static int          timeSize;
    public static final ColorRGBA DARK_GRAY = new ColorRGBA(0.2f, 0.2f, 0.2f, 0.8f);
    public static final ColorRGBA BLACK     = new ColorRGBA(0.0f, 0.0f, 0.0f, 0.8f);
    
    public static GradientBackdrop backdrop = new GradientBackdrop(DARK_GRAY, DARK_GRAY,
                                                                    BLACK, BLACK);
    public UINotifyFrame(String itemId, long timestamp, EnumSet<FrameButtons> buttons) {
        super(itemId, buttons); //EnumSet.of(FrameButtons.CLOSE));
        m_itemId = itemId;
        m_timestamp = timestamp;
        m_titleString = itemId;
        titleSize = UIComponent.getDefaultFontSize()+0;
        timeSize  = UIComponent.getDefaultFontSize()-2;
        setResizeable(false);
        getContentPanel().setBorder(new EmptyBorder());
        getContentPanel().setLayout(new RowLayout(false));
        getContentPanel().setBackdrop(null);
        getBasePanel().setBackdrop(null);
        setBackdrop(null);
        applySuperSkin();
        setDragListener(new UINotifyDragListener(this));
        setMargin(new Insets(1, 1, 1, 1));

        m_titleBackdrop = new SolidBackdrop(ColorRGBA.GRAY);
        getTitleBar().setBackdrop(m_titleBackdrop);
        //getTitleBar().setFontStyles(titleStyledText);
        //modifyButton(getTitleBar().getCloseButton(), titleSize);
        //modifyButton(getTitleBar().getMinimizeButton(), titleSize);

        getTitleBar().getTitleLabel().setStyledText(true);
        setTitleString(m_titleString, titleSize, -1);
    }
    
    @Override
    public void setTitle(String title) {
        m_titleString = title;
    }
    
    public String getItemId() {
        return m_itemId;
    }
    
    public void setNoticeId(String noticeId) {
        m_noticeId = noticeId;
    }
    
    public String getNoticeId() {
        return m_noticeId;
    }
    
    public void setContext(String context) {
        m_context = context;
    }
    
    public String getContext() {
        return m_context;
    }
    
    public long getTimestamp() {
        return m_timestamp;
    }
    public void setTimestamp(long timestamp) {
        m_timestamp = timestamp;
    }
    
    public long getSortTimestamp() {
        return m_sortTimestamp;
    }
    public SaliencyLevel getSortLevel() {
        return m_sortLevel;
    }
    
    public SaliencyLevel getLevel() {
        return m_currentLevel;
    }
    public void setLevel(SaliencyLevel level) {
        m_currentLevel = level;
        if(m_currentLevel.ordinal() < m_sortLevel.ordinal()) {
            m_sortLevel = m_currentLevel;
            m_sortTimestamp = m_timestamp; 
        }
    }
    
    public void setTitleBarColor(ReadOnlyColorRGBA clr) {
        m_titleBackdrop.setColor(clr);
    }

    public void updateTimeout(double elapsed) {
        //logger.debug("updateTimeout : elapsed="+elapsed);
        if(m_timeoutInt > 0) {
            m_timeoutDbl -= elapsed;
            int secs = (int)Math.round(m_timeoutDbl);
            if(secs != m_timeoutInt) {
                m_timeoutInt = secs;
                setTitleTimeout(secs);
                if(secs <= 0) {          
                    setTitleTimeout(-1);
                    getNotifyHud().expire(this);
                }
            }
        }
    }

    public void setTimeout(double remaining) {
        m_timeoutDbl = remaining;
        m_timeoutInt = (int)remaining;
        setTitleTimeout(m_timeoutInt);
    }

    public void setTitleTimeout(int remaining) {
        setTitleString(m_titleString, titleSize, remaining);
    }

    public void setTitleString(String titleString, int titleSize, int remaining) {
        String title;
        if(remaining <= 0) {
            title = "[b][size="+titleSize+"]"+titleString+"[/size][/b]";
        }
        else {
            title = "[b][size="+titleSize+"]"+titleString+"[/size][/b]   [b][size="+timeSize+"][ "+remaining+" ][/size][/b]";
        }
        getTitleBar().getTitleLabel().setText(title);
    }

    /**
     * call after setting panel
     */
    public void skinHack() {
        getBasePanel().setBackdrop(backdrop);
        getContentPanel().setForegroundColor(ColorRGBA.WHITE);
        fireComponentDirty();
    }
    
    public boolean isExpired() {
        return m_expired;
    }
    public void setExpired(boolean state) {
        m_expired = state;
        if(m_expired) {
            m_currentLevel = SaliencyLevel.LogNotice;
            m_sortLevel    = SaliencyLevel.LogNotice;
        }
    }
    
    @Override
    public void close() {
        super.close();
        m_currentLevel  = SaliencyLevel.LogNotice;
        m_sortLevel     = SaliencyLevel.LogNotice;
        m_isPositionManaged = true;
        m_timestamp = 0;
        m_expired = false;
    }

    public VerveNotifyHud getNotifyHud() {
        VerveNotifyHud hud = (VerveNotifyHud)getHud();
        return hud;
    }

    public boolean isPositionManaged() {
        return m_isPositionManaged;
    }

    public void setPositionManaged(boolean state) {
        if(state != m_isPositionManaged) {
            m_isPositionManaged = state;
            VerveNotifyHud hud = getNotifyHud();
            VerveTask.asyncExec(new LayoutNoticesTask(hud));
            if(state) {
                setTitleTimeout(m_timeoutInt);
            }
            else {
                setTitleTimeout(-1);
            }
        }
    }

    public void setTitleColor(ReadOnlyColorRGBA color) {
        m_titleBackdrop.setColor(color);
    }

    @Override
    public boolean mouseClicked(final MouseButton button, final InputState state) {
        //logger.debug("clicked");
        return super.mouseClicked(button,  state);
    }

    protected void applySuperSkin() {
        super.applySkin();
    }

    @Override
    protected void applySkin() {
        //
    }

    public void startedDrag() {
        setPositionManaged(false);
    }

    public void dragged() {
        //setPositionManaged(false);
    }

    public void endedDrag() {
        if(getHudX() < this.getContentWidth()/2) {
            setPositionManaged(true);
        }
        else {
            setPositionManaged(false);
        }
    }

    void modifyButton(UIButton button, int mh) {
        if(button != null) {
            button.setIconDimensions(new Dimension(mh,mh));
            button.setContentSize(mh, mh);
            button.setMaximumContentSize(mh, mh);
            //button.removeAllListeners();
        }
    }

    public void setMinimized(boolean state) {
        // empty
    }
}
