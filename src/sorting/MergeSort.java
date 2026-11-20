package sorting;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Merge sort: split the data in half until the pieces are tiny, then merge the
 * sorted halves back together. O(n log n) in every case.
 *
 * <p>Two variants are provided so they can be compared:
 * <ul>
 *   <li>{@code mergeSort}: hybrid, switches to insertion sort for pieces of
 *       {@value #INSERTION_CUTOFF} elements or fewer.</li>
 *   <li>{@code mergeSortPure}: plain merge sort, splits all the way down to
 *       single elements.</li>
 * </ul>
 */
public final class MergeSort {

    private static final int INSERTION_CUTOFF = 15;

    private MergeSort() {
    }

    public static int[] mergeSort(int[] array) {
        return sort(array, true);
    }

    public static int[] mergeSortPure(int[] array) {
        return sort(array, false);
    }

    public static ArrayList<Integer> mergeSort(ArrayList<Integer> list) {
        return sort(list, true);
    }

    public static ArrayList<Integer> mergeSortPure(ArrayList<Integer> list) {
        return sort(list, false);
    }

    private static int[] sort(int[] array, boolean hybrid) {
        if (array.length <= 1) {
            return array;
        }
        if (hybrid && array.length <= INSERTION_CUTOFF) {
            InsertionSort.insertionSort(array);
            return array;
        }
        int mid = array.length / 2;
        int[] left = sort(Arrays.copyOfRange(array, 0, mid), hybrid);
        int[] right = sort(Arrays.copyOfRange(array, mid, array.length), hybrid);
        merge(array, left, right);
        return array;
    }

    private static ArrayList<Integer> sort(ArrayList<Integer> list, boolean hybrid) {
        if (list.size() <= 1) {
            return list;
        }
        if (hybrid && list.size() <= INSERTION_CUTOFF) {
            InsertionSort.insertionSort(list);
            return list;
        }
        int mid = list.size() / 2;
        ArrayList<Integer> left = sort(new ArrayList<>(list.subList(0, mid)), hybrid);
        ArrayList<Integer> right = sort(new ArrayList<>(list.subList(mid, list.size())), hybrid);
        merge(list, left, right);
        return list;
    }

    /** Merges two sorted arrays into {@code target}. Skips the merge if they are already in order. */
    private static void merge(int[] target, int[] left, int[] right) {
        int l = 0;
        int r = 0;
        int t = 0;
        if (left[left.length - 1] > right[0]) {
            while (l < left.length && r < right.length) {
                target[t++] = left[l] > right[r] ? right[r++] : left[l++];
            }
        }
        while (l < left.length) {
            target[t++] = left[l++];
        }
        while (r < right.length) {
            target[t++] = right[r++];
        }
    }

    private static void merge(ArrayList<Integer> target, ArrayList<Integer> left, ArrayList<Integer> right) {
        int l = 0;
        int r = 0;
        int t = 0;
        if (left.get(left.size() - 1) > right.get(0)) {
            while (l < left.size() && r < right.size()) {
                target.set(t++, left.get(l) > right.get(r) ? right.get(r++) : left.get(l++));
            }
        }
        while (l < left.size()) {
            target.set(t++, left.get(l++));
        }
        while (r < right.size()) {
            target.set(t++, right.get(r++));
        }
    }
}
