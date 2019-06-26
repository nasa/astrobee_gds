package gov.nasa.arc.verve.common.ardor3d.interact.filter;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.data.SpatialState;
import gov.nasa.arc.verve.common.ardor3d.util.RotUtil;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;

/**
 * 
 */
public class YawNotchFilter extends AbstractVerveUpdateFilter {    
    protected final Vector3   m_lastPosDiff = new Vector3();
    protected final Vector3   m_lastRotDiff = new Vector3();

    private float rotNotch = (float)Math.PI/4.0f;  //
    private float rotSnap  = (float)Math.PI/45.0f; // notch width
    private float rotSnapHi= rotNotch-rotSnap;

    public YawNotchFilter() {
    }

    @Override
    public void applyFilter(final InteractManager manager) {    
        filterMaxMove(manager);
        final SpatialState state = manager.getSpatialState();

        if(state.getTransform().isRotationMatrix()) {
            RotUtil.Order order = RotUtil.Order.XYZ;
            Vector3 rpy = RotUtil.toEuler(order, state.getTransform().getMatrix(), new Vector3());
            rpy.addLocal(m_lastRotDiff);
            
            float rot    = rpy.getZf();
            float rotMod = rot%rotNotch;
            if(Math.abs(rotMod) < rotSnap) { 
                m_lastRotDiff.set(0, 0, rotMod);
                rpy.subtractLocal(m_lastRotDiff);
            }
            else if(rotMod > rotSnapHi) {
                float add = rotNotch-rotMod;
                m_lastRotDiff.set(0, 0, -add);
                rpy.subtractLocal(m_lastRotDiff);
            }
            else if(rotMod < -rotSnapHi) {
                float add = -rotNotch-rotMod;
                m_lastRotDiff.set(0, 0, -add);
                rpy.subtractLocal(m_lastRotDiff);
            }
            else {
                m_lastRotDiff.set(0, 0, 0);
            }

            //rpy.subtractLocal(m_lastRotDiff);
            Matrix3 m33 = RotUtil.toMatrix(order, rpy, new Matrix3());
            state.getTransform().setRotation(m33);
        }
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
