package maze;

public class KruskalElem {
    private Edge edge;
    private Graph G;

    public KruskalElem(Graph inG, Edge w) {
        G = inG;
        edge = w;
    }

    public int getKey() {
        return G.weight(edge);
    }

    public Edge edge() {
        return edge;
    }
}
