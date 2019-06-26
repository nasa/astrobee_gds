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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class SerializationExample {
	public static void main(String[] args) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		//		StationBookmarkList ret = new StationBookmarkList();
		//		
		//		StationBookmark sb = new StationBookmark("hello", new Point6Dof());
		//		ret.addBookmark(sb);
		//		ModuleBayPoint mbp = new ModuleBayPoint();
		//		mbp.setModule(LocationMap.Module.COLUMBUS);
		//		mbp.setBayNumber(BayNumber.BETWEEN_TWO_THREE);
		//		StationBookmark sb2 = new StationBookmark("In Columbus", mbp);
		//		ret.addBookmark(sb2);
		//		//mapper.writeValue(System.out, sb);
		//		mapper.writeValue(System.out, ret);

		Album album = new Album("Kind Of Blue");
		album.setLinks(new String[] { "link1", "link2" });
		List<String> songs = new ArrayList<String>();
		songs.add("So What");
		songs.add("Flamenco Sketches");
		songs.add("Freddie Freeloader");
		album.setSongs(songs);
		Artist artist = new Artist();
		artist.name = "Miles Davis";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		artist.birthDate = format.parse("26-05-1926");
		album.setArtist(artist);
		album.addMusician("Miles Davis", "Trumpet, Band leader");
		album.addMusician("Julian Adderley", "Alto Saxophone");
		album.addMusician("Paul Chambers", "double bass");

		//        mapper.configure(SerializationConfig.Feature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
		mapper.setDateFormat(outputFormat);
//		mapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
//			@Override
//			public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
//				if (field.getFullName().equals("com.studytrails.json.jackson.Artist#name"))
//					return "Artist-Name";
//				return super.nameForField(config, field, defaultName);
//			}
//
//			@Override
//			public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
//				if (method.getAnnotated().getDeclaringClass().equals(Album.class) && defaultName.equals("title"))
//					return "Album-Title";
//				return super.nameForGetterMethod(config, method, defaultName);
//			}
//		});
		//        mapper.setSerializationInclusion(Inclusion.NON_NULL);
		//mapper.writeValue(new File(), album);

		// COMMENTING TO MAKE BAMBOO HAPPY - IT THINKS WE'RE JAVA 1.6
		//		String listAsString = mapper.writeValueAsString(album);
//		String filename = "testdata" + File.separator + "albums.json";
//		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//				new FileOutputStream(filename), "utf-8"))) {
//			writer.write(listAsString);
//		}

		//========================================
	}

	public static class Album {
		private String title;
		private String[] links;
		private List<String> songs = new ArrayList<String>();
		private Artist artist;
		private Map<String , String> musicians = new HashMap<String, String>();

		public Album() {
		}
		public Album(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setLinks(String[] links) {
			this.links = links;
		}

		public String[] getLinks() {
			return links;
		}

		public void setSongs(List<String> songs) {
			this.songs = songs;
		}

		public List<String> getSongs() {
			return songs;
		}

		public void setArtist(Artist artist) {
			this.artist = artist;
		}

		public Artist getArtist() {
			return artist;
		}

		public Map<String, String> getMusicians() {
			return Collections.unmodifiableMap(musicians);
		}

		public void addMusician(String key, String value) {
			musicians.put(key, value);
		}
	}

	public static class Artist {
		public String name;
		public Date birthDate;
		public int age;
		public String homeTown;
		public List<String> awardsWon = new ArrayList<String>();
	}
}
