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
package gov.nasa.arc.verve.freeflyer.workbench.parts.advanced;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.DiskStateHolder;
import gov.nasa.arc.irg.freeflyer.rapid.frequent.FrequentTelemetryListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.DiskInfoGds;
import gov.nasa.arc.irg.freeflyer.rapid.state.RosTopicsList.ARosTopic;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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

public class DataSizePart implements FrequentTelemetryListener {
	private static final Logger logger = Logger.getLogger(DataSizePart.class);
	private String titleString = "Data Size";
	protected Agent agent;
	protected MessageType[] sampleType;
	private Label agentNameLabel;
	private int entireWidth = 3;
	private int[] dataSizeTreeWidths = {150, 150, 150};
	private DiskStateHolder diskStateHolder;
	protected String participantId = Rapid.PrimaryParticipant;
	protected boolean waitingToSendSetCommand = false;
	
	@Inject
	IEclipseContext context;

//	protected AstrobeeStateManager astrobeeStateManager;
	private String[] diskNameLabel, dataSizeLabel, diskSizeLabel;
	private int maxNumDisks;
	protected String myId;

	private Tree dataSizeTree;
	private TreeViewer dataSizeViewer;
	private DecimalFormat formatter = new DecimalFormat("#.######");

	protected Tree dataToDiskSettingsTree;
	protected TreeViewer dataToDiskSettingsTreeViewer;
	protected Tree topicsTree;
	protected TreeViewer topicsTreeViewer;
	protected Display savedDisplay;

	@Inject 
	public DataSizePart(Composite parent) {
		savedDisplay = Display.getDefault();
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		GridLayout gl = new GridLayout(3, false);
		parent.setLayout(gl);

		GridData gdThreeWide = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdThreeWide.horizontalSpan = 3;
		gdThreeWide.verticalSpan = 6;
		parent.setLayoutData(gdThreeWide);
		myId = Agent.getEgoAgent().name();

		createAgentNameLabel(parent);
		makeDiskDataDisplay(parent);

		sampleType = new MessageType[] {
				MessageTypeExtAstro.COMPRESSED_FILE_ACK_TYPE,
		};
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		agent = a; 
		agentNameLabel.setText(agent.name() + " " + titleString );
	}
	
	protected List<MessageType> getMessageTypes() {
		List<MessageType> ret = new ArrayList<MessageType>();
		for(int i=0; i<getSampleType().length; i++) {
			ret.add(getSampleType()[i]);
		}
		return ret;
	}
	
	protected MessageType[] getSampleType() {
		return sampleType;
	}

	public Agent getAgent() {
		return agent;
	}

	public void makeDiskDataDisplay(Composite parent) {
		Composite c = setupTreeSectionComposite(parent, 3);

		dataSizeTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		dataSizeTree.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataSizeTree.setLayoutData(gd);

		dataSizeViewer = new TreeViewer(dataSizeTree);

		TreeColumn col1 = new TreeColumn(dataSizeTree, SWT.LEFT);
		dataSizeTree.setLinesVisible(true);
		col1.setText("Disk");
		col1.setWidth(dataSizeTreeWidths[0]);

		TreeColumn col2 = new TreeColumn(dataSizeTree, SWT.LEFT);
		col2.setText("Data Size");
		col2.setWidth(dataSizeTreeWidths[1]);

		TreeColumn col3 = new TreeColumn(dataSizeTree, SWT.LEFT);
		col3.setText("Disk Size");
		col3.setWidth(dataSizeTreeWidths[2]);

		dataSizeTree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				fillColumn(dataSizeTree);
			}
		});

		dataSizeViewer.setContentProvider( new DataTreeContentProvider());
		dataSizeViewer.setLabelProvider(new DataTableLabelProvider());
	}

	private void updateInput() {
		if(dataSizeTree == null || dataSizeTree.isDisposed()) {
			return;
		}
		Integer[] nums = new Integer[maxNumDisks];
		for(int i = 0; i < maxNumDisks; i++) {
			nums[i] = i;
		}

		dataSizeViewer.setInput(nums);
		dataSizeViewer.refresh();
		dataSizeTree.getParent().redraw();
	}

	private Composite setupTreeSectionComposite(Composite parent, int width) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		c.setLayout(gl);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 200;
		data.horizontalSpan = width;
		c.setLayoutData(data);
		return c;
	}
	

	protected class RosTopicTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) { /**/ }

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Collection<?>) {
				return ((Collection<ARosTopic>)inputElement).toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {	
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(Object element) { 
			return null;
		}

		@Override
		public boolean hasChildren(Object element) { 
			return false;
		}
	}

	class RosTopicTableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){ return null; }

		public String getColumnText(Object element, int columnIndex){
			switch(columnIndex) {
			case 0:
				if(element instanceof ARosTopic) {
					return ((ARosTopic)element).topicName;
				}
				return null;
			case 1:
				if(element instanceof ARosTopic) {
					return GuiUtils.toTitleCase(((ARosTopic)element).getDownlink().toString());
				}
				return null;
			case 2:
				if(element instanceof ARosTopic) {
					return Float.toString(((ARosTopic)element).getFrequency());

				}
				return null;
			default:
				return null;
			}
		}

		public void addListener(ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(Object element, String property){ return false; }

		public void removeListener(ILabelProviderListener listener) { /**/ }
	}	

	class DataTableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){ return null; }

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
			case 0: return diskNameLabel[(Integer) element];
			case 1: return dataSizeLabel[(Integer) element];
			case 2: return diskSizeLabel[(Integer) element];
			default: return null;
			}
		}

		public void addListener(ILabelProviderListener listener) { /**/ }

		public void dispose() { /**/ }

		public boolean isLabelProperty(Object element, String property){ return false; }

		public void removeListener(ILabelProviderListener listener) { /**/ }
	}	

	protected class DataTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() { /**/ }

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) { /**/ }

		@Override
		public Object[] getElements(Object inputElement) {
			return (Integer[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {	return null; }

		@Override
		public Object getParent(Object element) { return null; }

		@Override
		public boolean hasChildren(Object element) { return false; }
	}

	protected void updateDiskDataLabels() {
		diskNameLabel = new String[maxNumDisks];
		dataSizeLabel = new String[maxNumDisks];
		diskSizeLabel = new String[maxNumDisks];
	}

	private void updateDiskStatus() {
		Vector<DiskInfoGds> diskInfo = diskStateHolder.getDiskInfo();
		if(diskInfo == null) {
			return;
		}

		maxNumDisks = diskInfo.size();
		updateDiskDataLabels();

		int i = 0;		
		for(DiskInfoGds info: diskInfo) {
			diskNameLabel[i] = info.getName();
			dataSizeLabel[i] = humanReadable(info.getDataSize());
			diskSizeLabel[i] = humanReadable(info.getDiskSize());
			i++;
		}
		updateInput();
	}
	
	public static String humanReadable(double size) {
		String[] bytes = new String[] { "B", "KB", "MB", "GB"};
		if(size <= 0){
			return "0";
		}
		//Math.log calculates how many zeroes in the entered number, and the division tells whether the output should be in bytes,
		//kilobytes, megabytes, etc. 	
	    int digitGroups = (int)(Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + bytes[digitGroups];
	}

	private void createAgentNameLabel(Composite parent) {
		agentNameLabel = new Label(parent, SWT.None);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = entireWidth;
		agentNameLabel.setLayoutData(data);
		agentNameLabel.setText(titleString);
		Font bigFont = GuiUtils.makeBigFont(parent, agentNameLabel);
		agentNameLabel.setFont(bigFont);
	}

	protected void fillColumn(Tree tree) {
		// calculate dataTreeWidths
		int columnsWidth = 0;
		for (int i = 0; i < tree.getColumnCount() - 1; i++) {
			columnsWidth += tree.getColumn(i).getWidth();
		}

		Point size = tree.getSize();

		int scrollBarWidth;
		ScrollBar verticalBar = tree.getVerticalBar();
		if(verticalBar.isVisible()) {
			scrollBarWidth = verticalBar.getSize().x + 10;
		} else {
			scrollBarWidth = 0;
		}

		// adjust column according to available horizontal space
		TreeColumn lastColumn = tree.getColumn(tree.getColumnCount() - 1);
		if(columnsWidth + dataSizeTreeWidths[dataSizeTreeWidths.length - 1] + tree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth - tree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if(lastColumn.getWidth() != dataSizeTreeWidths[dataSizeTreeWidths.length - 1]) {
				lastColumn.setWidth(dataSizeTreeWidths[dataSizeTreeWidths.length - 1]);
			}
		}
	}

	@Inject @Optional
	public void acceptComponentHolder(DiskStateHolder diskStateHolder) {
		this.diskStateHolder = diskStateHolder;
		diskStateHolder.addListener(this);
	}

	public void onSampleUpdate(Object sample) {
		if(savedDisplay.isDisposed()) {
			return;
		}
		savedDisplay.asyncExec(new Runnable() {
			public void run() {
				if(agentNameLabel != null && !agentNameLabel.isDisposed()) {
					updateDiskStatus();
				}
			}
		});
	}

	public void onConfigUpdate(Object config) {
		// TODO Auto-generated method stub
	}

}
