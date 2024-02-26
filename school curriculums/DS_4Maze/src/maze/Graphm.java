package maze;

import java.util.Random;

public class Graphm implements Graph {
    /*
    图的相邻矩阵实现
    nodes是存放所有Node的数组，目的是给各Node编号
    matrix是记录结点间的Edge和weight的矩阵，无Edge记作0，有Edge就记录权值
    numEdge是整个图的边数
    Mark用于记录结点是否被访问过，对应着nodes的下标索引
    这里先用 int 了，最后再改成 CellStatus 吧。。。
     */
    private Node[] nodes;
    private int[][] matrix;
    private int numEdge;
    private int height;
    private int width;
    private int n;
    int[] Mark;


    public Graphm(int height, int width) {
        this.height = height;
        this.width = width;
        this.n = height * width;
        nodes = new Node[n];
        Mark = new int[n];
        // 这里和普通的图有点差别，永远只有四列
        matrix = new int[n][4];
        // 默认状态下边数为 0
        numEdge = 0;
        // 默认状态下权重为正无穷
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = MazeConstants.MAX_INF; // 因为后面可能涉及到计算，所以就用MazeConstants.MAX_INF来表示无穷大了
            }
        }
        // 需要对各个结点进行标号
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(i);
        }
    }

    /**
     * return the number of nodes
     *
     * @return
     */
    @Override
    public int n() {
        // 结点数
        return n;
    }

    /**
     * return the number of edges
     *
     * @return
     */
    @Override
    public int e() {
        // 边数
        return numEdge;
    }

    /*
    -------------------------------------------------
    下面是一些辅助函数：
     */
    private int up(int pos) {
        if (pos >= MazeConstants.WIDTH) {
            return pos - MazeConstants.WIDTH;
        } else {
//            System.out.println("First line!");
            return -1;
        }
    }

    private int left(int pos) {
        if (pos % MazeConstants.WIDTH != 0) {
            return pos - 1;
        } else {
//            System.out.println("First column!");
            return -1;
        }
    }

    private int down(int pos) {
        if (pos + MazeConstants.WIDTH < n()) {
            return (pos + MazeConstants.WIDTH);
        } else {
//            System.out.println("Last line!");
            return -1;
        }
    }

    private int right(int pos) {
        if (pos % MazeConstants.WIDTH != (MazeConstants.WIDTH - 1)) {
            return pos + 1;
        } else {
//            System.out.println("Last column!");
            return -1;
        }
    }

    /**
     * find neighbor of k;
     *
     * @param k         key of curr node
     * @param direction which neighbor to find
     * @return key of the neighbor
     */
    private int findNeighbor(int k, int direction) {
        switch (direction) {
            case 0 -> {
                return up(k);
            }
            case 1 -> {
                return left(k);
            }
            case 2 -> {
                return down(k);
            }
            case 3 -> {
                return right(k);
            }
        }
        return -1;
    }

    /**
     * Input 2 keys of nodes, then get k2's relative position to k1.
     *
     * @param k1
     * @param k2
     * @return
     */
    private int getRelativePos(int k1, int k2) {
        if (k2 == up(k1)) return 0;
        if (k2 == left(k1)) return 1;
        if (k2 == down(k1)) return 2;
        if (k2 == right(k1)) return 3;
        return -1;
    }

    /**
     * Judge if the k1 and k2 could be neighbors.
     *
     * @param k1
     * @param k2
     * @return
     */
    private boolean isPossibleEdge(int k1, int k2) {
        return getRelativePos(k1, k2) != -1;
    }

    /*
    --------------------------------------------------
    下面是对图的ADT的实现：
     */

    @Override
    public Edge first(Node v) {
        for (int i = 0; i < 4; i++) {
            if (matrix[v.key()][i] < MazeConstants.MAX_INF) {
                return new Edgem(v, nodes[findNeighbor(v.key(), i)]);
            }
        }
        return null;
    }

    @Override
    public Edge next(Edge w) {
        if (w == null) return null;
        for (int i = getRelativePos(w.v1().key(), w.v2().key()) + 1; i < 4; i++) {
            if (matrix[w.v1().key()][i] < MazeConstants.MAX_INF) {
                Node v = w.v1();
                return new Edgem(v, nodes[findNeighbor(v.key(), i)]);
            }
        }
        return null;
    }

    @Override
    public boolean isEdge(Edge w) {
        return weight(w) < MazeConstants.MAX_INF;
    }

    @Override
    public boolean isEdge(Node i, Node j) {
        return weight(i, j) < MazeConstants.MAX_INF;
    }

    @Override
    public Node v1(Edge w) {
        return w.v1();
    }

    @Override
    public Node v2(Edge w) {
        return w.v2();
    }

    private boolean setEdgeHelp(int[][] graphMatrix, int k1, int k2, int weight) {
        if (weight == 0) {
            System.out.println("Cannot set weight to 0 !");
            return false;
        }
        
        if (weight >= MazeConstants.MAX_INF) {
            System.out.println("Cannot set weight more than MazeConstants.MAX_INF !");
            System.out.println("Please use 'delEdge' if you want to delete an edge.");
            return false;
        }

        // 不相邻的结点不能添加边
        if (!isPossibleEdge(k1, k2)) {
//            System.out.println("Invalid edge input, fail to set edge!");
            return false;
        }

        // 原来就有的边，不加边数；新加入边，需要增大 numEdge 的值
        if (weightHelp(k1, k2) < MazeConstants.MAX_INF) {
            // 这里其实是想用isEdge来着，只不过输入是int了，就不方便了，
            // 所以只好把weight也抽成weightHelp供这里调用
//            System.out.println("The Edge has already existed, but the weight will be changed.");
        } else {
            numEdge++;
        }

        // 都会对权重进行更新
        graphMatrix[k1][getRelativePos(k1, k2)] = weight;
        graphMatrix[k2][getRelativePos(k2, k1)] = weight;
        return true;
    }

    @Override
    public void setEdge(Node i, Node j, int weight) {
        setEdgeHelp(matrix, i.key(), j.key(), weight);
    }

    @Override
    public void setEdge(Edge w, int weight) {
        if (w == null) return;
        else setEdgeHelp(matrix, w.v1().key(), w.v2().key(), weight);
    }

    @Override
    public void delEdge(Edge w) {
        if (isEdge(w)) numEdge--;
        matrix[w.v1().key()][getRelativePos(w.v1().key(), w.v2().key())] = MazeConstants.MAX_INF;
        matrix[w.v2().key()][getRelativePos(w.v2().key(), w.v1().key())] = MazeConstants.MAX_INF;
    }

    @Override
    public void delEdge(Node i, Node j) {
        if (isEdge(i, j)) numEdge--;
        matrix[i.key()][getRelativePos(i.key(), j.key())] = MazeConstants.MAX_INF;
        matrix[j.key()][getRelativePos(j.key(), i.key())] = MazeConstants.MAX_INF;
    }

    private int weightHelp(int k1, int k2) {
        int rp2 = getRelativePos(k1, k2);
        if (rp2 == -1) return MazeConstants.MAX_INF;
        else return matrix[k1][rp2];
    }

    @Override
    public int weight(Node i, Node j) {
        return weightHelp(i.key(), j.key());
    }

    @Override
    public int weight(Edge w) {
        if (w == null) return MazeConstants.MAX_INF;
        return weightHelp(w.v1().key(), w.v2().key());
    }

    @Override
    public void setMark(int v, int val) {
        Mark[v] = val;
    }

    @Override
    public int getMark(int v) {
        return Mark[v];
    }

    public int[][] getMatrix() {
        return matrix;
    }

    /*
    -------------------------------------------------------
    上面是对图的基本改造
    下面着手实现迷宫生成算法
    -------------------------------------------------------
     */

    public void remove(int percentage) {
        // 默认状态下边数为 0
        Random rand = new Random();
        int maxEdge = (n * 4 - ((width + height - 4) * 2 + 4 * 2)) / 2;
        int target = (int) (percentage / 100.0 * maxEdge); // 这里做乘除法一定要搞清楚是 int 还是 double
        while (numEdge < target) {
            int row = rand.nextInt(n);
            int col = rand.nextInt(4);
            int neighborKey = findNeighbor(row, col);
            if (neighborKey == -1) continue;
            if (matrix[row][col] >= MazeConstants.MAX_INF) {
                matrix[row][col] = 1;
                matrix[neighborKey][getRelativePos(neighborKey, row)] = 1;
                numEdge++;
            }
        }
        numEdge = target; // 其实可以直接用numEdge的，不过就先这样啦
    }

    public void mst() {
        // 维护 numEdge
        numEdge = 0;
        // 随机赋权值
        // 这里的复杂度亟待优化
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int cnt = 0;
            for (int j = i + 1; j < n; j++) {
                if (!isPossibleEdge(i, j)) continue;
                // 虽然在 setEdge 里面也会判断是否是邻居，但这里提前判断一下可以省下调用函数的时间
//                setEdge(nodes[i], nodes[j], rand.nextInt(1,100));
                // 再加上一个计数器，当该单元格的右方和下方邻居都被修改之后，就直接退出内层循环
                // 这样可以把大多数情况的循环次数限制在 MaseConstants.WIDTH 之内
                // 这里需要获得是否修改成功的信息，但是 setEdge 是 ADT 里的方法，不能随意修改
                // 因此这里直接调整 setEdgeHelp 然后对修改是否成功进行返回
                if (setEdgeHelp(matrix, i, j, rand.nextInt(1, 100))) {
                    cnt++;
                    if (cnt >= 2) {
                        break;
                    }
                }
            }
        }
        // 生成最小支撑树
        Kruskal();
    }

    public void addEdgeToMST(int[][] mst, int i, int j) {
        // 将两个 node 之间的边的权重改成 1
        setEdgeHelp(mst, i, j, 1);
        // 这里使用了之前用过的函数，
        // 但是之前的函数局限于修改 matrix 的值，
        // 于是对其进行了调整
        numEdge++;
    }

    private void Kruskal() {
        // 创建一个矩阵来存储 mst 的最终结果
        int[][] mst = new int[n][4];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 4; j++) {
                mst[i][j] = MazeConstants.MAX_INF;
            }
        }
        // 生成 mst
        GenTree A = new GenTree(n);
        KruskalElem[] E = new KruskalElem[e() * 2]; // 这里一定要注意，有向图边数要乘2
        // 虽然在使用时并不是以有向图的形式使用的，但是这个算法在处理时是看作有向图处理的！
        int edgecnt = 0;

        for (int i = 0; i < n; i++) {
            for (Edge w = first(nodes[i]); isEdge(w); w = next(w)) {
                E[edgecnt++] = new KruskalElem(this, w);
            }
        }
        Min_Heap H = new Min_Heap(E, edgecnt, edgecnt);
        int numMST = n;
        for (int i = 0; numMST > 1; i++) {
            KruskalElem temp = (KruskalElem) H.removemin();
            Edge w = temp.edge();
            int v = v1(w).key();
            int u = v2(w).key();
            if (A.differ(v, u)) {
                A.UNION(v, u);
                addEdgeToMST(mst, v, u);
                numMST--;
            }
        }
        // 把最终结果传回给图
        matrix = mst;
    }


    private void Dijkstra(int s, int[] distance, int[] path) {
        // 初始化为最长距离
        for (int i = 0; i < n; i++) {
            distance[i] = MazeConstants.MAX_INF;
        }
        distance[s] = 0;

        // 路径记录数组初始化
        for (int i = 0; i < n; i++) {
            path[i] = -1;
        }

        for (int i = 0; i < n; i++) {
            int v = minVertex(distance);
            setMark(v, 1);
            if (distance[v] >= MazeConstants.MAX_INF) return;
            for (Edge w = first(nodes[v]); isEdge(w); w = next(w)) {
                if (distance[v2(w).key()] > distance[v] + weight(w)) {
                    distance[v2(w).key()] = distance[v] + weight(w);
                    path[v2(w).key()] = v;
                }
            }
        }
    }

    private int minVertex(int[] distance) {
        int v = 0;
        // 找到第一个未被访问的顶点，记作 v
        for (int i = 0; i < n; i++) {
            if (getMark(i) == 0) {
                v = i;
                break;
            }
        }

        for (int i = 0; i < n; i++) {
            if (getMark(i) == 0 && distance[i] < distance[v]) {
                v = i;
            }
        }
        return  v;
    }

    public int[] getPath() {
        int[] path = new int[n];
        int[] distance = new int[n];
        Dijkstra(0, distance, path);
        if (distance[n - 1] >= MazeConstants.MAX_INF) {
            return null;
        }
        return path;
    }
}
