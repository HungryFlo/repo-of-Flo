public class Quicksort1_1 extends SortAlgorithm{
    private static final int INSERTION_SORT_THRESHOLD = 10; // 适当调整阈值

    public void sort(Comparable[] objs) {
        sort(objs, 0, objs.length - 1);
    }

    private void sort(Comparable[] objs, int lo, int hi) {
        if (hi - lo + 1 <= INSERTION_SORT_THRESHOLD) {
            // 当数据量小于等于阈值时，调用直接插入排序
            insertionSort(objs, lo, hi);
            return;
        }

        int partitionIndex = partition(objs, lo, hi);
        sort(objs, lo, partitionIndex - 1);
        sort(objs, partitionIndex + 1, hi);
    }

    private int partition(Comparable[] objs, int lo, int hi) {
        // 随机选择枢纽元素
        int randomIndex = lo + (int) (Math.random() * (hi - lo + 1));
        exchange(objs, lo, randomIndex);

        Comparable pivot = objs[lo];
        int i = lo + 1;
        int j = hi;

        while (true) {
            while (i <= j && less(objs[i], pivot)) {
                i++;
            }
            while (j >= i && less(pivot, objs[j])) {
                j--;
            }
            if (i > j) {
                break;
            }
            exchange(objs, i, j);
            i++;
            j--;
        }

        exchange(objs, lo, j);
        return j;
    }

    private void insertionSort(Comparable[] objs, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            Comparable key = objs[i];
            int j = i - 1;
            while (j >= lo && less(key, objs[j])) {
                objs[j + 1] = objs[j];
                j--;
            }
            objs[j + 1] = key;
        }
    }
}

