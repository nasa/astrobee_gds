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
package gov.nasa.arc.irg.util.undo;


/**
 * subset of IUndoableOperation provided by Eclipse
 * <p>
 * IUndoable defines an operation that can be undone and redone. 
 * </p>
 * <p>
 * Operations determine their ability to execute, undo, or redo according to the
 * current state of the application. They do not make decisions about their
 * validity based on where they occur in the operation history. That is left to
 * the particular operation history.
 * </p>
 * 
 */
public interface IUndoable {


	/**
	 * <p>
	 * Returns whether the operation can be redone in its current state.
	 * </p>
	 * 
	 * <p>
	 * Note: The computation for this method must be fast, as it is called
	 * frequently. If necessary, this method can be optimistic in its
	 * computation (returning true) and later perform more time-consuming
	 * computations during the actual redo of the operation, returning the
	 * appropriate status if the operation cannot actually be redone at that
	 * time.
	 * </p>
	 * 
	 * @return <code>true</code> if the operation can be redone;
	 *         <code>false</code> otherwise.
	 */
	boolean canRedo();

	/**
	 * <p>
	 * Returns whether the operation can be undone in its current state.
	 * </p>
	 * 
	 * <p>
	 * Note: The computation for this method must be fast, as it is called
	 * frequently. If necessary, this method can be optimistic in its
	 * computation (returning true) and later perform more time-consuming
	 * computations during the actual undo of the operation, returning the
	 * appropriate status if the operation cannot actually be undone at that
	 * time.
	 * </p>
	 * 
	 * @return <code>true</code> if the operation can be undone;
	 *         <code>false</code> otherwise.
	 */
	boolean canUndo();

	/**
	 * Dispose of the operation. This method is used when the operation is no
	 * longer kept in the history. Implementers of this method typically
	 * unregister any listeners.
	 * 
	 */
	void dispose();



	/**
	 * Return the label that should be used to show the name of the operation to
	 * the user. This label is typically combined with the command strings shown
	 * to the user in "Undo" and "Redo" user interfaces.  
	 * 
	 * @return the String label.  Should never be <code>null</code>.
	 */
	String getLabel();


	/**
	 * Redo the operation. This method should only be called after an operation
	 * has been undone.
	 * 
	 * @param info
	 *            the IAdaptable (or <code>null</code>) provided by the
	 *            caller in order to supply UI information for prompting the
	 *            user if necessary. 
	 * @return the IStatus of the redo. The status severity should be set to
	 *         <code>OK</code> if the redo was successful, and
	 *         <code>ERROR</code> if it was not. Any other status is assumed
	 *         to represent an incompletion of the redo.
	 * @throws Exception
	 *             if an exception occurred during redo.
	 */

	UndoStatus redo(Object info)
			throws Exception;


	/**
	 * Undo the operation. This method should only be called after an operation
	 * has been executed.
	 * 
	 * @param info
	 *            the IAdaptable (or <code>null</code>) provided by the
	 *            caller in order to supply UI information for prompting the
	 *            user if necessary. 
	 * @return the IStatus of the undo. The status severity should be set to
	 *         <code>OK</code> if the redo was successful, and
	 *         <code>ERROR</code> if it was not. Any other status is assumed
	 *         to represent an incompletion of the undo.
	 * @throws Exception
	 *             if an exception occurred during undo.
	 */
	UndoStatus undo(Object info)
			throws Exception;
	
}
