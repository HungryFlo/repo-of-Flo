package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class StopWatch extends JPanel{
    private JLabel currTimeLabel;
    private int cntSec, cntMin, cntHour;
    private DecimalFormat textFormat = new DecimalFormat("00");
    private Timer timer = new Timer(1000, new TimeActionListener());

    public StopWatch() {
        currTimeLabel = new JLabel(" ");
        TimeActionListener timeActionListener = new TimeActionListener();
        currTimeLabel.setFont(new Font("Comic Sans MS", Font.PLAIN,20));

        this.add(currTimeLabel);

        start();
        paint();
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public String getCurrTime() {
        return currTimeLabel.getText();
    }

    public void reset() {
        cntHour = cntMin = cntSec = 0;
    }

    public void paint() {
        currTimeLabel.setText(textFormat.format(cntHour)+":"
                +textFormat.format(cntMin)+":"+textFormat.format(cntSec));
        repaint();
    }

    class TimeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cntSec++;
            if(cntSec >= 59) {
                cntMin++;
                cntSec = 0;
            } else if (cntMin >= 59) {
                cntHour++;
                cntMin = 0;
            }
            paint();
        }
    }

}