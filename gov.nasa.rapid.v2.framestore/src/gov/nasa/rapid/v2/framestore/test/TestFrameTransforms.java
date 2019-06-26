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
package gov.nasa.rapid.v2.framestore.test;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;

import gov.nasa.rapid.v2.framestore.FrameStoreException;
import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTree;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.framestore.tree.visitors.PrintFrameNamesVisitor;

public class TestFrameTransforms {

    public static final Vector3 AXIS_X = new Vector3(1,0,0);
    public static final Vector3 AXIS_Y = new Vector3(0,1,0);
    public static final Vector3 AXIS_Z = new Vector3(0,0,1);

    /**
     * @param args
     */
    public static void main(String[] args) {
        PrintFrameNamesVisitor visitor = new PrintFrameNamesVisitor();
        FrameTreeNode rootA = new FrameTreeNode(new Frame("root"));
        FrameTreeNode rootB = new FrameTreeNode(new Frame("root"));

//        createATransforms(rootA);
        System.err.println("rootA");
        rootA.traversePreOrder(visitor, true);

//        createBTransforms(rootB);
        System.err.println("rootB");
        rootB.traversePreOrder(visitor, true);

        try {
            FrameStore.mergeFrameTrees(rootB, rootA);
            FrameTreeNode start  = FrameTree.lookup(rootA, ".../start");
            FrameTreeNode finish = FrameTree.lookup(rootA, ".../finish");
            System.err.println("rootA merged");
            rootA.traversePreOrder(visitor, true);
            
            System.out.println("start  = "+start);
            System.out.println("finish = "+finish);

            Transform xfm = FrameTree.getTransform(start, finish);
            System.out.println("xfm = \n"+xfm);
        }
        catch (FrameStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
/*
    public static void createATransforms(FrameTreeNode root) {
        FrameTreeNode cur;
        FrameTreeNode tmp;
        cur = root.attachChild(new FrameTreeNode("rZ90"));
        
        // Temptative adaptation:
        trans.fromAngleNormalAxis(Math.PI/2, AXIS_Z);
        double col1[] = {2,0,0,1};
        trans.setColumn(3, col1);
        cur.getData().setTransform(trans);
        // end
        
        AxisAngle4d aa = new AxisAngle4d();
        aa.set(AXIS_Z, Math.PI/2);
        cur.getData().getTransform().set(aa);
        cur = cur.attachChild(new FrameTreeNode("tX2"));
        cur.getData().getTransform().setTranslation(new Vector3d(2,0,0));
        tmp = cur;
        cur = cur.attachChild(new FrameTreeNode("tY-1.9"));
        cur.getData().getTransform().setTranslation(new Vector3d(0,-1.9,0));
        aa.set(AXIS_Z, -Math.PI/2);
        cur = cur.attachChild(new FrameTreeNode("start"));
        cur.getData().getTransform().set(aa);
        cur.getData().getTransform().setTranslation(new Vector3d(0,0,0.1));

        cur = cur.attachChild(new FrameTreeNode("test"));
        cur.getData().getTransform().setTranslation(new Vector3d(0.1,0,0.05));



        cur = tmp;
        cur = cur.attachChild(new FrameTreeNode("tY0.5"));
        cur.getData().getTransform().setTranslation(new Vector3d(0,0.5,0));
        cur = cur.attachChild(new FrameTreeNode("tY0.5"));
        aa.set(AXIS_Y, -Math.PI/10);
        for(int i = 0; i < 10; i++) {
            cur = cur.attachChild(new FrameTreeNode("rY-10tX0.5"));
            cur.getData().getTransform().set(aa);
            cur.getData().getTransform().setTranslation(new Vector3d(0.5,0,0));
        }
    }

    public static void createBTransforms(FrameTreeNode root) {
        FrameTreeNode cur;
        cur = root.attachChild(new FrameTreeNode("rZ0"));
        cur = cur.attachChild(new FrameTreeNode("tX2"));
        cur.getData().getTransform().setTranslation(new Vector3d(2,0,0));
        cur = cur.attachChild(new FrameTreeNode("tY2"));
        cur.getData().getTransform().setTranslation(new Vector3d(0,2,0));
        cur = cur.attachChild(new FrameTreeNode("finish"));
        cur.getData().getTransform().setTranslation(new Vector3d(0,0,0.1));

    }
*/
}
