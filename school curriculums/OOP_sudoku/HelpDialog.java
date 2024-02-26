package sudoku;

import javax.swing.*;
import java.awt.*;

public class HelpDialog extends JDialog {
    public HelpDialog() {
        super();
        setTitle("About Sudoku");
        JLabel info = new JLabel("<html><h1>关于数独</h1><p>数独（shù dú）是源自18世纪瑞士的一种数学游戏。是一种运用纸、笔进行演算的逻辑游戏。玩家需要根据9×9盘面上的已知数字，推理出所有剩余空格的数字，并满足每一行、每一列、每一个粗线宫（3*3）内的数字均含1-9，不重复。</p><p>数独盘面是个九宫，每一宫又分为九个小格。在这八十一格中给出一定的已知数字和解题条件，利用逻辑和推理，在其他的空格上填入1-9的数字。使1-9每个数字在每一行、每一列和每一宫中都只出现一次，所以又称“九宫格”。</p>");
        info.setFont(new Font("宋体",Font.PLAIN,20));
        setSize(500,350);
        add(info);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
