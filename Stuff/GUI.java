package Stuff;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.KeyEvent;

import java.awt.event.MouseEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class GUI extends JPanel implements ActionListener {
    public JMenuBar menuBar;
    MineDisplay [][] mineDisplays;
    Champ champ;
    JPanel grid;

    DataInputStream input;
    DataOutputStream output;

    ImageIcon mineDisplay_default;
    ImageIcon mineDisplay_mine;
    ImageIcon mineDisplay_0;
    ImageIcon mineDisplay_1;
    ImageIcon mineDisplay_2;
    ImageIcon mineDisplay_3;
    ImageIcon mineDisplay_4;
    ImageIcon mineDisplay_5;
    ImageIcon mineDisplay_6;
    ImageIcon mineDisplay_7;
    ImageIcon mineDisplay_8;

    GUI(Champ champ) {
        // MineDisplay icons
        try {
            this.mineDisplay_default = new ImageIcon(ImageIO.read(new File("img/default.bmp")));
            this.mineDisplay_mine = new ImageIcon(ImageIO.read(new File("img/mine.bmp")));
            this.mineDisplay_0 = new ImageIcon(ImageIO.read(new File("img/0.bmp")));
            this.mineDisplay_1 = new ImageIcon(ImageIO.read(new File("img/1.bmp")));
            this.mineDisplay_2 = new ImageIcon(ImageIO.read(new File("img/2.bmp")));
            this.mineDisplay_3 = new ImageIcon(ImageIO.read(new File("img/3.bmp")));
            this.mineDisplay_4 = new ImageIcon(ImageIO.read(new File("img/4.bmp")));
            this.mineDisplay_5 = new ImageIcon(ImageIO.read(new File("img/5.bmp")));
            this.mineDisplay_6 = new ImageIcon(ImageIO.read(new File("img/6.bmp")));
            this.mineDisplay_7 = new ImageIcon(ImageIO.read(new File("img/7.bmp")));
            this.mineDisplay_8 = new ImageIcon(ImageIO.read(new File("img/8.bmp")));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        this.champ = champ;
        int width = this.champ.getWidth();
        int height = this.champ.getHeight();
        mineDisplays = new MineDisplay[height][width];

        this.grid = new JPanel();

        setLayout(new BorderLayout());

        this.grid.setLayout(new GridLayout(height, width));
        
        for (int row = 0; row < champ.getHeight(); row++) {
            for (int col = 0; col < champ.getWidth(); col++) {
                MineDisplay button = new MineDisplay(row, col);
                mineDisplays[row][col] = button;
                grid.add(button);
            }
        }
        add(this.grid, BorderLayout.CENTER);
        initGrid();

        add(new JLabel("Le mur"), BorderLayout.NORTH);
        add(new JLabel("un petit jaune"), BorderLayout.SOUTH);
        RestartButton restartButton = new RestartButton();
        restartButton.addActionListener(this);
        add(restartButton, BorderLayout.SOUTH);
        add(new JLabel("des petits jaunes"), BorderLayout.EAST);
        add(new JLabel("Buffalo Bill"), BorderLayout.WEST);

        /* Les menus */
        // création de la barre
        menuBar = new JMenuBar();
        // Le menu Partie
        JMenu menuPartie = new JMenu("Partie");
        menuBar.add(menuPartie);
        // L’item Quitter
        JMenuItem mPlayOnline = new JMenuItem("En ligne", KeyEvent.VK_Q);
        menuPartie.add(mPlayOnline);
        mPlayOnline.addActionListener(this);
        JMenuItem mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        menuPartie.add(mQuitter);
        mQuitter.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof RestartButton) {
            champ.emptyMines();
            champ.placeMines();
            initGrid();
        }
        else if (source instanceof JMenuItem) {
            switch(((JMenuItem)source).getText()) {
                case "Quitter":
                    System.exit(0);
                    break;

                case "En ligne":
                    connectNetwork("localhost", 42069);
                    break;
            }
        }
    }

    private void initGrid() {
        for (int row = 0; row < champ.getHeight(); row++) {
            for (int col = 0; col < champ.getWidth(); col++) {
                MineDisplay mineDisplay = mineDisplays[row][col];
                mineDisplay.setIcon(mineDisplay_default);
            }
        }
    }

    private class MineDisplay extends JLabel {
        private int row, col;
        GUI gui;
        MineDisplay(int mineRow, int mineCol) { this("", mineRow, mineCol); }
        MineDisplay(String text, int mineRow, int mineCol) {
            super(text);
            row = mineRow;
            col = mineCol;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    
                }
            });
        }

        public int getRow() {
            return row;
        }
        
        public int getCol() {
            return col;
        }
    }

    private class RestartButton extends JButton {
        public RestartButton() {
            super("Restart");
        }
    }

    void connectNetwork(String ip, int port) {
        try {
            Socket sock = new Socket(ip, port);
            System.out.println("Connected to server :3");

            input = new DataInputStream(sock.getInputStream());
            output = new DataOutputStream(sock.getOutputStream());

            int numClient = input.readInt();
            System.out.println(numClient);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}