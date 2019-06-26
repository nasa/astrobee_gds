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
package gov.nasa.util;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {

	/**
	 * recursive call to find nodes of a given name
	 */
	public static List<Node> findNodes(String nodeName, Node root, List<Node> foundList) {
		if (root != null) {
			if (root.getNodeName().equals(nodeName)) {
				foundList.add(root);
			}
			if (root.hasChildNodes()) {
				NodeList children = root.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					findNodes(nodeName, children.item(i), foundList);
				}
			}
		}
		return foundList;
	}

	public static String getAttribute(Node node, String attribute) {
		Node attribNode = node.getAttributes().getNamedItem(attribute);
		if (attribNode != null) {
			return attribNode.getNodeValue();
		}
		return null;
	}
}
