public class quickSortTriple  extends SortAlgorithm {
    public void sort(Comparable[] objs) {
        int N = objs.length;
        quickSortTriple_rightPivot(objs, 0, N - 1);
    }

    private Comparable choosePivot(Comparable[] objs, int low, int high){
        int mid = low + (high - low) / 2;
        Comparable pivot = objs[high];
        if (less(objs[low], objs[high]) == less(objs[mid], objs[low])) { //==还可以用于逻辑判断！！
            pivot = objs[low];
            exchange(objs, low, high);
        } else if (less(objs[mid], objs[low]) == less(objs[high], objs[mid])) {
            pivot = objs[mid];
            exchange(objs, mid, high);
        }
        return pivot;
    }

    //下面两段代码的三路划分方法是在同向双指针的快排基础上优化的，采用的思想其实相同，
    //都是将数组分段，将被扫描到的元素通过与分段的“栈顶”相邻元素交换的方法来完成。


    //如果轴值是放到右边，那么代码的实现是这样的：
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
        quickSortTriple_rightPivot(objs,left,lt-1);
        quickSortTriple_rightPivot(objs,gt+2,right);
    }


    private void quickSortTriple_leftPivot(Comparable[] objs, int left, int right) {
        if (right <= left)
            return;
        //三路快排时需要返回两个下标来确定划分的位置，为方便两个值的返回，在此我们不再将partition作为单独的方法，而是直接嵌入到排序函数中来
        //这里按照视频的演示，代码采用的是将轴值的位置交换到左边的情形。
        Comparable pivot = choosePivot(objs,left,right);

        int lt = left + 1;//lt: less than
        int gt = right;//gt: greater than
        //划分的规定：
        //all in objs[left + 1..lt) < pivot
        //all in objs[lt..i) == pivot
        //all in objs(gt..right] > pivot
        int i = left + 1;
        //判断这三个变量的初始化是否合理：
        //初始时第一、二、三个区间均为空区间，代表我们还没有确定任何元素的位置，合理！
        //判断初始时是否为空区间的方法可以很有效地检查变量的初始化值。
        //此处判断循环条件的方法：当三部分连起来的时候，整个数组就被扫描过一遍了，循环就可以停止了。
        //此处看的是i和gt的关系。当i==gt时，由于第二、三个区间都是半开的，i和gt这个值就没有被扫描到，所以在i==gt时要执行一次代码。
        while(i <= gt){
            if(less(objs[i],pivot)) {
                exchange(objs,i,lt);
                lt++;
                i++;
            } else if (objs[i].equals(pivot)) {
                i++;
            } else {
                exchange(objs,i,gt);
                gt--;
                //这里不可以让i--，因为从后面换过来的这个元素并未经过大小判断
                //这也是这里使用while循环而不使用for循环的原因：在这种情况下i是不应该++的
            }
        }
        //到这里，我们就已经把轴值后面的所有元素按照先前的规定划分好了
        //此时，我们应该让正处于最左端的轴值放到中间区域去
        //根据之前的规定，这时应该让小于轴值的最后一个值和轴值交换位置
        exchange(objs, left, lt - 1);
        //照理说，这时我们应该把lt前移一位的，但是因为后面影响不大，就算了。
        quickSortTriple_leftPivot(objs,left,lt - 2);//但是要注意这里就是lt-2了，不是lt-1.
        quickSortTriple_leftPivot(objs,gt + 1, right);
    }
}