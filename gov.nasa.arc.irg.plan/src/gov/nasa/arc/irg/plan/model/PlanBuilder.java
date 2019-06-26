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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * 
 * A PlanBuilder has a SINGLE plan.  It can load or save the plan, and it keeps track of the dirty state of the plan.
 * It references a PlanBuilderConfiguration which has all the necessary settings for loading and storing a json plan.
 * 
 * @author tecohen
 *
 *
 * @param <T> the plan class that this builder supports
 */
@SuppressWarnings("rawtypes")
public class PlanBuilder<T extends Plan> {
	
	private static final Logger logger = Logger.getLogger(PlanBuilder.class);

	protected static HashMap<String, Class> s_extensionMap = new HashMap<String, Class>(); 
	protected static HashMap<String, String> s_planTypeExtensionMap = new HashMap<String, String>();
	protected static HashMap<File, PlanBuilder> s_planMap = new HashMap<File, PlanBuilder>();
	
	protected Class<T> m_planClass;
	protected T m_plan; // the plan we are building
	private boolean m_dirty = false;
	protected PlanBuilderConfiguration m_configuration;
	protected Date m_lastLoaded;
	protected File m_planFile = null;
	
	public static void addExtension(String extension, Class planClass, String planType){
		s_extensionMap.put(extension, planClass);
		s_planTypeExtensionMap.put(planType, extension);
	}
	
	public static Class getClassForExtension(String extension){
		return s_extensionMap.get(extension);
	}

	public static String[] getExtensions(){
		return s_extensionMap.keySet().toArray(new String[s_extensionMap.size()]);
	}
	
	/**
	 * Return existing plan builder for a given file
	 * returns null if it does not exist
	 * @param f
	 * @return
	 */
	public static PlanBuilder getPlanBuilder(File f)  {
		return s_planMap.get(f);
	}
	
	public static void removePlanBuilder(File f) {
		s_planMap.remove(f);
	}
	
	/**
	 * Return existing or construct a new plan builder.
	 * 
	 * @param f  the file with the plan in it
	 * @param load true to load the plan
	 * @return
	 */
	public static PlanBuilder getPlanBuilder(File f, boolean load)  {
		PlanBuilder found = s_planMap.get(f);
//		if (found != null){
//			// check if file has changed
//			long lastMod = f.lastModified();
//			Date lastModDate = new Date(lastMod);
//			if (lastModDate.after(found.m_lastLoaded)) {
//				s_planMap.remove(f);
//				found = null;
//			}
//		}
		if (found == null){
			String extension = FilenameUtils.getExtension(f.getName());
			Class planClass = s_extensionMap.get(extension);
			if (planClass == null){
				return null;
			}
			found = new PlanBuilder(planClass);
			found.setPlanFile(f);
			s_planMap.put(f, found);
			if (f.exists() && load){
				try {
					found.loadPlan(f);
				} 
				catch (Exception e) {
					logger.error("error loading plan: "+f.getName(), e);
				}
			}
		} 
		return found;
	}
	
	/**
	 * Get the plan builder given the class
	 * @param f
	 * @param planClass
	 * @param load
	 * @return
	 */
	public static PlanBuilder getPlanBuilder(File f, Class planClass, boolean load)  {
		PlanBuilder found = s_planMap.get(f);
		if (found == null){
			String extension = FilenameUtils.getExtension(f.getName());
			if (s_extensionMap.get(extension) == null){
				s_extensionMap.put(extension, planClass);
			}
			found = new PlanBuilder(planClass);
			found.setPlanFile(f);
			s_planMap.put(f, found);
			if (f.exists() && load){
				try {
					found.loadPlan(f);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					logger.error(e);
				}
			}
		}
		return found;
	}
	
	/**
	 * If you have a plan that you magically got (say from a rover) and you want to save it to a file, 
	 * make a PlanBuilder to help you.
	 * @param plan  the plan that you want to save
	 * @param f  the file where you want to save it.
	 * @return
	 */
	public static PlanBuilder getPlanBuilder(Plan plan, File f){
		PlanBuilder found = s_planMap.get(f);
		if (found == null){
			found = new PlanBuilder(plan.getClass());
			found.setPlanFile(f);
		}
		found.setPlan(plan);
		return found; 
	}
	
	private PlanBuilder(T plan, Class<T> planClass){
		m_plan = plan;
		initialize(planClass);
	}
	
	private PlanBuilder(Class<T> planClass) {
		initialize(planClass);
	}
	
	protected void initialize(Class<T> planClass){
		m_planClass = planClass;
		try {
			Method m = planClass.getMethod("getPlanBuilderConfiguration");
			m_configuration = (PlanBuilderConfiguration)m.invoke(null, new Object[0]);
		} catch (SecurityException e) {
			logger.error(e);
		} catch (NoSuchMethodException e) {
			logger.error(e);
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
	}

	public T getPlan() {
		return m_plan;
	}
	
	protected void setPlan(T plan){
		m_plan = plan;
	}
	
	/**
	 * @return the planFile
	 */
	public File getPlanFile() {
		return m_planFile;
	}

	/**
	 * @param planFile the planFile to set
	 */
	public void setPlanFile(File planFile) {
		m_planFile = planFile;
	}

	public T constructPlan(){
		try {
			Constructor constructor = getPlanClass().getConstructor((Class[])null);
			m_plan = (T) constructor.newInstance((Object[])null);
			return m_plan;
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		} catch (SecurityException e) {
			logger.error(e);
		} catch (NoSuchMethodException e) {
			logger.error(e);
		}
		return null;
	}


	public Class<T> getPlanClass() {
		return m_planClass;
	}

	public boolean isDirty() {
		return m_dirty;
	}

	public void setDirty(boolean dirty) {
		m_dirty = dirty;
	}


	public T loadPlan(File f) throws Exception {
		if (f == null) {
			throw new Exception("No plan file to load.");
		}
		if  (!(f.exists())){
			if (this.getPlanFile() != null) {
				f = this.getPlanFile();
				if (!f.exists()){
					throw new IOException(f.getAbsolutePath() + " does not exist");
				}
			}
			// see if removing spaces helps
//			String absPath = f.getAbsolutePath();
//			int lastSlash = absPath.lastIndexOf(File.separator);
//			String filename = absPath.substring(lastSlash + 1);
//			filename = filename.replaceAll("\\s+","");
//			String newPath = absPath.substring(0, lastSlash) + File.separator + filename;
//			f = new File(newPath);
//			if (!f.exists()) {
//				throw new IOException(f.getAbsolutePath() + " does not exist");
//			}
		}
		
		boolean load = true;
		if (m_plan != null){
			if ((m_planFile != null) && (!f.equals(m_planFile))){
				throw new Exception("You are trying to read the plan with the wrong plan builder! " + f.getAbsolutePath());
			}
			Date fileModDate = new Date(f.lastModified());
			if (m_lastLoaded != null && m_lastLoaded.after(fileModDate)) {
				load = false;
			}
		}
		
		if (load){
			this.m_planFile = f;
			this.m_plan = null;
			return loadPlan(FileUtils.readFileToString(f));
		} else {
			return m_plan;
		}
	}
	
	public T loadPlan(String s) throws Exception {
		if (s == null || s.isEmpty()) {
			return null;
		}
		T plan = m_configuration.getMapper().readValue(s, m_planClass);
		m_lastLoaded = new Date();
		
		if (plan != null) {
			if (m_plan != null){
				m_plan.populate(plan);
			} else {
				m_plan = plan;
			}
		}
		
		m_plan.initialize();
		m_plan.setBuilder(this);

		return m_plan;
	}

	public String exportPlan() throws Exception {
		if (m_plan == null) {
			return null;
		}
		m_plan.setDateModified(new Date());
		String result = m_configuration.getMapper().writeValueAsString(m_plan);
		return result;
	}
	
	public void savePlanToFile() throws Exception {
		if (m_plan == null || m_planFile == null) {
			return;
		}
		
		String contents = exportPlan();
		BufferedWriter bw = new BufferedWriter(new FileWriter(m_planFile));
		Exception e = null;
		try {
            bw.write(contents);
		} catch (Exception ex){
			e = ex;
		} finally {
			bw.close();
		}
		if (e != null){
			throw(e);
		}
		return;
		
	}
	
	public ProfileManager getProfileManager() {
		if (m_configuration != null){
			return m_configuration.getProfileManager();
		}
		return null;
	}
	
	public void dispose() {
		s_planMap.remove(m_planFile);
	}
	
	/** @return true if file deleted successfully */
	public boolean deleteFile() {
		s_planMap.remove(m_planFile);
		return m_planFile.delete();
	}
	
	public static String getExtension(String planType){
		return s_planTypeExtensionMap.get(planType);
	}

}
