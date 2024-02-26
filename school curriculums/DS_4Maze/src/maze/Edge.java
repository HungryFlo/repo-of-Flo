package maze;

public interface Edge {
    public Node v1();
    public Node v2();
}
// 这里采用了用一个单独的矩阵来存储weight的方法，
// 所以就没有在edge中加入与权重有关的操作
