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
package gov.nasa.arc.verve.freeflyer.workbench.undo;

import gov.nasa.arc.ff.ocu.commands.CommandStack;
import gov.nasa.arc.ff.ocu.commands.ReversibleCommand;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor.PlanFileManager;
import gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor.SelectionEditorView;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.AddViaMapPlane;
import gov.nasa.arc.verve.freeflyer.workbench.widget.StationAndPointWidget;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;

import com.ardor3d.math.type.ReadOnlyVector3;

public class DelegateCommandStack {
	private static Logger logger = Logger.getLogger(DelegateCommandStack.class);

	@Inject @Optional
	private SelectionEditorView selectionEditor;
	@Inject @Optional
	private TypedObject currentSequenceable;
	@Inject
	protected IEclipseContext context;
	@Inject
	protected PlanFileManager planFileManager;
	@Inject @Optional @Named(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR)
	protected PlanBuilder<ModuleBayPlan> planBuilder; // the specific plan builder for this editor
	private final float epsilon = 0.001f;
	protected boolean clickToAddMode = false;

	@Inject
	public DelegateCommandStack() {
	}

	@PostConstruct
	public void postConstruct() {
		context.set(DelegateCommandStack.class, this);
	}

	public Sequenceable onAppend() {
		if(planBuilder == null) {
			logger.error("No plan selected.");
			return null;
		}
		AppendCommand command = new AppendCommand(planBuilder.getPlan());
		return runCommand(command);
	}

	public Sequenceable onAppendCommandCommand(String command) {
		if(planBuilder == null) {
			logger.error("No plan selected");
			return null;
		}
		AppendCommandCommand appendCommand = new AppendCommandCommand(planBuilder.getPlan(), (Station) currentSequenceable, command);
		return runCommand(appendCommand);
	}

	public Sequenceable onInsertAfter() {
		if(planBuilder == null) {
			logger.error("No plan selected.");
			return null;
		}
		if(currentSequenceable instanceof Station) {
			InsertStationAfterCommand command = new InsertStationAfterCommand(planBuilder.getPlan(), (Station)currentSequenceable);
			return runCommand(command);
		}
		else{
			return onInsert();
		}
	}

	public Sequenceable onInsert() {
		if(planBuilder == null) {
			logger.error("No plan selected.");
			return null;
		}
		InsertStationCommand command = new InsertStationCommand(planBuilder.getPlan(), (Sequenceable)currentSequenceable);
		return runCommand(command);
	}

	public Sequenceable onDelete() {
		if(planBuilder == null) {
			logger.error("No plan selected.");
			return null;
		}
		System.out.println("DelegateCommandStack onDelete");
		DeleteCommand command = new DeleteCommand(planBuilder.getPlan(), (Sequenceable)currentSequenceable);
		return runCommand(command);
	}

	public Sequenceable onMoveUp() {
		if(planBuilder == null) {
			logger.error("No plan selected.");
			return null;
		}
		MoveUpCommand command = new MoveUpCommand(planBuilder.getPlan(), (Sequenceable)currentSequenceable);
		return runCommand(command);
	}

	public Sequenceable onMoveDown() {
		if(planBuilder == null) {
			logger.error("No plan selected.");
			return null;
		}
		MoveDownCommand command = new MoveDownCommand(planBuilder.getPlan(), (Sequenceable)currentSequenceable);
		return runCommand(command);
	}

	@Inject @Optional
	public void receiveStationLocation(@Named(ContextNames.NEW_STATION_LOCATION) Point6Dof newLoc) {
		if(currentSequenceable instanceof ModuleBayStation) {
			UpdateValueCommand command = new UpdateValueCommand((ModuleBayStation) currentSequenceable, newLoc);
			runCommand(command);
		}
	}

	@Inject @Optional
	public void appendFromClickToAdd(@Named(ContextNames.ADD_VIA_MAP_LOCATION) ReadOnlyVector3 loc) {
		AppendCommand appendCommand = new AppendCommand(planBuilder.getPlan());
		Sequenceable newStation = runCommand(appendCommand);
		UpdateValueCommand moveCommand = new UpdateValueCommand((ModuleBayStation) newStation, loc);
		runCommand(moveCommand);
	}

	public void moveCurrentStationTo(ReadOnlyVector3 loc) {
		AbstractDatabindingWidget m_currentCommandWidget = selectionEditor.getCurrentCommandWidget();
		if(m_currentCommandWidget instanceof StationAndPointWidget) {
			StationAndPointWidget sw = (StationAndPointWidget) m_currentCommandWidget;
			float x = roundToZero(loc.getXf());
			float y = roundToZero(loc.getYf());
			float z = roundToZero(loc.getZf());

			sw.moveTo(x, y, z);
			markPlanDirty();
		}
	}

	private Sequenceable runCommand(ReversibleCommand command) {
		Sequenceable seq = command.runCommand();
		CommandStack.getInstance().addCommand(command);
		updateUndoEnabled();
		setPlanBuilderInContext();
		setSequenceableInContext(seq);
		markPlanDirty();
		return seq;
	}

	public Sequenceable onUndo() {
		Sequenceable seq = CommandStack.getInstance().undo();
		updateUndoEnabled();
		setPlanBuilderInContext();
		setSequenceableInContext(seq);
		return seq;
	}

	protected void updateUndoEnabled() {
		context.set(ContextNames.UNDO_ENABLED, CommandStack.getInstance().canUndo());
	}

	protected float roundToZero(float f) {
		if(f < epsilon && f > -epsilon) {
			f = 0f;
		}
		return f;
	}

	private void markPlanDirty() {
		if(planFileManager == null) {
			logger.error("We don't have a PlanFileManager");
			return;
		}
		planFileManager.setPlanDirty();
		planBuilder.getPlan().setValid(false);
		//	XXX FIX THIS	m_createPlanView.refreshValidField();
	}

	public void enterClickToAddMode() {
		clickToAddMode = true;
		AddViaMapPlane.getStaticInstance().showPlane();
		if(selectionEditor != null) {
			selectionEditor.showClickToAddAdjustmentWidget();
		}
	}

	public void exitClickToAddMode() {
		clickToAddMode = false;
		AddViaMapPlane.getStaticInstance().hidePlane();
		if(selectionEditor != null) {
			selectionEditor.hideClickToAddAdjustmentWidget();
		}
	}

	public void setPlanBuilderInContext() {
		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, planBuilder);
	}

	public void setSequenceableInContext(Sequenceable seq) {
		context.set(Sequenceable.class, seq);
	}
}
