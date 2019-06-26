package gov.nasa.arc.verve.common.ardor3d.interact;

import com.ardor3d.scenegraph.Spatial;

/**
 * Interface to be notified if interact target has changed
 */
public interface IVerveInteractTargetChangedListener {
    void interactTargetChanged(Spatial oldTarget, Spatial newTarget);
}
