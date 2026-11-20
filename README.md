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

Results from the team's runs (full tables for 100, 50,000 and 100,000 elements, the 1-5 case and the challenge mode are in the report):

- **Complexity shows up as soon as the data grows.** With 100 elements every implementation finished in under 0.1 ms, so the ranking is mostly noise (an O(n²) Insertion Sort on an array came first). With 100,000 random elements the O(n log n) algorithms took about 8-37 ms, while the O(n²) ones took from about 0.8 s (Insertion on an array) to about 35 s (Bubble on an `ArrayList`), roughly 4,400 times slower than the fastest (Quick Sort on an array).
- **Doubling the input roughly quadruples the O(n²) time.** From 50,000 to 100,000 elements, five of the six O(n²) implementations got 3.3-3.9x slower, close to the factor of 4 that quadratic growth predicts (Bubble Sort on an array was an 8.4x outlier). The O(n log n) ones grew only 1.2-3.0x.
- **Arrays beat `ArrayList`.** With 50,000+ elements and in the challenge mode, all six algorithms were faster on arrays, by 1.4x up to about 18x (Insertion Sort, 100,000 elements), because arrays hold primitives in contiguous memory while `ArrayList` pays for boxing and `get`/`set` calls on every access.
- **Duplicates favor three-way Quick Sort.** With 100,000 values restricted to 1-5, Quick Sort on an array took 0.91 ms versus 7.9 ms on random data, since every element equal to the pivot is placed once and never touched again.
- **Hybrid Merge Sort beats the pure one on arrays.** Switching to insertion sort for pieces of 15 elements or fewer made Merge Sort 1.8-2.2x faster on arrays with 50,000+ elements; on `ArrayList` the results were mixed.
- **Algorithm choice mattered much more than data structure choice.**
- **Timings are noisy.** All 14 threads compete for the same CPU, and a few results (for example Quick Sort on an `ArrayList` in the 1-5 test) were much slower than the algorithm explains. Read the numbers as trends, not exact measurements.

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
