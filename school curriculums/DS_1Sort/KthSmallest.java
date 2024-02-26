public class KthSmallest {
    public static void main(String[] args){
        Double[] testData = GenerateData.getRandomData(100);
        for (int i = 0; i < 10; i++) {
            System.out.print(testData[i] + " ");
        }
        KthSmallest ks = new KthSmallest();
        System.out.println();
        System.out.println("4小的元素为：" + ks.kthsmallest_triple(testData, 4,0,99));
        System.out.println("4小的元素为：" + ks.kthsmallest_double(testData, 4));
    }
    /**
     * 基于双路划分的寻找第k小元素算法
     * @param objs
     * @param k
     * @return
     */
    public Comparable kthsmallest_double(Comparable[] objs, int k){
        int len = objs.length;
        int target = k - 1; //为了防止出错，先把k转化为目标下标值，再继续之后的操作，减少了边界值确定上的麻烦。
        int left = 0;
        int right = len - 1;
        while(true){
            int pivotIndex = partition(objs, left, right);
            if(pivotIndex == target){
                return objs[pivotIndex];
            } else if (pivotIndex < target) {
                left = pivotIndex + 1;
            }else {
                right = pivotIndex - 1;
            }
        }
    }

    /**
     * 基于三路划分的寻找第k小元素算法
     * @param objs
     * @param k
     * @param left
     * @param right
     * @return kthsmallest
     */
    public Comparable kthsmallest_triple(Comparable[] objs, int k, int left, int right){
        /*
        算法：
        基准情形：数组为空或只有一个元素
        1. 一个元素：return
        2. 两个元素：如果k==1，返回小的；如果k==2，返回大的
        递归算法：
        1. 三路划分
        2. 比较左边界与k的大小
            若k-1<=左边界左侧的元素个数，则对左侧进行递归操作（需要传入：数组，左右边界，k）
            若k-1>左边界左侧元素个数且k<右边界左侧元素个数，则返回轴值
            若k-1>=右边界左侧元素个数，则对右侧进行递归操作（此时k应传入k-右边界）
            这里的k可能需要-1，因为人是从1开始数数的，数组是从0开始的下标
            一定要注意，左边界是会浮动的，所以一定要分清楚是在判断边界左右的元素个数还是在判断边界本身
        经验：
        当遇到边界值确定confusing的问题时，要尝试多设立几个变量值，将目标一步一步转化为可以直观判断的值
        通过添加输出语句来调试比一步一步做人肉计算机快得多！
         */
        // base case
        if ( right - left <= 1 ){
            if ( left == right ){
                return objs[left];
            } else {
                if (less(objs[right],objs[left])){
                    exchange(objs, left, right);
                }
                if ( k==1 ){
                    return objs[left];
                } else {
                    return objs[right];
                }
            }
        }
        Comparable pivot = choosePivot(objs,left,right);
        //循环不变量
        //[left..lt) < pivot
        //[lt..i) == pivot
        //(gt..right-1] >pivot
        int lt = left;
        int gt = right - 1;
        int i = left;

        while(i <= gt){
            if(less(objs[i],pivot)){
                exchange(objs, lt, i);
                lt++;
                i++;
            } else if (less(pivot, objs[i])) {
                exchange(objs, gt, i);
                gt--;
            } else {
                i++;
            }
        }
        //轴值和第一个比轴值大的元素交换
        exchange(objs,right, gt+1);
        // 每轮排序完成后，[left, lt-1] < pivot, [gt+2, right] > pivot

        if (k-1 <= lt-left-1){
            return kthsmallest_triple(objs,k,left,lt-1);
        } else if (k-1 > lt-left-1 && k-1 < gt-left+2) {
            return pivot;
        } else {
            return kthsmallest_triple(objs, k-gt-2, gt+2, right);
        }
    }

    /**
     * 通过将重复元素均分进行优化后的双路快排划分函数，返回值为最终轴值所在的位置
     * @param objs
     * @param left
     * @param right
     * @return
     */
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

    /**
     * 初始时将轴值位置置于右侧的三路划分快速排序（未将划分步骤独立成partition函数）
     * @param objs
     * @param left
     * @param right
     */
    private void quickSortTriple_rightPivot(Comparable[] objs, int left, int right){
        if(left <= right){
            return;
        }

        Comparable pivot = choosePivot(objs,left,right);
        //循环不变量
        //[left..lt) < pivot
        //[lt..i) == pivot
        //(gt..right-1] >pivot
        int lt = left;
        int gt = right - 1;
        int i = left;

        while(i <= gt){
            if(less(objs[i],pivot)){
                exchange(objs, lt, i);
                lt++;
                i++;
            } else if (less(pivot, objs[i])) {
                exchange(objs, gt, i);
                gt--;
            } else {
                i++;
            }
        }
        //轴值和第一个比轴值大的元素交换
        exchange(objs,right, gt+1);
        // 每轮排序完成后，[left, lt-1] < pivot, [gt+2, right] > pivot
        quickSortTriple_rightPivot(objs,left,lt-1);
        quickSortTriple_rightPivot(objs,gt+2,right);
    }

    protected void exchange(Comparable[] numbers, int i, int j){
        Comparable temp;
        temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }
    protected boolean less(Comparable one, Comparable other){
        return one.compareTo(other) < 0;
    }

    private Comparable choosePivot(Comparable[] objs, int low, int high){
        int mid = low + (high - low) / 2;
        Comparable pivot = objs[high];
        if (less(objs[low], objs[high]) == less(objs[mid], objs[low])) {
            pivot = objs[low];
            exchange(objs, low, high);
        } else if (less(objs[mid], objs[low]) == less(objs[high], objs[mid])) {
            pivot = objs[mid];
            exchange(objs, mid, high);
        }
        return pivot;
    }

    private void show(Comparable[] objs){
        for (Comparable obj :
                objs) {
            System.out.print(obj + " ");
        }
    }

}
