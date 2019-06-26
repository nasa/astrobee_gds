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
package gov.nasa.arc.verve.ui3d.notify;

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.common.notify.NoticeActionConnector;
import gov.nasa.arc.verve.ui3d.widgets.UIIconButton;
import gov.nasa.arc.verve.ui3d.widgets.UINotifyFrame;

import java.util.EnumSet;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UILabel;
import com.ardor3d.extension.ui.UIPanel;
import com.ardor3d.extension.ui.layout.GridLayout;
import com.ardor3d.extension.ui.layout.GridLayoutData;
import com.ardor3d.extension.ui.layout.UILayout;
import com.ardor3d.extension.ui.util.Alignment;
import com.ardor3d.math.ColorRGBA;

public class NotifyBox extends UINotifyFrame {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NotifyBox.class);
    protected String       m_subject;
    protected String       m_body;

    protected int          m_subjWrap = 25;
    protected int          m_bodyWrap = 25;

    protected UIPanel      m_panel;
    protected UIIconButton m_levelIcon;
    protected UILabel      m_subjLabel;
    protected UILabel      m_bodyLabel;

    protected Runnable m_closeRunnable = new Runnable() {
        @Override
        public void run() {
            NotifyBox.this.close();
        }
    };

    public NotifyBox(String itemId) {
        this(itemId, 0, SaliencyLevel.Notice, "", "");
    }

    public NotifyBox(String itemId, long timestamp, SaliencyLevel level, String subject, String body) {
        super(itemId, timestamp, EnumSet.of(FrameButtons.CLOSE, FrameButtons.MAXIMIZE));
        UILayout layout = new GridLayout();
        m_panel = new UIPanel(layout);
        int minWidth;

        minWidth = (UIComponent.getDefaultFontSize() * m_subjWrap);
        m_subjLabel = new UILabel("");
        m_subjLabel.setLayoutData(new GridLayoutData(2, true, true));
        m_subjLabel.setStyledText(true);
        m_subjLabel.setAlignment(Alignment.MIDDLE);
        m_subjLabel.setMinimumContentWidth(minWidth);
        m_panel.add(m_subjLabel);

        m_levelIcon = new UIIconButton();
        m_levelIcon.setDimensions(48, 48);
        m_levelIcon.setLayoutData(new GridLayoutData(1, false, false));
        m_levelIcon.setOnLeftClickRunnable(m_closeRunnable);
        m_panel.add(m_levelIcon);

        minWidth = (UIComponent.getDefaultFontSize() * m_bodyWrap)/2;
        m_bodyLabel = new UILabel("");
        m_bodyLabel.setLayoutData(new GridLayoutData(1, true, true));
        m_bodyLabel.setStyledText(false);
        m_bodyLabel.setMinimumContentWidth(minWidth);
        m_panel.add(m_bodyLabel);

        setContentPanel(m_panel);
        skinHack();

        updateMinimumSizeFromContents();
        layout();
        pack();        
        setOpacity(0.8f);

        if(m_minimized) {
            m_panel.remove(m_levelIcon);
            m_panel.remove(m_bodyLabel);
            updateMinimumSizeFromContents();
            layout();
            pack();        
        }

        setTimestamp(timestamp);
        setLevel(level);
        setText(subject, body);
    }

    ColorRGBA tmpBackColor = new ColorRGBA();
    ColorRGBA tmpForeColor = new ColorRGBA();

    public void updateNotice(String context, String noticeId, long timestamp,
                             int timeout, SaliencyLevel level, 
                             String subject, String body) {
        setContext(context);
        setNoticeId(noticeId);
        setTimestamp(timestamp);
        setTimeout(timeout);
        setLevel(level);
        setText(subject, body);
    }

    @Override
    public void setLevel(SaliencyLevel level) {
        super.setLevel(level);
        Ardor3D.color(level.foreColor, tmpForeColor);
        Ardor3D.color(level.backColor, tmpBackColor);
        m_levelIcon.setIcon(NotifySubTex.get(level));
        setTitleBarColor(tmpBackColor);
        getTitleBar().getTitleLabel().setForegroundColor(tmpForeColor);
        m_bodyLabel.setForegroundColor(tmpForeColor);
        m_subjLabel.setForegroundColor(tmpForeColor);
    }

    /**
     * 
     * @param subject
     * @param body
     */
    protected void setText(String subject, String body) {
        int x = getHudX();
        int y = getHudY();
        int oldHeight = getLocalComponentHeight();

        m_subject = subject;
        m_body    = body;

        final int big    = 4+UIComponent.getDefaultFontSize();
        final int magic  = 15;
        int minBodyWidth = (UIComponent.getDefaultFontSize()*m_bodyWrap) / 2;
        int minSubjWidth = magic + minBodyWidth+m_levelIcon.getContentWidth();

        StringBuilder sb = new StringBuilder();
        sb.append("[b][size="+big+"]");
        sb.append(WordUtils.wrap(subject, m_subjWrap, "\n", false));
        sb.append("[/size][/b]\n");
        m_subjLabel.setMinimumContentSize(1,1);
        m_subjLabel.setContentSize(1,1);
        m_subjLabel.setText(sb.toString());
        //m_subjLabel.updateMinimumSizeFromContents();
        m_subjLabel.setMinimumContentWidth(minSubjWidth);

        m_bodyLabel.setMinimumContentSize(1,1);
        m_bodyLabel.setContentSize(1,1);
        m_bodyLabel.setText(WordUtils.wrap(body, m_bodyWrap, "\n", true));
        // rough min width
        m_bodyLabel.setMinimumContentWidth(minBodyWidth);

        updateMinimumSizeFromContents();
        layout();
        pack();  

        int heightDiff = getLocalComponentHeight()-oldHeight;
        if(!isPositionManaged()) 
            setHudXY(x, y-heightDiff);
    }

    public void setIconLeftClickRunnable(Runnable runnable) {
        m_levelIcon.setOnLeftClickRunnable(runnable);
    }

    @Override
    public void close() {
        try {
            NoticeActionConnector.INSTANCE.noticeDismissed(m_context, m_itemId, m_noticeId);
        }
        finally {
            super.close();
        }
    }

    @Override
    public void setMinimized(boolean state) {
        m_minimized = state;
        if(m_minimized) {
            m_panel.remove(m_levelIcon);
            m_panel.remove(m_bodyLabel);
        }
        else {
            m_panel.add(m_levelIcon);
            m_panel.add(m_bodyLabel);
        }
        setText(m_subject, m_body);
        getNotifyHud().layoutNoticesFast();
    }
    
    @Override
    public void maximize() {
        setMinimized(!m_minimized);
    }

    @Override
    public void restore() {
        setMinimized(false);
    }
}
