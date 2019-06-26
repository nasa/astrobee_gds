package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;

import org.junit.Test;

import rapid.AccessControlState;
import rapid.AckStatus;
import rapid.ext.astrobee.AgentState;
import rapid.ext.astrobee.PlanStatus;

// Removed SWTBot and DDS tests because our build system can't do them :(
public class TestStateChangesPlain {
	
	String fakeProfileName = "fakeProfileName";
	String fakeFlightMode = "fakeFlightMode";
	
	@Test
	public void testAgentState() {
		AggregateAstrobeeState astroState = AggregateAstrobeeState.getInstance();
		AgentState agentState = new AgentState();
		
		agentState.proximity = 1;
		agentState.profileName = fakeProfileName;
		agentState.flightMode = fakeFlightMode;
		agentState.targetLinearVelocity = 2;
		agentState.targetLinearAccel = 3;
		agentState.targetAngularVelocity = 4;
		agentState.targetAngularAccel = 5;
		agentState.collisionDistance = 6;
		agentState.enableHolonomic = true;
		agentState.checkObstacles = true;
		agentState.checkKeepouts = true;
		agentState.enableAutoReturn = true;
		agentState.bootTime = 8;		
		agentState.operatingState = rapid.ext.astrobee.OperatingState.OPERATING_STATE_PLAN_EXECUTION;
		agentState.executionState = rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_IDLE;
		agentState.mobilityState = rapid.ext.astrobee.MobilityState.MOBILITY_STATE_FLYING;
		
		AstrobeeStateGds.OperatingState[] operating = AstrobeeStateGds.OperatingState.values();
		for(int index : rapid.ext.astrobee.OperatingState.getOrdinals()) {
			agentState.operatingState = rapid.ext.astrobee.OperatingState.valueOf(index);
			astroState.ingestAgentState(agentState);
			validateAgentState(operating[index].name(), "IDLE", "FLYING");
		}
		
		AstrobeeStateGds.ExecutionState[] executing = AstrobeeStateGds.ExecutionState.values();
		for(int index : rapid.ext.astrobee.ExecutionState.getOrdinals()) {
			agentState.executionState = rapid.ext.astrobee.ExecutionState.valueOf(index);
			astroState.ingestAgentState(agentState);
			validateAgentState("FAULT", executing[index].name(), "FLYING");
		}
		
		AstrobeeStateGds.MobilityState[] mobility = AstrobeeStateGds.MobilityState.values();
		for(int index : rapid.ext.astrobee.MobilityState.getOrdinals()) {
			agentState.mobilityState = rapid.ext.astrobee.MobilityState.valueOf(index);
			astroState.ingestAgentState(agentState);
			validateAgentState("FAULT", "ERROR", mobility[index].name());
		}
		
	}
	
	private void validateAgentState(String operation, String executing, String mobility) {
		float EPSILON = 0.0001f;
		
		AstrobeeStateGds astroState = AggregateAstrobeeState.getInstance().getAstrobeeState();
		
		assertEquals("Proximity wrong.", astroState.getProximity(), 1.0f, EPSILON);
		assertTrue("Profile name wrong.", fakeProfileName.equals(astroState.getProfileName()));
		assertTrue("Flight mode wrong.", fakeFlightMode.equals(astroState.getFlightMode()));
		
		assertEquals("Target velocity wrong.", astroState.getTargetLinearVelocity(), 2, EPSILON);
		assertEquals("Target acceleration wrong.", astroState.getTargetLinearAccel(), 3, EPSILON);
		assertEquals("Target Ang Vel wrong.", astroState.getTargetAngularVelocity(), 4, EPSILON);
		assertEquals("Target Ang Accel wrong.", astroState.getTargetAngularAccel(), 5, EPSILON);
		assertEquals("ObstacleDistanceLimit wrong.", astroState.getCollisionDistance(), 6, EPSILON);
		
		assertTrue("Enable holonomic wrong.", astroState.getEnableHolonomic());
		assertTrue("Check obstacles wrong.", astroState.getCheckObstacles());
		assertTrue("Check keepouts wrong.", astroState.getCheckKeepouts());
		assertTrue("Enable autoreturn wrong.", astroState.getEnableAutoReturn());
		
		assertEquals("Boot time wrong.", astroState.getBootTime(), 8, EPSILON);
		
		assertTrue("Operating state wrong.", astroState.getOperatingState().name().equals(operation));
		assertTrue("Executing state wrong.", astroState.getPlanExecutionState().name().equals(executing));
		assertTrue("Mobility state wrong.", astroState.getMobilityStateName().equals(mobility));
	}
	
	@Test
	public void testPlanStatus() {
		PlanStatus planStatus = new PlanStatus();
		planStatus.currentCommand = 1;
		planStatus.currentPoint = 2;
		planStatus.currentStatus = AckStatus.ACK_COMPLETED;
		planStatus.planName = "test_plan";
		
		
		AggregateAstrobeeState astroState = AggregateAstrobeeState.getInstance();
		astroState.ingestPlanStatus(planStatus);		
		assertTrue("Plan name wrong.", astroState.getCurrentPlanName().equals("test_plan"));
		
		planStatus.planName = "changed";
		astroState.ingestPlanStatus(planStatus);		
		assertTrue("Plan name wrong.", astroState.getCurrentPlanName().equals("changed"));
		
		for(int index : AckStatus.getOrdinals()) {
			planStatus.currentStatus = AckStatus.valueOf(index);
			astroState.ingestPlanStatus(planStatus);		
			assertTrue("Plan name wrong.", astroState.getCurrentPlanName().equals("changed"));
		}
		
		planStatus.planName = "";
		astroState.ingestPlanStatus(planStatus);
		assertTrue("Plan name wrong.", astroState.getCurrentPlanName().equals(WorkbenchConstants.NO_PLAN_LOADED));
	}
	
	@Test
	public void testAccessControlState() {
		AccessControlState acs = new AccessControlState();
		String testString = "This little piggy went to market";
		acs.controller = testString;
		acs.requestors.userData.add("cookie");
		
		AggregateAstrobeeState astroState = AggregateAstrobeeState.getInstance();
		astroState.ingestAccessControlState(acs);
		
		assertTrue("Access controller wrong.", astroState.getAccessControl().equals(testString));
	}
	
}
