/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.bookmarks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class StationBookmarkList {

	protected List<StationBookmark> bookmarks = new ArrayList<StationBookmark>();

	// for json deserializing
	public StationBookmarkList() {
	}

	public int size() {
		return bookmarks.size();
	}
	
	@JsonIgnore
	public String[] getArrayOfNames() {
		int sz = size();
		String[] arr = new String[sz];
		
		for(int i=0; i<sz; i++) {
			arr[i] = bookmarks.get(i).getName();
		}
		return arr;
	}
	
	public StationBookmark getBookmarkFromInt(int index) {
		if(index < 0 || index >= bookmarks.size()) {
			return null;
		}
		
		return bookmarks.get(index);
	}
	
	public int getIndexOfBookmark(StationBookmark sb) {
		return bookmarks.indexOf(sb);
	}
	
	public StationBookmark getBookmarkFromName(String name) {
		Iterator<StationBookmark> iter = bookmarks.iterator();
		while(iter.hasNext()) {
			StationBookmark candidate = iter.next();
			if(candidate.getName().equals(name)) {
				return candidate;
			}
		}
		return null;
	}
	
	public List<StationBookmark> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List<StationBookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public void addBookmark(StationBookmark bookmark) {
		bookmarks.add(bookmark);
	}

	public void deleteBookmark(StationBookmark bookmark) {
		bookmarks.remove(bookmark);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("StationBookmarkList: ");
		for(StationBookmark sbk : bookmarks) {
			sb.append(sbk.toString() + ", ");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return ((bookmarks == null) ? 0 : bookmarks.hashCode());
	}

	@Override
	/** requires bookmarks to be in same order */
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if( !(obj instanceof StationBookmarkList)) {
			return false;
		}
		StationBookmarkList other = (StationBookmarkList) obj;

		if(other.getBookmarks() == null) {
			if(bookmarks == null) {
				return true;
			}
			return false;
		}

		if(bookmarks.equals(other.getBookmarks())) {
			return true;
		}
		return false;
	}
}
