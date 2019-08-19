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
package gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor;

import gov.nasa.arc.ff.ocu.commands.CommandStack;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.PlanLibrary;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class PlanFileManager implements PropertyChangeListener {
	private static final Logger logger = Logger.getLogger(PlanFileManager.class);
	@Inject
	@Optional
	@Named(IServiceConstants.ACTIVE_SHELL)
	public Shell shell;
	@Inject
	private IEclipseContext context;
	protected final String suffix = "fplan";
	private boolean planDirty = false;
	private boolean planOpen = false;
	protected PlanBuilder<ModuleBayPlan> planBuilder; // the specific plan builder for this editor

	@Inject
	public PlanFileManager() {
	}

	@PostConstruct
	public void setup() {
		context.set(PlanFileManager.class, this);
	}

	public void onClosePlanCommand() {
		boolean goAhead = askToSave();
		if(!goAhead) {
			return;
		}
		closeThePlan();
	}

	private void closeThePlan() {
		PlanBuilder.removePlanBuilder(planBuilder.getPlanFile());
		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, null);
		setSequenceableInContext(null);

		CommandStack.getInstance().flush();
		setPlanNotDirty();
		setPlanClosed();
	}

	private boolean askToSave() {
		if(planDirty) {
			int result;

			// customized MessageDialog with configured buttons
			if(shell != null) {
				MessageDialog dialog = new MessageDialog(shell, "Save Plan?", null,
						"Save plan before closing?", MessageDialog.QUESTION, new String[] { "Save Plan",
						"Discard Changes", "Cancel" }, 0);
				result = dialog.open();
				System.out.println(result);
			} else {
				result = 1;
			}

			switch(result) {
			case 0:
				onSavePlanCommand();
				break;
			case 1:
				PlanBuilder.removePlanBuilder(planBuilder.getPlanFile());
				break;
			case 2:
				return false;
			}
		}
		return true;
	}

	public void onNewPlanCommand() {
		if(planOpen) {
			onClosePlanCommand();
		}
		String path = showNewPlanDialog();

		if(path != null) {
			setupNewPlan(path);
		}
	}

	public void onOpenPlanCommand() {
		String path = showOpenPlanDialog();

		if(path != null) {
			if(planOpen) {
				onClosePlanCommand();
			}

			setupOpenPlan( path );
		}
	}

	public void onSaveAsCommand() {
		String path = showSaveAsDialog();

		if(path != null) {
			setupSaveAsPlan(path);
		}
	}

	public String showNewPlanDialog() {
		if(shell == null) {
			System.err.println("PlanFileManager has no Shell");
			return "";
		}

		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		String[] allowedExtension = new String[]{"*." + suffix};
		dlg.setFilterExtensions(allowedExtension);
		dlg.setText("Enter New Plan Name");

		String path = dlg.open();
		return path;
	}

	public String showOpenPlanDialog() {
		if(shell == null) {
			System.err.println("PlanFileManager has no Shell");
			return "";
		}

		FileDialog dlg = new FileDialog(shell,  SWT.OPEN  );
		String[] extension = new String[]{"*." + suffix};
		dlg.setFilterExtensions(extension);
		dlg.setText("Select a Plan");
		String path = dlg.open();
		if (path == null || path.length() == 0) {
			// TODO make an error
			logger.error("Selected plan was null");
		}
		return path;
	}

	public String showSaveAsDialog() {
		return showNewPlanDialog();
	}

	public void setupOpenPlan(String filename) {
		File selectedPlanFile = new File(filename);
		planBuilder = PlanBuilder.getPlanBuilder(selectedPlanFile, ModuleBayPlan.class, true);
		if(planBuilder == null || planBuilder.getPlan() == null) {
			if(shell != null) {
				MessageBox dialog = 
						new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText("Open Plan Failed");
				dialog.setMessage("Please check the plan syntax.");
				dialog.open();
			}
			return;
		}
		planBuilder.getPlan().addPropertyChangeListener("valid", this);
		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, planBuilder);
		setPlanOpen();
	}

	public ModuleBayPlan setupNewPlan(String path) {	
		constructPlanBuilder(path);
		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, planBuilder);

		try {
			planBuilder.savePlanToFile();
		} catch (Exception e1) {
			logger.error(e1.getMessage());
			return null;
		}
		setPlanOpen();
		planBuilder.getPlan().addPropertyChangeListener("valid", this);
		return planBuilder.getPlan();
	}

	public void setupSaveAsPlan(String path) {		
		List<Sequenceable> items = extractSequenceFromPlan();
		InertiaConfig inertiaConfig = planBuilder.getPlan().getInertiaConfiguration();
		OperatingLimitsConfig opLimitsConfig = planBuilder.getPlan().getOperatingLimits();
		String description = planBuilder.getPlan().getNotes();
		closeThePlan();
		setupNewPlan(path);
		planBuilder.getPlan().setSequence(items);
		planBuilder.getPlan().setInertiaConfiguration(inertiaConfig);
		planBuilder.getPlan().setOperatingLimits(opLimitsConfig);
		planBuilder.getPlan().setNotes(description);
		onSavePlanCommand();
		// reset in context so the list in PlanEditor will expandToLevel(2);
		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, null);
		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, planBuilder);
	}

	public PlanBuilder<ModuleBayPlan> constructPlanBuilder(String path) {
		planBuilder = PlanBuilder.getPlanBuilder(new File(path), ModuleBayPlan.class, true);

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

	public void onSavePlanCommand() {
		try {
			if(planBuilder == null) {
				return;
			}
			// Validate it first
			boolean valid;
			valid = PlanCompiler.compilePlan(planBuilder.getPlan(), planBuilder.getPlan().getOperatingLimits());

			planBuilder.savePlanToFile();
			setPlanNotDirty();

			if(!valid && shell != null) {
				MessageBox dialog = 
						new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText("Validation Failed");
				String msg = PlanCompiler.getLastErrorMessage();
				String firstLetter = msg.substring(0, 1);
				String msgTail = msg.substring(1, msg.length());

				dialog.setMessage("Plan saved, but there is a " + firstLetter.toLowerCase() + msgTail);
				dialog.open();
			}

		} catch (Exception e1) {
			logger.error(e1);
		}
	}

	protected List<Sequenceable> extractSequenceFromPlan() {
		List<Sequenceable> items = new ArrayList<Sequenceable>();
		try {
			for(Sequenceable item : planBuilder.getPlan().getSequence()) {
				items.add(item.clone());
			}
		} catch (CloneNotSupportedException e) { 
			e.printStackTrace();
		}
		return items;
	}

	public void deletePlan() {
		if(planBuilder.deleteFile()) {
			setPlanNotDirty();
			setPlanClosed();
		}
	}

	public void setPlanOpen() {
		planOpen = true;
		context.remove(TypedObject.class);
		context.set(TypedObject.class, planBuilder.getPlan());
		updateEnabledOptions();
	}

	public void setPlanClosed() {
		planOpen = false;
		updateEnabledOptions();
	}

	private void updateEnabledOptions() {
		context.set(ContextNames.CLOSE_PLAN_ENABLED, planOpen);
		context.set(ContextNames.SAVE_PLAN_AS_ENABLED, planOpen);
		context.set(ContextNames.OPEN_PLAN_ENABLED, !planOpen);
		context.set(ContextNames.NEW_PLAN_ENABLED, !planOpen);
	}

	public void setPlanDirty() {
		planDirty = true;
		context.set(ContextNames.SAVE_PLAN_ENABLED, planDirty);
	}

	public void setPlanNotDirty() {
		planDirty = false;
		context.set(ContextNames.SAVE_PLAN_ENABLED, planDirty);
	}

	protected void setSequenceableInContext(Sequenceable seq) {
		context.set(Sequenceable.class, seq);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("valid")) {
			if(evt.getNewValue().equals(Boolean.FALSE)) {
				setPlanDirty();
			}
		}
	}
}
