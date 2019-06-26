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

import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

public class FrameParentPair {
	public final FrameTreeNode node;
	public final String parent;

	public FrameParentPair(FrameTreeNode frame, String parent) {
		this.node = frame;
		this.parent = parent;
	}

	public FrameParentPair(Frame frame, String parent) {
		this.node = new FrameTreeNode(frame);
		this.parent = parent;
	}
}
