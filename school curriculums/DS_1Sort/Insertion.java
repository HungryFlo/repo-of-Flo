
public class Insertion extends SortAlgorithm {
    public void sort(Comparable[] objs) {
        int N = objs.length;
        insertionSort(objs,0,N-1);
    }
    public void insertionSort(Comparable[] objs, int low, int high){
        for (int i = 1; i < high - low + 1; i++) {
            for (int j = i; j > 0 && less(objs[j], objs[j - 1]); j--)
                exchange(objs, j, j - 1);
        }
    }
}