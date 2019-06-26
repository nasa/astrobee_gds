/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package gov.nasa.arc.verve.common.ardor3d.interact;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.filter.UpdateFilter;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.AbstractInteractWidget;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.BasicFilterList;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.IFilterList;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.InteractMatrix;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MovePlanarWidget;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MovePlanarWidget.MovePlane;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.MoveWidget;
import gov.nasa.arc.verve.ardor3d.extension.interact.widget.RotateWidget;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.ardor3d.framework.VerveBucketType;
import gov.nasa.arc.verve.common.ardor3d.interact.filter.NoOpFilter;
import gov.nasa.arc.verve.common.ardor3d.text.BMFont;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.common.ardor3d.text.BMText.AutoFade;
import gov.nasa.arc.verve.common.ardor3d.text.BMText.AutoScale;
import gov.nasa.arc.verve.common.interact.VerveInteractable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.framework.Canvas;
import com.ardor3d.image.Texture2D;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.util.ReadOnlyTimer;
import com.google.common.collect.Maps;

public class VerveCompoundInteractWidget extends AbstractInteractWidget implements IFilterList {
    private final Logger logger = Logger.getLogger(VerveCompoundInteractWidget.class);

    private static final String MOVE_KEY = "Move";
    private static final String ROTATE_KEY = "Rotate";
    private static final String MOVE_PLANAR_KEY = "MovePlanar";
    private static final String MOVE_MULTIPLANAR_KEY = "MoveMultiPlanar";

    public static double MIN_SCALE = 0.000001;

    protected MouseButton _clickButton = MouseButton.RIGHT;

    protected Map<String, AbstractInteractWidget> _widgets = Maps.newHashMap();

    protected AbstractInteractWidget _lastInputWidget = null;

    protected InteractMatrix _interactMatrix;

    protected final String _id;

    protected MouseState _clickCheck;
    protected MouseState _scrollCheck;
    protected boolean    _scrollDrag = false;

    protected Node   _markup;
    protected BMText _text;
    static BMFont font;

    public VerveCompoundInteractWidget(String id) {
        super(new BasicFilterList());
        _id = id;
        _handle = new Node("handleRoot");
        // always have at least 1 filter
        addFilter(new NoOpFilter());

        _markup = new Node("markup");

        if(font == null) {
            font = BMFontManager.newFont(BMFontManager.FontStyle.SansMedium.fontName);
            //font = BMFontManager.newFont("DroidSans-20-bold-regular-outline2");

            //            RenderStateSetter setter = font.new RenderStateSetter(font.getPageTexture(), true) {
            //                {
            //                    zBuffState.setFunction(TestFunction.Always);
            //                }
            //                @Override
            //                public void applyTo(final Spatial spatial) {
            //                    logger.debug("applyTo...");
            //                    spatial.setRenderState(textureState);
            //                    spatial.setRenderState(blendState);
            //                    spatial.setRenderState(zBuffState);
            //                    spatial.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
            //                }
            //            };
            //            font.setBlendStateSetter(setter);
        }

        _text = new BMText("VerveCompoundInteractWidget-text",
                           "",
                           font,
                           BMText.Align.SouthWest,
                           BMText.Justify.Left);
        _text.setAutoRotate(true);
        _text.setAutoFade(AutoFade.Off);
        _text.setAutoScale(AutoScale.FixedScreenSize);
        //_text.setFontScale(0.985);
        _text.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
        _text.getSceneHints().setCullHint(CullHint.Always);
        _text.setUseBlend(true);
        // set ZBuffer state after states are set in setUseBlend()
        _text.getSceneHints().setRenderBucketType(VerveBucketType.Transparent);
        ZBufferState zBuffState = new ZBufferState();
        zBuffState.setFunction(ZBufferState.TestFunction.Always);
        _text.setRenderState(zBuffState);

        _markup.attachChild(_text);
    }

    protected void updateTextOffset(double targetScale) {
        _text.setFixedOffset(3*targetScale, -3*targetScale);
        //_text.setFixedOffset(3*targetScale, 0);
    }
    public String getId() {
        return _id;
    }

    @Override
    public void addFilter(final UpdateFilter filter) {
        // if we have NoOpFilter, remove it
        if(_filters.size() == 1) {
            UpdateFilter maybeNoOp = _filters.get(0);
            if(maybeNoOp instanceof NoOpFilter) {
                doRemoveFilter(maybeNoOp, false);
            }
        }

        for(final AbstractInteractWidget widget : _widgets.values()) {
            widget.addFilter(filter);
        }
        super.addFilter(filter);
    }

    @Override
    public void removeFilter(final UpdateFilter filter) {
        doRemoveFilter(filter, true);
    }

    private void doRemoveFilter(final UpdateFilter filter, boolean addNoOpIf0) {
        for(final AbstractInteractWidget widget : _widgets.values()) {
            widget.removeFilter(filter);
        }
        super.removeFilter(filter);
        // if we have no filters left, add a NoOpFilter
        if(addNoOpIf0 && _filters.size() == 0) {
            addFilter(new NoOpFilter());
        }
    }

    @Override
    public void clearFilters() {
        for(final AbstractInteractWidget widget : _widgets.values()) {
            widget.clearFilters();
        }
        super.clearFilters();
        // always have at least 1 filter
        addFilter(new NoOpFilter());
    }

    public VerveCompoundInteractWidget withMoveXAxis() {
        verifyMoveWidget().withXAxis();
        return this;
    }

    public VerveCompoundInteractWidget withMoveXAxis(final ReadOnlyColorRGBA color) {
        verifyMoveWidget().withXAxis(color);
        return this;
    }

    public VerveCompoundInteractWidget withMoveXAxis(final ReadOnlyColorRGBA color, final double scale, final double width,
                                                     final double lengthGap, final double tipGap) {
        verifyMoveWidget().withXAxis(color, scale, width, lengthGap, tipGap);
        return this;
    }

    public VerveCompoundInteractWidget withMoveYAxis() {
        verifyMoveWidget().withYAxis();
        return this;
    }

    public VerveCompoundInteractWidget withMoveYAxis(final ReadOnlyColorRGBA color) {
        verifyMoveWidget().withYAxis(color);
        return this;
    }

    public VerveCompoundInteractWidget withMoveYAxis(final ReadOnlyColorRGBA color, final double scale, final double width,
                                                     final double lengthGap, final double tipGap) {
        verifyMoveWidget().withYAxis(color, scale, width, lengthGap, tipGap);
        return this;
    }

    public VerveCompoundInteractWidget withMoveZAxis() {
        verifyMoveWidget().withZAxis();
        return this;
    }

    public VerveCompoundInteractWidget withMoveZAxis(final ReadOnlyColorRGBA color) {
        verifyMoveWidget().withZAxis(color);
        return this;
    }

    public VerveCompoundInteractWidget withMoveZAxis(final ReadOnlyColorRGBA color, final double scale, final double width,
                                                     final double lengthGap, final double tipGap) {
        verifyMoveWidget().withZAxis(color, scale, width, lengthGap, tipGap);
        return this;
    }

    public VerveCompoundInteractWidget withRotateXAxis() {
        verifyRotateWidget().withXAxis();
        return this;
    }

    public VerveCompoundInteractWidget withRotateXAxis(final ReadOnlyColorRGBA color) {
        verifyRotateWidget().withXAxis(color);
        return this;
    }

    public VerveCompoundInteractWidget withRotateXAxis(final ReadOnlyColorRGBA color, final float scale, final float width) {
        verifyRotateWidget().withXAxis(color, scale, width);
        return this;
    }

    public VerveCompoundInteractWidget withRotateYAxis() {
        verifyRotateWidget().withYAxis();
        return this;
    }

    public VerveCompoundInteractWidget withRotateYAxis(final ReadOnlyColorRGBA color) {
        verifyRotateWidget().withYAxis(color);
        return this;
    }

    public VerveCompoundInteractWidget withRotateYAxis(final ReadOnlyColorRGBA color, final float scale, final float width) {
        verifyRotateWidget().withYAxis(color, scale, width);
        return this;
    }

    public VerveCompoundInteractWidget withRotateZAxis() {
        verifyRotateWidget().withZAxis();
        return this;
    }

    public VerveCompoundInteractWidget withRotateZAxis(final ReadOnlyColorRGBA color) {
        verifyRotateWidget().withZAxis(color);
        return this;
    }

    public VerveCompoundInteractWidget withRotateZAxis(final ReadOnlyColorRGBA color, final float scale, final float width) {
        verifyRotateWidget().withZAxis(color, scale, width);
        return this;
    }

    public VerveCompoundInteractWidget withRingTexture(final Texture2D texture) {
        verifyRotateWidget().setTexture(texture);
        return this;
    }

    public VerveCompoundInteractWidget withMultiPlanarHandle() {
        VerveMoveMultiPlanarWidget widget = (VerveMoveMultiPlanarWidget) _widgets
                .get(VerveCompoundInteractWidget.MOVE_MULTIPLANAR_KEY);
        if (widget != null) {
            widget.getHandle().removeFromParent();
        }

        widget = new VerveMoveMultiPlanarWidget(this);
        _widgets.put(VerveCompoundInteractWidget.MOVE_MULTIPLANAR_KEY, widget);
        _handle.attachChild(widget.getHandle());

        return this;
    }

    public VerveCompoundInteractWidget withMultiPlanarHandle(final double extent) {
        VerveMoveMultiPlanarWidget widget = (VerveMoveMultiPlanarWidget) _widgets
                .get(VerveCompoundInteractWidget.MOVE_MULTIPLANAR_KEY);
        if (widget != null) {
            widget.getHandle().removeFromParent();
        }

        widget = new VerveMoveMultiPlanarWidget(this, extent);
        _widgets.put(VerveCompoundInteractWidget.MOVE_MULTIPLANAR_KEY, widget);
        _handle.attachChild(widget.getHandle());

        return this;
    }

    public VerveCompoundInteractWidget withPlanarHandle(final MovePlane plane, final ReadOnlyColorRGBA color) {
        return withPlanarHandle(plane, color, false);
    }
    
    public VerveCompoundInteractWidget withPlanarHandle(final MovePlane plane, final ReadOnlyColorRGBA color, boolean mouseWheelRotate) {
        MovePlanarWidget widget = (VerveMovePlanarWidget) _widgets.get(VerveCompoundInteractWidget.MOVE_PLANAR_KEY);
        if (widget != null) {
            widget.getHandle().removeFromParent();
        }

        widget = new VerveMovePlanarWidget(this).withPlane(plane);
        ((VerveMovePlanarWidget)widget).withDefaultHandle(.5, .25, color, mouseWheelRotate);
        _widgets.put(VerveCompoundInteractWidget.MOVE_PLANAR_KEY, widget);
        _handle.attachChild(widget.getHandle());

        return this;
    }

    public VerveCompoundInteractWidget withPlanarHandle(final MovePlane plane, final ReadOnlyColorRGBA color,
                                                        boolean mouseWheelRotate,
                                                        final double radius, final double height) {
        MovePlanarWidget widget = (MovePlanarWidget) _widgets.get(VerveCompoundInteractWidget.MOVE_PLANAR_KEY);
        if (widget != null) {
            widget.getHandle().removeFromParent();
        }

        widget = new VerveMovePlanarWidget(this).withPlane(plane).withDefaultHandle(radius, height, color);
        _widgets.put(VerveCompoundInteractWidget.MOVE_PLANAR_KEY, widget);
        _handle.attachChild(widget.getHandle());

        return this;
    }

    private MoveWidget verifyMoveWidget() {
        VerveMoveWidget moveWidget = (VerveMoveWidget) _widgets.get(VerveCompoundInteractWidget.MOVE_KEY);
        if (moveWidget == null) {
            moveWidget = new VerveMoveWidget(this);
            _widgets.put(VerveCompoundInteractWidget.MOVE_KEY, moveWidget);
            _handle.attachChild(moveWidget.getHandle());
        }
        return moveWidget;
    }

    private RotateWidget verifyRotateWidget() {
        VerveRotateWidget rotateWidget = (VerveRotateWidget) _widgets.get(VerveCompoundInteractWidget.ROTATE_KEY);
        if (rotateWidget == null) {
            rotateWidget = new VerveRotateWidget(this);
            _widgets.put(VerveCompoundInteractWidget.ROTATE_KEY, rotateWidget);
            _handle.attachChild(rotateWidget.getHandle());
        }
        return rotateWidget;
    }

    @Override
    public void targetChanged(final InteractManager manager) {
        //logger.debug("targetChanged");
        for (final AbstractInteractWidget widget : _widgets.values()) {
            widget.targetChanged(manager);
        }

        // calculate scale for text offset
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
            this.updateTextOffset(scale);
        }
    }

    @Override
    public void targetDataUpdated(final InteractManager manager) {
        for (final AbstractInteractWidget widget : _widgets.values()) {
            widget.targetDataUpdated(manager);
        }
    }

    @Override
    public void receivedControl(final InteractManager manager) {
        for (final AbstractInteractWidget widget : _widgets.values()) {
            widget.receivedControl(manager);
        }
    }

    @Override
    public void render(final Renderer renderer, final InteractManager manager) {
        for (final AbstractInteractWidget widget : _widgets.values()) {
            widget.render(renderer, manager);
        }
        {
            final Spatial spat = manager.getSpatialTarget();
            if (spat == null) {
                return;
            }
            _markup.setTranslation(spat.getWorldTranslation());
            _markup.updateGeometricState(0);
            renderer.draw(_markup);
        }
    }

    //-- Process Input
    //-----------------------------------------------------------------------------------------
    @Override
    public void processInput(final Canvas source, 
                             final TwoInputStates inputStates, 
                             final AtomicBoolean inputConsumed,
                             final InteractManager manager) {
        // Make sure we have something to modify
        if (manager.getSpatialTarget() == null) {
            return;
        }
        // Make sure we are dragging or clicking.
        final MouseState current  = inputStates.getCurrent().getMouseState();
        final MouseState previous = inputStates.getPrevious().getMouseState();
        final Camera     camera   = source.getCanvasRenderer().getCamera();
        final Vector2    oldMouse = new Vector2(previous.getX(), previous.getY());

        // check for click
        if(previous.getButtonState(_clickButton) == ButtonState.DOWN && 
                current.getButtonState(_clickButton) != ButtonState.DOWN) {
            findPick(oldMouse, camera);
            if (_results.getNumber() > 0) {
                fireClicked(manager, _clickButton, source, inputStates);
            }
        }

        // check for key press
        final KeyboardState keyCurr = inputStates.getCurrent().getKeyboardState();
        final KeyboardState keyPrev = inputStates.getPrevious().getKeyboardState();
        Set<Key> pressed = keyCurr.getKeysPressedSince(keyPrev);
        if(!pressed.isEmpty()) {
            findPick(oldMouse, camera);
            if (_results.getNumber() > 0) {
                fireKeyPress(manager, pressed, source, inputStates);
            }
            return;
        }

        //-- check for scroll before we check for drag
        // We get the same mouse state every frame unless mouse state changes, so if we
        // detect scroll, hold onto mouse state and do not send the same mouse state to child
        // widget repeatedly. In order to get consistent behavior in VerveInteractables, 
        // scroll events invoke beginDrag() when scrolling begins, and endDrag() when we
        // receive a non-scroll mouse event.
        if(current.getDwheel() != 0 ) {
            if(_scrollCheck != current) {
                _scrollCheck = current;
                findPick(oldMouse, camera);
                if (_results.getNumber() > 0) {
                    final Spatial picked = (Spatial) _results.getPickData(0).getTarget();
                    if (picked != null) {
                        for (final AbstractInteractWidget widget : _widgets.values()) {
                            if (picked.hasAncestor(widget.getHandle())) {
                                if(!_scrollDrag) {
                                    _scrollDrag = true;
                                    beginDrag(manager);
                                }
                                widget.processInput(source, inputStates, inputConsumed, manager);
                                if(inputConsumed.get()) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            if(_scrollDrag) {
                _scrollDrag = false;
                endDrag(manager);
            }
        }

        if (current.getButtonState(_dragButton) != ButtonState.DOWN) {
            if (_lastInputWidget != null) {
                _lastInputWidget.processInput(source, inputStates, inputConsumed, manager);
                _lastInputWidget = null;
                //logger.debug("processInput - finished.");
            }
            if(previous.getButtonState(_dragButton) == ButtonState.DOWN) {
                if(_clickCheck != null) {
                    if(_clickCheck.getX()-current.getX() == 0) {
                        if(_clickCheck.getY()-current.getY() == 0) {
                            fireClicked(manager, _dragButton, source, inputStates);
                        }
                    }
                }
            }
            _clickCheck = null;
            return;
        }

        if (_lastInputWidget == null) {
            findPick(oldMouse, camera);
            if (_results.getNumber() <= 0) {
                return;
            }

            final Spatial picked = (Spatial) _results.getPickData(0).getTarget();
            if (picked == null) {
                return;
            }

            for (final AbstractInteractWidget widget : _widgets.values()) {
                if (picked.hasAncestor(widget.getHandle())) {
                    _lastInputWidget = widget;
                    break;
                }
            }
        }
        // check for click
        if(previous.getButtonState(_dragButton) == ButtonState.UP && 
                current.getButtonState(_dragButton) == ButtonState.DOWN) {
            _clickCheck = current;
        }

        _lastInputWidget.processInput(source, inputStates, inputConsumed, manager);


        // apply our filters, if any, now that we've made updates.
        // applyFilters(manager);
        // ^^^^ this should not be necessary because the child widget should have called it already
    }



    @Override
    public void setInteractMatrix(final InteractMatrix matrix) {
        _interactMatrix = matrix;
        for (final AbstractInteractWidget widget : _widgets.values()) {
            widget.setInteractMatrix(matrix);
        }
    }

    @Override
    public InteractMatrix getInteractMatrix() {
        return _interactMatrix;
    }

    @Override
    public void update(final ReadOnlyTimer timer, final InteractManager manager) {
        for (final AbstractInteractWidget widget : _widgets.values()) {
            widget.update(timer, manager);
        }
    }

    //== VerveInteractable callback invocations ========================================================
    /**
     * pass clicked event to VerveInteractable
     * @param manager
     * @param canvas
     * @param inputStates
     */
    public void fireClicked(final InteractManager manager, MouseButton button, final Canvas canvas, final TwoInputStates inputStates) {
        Spatial spatial = manager.getSpatialTarget();
        if(spatial != null) {
            VerveInteractable vi = VerveUserData.getInteractable(spatial);
            if(vi != null) {
                vi.interactClicked(spatial, button, canvas, inputStates);
            }
        }
    }
    
    public void fireKeyPress(final InteractManager manager, Set<Key> keysPressed, final Canvas canvas, final TwoInputStates inputStates) {
        Spatial spatial = manager.getSpatialTarget();
        if(spatial != null) {
            VerveInteractable vi = VerveUserData.getInteractable(spatial);
            if(vi != null) {
                vi.interactKeyPress(spatial, keysPressed, canvas, inputStates);
            }
        }
    }

    //-- IFilterList delegation ---------------------
    @Override
    public void applyFilters(final InteractManager manager) {
        _filters.applyFilters(manager);

        final Spatial spatial = manager.getSpatialTarget();
        VerveInteractable vi = VerveUserData.getInteractable(spatial);
        if(vi != null) {
            final String displayString = vi.getDisplayString(manager.getSpatialState().getTransform());
            if(displayString != null) {
                _text.setText(displayString);
            }
        }
    }

    @Override
    public void beginDrag(final InteractManager manager) {
        //invoke VerveInteractable callback
        final Spatial spatial = manager.getSpatialTarget();
        VerveInteractable vi = VerveUserData.getInteractable(spatial);
        if(vi != null) {
            vi.beginInteractDrag(spatial);
            final String displayString = vi.getDisplayString(manager.getSpatialState().getTransform());
            if(displayString != null) {
                _text.setText(displayString);
            }
        }

        _filters.beginDrag(manager);
        _text.getSceneHints().setCullHint(CullHint.Never);
    }

    @Override
    public void endDrag(final InteractManager manager) {
        _filters.endDrag(manager);
        _text.getSceneHints().setCullHint(CullHint.Always);

        //invoke VerveInteractable callback
        final Spatial spatial = manager.getSpatialTarget();
        VerveInteractable vi = VerveUserData.getInteractable(spatial);
        if(vi != null) {
            vi.endInteractDrag(spatial);
        }
    }

    public Iterator<UpdateFilter> iterator() {
        return _filters.iterator();
    }

    public int size() {
        return _filters.size();
    }

    public UpdateFilter get(int index) {
        return _filters.get(index);
    }

    public boolean add(UpdateFilter filter) {
        return _filters.add(filter);
    }

    public boolean remove(UpdateFilter filter) {
        return _filters.remove(filter);
    }

    public void clear() {
        _filters.clear();
    }

}
