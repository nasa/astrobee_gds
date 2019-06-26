package gov.nasa.arc.verve.common.ardor3d.bounding;

import java.util.List;
import java.util.Map;

import com.ardor3d.bounding.CollisionTree;
import com.ardor3d.bounding.CollisionTreeController;
import com.ardor3d.scenegraph.Mesh;

/**
 * LRU cache removal. Default CollisionTreeManager's cache's key set will be ordered
 * with the first element being the oldest used. 
 * 
 * Thrashing in the collision cache causes severe performance problems, so
 * we add some margin to the desired size, and don't perform cleanup every call. 
 * 
 * NOTE: this interface seems inefficient... IMO the protected list held by 
 * CollisionTree manager should be a completely separate cache so the dynamic 
 * cache can be cleaned more quickly.
 */
public class LruUsageTreeController implements CollisionTreeController {
    //private static final Logger logger = Logger.getLogger(LruUsageTreeController.class);
    private int count  = 0;
    private int doCall = 200;
    
    public void clean(final Map<Mesh, CollisionTree> cache, final List<Mesh> protectedList, int desiredSize) {
        desiredSize += protectedList.size();
        if(++count%doCall==0 && cache.size() > desiredSize*1.0) {
            //logger.debug("*** cache size = "+cache.size()+", desiredSize="+desiredSize);
            // get the ordered keyset (this will be ordered with oldest to newest).
            final Object[] set = cache.keySet().toArray();

            //LinkedList<Spatial> debugList = new LinkedList<Spatial>();
            int count = 0;
            // go through the cache removing items that are not protected until the
            // size of the cache is small enough to return.
            while (cache.size() > desiredSize && count < set.length) {
                if (!protectedList.contains(set[count])) {
                    cache.remove(set[count]);
                    //Mesh mesh = (Mesh)set[count];
                    //logger.debug("remove "+mesh.getName());
                    //debugList.add(mesh);
                }
                count++;
            }
            //logger.debug("*** cache size = "+cache.size());
            //VerveDebug.setSelectBounds(debugList);
        }
    }

}
