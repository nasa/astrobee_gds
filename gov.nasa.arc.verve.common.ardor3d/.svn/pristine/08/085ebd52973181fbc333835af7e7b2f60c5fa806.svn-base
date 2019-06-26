package gov.nasa.arc.verve.common.ardor3d.pssm;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.ardor3d.scenegraph.Spatial;

public class ShadowCasters {
    public static final ShadowCasters INSTANCE = new ShadowCasters();
    
    protected List<WeakReference<Spatial>> m_spatialRefs = new ArrayList<WeakReference<Spatial>>();
    protected List<WeakReference<Spatial>> m_cleanup = new ArrayList<WeakReference<Spatial>>();
    
    
    public void addSpatial(Spatial spatial) {
        for(WeakReference<Spatial> ref : m_spatialRefs) {
            Spatial spat = ref.get();
            if(spat == null) 
                m_cleanup.add(ref);
            if(spat == spatial) 
                return;
        }
        m_spatialRefs.add(new WeakReference<Spatial>(spatial));
        m_spatialRefs.removeAll(m_cleanup);
        m_cleanup.clear();
    }

    public void removeSpatial(Spatial spatial) {
        for(WeakReference<Spatial> ref : m_spatialRefs) {
            Spatial spat = ref.get();
            if(spat == spatial) {
                m_cleanup.add(ref);
            }
        }
        m_spatialRefs.removeAll(m_cleanup);
        m_cleanup.clear();
    }
    
    List<WeakReference<Spatial>> getSpatialRefs() {
        return m_spatialRefs;
    }
}
