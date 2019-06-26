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

import gov.nasa.arc.irg.plan.json.JsonDateDeserializer;
import gov.nasa.arc.irg.plan.json.JsonDateSerializer;
import gov.nasa.arc.irg.plan.util.StrUtil;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A Plan is a representation of a sequential series of commands.
 * Plans are customizable for various projects by setting up specific commands and profiles of commands.
 * 
 * TODO: the initialize method references positions of PlanCommand children for their parent's end position.
 * We need to set up listeners to deal with this during editing, if there is editing.
 * 
 * @author tecohen
 *
 */
@SuppressWarnings("rawtypes")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Plan extends SequenceHolder implements PropertyChangeListener, Comparable {
	private static final Logger logger = Logger.getLogger(Plan.class);
	public static final boolean CONNECT_SEGMENT_LISTENERS = false;

	protected String m_xpjson;				// spec version
	protected String m_schemaURL;			// url to the schema
	protected String m_type;				// type of the plan
	protected Site m_site;					// information describing the site or location of the plan
	protected String m_creator = "";		// author of the plan
	protected int m_planNumber;				// number for the plan
	protected String m_planVersion;			// version for the plan
	protected Date m_dateCreated;			// creation date
	protected Date m_dateModified;			// last modification date
	protected Platform m_platform;			// what "platform" we are running on, ie K10Red
	protected Set<String> m_libraryURIs = new HashSet<String>();
	protected int m_numRapidSteps;

	protected boolean m_visible = true;		// whether or not the plan is "visible", ie a checkout or non normal plan	
	protected boolean valid = false;

	protected float m_defaultSpeed = 1.0f;	// default speed
	protected float m_defaultTolerance = 1.0f;  // default tolerance for stations & drives, in meters

	protected List<String> m_contributors;	// contributors to the plan

	protected PlanBuilder m_builder;		// plan builder for this plan

	public Plan(){
		super();
		m_site = new Site();
	}

	public Plan(Site site) {
		super();
		m_site = site;
	}

	/**
	 * Iterate through and ensure calculated values are populated
	 * ie start and end positions
	 */
	public void initialize() {
		Station station = null;
		Segment segment = null;
		for (Sequenceable s : getSequence()){
			try {
				if (s instanceof Station){
					station = (Station)s;
					if(segment != null) {
						// I feel like we just did this
						segment.setEndPosition(station.getStartPosition().clone());
					}

					Position newEndPosition = null;
					for (int i = 0; i < station.getSequence().size(); i++){
						Sequenceable child = station.getSequenceable(i);
						if (i == 0){
							child.setStartPosition(station.getStartPosition().clone());
						} else {
							child.setStartPosition(station.getSequenceable(i-1).getEndPosition().clone());
						}
						if (child instanceof PlanCommand){
							PlanCommand pc = (PlanCommand)child;
							pc.calculateEndPosition(); // this is already done for spheres orient commands
							// but we might need it later
						}
						newEndPosition = child.getEndPosition();
					}
					if (newEndPosition != null){
						station.setEndPosition(newEndPosition.clone());
					}


				} else if (s instanceof Segment){
					// um, nothing
				}

			} catch (CloneNotSupportedException e) {
				// but it is
			}
		}
	}

	public String getCreator() {
		return m_creator;
	}

	public void setCreator(String author) {
		String oldCreator = m_creator;
		m_creator = author;
		if (oldCreator == null || !oldCreator.equals(author)){
			firePropertyChange("author", oldCreator, author);
		}
	}

	public boolean isVisible() {
		return m_visible;
	}

	@JsonSetter("visible")
	public void setVisible(boolean visible) {
		boolean oldVisible = m_visible;
		m_visible = visible;
		if (oldVisible != visible){
			firePropertyChange("visible", oldVisible, visible);
		}
	}

	public Site getSite() {
		return m_site;
	}

	public void setSite(Site site) {
		Site oldSite = m_site;
		m_site = site;
		if (oldSite == null || !oldSite.equals(site)){
			firePropertyChange("site", oldSite, site);
		}
	}

	public String getXpjson() {
		return m_xpjson;
	}

	public void setXpjson(String xpjson) {
		String oldxpjson = m_xpjson;
		m_xpjson = xpjson;
		if (oldxpjson == null || !oldxpjson.equals(xpjson)){
			firePropertyChange("xpjson", oldxpjson, xpjson);
		}
	}

	public String getSchemaUrl() {
		return m_schemaURL;
	}

	@JsonSetter("schemaUrl")
	public void setSchemaUrl(String schema) {
		String oldschemaurl = m_schemaURL;
		m_schemaURL = schema;
		if (oldschemaurl == null || !oldschemaurl.equals(schema)){
			firePropertyChange("schemaUrl", oldschemaurl, schema);
		}
	}


	/**
	 * @return the planNumber
	 */
	public int getPlanNumber() {
		return m_planNumber;
	}

	/**
	 * @param planNumber the planNumber to set
	 */
	public void setPlanNumber(int planNumber) {
		int oldplannumber = m_planNumber;
		m_planNumber = planNumber;
		if (planNumber != oldplannumber){
			firePropertyChange("planNumber", oldplannumber, planNumber);
		}
	}

	/**
	 * @return the planVersion
	 */
	public String getPlanVersion() {
		return m_planVersion;
	}

	/**
	 * @param planVersion the planVersion to set
	 */
	public void setPlanVersion(String planVersion) {
		String oldplanversion = m_planVersion;
		m_planVersion = planVersion;
		if (oldplanversion == null || !oldplanversion.equals(planVersion)){
			firePropertyChange("planVersion", oldplanversion, planVersion);
			//			if (oldplanversion != null){
			//				planVersionNameChanges();
			//			}
		}
	}

	/**
	 * @return the dateCreated
	 */
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getDateCreated() {
		return m_dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	@JsonDeserialize(using=JsonDateDeserializer.class)
	public void setDateCreated(Date dateCreated) {
		Date olddatecreated = m_dateCreated;
		m_dateCreated = dateCreated;
		if (olddatecreated == null || !olddatecreated.equals(dateCreated)){
			firePropertyChange("dateCreated", olddatecreated, dateCreated);
		}
	}

	/**
	 * @return the dateModified
	 */
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getDateModified() {
		return m_dateModified;
	}

	/**
	 * @param dateModified the dateModified to set
	 */
	@JsonDeserialize(using=JsonDateDeserializer.class)
	public void setDateModified(Date dateModified) {
		Date olddatemodified = m_dateModified;
		m_dateModified = dateModified;
		if (olddatemodified == null || !olddatemodified.equals(dateModified)){
			firePropertyChange("dateModified", olddatemodified, dateModified);
		}
	}

	/**
	 * @return the platform
	 */
	public Platform getPlatform() {
		return m_platform;
	}

	/**
	 * @param platform the platform to set
	 */
	public void setPlatform(Platform platform) {
		Platform oldplatform = m_platform;
		m_platform = platform;
		if (oldplatform == null || !oldplatform.equals(platform)){
			firePropertyChange("platform", oldplatform, m_platform);
		}
	}

	/**
	 * @return the defaultSpeed
	 */
	public float getDefaultSpeed() {
		return m_defaultSpeed;
	}

	/**
	 * @param defaultSpeed the defaultSpeed to set
	 */
	public void setDefaultSpeed(float defaultSpeed) {
		if(defaultSpeed < 0.001) {
			logger.error("Cannot set default speed to 0");
			return;
		}
		float oldDefaultSpeed = m_defaultSpeed;
		m_defaultSpeed = defaultSpeed;
		if (oldDefaultSpeed != defaultSpeed){
			firePropertyChange("defaultSpeed", oldDefaultSpeed, defaultSpeed);
		}
	}

	public float getDefaultTolerance() {
		return m_defaultTolerance;
	}

	public void setDefaultTolerance(float defaultTolerance) {
		float oldDefaultTolerance = m_defaultTolerance;
		m_defaultTolerance = defaultTolerance;
		if (oldDefaultTolerance != defaultTolerance){
			firePropertyChange("defaultTolerance", oldDefaultTolerance, defaultTolerance);
		}
	}

	/**
	 * @return the libraryURIs
	 */
	public Set<String> getLibraryURLs() {
		return m_libraryURIs;
	}

	/**
	 * @param libraryURIs the libraryURIs to set
	 */
	@JsonSetter("libraryUrls")
	public void setLibraryURLs(Set<String> libraryURIs) {
		Set<String> oldLibraryURLs = m_libraryURIs;
		m_libraryURIs = libraryURIs;
		if (oldLibraryURLs == null || !oldLibraryURLs.equals(libraryURIs)){
			firePropertyChange("libraryURLs", oldLibraryURLs, libraryURIs);
		}
	}

	public void addLibraryURL(String uri){
		m_libraryURIs.add(uri);
	}

	public List<String> getContributors() {
		return m_contributors;
	}

	public void setContributors(List<String> contributors) {
		m_contributors = contributors;
	}

	public void addStation(Station station) {
		Station lastStation = getPreviousStation(getSequence().size());

		// if there is a last station, then make a segment.
		if (lastStation != null){
			Segment segment = new Segment();
			segment.setStartPosition(lastStation.getEndPosition());
			segment.setEndPosition(station.getStartPosition());
			segment.setSpeed(getDefaultSpeed());
			segment.setName(lastStation.getName() + "-" + station.getName());

			super.addSequenceable(segment);
			//segment.autoId(getTrailingInt(lastStation.getId()) + 1);

		}
		super.addSequenceable(station);
		setValid(false);
	}

	public boolean addStation(int index, Station station) {
		List<Sequenceable> oldSequence = new ArrayList<Sequenceable>();
		oldSequence.addAll(m_sequence);
		
		// if we are appending do it the easy way.
		if (index == m_sequence.size()){
			addStation(station);
			return true;
		}

		// we know there will always be something next.
		boolean result = super.addSequenceable(index, station);
		if (result){
			Station previousStation = null;
			Station nextStation = null;

			Sequenceable next = m_sequence.get(index+1);
			if (next instanceof Segment){
				previousStation = getPreviousStation(index);
				// make a segment to go before.
				if (previousStation != null){
					final Segment segment = new Segment();
					segment.setStartPosition(previousStation.getEndPosition());
					segment.setEndPosition(station.getStartPosition());

					segment.setSpeed(getDefaultSpeed());
					//segment.autoId(getTrailingInt(previousStation.getId()) + 1);
					result |= super.addSequenceable(index-1, segment);
				}

			} else if (next instanceof Station){
				nextStation = (Station)next;

				// make a segment to go after
				if (nextStation != null){
					final Segment segment = new Segment();
					segment.setPrevious(station);
					segment.setNext(nextStation);
					station.setNext(segment);
					nextStation.setPrevious(segment);
					//					segment.setStartPosition(station.getEndPosition());
					//					segment.setEndPosition(nextStation.getStartPosition());
					segment.setSpeed(getDefaultSpeed());
					if (station != null){
						segment.autoName(index+1);
					}

					result |= super.addSequenceable(index+1, segment);
				}

			}
		}

		renumber(station);
		setValid(false);
		firePropertyChange("sequence", oldSequence, m_sequence);
		return result;
	}

	/**
	 * Renumber all subsequent sequenceables
	 * TODO this is probably broken now ...
	 * @param seq
	 */
	public void renumber(Sequenceable seq){
		int index = indexOf(seq);
		if (index >= 0){
			for (int i = index; i < getSequence().size(); i++){
				Sequenceable s = getSequenceable(i);
				s.autoName(i);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + (valid ? 1 : 0);
		result = prime * result + ((m_sequence == null) ? 0 : m_sequence.hashCode());
		result = prime * result + ((m_planVersion == null) ? 0 : m_planVersion.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		result = prime * result + ((m_creator == null) ? 0 : m_creator.hashCode());
		result = prime * result + ((m_dateCreated == null) ? 0 : m_dateCreated.hashCode());
		result = prime * result + ((m_dateModified == null) ? 0 : m_dateModified.hashCode());
		result = prime * result + m_planNumber;
		result = prime * result + Float.floatToIntBits(m_defaultSpeed);
		result = prime * result + Float.floatToIntBits(m_defaultTolerance);
		result = prime * result + ((m_xpjson == null) ? 0 : m_xpjson.hashCode());
		result = prime * result + ((m_site == null) ? 0 : m_site.hashCode());
		result = prime * result + ((m_platform == null) ? 0 : m_platform.hashCode());
				
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof Plan){
			Plan other = (Plan) obj;
			if (m_name == null) {
				if (other.m_name != null) {
					return false;
				}
			} else if (!m_name.equals(other.m_name)){
				return false;
			}
			if(valid != other.valid) {
				return false;
			}
			if (m_sequence == null) {
				if (other.m_sequence != null) {
					return false;
				}
			} else if (!m_sequence.equals(other.m_sequence)) {
				return false;
			}
			if (m_planVersion == null) {
				if (other.m_planVersion != null) {
					return false;
				}
			} else if (!m_planVersion.equals(other.m_planVersion)) {
				return false;
			}

			if (m_notes == null) {
				if (other.m_notes != null) {
					return false;
				}
			} else if (!m_notes.equals(other.m_notes)){
				return false;
			}

			if (m_creator == null) {
				if (other.m_creator != null) {
					return false;
				}
			} else if (!m_creator.equals(other.m_creator)){
				return false;
			}

			if (m_dateCreated == null) {
				if (other.m_dateCreated != null) {
					return false;
				}
			} else if (!datesEqualToSecond(m_dateCreated, other.m_dateCreated)) {
				return false;
			}

			if (m_dateModified == null) {
				if (other.m_dateModified != null) {
					return false;
				}
			} else if (!datesEqualToSecond(m_dateModified, other.m_dateModified)) {
				return false;
			}

			if (m_planNumber != other.m_planNumber) {
				return false;
			}

			if (m_defaultSpeed != other.m_defaultSpeed) {
				return false;
			}

			if (m_defaultTolerance != other.m_defaultTolerance) {
				return false;
			}


			if (m_xpjson == null) {
				if (other.m_xpjson != null) {
					return false;
				}
			} else if (!m_xpjson.equals(other.m_xpjson)){
				return false;
			}

			if (m_site == null) {
				if (other.m_site != null) {
					return false;
				}
			} else {
				if (!m_site.equals(other.getSite())) {
					return false;
				}
			}
			if (m_platform == null) {
				if (other.m_platform != null) {
					return false;
				}
			} else if (!m_platform.equals(other.m_platform)){
				return false;
			}

			return true;
		}
		return false;
	}

	/**
	 * Populate the contents of this plan with the values from the other plan.
	 * @param other
	 */
	public void populate(Plan other){
		if (other != null){
			setName(other.getName());
			setNotes(other.getNotes());
			setId(other.getId());

			setStartTime(other.getStartTime());

			setXpjson(other.getXpjson());
			setSchemaUrl(other.getSchemaUrl());
			getSite().populate(other.getSite());
			setCreator(other.getCreator());
			setPlanNumber(other.getPlanNumber());
			setPlanVersion(other.getPlanVersion());
			// whoa - why are we doing this?
			setDateCreated(new Date());
			setDateModified(new Date());
			try {
				if (other.getPlatform() != null){
					setPlatform(other.getPlatform().clone());
				}
			} catch (CloneNotSupportedException e1) {
				logger.error(e1);
			}

			setVisible(other.isVisible());
			setDefaultSpeed(other.getDefaultSpeed());
			setDefaultTolerance(other.getDefaultTolerance());

			m_libraryURIs.addAll(other.getLibraryURLs());

			if (other.getContributors() != null){
				List<String> contributors = new ArrayList<String>();
				contributors.addAll(other.getContributors());
				setContributors(contributors);
			}


			//TODO see if we can populate existing commands with changes ...
			clearSequence();
			for (Sequenceable s : other.getSequence()){
				try {
					addSequenceable(s.clone());
				} catch (CloneNotSupportedException e) {
					logger.error(e);
				}
			}
		}
	}

	// returns the Sequenceable corresponding to the command number
	// Command number comes from the sequence of Spheres commands sent for this plan
	// assumes Stations aren't rapid commands and have no grandchildren
	public Sequenceable getStepNumber(int index) {
		if(index < 0) {
			return null;
		}
		ListIterator<Sequenceable> iterator = getSequence().listIterator(); 
		int count = 0;
		while (iterator.hasNext()){ 
			Sequenceable s = iterator.next();
			if (s instanceof SequenceHolder) {
				if(!((SequenceHolder) s).isEmpty()){
					ListIterator<Sequenceable> substationsIter = ((SequenceHolder)s).getSequence().listIterator();
					while (substationsIter.hasNext()) { // count the kids
						Sequenceable ss = substationsIter.next();
						if(count == index) {
							return ss;
						}
						count++;
					}
				}
			} else { // it's the first drive, count it
				if(count == index) {
					return s;
				}
				count++;
			}
		}
		return null;
	}

	@JsonIgnore
	public Station getNextStation(Sequenceable seq) {
		ListIterator<Sequenceable> iterator = getSequence().listIterator(); 
		boolean startLooking = false;
		while (iterator.hasNext()){ 
			Sequenceable s = iterator.next();
			if(s.equals(seq)) {
				startLooking = true;
			} else if(startLooking && s instanceof Station) {
				return (Station)s;
			}
			if (s instanceof SequenceHolder) {
				if(!((SequenceHolder) s).isEmpty()){
					ListIterator<Sequenceable> substationsIter = ((SequenceHolder)s).getSequence().listIterator();
					while (substationsIter.hasNext()) { // check the kids
						Sequenceable ss = substationsIter.next();
						if(ss.equals(seq)) {
							startLooking = true;
						}
					}
				}
			} else { 
				if(s.equals(seq)) {
					startLooking = true;
				}
			}
		}

		while (iterator.hasNext()){ 
			Sequenceable s = iterator.next();
			if (s instanceof Station){
				return (Station)s;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return number of steps in MacroCommand version of this plan
	 */
	@JsonIgnore
	public int getNumRapidSteps() {
		return m_numRapidSteps;
	}

	@JsonIgnore
	public int getSeqToRapidNumber(Sequenceable query) {
		// for some reason the build will not let us make abstract methods
		// all subclasses should override this or the plans will probably never show complete
		return 100;
	}

	@JsonIgnore
	public Sequenceable getSequenceableById(String id) {
		Iterator<Sequenceable> list = getFlattenedSequence().iterator();
		while(list.hasNext()) {
			Sequenceable s = list.next();
			if(s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}

	// this is just for debugging 
	/**
	 * Debug printout of positions
	 */
	public void printPlanMovement() {
		for (Sequenceable s : getSequence()){

			//System.out.print(m_commands.get(i).toString());
			System.out.print(s.getId()+"   \t");
			Position position = s.getStartPosition();
			if (position != null){
				System.out.print(position.getPositionString());
				System.out.print(position.getOrientationString());
			}
			System.out.println("\t\t" + (s.getCalculatedDuration()));
		}
		System.out.println("---------------------------");
	}

	public static Sequenceable getPrevious(Sequenceable current) {
		Sequenceable previous = null;
		SequenceHolder parent = current.getParent();
		int index = parent.indexOf(current);
		if (index > 0) {
			previous = parent.getSequenceable(index - 1);
		} else {

			// see the parent plan first
			// parent is not a plan
			if (parent instanceof Sequenceable){
				SequenceHolder plan = ((Sequenceable)parent).getParent();
				int parentIndex = plan.indexOf((Sequenceable)parent);
				if (parentIndex > 0){
					previous = plan.getSequenceable(parentIndex - 1);
				}
			}
		}
		return previous;
	}

	/**
	 * @return
	 */
	@JsonIgnore
	public Station getFirstStation() {
		if (isEmpty()){
			return null;
		}

		for (Sequenceable s : getSequence()){
			if (s instanceof Station){
				return (Station)s;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	@JsonIgnore
	public Station getLastStation() {
		if (isEmpty()){
			return null;
		}

		int lastIndex = m_sequence.size() - 1;
		Sequenceable s = m_sequence.get(lastIndex);
		if(s instanceof Station) {
			return (Station)s;
		}
		logger.error("Plan did not end with Station");
		return null;
	}

	public static Sequenceable getNext(Sequenceable current) {
		Sequenceable next = null;
		SequenceHolder parent = current.getParent();
		int index = parent.indexOf(current);
		if (index < parent.size()-1) {
			next = parent.getSequenceable(index + 1);
		} else {

			// see the parent plan first
			// parent is not a plan
			if (parent instanceof Sequenceable){
				SequenceHolder plan = ((Sequenceable)parent).getParent();
				int parentIndex = plan.indexOf((Sequenceable)parent);
				if (parentIndex < plan.size() - 1){
					next = plan.getSequenceable(parentIndex + 1);
				}
			}
		}
		return next;
	}

	public static int getTrailingInt(String name){
		if (name != null && !name.isEmpty()){
			String chomp = name.substring(name.length() - 2);
			try {
				return Integer.parseInt(chomp); 
			} catch (NumberFormatException e){
				//				logger.error(e);
			}
		}
		return -1;
	}

	public int getStationIndex(Station station){
		int result = -1;
		if (containsSequenceable(station)){
			for (Sequenceable s : getSequence()){
				if (s instanceof Station){
					result += 1;
					if (s.equals(station)){
						return result;
					}
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object other){
		final int BEFORE = -1;
		final int EQUAL = 0;
		if (other == null){
			return BEFORE;
		}

		if (!(other instanceof Plan)){
			return BEFORE;
		}

		Plan otherPlan = (Plan)other;
		if (this == other){
			return EQUAL;
		}

		if (getId() != null && otherPlan.getId() != null){
			int result =  getId().compareTo(otherPlan.getId());
			if (result == EQUAL){
				// check the version
				if (getPlanVersion() != null && otherPlan.getPlanVersion() != null){
					return getPlanVersion().compareTo(otherPlan.getPlanVersion());
				}
			}
			return result;
		}

		return BEFORE;
	}

	@JsonIgnore
	public int getNumStations() {
		int result = 0;
		for (Sequenceable s :getSequence()) {
			if (s instanceof Station){
				result++;
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	@JsonIgnore
	public int getPlanVersionInt(){
		int result = 0;
		if (getPlanVersion() == null || getPlanVersion().isEmpty()){
			return result;
		}
		char first = getPlanVersion().charAt(0);
		return StrUtil.getAscii(first);
	}

	/**
	 * @param version
	 */
	public void setPlanVersionFromInt(int version){
		String stringVersion = new String(StrUtil.getCharFromAscii(version));
		setPlanVersion(stringVersion);
	}

	public void planVersionNameChanges(){
		int i = 0;
		for (Sequenceable s : getSequence()){
			s.autoName(i);
			if (s instanceof SequenceHolder){
				SequenceHolder child = (SequenceHolder)s;
				int j = 0;
				for (Sequenceable gc : child.getSequence()){
					gc.autoName(j);
					j++;
				}
			}
			i++;
		}
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean validated) {
		boolean oldvalid = valid;
		valid = validated;
		if (oldvalid != validated){
			firePropertyChange("valid", oldvalid, validated);
		}
	}


	/**
	 * @return a separator to use for generating ids
	 */
	@JsonIgnore
	public String getSeparator(){
		return "_";
	}

	/**
	 * @return the file extension for this type of plan
	 */
	@JsonIgnore
	public abstract String getFileExtension();

	/**
	 * @return the category for this type of plan, for conversion to/from macro config
	 */
	@JsonIgnore
	public abstract String getCategory();

	@JsonIgnore
	public PlanBuilder getBuilder() {
		return m_builder;
	}

	@JsonIgnore
	public void setBuilder(PlanBuilder builder) {
		m_builder = builder;
	}

	public void refresh() {
		//
	}
	protected boolean datesEqualToSecond(Date d1, Date d2) {
		long l1 = d1.getTime();
		long l2 = d2.getTime();

		long ll1 = (long) (l1/1000.0);
		long ll2 = (long) (l2/1000.0);

		return ll1 == ll2;
	}
}