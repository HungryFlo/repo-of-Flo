package maze;

public class GTNode {
    private GTNode par;

    public GTNode() {
        par = null;
    }

    public GTNode getPar() { return par; }

    public GTNode setPar(GTNode newPar) { return par = newPar; }
}
