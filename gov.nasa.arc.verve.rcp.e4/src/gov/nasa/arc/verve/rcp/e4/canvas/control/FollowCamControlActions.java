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
package gov.nasa.arc.verve.rcp.e4.canvas.control;

import gov.nasa.arc.irg.util.ui.UiTarget;
import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.input.control.AbstractCamControl;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.util.ui.jface.action.ActionCombo;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 *
 */
public class FollowCamControlActions implements ICamControlUiEclipsePlugin {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(FollowCamControlActions.class);

    private final AbstractCamControl m_camControl;
    private final Action m_targetAction;
    private final Action m_modeAction;
    private final Shell  m_shell;

    private TargetMenuCreator m_targetMenuCreator = null;
    private ModeMenuCreator   m_modeMenuCreator   = null;

    protected InterestPointProvider m_ipp;

    /**
     * 
     * @param camControl
     * @param shell
     */
    public FollowCamControlActions(AbstractCamControl camControl, Shell shell) {
        m_shell = shell;
        m_camControl = camControl;

        // XXX COMMENTFORNOW
        m_targetAction = new ActionCombo("target", "Choose a target to follow");
//        m_targetAction.setImageDescriptor(VerveArdor3dActivator.getImageDescriptorFromRegistry("target"));
        m_modeAction = new ActionCombo("mode", "Choose camera mode");
//        m_modeAction.setImageDescriptor(VerveArdor3dActivator.getImageDescriptorFromRegistry("gear"));

        camControl.setCamControlUi(this);
    }

    /**
     * when Actions are removed from an action bar, eclipse 
     * automatically destroys the menu creator. Which is sheer *genius*. 
     * So we have to keep creating new ones... I wonder what happens when 
     * an ActionContributionItem is removed from one toolbar and not
     * another? 
     */
    protected void createNewMenuCreators() {
        m_modeMenuCreator = new ModeMenuCreator(m_modeAction, m_shell);
        m_modeAction.setMenuCreator(m_modeMenuCreator);
        m_targetMenuCreator = new TargetMenuCreator(m_targetAction, m_shell);
        m_targetAction.setMenuCreator(m_targetMenuCreator);
    }

    /**
     * Allocates new ActionContributionItems to wrap our Actions. 
     * Each toolbar must have its own ActionContributionItem to
     * represent an Action, so we keep creating new contribution items
     * and menu creators. How efficient. 
     * @return
     */
    public ActionContributionItem[] getActionItems() {
        createNewMenuCreators();
        ActionContributionItem[] actionItems = new ActionContributionItem[2];
        actionItems[0] = new ActionContributionItem(m_targetAction);
        actionItems[1] = new ActionContributionItem(m_modeAction);
        return actionItems;
    }

    public void setFollow(InterestPointProvider ipp) {
        if(m_ipp != ipp) {
            //            Menu menu = m_targetMenuCreator.getMenu();
            //            if(menu != null) {
            //                int numItems = menu.getItemCount();
            //                for(int i = 0; i < numItems; i++) {
            //                    if(menu.getItem(i).getText().equals(ipp.getInterestPointName())) {
            //                        menu.getItem(i).setSelection(true);
            //                    }
            //                    else {
            //                        // super awesome that I have to explicitly turn off
            //                        // the items that aren't selected...
            //                        menu.getItem(i).setSelection(false);
            //                    }
            //                }
            //            }
            interestPointSelected(ipp);
            m_ipp = ipp;
        }
    }

    protected void interestPointSelected(InterestPointProvider ipp) {
        //logger.debug("interestPointSelected:"+ipp.getInterestPointName());
        m_camControl.setCenterOfInterest(ipp);
        if(m_modeMenuCreator != null) {
        	// what is this anyway? 11/14/14
        	m_modeMenuCreator.setInterestPointProvider(ipp);
        }
        Ardor3D.setPreference(Ardor3D.P_LAST_TARGET, ipp.getInterestPointName());
        UiTarget.targetChanged(ipp.getInterestPointName());
    }

    public InterestPointProvider findInterestPointProvider(String name) {
        if(name != null) {
            List<InterestPointProvider> ippList = m_camControl.getInterestPointProviders();
            for(final InterestPointProvider ipp : ippList) {
                if(name.equals(ipp.getInterestPointName())) {
                    return ipp;
                }
            }
        }
        return null;
    }

    //=====================================================
    class TargetMenuCreator implements IMenuCreator {
        final Action m_action;
        final Menu   m_menu;
        TargetMenuCreator(Action action, Shell shell) {
            m_action = action;
            m_menu = new Menu(shell, SWT.POP_UP);
            fillMenu();
        }
        @Override
        public void dispose() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
        }

        public Menu getMenu() {
            fillMenu();
            return m_menu; 
        }
        @Override
        public Menu getMenu(Control parent) {
            fillMenu();
            return m_menu;
        }
        @Override
        public Menu getMenu(Menu parent) {
            fillMenu();
            return m_menu;
        }

        private void fillMenu() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
            List<InterestPointProvider> ippList = m_camControl.getInterestPointProviders();
            String ippName = null;
            try { ippName = m_camControl.getIp().provider.getInterestPointName(); }
            catch(Throwable t) { /**/ }
            Collections.sort(ippList);
            for(final InterestPointProvider ipp : ippList) {
                MenuItem item = new MenuItem(m_menu, SWT.RADIO);
                final String finalName = new String(ipp.getInterestPointName());
                item.setText(finalName);
                item.setSelection(finalName.equals(ippName));
                item.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        interestPointSelected(ipp);
                    }
                } );
            }
        }
    }


    //=====================================================
    class ModeMenuCreator implements IMenuCreator {
        final Action m_action;
        final Menu   m_menu;
        InterestPointProvider m_ipp;
        ModeMenuCreator(Action action, Shell shell) {
            m_action = action;
            m_menu = new Menu(shell, SWT.POP_UP);
        }
        public void setInterestPointProvider(final InterestPointProvider ipp) {
            m_ipp = ipp;
        }
        @Override
        public void dispose() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
        }
        public Menu getMenu() { 
            if(m_ipp == null)
                return null;
            createMenu(m_ipp);
            return m_menu; 
        }
        @Override
        public Menu getMenu(Control parent) {
            return getMenu();
        }
        @Override
        public Menu getMenu(Menu parent) {
            return getMenu();
        }
        public void createMenu(final InterestPointProvider ipp) {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
            final String currentMode = m_camControl.getInterestPointMode();
            String [] modes = ipp.getInterestPointModes();
            for(final String mode : modes) {
                MenuItem item = new MenuItem(m_menu, SWT.RADIO);
                item.setText(mode);
                item.setSelection(mode.equals(currentMode));
                item.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        m_camControl.setInterestPointMode(mode);
                    }
                } );
            }
        }
    }
}
