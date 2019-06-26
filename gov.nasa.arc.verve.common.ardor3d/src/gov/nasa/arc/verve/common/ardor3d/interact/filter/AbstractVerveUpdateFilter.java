package gov.nasa.arc.verve.common.ardor3d.interact.filter;

import gov.nasa.arc.verve.ardor3d.extension.interact.InteractManager;
import gov.nasa.arc.verve.ardor3d.extension.interact.filter.UpdateFilter;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;

/**
 * 
 */
public abstract class AbstractVerveUpdateFilter implements UpdateFilter {
    //private static final Logger logger = Logger.getLogger(AbstractVerveUpdateFilter.class);
    double m_maxMove  = 5;
    double m_maxMove2 = m_maxMove * m_maxMove;

    /**
     * set maximum translation that can be done in a single 
     * iteration (avoid large jumps when mouse ray is close to 
     * parallel to move plane)
     */
    public void setMaxMove(double maxMove) {
        m_maxMove  = maxMove;
        m_maxMove2 = maxMove*maxMove;
    }
    /** maximum translation allowed in single iteration  */
    public double getMaxMove() {
        return m_maxMove;
    }
    
    /** 
     * filter out moves larger than m_maxMove*boundRadius. AbstractVerveUpdateFilters 
     * are not required to do this, but it is recommended that this be the 
     * first call in the applyFilter implementation. 
     */
    protected void filterMaxMove(InteractManager manager) {
        final Spatial target = manager.getSpatialTarget();
        final double  boundR = target.getWorldBound().getRadius();
        final double  boundR2 = boundR*boundR;
        final ReadOnlyVector3 stateLoc = manager.getSpatialState().getTransform().getTranslation();
        final ReadOnlyVector3 spatLoc  = target.getTranslation();
        final Vector3 diff = stateLoc.subtract(spatLoc, new Vector3());
        if(diff.lengthSquared() > boundR2*m_maxMove2) {
            double  scale    = boundR*m_maxMove/diff.length();
            Vector3 newTrans = diff.multiply(scale, null).addLocal(target.getTranslation());
            manager.getSpatialState().getTransform().setTranslation(newTrans);
            //logger.debug("scale move by "+scale);
        }
    }
    
    abstract public void applyFilter(InteractManager manager);
    abstract public void beginDrag(InteractManager manager);
    abstract public void endDrag(final InteractManager manager);
}