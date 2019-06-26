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

import gov.nasa.rapid.v2.framestore.FrameStoreException;
import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.framestore.tree.visitors.PrintFrameNamesVisitor;


public class TestFrameMerge {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FrameTreeNode rootA = new FrameTreeNode(new Frame("root"));
        FrameTreeNode rootB = new FrameTreeNode(new Frame("root"));

        {
            String[] robots = new String[] { "red", "black" };
            String[] sides  = new String[] { "left", "right" };
            String[] leaves = new String[] { "a", "c", "d" };
            for(String r : robots) {
                FrameTreeNode robot = rootA.attachChild(new FrameTreeNode(new Frame(r)) );
                for(String s : sides) {
                    FrameTreeNode side = robot.attachChild(new FrameTreeNode(new Frame(s)) );
                    for(String l : leaves) {
                        /*FrameTreeNode leaf = */side.attachChild(new FrameTreeNode(new Frame(l)) );
                    }
                }
            }
        }
        {
            String[] robots = new String[] { "black", "gold" };
            String[] sides  = new String[] { "left", "apple" };
            String[] leaves = new String[] { "b", "c", "d", "e" };
            for(String r : robots) {
                FrameTreeNode robot = rootB.attachChild(new FrameTreeNode(new Frame(r)) );
                for(String s : sides) {
                    FrameTreeNode side = robot.attachChild(new FrameTreeNode(new Frame(s)) );
                    for(String l : leaves) {
                        /*FrameTreeNode leaf =*/ side.attachChild(new FrameTreeNode(new Frame(l)) );
                    }
                }
            }
        }

        PrintFrameNamesVisitor visitor = new PrintFrameNamesVisitor();
        if( true ) {
            System.out.println("-- ");
            System.out.println("-- rootA before merge");
            System.out.println("-----------------------------------------");
            rootA.traversePreOrder(visitor, true);
        }

        if( true ) {
            System.out.println("-- ");
            System.out.println("-- rootB");
            System.out.println("-----------------------------------------");
            rootB.traversePreOrder(visitor, true);
        }

        try {
            FrameStore.mergeFrameTrees(rootB, rootA);
            if( true ) {
                System.out.println("-- ");
                System.out.println("-- rootA after merge");
                System.out.println("-----------------------------------------");
                rootA.traversePreOrder(visitor, true);
            }
        }
        catch (FrameStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
