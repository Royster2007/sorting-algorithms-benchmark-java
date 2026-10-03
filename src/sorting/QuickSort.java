package sorting;

import java.util.ArrayList;

/**
 * Quick sort with a middle pivot and three-way partitioning (less than, equal
 * to, greater than the pivot). Elements equal to the pivot are never touched
 * again, which makes it very fast on data with many repeated values.
 * Best/average case O(n log n), worst case O(n^2).
 */
public final class QuickSort {

    private QuickSort() {
    }

    public static void quickSort(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    public static void quickSort(int[] array, int start, int end) {
        if (start >= end) {
            return;
        }
        int pivot = array[start + (end - start) / 2];
        int lessEnd = start;
        int i = start;
        int greaterStart = end;

        while (i <= greaterStart) {
            if (array[i] < pivot) {
                SortUtils.swap(array, lessEnd, i);
                lessEnd++;
                i++;
            } else if (array[i] > pivot) {
                SortUtils.swap(array, i, greaterStart);
                greaterStart--;
            } else {
                i++;
            }
        }
        quickSort(array, start, lessEnd - 1);
        quickSort(array, greaterStart + 1, end);
    }

    public static void quickSort(ArrayList<Integer> list) {
        quickSort(list, 0, list.size() - 1);
    }

    public static void quickSort(ArrayList<Integer> list, int start, int end) {
        if (start >= end) {
            return;
        }
        int pivot = list.get(start + (end - start) / 2);
        int lessEnd = start;
        int i = start;
        int greaterStart = end;

        while (i <= greaterStart) {
            if (list.get(i) < pivot) {
                SortUtils.swap(list, lessEnd, i);
                lessEnd++;
                i++;
            } else if (list.get(i) > pivot) {
                SortUtils.swap(list, i, greaterStart);
                greaterStart--;
            } else {
                i++;
            }
        }
        quickSort(list, start, lessEnd - 1);
        quickSort(list, greaterStart + 1, end);
    }
}
