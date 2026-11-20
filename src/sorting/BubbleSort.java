package sorting;

import java.util.ArrayList;

/**
 * Bubble sort with an early exit: if a full pass makes no swaps the data is
 * already sorted. Best case O(n), average and worst case O(n^2).
 */
public final class BubbleSort {

    private BubbleSort() {
    }

    public static void bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    SortUtils.swap(array, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                return;
            }
        }
    }

    public static void bubbleSort(ArrayList<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    SortUtils.swap(list, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                return;
            }
        }
    }
}
