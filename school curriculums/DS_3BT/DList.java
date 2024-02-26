import java.io.PrintWriter;

public class DList<T> implements List<T>{
    private static class DLinkedNode<T>{
        DLinkedNode<T> prev;
        T element;
        DLinkedNode<T> next;
        public DLinkedNode(DLinkedNode<T> prev, T element, DLinkedNode<T> next){
            this.prev = prev;
            this.element = element;
            this.next = next;
        }
    }
    private int length;
    private DLinkedNode<T> head;
    private DLinkedNode<T> curr;
    public DList(){
        setup();
    }
    private void setup(){
        head = new DLinkedNode<>(null,null,null);
        curr = new DLinkedNode<>(null, null, head);
//        head.prev = curr;
        length = 0;
    }

    @Override
    public void insert(T newElement){
        if(newElement==null) return;
        if(isEmpty()) clear();
        if(isEmpty()){
            head = curr = new DLinkedNode<>(null,newElement,null);
        } else {
            curr.next = new DLinkedNode<>(curr, newElement, curr.next);
            if(curr.next.next!=null) {
                curr.next.next.prev = curr.next;
            }
            curr = curr.next;
        }
        length++;
    }

    @Override
    public void remove() {
        if(isEmpty() || length == 1){
            clear();
            return;
        } else if(curr == head && head.next!=null){
            head = curr = head.next;
            head.prev = null;
        } else if(curr.next == null && curr.prev!=null){
            curr.prev.next = null;
            curr = head;
        }
        else if(curr.next!=null && curr.prev != null){
            DLinkedNode del = curr;
            DLinkedNode delPrev = curr.prev;
            DLinkedNode delNext = curr.next;
            delPrev.next = curr.next;
            delNext.prev = curr.prev;
            curr = delNext;
        }
        length--;
    }

    @Override
    public void replace(T newElement) {
        if(isEmpty()) return;
        if(curr==null) return;
        if(newElement==null) return;
        curr.element = newElement;
    }

    @Override
    public void clear() {
        setup();
    }

    @Override
    public boolean isEmpty() {
        return length==0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean gotoBeginning() {
        if(!isEmpty()){
           curr = head;
        }
        return true;
    }

    @Override
    public boolean gotoEnd() {
        if(isEmpty()) return false;
        while(curr!=null && curr.next!=null) curr=curr.next;
        return true;
    }

    @Override
    public boolean gotoNext() {
        if(isEmpty()) return false;
        if(curr!=null && curr.next!=null){
            curr = curr.next;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoPrev() {
        if(isEmpty()) return false;
        if(curr!= null && curr.prev!=null){
            curr = curr.prev;
            return true;
        }
        return false;
    }

    @Override
    public T getCursor() {
        if(isEmpty()) return null;
        return curr.element;
    }

    @Override
    public void showStructure(PrintWriter pw) {
        if(isEmpty()) pw.println("Empty list"+" {length = "+length+", cursor = " + "-1" +"}");
        else {
            int cursor = -2;
            DLinkedNode temp = head;
            if(curr==head){
                cursor = -1;
            }
            int i=-1;
            while(temp!=null){
                i++;
                if(temp==curr){
                    cursor = i;
//                }if(temp.prev != null && temp.next!=null) {
//                    pw.print(temp.element + " {prev = " + temp.prev.element + " next = " + temp.next.element + "} ");
//                }else {
                }
                pw.print(temp.element + " ");
                temp = temp.next;
            }
//            if(temp!=null) {
//                for (int i = 1; i < length; i++) {
//                    pw.print(temp.element + " ");
//                    if (curr == temp) {
//                        cursor = i;
//                    }
//                    temp = temp.next;
//                }
//            }
            pw.println("{length = " + length + ",cursor = " + cursor + "}");
        }
    }

    @Override
    public void moveToNth(int n) {
        if(length <= n){
            System.out.println("moveToNth: List should contain at least n + 1 elements.");
            return;
        }
        T temp = curr.element;
        remove();
        curr = head;
        for(int i=0; i<n-1; i++){
            curr=curr.next;
        }
        insert(temp);
    }

    @Override
    public boolean find(T searchElement) {
        if(isEmpty()) return false;
        curr = head;
        while(true){
            if(curr.element.equals(searchElement)){
                return true;
            }
            if(curr.next==null){
                return false;
            }
            curr = curr.next;
        }
    }
}
