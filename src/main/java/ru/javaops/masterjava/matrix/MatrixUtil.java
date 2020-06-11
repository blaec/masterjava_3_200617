package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < matrixSize; i++) {
            final int row = i;
            Future<?> submit = executor.submit(() -> {
                for (int j = 0; j < matrixSize; j++) {
                    matrixC[row][j] = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        matrixC[row][j] += matrixA[row][k] * matrixB[k][j];
                    }
                }
            });
            futures.add(submit);
        }
        try {
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int[] thatColumn = new int[matrixSize];
        for (int bCol = 0; bCol < matrixSize; bCol++) {
            for (int aCol = 0; aCol < matrixSize; aCol++) {
                thatColumn[aCol] = matrixB[aCol][bCol];
            }

            for (int aRow = 0; aRow < matrixSize; aRow++) {
                int[] thisRow = matrixA[aRow];
                int summand = 0;
                for (int aCol = 0; aCol < matrixSize; aCol++) {
                    summand += thisRow[aCol] * thatColumn[aCol];
                }
                matrixC[aRow][bCol] = summand;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
