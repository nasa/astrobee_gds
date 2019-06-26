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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rapid.AckCompletedStatus;

public class GuiUtils {
	private static int bigFontSize = 11;
	private static String blankDash = "-";
	public static String uninitializedString = blankDash;
	
	public static Font makeBigFont(Composite parent, Label label) {
		return makeBigFont(parent, label, bigFontSize);
	}

	public static Font makeBigFont(Composite parent, Label label, int size) {
		LocalResourceManager resManager = 
				new LocalResourceManager(JFaceResources.getResources(), parent);

		FontDescriptor bigDescriptor = FontDescriptor.createFrom(label.getFont()).setHeight(size);
		return resManager.createFont(bigDescriptor);
	}
	
	public static void makeHorizontalSeparator(Composite parent, int horizontalSpan) {
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = horizontalSpan;
		separator.setLayoutData(data);
	}

	public static void makeHorizontalSeparator(Composite parent) {
		makeHorizontalSeparator(parent, 3);
	}

	public static void giveGridLayout(Composite c, int columns) {
		GridLayout gl = new GridLayout(columns, false);
		c.setLayout(gl);
	}
	
	public static void fillColumn(Tree tree, int[] widths) {
		// calculate wirelessWidths
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
		if(columnsWidth + widths[widths.length - 1] + tree.getBorderWidth() * 2 < size.x - scrollBarWidth) {
			lastColumn.setWidth(size.x - scrollBarWidth - columnsWidth - tree.getBorderWidth() * 2);

		} else {
			// fall back to minimum, scrollbar will show
			if(lastColumn.getWidth() != widths[widths.length - 1]) {
				lastColumn.setWidth(widths[widths.length - 1]);
			}
		}
	}
	
	public static String prettyPrint(AckCompletedStatus acs) {
		// it's an enum, but it won't let me switch
		if(acs.equals(AckCompletedStatus.ACK_COMPLETED_NOT)) {
			return "Pending...";
		}
		if(acs.equals(AckCompletedStatus.ACK_COMPLETED_OK)) {
			return "Complete";
		}
		if(acs.equals(AckCompletedStatus.ACK_COMPLETED_BAD_SYNTAX)) {
			return "Unrecognized syntax";
		}
		if(acs.equals(AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED)) {
			return "Failed";
		}
		if(acs.equals(AckCompletedStatus.ACK_COMPLETED_CANCELED)) {
			return "Skipped";
		}
		return "~~";
	}
	
	private static int roundToNearest10(int input) {
		int rounded;
		int remainder = input % 10;
		
		if(remainder < 5) {
			rounded = input - remainder;
		} else {
			rounded = input + (10-remainder);
		}
		return rounded;
	}
	
	public static String convertMinutesToHHM0(int totalMinutes) {
		if(totalMinutes < 60) {
			return Integer.toString(roundToNearest10(totalMinutes));
		}
		int hours = totalMinutes / 60;
		int min = totalMinutes % 60;
		
		int roundedMinutes = roundToNearest10(min);
		
		String minutesString;
		if(hours > 0 && roundedMinutes < 10) {
			minutesString = "0" + Integer.toString(roundedMinutes);
		} else {
			minutesString = Integer.toString(roundedMinutes);
		}
		
		return  Integer.toString(hours) + ":" + minutesString;
	}
	
	// ONEWORD -> Oneword
	// TWO_WORDS -> Two Words
	// oneword -> Oneword
	// two_words -> Two Words
	public static String toTitleCase(String input) {
		if(input == null) {
			return blankDash;
		}
		
		if(input.length() < 2) {
			return input;
		}
		
		
		int spaceIndex = input.indexOf("_");
		
		if(spaceIndex < 0) {
			// no spaces
			return input.substring(0, 1).toUpperCase()
					+ input.substring(1).toLowerCase();
		} else {
			return input.substring(0, 1).toUpperCase()
					+ input.substring(1, spaceIndex).toLowerCase()
					+ " "
					+ input.substring(spaceIndex+1, spaceIndex+2).toUpperCase()
					+ input.substring(spaceIndex+2).toLowerCase();
		}
	}
	
	public static Composite setupInnerComposite(Composite parent, int cellsAcross, int alignment) {
		Composite innerComposite = new Composite(parent, SWT.None);
		innerComposite.setLayout(new GridLayout(cellsAcross,false));
		GridData compositeGd = new GridData(alignment);
		innerComposite.setLayoutData(compositeGd);
		return innerComposite;
	}
	
	public static Composite setupInnerCompositeEvenSpacing(Composite parent, int cellsAcross, int alignment) {
		Composite innerComposite = new Composite(parent, SWT.None);
		innerComposite.setLayout(new GridLayout(cellsAcross,true));
		GridData compositeGd = new GridData(alignment);
		innerComposite.setLayoutData(compositeGd);
		return innerComposite;
	}
}
