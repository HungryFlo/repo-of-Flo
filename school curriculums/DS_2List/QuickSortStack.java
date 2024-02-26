import 准备工作.LStack;
public class QuickSortStack extends SortAlgorithm{

    //在主函数中写一些用于测试的代码
    public static void main(String[] args){
        int[] testList = {1,2,3,4,5,6,7,7,6,5,4,3,2,1};
        Integer[] test = new Integer[14];
        for (int i = 0; i < 14; i++) {
            test[i] = (Integer) testList[i];
        }
        QuickSortStack qss = new QuickSortStack();
        qss.quick(test, 0, test.length-1);
        for (Integer i :
                test) {
            System.out.print(i + " ");
        }
    }
    //对quick进行的封装，使参数表更简洁
    public void sort(Comparable[] objs) {
        int N = objs.length;
        quick(objs, 0, N - 1);
    }
    //内部类QuickSortOBJ,是一个存储工作块信息的数据类型
    public class QuickSortOBJ {
        private Comparable[] objs;
        private int low;
        private int high;
        private int operationType;
        //构造函数
        public QuickSortOBJ(Comparable[] objs, int low, int high, int operationType){
            this.low = low;
            this.objs = objs;
            this.high = high;
            this.operationType = operationType;
            //0:quicksort
            //1:partition
        }
    }
    //快速排序的主干代码
    public void quick(Comparable[] objs, int low, int high){
        LStack QSLStack = new LStack();
        QuickSortOBJ currTask = new QuickSortOBJ(objs,low,high,0);
        QSLStack.push(currTask);
        //这些之前每轮都要迭代的变量应该放到循环外面进行声明
        int pivotIndex = low;
        while(!QSLStack.isEmpty()){
            currTask = (QuickSortOBJ) QSLStack.pop();
            if (currTask.low >= currTask.high) {
                continue;//这里不应该return，而应该continue!
            }
            if(currTask.operationType == 1){
                pivotIndex = partition(currTask.objs, currTask.low, currTask.high);
            } else {
                QSLStack.push(new QuickSortOBJ(currTask.objs, pivotIndex+1, currTask.high, 0));
                QSLStack.push(new QuickSortOBJ(currTask.objs, currTask.low, pivotIndex-1, 0));
                QSLStack.push(new QuickSortOBJ(currTask.objs, currTask.low, currTask.high, 1));
            }
        }
    }

    //沿用了上一次实验的轴值选择方法：三值选中法
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

    //双路划分
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
