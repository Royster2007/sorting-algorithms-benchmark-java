package sorting;

import java.util.ArrayList;

/**
 * Shell sort using the simple n/2, n/4, ... gap sequence. Insertion sort is
 * applied to elements that are {@code gap} positions apart, and the gap shrinks
 * on every round until it reaches 1. Worst case O(n^2); the average case
 * depends on the gap sequence.
 */
public final class ShellSort {

    private ShellSort() {
    }

    public static void shellSort(int[] array) {
        int n = array.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = array[i];
                int j = i;
                while (j >= gap && array[j - gap] > temp) {
                    array[j] = array[j - gap];
                    j -= gap;
                }
                array[j] = temp;
            }
        }
    }

    public static void shellSort(ArrayList<Integer> list) {
        int n = list.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = list.get(i);
                int j = i;
                while (j >= gap && list.get(j - gap) > temp) {
                    list.set(j, list.get(j - gap));
                    j -= gap;
                }
                list.set(j, temp);
            }
        }
    }
}
