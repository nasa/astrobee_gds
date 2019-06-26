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
package gov.nasa.arc.verve.ui3d.skin;

import org.apache.log4j.Logger;

import com.ardor3d.extension.ui.UIButton;
import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.extension.ui.UIFrameBar;
import com.ardor3d.extension.ui.UILabel;
import com.ardor3d.extension.ui.backdrop.GradientBackdrop;
import com.ardor3d.extension.ui.border.ImageBorder;
import com.ardor3d.extension.ui.border.UIBorder;
import com.ardor3d.extension.ui.skin.generic.GenericSkin;
import com.ardor3d.extension.ui.util.Alignment;
import com.ardor3d.extension.ui.util.Insets;
import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceSource;

public class VerveDarkSkin extends GenericSkin {
    private static final Logger logger = Logger.getLogger(VerveDarkSkin.class);

    protected ReadOnlyColorRGBA[] foreground = new ReadOnlyColorRGBA[] { ColorRGBA.WHITE, ColorRGBA.YELLOW, ColorRGBA.LIGHT_GRAY };

    public VerveDarkSkin() {
        try {
            _sharedTex = VerveArdorSkin.getTex("verveDarkSkin.png");
        }
        catch(Throwable t) {
            logger.warn("", t);
        }
    }

    public VerveDarkSkin(final String skinTexture) {
        loadTexture(skinTexture);
    }

    public VerveDarkSkin(final ResourceSource skinTexture) {
        loadTexture(skinTexture);
    }

    @Override
    protected void loadTexture(final String skinTexture) {
        try {
            _sharedTex = TextureManager.load(skinTexture, MinificationFilter.BilinearNoMipMaps,
                    TextureStoreFormat.GuessNoCompressedFormat, false);
        } 
        catch (final Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void loadTexture(final ResourceSource skinTexture) {
        try {
            _sharedTex = TextureManager.load(skinTexture, MinificationFilter.BilinearNoMipMaps,
                    TextureStoreFormat.GuessNoCompressedFormat, false);
        } 
        catch (final Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void applyToFrame(final UIFrame component) {
        super.applyToFrame(component);
        final UIFrameBar titleBar = component.getTitleBar();
        titleBar.getTitleLabel().setForegroundColor(ColorRGBA.LIGHT_GRAY);
    }

    @Override
    protected void applyToLabel(final UILabel component) {
        super.applyToLabel(component);
        component.getDefaultState().setForegroundColor(foreground[0]);
        component.getDisabledState().setForegroundColor(foreground[1]);
    }
    
    @Override
    protected void applyToButton(final UIButton component) {
        component.setAlignment(Alignment.MIDDLE);
        component.setMargin(new Insets(1, 1, 1, 1));
        component.setPadding(new Insets(2, 14, 2, 14));
        // State values...
        final UIBorder defaultBorder = new ImageBorder(
        // left
                new SubTex(_sharedTex, 47, 11, 4, 10),
                // right
                new SubTex(_sharedTex, 77, 11, 4, 10),
                // top
                new SubTex(_sharedTex, 51, 7, 26, 4),
                // bottom
                new SubTex(_sharedTex, 51, 21, 26, 4),
                // top left
                new SubTex(_sharedTex, 47, 7, 4, 4),
                // top right
                new SubTex(_sharedTex, 77, 7, 4, 4),
                // bottom left
                new SubTex(_sharedTex, 47, 21, 4, 4),
                // bottom right
                new SubTex(_sharedTex, 77, 21, 4, 4));

        final UIBorder overBorder = new ImageBorder(
        // left
                new SubTex(_sharedTex, 47, 33, 4, 10),
                // right
                new SubTex(_sharedTex, 77, 33, 4, 10),
                // top
                new SubTex(_sharedTex, 51, 29, 26, 4),
                // bottom
                new SubTex(_sharedTex, 51, 43, 26, 4),
                // top left
                new SubTex(_sharedTex, 47, 29, 4, 4),
                // top right
                new SubTex(_sharedTex, 77, 29, 4, 4),
                // bottom left
                new SubTex(_sharedTex, 47, 43, 4, 4),
                // bottom right
                new SubTex(_sharedTex, 77, 43, 4, 4));

        final UIBorder pressedBorder = new ImageBorder(
        // left
                new SubTex(_sharedTex, 47, 55, 4, 10),
                // right
                new SubTex(_sharedTex, 77, 55, 4, 10),
                // top
                new SubTex(_sharedTex, 51, 51, 26, 4),
                // bottom
                new SubTex(_sharedTex, 51, 65, 26, 4),
                // top left
                new SubTex(_sharedTex, 47, 51, 4, 4),
                // top right
                new SubTex(_sharedTex, 77, 51, 4, 4),
                // bottom left
                new SubTex(_sharedTex, 47, 65, 4, 4),
                // bottom right
                new SubTex(_sharedTex, 77, 65, 4, 4));

        final ColorRGBA upTop    = new ColorRGBA(110 / 255f, 110 / 255f, 110 / 255f, 1);
        final ColorRGBA upBottom = new ColorRGBA(105 / 255f, 105 / 255f, 105 / 255f, 1);
        final GradientBackdrop upBack = new GradientBackdrop(upTop, upTop, upBottom, upBottom);
        final ColorRGBA downTop    = new ColorRGBA(100 / 255f, 100 / 255f, 100 / 255f, 1);
        final ColorRGBA downBottom = new ColorRGBA(110 / 255f, 110 / 255f, 110 / 255f, 1);
        final GradientBackdrop downBack = new GradientBackdrop(downTop, downTop, downBottom, downBottom);
        // DEFAULT
        {
            component.getDefaultState().setBorder(defaultBorder);
            component.getDefaultState().setBackdrop(upBack);
            component.getDefaultState().setForegroundColor(ColorRGBA.WHITE);
        }
        // DISABLED
        {
            component.getDisabledState().setBorder(defaultBorder);
            component.getDisabledState().setBackdrop(upBack);
            component.getDisabledState().setForegroundColor(ColorRGBA.GRAY);

            component.getDisabledSelectedState().setBorder(pressedBorder);
            component.getDisabledSelectedState().setBackdrop(downBack);
            component.getDisabledSelectedState().setForegroundColor(ColorRGBA.GRAY);
        }
        // MOUSE OVER
        {
            final ColorRGBA top = new ColorRGBA(130 / 255f, 130 / 255f, 130 / 255f, 1);
            final ColorRGBA bottom = new ColorRGBA(125 / 255f, 125 / 255f, 125 / 255f, 1);
            final GradientBackdrop back = new GradientBackdrop(top, top, bottom, bottom);

            component.getMouseOverState().setBorder(overBorder);
            component.getMouseOverState().setBackdrop(back);
            component.getMouseOverState().setForegroundColor(ColorRGBA.WHITE);
        }
        // PRESSED AND SELECTED
        {
            component.getPressedState().setBorder(pressedBorder);
            component.getPressedState().setBackdrop(downBack);
            component.getPressedState().setForegroundColor(ColorRGBA.GRAY);

            component.getSelectedState().setBorder(pressedBorder);
            component.getSelectedState().setBackdrop(downBack);
            component.getSelectedState().setForegroundColor(ColorRGBA.WHITE);

            component.getMouseOverSelectedState().setBorder(pressedBorder);
            component.getMouseOverSelectedState().setBackdrop(downBack);
            component.getMouseOverSelectedState().setForegroundColor(ColorRGBA.YELLOW);
        }
    }
}
