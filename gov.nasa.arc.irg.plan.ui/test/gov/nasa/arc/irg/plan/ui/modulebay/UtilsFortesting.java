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
package gov.nasa.arc.irg.plan.ui.modulebay;

import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfigList;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.PlanLibrary;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class UtilsFortesting {

	public static void printUnequalSequenceables(final ModuleBayPlan created, final ModuleBayPlan loaded) {
		for(int i=0; i<created.getSequence().size(); i++) { 
			if(!created.getSequence().get(i).equals(loaded.getSequence().get(i))) {
				Sequenceable cr = created.getSequence().get(i);
				Sequenceable ld = loaded.getSequence().get(i);
				System.out.println(cr.toString() + " does not equal " + 
						ld.toString() );
			}
		}
	}

	public static boolean planPayloadConfigListsShouldBeEqual(final PlanPayloadConfigList first, final PlanPayloadConfigList second) {
		if(first == second) {
			return true;
		}
		if(!(first instanceof PlanPayloadConfigList)) {
			fail("first PlanPayloadConfigList is not PlanPayloadConfigList");
		}
		if(!(second instanceof PlanPayloadConfigList)) {
			fail("second PlanPayloadConfigList is not PlanPayloadConfigList");
		}

		if(first.getType() == null) {
			if(second.getType() != null) {
				fail("first type is null but second type is not");
			}
		} else if(!first.getType().equals(second.getType())) {
			fail("types don't match");
		}

		List<PlanPayloadConfig> theseConfigs = first.getPlanPayloadConfigs();
		List<PlanPayloadConfig> otherConfigs = second.getPlanPayloadConfigs();

		if(theseConfigs.size() != otherConfigs.size()) {
			fail("lengths of PlanPayloadConfig lists don't match");
		}

		for(int i=0; i<theseConfigs.size(); i++) {
			if(!planPayloadConfigsShouldMatch(theseConfigs.get(i),otherConfigs.get(i))){
				fail("config " + i + " doesn't match");
			}
		}
		return true;
	}

	public static boolean planPayloadConfigsShouldMatch(final PlanPayloadConfig first, final PlanPayloadConfig second) {
		double EPSILON = 0.0001;

		if(first == second) {
			return true;
		}
		if(!(first instanceof PlanPayloadConfig)) {
			fail("first PlanPayloadConfig is not PlanPayloadConfig");
		}
		if(!(second instanceof PlanPayloadConfig)) {
			fail("second PlanPayloadConfig is not PlanPayloadConfig");
		}
	
		if(!first.getName().equals(second.getName())) {
			return false;
		}
		if(Math.abs(first.getPower() - second.getPower()) > EPSILON) {
			return false;
		}
		return true;
	}

	public static boolean moduleBayPlansShouldBeEqual(final ModuleBayPlan first, final ModuleBayPlan second) {
		if (first == second)
			return true;
		if (second == null) {
			fail("Second ModuleBayPlan is null");
			return false;
		}
		if (first.getClass() != second.getClass()) {
			fail("classes of ModuleBayPlan not equal");
		}

		if (first.getInertiaConfiguration() == null) {
			if (second.getInertiaConfiguration() != null) {
				fail("First inertiaConfig null, second not null");
			}
		} else if (!first.getInertiaConfiguration().equals(second.getInertiaConfiguration())) {
			fail("inertiaConfigs not equal");
		}

		if (first.getOperatingLimits() == null) {
			if (second.getOperatingLimits() != null) {
				fail("First operatingLimits null, second not null");
			}
		} else if (!first.getOperatingLimits().equals(second.getOperatingLimits())) {
			fail("operatingLimits not equal");
		}

		if(!freeFlyerPlansShouldBeEqual(first, second)) {
			fail("plans not equal");
		}
		return true;
	}

	public static boolean freeFlyerPlansShouldBeEqual(final FreeFlyerPlan first, final FreeFlyerPlan second) {
		if (first == second)
			return true;
		if (second == null && first != null) {
			fail("second plan is null and first isn't");
			return false;
		}
		if (first.getClass() != second.getClass())
			fail("classes of plans do not match");

		if (first.getName() == null) {
			if (second.getName() != null) {
				fail("first plan name is null and second isn't");
			}
		} else if (!first.getName().equals(second.getName())){
			fail("plan names don't match");
		}
		if(first.isValid() != second.isValid()) {
			if(first.isValid() && !second.isValid()) {
				fail("first planValid true and second false");
			} else {
				if(!first.isValid() && second.isValid()) {
					fail("first planValid false and second true");
				}
			}
			fail("plan valids don't match:");
		}

		if (first.getPlanVersion() == null) {
			if (second.getPlanVersion() != null) {
				fail("first plan version is null and second isn't");
			}
		} else if (!first.getPlanVersion().equals(second.getPlanVersion())) {
			fail("plan versions don't match");
		}

		if (first.getNotes() == null) {
			if (second.getNotes() != null) {
				fail("first plan notes is null and second isn't");
			}
		} else if (!first.getNotes().equals(second.getNotes())){
			fail("plan notes don't match");
		}

		if (first.getCreator() == null) {
			if (second.getCreator() != null) {
				fail("first plan creator is null and second isn't");
			}
		} else if (!first.getCreator().equals(second.getCreator())){
			fail("plan creators don't match");
		}

		if (first.getPlanNumber() != second.getPlanNumber()) {
			fail("plan numbers don't match");
		}

		if (first.getDefaultSpeed() != second.getDefaultSpeed()) {
			fail("plan default speeds don't match");
		}

		if (first.getDefaultTolerance() != second.getDefaultTolerance() ) {
			fail("plan default tolerances don't match");
		}

		if (first.getXpjson() == null) {
			if (second.getXpjson() != null) {
				fail("first plan xpjson is null and second isn't");
			}
		} else if (!first.getXpjson().equals(second.getXpjson())){
			fail("plan xpjsons don't match");
		}

		if (first.getSite() == null) {
			if (second.getSite() != null) {
				fail("first plan site is null and second isn't");
			}
		} else {
			if (!first.getSite().equals(second.getSite())) {
				fail("plan sites don't match");
			}
		}
		if (first.getPlatform() == null) {
			if (second.getPlatform() != null) {
				fail("first plan platform is null and second isn't");
			}
		} else if (!first.getPlatform().equals(second.getPlatform())){
			fail("plan platforms don't match");
		}

		if (first.getSequence() == null) {
			if (second.getSequence() != null) {
				fail("first plan sequence is null and second isn't");
			}
		} else if (!sequencesShouldBeEqual(first.getSequence(),second.getSequence())) {
			fail("plan sequences don't match");
		}

		if (first.getDateCreated() == null) {
			if (second.getDateCreated() != null) {
				fail("first plan date created is null and second isn't");
			}
		} else if (!datesEqualToSecond(first.getDateCreated(), second.getDateCreated())) {
			fail("plan dates created don't match");
		}

		if (first.getDateModified() == null) {
			if (second.getDateModified() != null) {
				fail("first plan date modified is null and second isn't");
			}
		} else if (!datesEqualToSecond(first.getDateModified(), second.getDateModified())) {
			fail("plan dates modified don't match");
		}

		return true;
	}

	public static boolean sequencesShouldBeEqual(List<Sequenceable> first, List<Sequenceable> second) {
		if(first.size() != second.size()) {
			fail("plan sequences are different lengths");
		}

		for(int i=0; i<first.size(); i++) {
			if(!first.get(i).equals(second.get(i))) {
				String errStr = first.get(i).toString() + " does not equal " + second.get(i).toString();
				if(first.get(i) instanceof Segment) {
					Segment one = (Segment)first.get(i);
					Segment two = (Segment)second.get(i);

					System.out.println("first:");
					System.out.println(one.getWaypoints());
					System.out.println("second:");
					System.out.println(two.getWaypoints());

				}
				fail(errStr);
			}
		}
		return true;
	}

	protected static boolean datesEqualToSecond(final Date d1, final Date d2) {
		final long l1 = d1.getTime();
		final long l2 = d2.getTime();

		final long ll1 = (long) (l1/1000.0);
		final long ll2 = (long) (l2/1000.0);

		return ll1 == ll2;
	}

	public static Date makeDateFromStringPST(String str) {
		return makeDateFromStringGeneric(str, 8);
	}

	public static Date makeDateFromStringPDT(String str) {
		return makeDateFromStringGeneric(str, 7);
	}

	public static Date makeDateFromStringGeneric(String str, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		String delims = "[-T:Z]";
		String[] tokens = str.split(delims);
		int year = Integer.parseInt(tokens[0]);
		// month is 0-based(?!)
		int month = Integer.parseInt(tokens[1]) - 1;
		int day = Integer.parseInt(tokens[2]);
		// Date is written in GMT (PST + hours)
		int hour = Integer.parseInt(tokens[3]) - hours;
		if(hour < 0) {
			hour += 24;
			day -= 1;
		}
		int minute = Integer.parseInt(tokens[4]);
		int second = Integer.parseInt(tokens[5]);
		cal.set(year, month, day, hour, minute, second);
		return cal.getTime();
	}


	// This is copied from PlanFileManager
	public static <T extends Plan> T createNewPlan(String path, Class<T> planClass) {
		PlanBuilder<T> planBuilder = PlanBuilder.getPlanBuilder(new File(path), planClass, false);
		String planName = "";
		int sepIndex = path.lastIndexOf(File.separator);
		//int sepIndex = Math.max(0, sep);
		int dotIndex = path.lastIndexOf(".");
		if (dotIndex != -1) {
			planName = path.substring(sepIndex+1, dotIndex);
		} else {
			planName = path.substring(sepIndex);
		}

		T plan = planBuilder.constructPlan();
		plan.setName(planName);
		UUID uuid = UUID.randomUUID();
		plan.setId(uuid.toString());
		// Just for the tests
		plan.setCreator("ddwheele");
		plan.setDateCreated(new Date());
		PlanLibrary library = planBuilder.getProfileManager().getLibrary();
		if (library != null) {
			plan.setSite(library.getSites().get(0));
			plan.setXpjson(library.getXpjson());
			plan.setPlatform(library.getPlatforms().get(0));
		} 
		return plan;
	}
}
