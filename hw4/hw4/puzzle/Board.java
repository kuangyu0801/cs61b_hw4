package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState {
    private int[] board;
    private int size;
    private static final int BLANK = 0;
    private static final boolean USEMANHATTAN = true;

    /** Constructs a board from an N-by-N array of tiles where
    tiles[i][j] = tile at row i, column j*/
    public Board(int[][] tiles) {
        size = tiles.length;
        board = new int[size * size];
        for (int i = 0; i < size; i += 1) {
            for (int j = 0; j < size; j += 1) {
                board[board2Dto1D(i, j)] = tiles[i][j];
            }
        }
    }

    private int board2Dto1D(int i, int j) {
        return i * size + j;
    }

    /** Returns the string representation of the board. 
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    /** Returns value of tile at row i, column j (or 0 if blank)*/
    public int tileAt(int i, int j) {
        boolean isIOut =  (i > (size - 1) || i < 0) ? true : false;
        boolean isJOut =  (j > (size - 1) || j < 0) ? true : false;
        if (isIOut || isJOut) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return board[board2Dto1D(i, j)];
    }

    /** Returns the board size N*/
    public int size() {
        return size;
    }

    /** return the hamming distance*/
    public int hamming() {
        int hammDist = 0;
        // only item that is not BLANK
        for (int i = 0; i < (size * size); i += 1) {
            if (board[i] != i + 1 && board[i] != BLANK) {
                hammDist += 1;
            }
        }
        return hammDist;
    }

    private int manhattanCalulator() {
        return 0;
    }

    private int toX(int index) {
        return index / size;
    }

    private int toY(int index) {
        return index % size;
    }
    /** return the manhattan distance*/
    public int manhattan() {
        int manDist = 0;

        for (int i = 0; i < (size * size); i += 1) {
            if (board[i] != i + 1 && board[i] != BLANK) {
                int xDiff = Math.abs(toX(board[i] - 1) - toX(i));
                int yDiff = Math.abs(toY(board[i] - 1) - toY(i));
                manDist += xDiff + yDiff;
            }
        }
        return manDist;
    }

    @Override
    public int estimatedDistanceToGoal() {
        if (USEMANHATTAN) {
            return manhattan();
        } else {
            return hamming();
        }
    }

    /** source code provided in http://joshh.ug/neighbors.html*/
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int nextSize = size();
        int nextX = -1;
        int nextY = -1;
        for (int i = 0; i < nextSize; i++) {
            for (int j = 0; j < nextSize; j++) {
                if (tileAt(i, j) == BLANK) {
                    nextX = i;
                    nextY = j;
                }
            }
        }
        int[][] nextBoard = new int[nextSize][nextSize];
        for (int pug = 0; pug < nextSize; pug++) {
            for (int yug = 0; yug < nextSize; yug++) {
                nextBoard[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int i = 0; i < nextSize; i++) {
            for (int j = 0; j < nextSize; j++) {
                if (Math.abs(-nextX + i) + Math.abs(j - nextY) - 1 == 0) {
                    nextBoard[nextX][nextY] = nextBoard[i][j];
                    nextBoard[i][j] = BLANK;
                    Board neighbor = new Board(nextBoard);
                    neighbors.enqueue(neighbor);
                    nextBoard[i][j] = nextBoard[nextX][nextY];
                    nextBoard[nextX][nextY] = BLANK;
                }
            }
        }
        return neighbors;
    }

    /** Returns true if this board's tile values are the same
    position as y's*/
    @Override
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (y == null || getClass() != y.getClass()) {
            return false;
        }

        Board board1 = (Board) y;

        if (size() != board1.size()) {
            return false;
        } else {
            for (int i = 0; i < size; i += 1) {
                for (int j = 0; j < size; j += 1) {
                    if (this.tileAt(i, j) != board1.tileAt(i, j)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = board != null ? board.hashCode() : 0;
        return result;
    }
}
