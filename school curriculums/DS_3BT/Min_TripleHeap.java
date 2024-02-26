public class Min_TripleHeap {
    private Elem[] Heap;
    private int size;
    private int n;

    public Min_TripleHeap(Elem[] h, int num, int max) {
        Heap = h;
        n = num;
        size = max;
        buildheap();
    }

    public int heapsize() {
        return n;
    }

    public boolean isLeaf(int pos) {
        return ((3 * pos + 1) >= n) && (pos < n);
    }
    // 第一个叶子结点的下标为 3 * pos + 1 == n n+1 n+2
    // pos == (n-1) / 3.0
    // pos == n / 3.0
    // pos == (n+1) / 3.0
    // 上面三个数中只可能有一个整数，那个整数就是 pos 的值
    // 所以可以写成 pos == (n+1) / 3
    // 也即，pos >= (n+1) / 3 时为叶子结点
    // 所以最后一个非叶子结点为 pos == (n+1) / 3 - 1

    public int heapheight() {
        return (int) Math.ceil(Math.log(n + 1) / Math.log(3)) - 1;
    }

    public int leftchild(int pos) {
        if(!isLeaf(pos)) {
            return 3 * pos + 1;
        } else {
            System.out.println("Position has no left child!");
            return -1;
        }
    }

    public int middlechild(int pos) {
        if((3 * pos + 2) < n) {
            return 3 * pos + 2;
        } else {
            System.out.println("Position has no middle child!");
            return -1;
        }
    }

    public int rightchild(int pos) {
        if((3 * pos + 3) < n) {
            return 3 * pos + 3;
        } else {
            System.out.println("Position has no right child!");
            return -1;
        }
    }

    public int parent(int pos) {
        if(pos >= 0) {
            return ((n - 1) / 3);
        } else {
            System.out.println("Position has no parent.");
            return -1;
        }
    }

    public void buildheap() {
        for(int i = (n + 1) / 3 - 1; i >= 0; i--) {
            siftdown(i);
        }
    }

    private int findMinChild(int pos) {
        if (isLeaf(pos)) return -1; // 这里其实有点重复了
        int l = leftchild(pos);
        int m = middlechild(pos);
        int r = rightchild(pos);
        int res = l;
        if (r != -1) { // 若有三个子节点
            if (Heap[res].getKey() > Heap[m].getKey()) res = m;
            if (Heap[res].getKey() > Heap[r].getKey()) res = r;
        } else if (m != -1) { // 若有两个子节点
            if (Heap[res].getKey() > Heap[m].getKey()) res = m;
        }
        return res;
    }

    private void siftdown(int pos) {
        if (pos<0 || pos>=n) {
            System.out.println("Illegal heap position!");
            return;
        }
        while (!isLeaf(pos)) {
            // 选择子节点中的最小值
            int j = findMinChild(pos);
            // 与根节点比较
            if (Heap[pos].getKey() <= Heap[j].getKey()) return;
            Elem temp = Heap[pos];
            Heap[pos] = Heap[j];
            Heap[j] = temp;
            pos = j;
        }
    }

    public void insert(Elem val) {
        if (n>=size) {
            System.out.println("Heap is full!");
            return;
        }
        int curr = n++;
        Heap[curr] = val;
        //sift up
        while ((curr!=0) && (Heap[curr].getKey()<Heap[parent(curr)].getKey())) {
            Elem temp = Heap[curr];
            Heap[curr] = Heap[parent(curr)];
            Heap[parent(curr)] = temp;
            curr = parent(curr);
        }
    }

    public Elem removemin() {
        if (n<=0) {
            System.out.println("Remove from empty heap!");
            return null;
        }
        // 与最后一个元素交换
        Elem temp = Heap[0];
        Heap[0] = Heap[n-1];
        Heap[n-1] = temp;
        n--;
        if (n!=0) siftdown(0);
        return Heap[n];
    }

    public Elem remove(int pos) {
        if (pos<0 || pos>=n) {
            System.out.println("Illegal heap position!");
            return null;
        }
        n--;
        Elem temp = Heap[pos];
        Heap[pos] = Heap[n];
        Heap[n] = temp;
        if (n!=0) siftdown(pos);
        return Heap[n];
    }

    public Elem getMin() {
        return Heap[0];
    }
}