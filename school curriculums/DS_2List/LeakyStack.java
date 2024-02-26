public class LeakyStack<T> {
    private static final int DEFAULT_SIZE = 6;
    private int size = 6;
    private int front;
    private int rear;
    private T[] listArray;

    LeakyStack(){
        setup(DEFAULT_SIZE);
    }
    LeakyStack(int capacity){
        setup(capacity+1);
    }
    private void setup(int size){
        this.size = size;
        listArray = (T[]) new Object[size];
        front = 0;
        rear = 0;
    }

    public boolean isFull(){
        if((rear + 1) % size == front){
            return true;
        }
        return false;
    }

    public boolean isEmpty(){
        if(rear == front){
            return true;
        }
        return false;
    }

    /**
     * Push newELement into LeakyStack,
     * and return whether a certain element leaking from the Stack.
     * @param newElement
     * @return "true" if something leaked out
     */
    public boolean push(T newElement){
        boolean flag = false;
        if(isFull()){
            rear = (rear-1+size) % size;
            flag = true;
        }
        //这里先进行Leak和后进行Leak是很有讲究的，
        //如果先push再判断栈满的话，刚刚满的栈就会漏出一个元素，变得不满
        //这样的话push操作后栈永远满不了，而是只能处在“剩一个元素”的状态
        //这样本质上会让栈的容量-1
        listArray[front] = newElement;
        front = (front-1+size) % size;
        return flag;
    }

    /**
     * Pop the element at the top of the stack.
     * @return the popped element
     */
    public T pop(){
        if(!isEmpty()){
            front = (front+1) % size;
            return listArray[front];
        }
        return null;
    }

    /**
     * Get the value of the element at the top of the stack.
     * @return value of that element
     */
    public T topValue(){
        return listArray[(front+1)%size];
    }

    public void clear(){
        setup(size);
    }

    public int length(){
        return (rear - front + size) % size;
    }

    @Override
    public String toString() {
        if(isEmpty()){
            return "Empty Stack!";
        }
        {
            StringBuilder out = new StringBuilder((length() + 1) * 4);
            out.append("< ");
            for (int i = (front+1)%size; i!=rear; i=(i+1)%size) {
                out.append(listArray[i]);
                out.append(" ");
            }
            out.append(listArray[rear]);
            out.append(" ");
            out.append(">");
            out.append(" {numInStack = ");
            out.append(length());
            out.append("}");
            return out.toString();
        }
    }

    public static void main(String[] args){
        LeakyStack<Integer> numLS = new LeakyStack<>(6);
        numLS.push(1);
        System.out.println(numLS);
        numLS.push(2);
        System.out.println(numLS);
        numLS.push(3);
        System.out.println(numLS);
        numLS.push(4);
        System.out.println(numLS);
        numLS.push(5);
        System.out.println(numLS);
        numLS.push(6);
        System.out.println(numLS);
        numLS.push(7);
        System.out.println(numLS);
        numLS.pop();
        System.out.println(numLS);
        numLS.pop();
        System.out.println(numLS);
        numLS.pop();
        System.out.println(numLS);
        numLS.clear();
        System.out.println(numLS);
        numLS.pop();
        System.out.println(numLS);
        numLS.push(7);
        System.out.println(numLS);
        numLS.push(8);
        System.out.println(numLS);
        numLS.push(9);
        System.out.println(numLS);
        numLS.push(10);
        System.out.println(numLS);
        numLS.push(11);
        System.out.println(numLS.topValue());
        System.out.println(numLS);
    }
}