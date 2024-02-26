import maze.Graphm;
import maze.MazeConstants;

public class MazeTest {
    public static void main(String[] args) {
        Graphm graph;
        int res = 0;
        for (int i = 0; i < 1000; i++) {
            graph = new Graphm(MazeConstants.HEIGHT, MazeConstants.WIDTH);
            graph.remove(50);
            if (graph.getPath() != null) {
                res ++;
            }
        }
        System.out.println("remove 50% : " + res + " ‰");

        for (int i = 0; i < 1000; i++) {
            graph = new Graphm(MazeConstants.HEIGHT, MazeConstants.WIDTH);
            graph.remove(70);
            if (graph.getPath() != null) {
                res ++;
            }
        }
        System.out.println("remove 70% : " + res + " ‰");
    }
}
