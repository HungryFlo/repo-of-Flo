public class AQueue<T> implements Queue<T> {
    private static final int DEFAULT_SIZE = 6;
    private int size = 6;
    private int front;
    private int rear;
    private T[] listArray;

    //构造函数
    AQueue(){
        setup(DEFAULT_SIZE);
    }
    AQueue(int capacity){
        setup(capacity+1);
    }
    //独立setup方法
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

    public void clear(){
        setup(size);
    }

    //入队操作
    @Override
    public void enqueue(T newElement) {
        if(!isFull()) {
            rear = (rear + 1) % size;
            listArray[rear] = newElement;
        }
    }

    //出队操作
    @Override
    public T dequeue() {
        if(!isEmpty()){
            front = (front + 1) % size;
        }
        return null;
    }

    //首元素值的读取
    @Override
    public T firstValue() {
        return listArray[(front+1)%size];
    }

    public int length(){
        return (rear - front + size) % size;
    }

    //使用StringBuider进行字符串构建
    @Override
    public String toString() {
        if(isEmpty()){
            return "Empty Queue!";
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
            out.append(" {numInQueue = ");
            out.append(length());
            out.append("}");
            return out.toString();
        }
    }

    //一些测试代码
    public static void main(String[] args){
        AQueue<Integer> numLS = new AQueue<>(6);
        numLS.enqueue(1);
        System.out.println(numLS);
        numLS.enqueue(2);
        System.out.println(numLS);
        numLS.enqueue(3);
        System.out.println(numLS);
        numLS.enqueue(4);
        System.out.println(numLS);
        numLS.enqueue(5);
        System.out.println(numLS);
        numLS.enqueue(6);
        System.out.println(numLS);
        numLS.enqueue(7);
        System.out.println(numLS);
        numLS.dequeue();
        System.out.println(numLS);
        numLS.dequeue();
        System.out.println(numLS);
        numLS.dequeue();
        System.out.println(numLS);
        numLS.clear();
        System.out.println(numLS);
        numLS.dequeue();
        System.out.println(numLS);
        numLS.enqueue(7);
        System.out.println(numLS);
        numLS.enqueue(8);
        System.out.println(numLS);
        numLS.enqueue(9);
        System.out.println(numLS);
        numLS.enqueue(10);
        System.out.println(numLS);
        numLS.enqueue(11);
        System.out.println(numLS.firstValue());
        System.out.println(numLS);
    }
}