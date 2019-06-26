package gov.nasa.arc.verve.common;

import gov.nasa.arc.verve.common.interact.VerveInteractable;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 * 
 * @author mallan
 *
 */
public class VerveUserData {
    private static final Logger logger = Logger.getLogger(VerveUserData.class);

    protected Map<String,Object> m_userDataMap = new HashMap<String,Object>();

    public static final String ORIGINAL_USER_DATA       = "OriginalUserData";
    public static final String CAMERA_FOLLOWABLE        = "CameraFollowable";
    public static final String INTERACTABLE             = "Interactable";

    public Object getData(String key) {
        return m_userDataMap.get(key);
    }

    public void setData(String key, Object value) {
        m_userDataMap.put(key, value);
    }
    
    /** default for camera followable is TRUE */
    public static boolean isCameraFollowable(Spatial spat) {
        return isCameraFollowable(spat, true);
    }

    /** if this spatial or any parents are false, return false
     */
    public static boolean isCameraFollowable(Spatial spat, boolean checkParents) {
        if(spat == null) {
            return false;
        }
        boolean status = isCameraFollowableImpl(spat);
        if(checkParents) {
            if(status) {
                Node parent = spat.getParent();
                if(parent == null)
                    return true;
                return isCameraFollowable(parent, checkParents);
            }
            return status;
        }
        else {
            return status;
        }
    }
    
    /** default for camera followable is TRUE */
    protected static boolean isCameraFollowableImpl(Spatial spat) {
        VerveUserData userData = getUserData(spat, false);
        if(userData != null) {
            Object data = userData.getData(CAMERA_FOLLOWABLE);
            if(data != null) {
                return (Boolean)data;
            }
        }
        return true;
    }

    public static void setCameraFollowable(Spatial spat, boolean value) {
        VerveUserData userData = getUserData(spat, true);
        userData.setData(CAMERA_FOLLOWABLE, new Boolean(value));
    }

    /** default for interactable is FALSE */
    public static boolean isInteractable(Spatial spat) {
        VerveInteractable vi = getInteractable(spat);
        if(vi != null) {
            return vi.isEnabled();
        }
        return false;
    }

    /** 
     * @param spat spatial to query for VerveInteractable. spat may be null.
     * @return VerveInteractable of spat (if it exists) or null
     */
    public static VerveInteractable getInteractable(Spatial spat) {
        VerveUserData userData = getUserData(spat, false);
        if(userData != null) {
            return (VerveInteractable)userData.getData(INTERACTABLE);
        }
        return null;
    }

    public static void setInteractable(Spatial spat, VerveInteractable value) {
        VerveUserData userData = getUserData(spat, true);
        userData.setData(INTERACTABLE, value);
    }

    /** 
     * Convenience method. Sets the enabled state on VerveInteractable object
     * associated with this spatial. If a VerveInteractable object does not already
     * exist, one will be created.
     */
    public static void setInteractable(Spatial spat, boolean state) {
        VerveUserData userData = getUserData(spat, true);
        VerveInteractable vi = getInteractable(spat);
        if(vi == null) {
            vi = new VerveInteractable();
            userData.setData(INTERACTABLE, vi);
        }
        vi.setEnabled(state);
    }

    /**
     * Get userData from spatial. If userData is null and doSubsume is true,
     * a new VerveUserData instance is created. If userData is not null and
     * not an instance of VerveUserData, a new instance of VerveUserData is
     * created, set on the spatial and the original userData is inserted into 
     * the map
     * @param spat
     * @param doSubsume 
     * @return
     */
    public static VerveUserData getUserData(Spatial spat, boolean doSubsume) {
        if(spat != null) {
            Object ud = spat.getUserData();
            if(doSubsume && ud == null) {
                ud = new VerveUserData();
                spat.setUserData(ud);
            }
            try {
                return (VerveUserData)ud;
            }
            catch(ClassCastException e) {
                if(doSubsume) {
                    logger.warn("Spatial user data is not an instance of VerveUserData. ");
                    logger.warn(ud.getClass().getSimpleName()+" has been subsumed into an instance of VerveUserData and is accessible with key \""+ORIGINAL_USER_DATA+"\".");
                    VerveUserData retVal = new VerveUserData();
                    retVal.setData(ORIGINAL_USER_DATA, ud);
                    return retVal;
                }
            }
        }
        return null;
    }
}
