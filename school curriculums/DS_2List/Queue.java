public interface Queue<T> {
    public void clear();
    public void enqueue(T newElement);
    public T dequeue();
    public T firstValue();
    public boolean isEmpty();
    public boolean isFull();
    public int length();
}
