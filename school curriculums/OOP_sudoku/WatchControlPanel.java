package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class WatchControlPanel extends JPanel {
    private boolean flag = true; // true 代表秒表正在走
    public WatchControlPanel() {
        setLayout(new BorderLayout());

        ImageIcon pauseIcon = new ImageIcon("icon/暂停.png");
        ImageIcon pauseIconBlue = new ImageIcon("icon/暂停蓝.png");
        ImageIcon continueIcon = new ImageIcon("icon/继续.png");
        ImageIcon continueIconBlue = new ImageIcon("icon/继续蓝.png");
        ImageIcon watchIcon = new ImageIcon("icon/秒表.png");

        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new FlowLayout(FlowLayout.TRAILING,10,5));

        JLabel pauseLabel = new JLabel(pauseIcon);
        pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pauseLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(flag) {
                    ((SudokuMain) SwingUtilities.getWindowAncestor(e.getComponent())).pauseTime();
                    pauseLabel.setIcon(continueIcon);
                    flag = false;
                } else {
                    ((SudokuMain) SwingUtilities.getWindowAncestor(e.getComponent())).continueTime();
                    pauseLabel.setIcon(pauseIcon);
                    flag = true;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(flag) {
                    pauseLabel.setIcon(pauseIconBlue);
                } else {
                    pauseLabel.setIcon(continueIconBlue);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(flag) {
                    pauseLabel.setIcon(pauseIcon);
                } else {
                    pauseLabel.setIcon(continueIcon);
                }
            }
        });

        JLabel watchIconLabel = new JLabel(watchIcon);

        iconPanel.add(pauseLabel);
        iconPanel.add(watchIconLabel);

        add(iconPanel, BorderLayout.CENTER);
    }
}
