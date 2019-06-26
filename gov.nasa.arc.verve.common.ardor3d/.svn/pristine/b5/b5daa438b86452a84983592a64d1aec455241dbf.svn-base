package gov.nasa.arc.verve.common.ardor3d.interact.filter;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.data.SpatialState;
import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.common.ardor3d.util.SpatialPath;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;

public class BaseMapHeightFilter extends AbstractVerveUpdateFilter {
    //private static final Logger logger = Logger.getLogger(BaseMapHeightFilter.class);
    
    protected       float     m_zOff      = 0;
    protected boolean         m_zOffValid = false;
    protected final Transform m_fromWorld = new Transform();
    protected final Transform m_toWorld   = new Transform();
    protected final Vector3   m_lastWorldPos = new Vector3();
    protected final Transform m_lastXfm   = new Transform();
    protected final Transform m_worldXfm  = new Transform();
    protected SpatialPath     m_path      = new SpatialPath();

    public BaseMapHeightFilter() {
    }

    @Override
    public void applyFilter(final InteractManager manager) {    
        filterMaxMove(manager);
        if(m_zOffValid) {
            final SpatialState state = manager.getSpatialState();
            m_fromWorld.multiply(state.getTransform(), m_worldXfm);
            final ReadOnlyVector3 trans = m_worldXfm.getTranslation();
            final float x = trans.getXf();
            final float y = trans.getYf();
            final float z = trans.getZf();
            final float height = VerveBaseMap.getHeightAt(x, y);
            if(height == height) {
                if(m_lastWorldPos.distance(trans) > 10) {
                    state.getTransform().set(m_lastXfm);
                }
                else if (height != z) {
                    m_worldXfm.translate(0, 0, (height+m_zOff) - z);
                    m_toWorld.multiply(m_worldXfm, m_lastXfm);
                    state.getTransform().set(m_lastXfm);
                }
                m_lastWorldPos.set(trans);
            }
        }
    }

    @Override
    public void beginDrag(InteractManager manager) {        
        Spatial spatial = manager.getSpatialTarget();
        if(spatial == null)
            return;
        m_path.findPath(spatial, null, false);
        m_path.getTransform(m_fromWorld);
        m_fromWorld.invert(m_toWorld);

        m_lastXfm.set(spatial.getTransform());
        m_fromWorld.multiply(spatial.getTransform(), m_worldXfm);

        m_lastWorldPos.set(m_worldXfm.getTranslation());
        final float spaX = m_lastWorldPos.getXf();
        final float spaY = m_lastWorldPos.getYf();
        final float spaZ = m_lastWorldPos.getZf();
        final float mapZ = VerveBaseMap.getHeightAt(spaX, spaY);
        m_zOff = spaZ - mapZ;
        // check for NaN (as would happen if we don't have basemap or basemap is invalid here)
        m_zOffValid = (m_zOff==m_zOff) ? true : false;
    }

    @Override
    public void endDrag(InteractManager manager) {        
        m_lastWorldPos.set(0,0,0);
        m_zOff = 0;
    }

}
