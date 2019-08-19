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

import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveScenario;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveScenarioStartedListener;
import gov.nasa.arc.verve.ardor3d.e4.framework.VerveScenarioStarted;
import gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.TabName;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.AddViaMapPlane;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.AddViaMapTypedObject;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.undo.DelegateCommandStack;
import gov.nasa.arc.verve.robot.freeflyer.plan.AbstractPlanTrace;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsListener;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanEditsRegistry;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class PlanEditorPart implements PropertyChangeListener, PlanEditsListener {
	//private final Logger logger = Logger.getLogger(PlanEditorPart.class);
	protected TreeViewer planTreeViewer;

	protected PlanBuilder<ModuleBayPlan> planBuilder; // the specific plan builder for this editor
	@Inject 
	protected EPartService partService;
	protected MApplication application;
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	public Shell shell;
	@Inject
	protected PlanFileManager planFileManager;
	@Inject
	protected DelegateCommandStack delegateCommandStack;
	protected final String selectionEditorName = "gov.nasa.arc.verve.freeflyer.workbench.part.selectionEditor";
	protected AbstractPlanTrace abstractPlanTrace;
	protected SimpleDateFormat dateFormat;
	protected Label duration, planName, power,planValid;
	protected Color notValidColor, validColor, noPlanColor;
	protected EnlargeableButton validButton, previewButton, stopPreviewButton;
	protected EnlargeableButton addButton, deleteButton, addViaMapButton;
	protected String noPlanString = "No Plan";
	protected String validString = "Validated";
	protected String notValidString = "Not Validated";
	protected String addViaMapString = "Add via 3d View";
	protected String exitAddViaMapString = "Close Add via 3d View";
	protected Tree planTree;
	protected boolean appendStationEnabled, insertStationEnabled;
	protected IStructuredSelection shouldBeSelected;

	AddViaMapTypedObject addViaMapTypedObject;
	boolean addViaMapEnabled;

	protected Segment invalidSegment;
	private final Color orange = new Color(Display.getCurrent(), 239, 118, 51);
	private Color warningColor;
	private Color whiteColor;
	private boolean planDirty = false;
	private final String DIRTY_STRING = "*";
	public TabName MY_TAB_NAME = TabName.PLAN_EDITOR;
	@Inject
	public FreeFlyerScenario freeflyerScenario;

	@Inject
	public PlanEditorPart(final Shell shell, final Display display, final MApplication app) {
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		application = app;
		addViaMapTypedObject = new AddViaMapTypedObject();
		notValidColor = display.getSystemColor(SWT.COLOR_BLACK);
		validColor = display.getSystemColor(SWT.COLOR_DARK_GREEN);
		noPlanColor = display.getSystemColor(SWT.COLOR_BLACK);
		warningColor = new Color(display, 225,153,0);
		whiteColor = new Color(display, 255,255,255);
	}

	protected void addListenerToNewStation(final Sequenceable newStation) {
		newStation.addPropertyChangeListener("command",this);
	}

	protected void addListenerToNewSegment(final Segment segment) {
		System.out.println(segment);
		segment.addPropertyChangeListener("speed",this);
	}

	@PostConstruct
	public void createPartControl(final Composite parent, final EMenuService menuService, final EPartService eps) {
	
		setupTopOfPartControl(parent);

		createPlanSection(parent);
		createTreeSection(parent);
		createButtonSection(parent);

		menuService.registerContextMenu(planTree, "gov.nasa.arc.verve.freeflyer.workbench.popupmenu.1");
	  
		eps.addPartListener(new IPartListener() {		
			@Override
			public void partVisible(MPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partHidden(MPart part) {
				if (MY_TAB_NAME.matches(part.getElementId())) {
					application.getContext().set(ContextNames.OPEN_PLAN_ENABLED,false);
					application.getContext().set(ContextNames.NEW_PLAN_ENABLED,false);
				} 
			}
			
			@Override
			public void partDeactivated(MPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partBroughtToTop(MPart part) {
				if (MY_TAB_NAME.matches(part.getElementId())) {
					application.getContext().set(ContextNames.OPEN_PLAN_ENABLED,true);
					application.getContext().set(ContextNames.NEW_PLAN_ENABLED,true);
				} 
			}
			
			@Override
			public void partActivated(MPart part) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void setupTopOfPartControl(final Composite parent) {
		parent.setLayout(new GridLayout());
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalAlignment = SWT.LEFT;
		parent.setLayoutData(gd);

		final Label partTitle = new Label(parent, SWT.None);
		partTitle.setText("Plan Editor");
	}

	@Inject @Optional 
	protected void setAppendStationEnabled(@Named(ContextNames.APPEND_STATION_ENABLED) final boolean appendStationEnabled) {
		this.appendStationEnabled = appendStationEnabled;
		if(addButton != null) 	{
			addButton.setEnabled(appendStationEnabled || insertStationEnabled);
		}
		if(addViaMapButton != null) 	{
			addViaMapButton.setEnabled(appendStationEnabled || insertStationEnabled);
		}
	}

	@Inject @Optional 
	protected void setClickToAddEnabled(@Named(ContextNames.ADD_VIA_MAP_ENABLED) final boolean enabled) {
		addViaMapEnabled = enabled;
		if(addViaMapEnabled) {
			addViaMapButton.setText(exitAddViaMapString);
			AddViaMapPlane.getStaticInstance().showPlane();
			setTypedObjectInContext(addViaMapTypedObject);
			addButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} else {
			addViaMapButton.setText(addViaMapString);
			AddViaMapPlane.getStaticInstance().hidePlane();
			setTypedObjectInContext(getSelected());
			addButton.setEnabled(true);
			deleteButton.setEnabled(true);
		}
	}

	private void createButtonSection(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(3, true);
		c.setLayout(layout);
		c.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		makeAddButton(c);
		makeDeleteButton(c);
		makeAddViaMapButton(c);
	}

	private void createPlanSection(final Composite parent) {
		final Composite c = setupPlanSectionComposite(parent);
		makePlanNameDurationAndPowerLines(c);
		makeValidLine(c);
	}

	private void makePlanNameDurationAndPowerLines(final Composite c) {
		final Label status = new Label(c, SWT.NONE);
		status.setText("Plan Name");

		planName = makeTopLabel(c, "Current plan name");

		final Label durationLabel = new Label(c, SWT.NONE);
		durationLabel.setText("Estimated Duration");

		duration = makeTopLabel(c, "Estimated execution time");

		final Label powerLabel = new Label(c,SWT.NONE);
		powerLabel.setText("Estimated Power");
		power = makeTopLabel(c, "Estimated power");

	}

	private void makeValidLine(final Composite c) {
		final Label validLabel = new Label(c, SWT.None);
		validLabel.setText("Validation");

		final Composite validComposite = new Composite(c, SWT.None);
		final GridData validCompositeGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		validCompositeGridData.grabExcessHorizontalSpace = true;
		validComposite.setLayoutData(validCompositeGridData);
		validComposite.setLayout(new GridLayout(2,false));

		makePlanValidLabel(validComposite);

		makeValidButton(c);

		PlanEditsRegistry.addListener(this);
	}

	private Label makeTopLabel(final Composite c, final String toolTipText) {
		final GridData gridDataCurrentPlanFields = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridDataCurrentPlanFields.grabExcessHorizontalSpace = true;
		gridDataCurrentPlanFields.horizontalSpan = 2;

		final Label planName = new Label(c, SWT.NONE);
		planName.setToolTipText(toolTipText);
		planName.setBackground(ColorProvider.INSTANCE.white);
		planName.setText(noPlanString);
		planName.setLayoutData(gridDataCurrentPlanFields);
		return planName;
	}

	private void makePlanValidLabel(final Composite c) {
		planValid = new Label(c, SWT.None);
		planValid.setBackground(ColorProvider.INSTANCE.white);
		planValid.setData("gov.nasa.arc.irg.iss.ui.widget.key", "validLabel");
		final GridData planValidGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		planValidGridData.grabExcessHorizontalSpace = true;
		planValid.setLayoutData(planValidGridData);
		planValid.setText(noPlanString);
	}

	private void makePreviewButton(final Composite c) {
		previewButton = new EnlargeableButton(c, SWT.PUSH);
		final GridData previewButtonGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		previewButton.setLayoutData(previewButtonGridData);
		previewButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		previewButton.setText("Preview");
		previewButton.setEnabled(false);
		previewButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				freeflyerScenario.previewPlan(planBuilder.getPlan());
				stopPreviewButton.setEnabled(true);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	private void makeStopPreviewButton(final Composite c) {
		stopPreviewButton = new EnlargeableButton(c, SWT.PUSH);
		final GridData previewButtonGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		stopPreviewButton.setLayoutData(previewButtonGridData);
		stopPreviewButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stopPreviewButton.setText("Stop Preview");
		stopPreviewButton.setEnabled(false);
		stopPreviewButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				freeflyerScenario.stopPlanPreview();
				stopPreviewButton.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void makeValidButton(final Composite c) {
		validButton = new EnlargeableButton(c, SWT.PUSH);
		final GridData validButtonGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		validButton.setLayoutData(validButtonGridData);
		validButton.setButtonLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		validButton.setText("Validate");
		validButton.setEnabled(false);
		validButton.setData("gov.nasa.arc.irg.iss.ui.widget.key", "Validate");
		validButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				System.out.println("You pressed Validate");

				if(planBuilder != null) {
					boolean valid;
					valid = PlanCompiler.compilePlan(planBuilder.getPlan(), planBuilder.getPlan().getOperatingLimits());

					generatePower();
					refreshValidField();
					planFileManager.setPlanDirty();

					if(!valid) {						
						final MessageBox dialog = 
								new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
						dialog.setText("Validation Failed");
						dialog.setMessage(PlanCompiler.getLastErrorMessage());
						dialog.open();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void generatePower(){
		final double generatePower = PlanCompiler.generatePower(planBuilder.getPlan());
		power.setText( DecimalFormat.getInstance().format(generatePower) + " W" );
		if(generatePower > 196)
			power.setBackground(warningColor);
		else
			power.setBackground(whiteColor);
	}

	private void makeAddButton(final Composite c) {
		addButton = new EnlargeableButton(c, SWT.NONE);
		addButton.setText("Add Station");
		addButton.setEnabled(false);
		addButton.setToolTipText("Add a Station");
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if(insertStationEnabled) {
					delegateCommandStack.onInsertAfter();
					return;
				}
				if(appendStationEnabled) {
					delegateCommandStack.onAppend();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) { /**/ }
		});
	}

	private void makeDeleteButton(final Composite c) {
		deleteButton = new EnlargeableButton(c, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setEnabled(false);
		deleteButton.setToolTipText("Delete selected Station");
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deleteButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Sequenceable highlightNext = delegateCommandStack.onDelete();
				if(highlightNext instanceof Station) {
					setTypedObjectInContext( (Station) highlightNext );
				}
				else if(highlightNext instanceof FreeFlyerCommand) {
					setTypedObjectInContext( (FreeFlyerCommand) highlightNext );
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) { /**/ }
		});
	}

	private void makeAddViaMapButton(final Composite c) {
		addViaMapButton = new EnlargeableButton(c, SWT.NONE);
		addViaMapButton.setText(addViaMapString);
		addViaMapButton.setEnabled(false);
		addViaMapButton.setToolTipText("Click to add Stations in the 3D viewer");
		addViaMapButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addViaMapButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addViaMapButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				onAddViaMapButtonPushed();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) { /**/ }
		});

	}

	private void onAddViaMapButtonPushed() {
		if(addViaMapButton.getText().equals(addViaMapString)) {
			application.getContext().set(ContextNames.ADD_VIA_MAP_ENABLED, true);
		} else {
			application.getContext().set(ContextNames.ADD_VIA_MAP_ENABLED, false);
		}
	}

	private Composite setupPlanSectionComposite(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(3, false);
		c.setLayout(layout);
		c.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		return c;
	}

	public void createTreeSection(final Composite parent) {
		final Composite b = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, true);
		b.setLayout(layout);
		b.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		makePreviewButton(b);
		makeStopPreviewButton(b);
		
		
		final Composite c = setupTreeSectionComposite(parent);
		
		planTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		planTree.setHeaderVisible(true);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		planTree.setLayoutData(gd);

		planTreeViewer = new TreeViewer(planTree);

		final TreeColumn col1 = new TreeColumn(planTree, SWT.LEFT);
		planTree.setLinesVisible(true);
		col1.setText("Plan Step");
		col1.setWidth(300);

		final TreeColumn col2 = new TreeColumn(planTree, SWT.LEFT);
		col2.setText("Duration");
		col2.setWidth(200);

		planTreeViewer.setContentProvider( new CreateTreeContentProvider());
		planTreeViewer.setLabelProvider(new TableLabelProvider());

		planTreeViewer.setInput(getPlanBuilder());
		planTreeViewer.expandToLevel(2);

		planTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {				
				if(addViaMapEnabled) {
					return;
				} 				

				setTypedObjectInContext(getSelected());
				// open Station Editor in adjacent window
				openElementEditor();
			}
		});
	}

	private Composite setupTreeSectionComposite(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		final GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		c.setLayout(gl);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return c;
	}

	protected void openElementEditor() {
		final TypedObject selected = getSelected();

		if( selected != null ) {
			partService.showPart(selectionEditorName, PartState.ACTIVATE);
		}
	}

	public void setTypedObjectInContext(final TypedObject seq) {
		final IEclipseContext context = application.getContext();
		context.remove(TypedObject.class);
		context.set(TypedObject.class, seq);
	}

	@Inject @Optional 
	public void updateDisplayedPlan(@Named(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR) final PlanBuilder<ModuleBayPlan> builder) {
		if(builder != null && builder.getPlan() != null) {
			planBuilder = builder;
			final ModuleBayPlan plan = planBuilder.getPlan();

			plan.addPropertyChangeListener("sequence", this);
			plan.addPropertyChangeListener("valid", this);
			for(final Sequenceable seq : plan.getSequence()) {
				seq.addPropertyChangeListener(this);
				if(seq instanceof SequenceHolder) {
					final SequenceHolder sh = (SequenceHolder)seq;
					for(final Sequenceable child : sh.getSequence()) {
						child.addPropertyChangeListener(this);
					}
				}
			}
			planName.setText(plan.getName());
			refreshValidField();
			generatePower();
			duration.setText(convertLongToString(plan.getCalculatedDuration()));
			validButton.setEnabled(true);
			addViaMapButton.setEnabled(true);
			previewButton.setEnabled(true);
			stopPreviewButton.setEnabled(false);
		} else {
			removeAsPropertyChangeListener();
			if(!addViaMapButton.getText().equals(addViaMapString)) {
				onAddViaMapButtonPushed();
			}
			planName.setText(noPlanString);
			planValid.setText(noPlanString);
			planValid.setForeground(noPlanColor);
			duration.setText("");
			power.setText("");
			validButton.setEnabled(false);
			delegateCommandStack.exitClickToAddMode();
			planBuilder = builder;
			addViaMapButton.setEnabled(false);
			previewButton.setEnabled(false);
			stopPreviewButton.setEnabled(false);
		}

		if(planTreeViewer != null) {
			planTreeViewer.setInput(getPlanBuilder());
			planTreeViewer.expandToLevel(2);
		}
	}

	public PlanBuilder<ModuleBayPlan> getPlanBuilder() {
		return planBuilder;
	}

	class TableLabelProvider implements ITableLabelProvider, ITableColorProvider {

		public Image getColumnImage(final Object element, final int columnIndex){
			return null;
		}

		public String getColumnText(final Object element, final int columnIndex){
			switch (columnIndex){

			case 0: 
				if( element instanceof Station) 
				{
					return ((Station)element).getName() + " Station";
				} else if( element instanceof Segment) 
				{
					return ((Segment)element).getName() + " Segment";
				}
				else if( element instanceof FreeFlyerCommand ) 
				{
					return ((FreeFlyerCommand)element).getDisplayName();
				} else if( element instanceof TypedObject ) 
				{
					return ((TypedObject)element).getName();
				}
				return "--";
			case 1:
				if (element instanceof Sequenceable) {
					return convertLongToString(((Sequenceable)element).getCalculatedDuration());
				}
				break;
			case 2:
				return null;
			}
			return null;
		}

		public void addListener(final ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(final Object element, final String property){
			return false;
		}

		public void removeListener(final ILabelProviderListener listener) { /**/ }

		@Override
		public Color getBackground(final Object element, final int columnIndex) {
			if (element.equals(invalidSegment)) {
				return orange;
			}
			return null;
		}

		@Override
		public Color getForeground(final Object element, final int columnIndex) {
			return null;
		}
	}	

	protected String convertLongToString(final long time) {
		if (time == 0) {
			return "";
		}
		// Date requires milliseconds
		final Date date = new Date(time*1000);
		return dateFormat.format(date);
	}

	protected class CreateTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) { /**/ }

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof PlanBuilder){
				final Plan plan = getPlanBuilder().getPlan();
				if (plan != null){
					return new Object[]{ plan };
				}
			} else if (inputElement instanceof SequenceHolder){
				return ((SequenceHolder)inputElement).getSequence().toArray();
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object[] getChildren(final Object parentElement) {
			if (parentElement instanceof SequenceHolder){
				return ((SequenceHolder)parentElement).getSequence().toArray();
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(final Object element) {
			if (element instanceof Sequenceable){
				return ((Sequenceable)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(final Object element) {
			if (element instanceof SequenceHolder){
				return !((SequenceHolder)element).isEmpty();
			}
			return false;
		}
	}

	protected TypedObject getSelected() {
		final IStructuredSelection iss = (IStructuredSelection)planTreeViewer.getSelection();
		if (iss == null){
			return null;
		}
		final Object first = iss.getFirstElement();
		if (first == null){
			return null;
		}
		if (first instanceof TypedObject){
			return (TypedObject)first;
		}
		return null;
	}

	protected AbstractPlanTrace getPlanTrace() {
		try {
			abstractPlanTrace = AbstractPlanTrace.getStaticInstance();
		} catch (final Exception e){
			return null;
		}
		if (abstractPlanTrace == null){
			VerveScenarioStarted.addListener(new IVerveScenarioStartedListener() {
				@Override
				public void onScenarioStarted(final IVerveScenario scenario) {
					if (abstractPlanTrace == null){
						abstractPlanTrace = AbstractPlanTrace.getStaticInstance();
					}
				}
			});
		}
		return abstractPlanTrace;
	}

	protected void enableAppendInsertUpDownDelete(final boolean enableAppend, final boolean enableInsert, final boolean enableUp, final boolean enableDown, final boolean enableDelete) {
		final IEclipseContext context = application.getContext();
		context.set(ContextNames.INSERT_STATION_ENABLED, enableInsert);
		context.set(ContextNames.MOVE_STATION_UP_ENABLED, enableUp);
		context.set(ContextNames.MOVE_STATION_DOWN_ENABLED, enableDown);
		context.set(ContextNames.DELETE_STATION_ENABLED, enableDelete);

		appendStationEnabled = enableAppend;
		insertStationEnabled = enableInsert;
		addButton.setEnabled(enableAppend || enableInsert);
		deleteButton.setEnabled(enableDelete);
	}

	@Inject @Optional
	public void setSelectedElement(final TypedObject selected) {
		if( planTreeViewer == null) {
			return;
		}
		planTreeViewer.refresh();
		if(selected != null) {
			final IStructuredSelection selection = new StructuredSelection(selected);
			if(!selection.equals(planTreeViewer.getSelection())) {
				planTreeViewer.setSelection(selection);
			}
			shouldBeSelected = selection;
		}

		enableCommandsForSelectedItem(selected);
	}

	private void enableCommandsForSelectedItem(final TypedObject selected) {
		if(selected instanceof ModuleBayPlan) {
			enableAppendInsertUpDownDelete(true, false, false, false, false);
			return;
		}
		if(selected == null || selected instanceof AddViaMapTypedObject) {
			enableAppendInsertUpDownDelete(false, false, false, false, false);
			return;
		}
		if(selected instanceof Segment || selected instanceof Plan) {
			enableAppendInsertUpDownDelete(false, true, false, false, false);
			return;
		} 

		enableCommandsForSelectedStationOrCommand(selected);
	}

	private void enableCommandsForSelectedStationOrCommand(final TypedObject selected) {
		boolean insert = true;
		boolean up = true;
		boolean down = true;
		SequenceHolder parent = null;

		if(selected instanceof Station) {
			parent = planBuilder.getPlan();
		} else if(selected instanceof FreeFlyerCommand) {
			parent = ((FreeFlyerCommand)selected).getParent();
			insert = false;
		}

		if(parent != null) {
			if(parent.indexOf((Sequenceable)selected) == 0)  {
				up = false;
			}
			if(parent.indexOf((Sequenceable)selected) == parent.size()-1) {
				down = false;
			}
		} 

		enableAppendInsertUpDownDelete(false, insert, up, down, true);
	}

	public void onAppend(final Sequenceable newStation) {
		addListenerToNewStation(newStation);

		if(newStation.getPrevious() instanceof Segment) {
			addListenerToNewSegment((Segment)newStation.getPrevious());
		}

		selectItemInTree(newStation);
	}

	// for undo, not covered in unit tests
	public void appendThisStation(final Station newStation) {
		final ModuleBayPlan p = planBuilder.getPlan();
		p.addStation(newStation);

		onAppend(newStation);
	}

	public void onInsert(final Sequenceable inserted) {
		selectItemInTree(inserted);
		addListenerToNewStation(inserted);

		if(inserted.getPrevious() instanceof Segment) {
			addListenerToNewSegment((Segment)inserted.getPrevious());
		}
		if(inserted.getNext() instanceof Segment) {
			addListenerToNewSegment((Segment)inserted.getNext());
		}
	}

	// for undo, not covered in unit tests
	public void insertThisStation(final Station toInsert) {
		final ITreeSelection selection = (ITreeSelection)planTreeViewer.getSelection();
		if(!selection.isEmpty()) {
			final Station reference = getStationToInsertBefore(selection);
			if(reference != null) {
				final Station inserted = planBuilder.getPlan().insertThisStation(toInsert,reference);
				selectItemInTree(inserted);
			}
		}
	}

	private Station getStationToInsertBefore(final ITreeSelection selection) {
		Station reference = null;
		if(selection.getFirstElement() instanceof Station) {
			reference = (Station) selection.getFirstElement();

		} else if(selection.getFirstElement() instanceof Segment) {
			final Segment seg = (Segment) selection.getFirstElement();
			reference = planBuilder.getPlan().getNextStation(seg);
		}
		return reference;
	}

	public void onMoveUp(final Sequenceable moved) {
		selectItemInTree(moved);
	}

	public void onMoveDown(final Sequenceable moved) {
		selectItemInTree(moved);
	}

	private void selectItemInTree(final Object o) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(o != null) {
					planTreeViewer.refresh();
					planTreeViewer.reveal(o);
					final IStructuredSelection newSelection = new StructuredSelection(o);
					planTreeViewer.setSelection(newSelection);
				}
			}
		});
	}

	public void onDelete(final Sequenceable deleted) {
		// updating highlighted Sequnceable handled by setSelectedElement
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				planTreeViewer.setInput(null);
				planTreeViewer.refresh();
				planTreeViewer.setInput(getPlanBuilder());
				planTreeViewer.refresh();
				planTreeViewer.expandToLevel(2);
				planTreeViewer.setSelection(shouldBeSelected);
			}
		});
	}

	//	public void doClosePlan() {
	//		IEclipseContext context = application.getContext();
	//		context.set(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR, null);
	//		setTypedObjectInContext(null);
	//
	//		removeAsPropertyChangeListener();
	//	}

	private void removeAsPropertyChangeListener() {
		if(planBuilder == null) {
			return;
		}
		final ModuleBayPlan plan = planBuilder.getPlan();
		plan.removePropertyChangeListener("sequence", this);
		plan.removePropertyChangeListener("valid", this);
		for(final Sequenceable seq : plan.getSequence()) {
			seq.removePropertyChangeListener(this);
			if(seq instanceof SequenceHolder) {
				final SequenceHolder sh = (SequenceHolder)seq;
				for(final Sequenceable child : sh.getSequence()) {
					child.removePropertyChangeListener(this);
				}
			}
		}
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		if(!evt.getPropertyName().equals("valid")) {
			planBuilder.getPlan().setValid(false);
		}
		planTreeViewer.refresh();
		if(evt.getPropertyName().equals("sequence")) {
			planTreeViewer.expandToLevel(2);
		}
		duration.setText(convertLongToString(planBuilder.getPlan().getCalculatedDuration()));
		if(evt.getPropertyName().equals("command")) {
			selectItemInTree(evt.getNewValue());
			if(evt.getNewValue() instanceof Sequenceable) {
				((Sequenceable)evt.getNewValue()).addPropertyChangeListener(this);
			}
		}
		refreshValidField();
		generatePower();

	}

	public void refreshValidField() {
		final boolean valid = planBuilder.getPlan().isValid();
		if(valid) {
			planValid.setText(validString);
			planValid.setForeground(validColor);
		} else {
			planValid.setText(notValidString);
			planValid.setForeground(notValidColor);
		}
		invalidSegment = PlanCompiler.getInvalidSegment();
		planTreeViewer.refresh();
	}

	public void onStationMoved(final Station moved) {
		refreshValidField();
		planTreeViewer.refresh();
	}

	@Inject @Optional
	public void acceptPlanDirty(@Named(ContextNames.SAVE_PLAN_ENABLED) boolean dirty) {
		if(dirty) {
			onPlanDirty();
		} else {
			onPlanNotDirty();
		}
	}

	public void onPlanDirty() {
		if(!planDirty) {
			planDirty = true;
			updateDirtySymbol();
		}
		else {
			planDirty = true;
		}
	}

	public void onPlanNotDirty() {
		if(planDirty) {
			planDirty = false;
			updateDirtySymbol();
		} else {
			planDirty = false;
		}
	}

	protected void updateDirtySymbol() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String currentText = planName.getText();
				if(planDirty) {
					planName.setText(DIRTY_STRING + currentText);
				} else {
					if(currentText.startsWith(DIRTY_STRING)) {
						planName.setText(currentText.substring(DIRTY_STRING.length()));
					}
				}
			}
		});
	}
}
