public class Merge extends SortAlgorithm {
    public void sort(Comparable[] objs) {
        int N = objs.length;
        mergeSort(objs,0,N-1);
    }

    public void mergeSort(Comparable[] objs, int low, int high){
        if(low == high){
            return;
        }
        int mid = low + (high - low) / 2;
        mergeSort(objs, low, mid);
        mergeSort(objs, mid + 1, high);
        merge(objs, low, mid + 1, high);
    }

    private void merge(Comparable[] objs, int start, int start2, int end){
        int len1 = start2 - start;
        Comparable[] tmp = new Comparable[len1];
        for (int i = 0; i < len1; i++) {
            tmp[i] = objs[i];
        }

        int p1 = 0;
        int p2 = start2;
        for(int i = start; i <= end; i++){
            if(less(tmp[p1],objs[p2])){
                objs[i] = tmp[p1];
                p1++;
                if(p1 == len1){
                    break;
                }
            }else{
                objs[i] = objs[p2];
                p2++;
                if(p2>end){
                    while(p1<len1){
                        i++;
                        objs[i] = tmp[p1];
                        p1++;
                    }
                }
            }
        }
    }

    /**
     * 对objs[left..right]进行归并排序（公用temp数组版本）
     *
     * @param objs
     * @param left
     * @param right
     * @param temp
     */
    public void mergeSort(Comparable[] objs, int left, int right, Comparable[] temp){
        if(right - left < 16){ //取THRESHOLD=16
            Insertion insertionSort = new Insertion();
            insertionSort.insertionSort(objs,left,right);
            return;
        }
        int mid = (left + right) / 2;
        mergeSort(objs, left, mid, temp);
        mergeSort(objs, mid + 1, right, temp);

        if (!less(objs[mid+1],objs[mid])){
            return;
        }
        //合并两个有序区间
        for (int i = left; i <= right; i++) {
            temp[i] = objs[i];
        }
        //这里给出了一种在递归函数中让某个内容“不递归”的方法：作为函数的参数传进去。
        //比如这里的temp数组，是一个全局使用的数组，我们就可以不在函数中创建它，而是在调用处创建之后作为参数传入。
        //数组全局使用有两个好处：
        //1. 避免了将元素复制时产生的下标偏移问题，在调用时，取哪块用哪块
        //2. 如果每一次归并都创建一个新的数组，此时创建和回收数组的性能开销会比较大
        int i = left;
        int j = mid + 1;
        for (int k = left; k <= right; k++) {
            if(i == mid+1){
                objs[k] = temp[j];
                j++;
            } else if (j == right + 1) {
                objs[k] = temp[i];
            } else if (less(temp[j],temp[i])){//此处应考虑归并排序算法的稳定性，要使得两元素相等时i优先放入。
                objs[k] = temp[j];
                j++;
            }else{
                objs[k] = temp[i];
                i++;
            }
        }
    }

    //归并排序的优化1：在小区间内使用插入排序
    //归并排序的优化2：在归并两个有序数组之前检查是否满足“左最大>=右最小”

    /*关于分治算法：
        分解、解决、合并
        应用：归并排序、快速排序、树、回溯算法、动态规划（记忆化递归）
     */


    public static void main(String[] args){
        Double[] testData = GenerateData.getRandomData(5);
        Double[] test = new Double[6];
        for (int i = 0; i < 5; i++) {
            test[i] = testData[i];
        }
        SortAlgorithm alg = new Merge();
        alg.sort(testData);
        System.out.println(test.equals(testData));
        System.out.println(alg.isSorted(testData));
    }
}

//    public void mergeSort(Comparable[] objs, int low, int high){
//        if(high == low) return;
//        int mid = low + (high - low) / 2;
//        if(high > low) {
//            mergeSort(objs,low,mid);
//            mergeSort(objs,mid+1,high);
//        }
//        int N = high - low + 1;
//        Comparable[] tep = new Comparable[N];
//        for (int i = 0; i < N; i++) {
//            tep[i] = objs[i];
//        }
//        int p1 = low;
//        int p2 = mid + 1;
//        for (int i = 0; i < N; i++) {
//            if(less(tep[p2],tep[p1])){
//                objs[i] = tep[p2];
//                p2 ++;
//                if(p2 >= N){
//                    for (int j = p1; j < mid+1; i++,j++) {
//                        i++;
//                        objs[i] = tep[j];
//                    }
//                    return;
//                }
//            }else{
//                objs[i] = tep[p1];
//                p1 ++;
//                if(p1 > mid){
//                    for (int j = p2; j < N; i++,j++) {
//                        i++;
//                        objs[i] = tep[j];
//                    }
//                    return;
//                }
//            }
//        }
//        //在写代码前就要想清楚结果存放在哪里，这对递归调用有什么影响。
//        //对于递归调用的设计还需要加强。一些细节就决定了递归能否优雅实现。
//    }