package maze;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class Cell extends JTextField {
    int row, col;
    boolean onPath;
    public static final int FONT_SIZE = 14;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        setEditable(false);
        setHorizontalAlignment(0);
        setFont(new Font("Comic Sans MS", Font.BOLD, FONT_SIZE));
    }

    public void newGame(int[] bd, boolean onPath) {
        this.onPath = onPath;
        setText("");
        MatteBorder boarder;
        boarder = new MatteBorder(bd[0], bd[1], bd[2], bd[3], Color.black);
        setBorder(boarder);
        if (row == 0 && col == 0) {
            setText("*");
            setForeground(Color.BLACK);
        } else if (row == MazeConstants.HEIGHT - 1 && col == MazeConstants.WIDTH - 1) {
            setText("*");
            setForeground(Color.BLACK);
        }
    }

    public void showPath() {
        if (onPath) {
            setText("#");
            setForeground(Color.MAGENTA);
        }
    }
}
