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
package gov.nasa.arc.verve.freeflyer.workbench.parts.teleop;

import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerCommands;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Child classes create widgets on the Miscellaneous Commands subtab
 * @author ddwheele
 */
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({  
	@Type(value = CameraPresetTeleopCommand.class, name = "CameraPresetTeleopCommand"),
	@Type(value = DataManagementTeleopCommand.class, name = "DataManagementTeleopCommand"),
	@Type(value = FlashlightTeleopCommand.class, name = "FlashlightTeleopCommand"),
	@Type(value = HorizontalSeparatorTeleopCommand.class, name = "HorizontalSeparatorTeleopCommand"),
	@Type(value = NoParamsTeleopCommand.class, name = "NoParamsTeleopCommand"),
	@Type(value = OperatingLimitsTeleopCommand.class, name = "OperatingLimitsTeleopCommand"),
	@Type(value = OppositeCommandsTeleopCommand.class, name = "OppositeCommandsTeleopCommand"),
	@Type(value = StringBooleanTeleopCommand.class, name = "StringBooleanTeleopCommand"),
	@Type(value = StringListIntTeleopCommand.class, name = "StringListIntTeleopCommand"),
	@Type(value = StringListTeleopCommand.class, name = "StringListTeleopCommand"),
	@Type(value = ToggleBooleanTeleopCommand.class, name = "ToggleBooleanTeleopCommand"),
	@Type(value = TwoFloatsTeleopCommand.class, name = "TwoFloatsTeleopCommand")
})
public abstract class AbstractTeleopCommandConfig implements AstrobeeStateListener {

	protected String label;
	protected String buttonText;

	protected String subsystem = FreeFlyerCommands.SUBSYSTEM_NAME;
	protected CommandPublisher commandPublisher;
	protected CommandButton button = null;
	protected String command;

	protected AstrobeeStateManager astrobeeStateManager;
	protected String accessControlName = "";
	protected String myId = Agent.getEgoAgent().name();
	protected Shell shell;

	@JsonIgnore
	public void setCommandPublisher(CommandPublisher commandPublisher) {
		this.commandPublisher = commandPublisher;
	}

	// for debugging without a simulator
	public void forceEnabled() {
		if(button != null) {
			button.setCompositeEnabled(true);
			button.onSelectedAgentConnected();
		}
	}

	@JsonIgnore
	public void setAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		astrobeeStateManager.addListener(this, MessageType.ACCESSCONTROL_STATE_TYPE);
	}

	public void onDisconnect() {
		if(button != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					button.setCompositeEnabled(false);
				}
			});
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + label;
	}

	public void createWidget(Composite parent) {
		shell = parent.getShell();
	}

	protected void createNameLabel(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(label);
	}

	protected abstract void createSendButton(Composite parent);

	@JsonIgnore
	public CommandButton getButton() {
		return button;
	}

	@JsonIgnore
	public void setButton(CommandButton button) {
		this.button = button;
	}

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	@JsonIgnore
	public CommandPublisher getCommandPublisher() {
		return commandPublisher;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState aggregateState) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(button == null || button.isDisposed()) {
					return;
				}
				accessControlName = aggregateState.getAccessControl();
				enableButtonsForAccessControl();

			}
		});
	}

	/** returns false if no more buttons should be enabled. */
	protected boolean enableButtonsForAccessControl() {
		if(accessControlName == null || button == null) {
			return false;
		}
		boolean iHaveControl = accessControlName.equals(myId);
		if(!iHaveControl) {
			button.setCompositeEnabled(false);
			return false;
		} else {
			button.setCompositeEnabled(true);
			return true;
		}
	}

	public void preDestroy() {
		if(astrobeeStateManager != null) {
			astrobeeStateManager.removeListener(this);
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	protected void showErrorDialog(Shell shell, final String title, final String errorMsg)  {
		// create a dialog with ok and cancel buttons and a question icon
		if(shell == null) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText(title);
				dialog.setMessage(errorMsg);

				// open dialog and await user selection
				int returnCode = dialog.open(); 

			}
		});
	}
}
