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
package gov.nasa.arc.irg.plan.schema;

import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class PlanSchemaBuilder {
	
	Logger logger = Logger.getLogger(PlanSchemaBuilder.class);
	protected ObjectMapper m_mapper; // for converting to/from JSON
	protected PlanSchema m_planSchema;
//	protected URL m_url;
	protected InputStream m_schemaInputStream;
	protected PlanBuilderConfiguration m_planBuilderConfiguration;

	protected static HashMap<Class<? extends Plan>, PlanSchema> m_schemaMap = new HashMap<Class<? extends Plan>, PlanSchema>();
	
	public static PlanSchema loadPlanSchema(Class<? extends Plan> pClass, InputStream schemaInputStream, PlanBuilderConfiguration planBuilderConfiguration){
		
		PlanSchema result = m_schemaMap.get(pClass);
		if (result == null){
			PlanSchemaBuilder builder = new PlanSchemaBuilder(schemaInputStream, planBuilderConfiguration);
			result = builder.getPlanSchema();
//			result.setUrl(url);
			m_schemaMap.put(pClass, result);
		}
		return result;
	}
	
	public static PlanSchema getPlanSchema(Class<? extends Plan> pClass){
		return m_schemaMap.get(pClass);
	}
	
	public PlanSchemaBuilder(InputStream schemaInputStream, PlanBuilderConfiguration planBuilderConfiguration) {
		m_schemaInputStream = schemaInputStream;
		m_planBuilderConfiguration = planBuilderConfiguration;
		initJsonConverter();
		loadSchema(m_schemaInputStream);
	}
	
	protected void initJsonConverter() {
		m_mapper = new ObjectMapper();
		m_mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
	}
	
	protected void loadSchema(InputStream schemaInputStream){
		try {
			m_planSchema = m_mapper.readValue(schemaInputStream, PlanSchema.class);
			m_planSchema.createBasicCommandSpec();
			m_planSchema.constructHierarchy();
			populateCommandClasses();
			
		} catch (JsonParseException e) {
			logger.error(e);
		} catch (JsonMappingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public PlanSchema getPlanSchema() {
		return m_planSchema;
	}

	public void setPlanSchema(PlanSchema planSchema) {
		m_planSchema = planSchema;
	}
	
	protected void populateCommandClasses() {
		for (CommandSpec cs : m_planSchema.getCommandSpecs()){
			if (cs.getCommandClass() == null){
//				Class<? extends PlanCommand> commandClass = lookupCommandClass(cs.getId());
				cs.setCommandClass(m_planBuilderConfiguration.lookupCommandClass(cs.getId()));
			}
			cs.lookupValues();
		}
	}
	
	/**
	 * Look up the Java class representing this command spec.
	 * This must be called from the plugin containing the class else it won't be found by the classloader.
	 * 
	 */
//	protected Class<? extends PlanCommand> lookupCommandClass(String name) {
//		if (name != null && !name.isEmpty()){
////			TODO TAMAR SOLVE THIS
////			ClassLoader classLoader = m_planBuilderConfiguration.getPluginClass().getClassLoader();
//			
//			
//			
//			ClassLoader classLoader = m_planBuilderConfiguration.getClass().getClassLoader();
//			assert classLoader != null;
//			
//			String vcName = m_planBuilderConfiguration.getCommandPackage() + "." + StrUtil.upperFirstChar(name, false); 
//			try {
//				Class commandClass = classLoader.loadClass(vcName);
//				if (commandClass != null){
//					return commandClass;
//				}
//			} catch (ClassNotFoundException e) {
//				logger.error(e);
//			}
//		}
//		return null;
//	}
}
