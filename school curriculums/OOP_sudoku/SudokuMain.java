package sudoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
/**
 * 运行Sudoku游戏的程序
 */
public class SudokuMain extends JFrame {
    private static final long serialVersionUID = 1L;
    GameBoardPanel board = new GameBoardPanel();
    // 秒表
    StopWatch watch = new StopWatch();
    // 进度条
    JProgressBar progress;

    public SudokuMain(int mode) {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(board, BorderLayout.CENTER);

        // 初始化一局游戏面板
        // newGame的参数代表三个难度，分别为0,1,2
        board.newGame(mode, true);

        //创建菜单栏对象
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);


        JMenu menu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(menu);
        menuBar.add(helpMenu);

        menu.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        helpMenu.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        JMenuItem menuItemNew = new JMenuItem("New Game");
        menuItemNew.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WelcomeMain newGame = new WelcomeMain();
            }
        });
        menu.add(menuItemNew);

        JMenuItem menuItemRes = new JMenuItem("Reset Game");
        menuItemRes.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        menuItemRes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.newGame(mode, false);
                watch.reset();
            }
        });
        menu.add(menuItemRes);

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItemExit);

        // JPanel的嵌套使用
        JPanel jpNorth = new JPanel();
        jpNorth.setLayout(new BorderLayout());

        // 进度条
        progress = new JProgressBar(0,board.getCellsToGuess());
        progress.setStringPainted(true);
        progress.setString("千里之行，始于足下。");
        progress.setFont(new Font("楷体",Font.PLAIN,20));
        jpNorth.add(progress,BorderLayout.WEST);

        // 秒表
        jpNorth.add(watch,BorderLayout.EAST);

        // 秒表控制条
        jpNorth.add(new WatchControlPanel(), BorderLayout.CENTER);

        cp.add(jpNorth, BorderLayout.NORTH);

        // helpMenu
        JMenuItem sudokuMenuItem = new JMenuItem("About Sudoku");
        sudokuMenuItem.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        sudokuMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelpDialog helpDialog = new HelpDialog();
            }
        });

        helpMenu.add(sudokuMenuItem);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setVisible(true);
    }

    public String getSuccessTime() {
        watch.stop();
        return watch.getCurrTime();
    }

    public void pauseTime() {
        watch.stop();
    }

    public void continueTime() {
        watch.start();
    }

    public void setProgress(int cellsRemain, int cellsFilled) {
        progress.setValue(cellsFilled);
        progress.setString("还剩 "+cellsRemain+" 个");
        progress.updateUI();
    }
}