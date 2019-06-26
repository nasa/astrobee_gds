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
package gov.nasa.arc.verve.freeflyer.workbench.parts.standard;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.ExecutingObject;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.ExecutingSequenceable;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.RunningPlanInfo;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rapid.AckCompletedStatus;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.Status;

public class LivePlanPart implements IRapidMessageListener, AstrobeeStateListener, SelectedAgentConnectedListener {
	private static final Logger logger = Logger.getLogger(LivePlanPart.class);
	protected int[] widths = { 210, 150, 50 };
	protected String[] titles = {"Plan Step", "Duration", "Success" };
	protected int[] alignment = { SWT.LEFT, SWT.LEFT, SWT.LEFT };
	protected Tree planTree;
	protected TreeViewer planTreeViewer;
	protected SimpleDateFormat dateFormat;
	protected RunningPlanInfo runningPlanInfo;
	protected String participantId = Rapid.PrimaryParticipant;
	protected Agent agent;
	protected Color skippedColor = ColorProvider.get(53, 121, 220);
	protected Color white = ColorProvider.get(255, 255, 255);
	protected Color cyan = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	protected Label elapsedTime, planName, planStatus;
	protected final String ZERO_ELAPSED_TIME = "00:00:00";
	private Timer timer = new Timer(false); // this is the timer that will run the throttled updates
	private TimerTask updateTimerTask = null;   // this is the actual update
	protected AstrobeeStateManager astrobeeStateManager;
	protected boolean dataStale = false;

	protected MessageType sampleType;
	protected MessageType configType;
	private PlanStatus savedPlanStatus;

	protected long combinedPastIntervals = 0;
	protected Date mostRecentStart;
	protected boolean countingElapsedTime = false;

	@Inject 
	public LivePlanPart(Composite parent) {
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		sampleType = MessageTypeExtAstro.PLAN_STATUS_TYPE;
	}

	@PostConstruct
	public void onPostConstruct(Composite parent) {
		setupTitleArea(parent);
		setupPlanStatus(parent);
		setupTotalElapsedTime(parent);
		createTreeSection(parent);
		SelectedAgentConnectedRegistry.addListener(this);
	}

	@PreDestroy
	public void preDestroy() {
		astrobeeStateManager.removeListener(this);
		SelectedAgentConnectedRegistry.removeListener(this);
		unsubscribe();
	}

	@Inject @Optional
	public void acceptRunningPlanInfo(@Named(FreeFlyerStrings.RUNNING_PLAN_INFO) RunningPlanInfo rpi) {
		if(rpi == null) {
			return;
		}
		runningPlanInfo = rpi;
		if(savedPlanStatus != null) {
			runningPlanInfo.setPlanStatus(savedPlanStatus);
		}
		if(runningPlanInfo.getPlan() != null && planName != null) {
			planName.setText(runningPlanInfo.getPlan().getName());
		}
		if(planTreeViewer != null) {
			planTreeViewer.setInput(runningPlanInfo);
			planTreeViewer.refresh();
			planTreeViewer.expandToLevel(3);
		}
	}

	private void setupTitleArea(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalAlignment = SWT.LEFT;
		parent.setLayoutData(gd);
	}

	private void setupPlanStatus(Composite parent){
		planName = new Label(parent, SWT.None);
		planName.setText("Plan Name");

		planName = new Label(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		planName.setLayoutData(gd);
		planName.setText("");
		planName.setBackground(white);
		planName.setToolTipText(WorkbenchConstants.PLAN_LABEL_TOOLTIP);
		
		planStatus = new Label(parent, SWT.None);
		planStatus.setText("Plan State");

		planStatus = new Label(parent, SWT.NONE);
		GridData gds = new GridData(SWT.FILL, SWT.CENTER, true, false);
		planStatus.setLayoutData(gds);
		planStatus.setText("");
		planStatus.setBackground(white);
		planStatus.setToolTipText(WorkbenchConstants.PLAN_STATUS_TOOLTIP);
	}
	
	private void setupTotalElapsedTime(Composite parent) {
		Label elapsedTimeTitle = new Label(parent, SWT.None);
		elapsedTimeTitle.setText("Total Elapsed Time");

		elapsedTime = new Label(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		//		gd. = SWT.END;
		elapsedTime.setLayoutData(gd);
		elapsedTime.setText(ZERO_ELAPSED_TIME);
		elapsedTime.setBackground(white);
		elapsedTime.setToolTipText("Time Plan Has Been Running");
	}

	public void createTreeSection(Composite parent) {
		Composite c = setupTreeSectionComposite(parent);

		planTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		planTree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		planTree.setLayoutData(gd);
		planTree.setLinesVisible(true);

		planTreeViewer = new TreeViewer(planTree);

		createColumns();

		planTree.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				fillColumn();
			}
		});

		planTreeViewer.setContentProvider( new CreateTreeContentProvider());
		planTreeViewer.setLabelProvider(new TableLabelProvider());
	}

	protected void createColumns() {
		TreeColumn col;
		for(int i=0; i<widths.length; i++) {
			col = new TreeColumn(planTree, alignment[i]);
			col.setText(titles[i]);
			col.setWidth(widths[i]);
		}
	}

	protected class CreateTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() {
			// do nothing 
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) {
			// do nothing 
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof RunningPlanInfo) {
				if(((RunningPlanInfo)inputElement).toArray() != null) {
					return ((RunningPlanInfo)inputElement).toArray();
				}
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ExecutingObject) {
				Object[] arr = ((ExecutingObject)parentElement).getChildren();
				if(arr != null) {
					return arr;
				}
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof ExecutingObject){
				Object o = ((ExecutingObject)element).getParent();
				if(o != null) {
					return o;
				}
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof ExecutingObject){
				return ((ExecutingObject)element).hasChildren();
			}
			return false;
		}
	}

	class TableLabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {

		public Image getColumnImage(Object element, int columnIndex){
			return null;
		}

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
			case 0: 
				if( element instanceof ExecutingObject ) {	
					return ((ExecutingObject)element).getName();
				} else {
					return "--";
				}
			case 1:
				if (element instanceof ExecutingSequenceable) {
					long dur =((ExecutingSequenceable)element).getDuration();
					if(dur > 0) {
						return convertLongToString(dur);
					}
				} 
				return null;
			case 2:
				if( element instanceof ExecutingObject ) {	
					AckCompletedStatus acs = ((ExecutingObject)element).getStatus();
					if(acs != null) {
						return GuiUtils.prettyPrint(acs);
					}
				}
			}
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {

			if(runningPlanInfo.getPlanStatus() == null) {
				return null;
			}

			if( element instanceof ExecutingSequenceable ) {
				if(dataStale) {
					return cyan;
				}
				ExecutingSequenceable es = (ExecutingSequenceable)element;

				if(runningPlanInfo.isPointCommandExecuting(es.getPointCommand())) {
					return ColorProvider.INSTANCE.lightGreen2;
				}
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener){
		}

		public void dispose(){
		}

		public boolean isLabelProperty(Object element, String property){
			return false;
		}

		public void removeListener(ILabelProviderListener listener){
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {

			if( element instanceof ExecutingSequenceable ) {
				ExecutingSequenceable es = (ExecutingSequenceable)element;
				if(es.getStatus() == null) {
					return null;
				}

				if(es.getStatus().equals(AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED)) {
					return ColorProvider.INSTANCE.orange;
				}

				if(es.getStatus().equals(AckCompletedStatus.ACK_COMPLETED_OK)) {
					return ColorProvider.INSTANCE.darkGray;
				}
				if(es.getStatus().equals(AckCompletedStatus.ACK_COMPLETED_CANCELED)) {
					return skippedColor;
				}
			}
			return null;
		}
		FontRegistry registry = new FontRegistry();
		public Font getFont(Object element, int columnIndex) {
			if(runningPlanInfo.getPlanStatus() == null) {
				return null;
			}

			if( element instanceof ExecutingSequenceable ) {
				ExecutingSequenceable es = (ExecutingSequenceable)element;

				if(runningPlanInfo.isPointCommandExecuting(es.getPointCommand())) {
					return  registry.getBold(Display.getCurrent().getSystemFont()
							.getFontData()[0].getName());
				}
			}
			return null;
		}
	}	

	protected String convertLongToString(long time) {
		if (time == 0) {
			return "";
		}
		// Date requires milliseconds
		Date date = new Date(time*1000);
		return dateFormat.format(date);
	}

	private Composite setupTreeSectionComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		c.setLayout(gl);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		c.setLayoutData(gd);
		return c;
	}

	public void printSample(PlanStatus sample) {
		System.out.println("*************** - RunPlanTable");
		System.out.println("- PointCommand "+sample.currentPoint + ", " +sample.currentCommand + " is " + sample.currentStatus);

		for(int i=0; i<sample.statusHistory.userData.size(); i++) {
			Status s = (Status)sample.statusHistory.userData.get(i);
			System.out.println("--" + s.point + ", " + s.command + " - " + s.status);
		}
		System.out.println("***************");
	}

	public synchronized void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.PLAN_STATUS_TYPE)) {

			if(msgObj.equals(savedPlanStatus)) {
				logger.debug("Got duplicate PlanStatus");
				return;
			}
			savedPlanStatus = (PlanStatus)msgObj;
			
			//printSample(savedPlanStatus);

			if(runningPlanInfo != null) {
				if(savedPlanStatus.planName.equals("")) {
					runningPlanInfo.clear();
				} else {
					runningPlanInfo.setPlanStatus(savedPlanStatus);
				}
			}

			try {
				final Display display = Display.getDefault();
				try {
					if (!display.isDisposed()){
						display.asyncExec(new Runnable() {
							public void run() {
								try {
									planName.setText(astrobeeStateManager.getAggregateAstrobeeState().getCurrentPlanName());
									planStatus.setText(GuiUtils.toTitleCase(astrobeeStateManager.getAggregateAstrobeeState().getAstrobeeState().getPlanExecutionStateName()) );
									if(runningPlanInfo != null) {
										if(runningPlanInfo.isValid()) {
											ExecutingSequenceable executing = runningPlanInfo.getExecutingExecutingSequenceable();
											if(executing != null) {
												planTreeViewer.reveal(executing);
											}
										}
										else 
										{
											planTree.removeAll();
											elapsedTime.setText(ZERO_ELAPSED_TIME);
										}
										planTreeViewer.refresh();
									}
								} catch (Exception e) {
									logger.error("refresh", e);
								}
							}
						});
					}
				} catch (SWTException e){
					logger.error("SWTException", e);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Inject
	@Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager asm) {
		astrobeeStateManager = asm;
		astrobeeStateManager.addListener(this, MessageTypeExtAstro.AGENT_STATE_TYPE);
	}

	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		if(AstrobeeStateGds.ExecutionState.EXECUTING.equals(stateKeeper.getAstrobeeState().getPlanExecutionState())) {
			if(!countingElapsedTime) {
				startCountingElapsedTime();
			}
		} else {
			if(countingElapsedTime) {
				stopCountingElapsedTime();
			}
		}
		if(AstrobeeStateGds.ExecutionState.PAUSED.equals(stateKeeper.getAstrobeeState().getPlanExecutionState())) {
			if(stateKeeper.getCurrentPlanCommand().equals(0, -1)) {
				resetElapsedTime();
			}
		}
	}

	protected void resetElapsedTime() {
		countingElapsedTime = false;
		combinedPastIntervals = 0;
		mostRecentStart = null;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateTime();
			}
		});
	}

	protected void updateTime(){
		if (elapsedTime == null || elapsedTime.isDisposed()){
			return;
		}
		elapsedTime.setText(getCurrentElapsedTimeString());
	}

	protected String getCurrentElapsedTimeString() {
		long answer;
		if(countingElapsedTime) {
			answer = combinedPastIntervals + (new Date()).getTime() - mostRecentStart.getTime();
			return dateFormat.format(answer);
		} else {
			return dateFormat.format(combinedPastIntervals);
		}
	}

	protected void startCountingElapsedTime() {
		mostRecentStart = new Date();
		countingElapsedTime = true;
		updateTimerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					Display display = Display.getDefault();
					if (display != null && !display.isDisposed()){
						display.asyncExec(new Runnable() {
							public void run() {
								updateTime();
							}
						});
					}
				} catch (SWTException e){
					// hi
				}
			}
		};
		// update time once per second
		timer.schedule(updateTimerTask, 0, 1000);
	}

	protected void stopCountingElapsedTime() {
		if(countingElapsedTime) {
			updateTimerTask.cancel();
			combinedPastIntervals = (new Date()).getTime() - mostRecentStart.getTime();
			countingElapsedTime = false;
		}
	}

	public void subscribe() {
		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}
		//					logger.debug("subscribe on "+getAgent().name() + " for view " + getTitle());
		final Agent agent = getAgent();
		final String id = getParticipantId();
		
		DdsTask.dispatchExec(new Runnable() {
			@Override
			public void run() {
				for(MessageType mt : getMessageTypes()) {
					if (mt == null) continue;
					RapidMessageCollector.instance().addRapidMessageListener(id, 
							agent, 
							mt, 
							LivePlanPart.this);
				}
			}
		});
	}

	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		ret.add(getSampleType());
		ret.add(getConfigType());
		return ret;
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");

			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}

	public String getParticipantId() {
		return participantId;
	}

	@Inject @Optional
	public void acceptAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		if(agent == null) {
			return;
		}
		unsubscribe();
		dataStale = false;
		changeElapsedTimeColor();
		this.agent = agent;
		if(runningPlanInfo != null) {
			runningPlanInfo.clear();
			if(planTreeViewer != null) {
				planTreeViewer.refresh();
			}
		}


		subscribe();
	}

	public Agent getAgent() {
		return agent;
	}
	protected MessageType getSampleType() {
		return sampleType;
	}

	protected MessageType getConfigType() {
		return configType;
	}

	protected void fillColumn() {
		// calculate widths
		int columnsWidth = 0;
		for (int i = 0; i < planTree.getColumnCount() - 1; i++) {
			columnsWidth += planTree.getColumn(i).getWidth();
		}

		Point size = planTree.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = planTree.getVerticalBar();
		if(verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = planTree.getColumn(planTree.getColumnCount() - 1);
		if(columnsWidth + widths[widths.length - 1] + planTree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth - planTree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if(lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}

	@Override
	public void onSelectedAgentConnected() {
		dataStale = false;
		changeElapsedTimeColor();
	}

	protected void changeElapsedTimeColor() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(planTreeViewer != null) {
					if(dataStale) {
						elapsedTime.setBackground(cyan);
						planName.setBackground(cyan);
						planStatus.setBackground(cyan);
					} else {
						elapsedTime.setBackground(white);
						planName.setBackground(white);
						planStatus.setBackground(white);
					}
				}
			}
		});
	}

	@Override
	public void onSelectedAgentDisconnected() {
		if(dataStale) {
			return;
		}
		dataStale = true;
		changeElapsedTimeColor();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(planTreeViewer != null) {
					planTreeViewer.refresh();
				}
			}
		});
	}

}
