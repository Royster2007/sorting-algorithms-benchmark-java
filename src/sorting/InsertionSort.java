package sorting;

import java.util.ArrayList;

/**
 * Insertion sort (swap-based). Best case O(n), average and worst case O(n^2).
 * Also used by {@link MergeSort} for very small sub-arrays.
 */
public final class InsertionSort {

    private InsertionSort() {
    }

    public static void insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int index = i;
            while (index > 0 && array[index] < array[index - 1]) {
                SortUtils.swap(array, index, index - 1);
                index--;
            }
        }
    }

    public static void insertionSort(ArrayList<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            int index = i;
            while (index > 0 && list.get(index) < list.get(index - 1)) {
                SortUtils.swap(list, index, index - 1);
                index--;
            }
        }
    }
}
