/*******************************************************************************
 * Copyright (c) 2011 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package gov.nasa.arc.irg.plan.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * A site describes the locale of the plan.
 * @author tecohen
 *
 */
@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")
public class Site extends TypedObject {
	private Logger logger = Logger.getLogger(Site.class);
	protected List<Number> m_bbox = null;
	protected Crs m_alternateCrs = null;
	protected Crs m_crs = null;

	public Site() {
	}

	public void populate(Site other){
		if (other != null){
			setName(other.getName());
			setNotes(other.getNotes());
			try {
				if(other.getAlternateCrs() != null) {
					setAlternateCrs(other.getAlternateCrs().clone());
				}
			} catch (CloneNotSupportedException e) {
				logger.warn("Problems cloning crs ", e);
			}
		}
	}

	public List<Number> getBbox() {
		return m_bbox;
	}

	public void setBbox(List<Number> bbox) {
		m_bbox = bbox;
	}

	public Crs getCrs() {
		return m_crs;
	}

	public void setCrs(Crs crs) {
		m_crs = crs;
	}

	public Crs getAlternateCrs() {
		return m_alternateCrs;
	}

	public void setAlternateCrs(Crs crs) {
		m_alternateCrs = crs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
	
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
		
		result = prime * result + ((m_bbox == null) ? 0 : m_bbox.hashCode());
		result = prime * result + ((m_alternateCrs == null) ? 0 : m_alternateCrs.hashCode());
		result = prime * result + ((m_crs == null) ? 0 : m_crs.hashCode());
		
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof Site)) {
			return false;
		}
		Site other = (Site)o;
		
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(getNotes() == null) {
			if(other.getNotes() != null) {
				return false;
			}
		} else if(!getNotes().equals(other.getNotes())) {
			return false;
			
		}
		if(getId() == null) {
			if(other.getId() != null) {
				return false;
			}
		} else if(!getId().equals(other.getId())) {
			return false;
		}
		
		if(getBbox() == null) {
			if(other.getBbox() != null) {
				return false;
			}
		} else if(!getBbox().equals(other.getBbox())) {
			return false;
		}
		
		if(getCrs() == null) {
			if(other.getCrs() != null) {
				return false;
			}
		} else if(!getCrs().equals(other.getCrs())) {
			return false;
		}
		
		if(getAlternateCrs() == null) {
			if(other.getAlternateCrs() != null) {
				return false;
			}
		} else if(!getAlternateCrs().equals(other.getAlternateCrs())) {
			return false;
		}
		
		return true;
	}
}
