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
package gov.nasa.arc.irg.plan.freeflyer.config;

import java.util.List;

/** This holds one of the original format keepout/keepin files */
public class KeepoutConfig {
	/** Opposite corners of the boxes */
	private List<List<Float>> sequence;
	private String dateCreated;
	private String dateModified;
	private String notes;
	private String author;
	private String name;
	private boolean safe;
	
	public KeepoutConfig() {
		
	}
	
	public List<List<Float>> getSequence() {
		return sequence;
	}
	public void setSequence(List<List<Float>> sequence) {
		this.sequence = sequence;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSafe() {
		return safe;
	}
	public void setSafe(boolean safe) {
		this.safe = safe;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (safe ? 1 : 0);
	
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		float EPSILON = 0.0001f;
		
		if(this == o) {
			return true;
		}
		if(!(o instanceof KeepoutConfig)) {
			return false;
		}
		KeepoutConfig other = (KeepoutConfig)o;

		if(dateCreated == null) {
			if(other.dateCreated != null) {
				return false;
			}
		} else if(!dateCreated.equals(other.dateCreated)) {
			return false;
		}
		
		if(notes == null) {
			if(other.notes != null) {
				return false;
			}
		} else if(!notes.equals(other.notes)) {
			return false;
		}
		
		if(author == null) {
			if(other.author != null) {
				return false;
			}
		} else if(!author.equals(other.author)) {
			return false;
		}
		
		if(name == null) {
			if(other.name != null) {
				return false;
			}
		} else if(!name.equals(other.name)) {
			return false;
		}
		
		if(safe != other.safe) {
			return false;
		}
		
		if(sequence == null) {
			if(other.sequence != null) {
				return false;
			}
		}
		else if(sequence.size() != other.sequence.size()) {
			return false;
		}
		for(int i=0; i<sequence.size(); i++) {
			List<Float> box = sequence.get(i);
			List<Float> otherBox = other.sequence.get(i);
			
			if(box.size() != otherBox.size()) {
				return false;
			}
			
			for(int j=0; j<box.size(); j++) {
				if(Math.abs(box.get(j)-otherBox.get(j))>EPSILON) {
					return false;
				}
			}
		}

		return true;
	}
}
