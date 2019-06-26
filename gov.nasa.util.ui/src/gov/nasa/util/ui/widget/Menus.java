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
package gov.nasa.util.ui.widget;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

public class Menus {

    /**
     * return a new Menu that will auto-dispose when hidden
     */
    public static Menu autoDisposingMenu(Composite composite) {
        final Menu menu = new Menu(composite);
        MenuListener disposer = new MenuListener() {
            final Menu auto = menu;
            @Override
            public void menuHidden(MenuEvent e) {
                final MenuListener ml = this;
                Display.getDefault().asyncExec(new Runnable() { 
                    @Override 
                    public void run() { 
                        auto.removeMenuListener(ml);
                        auto.dispose();
                    } 
                }); 
            }
            @Override
            public void menuShown(MenuEvent e) {
                // don't care
            }
        };
        menu.addMenuListener(disposer);
        return menu;
    }
}
