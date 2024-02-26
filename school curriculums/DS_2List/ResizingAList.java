import java.io.PrintWriter;

public class ResizingAList<T> implements List<T>{
    private int capacity = 1;
    private int length;
    private int curr;
    private T[] listArray;

    ResizingAList(){
        listArray = setup(capacity);
        length = 0;
        curr = -1;
    }
    private T[] setup(int size){
        capacity = size;
        return (T[]) new Object[size];
    }

    private void swell(){
        T[] newArray = setup(2*capacity);
        for(int i=0; i<length; i++){
            newArray[i] = listArray[i];
        }
        listArray = newArray;
    }

    private void shrink(){
        T[] newArray = setup(capacity / 2);
        for(int i = 0; i < length; i ++){
            newArray[i] = listArray[i];
        }
        listArray = newArray;
    }

    @Override
    public void insert(T newElement){
        if (isFull()){
            swell();
        }
        if(isEmpty()){
            listArray[0] = newElement;
            curr = 0;
        }else {
            for (int i = length - 1; i > curr; i--) {
                listArray[i + 1] = listArray[i];
            }
            listArray[curr + 1] = (T) newElement;
            curr++;
        }
        //插入后要将光标右移
        length ++;
    }

    @Override
    public void remove() {
        if(isEmpty()) return;
        if(curr==length-1){
            curr = 0;
            length --;
            return;
        }
        for(int i=curr; i<length-1; i++){
            listArray[i] = listArray[i+1];
        }
        length--;
        if(length <= capacity/4){
            shrink();
        }
    }

    @Override
    public void replace(T newElement) {
        if(isEmpty()) return;
        else if(newElement==null) return;
        else{
            listArray[curr] = newElement;
        }
    }

    @Override
    public void clear() {
        length = 0;
        curr = -1;
    }

    @Override
    public boolean isEmpty() {
        return length==0;
    }

    @Override
    public boolean isFull() {
        return length==capacity;
    }

    @Override
    public boolean gotoBeginning() {
        if (!isEmpty()) {
            curr = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoEnd() {
        if(!isEmpty()) {
            curr = length - 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoNext() {
        if(isEmpty() || curr>=length - 1) return false;
        curr ++;
        return true;
    }

    @Override
    public boolean gotoPrev() {
        if (!isEmpty() && curr > 0) {
            curr--;
            return true;
        }
        return false;
    }

    @Override
    public T getCursor() {
        if(!isEmpty()) {
            T currElem = (T) listArray[curr];
            return currElem;
        }
        return null;
    }

    @Override
    public void showStructure(PrintWriter pw) {
        if(isEmpty()) pw.println("Empty list"+" {capacity = 512, length = "+length+", Cursor = " + curr+"}");//要用pw输出，而非System输出
        else{
            for (int i = 0; i < length; i++) {
                    pw.print(listArray[i]+" ");
            }
            pw.println("{capacity = " + capacity + ", length = " + length + ",cursor = " + curr + "}");
        }
    }

    public void showLengthDCapacity(PrintWriter pw) {
        pw.print((double)length/capacity + ",");
    }

    @Override
    public void moveToNth(int n) {
        if(length <= n){
            System.out.println("moveToNth: List should contain at least n + 1 elements.");
            return;
        }
        T temp = listArray[curr];
        if(curr <= n){
            for(int i=curr;i < n;i++){
                listArray[i] = listArray[i+1];
            }
        }else {
            for(int i=n;i < curr;i++){
                listArray[i+1] = listArray[i];
            }
        }
        listArray[n] = temp;
        curr = n;
    }

    @Override
    public boolean find(T searchElement) {
        while(true){
            if(listArray[curr].equals(searchElement)){
                return true;
            }
            if(curr==length-1){
                return false;
            }
            curr++;
        }
    }
}
