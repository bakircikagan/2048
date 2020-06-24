import java.util.ArrayList;

public final class Useful {

    private Useful() {}

    public static void require(boolean condition) {
        require(condition, "");
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static ArrayList<Integer> toArrayList(int[] arr) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int x: arr) {
            list.add(x);
        }
        return list;
    }

    public static void setMatrixRow(int[][] matrix, int rowNum, ArrayList<Integer> list) {
        require(matrix != null);
        require(rowNum < matrix.length);
        require(matrix.length == matrix[0].length);
        require(matrix.length == list.size());

        for (int j = 0; j < matrix.length; ++j) {
            matrix[rowNum][j] = list.get(j);
        }
    }

    public static void setMatrixColumn(int[][] matrix, int colNum, ArrayList<Integer> list) {
        require(matrix != null);
        require(colNum < matrix.length);
        require(matrix.length == matrix[0].length);
        require(matrix.length == list.size());

        for (int i = 0; i < matrix.length; ++i) {
            matrix[i][colNum] = list.get(i);
        }
    }

    public static ArrayList<Integer> listFromColumn(int[][] matrix, int colNum) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < matrix.length; ++i) {
            list.add(matrix[i][colNum]);
        }
        return list;
    }

    public static void appendZeros(ArrayList<Integer> list, int size) {
        while(list.size() < size) {
            list.add(0);
        }
    }

    public static void prependZeros(ArrayList<Integer> list, int size) {
        while(list.size() < size) {
            list.add(0, 0);
        }
    }

    public static void waitThreads(Thread[] threads) {
        boolean pass;
        do {
            pass = true;
            for(Thread t : threads) {
                pass = pass && t.getState() == Thread.State.TERMINATED;
            }
        } while(!pass);
    }
}
