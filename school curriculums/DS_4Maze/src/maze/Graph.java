package maze;

public interface Graph {
    public int n();
    public int e();
    public Edge first(Node v);
    public Edge next(Edge w);
    public boolean isEdge(Edge w);
    public boolean isEdge(Node i, Node j);
    public Node v1(Edge w);
    public Node v2(Edge w);
    public void setEdge(Node i, Node j, int weight);
    public void setEdge(Edge w, int weight);
    public void delEdge(Edge w);
    public void delEdge(Node i, Node j);
    public int weight(Node i, Node j);
    public int weight(Edge w);
    public void setMark(int v, int val);
    public int getMark(int v);
}
