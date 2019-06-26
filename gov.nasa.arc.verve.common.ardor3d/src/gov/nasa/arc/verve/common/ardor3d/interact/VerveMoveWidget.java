package gov.nasa.arc.verve.common.ardor3d.interact;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.IFilterList;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.InteractArrow;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MoveWidget;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.interact.VerveInteractable;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.input.MouseState;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Spatial;

public class VerveMoveWidget extends MoveWidget {

    protected double        m_moveScale = 0.5;
    
    protected InteractArrow m_xArrow = null;
    protected InteractArrow m_yArrow = null;
    protected InteractArrow m_zArrow = null;

    public VerveMoveWidget(IFilterList filterList) {
        super(filterList);
        // set default colors
    }

    @Override
    public MoveWidget withXAxis(final ReadOnlyColorRGBA color, final double scale, final double width,
                                final double lengthGap, final double tipGap) {
        super.withXAxis(color, scale, width, lengthGap,tipGap);
        if (m_xArrow != null) {
            m_xArrow.removeFromParent();
        }
        m_xArrow = new InteractArrow("xMoveArrowB", scale, width, lengthGap, tipGap);
        m_xArrow.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        final Quaternion rotate = new Quaternion().fromAngleAxis(MathUtils.HALF_PI, Vector3.UNIT_Y);
        m_xArrow.setRotation(rotate);
        m_xArrow.setRenderState(VerveWidget.alwaysZState);
        m_xArrow.getSceneHints().setAllPickingHints(false);
        _handle.attachChild(m_xArrow);
        return this;
    }

    @Override
    public MoveWidget withYAxis(final ReadOnlyColorRGBA color, final double scale, final double width,
                                final double lengthGap, final double tipGap) {
        super.withYAxis(color, scale, width, lengthGap,tipGap);
        if (m_yArrow != null) {
            m_yArrow.removeFromParent();
        }
        m_yArrow = new InteractArrow("yMoveArrowB", scale, width, lengthGap, tipGap);
        m_yArrow.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        m_yArrow.setRenderState(VerveWidget.alwaysZState);
        final Quaternion rotate = new Quaternion().fromAngleAxis(MathUtils.HALF_PI, Vector3.NEG_UNIT_X);
        m_yArrow.setRotation(rotate);
        m_yArrow.getSceneHints().setAllPickingHints(false);
        _handle.attachChild(m_yArrow);
        return this;
    }

    @Override
    public MoveWidget withZAxis(final ReadOnlyColorRGBA color, final double scale, final double width,
                                final double lengthGap, final double tipGap) {
        super.withZAxis(color, scale, width, lengthGap,tipGap);
        if (m_zArrow != null) {
            m_zArrow.removeFromParent();
        }
        m_zArrow = new InteractArrow("zMoveArrowB", scale, width, lengthGap, tipGap);
        m_zArrow.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        m_zArrow.setRenderState(VerveWidget.alwaysZState);
        m_zArrow.getSceneHints().setAllPickingHints(false);
        _handle.attachChild(m_zArrow);
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
    protected Vector3 getNewOffset(final InteractArrow arrow, 
                                   final Vector2 oldMouse, final MouseState current,
                                   final Camera camera, 
                                   final InteractManager manager) {
        Vector3 retVal = new Vector3(super.getNewOffset(arrow, oldMouse, current, camera, manager));
        retVal.multiplyLocal(m_moveScale);
        return retVal;
    }

}
