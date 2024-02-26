import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;

public class AList<T> implements List<T>{
    private static final int defaultSize = 512;
    private int msize;
    private int numInList;
    private int curr;
    private T[] listArray;

    //构造函数
    AList(){
        setup(defaultSize);
    }
    //单独设置setup函数，供构造函数之外的地方进行调用
    private void setup(int size){
        msize = size;
        numInList = 0;
        curr = -1;
        listArray = (T[]) new Object[size];
    }
    @Override
    public void insert(T newElement) throws ListException {
        if (isFull()) throw new ListException();
        if(isEmpty()){
            listArray[0] = newElement;
            curr = 0;
        }else {
            for (int i = numInList - 1; i > curr; i--) {
                listArray[i + 1] = listArray[i];
            }
            listArray[curr + 1] = (T) newElement;
            curr++;
        }
        //插入后要将光标右移
        numInList ++;
    }

    @Override
    public void remove() {
        if(isEmpty()) return;
        if(curr==numInList-1){
            curr = 0;
            numInList --;
            return;
        }
        for(int i=curr; i<numInList-1; i++){
            listArray[i] = listArray[i+1];
        }
        numInList--;
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
        numInList = 0;
        curr = -1;
    }

    @Override
    public boolean isEmpty() {
        return numInList==0;
    }

    @Override
    public boolean isFull() {
        return numInList==msize;
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
            curr = numInList - 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoNext() {
        if(isEmpty() || curr>=numInList - 1) return false;
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
        if(isEmpty()) pw.println("Empty list"+" {capacity = 512, length = "+numInList+", cursor = " + "-1}");//要用pw输出，而非System输出
        else{
            for (int i = 0; i < numInList; i++) {
                    pw.print(listArray[i]+" ");
            }
            pw.println("{capacity = " + msize + ", length = " + numInList + ", cursor = " + curr + "}");
        }
    }

    public void showLengthDCapacity(PrintWriter pw) {
        pw.print((double) numInList/msize + ",");
    }

    @Override
    public void moveToNth(int n) {
        if(numInList <= n){
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
            if(curr==numInList-1){
                return false;
            }
            curr++;
        }
    }
}
