//public class QuickTriple extends SortAlgorithm {
//    public void sort(Comparable[] objs) {
//        int N = objs.length;
//        quickTriple(objs, 0, N - 1);
//    }
//
//    public static void main(String[] args){
//        Double[] testData = GenerateData.getRandomData(5);
//        Double[] test = new Double[6];
//        for (int i = 0; i < 5; i++) {
//            test[i] = testData[i];
//            System.out.print(test[i] + " ");
//        }
//        System.out.println();
//        SortAlgorithm alg = new Quick();
//        alg.sort(testData);
//        for (int i = 0; i < 5; i++) {
//            System.out.print(testData[i] + " ");
//        }
//        System.out.println(test.equals(testData));
//        System.out.println(alg.isSorted(testData));
//    }
//
////    public void quickTriple(Comparable[] objs, int p, int r) {
////        if (p >= r)
////            return;
////
////        // 在数组大小小于7的情况下使用快速排序
////        if (r - p + 1 < 7) {
////            for (int i = p; i <= r; i++) {
////                for (int j = i; j > p && x[j - 1] > x[j]; j--) {
////                    swap(x, j, j - 1);
////                }
////            }
////            return;
////        }
////
////        int len = r - p + 1;
////        int m = p + (len >> 1);
////        if (len > 7) {
////            int l = p;
////            int n = p + len - 1;
////            if (len > 40) {
////                int s = len / 8;
////                l = med3(x, l, l + s, l + 2 * s);
////                m = med3(x, m - s, m, m + s);
////                n = med3(x, n - 2 * s, n - s, n);
////            }
////            m = med3(x, l, m, n);
////        }
////
////        int v = x[m];
////
////        // a,b进行左端扫描，c,d进行右端扫描
////        int a = p, b = a, c = p + len - 1, d = c;
////        while (true) {
////            // 尝试找到大于pivot的元素
////            while (b <= c && x[b] <= v) {
////                // 与pivot相同的交换到左端
////                if (x[b] == v)
////                    swap(x, a++, b);
////                b++;
////            }
////            // 尝试找到小于pivot的元素
////            while (c >= b && x[c] >= v) {
////                // 与pivot相同的交换到右端
////                if (x[c] == v)
////                    swap(x, c, d--);
////                c--;
////            }
////            if (b > c)
////                break;
////            // 交换找到的元素
////            exchange(objs, b++, c--);
////        }
////
////        // 将相同的元素交换到中间
////        int s, n = p + len;
////        s = Math.min(a - p, b - a);
////        vecswap(x, p, b - s, s);
////        s = Math.min(d - c, n - d - 1);
////        vecswap(x, b, n - s, s);
////
////        // 递归调用子序列
////        if ((s = b - a) > 1)
////            qsort7(x, p, s + p - 1);
////        if ((s = d - c) > 1)
////            qsort7(x, n - s, n - 1);
////
////    }
////
////    private void vecswap(Comparable[] objs, int a, int b, int n) {
////        for (int i = 0; i < n; i++, a++, b++)
////            exchange(objs, a, b);
////    }
//
//    public void quickTriple(Comparable[] objs, int low, int high){
//        //基准情形
//        if (high - low <= 20) {
//            Insertion ist = new Insertion();
//            ist.insertionSort(objs, low, high);
//            return;
//        }
//        //选择轴值
//        int mid = low + (high - low) / 2;
//        Comparable pivot = objs[high];
//        if (!(less(objs[low], objs[high]) ^ less(objs[mid], objs[low]))) {
//            pivot = objs[low];
//            exchange(objs, low, high);
//        } else if (!(less(objs[mid], objs[low]) ^ less(objs[high], objs[mid]))) {
//            pivot = objs[mid];
//            exchange(objs, mid, high);
//        }
//        //四指针法的操作和递归
//        int left = low;
//        int cleft =
//        int right = high - 1;
//        while (true) {
//            while (less(objs[left], pivot)) {
//                left++;
//            }
//            while (less(pivot, objs[right]) && right > 0) {
//                right--;
//            }
//            if (left < right) {
//                exchange(objs, left, right);
//            } else {
//                exchange(objs, left, high);
//                if (left > 1) {
//                    quickTriple(objs, low, left - 1);
//                }
//                quickTriple(objs, left + 1, high);
//                return; //一定不要忘记这个return，否则会一直循环退不出去！
//            }
//        }
//    }
//}
