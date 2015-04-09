package com.worldcretornica.legacy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Converter extends JPanel {

    private JPanel contentPane;
    private JButton convertButton;
    private JButton exitButton;

    public Converter() {

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("PlotMe LegacyConverter");
        Converter panel = new Converter();
        window.setContentPane(panel);
        window.setVisible(true);
        panel.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

    }

}
