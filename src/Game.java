import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public final class Game implements Subject {

    public static final int BASE = 2;
    public static final int NEXT_BASE = 2 * BASE;

    private static final Random RAND = new Random();

    // Fields
    private final int size;
    private int[][] matrix;
    private final Observer observer;
    private HashSet<Position> emptyCells;

    // Constructors
    public Game(int s, Observer o) {
        size = s;
        observer = o;
        matrix = new int[size][size];
        emptyCells = new HashSet<>();

        updateEmptyCellInfo();
        generateRandomCell();
        generateRandomCell();
    }

    // Private copy constructor for simulation purposes
    // it will only be used to check whether the game is over
    private Game(Game other) {
        this.observer = null;
        this.size = other.size;
        this.matrix = new int[size][size];
        this.emptyCells = new HashSet<>();

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                this.matrix[i][j] = other.matrix[i][j];
            }
        }

        for (Position pos: other.emptyCells) {
            this.emptyCells.add(pos);
        }
    }

    //Methods
    public int getNum(int i, int j) {
        return matrix[i][j];
    }

    public boolean isOver() {
        if (emptyCells.isEmpty()) {
            Game simulate = new Game(this);
            simulate.moveLeft(false);
            simulate.moveRight(false);
            simulate.moveUp(false);
            simulate.moveDown(false);
            simulate.updateEmptyCellInfo();
            return simulate.emptyCells.isEmpty();
        }
        return false;
    }

    public int calculateScore() {
        int score = Integer.MIN_VALUE;
        for (int  i = 0; i < size; ++i) {
            for (int  j = 0; j < size; ++j) {
                score = Math.max(score, matrix[i][j]);
            }
        }
        return score;
    }

    @Override
    public void notifyObserver() {
        observer.update();
    }

    public void moveLeft(boolean enable) {
        Thread[] threads = new Thread[size];
        for(int i = 0; i < size; ++i) {
            int row = i;
            threads[i] = new Thread(() -> {
                moveRowLeft(row);
            });
            threads[i].start();
        }
        Useful.waitThreads(threads);
        postMoveActions(enable);
    }

    public void moveRight(boolean enable) {
        Thread[] threads = new Thread[size];
        for(int i = 0; i < size; ++i) {
            int row = i;
            threads[i] = new Thread(() -> {
                moveRowRight(row);
            });
            threads[i].start();
        }
        Useful.waitThreads(threads);
        postMoveActions(enable);
    }

    public void moveUp(boolean enable) {
        Thread[] threads = new Thread[size];
        for(int j = 0; j < size; ++j) {
            int col = j;
            threads[j] = new Thread(() -> {
                moveColumnUp(col);
            });
            threads[j].start();
        }
        Useful.waitThreads(threads);
        postMoveActions(enable);
    }

    public void moveDown(boolean enable) {
        Thread[] threads = new Thread[size];
        for(int j = 0; j < size; ++j) {
            int col = j;
            threads[j] = new Thread(() -> {
                moveColumnDown(col);
            });
            threads[j].start();
        }
        Useful.waitThreads(threads);
        postMoveActions(enable);
    }

    private void moveRowLeft(int rowNum) {
        // Remove zeros
        ArrayList<Integer> list = Useful.toArrayList(matrix[rowNum]);
        list.removeIf(x -> x == 0);

        // Collapsing Algorithm
        collapse(list);

        //Remove zeros again
        list.removeIf(x -> x == 0);

        // Pad zeros to the end
        Useful.appendZeros(list, size);

        // Set the array row accordingly
        Useful.setMatrixRow(matrix, rowNum, list);
    }

    private void moveRowRight(int rowNum) {
        // Remove zeros
        ArrayList<Integer> list = Useful.toArrayList(matrix[rowNum]);
        list.removeIf(x -> x == 0);

        // Collapsing Algorithm
        reverseCollapse(list);

        //Remove zeros again
        list.removeIf(x -> x == 0);

        // Pad zeros to the beginning
        Useful.prependZeros(list, size);

        // Set the array row accordingly
        Useful.setMatrixRow(matrix, rowNum, list);
    }

    private void moveColumnUp(int colNum) {
        // Remove zeros
        ArrayList<Integer> list = Useful.listFromColumn(matrix, colNum);
        list.removeIf(x -> x == 0);

        // Collapsing Algorithm
        collapse(list);

        //Remove zeros again
        list.removeIf(x -> x == 0);

        // Pad zeros to the end
        Useful.appendZeros(list, size);

        // Set the array column accordingly
        Useful.setMatrixColumn(matrix, colNum, list);
    }

    private void moveColumnDown(int colNum) {
        // Remove zeros
        ArrayList<Integer> list = Useful.listFromColumn(matrix, colNum);
        list.removeIf(x -> x == 0);

        // Collapsing Algorithm
        reverseCollapse(list);

        //Remove zeros again
        list.removeIf(x -> x == 0);

        // Pad zeros to the beginning
        Useful.prependZeros(list, size);

        // Set the array column accordingly
        Useful.setMatrixColumn(matrix, colNum, list);
    }

    private void postMoveActions(boolean enable) {
        if (enable) {
            updateEmptyCellInfo();
            generateRandomCell();
            notifyObserver();
        }
    }

    private void updateEmptyCellInfo() {
        for (int  i = 0; i < size; ++i) {
            for (int  j = 0; j < size; ++j) {
                if (matrix[i][j] == 0) {
                    emptyCells.add(new Position(i, j));
                }
            }
        }
        emptyCells.removeIf(p -> matrix[p.x][p.y] != 0);
    }

    private void generateRandomCell() {
        if (!emptyCells.isEmpty()) {
            int len = emptyCells.size();
            int random = RAND.nextInt(len);
            int i = 0;
            Position p = null;
            for (Position pos : emptyCells) {
                if (i == random) {
                    p = pos;
                    break;
                }
                ++i;
            }
            Useful.require(p != null);
            Useful.require(matrix[p.x][p.y] == 0, p.x + "," + p.y + " " + matrix[p.x][p.y]);

            matrix[p.x][p.y] = RAND.nextBoolean() ?  NEXT_BASE : BASE;
            emptyCells.removeIf(pos -> matrix[pos.x][pos.y] != 0);
        }
    }

    private void collapse(ArrayList<Integer> list) {
        for (int i = 0; i < list.size() - 1; ++i) {
            int current = list.get(i);
            if (current == list.get(i + 1)) {
                int newValue = current << 1;
                list.set(i, newValue);
                list.set(i + 1, 0);
            }
        }
    }

    private void reverseCollapse(ArrayList<Integer> list) {
        for (int i = list.size() - 1; i > 0; --i) {
            int current = list.get(i);
            if (current == list.get(i - 1)) {
                int newValue = current << 1;
                list.set(i, newValue);
                list.set(i - 1, 0);
            }
        }
    }

    // Inner immutable Position class
    private static final class Position {
        private final int x;
        private final int y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Position) {
                Position otherPos = (Position) other;
                return x == otherPos.x && y == otherPos.y;
            }
            return false;
        }
    }
}
