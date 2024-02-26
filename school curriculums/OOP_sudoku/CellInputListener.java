package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 * 创建所有单元格都可以使用的一个监听器类型
 */
public class CellInputListener implements ActionListener {
    private GameBoardPanel gameboard;
    private int cellsRemain;
    private int cellsFilled = 0;
    public CellInputListener(GameBoardPanel gameboard){
        if (gameboard == null)
            throw new IllegalArgumentException("Null pointer reference.");
        this.gameboard = gameboard;
    }

    public void setCellsRemain(int val) {
        cellsRemain = val;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 获得是哪个单元格出发了回车事件（获得事件源）
        Cell sourceCell = (Cell)e.getSource();

        // 获得输入的数字
        try {
            int numberIn = Integer.parseInt(sourceCell.getText());
        //todo2
        if(numberIn == sourceCell.number){
            sourceCell.status = CellStatus.CORRECT_GUESS;
            cellsRemain--;
            cellsFilled++;
            ((SudokuMain)SwingUtilities.getWindowAncestor(this.gameboard)).setProgress(cellsRemain, cellsFilled);
        } else {
            sourceCell.status = CellStatus.WRONG_GUESS;
        }
        sourceCell.paint();
        } catch (NumberFormatException numberFormatException) {
            JDialog wrongInput = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this.gameboard), "Error!", true);
            wrongInput.setSize(300,100);
            JLabel wrongInputWarning = new JLabel("Invalid input! Please input a NUMBER!");
            wrongInputWarning.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            wrongInput.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this.gameboard));
            wrongInput.add(wrongInputWarning);
            wrongInput.setVisible(true);
        }

        //todo3
        if(gameboard.isSolved()){
            JDialog success = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this.gameboard), "success", true);
            String time = ((SudokuMain)SwingUtilities.getWindowAncestor(this.gameboard)).getSuccessTime();
            success.setSize(400,100);
            JLabel congratulations = new JLabel("Congratulations! Time: " + time);
            congratulations.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            success.add(congratulations);
            success.setVisible(true);
        }
    }
}