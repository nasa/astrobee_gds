package gov.nasa.arc.verve.common.ardor3d.interact;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.AbstractInteractWidget;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.interact.VerveInteractable;

import java.util.EnumSet;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.scenegraph.Spatial;


/**
 * VerveInteractManager adds the ability to select widgets by id and 
 * works with VerveInteractManagers to synchronize interaction state 
 * between multiple VerveInteractManager instances 
 */
public class VerveInteractManager extends InteractManager {
    public static final VerveInteractManager INSTANCE = new VerveInteractManager();
    protected int m_prefW = 0;
    
    public VerveInteractManager() {
        super();
        VerveInteractManagers.INSTANCE.addInstance(this);
    }
    
    @Override
    protected void offerInputToWidgets(final Canvas source, final TwoInputStates inputStates) {
        EnumSet<Key> kr = inputStates.getCurrent().getKeyboardState().getKeysReleasedSince(inputStates.getPrevious().getKeyboardState());
        // if escape has been pressed, remove interact widgets
        if(kr.contains(Key.ESCAPE)) {
            VerveInteractManagers.INSTANCE.setSpatialTarget(null);
        }
        // if tab has been pressed, cycle through preferred widgets
        if(kr.contains(Key.TAB)) {
            VerveInteractable vi = VerveUserData.getInteractable(_spatialTarget);
            if(vi != null) {
                int mod = vi.preferredWidgets().length;
                m_prefW = (m_prefW+1)%mod;
                setActiveWidget(vi.preferredWidgets()[m_prefW]);
            }
        }
        super.offerInputToWidgets(source, inputStates);
    }
    
    /**
     * Get widget based on id. Only VerveCompoundInteractWidgets have ids.
     */
    public AbstractInteractWidget getWidget(final String id) {
        for(AbstractInteractWidget widget : _widgets) {
            if(widget instanceof VerveCompoundInteractWidget) {
                VerveCompoundInteractWidget w = (VerveCompoundInteractWidget)widget;
                final String wid = w.getId();
                if(id.equals(wid)) {
                    return widget;
                }
            }
        }
        return null;
    }

    /**
     * Set active widget based on id.
     */
    public void setActiveWidget(final String id) {
        AbstractInteractWidget widget = getWidget(id);
        if(id != null) {
            this.setActiveWidget(widget);
        }
    }

    @Override
    public void setSpatialTarget(final Spatial target) {
        Spatial oldTarget = this.getSpatialTarget();
        setSpatialTargetImpl(target);
        VerveInteractManagers.INSTANCE.setSpatialTarget(target, this);
        VerveInteractManagers.INSTANCE.fireInteractTargetChanged(oldTarget, target);
    }
    
    void setSpatialTargetImpl(final Spatial target) {
        VerveInteractable vi = VerveUserData.getInteractable(target);
        if(vi != null) {
            final int prefW = m_prefW;
            final int numW  = vi.preferredWidgets().length;
            for(int i = 0; i < numW; i++) {
                m_prefW = (prefW+i)%numW;
                String wid = vi.preferredWidgets()[m_prefW];
                AbstractInteractWidget widget = getWidget(wid);
                if(widget != null) {
                    setActiveWidget(widget);
                    break;
                }
            }
        }
        super.setSpatialTarget(target);
    }
    
    /**
     * calls fireTargetDataUpdated() then updates other VerveInteractManagers
     * by calling VerveInteractManagers.fireTargetChanged()
     */
    @Override
    public void fireTargetDataUpdated() {
        super.fireTargetDataUpdated();        
        VerveInteractManagers.INSTANCE.fireTargetChanged(this);
    }
        
}

