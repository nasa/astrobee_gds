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
package gov.nasa.arc.verve.common.ardor3d.text;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ardor3d.util.resource.URLResourceSource;


/**
 * Simple singleton that loads some font textures and provides easy access. 
 */
public class BMFontManager {
    private static final Logger logger = Logger.getLogger(BMFontManager.class.getName()); 
    private static final BMFontManager s_instance = new BMFontManager();
    private final HashMap<String,BMFont> m_loadedFonts = new HashMap<String,BMFont>();

    public enum FontStyle {
        SansExtraSmall("DroidSans-14-bold-regular"),
        SansSmall     ("DroidSans-16-bold-regular"),
        SansMedium    ("DroidSans-20-bold-regular"),
        SansLarge     ("DroidSans-26-bold-regular"),
        SansExtraLarge("DroidSans-40-bold-regular"),
        MonoSmall     ("DroidSansMono-15-bold-regular"),
        MonoMedium    ("DroidSansMono-20-bold-regular"),
        MonoLarge     ("DroidSansMono-26-bold-regular"),
        MonoExtraLarge("DroidSansMono-40-bold-regular"),
        ;
        public final String fontName;
        FontStyle(String name) {
            this.fontName = name;
        }
    }

    /**
     * 
     * @param style
     * @return
     */
    public static BMFont getFont(FontStyle style) {
        return getFont(style.fontName);
    }

    /**
     * Retrieves the font, loads it if need be. Font must exist in this bundle in images/fonts.
     * @param fontName
     * @return
     */
    public static BMFont getFont(String fontName){
        BMFont found = s_instance.m_loadedFonts.get(fontName);
        if (found == null){
            found = newFont(fontName);
            if (found != null){
                s_instance.m_loadedFonts.put(fontName, found);
            }
        }
        return found;
    }

    /**
     * Creates the font. Font must exist in this bundle in images/fonts.
     * @param fontName
     * @return
     */
    public static BMFont newFont(String fontName){
        BMFont font = null;
        URLResourceSource rs = new URLResourceSource(BMFontManager.class.getResource(fontName+".fnt"));
        //BundleResourceSource rs = new BundleResourceSource(Activator.getDefault().getBundle(), "images/fonts/"+fontName+".fnt");
        try {
            font = new BMFont(rs, false);
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.logp(Level.WARNING, "BMFontManager", "getFont(String)",
                            "Unable to load font.", new Object[] { fontName });
            }
        }
        return font;
    }

    public static BMFont defaultFont() {
        return getFont(FontStyle.SansMedium);
    }

    public static BMFont sansExtraSmall(){ return getFont(FontStyle.SansExtraSmall); }
    public static BMFont sansSmall()     { return getFont(FontStyle.SansSmall); }
    public static BMFont sansMedium()    { return getFont(FontStyle.SansMedium); }
    public static BMFont sansLarge()     { return getFont(FontStyle.SansLarge); }
    public static BMFont sansExtraLarge(){ return getFont(FontStyle.SansExtraLarge); }

    public static BMFont monoSmall()     { return getFont(FontStyle.MonoSmall); }
    public static BMFont monoMedium()    { return getFont(FontStyle.MonoMedium); }
    public static BMFont monoLarge()     { return getFont(FontStyle.MonoLarge); }
    public static BMFont monoExtraLarge(){ return getFont(FontStyle.MonoExtraLarge); }

    ///////////////////////////////
    /// DEPRECATE THESE METHODS ///
    ///////////////////////////////
    boolean m_allLoaded = false;
    /**
     * Returns a list of all the fonts.  
     * Forces a load if they are not loaded.
     * @return
     */
    public static BMFont[] allFonts() {
        s_instance.loadAllFonts();
        return s_instance.m_loadedFonts.values().toArray(new BMFont[s_instance.m_loadedFonts.size()]);
    }

    /**
     * If they have not been loaded, force loading of all the fonts.
     */
    protected synchronized void loadAllFonts() {
        if (!m_allLoaded){
            for (FontStyle fontStyle : FontStyle.values()){
                getFont(fontStyle.fontName);
            }
            m_allLoaded = true;
        }
    }

}
