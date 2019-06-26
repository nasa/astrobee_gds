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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple manager to keep a list of undoables, with listener to handle the addition and removal of undoables.
 * @author tecohen
 *
 */
public class UndoableHistoryManager {

	/**
	 * the list of operations available for undo, LIFO
	 */
	private List<IUndoable> m_undoList = Collections.synchronizedList(new ArrayList<IUndoable>());

	private Set<IUndoableHistoryListener> m_listeners = new HashSet<IUndoableHistoryListener>();
	
	public static UndoableHistoryManager INSTANCE = new UndoableHistoryManager();
	
	protected UndoableHistoryManager() {
		
	}

	/**
	 * a lock that is used to synchronize access between the undo and redo
	 * history
	 */
	final Object undoHistoryLock = new Object();


	public void add(IUndoable operation) {
		assert(operation != null);

		synchronized (undoHistoryLock) {
			m_undoList.add(operation);
		}
		notifyAdd(operation);

	}

	public void remove(IUndoable operation){
		assert(operation != null);
		synchronized (undoHistoryLock) {
			m_undoList.add(operation);
		}
		notifyRemove(operation);

	}

	/*
	 * Notify listeners that an operation has been added.
	 */
	private void notifyAdd(IUndoable operation) {
		for (IUndoableHistoryListener listener : m_listeners){
			listener.undoableAdded(operation);
		}
	}

	/*
	 * Notify listeners that an operation has been removed.
	 */
	private void notifyRemove(IUndoable operation) {
		for (IUndoableHistoryListener listener : m_listeners){
			listener.undoableRemoved(operation);
		}
	}


	public void addHistoryListener(IUndoableHistoryListener listener) {
		m_listeners.add(listener);
	}


}
