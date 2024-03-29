package 准备工作;

public interface Stack<T> {

  public void clear();

  /** Push an element onto the top of the stack.
      @param it The element being pushed onto the stack. */
  public void push(T it);

  /** Remove and return the element at the top of the stack.
      @return The element at the top of the stack. */
  public T pop();

  /** @return A copy of the top element. */
  public T topValue();

  /** @return The number of elements in the stack. */
  public int length();
  /** @return true if the stack is empty; otherwise false. */
  public boolean isEmpty();
  /** @return true if the stack is full; otherwise false. */
  public boolean isFull();
};
