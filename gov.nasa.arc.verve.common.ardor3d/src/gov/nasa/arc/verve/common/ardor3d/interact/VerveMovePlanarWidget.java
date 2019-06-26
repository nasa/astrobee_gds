package gov.nasa.arc.verve.common.ardor3d.interact;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.IFilterList;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MovePlanarWidget;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MoveWidget;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.interact.VerveInteractable;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.ZBufferState.TestFunction;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Cylinder;

public class VerveMovePlanarWidget extends MovePlanarWidget {
    protected Matrix3 _calc = new Matrix3();
    protected Matrix3 _rot  = new Matrix3();

    protected boolean _mouseWheelRotate = false;

    public VerveMovePlanarWidget(IFilterList filterList) {
        super(filterList);
    }

    protected static final ZBufferState s_zState;
    static {
        s_zState = new ZBufferState();
        s_zState.setFunction(TestFunction.Always);
        s_zState.setWritable(false);
    }
    

    @Override
    public MovePlanarWidget withDefaultHandle(final double radius, final double height, final ReadOnlyColorRGBA color) {
        return withDefaultHandle(radius, height, color, false);
    }
    
    public MovePlanarWidget withDefaultHandle(final double radius, final double height, 
                                              final ReadOnlyColorRGBA color, 
                                              boolean enableMouseWheelRotate) {
        _mouseWheelRotate = enableMouseWheelRotate;
        Cylinder handle = new Cylinder("handle", 2, 16, radius, height, true);
        handle.setDefaultColor(color);
        switch (_plane) {
        case XZ:
            handle.setRotation(new Matrix3().fromAngleNormalAxis(MathUtils.HALF_PI, Vector3.UNIT_X));
            break;
        case YZ:
            handle.setRotation(new Matrix3().fromAngleNormalAxis(MathUtils.HALF_PI, Vector3.UNIT_Y));
            break;
        default:
            // do nothing
            break;
        }
        handle.updateModelBound();
        _handle.attachChild(handle);

        handle = new Cylinder("handleB", 2, 16, radius, height, true);
        handle.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        handle.setRenderState(VerveWidget.alwaysZState);
        switch (_plane) {
        case XZ:
            handle.setRotation(new Matrix3().fromAngleNormalAxis(MathUtils.HALF_PI, Vector3.UNIT_X));
            break;
        case YZ:
            handle.setRotation(new Matrix3().fromAngleNormalAxis(MathUtils.HALF_PI, Vector3.UNIT_Y));
            break;
        default:
            // do nothing
            break;
        }
        handle.updateModelBound();
        _handle.attachChild(handle);

        return this;
    }

    @Override
    public void targetChanged(final InteractManager manager) {
        if (_dragging) {
            endDrag(manager);
        }
        final Spatial target = manager.getSpatialTarget();
        if (target != null) {
            BoundingVolume  twb  = target.getWorldBound();
            ReadOnlyVector3 wxyz = target.getWorldTranslation();
            double scale = Math.max(MoveWidget.MIN_SCALE, 
                                    twb.getRadius() + wxyz.subtract(twb.getCenter(), _calcVec3A).length());
            VerveInteractable vi = VerveUserData.getInteractable(target);
            if(vi != null) {
                scale = scale*vi.getWidgetSize();
            } 
            _handle.setScale(scale);
        }
        targetDataUpdated(manager);
    }

    @Override
    public void processInput(final Canvas source, final TwoInputStates inputStates, final AtomicBoolean inputConsumed, final InteractManager manager) {
        if(_mouseWheelRotate) {
            final MouseState current = inputStates.getCurrent().getMouseState();
            // mouse wheel rotates
            if(current.getDwheel() != 0) {
                float amt = current.getDwheel() * 0.04f;
                _rot.setIdentity();
                switch(_plane) {
                case XY: _rot.applyRotationZ(amt); break;
                case XZ: _rot.applyRotationY(amt); break;
                case YZ: _rot.applyRotationX(amt); break;
                }
                final Transform transform = manager.getSpatialState().getTransform();
                _rot.multiply(transform.getMatrix(), _calc);
                transform.setRotation(_calc);
                applyFilters(manager);
                inputConsumed.set(true);
                return;
            }
        }
        super.processInput(source, inputStates, inputConsumed, manager);

    }


}
