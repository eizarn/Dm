package Stuff;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
    ImageIcon [] mineDisplay = new ImageIcon [9];

    GUI(Champ champ) {
        // MineDisplay icons
        try {
            this.mineDisplay_default = new ImageIcon(ImageIO.read(new File("img/default.bmp")));
            this.mineDisplay_mine = new ImageIcon(ImageIO.read(new File("img/mine.bmp")));
            this.mineDisplay[0] = new ImageIcon(ImageIO.read(new File("img/0.bmp")));
            this.mineDisplay[1] = new ImageIcon(ImageIO.read(new File("img/1.bmp")));
            this.mineDisplay[2] = new ImageIcon(ImageIO.read(new File("img/2.bmp")));
            this.mineDisplay[3] = new ImageIcon(ImageIO.read(new File("img/3.bmp")));
            this.mineDisplay[4] = new ImageIcon(ImageIO.read(new File("img/4.bmp")));
            this.mineDisplay[5] = new ImageIcon(ImageIO.read(new File("img/5.bmp")));
            this.mineDisplay[6] = new ImageIcon(ImageIO.read(new File("img/6.bmp")));
            this.mineDisplay[7] = new ImageIcon(ImageIO.read(new File("img/7.bmp")));
            this.mineDisplay[8] = new ImageIcon(ImageIO.read(new File("img/8.bmp")));
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
                MineDisplay button = new MineDisplay(row, col, this);
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
        champ.displayMines();
        playSound("Dm");
    }

    private class MineDisplay extends JLabel {
        private boolean clicked = false;
        MineDisplay(int row, int col, GUI gui) { this("", row, col, gui); }
        MineDisplay(String text, int row, int col, GUI gui) {
            super(text);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    MineDisplay me = (MineDisplay) e.getSource();
                    if (me.clicked == false) {
                        if (gui.champ.isMine(row, col)) {
                            setIcon(mineDisplay_mine);
                            gameOver(false);
                        }
                        else {
                            int nMines = countMines(row, col, gui);
                            setIcon(mineDisplay[nMines]);
                            if (nMines == 0) {
                                // propagate
                                Champ beenThereDoneThat = new Champ();
                                beenThereDoneThat.emptyMines();
                                propagate_rec(row, col, gui, beenThereDoneThat);
                            }
                        }
                    }
                }
            });
        }

        private void propagate_rec(int row, int col, GUI gui, Champ beenThereDoneThat) {
            beenThereDoneThat.setMine(row, col);
            final int [][] coordsToTest = {
                {-1, -1},
                {-1, 0},
                {-1, 1},
                {0, -1},
                {0, 1},
                {1, -1},
                {1, 0},
                {1, 1}
            };
            for (int [] coord: coordsToTest) {
                try {
                    int nMines = countMines(row + coord[0], col + coord[1], gui);
                    gui.mineDisplays[row + coord[0]][col + coord[1]].setIcon(mineDisplay[nMines]);
                    if (nMines > 0) {
                        gui.mineDisplays[row + coord[0]][col + coord[1]].setIcon(mineDisplay[nMines]);
                    }
                    else if (!( beenThereDoneThat.isMine(row + coord[0], col + coord[1])
                             || gui.champ.isMine(row + coord[0], col + coord[1])
                    )) {
                        propagate_rec(row + coord[0], col + coord[1], gui, beenThereDoneThat);
                    }
                }
                catch (Exception ex) {
                    // do nothing
                }
            }
        }
    }

    private int countMines(int row, int col, GUI gui) {
        final int [][] coordsToTest = {
            {-1, -1},
            {-1, 0},
            {-1, 1},
            {0, -1},
            {0, 1},
            {1, -1},
            {1, 0},
            {1, 1}
        };

        int nMines = 0;
        for (int [] coord: coordsToTest) {
            try {
                if (gui.champ.isMine(row + coord[0], col + coord[1])) {
                    nMines++;
                }
            }
            catch (Exception ex) {
                // do nothing
            }
        }

        return nMines;
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

    public static synchronized void playSound(final String name) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            new File("sfx/" + name + ".wav"));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }

    public void gameOver(boolean winner) {
        if (winner) {
            playSound("wahoo");
            JOptionPane.showMessageDialog(new JFrame(), "Congratulations, you won!", "", 0, mineDisplay[1]);
            // TODO: time, score
        }
        else {
            playSound("reverb fart");
            JOptionPane.showMessageDialog(new JFrame(), "We'll get them next time!", "", 0, mineDisplay_mine);
        }
    }
}