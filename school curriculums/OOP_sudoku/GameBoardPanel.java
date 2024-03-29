package sudoku;
import java.awt.*;
import javax.swing.*;

/*
 * 创建一个JPanel的子类类型，该面板中包含了9*9个Cell对象，采用了GridLayout布局管理器
 */
public class GameBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // 以像素单位为每一个Cell单元格设置外观大小
    public static final int CELL_SIZE = 60;
    public static final int BOARD_WIDTH  = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;

    /** 该游戏面板是由9x9个Cell对象 (Cell是JTextFields的子类)构成 */
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private Puzzle puzzle = new Puzzle();
    private CellInputListener listener;
    private int cellsToGuess; // 进度条和新建游戏正好可以共用这个变量
    public GameBoardPanel() {
        super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));

        // 将Cell对象组件加入到Panel对象中
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col);
                super.add(cells[row][col]);
            }
        }
        listener = new CellInputListener(this);

        for(int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            for(int j = 0; j < SudokuConstants.GRID_SIZE; j++) {
                cells[i][j].addActionListener(listener);
            }
        }
        // 为所有可编辑的单元格（即需要输入数字）绑定监听器对象
        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }

    /**
     * 生成一局新游戏，基于生成的新游戏中的数据重新初始化gameboard中包含的每一个cell对象
     */
    public void newGame(int mode, boolean newPuzzle) {
        if(newPuzzle) {
            switch (mode) {
                case 0: {
                    cellsToGuess = 20;
                    break;
                }
                case 1: {
                    cellsToGuess = 30;
                    break;
                }
                case 2: {
                    cellsToGuess = 40;
                    break;
                }
            }
            puzzle.newPuzzle(cellsToGuess);
        }
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
        listener.setCellsRemain(cellsToGuess);
    }

    /**
     * 如果当局游戏成功解决，那么返回true
     * 判断依据：只要有一个单元格的状态是CellStatus.TO_GUESS或者CellStatus.WRONG_GUESS，那么游戏就没有结束。
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getCellsToGuess() {
        return cellsToGuess;
    }
}