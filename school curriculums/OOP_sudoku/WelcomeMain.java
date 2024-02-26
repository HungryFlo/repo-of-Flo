package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeMain extends JFrame {
    public WelcomeMain() {
        Container cp = getContentPane();
        cp.setLayout(new FlowLayout(1, 20,20));

        JButton easyButton = new JButton("EASY");
        easyButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SudokuMain game = new SudokuMain(0);
                dispose();
            }
        });

        JButton intermediateButton = new JButton("INTERMEDIATE");
        intermediateButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        intermediateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SudokuMain game = new SudokuMain(1);
                dispose();
            }
        });

        JButton difficultButton = new JButton("DIFFICULT");
        difficultButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        difficultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SudokuMain game = new SudokuMain(2);
                dispose();
            }
        });

        cp.add(easyButton);
        cp.add(intermediateButton);
        cp.add(difficultButton);

        setSize(500,100);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new WelcomeMain();
            }
        });
    }
}
