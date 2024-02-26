package maze;

public class GenTree {
    private GTNode[] array;

    public GenTree(int size) {
        array = new GTNode[size];
        for (int i = 0; i < size; i++) {
            array[i] = new GTNode();
        }
    }

    public boolean differ(int a, int b) {
        GTNode root1 = FIND(array[a]);
        GTNode root2 = FIND(array[b]);
        return root1 != root2;
    }

    private GTNode FIND(GTNode curr) {
        if (curr.getPar() == null) return curr;
        return curr.setPar(FIND(curr.getPar()));
    }

    public void UNION(int a, int b) {
        GTNode root1 = FIND(array[a]);
        GTNode root2 = FIND(array[b]);
        root2.setPar(root1);
    }
}
