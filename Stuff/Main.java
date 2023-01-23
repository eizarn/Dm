/**
 * fichier Main.java : magnifique hellow world
 */
package Stuff;

import javax.swing.*;

public class Main extends JFrame {
    int nb = 12;

    final static int SIZE = 4;
    int [] myTab = new int[SIZE];
    int [][] my2DTab = new int[SIZE][SIZE];

    Main() {
        Champ champ = new Champ();
        champ.emptyMines();
        champ.placeMines();

        GUI gui = new GUI(champ);
        setContentPane(gui);
        setJMenuBar(gui.menuBar);
        pack(); // ajuster la taille de la fenêtre automatiquement
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * ouais le main
     * @param args non utilisé
     */
    public static void main(String args[]) {
        new Main();
    }

    public void affCoucou() {
        int i = 0;
        System.out.println("i = " + i);
    }

    public void affTab() {
        System.out.print("[ ");
        for (int val : myTab) {
            System.out.print(val + " ");
        }
        System.out.println("]");
    }

    public void aff2DTab() {
        System.out.print("┌─\n");
        
        for (int [] row: my2DTab) {
            for (int cell : row) {
                System.out.print(String.format("%4d ", cell));
            }
            System.out.println();
        }

        for (int i = 0; i < my2DTab[0].length; i++) {
            System.out.print("     ");
        }
        System.out.println("┘");
    }
}