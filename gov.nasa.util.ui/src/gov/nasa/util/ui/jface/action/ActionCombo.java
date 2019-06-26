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
package gov.nasa.util.ui.jface.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Action that will show menu when clicked if event is
 * initiated by a ToolItem
 * @author mallan
 *
 */
public class ActionCombo extends Action {

    public ActionCombo(String text, String tooltipText) {
        super(text, Action.AS_DROP_DOWN_MENU);
        setText(text);
        setToolTipText(tooltipText);
    }

    @Override
    public void runWithEvent(Event e) {
        showMenu(e);
    }

    protected void showMenu(Event e) {
        if(e.type == SWT.Selection && e.widget instanceof ToolItem) {
            final ToolItem ti = (ToolItem)e.widget;
            final ToolBar  tb = ti.getParent();
            final IMenuCreator mc = this.getMenuCreator();
            if(mc != null) {
                Menu menu =  mc.getMenu(tb);
                if(menu != null) {
                    final Rectangle rect = ti.getBounds();
                    final Point location = tb.toDisplay(new Point(rect.x, rect.y + rect.height));
                    menu.setLocation(location);
                    menu.setVisible(true);
                }
            }
        }
    }

}
