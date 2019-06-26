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
package gov.nasa.dds.rti.ui.jface.action;

import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.ui.RtiDdsUiActivator;
import gov.nasa.util.ui.jface.action.AbstractMenuCreator;
import gov.nasa.util.ui.jface.action.ActionCombo;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Creates a combobox Action that allows user to select a
 * DDS DomainParticipant from the current list of participantIds.
 * User must implement the participantChanged() method
 * @author mallan
 */
public abstract class ParticipantActionCombo extends ActionCombo implements IMenuListener {
    //private static final Logger logger = Logger.getLogger(ParticipantActionCombo.class);
    
    protected String m_participant;

    /**
     * @param initialParticipantId
     * @param shell getViewSite().getShell()
     */
    public ParticipantActionCombo(String initialParticipantId) {
        super("Participant", "Select DDS participant");
        m_participant = initialParticipantId;
        setImageDescriptor(RtiDdsUiActivator.getImageDescriptor("dds-participant"));
        setMenuCreator(new ParticipantMenuCreator());
    }

    /**
     * callback for when new participantId is selected
     * @param newParticipant
     */
    public abstract void participantChanged(String newParticipant);

    /**
     * Because RCP is *&&^* broken, we have to add this as a menu listener so that
     * we can rebuild the menu when the Action is inserted to the ActionBar menu manager. 
     * The MenuManager SHOULD call the MenuCreator.getMenu() whenever the menu is
     * shown, but it does NOT - it only calls it once, which breaks the MenuCreator contract. Sweet. 
     */
    public void menuAboutToShow(IMenuManager manager) {
        //logger.debug("ParticipantActionCombo menuAboutToShow");
        AbstractMenuCreator amc = (AbstractMenuCreator)getMenuCreator();
        amc.refreshDropdown();
    }

    //===========================================
    protected class ParticipantMenuCreator extends AbstractMenuCreator {
        @Override
        protected void fillMenu(Menu menu) {
            //logger.debug("ParticipantActionCombo ParticipantMenuCreator.fillMenu");
            disposeMenuItems(menu);
            for(final String participantId : DdsEntityFactory.getValidParticipantIds()) {
                final MenuItem item = new MenuItem(menu, SWT.RADIO);
                item.setText(participantId);
                item.setSelection(participantId.equals(m_participant));
                item.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        if(item.getSelection()) {
                            if(!participantId.equals(m_participant)) {
                                m_participant = participantId;
                                participantChanged(participantId);
                            }
                        }
                    }
                } );
            }
        }
    }
}
