package sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Minimal correctness check (no external libraries): every algorithm is compared
 * against {@link Arrays#sort(int[])} on many inputs, including edge cases.
 *
 * <p>Run with: {@code java -cp out sorting.SortingSelfTest}
 */
public class SortingSelfTest {

    private static int failures = 0;

    public static void main(String[] args) {
        check("MergeSort", MergeSort::mergeSort, MergeSort::mergeSort);
        check("MergeSort (pure)", MergeSort::mergeSortPure, MergeSort::mergeSortPure);
        check("BubbleSort", BubbleSort::bubbleSort, BubbleSort::bubbleSort);
        check("InsertionSort", InsertionSort::insertionSort, InsertionSort::insertionSort);
        check("SelectionSort", SelectionSort::selectionSort, SelectionSort::selectionSort);
        check("ShellSort", ShellSort::shellSort, ShellSort::shellSort);
        check("QuickSort", QuickSort::quickSort, QuickSort::quickSort);

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) FAILED.");
            System.exit(1);
        }
    }

    private static void check(String name, Consumer<int[]> arraySort, Consumer<ArrayList<Integer>> listSort) {
        Random rand = new Random(42);
        for (int size : new int[]{0, 1, 2, 3, 10, 15, 16, 17, 100, 1000}) {
            for (int[] data : inputs(size, rand)) {
                int[] expected = data.clone();
                Arrays.sort(expected);

                int[] array = data.clone();
                arraySort.accept(array);
                if (!Arrays.equals(expected, array)) {
                    fail(name + " (array), size " + size);
                }

                ArrayList<Integer> list = new ArrayList<>();
                for (int v : data) {
                    list.add(v);
                }
                listSort.accept(list);
                if (!list.equals(Arrays.stream(expected).boxed().toList())) {
                    fail(name + " (list), size " + size);
                }
            }
        }
    }

    /** Random, many duplicates (1-5), already sorted, reversed, all equal. */
    private static int[][] inputs(int size, Random rand) {
        int[] random = new int[size];
        int[] duplicates = new int[size];
        int[] sorted = new int[size];
        int[] reversed = new int[size];
        int[] equal = new int[size];
        for (int i = 0; i < size; i++) {
            random[i] = rand.nextInt();
            duplicates[i] = rand.nextInt(5) + 1;
            sorted[i] = i;
            reversed[i] = size - i;
            equal[i] = 7;
        }
        return new int[][]{random, duplicates, sorted, reversed, equal};
    }

    private static void fail(String message) {
        failures++;
        System.out.println("FAILED: " + message);
    }
}
