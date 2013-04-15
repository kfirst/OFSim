package csnet.openflow.util;

import java.util.LinkedList;

public class StackQueue<E> implements Queue<E> {
    private LinkedList<E> queue = new LinkedList<E>();

    @Override
    public boolean add(E e) {
        return queue.add(e);
    }

    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public E peek() {
        return queue.peek();
    }
}
