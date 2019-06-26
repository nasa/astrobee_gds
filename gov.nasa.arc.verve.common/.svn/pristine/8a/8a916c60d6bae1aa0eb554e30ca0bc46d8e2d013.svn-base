package gov.nasa.arc.verve.common.interact;

import gov.nasa.arc.irg.util.undo.UndoableHistoryManager;

import java.util.Set;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.Spatial;


/**
 * 
 * @author mallan
 *
 */
public class VerveInteractable {
    //private static final Logger logger = Logger.getLogger(VerveInteractable.class);

    protected boolean  m_enabled = true;
    protected String[] m_preferredWidgets = new String[0];
    protected float    m_widgetSize = 1;

    protected ReadOnlyTransform m_oldTransform; // store the last transform state of the spatial before edit started

    public VerveInteractable() {
        //
    }

    public VerveInteractable(String[] preferredWidgets) {
        this(preferredWidgets, 1);
    }

    public VerveInteractable(String[] preferredWidgets, float sizeScale) {
        m_preferredWidgets = preferredWidgets;
        m_widgetSize = sizeScale;
    }

    public boolean isEnabled() {
        return m_enabled;
    }
    public void setEnabled(boolean state) {
        m_enabled = state;
    }

    /**
     * @return array containing the ids of the preferred widgets
     * for interacting with this Spatial. 
     */
    public String[] preferredWidgets() {
        return m_preferredWidgets;
    }

    /**
     * 
     * @param transform SpatialState transform held by InteractManager, post widget filters
     * XXX NOTE: This is currently the only hook for the Interactable to get information 
     * about how it's being manipulated during dragging. Perhaps there should be 
     * another call with a more appropriate method name. 
     * @return
     */
    public String getDisplayString(ReadOnlyTransform transform) {
        return null;
    }

    /** 
     * Callback when interact drag begin. Default behavior is to save 
     * spatial state for undo operations. 
     */
    public void beginInteractDrag(Spatial spatial) {
        //logger.debug("beginInteractDrag");
        m_oldTransform = spatial.getTransform().clone();
    }

    /** 
     * Callback when interact drag ends. Default behavior is to push the 
     * modification onto the undo stack if transform has been changed.
     */
    public void endInteractDrag(Spatial spatial) {
        //logger.debug("endInteractDrag");
        final ReadOnlyTransform newTransform = spatial.getTransform();
        if(!newTransform.equals(m_oldTransform)) {
            // FIXME: undo at this level cannot call downstream VerveInteractManagers.fireTargetChanged() in order
            // to update widgets when transform is changed. This should be refactored to be an interface, and the
            // abstract implementation should go into gov.nasa.arc.verve.ardor3d.interact
            UndoableHistoryManager.INSTANCE.add(new UndoableSpatialModification(spatial, m_oldTransform, newTransform.clone()));
        }
    }

    /** Callback when interact is clicked */
    public void interactClicked(Spatial spatial, MouseButton button, Canvas canvas, TwoInputStates inputStates) {
        //logger.debug("interactClicked");
    }

    /** 
     * Callback when a key is pressed while cursor is over interact widget. 
     * Normally, pressedKeys will only have one Key in the set (the key that was most 
     * recently pressed). To detect multi-key presses (such as Ctrl-T), get the 
     * keyboard state from inputStates
     */
    public void interactKeyPress(Spatial spatial, Set<Key> pressedKeys, Canvas canvas, TwoInputStates inputStates) {
        //logger.debug("interactKeyPress");
    }

    public void setWidgetSize(float size) {
        m_widgetSize = size;
    }

    public float getWidgetSize() {
        return m_widgetSize;
    }


}
