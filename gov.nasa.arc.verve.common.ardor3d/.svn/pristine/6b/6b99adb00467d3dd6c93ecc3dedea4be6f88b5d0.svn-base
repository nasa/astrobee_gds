package gov.nasa.arc.verve.common.ardor3d.interact;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import com.ardor3d.scenegraph.Spatial;

/**
 * Singleton to coordinate multiple InteractManagers 
 * Each view has its own InteractManager 
 */
public class VerveInteractManagers {
    public static final VerveInteractManagers INSTANCE = new VerveInteractManagers();
    
    protected final List<WeakReference<VerveInteractManager>> m_managers = new LinkedList<WeakReference<VerveInteractManager>>();
    protected final List<IVerveInteractTargetChangedListener> m_listeners = new LinkedList<IVerveInteractTargetChangedListener>();
    
    public void addInstance(VerveInteractManager manager) {
        WeakReference<VerveInteractManager> ref;
        ref = new WeakReference<VerveInteractManager>(manager);
        m_managers.add(ref);
    }
    
    public void fireTargetChanged(final VerveInteractManager initiator) {
        for(WeakReference<VerveInteractManager> ref : m_managers) {
            VerveInteractManager manager = ref.get();
            if(manager != null && manager != initiator) {
                manager.fireTargetChanged();
            }
        }
    }
    
    /**
     * set spatial target for all VerveInteractManagers
     * @param target
     */
    public void setSpatialTarget(final Spatial target) {
        // get current target from one of the managers. 
        Spatial oldTarget = null;
        for(WeakReference<VerveInteractManager> ref : m_managers) {
            VerveInteractManager manager = ref.get();
            if(manager != null) {
                if(manager.getSpatialTarget() != null) 
                    oldTarget = manager.getSpatialTarget();
            }
        }
        setSpatialTarget(target, null);
        fireInteractTargetChanged(oldTarget, target);
    }
    
    public void addInteractTargetChangedListener(IVerveInteractTargetChangedListener listener) {
        if(!m_listeners.contains(listener)) {
            m_listeners.add(listener);
        }
    }
    
    public boolean removeInteractTargetChangedListener(IVerveInteractTargetChangedListener listener) {
        return m_listeners.remove(listener);
    }
    
    /**
     * PACKAGE VISIBILITY ONLY
     * set spatial target for all VerveInteractManagers except initiator
     * @param target
     * @param initiator
     */
    void setSpatialTarget(final Spatial target, final VerveInteractManager initiator) {
        List<WeakReference<VerveInteractManager>> remove = new LinkedList<WeakReference<VerveInteractManager>>();
        for(WeakReference<VerveInteractManager> ref : m_managers) {
            VerveInteractManager manager = ref.get();
            if(manager == null) {
                remove.add(ref);
            }
            else {
                if(manager != initiator)
                    manager.setSpatialTargetImpl(target);
            }
        }
        m_managers.removeAll(remove);
    }

    /**
     * PACKAGE VISIBILITY ONLY
     * @param oldTarget
     * @param newTarget
     */
    void fireInteractTargetChanged(Spatial oldTarget, Spatial newTarget) {
        for(IVerveInteractTargetChangedListener listener : m_listeners) {
            listener.interactTargetChanged(oldTarget, newTarget);
        }
    }
}
