import java.io.PrintWriter;

public class LListpro<T> implements List<T>{
    private class LinkedNode{
        T element;
        LinkedNode next;
        LinkedNode(T element, LinkedNode next){
            this.element = element;
            this.next = next;
        }
    }

    private LinkedNode dummy;
    private LinkedNode curr;
    private int length;
    LListpro() {
        curr = new LinkedNode(null, new LinkedNode(null, null));
        dummy = curr.next;
        length = 0;
    }


    @Override
    public void insert(T newElement){
        //漏掉了一种可能出现的情况，向空表添加元素，但不是在最初，而是通过若干步删除删成空表
        //此时，表中空无一物，但是curr==dummy而非在最初的"-1位置"
        if(newElement != null){
//            if(curr.next==null){
//                curr.next = new LinkedNode(newElement,null);
//            }
            if(isEmpty()){
                clear();
            }
            LinkedNode temp = curr.next.next;
            curr.next.next = new LinkedNode(newElement,temp);
            length++;
            curr = curr.next;
        }
    }

    @Override
    public void remove() {
        if(!isEmpty() && curr.next!=null){
            curr.next = curr.next.next;
            length--;
        }
        if(curr.next == null){
            curr = dummy;
        }
    }

    @Override
    public void replace(T newElement) {
        if(isEmpty()) return;
        else if (newElement == null) {
            return;
        }else {
            curr.next.element = newElement;
        }
    }

    @Override
    public void clear() {
        curr = new LinkedNode(null, new LinkedNode(null, null));
        dummy = curr.next;
        length = 0;
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
            curr = dummy;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoEnd() {
        if(!isEmpty()) {
            for (;curr.next!=null && curr.next.next != null; curr = curr.next) ;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoNext() {
        if(isEmpty() || curr.next == null || curr.next.next == null) return false;
        curr = curr.next;
        return true;
    }

    @Override
    public boolean gotoPrev() {
        if(!isEmpty()){
            LinkedNode i = dummy;
            for(; i.next!=curr; i = i.next){
                if(i.next==null){
                    return false;
                }
            }
            curr = i;
            return true;
        }
        return false;
    }

    @Override
    public T getCursor() {
        if(!isEmpty()){
            T currElem = curr.next.element;
            return currElem;
        }
        return null;
    }

    @Override
    public void showStructure(PrintWriter pw) {
        if(isEmpty()) pw.println("Empty list"+" {length = "+length+", cursor = " + "-1" +"}");
        else {
            LinkedNode temp = dummy.next;
            int cursor = -1;
            for(int i = 0; i < length; i++, temp=temp.next){
                pw.print(temp.element + " ");
                if(curr.next == temp){
                    cursor = i;
                }
            }
            pw.println("{length = " + length + ",cursor = " + cursor + "}");
        }
    }

    @Override
    public void moveToNth(int n) {
        if(length <= n){
            System.out.println("moveToNth: List should contain at least n + 1 elements.");
            return;
        }
        T temp = curr.next.element;
        remove();
        curr = dummy;
        for(int i=0; i<n-1; i++){
            curr = curr.next;
        }
        insert(temp);
    }

    @Override
    public boolean find(T searchElement) {
        curr = dummy;
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
