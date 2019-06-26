package gov.nasa.freeflyer.test;

import gov.nasa.arc.irg.plan.ui.modulebay.TestBookmarksList;
import gov.nasa.arc.irg.plan.ui.modulebay.TestModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.modulebay.TestModuleBayPlan1;
import gov.nasa.arc.irg.plan.ui.modulebay.TestModuleBayStation;
import gov.nasa.arc.verve.freeflyer.workbench.TestBoxLoading;
import gov.nasa.arc.verve.freeflyer.workbench.TestConvertUtils;
import gov.nasa.arc.verve.freeflyer.workbench.TestDelegateCommandStack;
import gov.nasa.arc.verve.freeflyer.workbench.TestFaultConfig;
import gov.nasa.arc.verve.freeflyer.workbench.TestGuestScienceConfig;
import gov.nasa.arc.verve.freeflyer.workbench.TestInertiaConfig;
import gov.nasa.arc.verve.freeflyer.workbench.TestKeepoutConfig;
import gov.nasa.arc.verve.freeflyer.workbench.TestOperatingLimitsConfig;
import gov.nasa.arc.verve.freeflyer.workbench.TestSetCameraPresets;
import gov.nasa.arc.verve.freeflyer.workbench.TestStateChangesPlain;
import gov.nasa.arc.verve.freeflyer.workbench.TestZonesConfig;
import gov.nasa.arc.verve.freeflyer.workbench.modulebay.TestCoordinatesGenerator;
import gov.nasa.arc.verve.freeflyer.workbench.modulebay.TestLocationMap;
import gov.nasa.arc.verve.freeflyer.workbench.modulebay.TestModuleBayPoint;
import gov.nasa.arc.verve.freeflyer.workbench.plancompiler.TestBoxMath;
import gov.nasa.arc.verve.freeflyer.workbench.plancompiler.TestFullState;
import gov.nasa.arc.verve.freeflyer.workbench.plancompiler.TestPointCommand;
import gov.nasa.arc.verve.freeflyer.workbench.plancompiler.TestQuaternion;
import gov.nasa.arc.verve.freeflyer.workbench.plancompiler.TestSimplifiedState;
import gov.nasa.arc.verve.freeflyer.workbench.plancompiler.TestTrajectoryBoundsCheck;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({TestBookmarksList.class,TestBoxLoading.class,TestBoxMath.class,
	TestConvertUtils.class, TestCoordinatesGenerator.class,
	TestDelegateCommandStack.class,			
	TestFaultConfig.class,TestFullState.class,
	TestGuestScienceConfig.class,
	TestInertiaConfig.class,
	TestKeepoutConfig.class,
	TestLocationMap.class,
	TestModuleBayPlan.class,TestModuleBayPlan1.class,TestModuleBayPoint.class,TestModuleBayStation.class,
	TestOperatingLimitsConfig.class,
	TestPointCommand.class,//TestPlanStatusHandler.class, - deadlocks test
	TestQuaternion.class,
	TestSetCameraPresets.class,TestSimplifiedState.class,TestStateChangesPlain.class,
	TestTrajectoryBoundsCheck.class,
	TestZonesConfig.class
})
public class AllTests {
	public static Test suite(){
		return new JUnit4TestAdapter(AllTests.class);
	}
}
