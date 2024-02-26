package maze;

public class Min_Heap {
    private KruskalElem[] Heap;
    private int size;
    private int n;

    public Min_Heap(KruskalElem[] h, int num, int max) {
        Heap = h;
        n = num;
        size = max;
        buildheap();
    }

    public int heapsize() {
        return n;
    }

    public boolean isLeaf(int pos) {
        return (pos >= n/2) && (pos < n);
    }

    public int leftchild(int pos) {
        if(pos <= n/2) {
            return 2 * pos + 1;
        } else {
            System.out.println("Position has no left child!");
            return -1;
        }
    }

    public int rightchild(int pos) {
        if(pos >= n/2) {
            return 2 * pos + 2;
        } else {
            System.out.println("Position has no right child!");
            return -1;
        }
    }

    public int parent(int pos) {
        if(pos >= 0) {
            return (pos-1) / 2;
        } else {
            System.out.println("Position has no parent.");
            return -1;
        }
    }

    public void buildheap() {
        for(int i = n/2 -1; i >= 0; i--) {
            siftdown(i);
        }
    }

    private void siftdown(int pos) {
        if (pos<0 || pos>=n) {
            System.out.println("Illegal heap position!");
            return;
        }
        while (!isLeaf(pos)) {
            int j = leftchild(pos);
            if (j < (n-1) && (Heap[j].getKey() > Heap[j+1].getKey())) {
                j ++;
            }
            if (Heap[pos].getKey() <= Heap[j].getKey()) return;
            KruskalElem temp = Heap[pos];
            Heap[pos] = Heap[j];
            Heap[j] = temp;
            pos = j;
        }
    }

    public void insert(KruskalElem val) {
        if (n>=size) {
            System.out.println("Heap is full!");
            return;
        }
        int curr = n++;
        Heap[curr] = val;
        //sift up
        while ((curr!=0) && (Heap[curr].getKey()<Heap[parent(curr)].getKey())) {
            KruskalElem temp = Heap[curr];
            Heap[curr] = Heap[parent(curr)];
            Heap[parent(curr)] = temp;
            curr = parent(curr);
        }
    }

    public KruskalElem removemin() {
        if (n<=0) {
            System.out.println("Remove from empty heap!");
            return null;
        }
        KruskalElem temp = Heap[0];
        Heap[0] = Heap[n-1];
        Heap[n-1] = temp;
        n--;
        if (n!=0) siftdown(0);
        return Heap[n];
    }

    public KruskalElem remove(int pos) {
        if (pos<0 || pos>=n) {
            System.out.println("Illegal heap position!");
            return null;
        }
        n--;
        KruskalElem temp = Heap[pos];
        Heap[pos] = Heap[n];
        Heap[n] = temp;
        if (n!=0) siftdown(pos);
        return Heap[n];
    }

    public KruskalElem getMin() {
        return Heap[0];
    }
}