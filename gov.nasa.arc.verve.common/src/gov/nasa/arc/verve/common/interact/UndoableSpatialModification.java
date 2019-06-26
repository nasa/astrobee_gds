package gov.nasa.arc.verve.common.interact;

import gov.nasa.arc.irg.util.undo.IUndoable;
import gov.nasa.arc.irg.util.undo.UndoStatus;

import java.lang.ref.WeakReference;

import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.Spatial;

/**
 * Support for undo and redo of a spatial modification
 * @author tecohen
 *
 */
public class UndoableSpatialModification implements IUndoable {
	
	protected ReadOnlyTransform m_newValue;	// the new value
	protected ReadOnlyTransform m_oldValue;	// the old value
	protected WeakReference<Spatial> m_spatialReference; // the spatial
	
	public UndoableSpatialModification(Spatial spatial, ReadOnlyTransform oldTransform, ReadOnlyTransform newTransform){
		assert(spatial != null);
		assert(oldTransform != null);
		assert(newTransform != null);
		m_oldValue = oldTransform;
		m_newValue = newTransform;
		m_spatialReference = new WeakReference<Spatial>(spatial);
	}

	@Override
	public boolean canRedo() {
		return (m_spatialReference.get() != null);
	}

	@Override
	public boolean canUndo() {
		return (m_spatialReference.get() != null);
	}

	@Override
	public void dispose() {
		m_spatialReference.clear();
		m_spatialReference = null;
		m_newValue = null;
		m_oldValue = null;
	}

	@Override
	public String getLabel() {
		return "Move Spatial " + m_spatialReference.get().getName();
	}

	@Override
	public UndoStatus redo(Object info) throws Exception {
		m_spatialReference.get().setTransform(m_newValue);
		return UndoStatus.OK;
	}

	@Override
	public UndoStatus undo(Object info) throws Exception {
		m_spatialReference.get().setTransform(m_oldValue);
		return UndoStatus.OK;
	}

}
