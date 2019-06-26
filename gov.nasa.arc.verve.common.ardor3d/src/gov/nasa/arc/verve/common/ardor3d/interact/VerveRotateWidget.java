package gov.nasa.arc.verve.common.ardor3d.interact;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.IFilterList;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.InteractRing;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MoveWidget;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.RotateWidget;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.ardor3d.framework.VerveBucketType;
import gov.nasa.arc.verve.common.interact.VerveInteractable;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;

public class VerveRotateWidget extends RotateWidget {

    protected InteractRing m_xRing = null;
    protected InteractRing m_yRing = null;
    protected InteractRing m_zRing = null;
    
    public VerveRotateWidget(IFilterList filterList) {
        super(filterList);
    }
    
    @Override
    public RotateWidget withXAxis(final ReadOnlyColorRGBA color, final float scale, final float width) {
        super.withXAxis(color, scale, width);
        
        if (m_xRing != null) {
            m_xRing.removeFromParent();
        }
        m_xRing = new InteractRing("xRotRingB", 4, 32, scale, width);
        m_xRing.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        final Quaternion rotate = new Quaternion().fromAngleAxis(MathUtils.HALF_PI, Vector3.UNIT_Y);
        m_xRing.getMeshData().rotatePoints(rotate);
        m_xRing.getMeshData().rotateNormals(rotate);
        m_xRing.setRenderState(VerveWidget.alwaysZState);
        m_xRing.getSceneHints().setAllPickingHints(false);
        m_xRing.getSceneHints().setRenderBucketType(VerveBucketType.PreOrtho);
        _handle.attachChild(m_xRing);
        return this;
    }
    
    @Override
    public RotateWidget withYAxis(final ReadOnlyColorRGBA color, final float scale, final float width) {
        super.withYAxis(color, scale, width);

        if (m_yRing != null) {
            m_yRing.removeFromParent();
        }
        m_yRing = new InteractRing("yRotRingB", 2, 32, scale, width);
        m_yRing.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        final Quaternion rotate = new Quaternion().fromAngleAxis(MathUtils.HALF_PI, Vector3.NEG_UNIT_X);
        m_yRing.getMeshData().rotatePoints(rotate);
        m_yRing.getMeshData().rotateNormals(rotate);
        m_yRing.setRenderState(VerveWidget.alwaysZState);
        m_yRing.getSceneHints().setAllPickingHints(false);
        m_yRing.getSceneHints().setRenderBucketType(VerveBucketType.PreOrtho);
        _handle.attachChild(m_yRing);
        return this;
    }
    
    @Override
    public RotateWidget withZAxis(final ReadOnlyColorRGBA color, final float scale, final float width) {
        super.withZAxis(color, scale, width);
        if (m_zRing != null) {
            m_zRing.removeFromParent();
        }
        m_zRing = new InteractRing("yRotRingB", 2, 32, scale, width);
        m_zRing.setDefaultColor(VerveWidget.alwaysZColor(color, new ColorRGBA()));
        m_zRing.setRenderState(VerveWidget.alwaysZState);
        m_zRing.getSceneHints().setAllPickingHints(false);
        m_zRing.getSceneHints().setRenderBucketType(VerveBucketType.PreOrtho);
        _handle.attachChild(m_zRing);
        return this;
    }
    
    @Override
    protected void setRingRotations(final ReadOnlyMatrix3 rot) {
        if (_xRing != null) {
            _xRing.setRotation(rot);
            m_xRing.setRotation(rot);
        }
        if (_yRing != null) {
            _yRing.setRotation(rot);
            m_yRing.setRotation(rot);
        }
        if (_zRing != null) {
            _zRing.setRotation(rot);
            m_zRing.setRotation(rot);
        }
    }

    @Override
    public void setTexture(final Texture2D texture) {
        if (_xRing != null) {
            _xRing.setTexture(texture);
            m_xRing.setTexture(texture);
        }
        if (_yRing != null) {
            _yRing.setTexture(texture);
            m_yRing.setTexture(texture);
        }
        if (_zRing != null) {
            _zRing.setTexture(texture);
            m_zRing.setTexture(texture);
        }
    }

    public RotateWidget withRingTexture(Texture2D tex) {
        setTexture(tex);
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

}
