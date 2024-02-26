import java.io.PrintWriter;

public class LList<T> implements List<T>{

    private static class LinkedNode<T>{
        T element;
        LinkedNode<T> next;
        public LinkedNode(T element, LinkedNode<T> next){
            this.element = element;
            this.next = next;
        }
//        public LinkedNode(LinkedNode<T> next){
//            this.next = next;
//        }
//        LinkedNode<T> next(){
//            return next;
//        }
//        LinkedNode<T> setNext(LinkedNode<T> next){
//            return this.next = next;
//        }
//        T element(){
//            return element;
//        }
//        T setElement(T elem){
//            return element = elem;
//        }
    }
    private int length;
    private LinkedNode<T> head;
    private LinkedNode<T> curr;

    public LList(){
        curr = new LinkedNode<>(null,null);//为-1位置单独设置一个节点，仅在空表插入时使用。
        head = curr.next = new LinkedNode<>(null,null);
        length = 0;
    }
    @Override
    public void insert(T newElement) {
        if(curr.next == null) return;
        //cursor.next = new LinkedNode<>(newElement, cursor.next)
        curr.next.next = new LinkedNode<>(newElement, curr.next.next);
        curr = curr.next;
        length++;
    }

    @Override
    public void remove() {
        if(isEmpty()) return;
        if(curr.next.next == null){
            curr.next = null;
            curr = head;//在线性表尾部删除元素需要单独考虑
        }else {
            curr.next = curr.next.next;
        }
        length--;
    }

    @Override
    public void replace(T newElement) {
        if(isEmpty()) return;
        curr.next.element = newElement;
    }

    @Override
    public void clear() {
        curr = new LinkedNode<>(null,null);//为-1位置单独设置一个节点，仅在空表插入时使用。
        head = curr.next = new LinkedNode<>(null,null);
        length = 0;
    }

    @Override
    public boolean isEmpty() {
        return length == 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean gotoBeginning() {
        curr = head;
        return true;
    }

    @Override
    public boolean gotoEnd() {
        while( curr.next!=null && curr.next.next != null ){
            curr = curr.next;
        }
        return true;
    }

    @Override
    public boolean gotoNext() {
        if(isEmpty()) return false;
        if(curr.next.next != null){
            curr = curr.next;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoPrev() {
        if(curr == head || isEmpty()){
            return false;
        }
        LinkedNode<T> temp = head;
        while(temp.next != curr){
            temp = temp.next;
        }
        curr = temp;
        return true;
    }

    @Override
    public T getCursor() {
        if(isEmpty()) return null;
        return curr.next.element;
    }

    @Override
    public void showStructure(PrintWriter pw) {
        int cursor=-1;
        if(!isEmpty()) {
            cursor++;
            for (LinkedNode i = head; i != curr; i = i.next) {
                cursor++;
            }
        }
        if(isEmpty()) pw.print("Empty list");
        LinkedNode<T> temp = head;
        while(temp.next!= null){
            pw.print(temp.next.element + " ");
            temp=temp.next;
        }
        pw.println(" {capacity = inf, length = "+length+", Cursor = " + cursor+"}");
    }



    @Override
    public void moveToNth(int n) {
        if(length <= n){
            System.out.println("moveToNth: List should contain at least n + 1 elements.");
            return;
        }
        T temp = curr.next.element;
        remove();
        curr = head;
        for(int i=0; i<n-1; i++){
            curr = curr.next;
        }
        insert(temp);
    }

    @Override
    public boolean find(T searchElement) {
        curr = head;
        while(true){
            if(curr.next.element.equals(searchElement)){
                return true;
            }
            if(curr.next.next==null){
                return false;
            }
            curr = curr.next;
        }
    }
}
