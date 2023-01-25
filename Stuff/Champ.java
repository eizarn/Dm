package Stuff;

import java.util.Random;

public class Champ {
    final static int N_MINES = 30;
    final static int HEIGHT = 10;
    final static int WIDTH = 10;
    private boolean [][] mines = new boolean[HEIGHT][WIDTH];

    private Random rng = new Random();

    public void emptyMines() {
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                mines[row][col] = false;
            }
        }
    }

    public void placeMines() {
        for (int i = 0; i < N_MINES; i++) {
            int x, y;
            while (mines[y = rng.nextInt(HEIGHT)][x = rng.nextInt(WIDTH)]);
            mines[y][x] = true;
        }
    }

    public void displayMines() {
        boolean darkCol = false;

        for (boolean [] row: mines) {
            boolean darkCell = darkCol = !darkCol;
            for (boolean mine : row) {
                darkCell = !darkCell;
                System.out.print(darkCell ? mine ? "\033[48;2;210;0;0m\033[38;2;0;0;0m  \033[0m"
                                                 : "\033[48;2;40;40;40m\033[38;2;0;0;0m  \033[0m"
                                          : mine ? "\033[48;2;255;0;0m\033[38;2;0;0;0m  \033[0m"
                                                 : "\033[48;2;50;50;50m\033[38;2;0;0;0m  \033[0m");
            }
            System.out.print("\n");
        }
    }

    public int getWidth() {
        return mines[0].length;
    }

    public int getHeight() {
        return mines.length;
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public void didier() {
        // do nothing
    }
}
