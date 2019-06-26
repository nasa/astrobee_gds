package gov.nasa.arc.verve.common.ardor3d.interact;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.IFilterList;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MoveMultiPlanarWidget;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MoveWidget;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.interact.VerveInteractable;

import java.nio.FloatBuffer;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.renderer.state.ShadingState;
import com.ardor3d.renderer.state.ShadingState.ShadingMode;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.ZBufferState.TestFunction;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.geom.BufferUtils;

public class VerveMoveMultiPlanarWidget extends MoveMultiPlanarWidget {

    public VerveMoveMultiPlanarWidget(IFilterList filterList) {
        this(filterList, 0.5);
    }

    public VerveMoveMultiPlanarWidget(IFilterList filterList, final double extent) {
        super(filterList);
        _handle = new Node("moveHandle");

        final BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        _handle.setRenderState(bs);

        final ZBufferState zs = new ZBufferState();
        zs.setFunction(TestFunction.LessThanOrEqualTo);
        _handle.setRenderState(zs);

        final CullState cs = new CullState();
        cs.setCullFace(Face.Back);
        _handle.setRenderState(cs);

        _handle.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
        _handle.updateGeometricState(0);

        createDefaultHandle(extent);
    }

    @Override
    protected void createDefaultHandle(final double extent) {
        final Box grip = new Box("grip", Vector3.ZERO, extent, extent, extent);
        grip.updateModelBound();
        _handle.attachChild(grip);

        ColorRGBA MAGENTA = new ColorRGBA(ColorRGBA.MAGENTA);
        ColorRGBA CYAN    = new ColorRGBA(ColorRGBA.CYAN);
        ColorRGBA YELLOW  = new ColorRGBA(ColorRGBA.YELLOW);
        MAGENTA.setAlpha(0.5f);
        CYAN.setAlpha(0.5f);
        YELLOW.setAlpha(0.5f);
        // setup some colors, just at the corner of the primitives since we will use flat shading.
        grip.setSolidColor(ColorRGBA.WHITE);
        final FloatBuffer colors = grip.getMeshData().getColorBuffer();
        BufferUtils.setInBuffer(YELLOW,  colors,  0);
        BufferUtils.setInBuffer(CYAN,    colors,  4);
        BufferUtils.setInBuffer(YELLOW,  colors,  8);
        BufferUtils.setInBuffer(CYAN,    colors, 12);
        BufferUtils.setInBuffer(MAGENTA, colors, 16);
        BufferUtils.setInBuffer(MAGENTA, colors, 20);

        // set flat shading
        final ShadingState shade = new ShadingState();
        shade.setShadingMode(ShadingMode.Flat);
        grip.setRenderState(shade);

        // setup a material state to use the colors from the vertices.
        final MaterialState material = new MaterialState();
        material.setColorMaterial(ColorMaterial.Diffuse);
        grip.setRenderState(material);
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
