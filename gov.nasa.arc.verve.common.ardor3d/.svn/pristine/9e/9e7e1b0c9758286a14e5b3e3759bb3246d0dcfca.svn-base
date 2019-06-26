package gov.nasa.arc.verve.common.ardor3d.interact.filter;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;

/**
 * Filters out translations that are too large for a 
 * single iteration. @see AbstractVerveUpdateFilter.filterMaxMove()
 */
public class NoOpFilter extends AbstractVerveUpdateFilter {
    
    @Override
    public void applyFilter(InteractManager manager) {
        filterMaxMove(manager);
    }

    @Override
    public void beginDrag(InteractManager manager) {
        // NO OP
    }

    @Override
    public void endDrag(InteractManager manager) {
        // NO OP        
    }

}
