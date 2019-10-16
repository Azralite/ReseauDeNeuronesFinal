package perceptron;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

//CTRL + SHIFT + O pour générer les imports
public class Fenetre extends JFrame {

    //LE MENU
    private JMenuBar menuBar = new JMenuBar();

    JMenuItem nouveau = new JMenuItem("Effacer"),
            quitter = new JMenuItem("Quitter");

    //Notre zone de dessin
    public DrawPanel drawPanel = new DrawPanel();

    public Fenetre() {
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        //On initialise le menu
        this.initMenu();
        //On positionne notre zone de dessin
        this.getContentPane().add(drawPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    //Initialise le menu
    private void initMenu() {
        nouveau.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawPanel.erase();
            }
        });

        nouveau.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));

        quitter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        quitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));


        this.setJMenuBar(menuBar);
    }
}