package maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MazeMain extends JFrame {
    MazePanel mazePanel = new MazePanel();

    public MazeMain() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(mazePanel, BorderLayout.CENTER);

        mazePanel.newGame();

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);


        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        menu.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        JMenuItem menuItemNew = new JMenuItem("New Game");
        menuItemNew.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.newGame();
            }
        });
        menu.add(menuItemNew);

        JMenuItem menuItemShow = new JMenuItem("Show Path");
        menuItemShow.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        menuItemShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.showPath();
            }
        });
        menu.add(menuItemShow);

//        setSize(10000,10000);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Maze");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new MazeMain();
            }
        });
    }
}
