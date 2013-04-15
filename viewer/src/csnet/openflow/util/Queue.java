package csnet.openflow.util;

public interface Queue<E> {
    public boolean add(E e);

    public E poll();

    public E peek();
}
