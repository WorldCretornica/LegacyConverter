package com.worldcretornica.legacy;

import com.worldcretornica.legacy.storage.MySQLConnector;
import com.worldcretornica.legacy.storage.SQLiteConnector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class Main extends JDialog {

    private final JFileChooser fileChooser;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton SQLiteRadioButton;
    private JRadioButton mySQLRadioButton;
    private JPasswordField password;
    private JTextField username;
    private JPanel mySQLOptions;
    private JPanel sqlliteOptions;
    private JButton choosePlotMeDatabaseFileButton;
    private JTextField mySQLURL;
    private ButtonGroup databaseButtons;
    private File sqliteFile = null;
    private File sqliteFileDirectory = null;

    public Main() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mySQLOptions.setVisible(false);
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //Limit Files to PlotMe Database files only
        fileChooser.setFileFilter(new PlotMeDatabaseFilter());
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SQLiteRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mySQLOptions.setVisible(false);
                sqlliteOptions.setVisible(true);

            }
        });
        mySQLRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mySQLOptions.setVisible(true);
                sqlliteOptions.setVisible(false);
            }
        });
        choosePlotMeDatabaseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int valueReturned = fileChooser.showOpenDialog(Main.this);
                if (valueReturned == JFileChooser.APPROVE_OPTION) {
                    sqliteFile = fileChooser.getSelectedFile();
                    sqliteFileDirectory = sqliteFile.getParentFile();
                }
            }
        });
    }

    public static void main(String[] args) {
        Main dialog = new Main();
        dialog.setTitle("PlotMe LegacyConverter");
        dialog.setSize(3000, 3000);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onOK() {
        if (mySQLRadioButton.isSelected()) {
            new MySQLConnector(mySQLURL.getText(), username.getText(), password.getPassword());
        } else if (SQLiteRadioButton.isSelected()) {
            new SQLiteConnector(sqliteFile.)
        } else {
            //error
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
