# Sorting Algorithm Benchmark (Java)

A multithreaded Java program that implements six classic sorting algorithms, runs each one on both a primitive `int[]` array and an `ArrayList<Integer>`, and compares their real running times side by side.

Built as a team project for a Data Structures course (Universidad TecMilenio).

## What it does

- Implements **6 sorting algorithms** from scratch (no `Arrays.sort` / `Collections.sort`): Merge Sort, Bubble Sort, Insertion Sort, Selection Sort, Shell Sort and Quick Sort.
- Runs each algorithm on **both an array and an `ArrayList`**, plus a second Merge Sort variant (hybrid with insertion sort vs. pure), for **14 implementations** in total.
- Gives every implementation **its own thread** and its own copy of the same data, so they never interfere with each other.
- Times only the sorting itself with `System.nanoTime()`, verifies that each result is actually sorted, and prints a table ranked from fastest to slowest, with the Big O complexity of each algorithm.
- Includes a **challenge mode**: each implementation sorts as many fresh 1,000-element collections as it can within a time limit you choose.

## Algorithms

| Algorithm | Best | Average | Worst | Notes |
|---|---|---|---|---|
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | Hybrid version switches to insertion sort for pieces of 15 elements or fewer; a pure version is included for comparison |
| Bubble Sort | O(n) | O(n²) | O(n²) | Early exit when a pass makes no swaps |
| Insertion Sort | O(n) | O(n²) | O(n²) | |
| Selection Sort | O(n²) | O(n²) | O(n²) | |
| Shell Sort | O(n log n) | depends on gaps | O(n²) | n/2, n/4, ... gap sequence |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | Middle pivot with three-way partitioning |

## How to run

Requires JDK 17 or newer.

```bash
javac -d out src/sorting/*.java
java -cp out sorting.SortingBenchmark
```

The menu lets you choose 100, 50,000 or 100,000 random elements, 100,000 elements restricted to the values 1-5 (lots of duplicates), a custom size, or the time-limit challenge. The console interface is in Spanish.

To run the correctness tests (every algorithm checked against `Arrays.sort` on random, sorted, reversed, duplicate-heavy and tiny inputs):

```bash
java -cp out sorting.SortingSelfTest
```

## Example output

Example run with 20,000 elements (timings vary by machine and by run):

```
Pos.  Algoritmo                      Tiempo (ms)     Ordeno?    Big O
1     MergeSort (Arreglo) SinInsert  76.9398         Si         O(n log n) todos los casos
2     MergeSort (Arreglo)            78.1060         Si         O(n log n) todos los casos
3     ShellSort (Arreglo)            101.0594        Si         Mejor O(n log n) | Peor O(n^2)
4     QuickSort (Arreglo)            116.9419        Si         Mejor/Prom O(n log n) | Peor O(n^2)
...
14    Burbuja (Lista)                3459.4484       Si         Mejor O(n) | Prom/Peor O(n^2)
```

## Findings

With 100,000 random elements (run on the team's machine, see the report for all tables):

- The O(n log n) algorithms finished in roughly 8-40 ms, while the O(n²) ones took from about 0.9 s (Insertion on an array) up to about 39 s (Bubble on an `ArrayList`).
- The array version was faster than the `ArrayList` version for all six algorithms, because arrays hold primitives in contiguous memory while `ArrayList` pays for boxing and `get`/`set` calls on every access.
- With 100,000 values restricted to 1-5, Quick Sort was by far the fastest (0.77 ms on an array). Its three-way partitioning groups all the elements equal to the pivot in a single pass and never touches them again.
- Algorithm choice mattered much more than data structure choice.

## Concurrency design

- Each implementation runs in its own `Thread` on its own copy of the data (`clone()` / copy constructor), so no two threads ever modify the same collection.
- Results are stored in a `ConcurrentHashMap`, which allows many threads to write at the same time safely (a plain `HashMap` does not).
- The main thread starts all threads with `start()` and waits for all of them with `join()` before printing the results.
- Because all threads compete for the same CPU cores, absolute times change from run to run. The results are best read as a comparison of trends, not as isolated measurements.

## Project structure

```
src/sorting/
  SortingBenchmark.java   menu, thread creation, results table, challenge mode
  MergeSort.java          hybrid and pure merge sort (array and ArrayList)
  BubbleSort.java
  InsertionSort.java
  SelectionSort.java
  ShellSort.java
  QuickSort.java
  SortUtils.java          shared swap helpers
  SortingSelfTest.java    correctness tests
docs/
  Evidencia_Final_Report.docx   full report with all result tables (in Spanish)
```

## Team

Developed by Luis Rodrigo Montúfar Valdés, Leonardo Marcos Montalvo and Patricio René Amaya Trejo.
