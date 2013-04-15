package csnet.openflow.util;

import csnet.openflow.util.FibonacciHeap.Entry;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class NoOverridePriorityQueue<E> {

    private FibonacciHeap<E> priorityQueue;
    private Map<E, Entry<E>> index;

    public NoOverridePriorityQueue(Comparator<E> comparator) {
        priorityQueue = new FibonacciHeap<E>();
        index = new TreeMap<>(comparator);
    }

    public boolean exist(E element) {
        return index.containsKey(element);
    }

    public E get(E element) {
        Entry<E> entry = index.get(element);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    public int size() {
        return index.size();
    }

    public boolean isEmpty() {
        return index.isEmpty();
    }

    public E put(E element, double priority) {
        if (priority <= 0) {
            throw new IllegalArgumentException("priority must greater than 0");
        }

        if (index.containsKey(element)) {
            return updateUnchecked(element, priority);
        } else {
            addUnchecked(element, priority);
            return null;
        }
    }

    public boolean delete(E element) {
        if (index.containsKey(element)) {
            deleteUnchecked(element);
            return true;
        }
        return false;
    }

    public E pollMin() {
        if (index.isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        E element = priorityQueue.dequeueMin().getValue();
        index.remove(element);
        return element;
    }

    public E peekMin() {
        if (index.isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return priorityQueue.min().getValue();
    }

    private E updateUnchecked(E element, double priority) {
        Entry<E> entry = index.get(element);
        E original = entry.getValue();
        double oldPriority = entry.getPriority();
        if (priority > oldPriority) {
            deleteUnchecked(original);
            addUnchecked(original, priority);
        } else {
            priorityQueue.decreaseKey(entry, priority);
        }
        return original;
    }

    private void deleteUnchecked(E element) {
        Entry<E> entry = index.remove(element);
        priorityQueue.delete(entry);
    }

    private void addUnchecked(E element, double priority) {
        Entry<E> entry = priorityQueue.enqueue(element, priority);
        index.put(element, entry);
    }
}
