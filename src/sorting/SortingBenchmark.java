package sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Benchmarks six sorting algorithms, each on a primitive array and on an
 * {@code ArrayList<Integer>}. Every implementation runs in its own thread on its
 * own copy of the same data, and results are collected in a
 * {@link ConcurrentHashMap} so the threads can write safely at the same time.
 *
 * <p>The console interface is in Spanish.
 */
public class SortingBenchmark {

    private static final Map<String, String> BIG_O = Map.of(
            "MergeSort", "O(n log n) todos los casos",
            "Burbuja", "Mejor O(n) | Prom/Peor O(n^2)",
            "Insertion", "Mejor O(n) | Prom/Peor O(n^2)",
            "Selection", "O(n^2) todos los casos",
            "ShellSort", "Mejor O(n log n) | Peor O(n^2)",
            "QuickSort", "Mejor/Prom O(n log n) | Peor O(n^2)"
    );

    /** Collection size used for each round in the time-limit challenge mode. */
    private static final int CHALLENGE_COLLECTION_SIZE = 1000;

    /** One algorithm with both its array and ArrayList implementation. */
    private record Variant(String algorithm, String suffix,
                           Consumer<int[]> arraySort, Consumer<ArrayList<Integer>> listSort) {
        String arrayName() {
            return algorithm + " (Arreglo)" + suffix;
        }

        String listName() {
            return algorithm + " (Lista)" + suffix;
        }
    }

    private static final List<Variant> VARIANTS = List.of(
            new Variant("MergeSort", "", MergeSort::mergeSort, MergeSort::mergeSort),
            new Variant("Burbuja", "", BubbleSort::bubbleSort, BubbleSort::bubbleSort),
            new Variant("Insertion", "", InsertionSort::insertionSort, InsertionSort::insertionSort),
            new Variant("Selection", "", SelectionSort::selectionSort, SelectionSort::selectionSort),
            new Variant("ShellSort", "", ShellSort::shellSort, ShellSort::shellSort),
            new Variant("QuickSort", "", QuickSort::quickSort, QuickSort::quickSort),
            new Variant("MergeSort", " SinInsert", MergeSort::mergeSortPure, MergeSort::mergeSortPure)
    );

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean seguir = true;

        while (seguir) {
            System.out.println();
            System.out.println("Elige una opcion:");
            System.out.println("1. 100 elementos aleatorios");
            System.out.println("2. 50,000 elementos aleatorios");
            System.out.println("3. 100,000 elementos aleatorios");
            System.out.println("4. 100,000 elementos restringidos entre 1 y 5");
            System.out.println("5. Ingrese el numero de elementos");
            System.out.println("6. Reto extra (ingresar tiempo limite)");

            int opcion = leerEnteroValido(sc, "Ingresa el numero de tu opcion (1-6):");

            switch (opcion) {
                case 1 -> ejecutarPruebas(100, false);
                case 2 -> ejecutarPruebas(50000, false);
                case 3 -> ejecutarPruebas(100000, false);
                case 4 -> ejecutarPruebas(100000, true);
                case 5 -> {
                    int n = leerEnteroValido(sc, "Ingresa el numero de elementos:");
                    ejecutarPruebas(n, false);
                }
                case 6 -> {
                    int segundos = leerEnteroValido(sc, "Cuantos segundos quieres darle a cada implementacion?");
                    modoDesafio(segundos);
                }
                default -> {
                    System.out.println("Opcion invalida, debe ser un numero del 1 al 6.");
                    continue;
                }
            }

            System.out.println();
            System.out.println("Quieres correr otra prueba? (1 = si, 2 = no)");
            int respuesta = leerEnteroValido(sc, "Ingresa 1 o 2:");
            seguir = (respuesta == 1);
        }

        System.out.println("Programa terminado.");
        sc.close();
    }

    /** Keeps asking until the user types a positive integer. */
    public static int leerEnteroValido(Scanner sc, String mensaje) {
        int valor = -1;
        boolean valido = false;
        while (!valido) {
            System.out.println(mensaje);
            if (sc.hasNextInt()) {
                valor = sc.nextInt();
                if (valor > 0) {
                    valido = true;
                } else {
                    System.out.println("Debe ser un numero entero positivo.");
                }
            } else {
                System.out.println("Eso no es un numero entero valido.");
                sc.next();
            }
        }
        return valor;
    }

    /**
     * Sorts the same random data with every implementation (one thread each),
     * then prints a table ordered from fastest to slowest.
     *
     * @param n          number of elements
     * @param restringido if true, values are limited to 1-5 (many duplicates)
     */
    public static void ejecutarPruebas(int n, boolean restringido) {
        ConcurrentHashMap<String, Double> resultados = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Boolean> ordenados = new ConcurrentHashMap<>();
        Random rand = new Random();

        int[] arregloOG = new int[n];
        ArrayList<Integer> listaOG = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int valor = restringido ? (rand.nextInt(5) + 1) : rand.nextInt();
            arregloOG[i] = valor;
            listaOG.add(valor);
        }

        List<Thread> hilos = new ArrayList<>();
        for (Variant v : VARIANTS) {
            hilos.add(hiloDePrueba(v.arrayName(), v.arraySort(), arregloOG::clone,
                    SortingBenchmark::estaOrdenado, resultados, ordenados));
            hilos.add(hiloDePrueba(v.listName(), v.listSort(), () -> new ArrayList<>(listaOG),
                    SortingBenchmark::estaOrdenado, resultados, ordenados));
        }
        iniciarYEsperar(hilos);

        List<Map.Entry<String, Double>> lista = new ArrayList<>(resultados.entrySet());
        lista.sort((a, b) -> Double.compare(a.getValue(), b.getValue()));

        System.out.println();
        System.out.println("RESULTADOS DE ORDENAMIENTO");
        System.out.println("Elementos: " + n + (restringido ? " (restringido 1-5)" : ""));
        System.out.println();
        System.out.printf("%-5s %-30s %-15s %-10s %-40s%n", "Pos.", "Algoritmo", "Tiempo (ms)", "Ordeno?", "Big O");

        int posicion = 1;
        for (Map.Entry<String, Double> entrada : lista) {
            String nombre = entrada.getKey();
            boolean ok = ordenados.get(nombre);
            String algoritmo = nombre.split(" ")[0];
            System.out.printf("%-5d %-30s %-15.4f %-10s %-40s%n",
                    posicion, nombre, entrada.getValue(), ok ? "Si" : "No", BIG_O.get(algoritmo));
            posicion++;
        }

        System.out.println();
        System.out.println("Implementacion con menor tiempo: " + lista.get(0).getKey());
    }

    /**
     * Challenge mode: each implementation sorts as many fresh random collections
     * as it can before the time limit runs out.
     */
    public static void modoDesafio(int segundos) {
        ConcurrentHashMap<String, Integer> colecciones = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Double> promedios = new ConcurrentHashMap<>();
        long limiteNs = segundos * 1_000_000_000L;

        List<Thread> hilos = new ArrayList<>();
        for (Variant v : VARIANTS) {
            hilos.add(hiloDesafio(v.arrayName(), limiteNs, generadorArreglo(new Random()),
                    v.arraySort(), colecciones, promedios));
            hilos.add(hiloDesafio(v.listName(), limiteNs, generadorLista(new Random()),
                    v.listSort(), colecciones, promedios));
        }
        iniciarYEsperar(hilos);

        List<Map.Entry<String, Integer>> lista = new ArrayList<>(colecciones.entrySet());
        lista.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // most collections first

        System.out.println();
        System.out.println("MODO DESAFIO - Limite: " + segundos + " segundos, Tamano por coleccion: "
                + CHALLENGE_COLLECTION_SIZE);
        System.out.printf("%-30s %-15s %-20s%n", "Algoritmo", "Colecciones", "Tiempo promedio (ms)");
        for (Map.Entry<String, Integer> entrada : lista) {
            String nombre = entrada.getKey();
            System.out.printf("%-30s %-15d %-20.4f%n", nombre, entrada.getValue(), promedios.get(nombre));
        }
    }

    /** Thread that sorts one private copy of the data and records time and correctness. */
    private static <T> Thread hiloDePrueba(String nombre, Consumer<T> sorter, Supplier<T> copiador,
                                           Function<T, Boolean> verificador,
                                           Map<String, Double> resultados, Map<String, Boolean> ordenados) {
        return new Thread(() -> {
            T copia = copiador.get();
            long inicio = System.nanoTime();
            sorter.accept(copia);
            long fin = System.nanoTime();
            resultados.put(nombre, (fin - inicio) / 1_000_000.0);
            ordenados.put(nombre, verificador.apply(copia));
        }, nombre);
    }

    /** Thread for challenge mode: sort fresh collections until the time limit is reached. */
    private static <T> Thread hiloDesafio(String nombre, long limiteNs, Supplier<T> generador, Consumer<T> sorter,
                                          Map<String, Integer> colecciones, Map<String, Double> promedios) {
        return new Thread(() -> {
            int contador = 0;
            double tiempoTotalMs = 0;
            long inicioGlobal = System.nanoTime();
            while (System.nanoTime() - inicioGlobal < limiteNs) {
                T datos = generador.get();
                long inicio = System.nanoTime();
                sorter.accept(datos);
                long fin = System.nanoTime();
                tiempoTotalMs += (fin - inicio) / 1_000_000.0;
                contador++;
            }
            colecciones.put(nombre, contador);
            promedios.put(nombre, contador == 0 ? 0.0 : tiempoTotalMs / contador);
        }, nombre);
    }

    private static Supplier<int[]> generadorArreglo(Random rand) {
        return () -> {
            int[] datos = new int[CHALLENGE_COLLECTION_SIZE];
            for (int i = 0; i < datos.length; i++) {
                datos[i] = rand.nextInt();
            }
            return datos;
        };
    }

    private static Supplier<ArrayList<Integer>> generadorLista(Random rand) {
        return () -> {
            ArrayList<Integer> datos = new ArrayList<>();
            for (int i = 0; i < CHALLENGE_COLLECTION_SIZE; i++) {
                datos.add(rand.nextInt());
            }
            return datos;
        };
    }

    private static void iniciarYEsperar(List<Thread> hilos) {
        for (Thread h : hilos) {
            h.start();
        }
        for (Thread h : hilos) {
            try {
                h.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public static boolean estaOrdenado(int[] arreglo) {
        for (int i = 0; i < arreglo.length - 1; i++) {
            if (arreglo[i] > arreglo[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public static boolean estaOrdenado(ArrayList<Integer> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            if (lista.get(i) > lista.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
}
