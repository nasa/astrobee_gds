/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.rapid.v2.framestore.dds;

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.framestore.tree.FrameTree;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rapid.FrameDef;
import rapid.FrameStoreConfig;
import rapid.Transform3D;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;

/**
 * 
 * @author mallan
 * 
 */
public class RapidFrameHelper {
	private static final RapidFrameHelper s_instance = new RapidFrameHelper();

	protected HashMap<Agent,String> m_siteMap = new HashMap<Agent,String>();

	/**
	 * Set up default Site Frame binding for known agents
	 */
	public RapidFrameHelper() {
		m_siteMap.put(Agent.K10Black,    "MarscapeSiteFrameZDown");
        m_siteMap.put(Agent.K10Red,      "MarscapeSiteFrameZDown");
        m_siteMap.put(Agent.KRex,        "MarscapeSiteFrameZDown");

		m_siteMap.put(Agent.LerA,        "RockYardSiteFrameZDown");
		m_siteMap.put(Agent.LerB,        "RockYardSiteFrameZDown");

		m_siteMap.put(Agent.AthleteA,    "MarsYardSiteFrameZDown");
		m_siteMap.put(Agent.AthleteB,    "MarsYardSiteFrameZDown");
		m_siteMap.put(Agent.AthleteSim,  "MarsYardSiteFrameZDown");
		m_siteMap.put(Agent.TriathleteA, "MarsYardSiteFrameZDown");
		m_siteMap.put(Agent.TriathleteB, "MarsYardSiteFrameZDown");
	}

	public static String getSiteFrame(Agent agent) {
		return s_instance.m_siteMap.get(agent);
	}

	public static void setSiteFrame(Agent agent, String frameName) {
		s_instance.m_siteMap.put(agent, frameName);
	}
	
	
    public static Transform newTransform(Transform3D xfm) {
        return setTransform(xfm, new Transform());
    }

    public static Transform setTransform(Transform3D xfm, Transform retVal) {
        final float[]    r = xfm.rot.userData;
        final double[] xyz = xfm.xyz.userData;
        Matrix3 rot = new Matrix3(r[0], r[1], r[2],
                                  r[3], r[4], r[5],
                                  r[6], r[7], r[8]);
        retVal.setRotation(rot);
        retVal.setTranslation(xyz[0], xyz[1], xyz[2]);
        return retVal;
    }

	/**
	 * Construct a tree from a FrameStoreConfig
	 * @param agent
	 * @param fsc
	 * @return map of roots to trees
	 */
    public static Map<String,FrameTreeNode> makeTree(Agent agent, FrameStoreConfig fsc) {
        Object[] objs = fsc.frames.userData.toArray();
        FrameDef fd;
        FrameTreeNode ftn;
        ArrayList<FrameParentPair> fppList = new ArrayList<FrameParentPair>();
        final Transform xfm = new Transform();
        for(Object obj : objs) {
            fd  = (FrameDef)obj;
            ftn = new FrameTreeNode(fd.name, setTransform(fd.transform, xfm));
            fppList.add(new FrameParentPair(ftn, fd.parent));
        }
        return makeTree(fppList);
    }

	/**
	 * Given a list of FrameParentPairs, create a tree. In the nominal 
	 * case, the return value will have a single root node in the map
	 * with an empty parent name. 
	 * @param allFrames
	 * @return
	 */
	public static Map<String,FrameTreeNode> makeTree(List<FrameParentPair> allFrames) {
		Map<String,FrameTreeNode> roots = new HashMap<String,FrameTreeNode>();
		if(allFrames.size() > 0) {
			int thisSize = allFrames.size();
			int lastSize = 0;
			ArrayList<FrameParentPair> rootList = new ArrayList<FrameParentPair>();
			ArrayList<FrameParentPair> findList = new ArrayList<FrameParentPair>();
			ArrayList<FrameParentPair> removeList = new ArrayList<FrameParentPair>();

			// exhaustively match children to their parents
			while( thisSize != lastSize) {
				removeList.clear();
				rootList.addAll(allFrames);
				findList.addAll(allFrames);
				for( FrameParentPair find : findList ) {
					for( FrameParentPair root : rootList ) {
						if(find.parent.length() > 0) {
							String parentName = find.parent;
							if(!parentName.startsWith("/")) {
								parentName = ".../"+find.parent;
							}
							//logger.debug("parentName = "+parentName+", root name = "+root.node.getFrame().getName());
							FrameTreeNode parent = FrameTree.lookup(root.node, parentName);
							if(parent != null) {
								parent.mergeChild(find.node);
								removeList.add(find);
								break;
							}
						}
					}
				}
				allFrames.removeAll(removeList);
				lastSize = thisSize;
				thisSize = allFrames.size();
			}
			for(FrameParentPair pair : allFrames) {
				roots.put(pair.parent, pair.node);
			}
		}
		return roots;
	}
}
