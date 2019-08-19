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
package gov.nasa.arc.verve.freeflyer.workbench.dialog;

import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.bookmarks.StationBookmarkList;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.BeeCommandingPartOnTeleoperateTab;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class BookmarkManagerDialog extends Dialog{
	private StationBookmarkList bookmarks;
	private String[] bookmarkNames;
	private String NO_BOOKMARKS_FOUND = "No bookmarks found";
	private Combo deleteCombo;
	private String makeBookmarkString = "Make Bookmark...";
	private String makeBookmarkLabelString = "Bookmark This Location";
	private TypedObject selected;
	private EnlargeableButton addButtonTeleop, addButtonPlanEditor;
	protected MApplication application;
	boolean onTeleopTab = false;
	private IncrementableText[] pos;
	private IncrementableText[] rot;
	private BeeCommandingPartOnTeleoperateTab manualTeleopPart;
	
	@Inject
	public BookmarkManagerDialog(@Optional @Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			MApplication mapp) {
		super(parentShell);
		application = mapp;
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);

		EPartService eps = application.getContext().get(EPartService.class);
		MElementContainer<MUIElement> parent = eps.getActivePart().getParent();

		while(parent != null){
			if(parent.getElementId().toLowerCase().contains("teleop")){
				onTeleopTab = !onTeleopTab;
				manualTeleopPart = (BeeCommandingPartOnTeleoperateTab)application.getContext().get(EPartService.class).findPart("gov.nasa.arc.verve.freeflyer.workbench.part.manualCommanding").getObject();
				break;
			}
			parent = parent.getParent();	
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		loadBookmarkList();
		Composite top = setupTopOfCoordinateArea(parent);

		makeAddBookmarkFromTeleopLine(top);
		makeAddBookmarkFromPlanEditorLine(top);
		makeDeleteBookmarkLine(top);
		updateAddButtonEnabled();
		
		return parent;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Bookmarks Manager");
		newShell.setEnabled(true);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button - cancel doesn't mean anything for this dialog
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Inject @Optional
	public void acceptTypedObject(TypedObject selected) {
		this.selected = selected;
		if(addButtonTeleop == null || addButtonTeleop.isDisposed()) {
			return;
		}
		updateAddButtonEnabled();
	}

	@Override
	public boolean close() {
		boolean returnValue = super.close();
		application.getContext().set(BookmarkManagerDialog.class, null);
		return returnValue;
	}

	private void updateAddButtonEnabled() {
		if(selected instanceof ModuleBayStation) { 
			addButtonPlanEditor.setEnabled(true);
		} else {
			addButtonPlanEditor.setEnabled(false);
		}
		if( onTeleopTab ) {
			addButtonTeleop.setEnabled(true);
			addButtonPlanEditor.setEnabled(false);
		}
	}

	protected void makeAddBookmarkFromTeleopLine(Composite top) {
		Label l2 = new Label(top, SWT.NONE);
		l2.setText(makeBookmarkLabelString);

		addButtonTeleop = new EnlargeableButton(top, SWT.CENTER);
		addButtonTeleop.setText(makeBookmarkString + " (Teleop)");
		addButtonTeleop.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		addButtonTeleop.setButtonLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// Create a label to display what the user typed in
		final Label label = new Label(top, SWT.NONE);
		label.setText("\t\t\t\t\t");
		addButtonTeleop.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						"Save the location of the Teleop Preview as a Bookmark", "Enter a name for the bookmark", "<name>", null);
				if (dlg.open() == Window.OK) {
					String name = dlg.getValue();

					StationBookmark newSB = null;
					ModuleBayStation station = null;;

					pos = manualTeleopPart.getCurrentPosition();
					rot = manualTeleopPart.getCurrentRotation();
					
					station = new ModuleBayStation();
					station.setCoordinate(new ModuleBayPoint((float)pos[0].getNumber(),
							(float)pos[1].getNumber(),
							(float)pos[2].getNumber(),
							(float)rot[0].getNumber(),
							(float)rot[1].getNumber(),
							(float)rot[2].getNumber()));
					newSB = new StationBookmark(name, station.getCoordinate());

					BookmarkListBuilder.addBookmark(newSB);
					try {
						BookmarkListBuilder.saveBookmarksList();
					} catch (Exception e1) {
						label.setText("Error saving bookmark");
						return;
					}
					label.setText("Bookmark Saved");

					updateComboBoxes();
					ModuleBayPoint mbp = station.getCoordinate();
					mbp.setBookmark(newSB);
				}
			}	

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { //
			}
		});
	}

	protected void makeAddBookmarkFromPlanEditorLine(Composite top) {
		Label l2 = new Label(top, SWT.NONE);
		l2.setText(makeBookmarkLabelString);

		addButtonPlanEditor = new EnlargeableButton(top, SWT.CENTER);
		addButtonPlanEditor.setText(makeBookmarkString + " (Plan Editor)");
		addButtonPlanEditor.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		addButtonPlanEditor.setButtonLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// Create a label to display what the user typed in
		final Label label = new Label(top, SWT.NONE);
		label.setText("\t\t\t\t\t");
		addButtonPlanEditor.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!(selected instanceof ModuleBayStation) || onTeleopTab) {
					return; 
				}

				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						"Save the location of the selected Plan Station as a bookmark", "Enter a name for the bookmark", "<name>", null);
				if (dlg.open() == Window.OK) {
					String name = dlg.getValue();

					StationBookmark newSB = null;
					ModuleBayStation station = null;;
					if(!onTeleopTab){
						station = (ModuleBayStation)selected;
						newSB = new StationBookmark(name, station.getCoordinate());
					}

					BookmarkListBuilder.addBookmark(newSB);
					try {
						BookmarkListBuilder.saveBookmarksList();
					} catch (Exception e1) {
						label.setText("Error saving bookmark");
						return;
					}
					label.setText("Bookmark Saved");

					updateComboBoxes();
					ModuleBayPoint mbp = station.getCoordinate();
					mbp.setBookmark(newSB);
				}
			}	

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { //
			}
		});
	}

	protected void makeDeleteBookmarkLine(Composite top) {
		Label l1 = new Label(top, SWT.NONE);
		l1.setText("Delete Bookmark");
		deleteCombo = new Combo(top, SWT.READ_ONLY);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		deleteCombo.setLayoutData(gd);
		deleteCombo.setItems(bookmarkNames);

		EnlargeableButton deleteButton = new EnlargeableButton(top, SWT.CENTER);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		deleteButton.setButtonLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BookmarkListBuilder.deleteBookmark(deleteCombo.getText());
				try {
					BookmarkListBuilder.saveBookmarksList();
					updateComboBoxes();
					//					// need to reset the bookmark of the current model in the box
					//					ModuleBayStation mbp = (ModuleBayStation)parent.getModel();
					//					StationBookmark sb = mbp.getCoordinate().getBookmark();
					//					if(sb != null) {
					//						int index = BookmarkListBuilder.getIndexOfBookmark(sb);
					//						bookmarksCombo.select(index);
					//					}


				} catch (Exception e1) {
					System.out.println("Error deleting bookmark");
				}
			}	

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { //
			}
		});
	}

	protected void updateComboBoxes() {
		loadBookmarkList();
		deleteCombo.setItems(bookmarkNames);
	}

	protected Composite setupTopOfCoordinateArea(Composite container) {
		Composite top = new Composite(container, SWT.None);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.horizontalSpan = 5;
		top.setLayoutData(gd);
		top.setLayout(new GridLayout(3, false));
		return top;
	}

	private void loadBookmarkList() {
		bookmarks = BookmarkListBuilder.getStaticBookmarkList();

		if(bookmarks == null) {
			bookmarkNames = new String[]{NO_BOOKMARKS_FOUND};
		} else {
			bookmarkNames = bookmarks.getArrayOfNames();
		}
	}
}
