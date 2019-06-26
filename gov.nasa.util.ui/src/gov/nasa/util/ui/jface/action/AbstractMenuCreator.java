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

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


/**
 * Provides caching of popup/dropdown menus and only requires
 * the user to implement fillMenu. User should call disposeMenuItems
 * at the beginning of the fillMenu call to properly dispose of 
 * the previous menu items
 */
public abstract class AbstractMenuCreator implements IMenuCreator {
    private Menu           m_popup = null;
    private Menu           m_dropdown = null;

    /** 
     * IMPORTANT: implementation MUST invoke disposeMenuItems() as the
     * first call in this method in order to properly dispose of 
     * any previous MenuItems
     */
    abstract protected void fillMenu(Menu menu);

    @Override
    public void dispose() {
        disposeMenu(m_popup);
        disposeMenu(m_dropdown);
    }

    protected void disposeMenuItems(Menu menu) {
        if(!menu.isDisposed()) {
            MenuItem[] menuItems = menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
        }
    }

    protected void disposeMenu(Menu menu) {
        if(menu != null) {
            disposeMenuItems(menu);
            menu.dispose();
        }
    }

    @Override
    public Menu getMenu(Control parent) {
        if(m_popup == null) {
            m_popup = new Menu(parent.getShell(), SWT.POP_UP);
        }
        fillMenu(m_popup);
        return m_popup;
    }
    
    @Override
    public Menu getMenu(Menu parent) {
        if(m_dropdown == null)
            m_dropdown = new Menu(parent.getShell(), SWT.DROP_DOWN);
        fillMenu(m_dropdown);
        return m_dropdown;
    }

    public void refreshDropdown() {
        if(m_dropdown != null) {
            fillMenu(m_dropdown);
        }
    }
}
