//Austin Morris
//10/10/2018

package CS_3700;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static void main(String[] args) {
        Grid grid = new Grid(30, 30);
        grid.playGame();
    }
}

class Cell extends Thread {
    boolean alive;
    boolean nextState;
    Cell N, NE, E, SE, S, SW, W, NW;
    CyclicBarrier barrier;

    Cell(CyclicBarrier barrier) {
        alive = Math.random() < 0.5;
        this.barrier = barrier;
    }


    @Override
    public void run() {
        while (true) {
            getNextState();

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            alive = nextState;

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private void getNextState() {
        int livingNeighbors = 0;
        if ((N != null)  && N.alive)  { livingNeighbors++; }
        if ((NE != null) && NE.alive) { livingNeighbors++; }
        if ((E != null)  && E.alive)  { livingNeighbors++; }
        if ((SE != null) && SE.alive) { livingNeighbors++; }
        if ((S != null)  && S.alive)  { livingNeighbors++; }
        if ((SW != null) && SW.alive) { livingNeighbors++; }
        if ((W != null)  && W.alive)  { livingNeighbors++; }
        if ((NW != null) && NW.alive) { livingNeighbors++; }

        if (alive && livingNeighbors < 2) {
            nextState = false;
        } else if (alive && livingNeighbors < 4) {
            nextState = true;
        } else if (alive) {
            nextState = false;
        } else if (livingNeighbors == 3) {
            nextState = true;
        } else {
            nextState = false;
        }

    }
}

class Grid {
    int width, height;
    Cell[][] cells;
    CyclicBarrier barrier;

    Grid(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        barrier = new CyclicBarrier(width * height + 1);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(barrier);
            }
        }
        setCellReferences();
    }

    public void playGame() {

        //start all threads
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j].start();
            }
        }

        while (true) {
            clearConsole();
            printGrid();

            try {
                barrier.await();
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    //method of clearing console taken from user Sean at:
    //https://stackoverflow.com/questions/19252496/clear-screen-with-windows-cls-command-in-java-console-application
    //this only works in the command prompt, not in the IDE console, of course!
    private void clearConsole() {
        final String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printGrid() {
        char character;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                character = cells[j][i].alive ? '*' : ' ';
                System.out.print(character);
            }
            System.out.println();
        }
    }

    private void setCellReferences() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                try {
                    cells[i][j].N = cells[i][j - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].N = null;
                }

                try {
                    cells[i][j].NE = cells[i + 1][j - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].NE = null;
                }

                try {
                    cells[i][j].E = cells[i + 1][j];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].E = null;
                }

                try {
                    cells[i][j].SE = cells[i + 1][j + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].SE = null;
                }

                try {
                    cells[i][j].S = cells[i][j + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].S = null;
                }

                try {
                    cells[i][j].SW = cells[i - 1][j + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].SW = null;
                }

                try {
                    cells[i][j].W = cells[i - 1][j];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].W = null;
                }

                try {
                    cells[i][j].NW = cells[i - 1][j - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cells[i][j].NW = null;
                }
            }
        }
    }
}
