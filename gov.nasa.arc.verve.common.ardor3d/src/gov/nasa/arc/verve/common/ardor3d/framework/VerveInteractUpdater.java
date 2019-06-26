package gov.nasa.arc.verve.common.ardor3d.framework;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;

import com.ardor3d.framework.Updater;
import com.ardor3d.util.ReadOnlyTimer;

public class VerveInteractUpdater implements Updater {
    final InteractManager m_manager;
    
    public VerveInteractUpdater(InteractManager manager) {
        m_manager = manager;
    }
    
    @Override
    public void init() {
        //
    }

    @Override
    public void update(ReadOnlyTimer timer) {
        m_manager.update(timer);
    }

}
