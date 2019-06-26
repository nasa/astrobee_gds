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
package gov.nasa.arc.irg.iss.ui.control;

import gov.nasa.arc.irg.iss.ui.IssFontHelper;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.irg.util.ui.status.IStatusLineChangedListener;
import gov.nasa.arc.irg.util.ui.status.StatusLineUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class SimpleStatusLineManager extends StatusLineManager {
	
	protected SimpleStatusLine m_statusLine;

	public SimpleStatusLineManager() {
		super();
		
		StatusLineUtil.INSTANCE.addListener(new IStatusLineChangedListener() {

			@Override
			public void statusChanged(String status) {
				setMessage(status);
			}
			
		});
	}
	
    /**
     * Creates and returns this manager's status line control. 
     * Does not create a new control if one already exists.
     *
     * @param parent the parent control
     * @param style the style for the control
     * @return the status line control
     * @since 3.0
     */
    @Override
	public Control createControl(Composite parent, int style) {
    	m_statusLine = new SimpleStatusLine(parent, style);
    	return m_statusLine;
    }
    
    /**
     * Returns whether the status line control is created
     * and not disposed.
     * 
     * @return <code>true</code> if the control is created
     *	and not disposed, <code>false</code> otherwise
     */
    private boolean statusLineExist() {
        return m_statusLine != null && !m_statusLine.isDisposed();
    }
    
    @Override
    public void dispose() {
    	if (statusLineExist()){
    		m_statusLine.dispose();
    	}
    }
    
    @Override
    public Control getControl() {
    	return m_statusLine;
    }
    
    @Override
    public void setMessage(Image image, String message) {
    	setMessage( message);
    }
    
    @Override
    public void setMessage(String message) {
    	if (statusLineExist()){
    		m_statusLine.setMessage(message);
    	}
    }
    
    public class SimpleStatusLine extends Composite {
    	String m_message;
    	Text m_text;

    	public SimpleStatusLine(Composite parent, int style){
    		super(parent, style);
    		setLayout(new GridLayout());
    		GridData gd = new GridData(SWT.FILL,SWT.FILL, true, true);
    		gd.minimumWidth = 800;
    		gd.widthHint = 800;
    		setLayoutData(gd);
        	
    		m_text = new Text(this, SWT.BORDER | SWT.READ_ONLY );
    		m_text.setFont(IssFontHelper.getISSFont());
    		m_text.setBackground(ColorProvider.INSTANCE.white);
    		m_text.setForeground(ColorProvider.INSTANCE.black);
    		m_text.setLayoutData(gd);
    	}
    	
    	public void setMessage(String message){
    		m_message =  trim(message);
    		if (m_message != null && !m_message.isEmpty()) {
    			m_message = convertToCorrectDateFormat(new Date().getTime()) + " " + m_message;
    		}
    		updateMessageDisplayed();
    	}
    	
    	/**
    	 * Updates the message label widget.
    	 */
    	protected void updateMessageDisplayed() {
    		if (m_text != null && !m_text.isDisposed()) {
    			Display display = m_text.getDisplay();
    			display.asyncExec(new Runnable() {

					@Override
					public void run() {
						m_text.setText(m_message == null ? "" : m_message); //$NON-NLS-1$
					}
    				
    			});
    			
    		}
    	}
    	
    	/**
    	 * Trims the message to be displayable in the status line. This just pulls
    	 * out the first line of the message. Allows null.
    	 */
    	String trim(String message) {
    		if (message == null) {
    			return null;
    		}
    		message = LegacyActionTools.escapeMnemonics(message);
    		int cr = message.indexOf('\r');
    		int lf = message.indexOf('\n');
    		if (cr == -1 && lf == -1) {
    			return message;
    		}
    		int len;
    		if (cr == -1) {
    			len = lf;
    		} else if (lf == -1) {
    			len = cr;
    		} else {
    			len = Math.min(cr, lf);
    		}
    		return message.substring(0, len);
    	}
    	
    	public  String convertToCorrectDateFormat(long time) {
    		String ds = ""; // ensures that a null can't be returned
    		SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy HH:mm:ss");
    		Date d = new Date(time);
    		ds = sdf.format(d);
    		return ds;
    	}
    }
}
