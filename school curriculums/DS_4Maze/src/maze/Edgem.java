package maze;

public class Edgem implements Edge {
    private Node vert1, vert2;

    public Edgem(Node vt1, Node vt2) {
        vert1 = vt1;
        vert2 = vt2;
    }

    @Override
    public Node v1() {
        return vert1;
    }

    @Override
    public Node v2() {
        return vert2;
    }
}
