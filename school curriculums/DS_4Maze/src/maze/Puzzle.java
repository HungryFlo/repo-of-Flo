package maze;

import javax.swing.*;

public class Puzzle {
    Graphm graph;
    int[][] boarders;
    boolean[] onPath;

    public void newPuzzle() {
        graph = new Graphm(MazeConstants.HEIGHT, MazeConstants.WIDTH);
//        graph.remove(70);
        graph.mst();
        boarders = graph.getMatrix();
        int[] path = graph.getPath();
        if (path != null) {
            transferPath(path);
        } else {
            onPath = new boolean[MazeConstants.CELL_NUM];
        }
        transferBoarder();
    }

    private void transferBoarder() {
        for (int i = 0; i < MazeConstants.CELL_NUM; i++) {
            for (int j = 0; j < 4; j++) {
                if (boarders[i][j] >= MazeConstants.MAX_INF) {
                    boarders[i][j] = 1;
                } else {
                    boarders[i][j] = 0;
                }
            }
        }
    }

    private void transferPath(int[] path) {
        onPath = new boolean[MazeConstants.CELL_NUM];
        transPathHelp(path, MazeConstants.CELL_NUM - 1);
    }

    private void transPathHelp(int[] path, int index) {
        if (index == -1) return;
        onPath[index] = true;
        transPathHelp(path, path[index]);
    }
}
