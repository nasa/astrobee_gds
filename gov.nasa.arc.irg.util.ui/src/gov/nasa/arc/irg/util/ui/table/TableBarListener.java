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
package gov.nasa.arc.irg.util.ui.table;

import gov.nasa.arc.irg.util.ui.ColorProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * This provides support for a tricolor dynamic bar based on a method that returns a percentage, 0 - 100.
 * @author tecohen
 *
 */
public class TableBarListener implements Listener {
	private Logger logger = Logger.getLogger(TableBarListener.class);
	
	protected float m_min = 0; // minimum valid value
	protected float m_max = 100; // max valid value;
	protected int m_spread = 100; // max - min

	protected float m_bottomThreshold = 25;	// 0 - this threshold is the bottom color
	protected float m_midThreshold = 50;		// bottom threshold to this threshold is the mid color
	
	protected Color m_bottomColor;	// default red
	protected Color m_midColor;	// default orange
	protected Color m_defaultColor;	// anything above the mid threshold is this color, default green
	protected Color m_foregroundColor; // default black
	
	protected DecimalFormat m_formatter = new DecimalFormat("###");  // you can customize this
	
	protected TableColumn m_barColumn;  // which table column should hold the bars
	protected int m_barColumnIndex; 	// what is the initial index of this table column in the table (start counting from 0)
	
	protected Method m_percentMethod;	// the method to get the percentage
	protected Object m_methodContainer = null;	// whatever will be holding the above method.  If the above method is static, this can be null.
	
	protected List<Color> m_colorsToDispose = new ArrayList<Color>();
	
	/**
	 * @param getPercentMethod The method to use to get the percentage.  Must return a number 0 -100 and take one object
	 * @param getMethodContainer The object which has the above method.  If the above method is static this can be null.
	 * @param barColumn	The column in the table to hold the bars
	 * @param barColumnIndex the initial index of this column
	 * @param bottomThreshold
	 * @param midThreshold
	 * @param bottomColor
	 * @param midColor
	 * @param defaultColor
	 * @param foregroundColor the font color
	 */
	public TableBarListener(Method getPercentMethod,
							Object getMethodContainer,
							TableColumn barColumn, 
							int barColumnIndex, 
							int bottomThreshold,
							int midThreshold,
							Color bottomColor,
							Color midColor,
							Color defaultColor,
							Color foregroundColor) {
		m_percentMethod = getPercentMethod;
		m_methodContainer = getMethodContainer;
		m_barColumn = barColumn;
		m_barColumnIndex = barColumnIndex;
		m_bottomThreshold = bottomThreshold;
		m_midThreshold = midThreshold;
		m_bottomColor = bottomColor;
		m_midColor = midColor;
		m_defaultColor = defaultColor;
		m_foregroundColor = foregroundColor;
	}
	
	/**
	 * @param getPercentMethod The method to use to get the percentage.  Must return a number 0 -100 and take one object
	 * @param getMethodContainer The object which has the above method.  If the above method is static this can be null.
	 * @param barColumn	The column in the table to hold the bars
	 * @param barColumnIndex the initial index of this column
	 * @param bottomThreshold
	 * @param midThreshold
	 * @param bottomColor
	 * @param midColor
	 * @param defaultColor
	 * @param foregroundColor the font color
	 */
	public TableBarListener(Method getPercentMethod,
							Object getMethodContainer,
							TableColumn barColumn, 
							int barColumnIndex, 
							int bottomThreshold,
							int midThreshold,
							Color bottomColor,
							Color midColor,
							Color defaultColor,
							Color foregroundColor,
							float min,
							float max) {
		m_percentMethod = getPercentMethod;
		m_methodContainer = getMethodContainer;
		m_barColumn = barColumn;
		m_barColumnIndex = barColumnIndex;
		m_bottomThreshold = bottomThreshold;
		m_midThreshold = midThreshold;
		m_bottomColor = bottomColor;
		m_midColor = midColor;
		m_defaultColor = defaultColor;
		m_foregroundColor = foregroundColor;
		m_min = min;
		m_max = max;
		m_spread = (int)(max - min);
	}
	
	/**
	 * Defaults to 0-25 red, 25-50 orange, 50+ green.
	 * @param getPercentMethod The method to use to get the percentage.  Must return a number 0 -100 and take one object.
	 * @param getMethodContainer The object which has the above method.  If the above method is static this can be null.
	 * @param barColumn	The column in the table to hold the bars
	 * @param barColumnIndex the initial index of this column
	 */
	public TableBarListener(Method getPercentMethod,
							Object getMethodContainer,
							TableColumn barColumn, 
							int barColumnIndex) {
		m_percentMethod = getPercentMethod;
		m_methodContainer = getMethodContainer;
		m_barColumn = barColumn;
		m_barColumnIndex = barColumnIndex;
		initializeDefaultColors();
	}
	
	/**
	 * Defaults to red, orange, green but you can set the thresholds
	 * @param getPercentMethod The method to use to get the percentage.  Must return a number 0 -100 and take one object.
	 * @param getMethodContainer The object which has the above method.  If the above method is static this can be null.
	 * @param barColumn	The column in the table to hold the bars
	 * @param barColumnIndex the initial index of this column
	 * @param bottomThreshold
	 * @param midThreshold
	 */
	public TableBarListener(Method getPercentMethod,
							Object getMethodContainer,
							TableColumn barColumn, 
							int barColumnIndex, 
							int bottomThreshold,
							int midThreshold) {
		m_percentMethod = getPercentMethod;
		m_methodContainer = getMethodContainer;
		m_barColumn = barColumn;
		m_barColumnIndex = barColumnIndex;
		m_bottomThreshold = bottomThreshold;
		m_midThreshold = midThreshold;
		initializeDefaultColors();
	}
	protected void initializeDefaultColors() {
		m_bottomColor = ColorProvider.INSTANCE.red;
		m_midColor = ColorProvider.INSTANCE.orange;
		m_defaultColor = ColorProvider.INSTANCE.superDarkGreen;
		m_foregroundColor = ColorProvider.INSTANCE.black;
	}
	
	/**
	 * Get the bar color based on the percents.
	 * @param percent
	 * @return
	 */
	protected Color getBarColor(Number percent) {
		if (percent == null){
			return getDefaultColor();
		}
		float floatPercent = percent.floatValue();
		if (floatPercent < getBottomThreshold()){
			return getBottomColor();
		} else if (floatPercent < getMidThreshold()){
			return getMidColor();
		}
		return getDefaultColor();
	}

	/*
	 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
	 * Therefore, it is critical for performance that these methods be
	 * as efficient as possible.
	 */
	public void handleEvent(Event event) {
		if (event.index == getBarColumnIndex()) {
			GC gc = event.gc;
			TableItem item = (TableItem)event.item;
			if (item.getData() != null){
				Number percent;
				try {
					percent = (Number) getPercentMethod().invoke(getMethodContainer(), item.getData());
					if (percent != null){
						String percentString = getFormatter().format(percent);
							
						Color foreground = gc.getForeground();
						Color background = gc.getBackground();
						
						Color barColor = getBarColor(percent);
						gc.setForeground(barColor);
						gc.setBackground(barColor);
						int width = (getBarColumn().getWidth() - 2) * (percent.intValue()) / m_spread;
						gc.fillRectangle(event.x-1, event.y, width, event.height-2);					
						Rectangle rect2 = new Rectangle(event.x-2, event.y, width-1, event.height-3);
						gc.drawRectangle(rect2);
						if (getForegroundColor() != null){
						    gc.setForeground(getForegroundColor());
						} else {
						    gc.setForeground(ColorProvider.INSTANCE.black);
						}
						Point size = event.gc.textExtent(percentString);					
						int offset = Math.max(0, (event.height - size.y) / 2);
						gc.drawText(percentString, event.x+2, event.y+offset, true);
						gc.setForeground(background);
						gc.setBackground(foreground);
					}
				} catch (IllegalArgumentException e) {
					logger.error(e);
				} catch (IllegalAccessException e) {
					logger.error(e);
				} catch (InvocationTargetException e) {
					logger.error(e);
				}
				
			}
		}
	}

	public void dispose() {
		for (Color c : m_colorsToDispose){
			c.dispose();
		}
	}

	public float getBottomThreshold() {
		return m_bottomThreshold;
	}


	public void setBottomThreshold(float bottomThreshold) {
		m_bottomThreshold = bottomThreshold;
	}


	public float getMidThreshold() {
		return m_midThreshold;
	}


	public void setMidThreshold(float midThreshold) {
		m_midThreshold = midThreshold;
	}


	public Color getBottomColor() {
		return m_bottomColor;
	}


	public void setBottomColor(Color bottomColor) {
		m_bottomColor = bottomColor;
	}


	public Color getMidColor() {
		return m_midColor;
	}


	public void setMidColor(Color midColor) {
		m_midColor = midColor;
	}


	public Color getDefaultColor() {
		return m_defaultColor;
	}


	public void setDefaultColor(Color defaultColor) {
		m_defaultColor = defaultColor;
	}


	public DecimalFormat getFormatter() {
		return m_formatter;
	}


	public void setFormatter(DecimalFormat formatter) {
		m_formatter = formatter;
	}


	public TableColumn getBarColumn() {
		return m_barColumn;
	}


	public void setBarColumn(TableColumn barColumn) {
		m_barColumn = barColumn;
	}


	public Color getForegroundColor() {
		return m_foregroundColor;
	}


	public void setForegroundColor(Color foregroundColor) {
		m_foregroundColor = foregroundColor;
	}


	public int getBarColumnIndex() {
		return m_barColumnIndex;
	}


	public void setBarColumnIndex(int barColumnIndex) {
		m_barColumnIndex = barColumnIndex;
	}

	public Method getPercentMethod() {
		return m_percentMethod;
	}

	public void setPercentMethod(Method getPercentMethod) {
		m_percentMethod = getPercentMethod;
	}

	public Object getMethodContainer() {
		return m_methodContainer;
	}

	public void setMethodContainer(Object methodContainer) {
		m_methodContainer = methodContainer;
	}

	public float getMax() {
		return m_max;
	}

	public void setMax(float max) {
		m_max = max;
		m_spread = (int)(m_max - m_min);
	}

	public float getMin() {
		return m_min;
	}

	public void setMin(float min) {
		m_min = min;
		m_spread = (int)(m_max - m_min);
	}
}
