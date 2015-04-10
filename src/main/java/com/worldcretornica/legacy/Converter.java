package com.worldcretornica.legacy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Converter extends JPanel {

    private static JPanel contentPane;
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
        contentPane = new Converter();
        window.setContentPane(contentPane);
        window.setVisible(true);
        contentPane.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

    }

}
