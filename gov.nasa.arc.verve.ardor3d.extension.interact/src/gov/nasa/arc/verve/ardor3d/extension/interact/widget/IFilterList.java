package gov.nasa.arc.verve.ardor3d.extension.interact.widget;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.filter.UpdateFilter;

/**
 * This is a deviation from the Ardor3D interact implementation. In that 
 * scheme, there was no clean way to capture all of beginDrag, endDrag, and 
 * applyFilters. begin/endDrag happened in the leaf widgets (e.g. MoveWidget) and 
 * applyFilters would happen in the leaf and compound widgets. This scheme, the
 * compound widget can implement IFilerList and pass itself to leaf widgets, who
 * will then forward calls to the parent list. 
 *
 */
public interface IFilterList extends Iterable<UpdateFilter> {
    
    public void         applyFilters(final InteractManager manager);
    public void         beginDrag(final InteractManager manager);
    public void         endDrag(final InteractManager manager);

    public int          size();
    public UpdateFilter get(int index);
    
    public boolean      add(final UpdateFilter filter);
    public boolean      remove(final UpdateFilter filter);
    public void         clear();
}
