/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Construct for storing request duration events. Synchronization is up to the using code.
 * The most important methods are used for storing AccumulatorEvents (request service time duration and timestamp)
 *   and for returning the M highest durations from N events (to be used in calculating the P99).
 */
public class CircularList<T> implements Iterable<T> {

    
    private T[] circularArrayList;
    private int lastAddedIndex = -1;
    private int highestIndexSoFar = lastAddedIndex;
    private boolean filledOnce = false;

    @SuppressWarnings("unchecked")
    public CircularList(int size) {
        circularArrayList = (T[])new Object[size];
        for (int index = 0; index < circularArrayList.length; ++index) {
            circularArrayList[index] = null;
        }
    }

    /**
     *
     * @param item
     * @return
     */
    public int add( T item ) {
        lastAddedIndex++;
        if (lastAddedIndex > highestIndexSoFar) {
            highestIndexSoFar = lastAddedIndex;
            if ( highestIndexSoFar >= circularArrayList.length )
                filledOnce = true;
        }
        if (lastAddedIndex >= circularArrayList.length) {
            lastAddedIndex = 0;
        }
        circularArrayList[lastAddedIndex] = item;
        return lastAddedIndex;
    }

    /**
     *
     * @param index
     * @return
     */
    public T get(int index) {
        if ((0 <= index && index <= lastAddedIndex) || (filledOnce && index > lastAddedIndex)) {
            return circularArrayList[index];
        }
        else {
            throw new ArrayIndexOutOfBoundsException("Attempted to access list element by index that is out of bounds.");
        }
    }

    /**
     * @return the highest index utilized so far
     */
    public int getHighestIndexSoFar() {
        return highestIndexSoFar;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = lastAddedIndex;

            @Override
            public boolean hasNext() {
                return ((0 <= index && index <= lastAddedIndex) || (filledOnce && index > lastAddedIndex));
            }

            @Override
            public T next() {
                if ((0 <= index && index <= lastAddedIndex) || (filledOnce && index > lastAddedIndex)) {
                    T item = circularArrayList[index--];
                    if ( index < 0 )
                        index = circularArrayList.length - 1;
                    return item;
                }
                else {
                    throw new NoSuchElementException("Iterator next() request cannot be fulfilled.");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("The remove() method does not apply to this circular list class");
            }
        };
    }


}