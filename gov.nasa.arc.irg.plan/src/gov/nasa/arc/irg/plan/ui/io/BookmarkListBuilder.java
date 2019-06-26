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
package gov.nasa.arc.irg.plan.ui.io;

import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.bookmarks.StationBookmarkList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class BookmarkListBuilder {
	private StationBookmarkList bookmarkList;
	private static BookmarkListBuilder INSTANCE;
	private static Vector<IBookmarksListListener> listeners = new Vector<IBookmarksListListener>();
	
	public static BookmarkListBuilder getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new BookmarkListBuilder();
			try {
				String bookmarksPath = ConfigFileWrangler.getInstance().getBookmarksPath();
				StationBookmarkList sbl = INSTANCE.readStationBookmark(bookmarksPath);
				INSTANCE.setBookmarkList(sbl);
				
			} catch (Exception e) {
				System.err.println("BookmarkListBuilder failed to read StationBookmarkList: "+ConfigFileWrangler.getInstance().getBookmarksPath());
			}
		}
		return INSTANCE;
	}
	
	public BookmarkListBuilder()
	{
	}
	
	public static void addListener(IBookmarksListListener bll) {
		listeners.addElement(bll);
	}
	
	public static void removeListener(IBookmarksListListener bll) {
		listeners.remove(bll);
	}
	
	private static void notifyListeners() {
		for(IBookmarksListListener bll : listeners) {
			bll.onBookmarksListChanged();
		}
	}
	
	public BookmarkListBuilder(StationBookmarkList bookmarkList)
	{
		this.bookmarkList = bookmarkList;
	}
	
	public StationBookmarkList getBookmarkList() {
		return bookmarkList;
	}
	
	public void setBookmarkList(StationBookmarkList sbl) {
		bookmarkList = sbl;
	}
	
	public static StationBookmarkList getStaticBookmarkList() {
		BookmarkListBuilder b;
		try {
			b = getInstance();
		} catch(Exception e) {
			return null; // happens when called from simulator
		}
		return b.getBookmarkList();
	}
	
	public static StationBookmark getBookmarkFromName(String name) {
		StationBookmarkList sb = getStaticBookmarkList();
		if(sb == null) {
			return null;
		}
		StationBookmark s = sb.getBookmarkFromName(name);
		return s;
	}
	
	public static StationBookmark getBookmarkFromInt(int index) {
		return getStaticBookmarkList().getBookmarkFromInt(index);
	}
	
	public static int getIndexOfBookmark(StationBookmark sb) {
		return getStaticBookmarkList().getIndexOfBookmark(sb);
	}
	
	public static void addBookmark(StationBookmark sb) {
		getStaticBookmarkList().addBookmark(sb);
		notifyListeners();
	}
	
	public static void deleteBookmark(String name) {
		StationBookmark bookmark = getStaticBookmarkList().getBookmarkFromName(name);
		getStaticBookmarkList().deleteBookmark(bookmark);
		notifyListeners();
	}
	
	public static void saveBookmarksList() throws Exception {
		getInstance().write(ConfigFileWrangler.getInstance().getBookmarksPath());
	}
	
	public StationBookmarkList read(File file) {
		if(file == null || !file.exists()) {
			System.err.println("BookmarkListBuilder no file: "+file);
			return null;
		}
		
		// Don't know why mapper can't be class variable, but it doesn't work if I do that
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		
		try {
			bookmarkList = mapper.readValue(file, StationBookmarkList.class);
		} catch (Exception e) {
			System.err.println("BookmarkListBuilder failed to read StationBookmarkList: "+file);
		}
		return bookmarkList;
	}
	
	public StationBookmarkList readStationBookmarkFromUrl(String urlName) throws Exception {
		URL url = new URL( urlName );
		InputStream is = url.openConnection().getInputStream();
		byte[] jsonData = IOUtils.toByteArray(is);
		bookmarkList = fromByteArray(jsonData);
		return bookmarkList;
	}
	
	public StationBookmarkList readStationBookmark(String filename) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(filename));
		return fromByteArray(jsonData);
	}
	
	private StationBookmarkList fromByteArray(byte[] jsonData) throws Exception {
		StationBookmarkList sbl = null;
        ObjectMapper objectMapper = new ObjectMapper();
        sbl = objectMapper.readValue(jsonData, StationBookmarkList.class);
		return sbl;
	}
	
	public void write(String filename) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		if(bookmarkList == null) {
			System.err.println("BookmarkListBuilder has no list to write");
			return;
		}
		
		String listAsString = mapper.writeValueAsString(bookmarkList);
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(filename), "utf-8"))) {
			writer.write(listAsString);
		}
	}
}
