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
package gov.nasa.ensemble.ui.databinding.widgets.customization;

import java.util.ArrayList;
import java.util.List;




/**
 * Bundle together descriptive information for a particular field.
 * This will be used when auto generating databound widgets with customization.
 * 
 * @author tecohen
 *
 */
public class GroupCustomization {
	
	protected String m_name;			// the name of the field
	protected String m_description = "";	// the tooltip / help description for the field
	protected boolean m_advanced;		// true if this should be only shown for advanced users
	protected boolean m_twistie;		// true if this group should be collapsible
	protected boolean m_expanded;		// true if this collapsible group is expanded by default.  (defaults to true)
	protected boolean m_titleBar;		// true if this group has a full title treatment (defaults to true)
	protected String m_flagName;		// the name of the field that controls turning this group on and off (checkbox)
	protected boolean m_skipLabel;		// to skip having label on the children
	
	protected List<String> m_children = new ArrayList<String>();  // child widgets if this is a group
	
	/**
	 * @param name
	 */
	public GroupCustomization(String name) {
		m_name = name;
	}
	
	/**
	 * @param name
	 * @param description
	 */
	public GroupCustomization(String name, String description){
		m_name = name;
		m_description = description;
	}
	
	/**
	 * Heading for the group
	 * @return
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Descriptive text for the group
	 * @return
	 */
	public String getDescription() {
		return m_description;
	}
	
	/**
	 * @param description
	 */
	public void setDescription(String description) {
		m_description = description;
	}
	
	/**
	 * True if this entire group should be hidden for non-advanced users
	 * @return
	 */
	public boolean isAdvanced() {
		return m_advanced;
	}

	/**
	 * @param advanced
	 */
	public void setAdvanced(boolean advanced) {
		m_advanced = advanced;
	}

	/**
	 * The ordered list of child widgets or groups that are in this group
	 * @return
	 */
	public List<String> getChildren() {
		return m_children;
	}

	/**
	 * @param children
	 */
	public void setChildren(List<String> children) {
		m_children = children;
	}
	
	/**
	 * Adds a child to the end of the ordered list of children
	 * @param child
	 */
	public void addChild(String child){
		if (m_children == null){
			m_children = new ArrayList<String>();
		}
		m_children.add(child);
	}
	
	/**
	 * @return true if this group has children
	 */
	public boolean hasChildren(){
		return (getChildren() != null && !getChildren().isEmpty());
	}
	
	/**
	 * Currently this is used just for form groups (sections) -- true if they should be collapsible
	 * @return
	 */
	public boolean isTwistie() {
		return m_twistie;
	}

	/**
	 * @param twistie
	 */
	public void setTwistie(boolean twistie) {
		m_twistie = twistie;
	}
	
	public boolean isExpanded() {
		return m_expanded;
	}

	public void setExpanded(boolean expanded) {
		m_expanded = expanded;
	}

	public String getFlagName() {
		return m_flagName;
	}

	public void setFlagName(String flagName) {
		m_flagName = flagName;
	}
	
	/**
	 * @return true if there's a flag name set.
	 */
	public boolean hasFlagName() {
		return m_flagName != null && m_flagName.length() > 0;
	}

	public boolean isSkipLabel() {
		return m_skipLabel;
	}

	public void setSkipLabel(boolean skipLabel) {
		m_skipLabel = skipLabel;
	}

	public boolean isTitleBar() {
		return m_titleBar;
	}

	public void setTitleBar(boolean titleBar) {
		m_titleBar = titleBar;
	}

	/**
	 * @param name
	 * @return
	 */
	public static GroupCustomization createGroup(String name) {
		GroupCustomization result = new GroupCustomization(name);
		return result;
	}
	
	/**
	 * @param name
	 * @param children
	 * @return
	 */
	public static GroupCustomization createGroup(String name, List<String> children){
		GroupCustomization result = createGroup(name);
		result.setChildren(children);
		return result;
	}
	
	/**
	 * @param name
	 * @param children
	 * @return
	 */
	public static GroupCustomization createGroup(String name, String[] children){
		GroupCustomization result = createGroup(name);
		List<String> childrenList = new ArrayList<String>();
		for (String c : children){
			childrenList.add(c);
		}
		result.setChildren(childrenList);
		return result;
	}
	
}
