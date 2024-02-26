public class QuickSort extends SortAlgorithm{
    public void sort(Comparable[] objs) {
        int N = objs.length;
        quick(objs, 0, N - 1);
    }
    private Comparable choosePivot(Comparable[] objs, int low, int high){
        int mid = low + (high - low) / 2;
        Comparable pivot = objs[high];
        if (!(less(objs[low], objs[high]) ^ less(objs[mid], objs[low]))) {
            pivot = objs[low];
            exchange(objs, low, high);
        } else if (!(less(objs[mid], objs[low]) ^ less(objs[high], objs[mid]))) {
            pivot = objs[mid];
            exchange(objs, mid, high);
        }
        return pivot;
    }

    private int partition(Comparable[] objs, int left, int right){
        Comparable pivot = choosePivot(objs,left,right);
        //循环不变量：
        //[left..i) < pivot
        //(j..right-1] > pivot
        int i = left;
        int j = right-1;
        while(i <= j){
            while(i<=j && less(objs[i],pivot)){ // i找大于等于轴值的值，因此在小于轴值时要向右移动
                i++;
            }
            while(i<=j && less(pivot,objs[j])){ // j找小于等于轴值的值，因此在大于轴值时要向左移动
                j--;
            }
            if(i<=j){
                exchange(objs,i,j);
                i++;
                j--;
            }
        }
        exchange(objs,i,right);
        return i;
    }

    public void quick(Comparable[] objs, int low, int high){
        //基准情形
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(objs, low, high);
        quick(objs, low, pivotIndex-1);
        quick(objs, pivotIndex+1, high);
    }
}
