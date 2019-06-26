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
package gov.nasa.arc.verve.freeflyer.workbench.widget;

import gov.nasa.arc.irg.plan.bookmarks.StationBookmarkList;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;
import gov.nasa.arc.irg.plan.ui.io.IBookmarksListListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class BookmarksWidget implements IBookmarksListListener, PropertyChangeListener {
	private StationBookmarkList bookmarks;
	private String[] bookmarkNames;
	private String NO_BOOKMARKS_FOUND = "No bookmarks found";
	protected StationAndPointWidget parent;
	Combo bookmarksCombo; // StationAndPointWidget needs access
	Label validLabel;
	String validString = "          ", overriddenString = "Overridden";
	
	protected Composite createBookmarksArea(StationAndPointWidget parent, Composite container) {
		this.parent = parent;
		loadBookmarkList();
		BookmarkListBuilder.addListener(this);
		Composite top = setupTopOfCoordinateArea(container);
		
		makeSetBookmarkLine(top);
		return top;
	}
	
	protected void makeSetBookmarkLine(Composite top) {
		Label l1 = new Label(top, SWT.NONE);
		l1.setText("Bookmark");
		bookmarksCombo = new Combo(top, SWT.READ_ONLY);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		bookmarksCombo.setLayoutData(gd);
		bookmarksCombo.setItems(bookmarkNames);
		
		validLabel = new Label(top, SWT.NONE);
		GridData gdValid = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		validLabel.setLayoutData(gdValid);
		validLabel.setText(validString);
		
		parent.addChildControl(new Label(top, SWT.NONE));
		parent.addChildControl(l1);
		parent.addChildControl(bookmarksCombo);
	}
	
	void updateValidLabel() {
		ModuleBayStation mbs = (ModuleBayStation) parent.getModel();
		if(mbs != null && mbs.getCoordinate().isBookmarkValid()) {
			validLabel.setText(validString);
		} else {
			validLabel.setText(overriddenString);
		}
	}

	private void loadBookmarkList() {
		bookmarks = BookmarkListBuilder.getStaticBookmarkList();

		if(bookmarks == null) {
			bookmarkNames = new String[]{NO_BOOKMARKS_FOUND};
		} else {
			bookmarkNames = bookmarks.getArrayOfNames();
		}
	}
	
	public void updateComboBoxes() {
		loadBookmarkList();
		bookmarksCombo.setItems(bookmarkNames);
	}
	
	protected void makeSeparator(Composite top) {
		Label separator = new Label(top, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		separator.setLayoutData(data);
		parent.addChildControl(separator);
	}
	
	protected Composite setupTopOfCoordinateArea(Composite container) {
		Composite top = new Composite(container, SWT.None);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.horizontalSpan = 5;
		top.setLayoutData(gd);
		top.setLayout(new GridLayout(3, false));
		return top;
	}

	public void onBookmarksListChanged() {
		updateComboBoxes();
	}
	
	public void dispose() {
		((ModuleBayStation) parent.getModel()).getCoordinate().removePropertyChangeListener(this);
		BookmarkListBuilder.removeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		updateValidLabel();
	}
}
