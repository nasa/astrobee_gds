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
package gov.nasa.arc.verve.freeflyer.workbench.utils;

import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.PlanLibrary;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;

import java.io.File;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

public class CSVPlanConverter {
	
	private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
	private static final String suffix = ".fplan";
	
	public static boolean CSVToPlanFile(File csv, String newName) {
		return convert(csv, newName);
	}
	
	private static boolean convert(File csv, String newName) {
		newName = newName.split("\\.")[0];
		String csvPath = findThePath(csv.getPath());
		PlanBuilder<ModuleBayPlan> planBuilder = constructPlanBuilder(csvPath + newName + suffix);
		ModuleBayPlan plan = planBuilder.getPlan();
		try {
			Scanner reader = new Scanner(csv);
			while(reader.hasNextLine()) {
				String[] paramsStrings = reader.nextLine().split(",\\s*");
				float[] params = parseStrings(paramsStrings);
				ModuleBayStation station = new ModuleBayStation();
				if(params.length == 3) {
					station.setCoordinate(new ModuleBayPoint(params[0], params[1], params[2]));
				} else {
					station.setCoordinate(new ModuleBayPoint(params[0], params[1], params[2],
															 params[3], params[4], params[5]));
				}
				plan.addStation(station);
			}
			reader.close();
		} catch (Exception e) {
			System.err.println("Failed while parsing CSV. Check for extra newlines and lines without 3 or 6 numbers.");
			return false;
		}
		
		try {
			planBuilder.savePlanToFile();
		} catch (Exception e) {
			System.err.println("Failed while saving to fplan file. Make sure you entered a valid name.");
			return false;
		}
		
		return true;
	}
	
	private static float[] parseStrings(String[] strings) {
		float[] floats = new float[strings.length];
		for(int i = 0; i < strings.length; i++) {
			floats[i] = Float.parseFloat(strings[i]);
		}
		return floats;
	}
	
	private static PlanBuilder<ModuleBayPlan> constructPlanBuilder(String path) {
		PlanBuilder<ModuleBayPlan> planBuilder = PlanBuilder.getPlanBuilder(new File(path), ModuleBayPlan.class, true);

		String planName = "";
		int sep = path.lastIndexOf(File.separator);
		int sepIndex = Math.max(0, sep);
		int dotIndex = path.lastIndexOf(".");
		if (dotIndex != -1) {
			planName = path.substring(sepIndex+1, dotIndex);
		} else {
			planName = path.substring(sepIndex);
		}

		Plan plan = planBuilder.constructPlan();
		plan.setName(planName);
		UUID uuid = UUID.randomUUID();
		plan.setId(uuid.toString());
		String username = System.getProperty("user.name");
		if(username != null) {
			plan.setCreator(username);
		}
		plan.setDateCreated(new Date());
		PlanLibrary library = planBuilder.getProfileManager().getLibrary();
		if (library != null) {
			plan.setSite(library.getSites().get(0));
			plan.setXpjson(library.getXpjson());
			plan.setPlatform(library.getPlatforms().get(0));
		} 
		return planBuilder;
	}
	
	private static String findThePath(String filepath) {
		String[] pathparts;
		if(IS_WINDOWS) {
			pathparts = filepath.split("\\\\");
		} else {
			pathparts = filepath.split("/");
		}
		int numparts = pathparts.length;
		
		String path = "";
		for(int i = 0; i < numparts - 1; i++) {
			path += pathparts[i] + File.separator;
		}
		return path;
	}
}
