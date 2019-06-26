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
package gov.nasa.rapid.v2.framestore.tree.visitors;

import gov.nasa.rapid.v2.framestore.tree.FrameTree;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

public class PrintFrameNamesVisitor implements IFrameVisitor {
	protected boolean m_printTransforms;

	public PrintFrameNamesVisitor() {
		this(false);
	}

	public PrintFrameNamesVisitor(boolean printTransforms) {
		m_printTransforms = printTransforms;
	}

	public boolean visit(FrameTreeNode node) {
		System.out.println(FrameTree.getFullNameOf(node));
		if (m_printTransforms) {
			System.out.println(node.getFrame().getTransform());
		}
		return false;
	}

	public void down(FrameTreeNode node) {
		// TODO Auto-generated method stub
	}

	public void up(FrameTreeNode node) {
		// TODO Auto-generated method stub
	}

}
