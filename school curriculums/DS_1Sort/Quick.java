public class Quick extends SortAlgorithm {
    public void sort(Comparable[] objs) {
        int N = objs.length;
        quick(objs, 0, N - 1);
    }

    public static void main(String[] args){
        Double[] testData = GenerateData.getRandomData(5);
        Double[] test = new Double[6];
        for (int i = 0; i < 5; i++) {
            test[i] = testData[i];
            System.out.print(test[i] + " ");
        }
        System.out.println();
        SortAlgorithm alg = new Quick();
        alg.sort(testData);
        for (int i = 0; i < 5; i++) {
            System.out.print(testData[i] + " ");
        }
        System.out.println(test.equals(testData));
        System.out.println(alg.isSorted(testData));
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

    /**
     * 老师上课讲的快速排序方法
     * @param objs
     * @param low
     * @param high
     */
    public void quickSort(Comparable[] objs, int low, int high) {
        //基准情形
        if (low >= high) {
            return;
        }
        //选择轴值
        Comparable pivot = choosePivot(objs, low, high);
        //相向双指针法的操作和递归
        int left = low;
        int right = high - 1;
        while (true) {
            while (less(objs[left], pivot)) {
                left++;
            }
            while (less(pivot, objs[right]) && right > 0) {
                right--;
            }
            if (left < right) {
                exchange(objs, left, right);
            } else {
                exchange(objs, left, high);
                if (left > 1) {
                    quickSort(objs, low, left - 1);
                }
                quickSort(objs, left + 1, high);
                return; //一定不要忘记这个return，否则会一直循环退不出去！
            }
        }
    }

    /**
     * 将partition函数分离出来的快速排序算法
     * @param objs
     * @param low
     * @param high
     */
    public void quick(Comparable[] objs, int low, int high){
        //基准情形
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(objs, low, high);
        quick(objs, low, pivotIndex-1);
        quick(objs, pivotIndex+1, high);
    }

    /**
     * 单路划分（两指针同向运动）
     * @param objs
     * @param low
     * @param high
     * @return
     */
    private int partition_1(Comparable[] objs, int low, int high){
        Comparable pivot = choosePivot(objs, low, high);
        int i = low; //i相当于是一个“栈”的top指针，指向目前已扫描到的最后一个比轴值小的元素的下一个位置。
        int j = low; //j往右遍历搜索比轴值小的元素，找到后就放到i左边的那个“栈”里去
        //刚开始时i和j在同一个位置上
        while(j < high){
            if(less(objs[j],pivot)){ //j找到了比轴值小的元素
                if(i != j){
                    exchange(objs,i,j);
                }
                //当一个比轴值小的元素放到左边的“栈”中时，i就会向后移动一位。
                i++;
            }
            j++;
        }
        //此时i指向的是比轴值小的元素的后一个元素（也就是第一个比轴值大的元素）
        exchange(objs,i,high);
        //将i指向的值和轴值交换位置，完成划分。
        return i;
        //返回轴值最后所在的位置。
    }

    /**
     * 普通双路划分（两指针相向运动）
     * @param objs
     * @param low
     * @param high
     * @return
     */
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