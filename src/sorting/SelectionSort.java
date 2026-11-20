package sorting;

import java.util.ArrayList;

/** Selection sort. O(n^2) in every case. */
public final class SelectionSort {

    private SelectionSort() {
    }

    public static void selectionSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int indexMin = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[indexMin]) {
                    indexMin = j;
                }
            }
            SortUtils.swap(array, indexMin, i);
        }
    }

    public static void selectionSort(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            int indexMin = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) < list.get(indexMin)) {
                    indexMin = j;
                }
            }
            SortUtils.swap(list, indexMin, i);
        }
    }
}
