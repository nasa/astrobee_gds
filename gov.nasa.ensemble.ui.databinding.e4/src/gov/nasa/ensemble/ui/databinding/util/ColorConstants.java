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
package gov.nasa.ensemble.ui.databinding.util;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorConstants  {
	
	public static final Color brown      = new Color(getDisplay(), 128, 64, 0);
	public static final Color magenta    = new Color(getDisplay(), 128, 0,  64);
	public static final Color navy       = new Color(getDisplay(), 0,   0,  128);
	public static final Color paleYellow = new Color(getDisplay(), 255, 255, 215);
	public static final Color paleGreen  = new Color(getDisplay(), 200, 255, 180);
	public static final Color paleRed    = new Color(getDisplay(), 255, 100, 100);
	public static final Color paleBlue   = new Color(getDisplay(), 200, 200, 255);
	public static final Color midGreen   = new Color(getDisplay(), 50, 200, 50);
	public static final Color midBlue 	 = new Color(getDisplay(), 50, 50, 200);
	
	public static Display getDisplay() {
		Display display = Display.getDefault();
		if (display != null)
			return display;
		return Display.getDefault();
	}
	

	    private static Color getColor(final int which) {
	        Display display = Display.getDefault();
	        if (display != null)
	            return display.getSystemColor(which);
	        display = Display.getDefault();
	        final Color result[] = new Color[1];
	        display.syncExec(new Runnable() {
	            public void run() {
	                synchronized (result) {
	                    result[0] = Display.getDefault().getSystemColor(which);                 
	                }
	            }
	        });
	        synchronized (result) {
	            return result[0];
	        }
	    }

	/**
	 * @see SWT#COLOR_WIDGET_HIGHLIGHT_SHADOW
	 */
	Color buttonLightest  = getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
	/**
	 * @see SWT#COLOR_WIDGET_BACKGROUND
	 */
	Color button  = getColor(SWT.COLOR_WIDGET_BACKGROUND);
	/**
	 * @see SWT#COLOR_WIDGET_NORMAL_SHADOW
	 */
	Color buttonDarker = getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	/**
	 * @see SWT#COLOR_WIDGET_DARK_SHADOW
	 */
	Color buttonDarkest = getColor(SWT.COLOR_WIDGET_DARK_SHADOW);

	/**
	 * @see SWT#COLOR_LIST_BACKGROUND
	 */
	Color listBackground = getColor(SWT.COLOR_LIST_BACKGROUND);
	/**
	 * @see SWT#COLOR_LIST_FOREGROUND
	 */
	Color listForeground = getColor(SWT.COLOR_LIST_FOREGROUND);

	/**
	 * @see SWT#COLOR_WIDGET_BACKGROUND
	 */
	Color menuBackground = getColor(SWT.COLOR_WIDGET_BACKGROUND);
	/**
	 * @see SWT#COLOR_WIDGET_FOREGROUND
	 */
	Color menuForeground = getColor(SWT.COLOR_WIDGET_FOREGROUND);
	/**
	 * @see SWT#COLOR_LIST_SELECTION
	 */
	Color menuBackgroundSelected = getColor(SWT.COLOR_LIST_SELECTION);
	/**
	 * @see SWT#COLOR_LIST_SELECTION_TEXT
	 */
	Color menuForegroundSelected = getColor(SWT.COLOR_LIST_SELECTION_TEXT);

	/**
	 * @see SWT#COLOR_TITLE_BACKGROUND
	 */
	Color titleBackground  = getColor(SWT.COLOR_TITLE_BACKGROUND);
	/**
	 * @see SWT#COLOR_TITLE_BACKGROUND_GRADIENT
	 */
	Color titleGradient = getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
	/**
	 * @see SWT#COLOR_TITLE_FOREGROUND
	 */
	Color titleForeground = getColor(SWT.COLOR_TITLE_FOREGROUND);
	/**
	 * @see SWT#COLOR_TITLE_INACTIVE_FOREGROUND
	 */
	Color titleInactiveForeground = getColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
	/**
	 * @see SWT#COLOR_TITLE_INACTIVE_BACKGROUND
	 */
	Color titleInactiveBackground = getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
	/**
	 * @see SWT#COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT
	 */
	Color titleInactiveGradient = getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);

	/**
	 * @see SWT#COLOR_INFO_FOREGROUND
	 */
	Color tooltipForeground  = getColor(SWT.COLOR_INFO_FOREGROUND);
	/**
	 * @see SWT#COLOR_INFO_BACKGROUND
	 */
	Color tooltipBackground = getColor(SWT.COLOR_INFO_BACKGROUND);

	/*
	 * Misc. colors
	 */
	/** One of the pre-defined colors */
	Color white      = new Color(null, 255, 255, 255);
	/** One of the pre-defined colors */
	Color lightGray  = new Color(null, 192, 192, 192);
	/** One of the pre-defined colors */
	Color gray       = new Color(null, 128, 128, 128);
	/** One of the pre-defined colors */
	Color darkGray   = new Color(null,  64,  64,  64);
	/** One of the pre-defined colors */
	Color black      = new Color(null,   0,   0,   0);
	/** One of the pre-defined colors */
	Color red        = new Color(null, 255,   0,   0);
	/** One of the pre-defined colors */
	Color orange     = new Color(null, 255, 196,   0);
	/** One of the pre-defined colors */
	Color yellow     = new Color(null, 255, 255,   0);
	/** One of the pre-defined colors */
	Color green      = new Color(null,   0, 255,   0);
	/** One of the pre-defined colors */
	Color lightGreen = new Color(null,  96, 255,  96);
	/** One of the pre-defined colors */
	Color darkGreen  = new Color(null,   0, 127,   0);
	/** One of the pre-defined colors */
	Color cyan       = new Color(null,   0, 255, 255);
	/** One of the pre-defined colors */
	Color lightBlue  = new Color(null, 127, 127, 255);
	/** One of the pre-defined colors */
	Color blue       = new Color(null,   0,   0, 255);
	/** One of the pre-defined colors */
	Color darkBlue   = new Color(null,   0,   0, 127);

}

	
