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

import java.util.List;

/**
 * in-place QuickSort for ArrayLists
 */
public class QuickSorter<TYPE extends Comparable<? super TYPE>> {

    public List<TYPE> sort(List<TYPE> list) {
        return sort(list, 0, list.size()-1);
    }
    public List<TYPE> sort(List<TYPE> list, int left, int right) {
        if(list != null && left < right) {
            int pivot = left + (right-left)/2;
            int partitionIdx = partition(list, left, right, pivot);
            sort(list, left, partitionIdx-1);
            sort(list, partitionIdx+1, right);
        }
        return list;
    }

    protected void swap(List<TYPE> list, int a, int b) {
        TYPE tmp = list.get(b);
        list.set(b, list.get(a));
        list.set(a, tmp);
    }

    protected int partition(List<TYPE> list, int left, int right, int pivot) {
        TYPE pivotVal = list.get(pivot);
        swap(list, right, pivot);
        int swapIdx = left; 
        for(int i = left; i < right; i++) {
            if(list.get(i).compareTo(pivotVal) < 0) {
                swap(list, i, swapIdx);
                swapIdx++;
            }
        }
        swap(list, right, swapIdx);
        
        return swapIdx;
    }

}
