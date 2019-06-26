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

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

/**
 * One stop shopping listener for font changes and settings for tables / titles  / views
 * 
 * @author tecohen
 *
 */
public class FontHelper {
    
    public static final String TEST = "test";
    protected static Color s_tableFontColor;
    protected static Font s_tableFont;
    protected static int s_tableFontHeight;
    protected static Color s_titleFontColor;
    protected static Font s_titleFont;
    
    protected static IThemeManager s_themeManager = null;
    protected static ITheme s_currentTheme = null;
    protected static FontRegistry s_fontRegistry = null;
    protected static ColorRegistry s_colorRegistry = null;

    public static FontRegistry getFontRegistry() {
        if (s_themeManager == null){
            s_themeManager = PlatformUI.getWorkbench().getThemeManager();
        }
        if (s_currentTheme == null){
            s_currentTheme = s_themeManager.getCurrentTheme();
        }
        if (s_fontRegistry == null){
            s_fontRegistry = s_currentTheme.getFontRegistry();
        }
        return s_fontRegistry;
    }
    
    public static ColorRegistry getColorRegistry() {
        if (s_themeManager == null){
            s_themeManager = PlatformUI.getWorkbench().getThemeManager();
        }
        if (s_currentTheme == null){
            s_currentTheme = s_themeManager.getCurrentTheme();
        }
        if (s_colorRegistry == null){
            s_colorRegistry = s_currentTheme.getColorRegistry();
        }
        return s_colorRegistry;
    }
    
    protected static IPropertyChangeListener s_fontChangeListener = new IPropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent event) {
            String property = event.getProperty();
            if (property.equals("gov.nasa.arc.irg.util.ui.tableFont")){
                if (s_tableFont != null){
                    s_tableFont.dispose();
                }
                getTableFont();
            } else if (property.equals("gov.nasa.arc.irg.util.ui.tableFontColor")){
                if (s_tableFontColor != null){
                    s_tableFontColor.dispose();
                }
                getTableFontColor();
            } else if (property.equals("gov.nasa.arc.irg.util.ui.titleFont")){
                if (s_titleFont != null){
                    s_titleFont.dispose();
                }
                getTitleFont();
            } else if (property.equals("gov.nasa.arc.irg.util.ui.titleFontColor")) {
                if (s_titleFontColor != null){
                    s_titleFontColor.dispose();
                }
                getTitleFontColor();
            }
        }
        
    };

    
    public static Font getTableFont() {
        if (s_tableFont == null || s_tableFont.isDisposed()){
            s_tableFont = getFontRegistry().get("gov.nasa.arc.irg.util.ui.tableFont");
           
           FontData[] fdarray = s_tableFont.getFontData();
           if (fdarray != null && fdarray.length > 0){
               FontData fd  = fdarray[0];
               s_tableFontHeight = fd.getHeight() + 6;
           } else {
               s_tableFontHeight = 14;
           }
           
           GC gc = new GC(Display.getDefault());
           gc.setFont(s_tableFont);
           Point size = gc.textExtent(TEST);
           gc.dispose();
           s_tableFontHeight = Math.max(s_tableFontHeight, size.y + 2);
        }
        return s_tableFont;
    }
    
    public static Font getTitleFont() {
        if (s_titleFont == null || s_titleFont.isDisposed()){
           s_titleFont =  getFontRegistry().get("gov.nasa.arc.irg.util.ui.titleFont");
        }
        return s_titleFont;
    }

    public static Color getTitleFontColor() {
        if (s_titleFontColor == null || s_titleFontColor.isDisposed()){
            s_titleFontColor = getColorRegistry().get("gov.nasa.arc.irg.util.ui.titleFontColor");
        }
        return s_titleFontColor;
    }
    
    public static Color getTableFontColor() {
        if (s_tableFontColor == null || s_tableFontColor.isDisposed()){
            s_tableFontColor = getColorRegistry().get("gov.nasa.arc.irg.util.ui.tableFontColor");
        }
        return s_tableFontColor;
    }


    public static int getTableFontHeight() {
        return s_tableFontHeight;
    }

    public static IPropertyChangeListener getFontChangeListener() {
        return s_fontChangeListener;
    }
    
    public static void dispose() {
        if (s_tableFontColor != null){
            s_tableFontColor.dispose();
            s_tableFontColor = null;
        }
        if (s_tableFont != null){
            s_tableFont.dispose();
            s_tableFont = null;
        }
        if (s_titleFontColor != null){
            s_titleFontColor.dispose();
            s_titleFontColor = null;
        }
        if (s_titleFont != null){
            s_titleFont.dispose();
            s_titleFont = null;
        }
    }
}
