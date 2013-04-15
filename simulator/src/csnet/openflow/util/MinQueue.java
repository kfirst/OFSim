package csnet.openflow.util;

import java.util.PriorityQueue;

public class MinQueue<E> implements Queue<E> {
    private PriorityQueue<E> queue = new PriorityQueue<E>();

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
