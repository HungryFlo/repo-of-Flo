public class LQueue<T> implements Queue<T>{
    //内部类LinkedNode
    private static class LinkedNode<T>{
        T element;
        LinkedNode<T> next;
        public LinkedNode(T element , LinkedNode<T> next){
            this.element = element;
            this.next = next;
        }
    }

    //定义front, rear, length
    private LinkedNode<T> front;
    private LinkedNode<T> rear;
    private int length;

    //constructor
    public LQueue(){
        front = null;
        rear = null;
        length = 0;
    }

    //清空队列
    @Override
    public void clear() {
        front = rear = null;
        length = 0;
    }

    //入队操作
    @Override
    public void enqueue (T newElement) {
        if(isEmpty()){
            front = rear = new LinkedNode<T>(newElement,null);
        } else {
            rear.next = new LinkedNode<T>(newElement,null);
            rear = rear.next;
        }
        length++;
    }

    //出队操作
    @Override
    public T dequeue() {
        if(!isEmpty()) {
            LinkedNode<T> temp = front;
            front = front.next;
            length--;
            return temp.element;
        }
        return null;
    }

    //读取首元素
    @Override
    public T firstValue() {
        if(isEmpty()) return null;
        return front.element;
    }

    //判空
    @Override
    public boolean isEmpty() {
        return length==0;
    }

    //判满
    @Override
    public boolean isFull(){
        return false;
    }

    //队长
    @Override
    public int length() {
        return length;
    }

    public String toString()
    {
        if(isEmpty()) return "Empty Queue!";
        StringBuilder out = new StringBuilder((length() + 1) * 4);
        LinkedNode<T> temp = front;
        out.append("< ");
        while(temp != null){
            out.append(temp.element);
            out.append(" ");
            temp = temp.next;
        }
        out.append(">");
        return out.toString();
    }

    //一些测试代码
    public static void main(String[] args){
        LQueue<Integer> numLS = new LQueue<>();
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
