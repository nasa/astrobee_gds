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

import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameTree;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.framestore.tree.visitors.PrintFrameNamesVisitor;


public class TestFrameNameLookup {

    static FrameTreeNode tryFind(FrameTreeNode searchRoot, String path, boolean shouldPass) {
        FrameTreeNode node;
        node = FrameTree.lookup(searchRoot, path);
        String pass;
        if(node == null) {
            if(!shouldPass) pass = "[pass]";
            else pass = "[FAIL]";
            System.out.println(pass+" did not find node path: "+path);
        }
        else {
            if(shouldPass) pass = "[pass]";
            else pass = "[FAIL]";
            System.out.println(pass+"   found node with path: "+path+" : "+FrameTree.getFullNameOf(node));
        }
        return node;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FrameTreeNode rootNode = new FrameTreeNode(new Frame("root"));

        String[] robots = new String[] { "red", "black", "gold" };
        String[] sides  = new String[] { "left", "right" };
        String[] leaves = new String[] { "a", "b", "c" };

        for(String r : robots) {
            FrameTreeNode robot = rootNode.attachChild(new FrameTreeNode(new Frame(r)) );
            for(String s : sides) {
                FrameTreeNode side = robot.attachChild(new FrameTreeNode(new Frame(s)) );
                for(String l : leaves) {
                    /*FrameTreeNode leaf = */side.attachChild(new FrameTreeNode(new Frame(l)) );
                }
            }
        }

        FrameTreeNode node;        
        node = tryFind(rootNode, "/root/black/left/a", true);
        node = node.attachChild(new FrameTreeNode(new Frame("cameraOne")));
        node = node.attachChild(new FrameTreeNode(new Frame("lens")));
        node = node.attachChild(new FrameTreeNode(new Frame("lensCap")));
        
        node = tryFind(rootNode, "/root/black/left/c", true);
        node = node.attachChild(new FrameTreeNode(new Frame("lidar")));

        node = tryFind(rootNode, ".../a/cameraOne", true);
        node = tryFind(rootNode, ".../a", true);
        node = tryFind(rootNode, ".../black/../red", true);
        node = tryFind(rootNode, "../black/left/a", true);
        node = tryFind(rootNode, "../../../black/left/a", true);
        node = tryFind(rootNode, "root/black/left/a", false);
        node = tryFind(rootNode, "/black/left/a", false);
        node = tryFind(rootNode, "black/left/a", true);
        node = tryFind(rootNode, "/foo/black/left/a", false);
        node = tryFind(rootNode, "foo/black/left/a", false);
        node = tryFind(rootNode, "red", true);
        node = tryFind(rootNode, "red/../black/left/a", true);
        node = tryFind(rootNode, "red/../black/.../lensCap", true);
        node = tryFind(rootNode, "red/.../lensCap", false);
        node = tryFind(rootNode, "black/.../lensCap", true);
        node = tryFind(rootNode, ".../lensCap", true);


        boolean doTest = true; // avoid stupid dead code warning
        PrintFrameNamesVisitor visitor = new PrintFrameNamesVisitor();
        doTest = false;
        if( doTest ) {
            System.out.println("-- ");
            System.out.println("-- Traverse Breadth First");
            System.out.println("-----------------------------------------");
            rootNode.traverseBreadthFirst(visitor);
        }
        
        doTest = false;
        if( doTest ) {
            System.out.println("-- ");
            System.out.println("-- Traverse Depth First Post-Order");
            System.out.println("-----------------------------------------");
            rootNode.traversePostOrder(visitor);
        }

        doTest = false;
        if( doTest ) {
            System.out.println("-- ");
            System.out.println("-- Traverse Depth First Pre-Order");
            System.out.println("-----------------------------------------");
            rootNode.traversePreOrder(visitor);
        }

        FrameTreeNode nodeA, nodeB, nodeC, nodeD;
        nodeA = FrameTree.lookup(rootNode, "/root/black/left/a/cameraOne/lens/lensCap");
        nodeB = FrameTree.lookup(rootNode, "/root/black/left/c/lidar");
        nodeC = FrameTree.lookup(rootNode, "/root/black/right/a");
        nodeD = FrameTree.lookup(rootNode, "/root/red/left/a");
        
        node = nodeA.getLastCommonAncestor(nodeB);
        System.out.println("Last common ancestor between:");
        System.out.println("   "+FrameTree.getFullNameOf(nodeA));
        System.out.println("   "+FrameTree.getFullNameOf(nodeB));
        System.out.println("-->"+FrameTree.getFullNameOf(node));

        node = nodeA.getLastCommonAncestor(nodeC);
        System.out.println("Last common ancestor between:");
        System.out.println("   "+FrameTree.getFullNameOf(nodeA));
        System.out.println("   "+FrameTree.getFullNameOf(nodeC));
        System.out.println("-->"+FrameTree.getFullNameOf(node));

        node = nodeA.getLastCommonAncestor(nodeD);
        System.out.println("Last common ancestor between:");
        System.out.println("   "+FrameTree.getFullNameOf(nodeA));
        System.out.println("   "+FrameTree.getFullNameOf(nodeD));
        System.out.println("-->"+FrameTree.getFullNameOf(node));

        
    }

}
