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
package gov.nasa.arc.verve.ui3d.widgets;

import com.ardor3d.extension.ui.StateBasedUIComponent;
import com.ardor3d.extension.ui.UIState;
import com.ardor3d.extension.ui.util.Dimension;
import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.extension.ui.util.SubTexUtil;
import com.ardor3d.input.InputState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.renderer.Renderer;
import com.google.common.collect.ImmutableSet;

/**
 * fixed size icon 
 * @author mallan
 *
 */
public class UIIconButton extends StateBasedUIComponent {
    private SubTex[]          m_icons          = null;
    private int               m_iconIndex      = 0;
    private final Dimension   m_iconDimensions = new Dimension(32,32);
    private final int[]       m_sx             = new int[2];

    protected final IconState m_stateDefault   = new IconStateDefault();
    protected final IconState m_stateDisabled  = new IconState();
    protected final IconState m_stateMouseOver = new IconStateMouseOver();
    protected final IconState m_statePressed   = new IconStatePressed();

    protected Runnable m_onLeftClickRunnable   = null;
    protected Runnable m_onRightClickRunnable  = new Runnable() {
        @Override
        public void run() {
            int index = (m_iconIndex+1)%m_icons.length;
            setIconIndex(index);
        }
    };

    public UIIconButton(final SubTex[] icons) {
        switchState(m_stateDefault);
        m_icons = icons;
    }

    public UIIconButton(final SubTex icon) {
        SubTex[] icons = new SubTex[] { icon };
        switchState(m_stateDefault);
        m_icons = icons;
    }

    public UIIconButton() {
        switchState(m_stateDefault);
        m_icons = null;
    }

    public Runnable getOnLeftClickRunnable() {
        return m_onLeftClickRunnable;
    }

    public Runnable getOnRightClickRunnable() {
        return m_onRightClickRunnable;
    }

    public void setOnLeftClickRunnable(Runnable runnable) {
        m_onLeftClickRunnable = runnable;
    }

    public void setOnRightClickRunnable(Runnable runnable) {
        m_onRightClickRunnable = runnable;
    }

    @Override
    public void updateMinimumSizeFromContents() {
        setMinimumContentSize(m_iconDimensions.getWidth(), m_iconDimensions.getHeight());
        setMaximumContentSize(m_iconDimensions.getWidth(), m_iconDimensions.getHeight());
        fireComponentDirty();
    }

    @Override
    protected void updateChildren(final double time) {
        super.updateChildren(time);
    }

    /**
    *
    */
   public UIIconButton setIcon(final SubTex icon) {
       SubTex[] icons = new SubTex[] { icon };
       return setIcons(icons);
   }

   /**
   *
   */
  public UIIconButton setIcons(final SubTex[] icons) {
      m_icons = icons;
      fireComponentDirty();
      return this;
  }

    public UIIconButton setIconIndex(final int index) {
        m_iconIndex = index;
        fireComponentDirty();
        return this;
    }

    public UIIconButton setDimensions(int w, int h) {
        m_iconDimensions.set(w, h);
        fireComponentDirty();
        return this;
    }

    public UIIconButton setDimensions(Dimension d) {
        return setDimensions(d.getWidth(), d.getHeight());
    }

    @Override
    protected void drawComponent(final Renderer renderer) {
        if (m_icons != null) {
            final IconState state = (IconState)getCurrentState();
            final double x = getTotalLeft() + state.getPosisionOffset();
            final double y = getTotalBottom() + state.getPosisionOffset();
            final double w = m_iconDimensions.getWidth() + state.getSizeOffset();
            final double h = m_iconDimensions.getHeight() + state.getSizeOffset();
            m_icons[m_iconIndex].setTint(state.getTint());
            SubTexUtil.drawSubTex(renderer, m_icons[m_iconIndex], 
                    x, y, w, h, 
                    getWorldTransform());
        }

    }

    @Override
    public void mouseEntered(final int mouseX, final int mouseY, final InputState state) {
        super.mouseEntered(mouseX, mouseY, state);
        //m_icons[m_iconIndex].setTint(ColorRGBA.WHITE);
        m_sx[0] = 3;
        m_sx[1] = 6;
    }

    @Override
    public void mouseDeparted(final int mouseX, final int mouseY, final InputState state) {
        super.mouseDeparted(mouseX, mouseY, state);
        //m_icons[m_iconIndex].setTint(ColorRGBA.LIGHT_GRAY);
        m_sx[0] = 0;
        m_sx[1] = 0;
    }


    @Override
    public UIState getDefaultState() {
        return m_stateDefault;
    }

    @Override
    public UIState getDisabledState() {
        return m_stateDisabled;
    }

    public UIState getMouseOverState() {
        return m_stateMouseOver;
    }

    public UIState getPressedState() {
        return m_statePressed;
    }

    @Override
    public ImmutableSet<UIState> getStates() {
        return ImmutableSet.of(
                (UIState)m_stateDefault,
                (UIState)m_stateDisabled);
    }

    public class IconStateDefault extends IconState {
        public IconStateDefault() {
            super(0,0, IconState.DEFAULT_TINT);
        }
        @Override
        public void mouseEntered(final int mouseX, final int mouseY, final InputState state) {
            switchState(getMouseOverState());
        }
        @Override
        public boolean mousePressed(final MouseButton button, final InputState state) {
            switchState(getPressedState());
            return true;
        }
    }

    public class IconStateMouseOver extends IconState {
        public IconStateMouseOver() {
            super(-2,4, IconState.ACTIVE_TINT);
        }
        @Override
        public void mouseDeparted(final int mouseX, final int mouseY, final InputState state) {
            switchState(getDefaultState());
        }
        @Override
        public boolean mousePressed(final MouseButton button, final InputState state) {
            switchState(getPressedState());
            return true;
        }
    }

    public class IconStatePressed extends IconState {
        public IconStatePressed() {
            super(0, 0, IconState.PRESSED_TINT);
        }
        @Override
        public void mouseDeparted(final int mouseX, final int mouseY, final InputState state) {
            switchState(getDefaultState());
        }
        @Override
        public boolean mouseReleased(final MouseButton button, final InputState state) {
            switch(button) {
            case LEFT:
                if(m_onLeftClickRunnable != null) {
                    m_onLeftClickRunnable.run();
                }
                break;
            case RIGHT:
                if(m_onRightClickRunnable != null) {
                    m_onRightClickRunnable.run();
                }
                break;
            default:
                break;
            }
            switchState(getMouseOverState());
            return true;
        }
    }
}
