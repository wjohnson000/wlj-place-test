/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.*;

/**
 * @author wjohnson000
 *
 */
public class CircularListX<E> implements Iterable<E> {

    int size = 0;
    private List<E> myList = new LinkedList<E>();

    public CircularListX(int size) {
        this.size = size;
    }

    @Override
    public Iterator<E> iterator() {
        return myList.iterator();
    }

    public void add(E item) {
        if (myList.size() < size) {
            myList.add(item);
        } else {
            myList.remove(0);
            myList.add(item);
        }
    }

    public E get(int index) {
        return myList.get(index);
    }

}
