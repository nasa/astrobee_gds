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
package gov.nasa.arc.irg.util.ui;

import gov.nasa.arc.irg.util.log.IrgLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;


public class IrgStatusBase extends ViewPart
{
    //public static String ID = IrgStatusBase.class.getName();
    protected StyledText	m_textWidget = null;
    protected int 			m_charCount = 0; // StyledText.getCharCount always returns 0
    protected int 			m_lineCount = 0; 
    protected StyleRange	m_styleRange[];
    protected Color			m_clr[];
    SimpleDateFormat        m_formatter = new SimpleDateFormat("h:mm:ss a");


    protected final HashMap<IrgLog.Level,StyleRange> m_styleMap = new HashMap<IrgLog.Level,StyleRange>();
    
    static final int ClrBlack	= 0;
    static final int ClrGrey	= 1;
    static final int ClrOrange	= 2;
    static final int ClrPurple 	= 3;
    static final int ClrRed 	= 4;
    static final int ClrYellow	= 5;
    static final int ClrBlue	= 6;
    static final int NumClrs	= 7;

    public IrgStatusBase() 
    {
        super();
         m_clr = new Color[NumClrs];
        for(int i = 0; i < NumClrs; i++)
        {
            switch(i) {
            case ClrBlack:	m_clr[i] = new Color(null,   0,   0,   0); break;
            case ClrGrey:	m_clr[i] = new Color(null, 122, 122, 122); break;
            case ClrOrange:	m_clr[i] = new Color(null, 255, 128,   0); break;
            case ClrPurple:	m_clr[i] = new Color(null, 192,   0,  96); break;
            case ClrRed:	m_clr[i] = new Color(null, 255,   0,   0); break;
            case ClrYellow:	m_clr[i] = new Color(null, 255, 255,   0); break;
            case ClrBlue:	m_clr[i] = new Color(null,   0,   0, 255); break;
            }
        }
    }

    @Override
    public void createPartControl(Composite parent) 
    {
        m_textWidget = new StyledText(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
    }

    @Override
    public void setFocus() {
        m_textWidget.setFocus();
    }

    public String formatString(String message) {
        if(message == null) {
            message = "(null)\n";
        }
        else if(!message.endsWith("\n")) {
            message += "\n";
        }

        Date today = new Date();
        return new String("(" + m_formatter.format(today) + ")\t" + message);
    }

	/**
	 * @param level
	 * @param message
	 * @param e
	 */
    public void postLogMessage(final IrgLog.Level level, String message, Throwable e) 
    {
        final String msg;
        if(e == null) {
            msg = formatString(message);
        }
        else {
            String tmpMsg = new String();

            tmpMsg = message + " - [" + e.getClass().getSimpleName() + "]" + (e.getMessage() == null ? "" : " : "+e.getMessage());
            Throwable cause = e.getCause();
            while(cause != null) {
                tmpMsg += "\n  Caused by: [" + cause.getClass().getSimpleName() + "]" + (cause.getMessage() == null ? "" : " : "+cause.getMessage());
                cause = cause.getCause();
            }
            msg = formatString(tmpMsg);
        }
        Runnable runnable = new Runnable()  {
            public void run() 
            {
                if(m_textWidget == null) {
                    Display.getDefault().asyncExec(this);
                    return;
                }
                try {
                    int visibleLines = 1+(m_textWidget.getClientArea().height / m_textWidget.getLineHeight());
                    int topIndex     = m_textWidget.getTopIndex();
                    StyleRange sr = createStyleRange(msg, level);
                    m_textWidget.append(msg);
                    m_textWidget.setStyleRange(sr);
                    m_textWidget.getDisplay().update();
                    if(topIndex >= m_lineCount-visibleLines) {
                        m_textWidget.setTopIndex(2+m_lineCount-visibleLines);
                    }
                    m_lineCount++;
                }
                catch (Exception e) {
                    // ignore
                }
            }
        };
        Display.getDefault().asyncExec(runnable);
    }

    /**
     * 
     * @param str
     * @param sc
     * @return
     */
    protected StyleRange createStyleRange(String str, IrgLog.Level sc) {
        StyleRange sr = new StyleRange();
        sr.start	= m_charCount;
        sr.length	= str.length();
        m_charCount += sr.length;
        switch(sc) {
        case Debug:
            sr.foreground = m_clr[ClrGrey];
            break;
        case Info:
            break;
        case Notice:
            sr.fontStyle  = SWT.BOLD;
            sr.foreground = m_clr[ClrBlue];
            break;
        case Warn:
            sr.fontStyle  = SWT.BOLD;
            sr.foreground = m_clr[ClrPurple];
            break;
        case Error:
            sr.fontStyle  = SWT.BOLD;
            sr.foreground = m_clr[ClrRed];
            break;
        case Fatal:
            sr.fontStyle  = SWT.BOLD;
            sr.foreground = m_clr[ClrBlack];
            sr.background = m_clr[ClrRed];
            break;
        }
        return sr;
    }
}  
