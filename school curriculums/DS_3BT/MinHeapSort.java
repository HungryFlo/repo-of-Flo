import java.util.Scanner;

public class MinHeapSort {
    public static void main(String[] args) {
        int[] test = {9,8,2,3,4,5,1,0};
        Elem[] t = new Elem[test.length];
        for (int i = 0; i < test.length; i++) {
            t[i] = new Elem(test[i],null);
        }
        t = new MinHeapSort().sort3(t);
        for (Elem e :
                t) {
            System.out.println(e);
        }
    }

    /**
     * Sort with Min_Heap
     * The origin array won't be sorted,
     * so you should use like "array = sort(array);"
     * @param array list of elements
     * @return sorted array
     */
    public Elem[] sort(Elem[] array) {
        int num = array.length;
        Elem[] res = new Elem[num];
        Min_Heap mh = new Min_Heap(array, num, num+1);
        for (int i = 0; i < num; i++) {
            res[i] = mh.removemin();
        }
        return res;
    }

    public Elem[] sort3(Elem[] array) {
        int num = array.length;
        Elem[] res = new Elem[num];
        Min_TripleHeap mh = new Min_TripleHeap(array, num, num+1);
        for (int i = 0; i < num; i++) {
            res[i] = mh.removemin();
        }
        return res;
    }
}
