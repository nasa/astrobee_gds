package gov.nasa.arc.verve.ardor3d.extension.interact.widget;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.filter.UpdateFilter;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class BasicFilterList implements IFilterList {
    final List<UpdateFilter> _filters = Lists.newArrayList();

    public Iterator<UpdateFilter> iterator() {
        return _filters.iterator();
    }

    public void applyFilters(InteractManager manager) {
        // apply any filters to our state
        for (final UpdateFilter filter : _filters) {
            filter.applyFilter(manager);
        }
    }

    public void beginDrag(InteractManager manager) {
        for (final UpdateFilter filter : _filters) {
            filter.beginDrag(manager);
        }
    }

    public void endDrag(InteractManager manager) {
        for (final UpdateFilter filter : _filters) {
            filter.endDrag(manager);
        }
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
