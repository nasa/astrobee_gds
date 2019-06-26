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
package gov.nasa.arc.verve.ui3d.font;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.text.CharacterDescriptor;
import com.ardor3d.extension.ui.text.StyleConstants;
import com.ardor3d.extension.ui.text.font.FontProvider;
import com.ardor3d.extension.ui.text.font.UIFont;
import com.ardor3d.image.Texture2D;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMFont.Char;
import com.ardor3d.util.export.binary.BinaryImporter;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.URLResourceSource;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * duplicate class so the class loader can find our font files
 */
public class VerveFontProvider implements FontProvider  {
    private static Logger logger = Logger.getLogger(VerveFontProvider.class.getName());

    protected Map<UIFont, Integer> _scoreMap = Maps.newHashMap();

    protected final Set<FontInfo> _fonts = Sets.newHashSet();

    public void addFont(final String source, final String family, final int size, final boolean bold,
            final boolean italic) {
        final FontInfo fontInfo = new FontInfo();
        fontInfo.source = source;
        fontInfo.family = family;
        fontInfo.size = size;
        fontInfo.bold = bold;
        fontInfo.italic = italic;
        _fonts.add(fontInfo);
    }

    @Override
    public UIFont getClosestMatchingFont(final Map<String, Object> currentStyles, final AtomicReference<Double> scale) {
        final boolean isBold = currentStyles.containsKey(StyleConstants.KEY_BOLD) ? (Boolean) currentStyles
                .get(StyleConstants.KEY_BOLD) : false;
        final boolean isItalic = currentStyles.containsKey(StyleConstants.KEY_ITALICS) ? (Boolean) currentStyles
                .get(StyleConstants.KEY_ITALICS) : false;
        final int size = currentStyles.containsKey(StyleConstants.KEY_SIZE) ? (Integer) currentStyles
                .get(StyleConstants.KEY_SIZE) : UIComponent.getDefaultFontSize();
        final String family = currentStyles.containsKey(StyleConstants.KEY_FAMILY) ? currentStyles.get(
                StyleConstants.KEY_FAMILY).toString() : UIComponent.getDefaultFontFamily();

        FontInfo closest = null;
        int score, bestScore = Integer.MIN_VALUE;

        for (final FontInfo info : _fonts) {
            score = 0;
            if (family.equalsIgnoreCase(info.family)) {
                score += 200;
            }

            if (info.bold) {
                if (isBold) {
                    score += 50;
                } else {
                    score -= 25;
                }
            } else if (isBold) {
                score -= 25;
            }

            if (info.italic) {
                if (isItalic) {
                    score += 50;
                } else {
                    score -= 25;
                }
            } else if (isItalic) {
                score -= 25;
            }

            score -= Math.abs(size - info.size);

            if (score > bestScore) {
                closest = info;
                bestScore = score;
            }
        }

        if (closest == null) {
            return null;
        }

        scale.set((double) size / closest.size);

        // now build the requested font if necessary
        if (closest.uiFont == null) {
            // load from file if necessary
            if (closest.bmFont == null) {
                // First try to load from a3d file
                String resource = closest.source + ".a3d";
                URL url = ResourceLocatorTool.getClassPathResource(VerveFontProvider.class, resource);
                try {
                    if (url != null) {
                        final BinaryImporter binaryImporter = new BinaryImporter();
                        closest.bmFont = (BMFont) binaryImporter.load(url);
                    } else {
                        // Not found, load from .fnt
                        resource = closest.source + ".fnt";
                        url = ResourceLocatorTool.getClassPathResource(VerveFontProvider.class, resource);
                        closest.bmFont = new BMFont(new URLResourceSource(url), false);
                    }
                } catch (final IOException e) {
                    VerveFontProvider.logger.severe("Unable to load font: " + closest.source);
                    return null;
                }
            }

            final Map<Character, CharacterDescriptor> descriptors = Maps.newHashMap();
            for (final int val : closest.bmFont.getMappedChars()) {
                final Char c = closest.bmFont.getChar(val);
                final CharacterDescriptor desc = new CharacterDescriptor(c.x, c.y, c.width, c.height, c.xadvance,
                        c.xoffset, c.yoffset, 1.0, null);
                descriptors.put((char) val, desc);
            }

            closest.uiFont = new UIFont((Texture2D) closest.bmFont.getPageTexture(), descriptors,
                    closest.bmFont.getLineHeight(), closest.bmFont.getSize());
            final Map<Integer, Map<Integer, Integer>> kernings = closest.bmFont.getKerningMap();
            for (final int valA : kernings.keySet()) {
                final Map<Integer, Integer> kerns = kernings.get(valA);
                for (final int valB : kerns.keySet()) {
                    closest.uiFont.addKerning((char) valA, (char) valB, kerns.get(valB));
                }
            }
        }

        return closest.uiFont;
    }

    class FontInfo {
        String source;
        String family;
        int size;
        boolean bold;
        boolean italic;
        BMFont bmFont;
        UIFont uiFont;
    }
}
