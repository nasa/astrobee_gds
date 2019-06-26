/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.common.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * wraps a LinkedBlockingDeque and puts at head of queue instead of tail
 */
public class BlockingLifoQueue<TYPE> implements BlockingQueue<TYPE> {
    private final LinkedBlockingDeque<TYPE> m_deque = new LinkedBlockingDeque<TYPE>();

    @Override
    public boolean offer(TYPE arg0) {
        return m_deque.offerFirst(arg0);
    }

    @Override
    public boolean offer(TYPE arg0, long arg1, TimeUnit arg2) throws InterruptedException {
        return m_deque.offerFirst(arg0, arg1, arg2);
    }

    @Override
    public void put(TYPE arg0) throws InterruptedException {
        m_deque.putFirst(arg0);
    }


    @Override
    public TYPE element() {
        return m_deque.element();
    }

    @Override
    public TYPE peek() {
        return m_deque.peek();
    }

    @Override
    public TYPE poll() {
        return m_deque.poll();
    }

    @Override
    public TYPE remove() {
        return m_deque.remove();
    }

    @Override
    public boolean addAll(Collection<? extends TYPE> arg0) {
        return m_deque.addAll(arg0);
    }

    @Override
    public void clear() {
        m_deque.clear();
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return m_deque.containsAll(arg0);
    }

    @Override
    public boolean isEmpty() {
        return m_deque.isEmpty();
    }

    @Override
    public Iterator<TYPE> iterator() {
        return m_deque.iterator();
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return m_deque.removeAll(arg0);
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return m_deque.retainAll(arg0);
    }

    @Override
    public int size() {
        return m_deque.size();
    }

    @Override
    public Object[] toArray() {
        return m_deque.toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return m_deque.toArray(arg0);
    }

    @Override
    public boolean add(TYPE arg0) {
        return m_deque.add(arg0);
    }

    @Override
    public boolean contains(Object arg0) {
        return m_deque.contains(arg0);
    }

    @Override
    public int drainTo(Collection<? super TYPE> arg0) {
        return m_deque.drainTo(arg0);
    }

    @Override
    public int drainTo(Collection<? super TYPE> arg0, int arg1) {
        return m_deque.drainTo(arg0, arg1);
    }

    @Override
    public TYPE poll(long arg0, TimeUnit arg1) throws InterruptedException {
        return m_deque.poll(arg0, arg1);
    }

    @Override
    public int remainingCapacity() {
        return m_deque.remainingCapacity();
    }

    @Override
    public boolean remove(Object arg0) {
        return m_deque.remove(arg0);
    }

    @Override
    public TYPE take() throws InterruptedException {
        return m_deque.take();
    }
}
