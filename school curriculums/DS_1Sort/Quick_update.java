public class Quick_update extends SortAlgorithm {
    public void sort(Comparable[] objs) {
        int N = objs.length;
        quickSort_U(objs, 0, N - 1);
    }

    public static void main(String[] args){
        Double[] testData = GenerateData.getRandomData(5);
        Double[] test = new Double[6];
        for (int i = 0; i < 5; i++) {
            test[i] = testData[i];
            System.out.print(test[i] + " ");
        }
        System.out.println();
        SortAlgorithm alg = new Quick_update();
        alg.sort(testData);
        for (int i = 0; i < 5; i++) {
            System.out.print(testData[i] + " ");
        }
        System.out.println(test.equals(testData));
        System.out.println(alg.isSorted(testData));
    }

    /**
     * 当数组长度小于20时用插入排序完成最终排序
     * @param objs
     * @param low
     * @param high
     */
    public void quickSort_U(Comparable[] objs, int low, int high){
        //基准情形
        if (high - low <= 10) {
            Insertion ist = new Insertion();
            ist.insertionSort(objs, low, high);
            return;
        }
        int pivotIndex = partition(objs, low, high);
        quickSort_U(objs, low, pivotIndex-1);
        quickSort_U(objs, pivotIndex+1, high);
    }
    public int partition_2(Comparable[] objs, int low, int high){
        Comparable pivot = choosePivot(objs, low, high);
        int left = low;
        int right = high - 1;
        while(left < right){
            //其实只要让left指针和right指针碰面即可，就算交错了也必定会是交错到对方已经排过的领域的第一个值.
            //下面的两个循环的顺序是有讲究的，left先动，最后指向的值是第一个大于轴值的值；right先动，最后指向的是最后一个小于轴值的值。
            while(left < right && less(objs[left], pivot)){ //这两个循环中的left<right都是有必要的！
                left++;
            }
            while(left < right && less(pivot, objs[right])){ //这两个循环中的比较都是严格小于，这样做可以使排序后等于pivot的元素平均划分到两个区域中，为下一次划分奠定基础
                right--;
            }
            exchange(objs,left,right);
        }
        exchange(objs,left,high);
        return left;
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
}