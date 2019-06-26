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

import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.util.ui.ColorProvider;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * TODO: correct color for windows
 *
 */
public class AckButton extends Composite {
	
	public static final int BUTTON_WIDTH  = 90;
	public static final int BUTTON_HEIGHT = 28;
	
	protected Image m_bgImage = IssUiActivator.getImageFromRegistry("button_nm_90x28");
	protected Image m_bgImageEnter = IssUiActivator.getImageFromRegistry("button_en_90x28");
	protected Image m_pressedBgImage = IssUiActivator.getImageFromRegistry("button_dn_90x28");
	protected Image m_disabledBgImage = IssUiActivator.getImageFromRegistry("button_nm_90x28");
	
	protected Label  m_ackCounterLabel;
	protected String m_ackCounterValue = "0";
	
	protected int width  = BUTTON_WIDTH;
	protected int height = BUTTON_HEIGHT;
	
	protected String m_textString = "";
	
	protected Canvas m_buttonLabel;
	
	protected boolean m_enabled = false;
	protected boolean m_pressed = false;
	protected Image m_currentImage = m_disabledBgImage;
	protected GridData m_gridData;
	
	protected Set<SelectionListener> m_selectionListeners = new HashSet<SelectionListener>();
	
	public AckButton(Composite parent, int style) {
		super(parent, style);
		GridLayout gl = new GridLayout(2, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		setLayout(gl);
		m_gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		m_gridData.widthHint = width;
		m_gridData.heightHint = height;
		m_gridData.minimumWidth = width;
		m_gridData.minimumHeight = height;
		setLayoutData(m_gridData);
		
		setSize(width, height);

		m_buttonLabel = new Canvas(this, SWT.NONE);
		m_buttonLabel.setSize(width, height);
		m_buttonLabel.setLayoutData(m_gridData);
		m_buttonLabel.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				draw(e.gc);
			}
			
		});
		
		m_buttonLabel.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (isEnabled()){
					m_pressed = true;
					m_currentImage = m_pressedBgImage;
					m_buttonLabel.redraw();
					m_buttonLabel.update();
				}
			}
			
		});
		
		m_buttonLabel.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				m_pressed = false;
				if (isEnabled()){
					m_currentImage = m_bgImage;
					// invoke the selection listeners if any
					for (SelectionListener listener : m_selectionListeners){
						listener.widgetSelected(new SelectionEvent(event));
					}
				} else {
					m_currentImage = m_disabledBgImage;
				}
				m_buttonLabel.redraw();
				m_buttonLabel.update();
			}
			
		});
		
		m_buttonLabel.addListener(SWT.MouseEnter, new Listener() {
			@Override
			public void handleEvent(Event event) {
				m_pressed = false;
				if (isEnabled()){
					m_currentImage = m_bgImageEnter;
				} else {
					m_currentImage = m_bgImageEnter;
				}
				m_buttonLabel.redraw();
				m_buttonLabel.update();
			}
		});
		
		m_buttonLabel.addListener(SWT.MouseHover, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!m_pressed) {
					if (isEnabled()){
						m_currentImage = m_bgImageEnter;
					} else {
						m_currentImage =  m_bgImageEnter;
					}
					m_buttonLabel.redraw();
					m_buttonLabel.update();
				}
			}
		});
		
		m_buttonLabel.addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event event) {
				m_pressed = false;
				if (isEnabled()){
					m_currentImage = m_bgImage;
				} else {
					m_currentImage = m_disabledBgImage;
				}
				m_buttonLabel.redraw();
				m_buttonLabel.update();
			}
		});
		
		setEnabled(false);
	}
	
	public void setText(String text){
		m_textString = text;
		draw(new GC(m_buttonLabel));
	}
	
	
	protected void draw(GC gc) {
		Color fg = isEnabled()?ColorProvider.INSTANCE.black:ColorProvider.INSTANCE.darkGray;
		gc.setForeground(fg);

		int imagey = Math.max(0, (m_buttonLabel.getSize().y - height) / 2);
		gc.drawImage(m_currentImage, 0, imagey);

		Point size = gc.textExtent(m_textString);			
		int x = Math.max(0, ((width - size.x) / 2) - 15);
		int y = Math.max(0, ((m_buttonLabel.getSize().y - size.y) / 2));
		if (m_pressed){
			x +=0;
			y +=0;
		}
		gc.drawText(m_textString, x, y, true);
		
		
		// draw the white text area
		Color cfg = ColorProvider.INSTANCE.white;
		gc.setBackground(cfg);
		Rectangle rect = new Rectangle(x+35, y, 30, (size.y));
		gc.fillRectangle(rect);
		
		// write the text in the middle
		Point counterValSize = gc.textExtent(m_ackCounterValue);
		int cx = Math.max(x+39, (((rect.width) - counterValSize.x) / 2) + 30);
		int cy = Math.max(0, ((m_buttonLabel.getSize().y - size.y) / 2));
		if (m_pressed){
			cx +=0;
			cy +=0;
		}
		gc.drawText(m_ackCounterValue, cx, cy, true);
	
		
		gc.dispose();
	}
	
	public void addSelectionListener(SelectionListener listener){
		m_selectionListeners.add(listener);
	}
	
	
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		m_buttonLabel.setBackground(color);
	}

	@Override
	public void setEnabled(boolean enabled) {
		m_enabled = enabled;
		if (enabled){
			m_currentImage = m_bgImage;
		} else {
			m_currentImage = m_disabledBgImage;
		}
		m_buttonLabel.redraw();
		m_buttonLabel.update();
	}
	
	@Override
	public boolean isEnabled() {
		return m_enabled;
	}
	
	@Override
	public void setToolTipText(String string) {
		m_buttonLabel.setToolTipText(string);
	}
	
	public void updateAckCounterValue(int val) {
		m_ackCounterValue = String.valueOf(val);
		m_buttonLabel.redraw();
		m_buttonLabel.update();
	}
	
	
}
