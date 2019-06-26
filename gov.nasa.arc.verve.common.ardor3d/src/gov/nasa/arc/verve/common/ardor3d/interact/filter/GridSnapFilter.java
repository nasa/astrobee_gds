package gov.nasa.arc.verve.common.ardor3d.interact.filter;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.data.SpatialState;
import gov.nasa.arc.verve.common.ardor3d.util.RotUtil;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;

/**
 * 
 */
public class GridSnapFilter extends AbstractVerveUpdateFilter {
    protected final Vector3   m_lastPosDiff = new Vector3();
    protected final Vector3   m_lastRotDiff = new Vector3();

    private float rotSnap = (float)Math.PI/12f;
    private float posSnap =  1.0f;

    public GridSnapFilter() {
    }

    @Override
    public void applyFilter(final InteractManager manager) {    
        filterMaxMove(manager);
        final SpatialState state = manager.getSpatialState();

        if(state.getTransform().isRotationMatrix()) {
            //RotUtil.Order order = RotUtil.Order.XYZ;
            RotUtil.Order order = RotUtil.Order.ZYX;
            Vector3 rpy = RotUtil.toEuler(order, state.getTransform().getMatrix(), new Vector3());
            rpy.addLocal(m_lastRotDiff);
            m_lastRotDiff.set(rpy.getXf()%rotSnap,
                              rpy.getYf()%rotSnap,
                              rpy.getZf()%rotSnap);
            rpy.subtractLocal(m_lastRotDiff);
            Matrix3 rot = RotUtil.toMatrix(order, rpy, new Matrix3());
            state.getTransform().setRotation(rot);
        }
        final Vector3 trans = new Vector3(state.getTransform().getTranslation());
        trans.addLocal(m_lastPosDiff);
        m_lastPosDiff.set(trans.getXf()%posSnap, 
                          trans.getYf()%posSnap, 
                          trans.getZf()%posSnap);
        trans.subtractLocal(m_lastPosDiff);

        state.getTransform().setTranslation(trans);
    }

    @Override
    public void beginDrag(InteractManager manager) {
        m_lastRotDiff.set(0,0,0);
        m_lastPosDiff.set(0,0,0);
    }

    @Override
    public void endDrag(InteractManager manager) {        
        m_lastRotDiff.set(0,0,0);
        m_lastPosDiff.set(0,0,0);
    }

}
