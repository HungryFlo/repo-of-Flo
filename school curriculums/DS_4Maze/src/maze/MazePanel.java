package maze;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private Cell[][] cells = new Cell[MazeConstants.HEIGHT][MazeConstants.WIDTH];
    private Puzzle puzzle = new Puzzle();
    public MazePanel() {
        super.setLayout(new GridLayout(MazeConstants.HEIGHT, MazeConstants.WIDTH));

        for (int row = 0; row < MazeConstants.HEIGHT; row++) {
            for (int col = 0; col < MazeConstants.WIDTH; col++) {
                cells[row][col] = new Cell(row, col);
                super.add(cells[row][col]);
            }
        }

        super.setPreferredSize(new Dimension(MazeConstants.BOARD_WIDTH, MazeConstants.BOARD_HEIGHT));
    }

    public void newGame() {
        puzzle.newPuzzle();
        int curr = 0;
        for (int row = 0; row < MazeConstants.HEIGHT; row++) {
            for (int col = 0; col < MazeConstants.WIDTH; col++) {
                cells[row][col].newGame(puzzle.boarders[curr], puzzle.onPath[curr]);
                curr ++;
            }
        }
    }

    public void showPath() {
        if (!puzzle.onPath[0]) {
            JDialog noPathDialog = new JDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "No path", true);
            JTextField text = new JTextField("There is no path.");
            text.setEditable(false);
            text.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
            noPathDialog.add(text);
            noPathDialog.setSize(400,100);
            noPathDialog.setLocationRelativeTo(this);
            noPathDialog.setVisible(true);
        } else {
            int curr = 0;
            for (int row = 0; row < MazeConstants.HEIGHT; row++) {
                for (int col = 0; col < MazeConstants.WIDTH; col++) {
                    cells[row][col].showPath();
                    // 其实单元格在不在路线上的信息可以这里再传，还省的存储各自的状态了
                    curr++;
                }
            }
        }
    }
}
