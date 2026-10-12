package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

public class SistemaQuindioExpress {
    private final List<Paquete> paquetesPorLlegada = new ArrayList<>();
    private final Map<String, Paquete> paquetesPorCodigo = new HashMap<>();
    private final Set<String> destinos = new HashSet<>();
    private final Set<String> destinosOrdenados = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, List<Paquete>> paquetesPorDestino = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Queue<Paquete> colaLlegada = new LinkedList<>();
    private final PriorityQueue<Paquete> colaPrioridad = new PriorityQueue<>(new ComparadorPrioridad());
    private final Map<Integer, Repartidor> repartidores = new HashMap<>();
    private final ListaSimple<Paquete> historialEntregas = new ListaSimple<>();
    private final Set<String> codigosPendientes = new HashSet<>();
    private final Set<String> codigosAsignados = new HashSet<>();
    private final Map<String, Repartidor> asignaciones = new HashMap<>();
    private final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        SistemaQuindioExpress sistema = new SistemaQuindioExpress();
        sistema.cargarDatosIniciales();
        sistema.iniciarMenu();
    }

    private void cargarDatosIniciales() {
        String[] destinosBase = {"Armenia", "Calarcá", "Salento", "Montenegro", "Quimbaya", "Circasia"};
        double[] pesos = {4.5, 12.0, 3.2, 18.7, 7.4, 5.1, 2.8, 9.3, 6.4, 11.2};
        int[] prioridades = {3, 5, 2, 4, 5, 1, 3, 4, 2, 5};
        int[] tiempos = {30, 45, 25, 20, 18, 55, 32, 27, 40, 22};
        for (int i = 0; i < 30; i++) {
            String codigo = "PQ" + (105 + i * 37);
            String destino = destinosBase[i % destinosBase.length];
            Paquete paquete = new Paquete(codigo, destino, pesos[i % pesos.length], prioridades[i % prioridades.length], tiempos[i % tiempos.length]);
            registrarPaquete(paquete);
        }
        repartidores.put(101, new Repartidor(101, "Laura Gómez", "Armenia", true));
        repartidores.put(102, new Repartidor(102, "Carlos Ramírez", "Calarcá", true));
        repartidores.put(103, new Repartidor(103, "Diana López", "Salento", true));
        repartidores.put(104, new Repartidor(104, "Andrés Marín", "Montenegro", true));
        repartidores.put(105, new Repartidor(105, "Sofía Torres", "Quimbaya", true));
        repartidores.put(106, new Repartidor(106, "Mateo Ruiz", "Circasia", false));
    }

    private boolean registrarPaquete(Paquete paquete) {
        if (paquetesPorCodigo.containsKey(paquete.getCodigo())) return false;
        paquetesPorCodigo.put(paquete.getCodigo(), paquete);
        paquetesPorLlegada.add(paquete);
        colaLlegada.offer(paquete);
        colaPrioridad.offer(paquete);
        codigosPendientes.add(paquete.getCodigo());
        destinos.add(paquete.getDestino());
        destinosOrdenados.add(paquete.getDestino());
        if (!paquetesPorDestino.containsKey(paquete.getDestino())) {
            paquetesPorDestino.put(paquete.getDestino(), new ArrayList<>());
        }
        paquetesPorDestino.get(paquete.getDestino()).add(paquete);
        return true;
    }

    private void iniciarMenu() {
        int opcion = 0;
        while (opcion != 14) {
            System.out.println("\n===== QUINDÍOEXPRESS =====");
            System.out.println("1. Registrar paquete");
            System.out.println("2. Consultar paquete por código");
            System.out.println("3. Mostrar paquetes por orden de llegada");
            System.out.println("4. Mostrar destinos sin repetir");
            System.out.println("5. Mostrar paquetes agrupados por destino");
            System.out.println("6. Despachar siguiente por orden de llegada");
            System.out.println("7. Despachar siguiente por prioridad");
            System.out.println("8. Registrar entrega de paquete");
            System.out.println("9. Mostrar historial de entregas");
            System.out.println("10. Consultas recursivas");
            System.out.println("11. Reportes de ordenamiento y búsqueda binaria");
            System.out.println("12. Registrar repartidor");
            System.out.println("13. Mostrar repartidores");
            System.out.println("14. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();
            switch (opcion) {
                case 1 -> menuRegistrarPaquete();
                case 2 -> consultarPaquete();
                case 3 -> mostrarPaquetesLlegada();
                case 4 -> mostrarDestinos();
                case 5 -> mostrarPaquetesPorDestino();
                case 6 -> despacharPorLlegada();
                case 7 -> despacharPorPrioridad();
                case 8 -> registrarEntrega();
                case 9 -> mostrarHistorial();
                case 10 -> menuRecursividad();
                case 11 -> menuReportes();
                case 12 -> registrarRepartidor();
                case 13 -> mostrarRepartidores();
                case 14 -> System.out.println("Sistema finalizado.");
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private int leerEntero() {
        while (!entrada.hasNextInt()) { entrada.nextLine(); System.out.print("Ingrese un número entero: "); }
        int valor = entrada.nextInt(); entrada.nextLine(); return valor;
    }

    private double leerReal() {
        while (!entrada.hasNextDouble()) { entrada.nextLine(); System.out.print("Ingrese un número válido: "); }
        double valor = entrada.nextDouble(); entrada.nextLine(); return valor;
    }

    private void registrarRepartidor() {
        try {
            System.out.print("Identificación: ");
            int identificacion = leerEntero();
            if (repartidores.containsKey(identificacion)) {
                System.out.println("Ya existe un repartidor con esa identificación.");
                return;
            }
            System.out.print("Nombre completo: ");
            String nombre = entrada.nextLine();
            System.out.print("Zona o municipio: ");
            String zona = entrada.nextLine();
            Repartidor repartidor = new Repartidor(identificacion, nombre, zona, true);
            repartidores.put(identificacion, repartidor);
            System.out.println("Repartidor registrado y disponible.");
        } catch (IllegalArgumentException error) {
            System.out.println("No se pudo registrar: " + error.getMessage());
        }
    }

    private void mostrarRepartidores() {
        if (repartidores.isEmpty()) {
            System.out.println("No hay repartidores registrados.");
            return;
        }
        for (Repartidor repartidor : repartidores.values()) {
            System.out.println(repartidor);
        }
    }

    private void menuRegistrarPaquete() {
        try {
            System.out.print("Código: "); String codigo = entrada.nextLine().trim().toUpperCase();
            if (paquetesPorCodigo.containsKey(codigo)) { System.out.println("Ya existe un paquete con ese código."); return; }
            System.out.print("Destino: "); String destino = entrada.nextLine();
            System.out.print("Peso en kg: "); double peso = leerReal();
            System.out.print("Prioridad (1 a 5): "); int prioridad = leerEntero();
            System.out.print("Tiempo estimado en minutos: "); int tiempo = leerEntero();
            Paquete paquete = new Paquete(codigo, destino, peso, prioridad, tiempo);
            registrarPaquete(paquete);
            System.out.println("Paquete registrado correctamente.");
        } catch (IllegalArgumentException error) { System.out.println("No se pudo registrar: " + error.getMessage()); }
    }

    private void consultarPaquete() {
        System.out.print("Código del paquete: ");
        Paquete paquete = paquetesPorCodigo.get(entrada.nextLine().trim().toUpperCase());
        if (paquete == null) System.out.println("No se encontró el paquete."); else System.out.println(paquete);
    }

    private void mostrarPaquetesLlegada() {
        if (paquetesPorLlegada.isEmpty()) { System.out.println("No hay paquetes registrados."); return; }
        for (Paquete paquete : paquetesPorLlegada) System.out.println(paquete);
    }

    private void mostrarDestinos() {
        System.out.println("Destinos sin repetir: " + destinos);
        System.out.println("Destinos ordenados alfabéticamente: " + destinosOrdenados);
    }

    private void mostrarPaquetesPorDestino() {
        for (String destino : destinosOrdenados) {
            System.out.println("\n" + destino + ":");
            List<Paquete> lista = paquetesPorDestino.get(destino);
            for (Paquete paquete : lista) System.out.println("  " + paquete.getCodigo());
        }
    }

    private void despacharPorLlegada() {
        while (!colaLlegada.isEmpty() && (!codigosPendientes.contains(colaLlegada.peek().getCodigo()) || codigosAsignados.contains(colaLlegada.peek().getCodigo()))) {
            colaLlegada.poll();
        }
        if (colaLlegada.isEmpty()) {
            System.out.println("No hay paquetes pendientes.");
            return;
        }
        Paquete paquete = colaLlegada.peek();
        Repartidor repartidorLlegada = buscarRepartidorDisponible(paquete);
        if (repartidorLlegada == null) {
            System.out.println("No hay repartidor disponible para " + paquete.getDestino() + ". Se conserva el orden de llegada.");
            return;
        }
        colaLlegada.poll();
        asignar(paquete, repartidorLlegada);
        System.out.println("Paquete asignado a " + repartidorLlegada.getNombre() + " respetando el orden de llegada: " + paquete);
    }

    private void despacharPorPrioridad() {
        while (!colaPrioridad.isEmpty() && (!codigosPendientes.contains(colaPrioridad.peek().getCodigo()) || codigosAsignados.contains(colaPrioridad.peek().getCodigo()))) {
            colaPrioridad.poll();
        }
        if (colaPrioridad.isEmpty()) {
            System.out.println("No hay paquetes pendientes.");
            return;
        }
        Paquete paquete = colaPrioridad.peek();
        Repartidor repartidorPrioridad = buscarRepartidorDisponible(paquete);
        if (repartidorPrioridad == null) {
            System.out.println("No hay repartidor disponible para " + paquete.getDestino() + ". Se conserva en la cola de prioridad.");
            return;
        }
        colaPrioridad.poll();
        asignar(paquete, repartidorPrioridad);
        System.out.println("Paquete asignado a " + repartidorPrioridad.getNombre() + " por prioridad: " + paquete);
    }

    private Repartidor buscarRepartidorDisponible(Paquete paquete) {
        for (Repartidor repartidor : repartidores.values()) {
            if (repartidor.estaDisponible() && repartidor.getZona().equalsIgnoreCase(paquete.getDestino())) {
                return repartidor;
            }
        }
        return null;
    }

    private void asignar(Paquete paquete, Repartidor repartidor) {
        repartidor.setDisponible(false);
        codigosAsignados.add(paquete.getCodigo());
        asignaciones.put(paquete.getCodigo(), repartidor);
    }

    private void registrarEntrega() {
        System.out.print("Código del paquete entregado: ");
        String codigo = entrada.nextLine().trim().toUpperCase();
        Paquete paquete = paquetesPorCodigo.get(codigo);
        if (paquete == null || !codigosPendientes.contains(codigo)) { System.out.println("El paquete no existe o ya fue entregado."); return; }
        Repartidor repartidor = asignaciones.get(codigo);
        if (!codigosAsignados.contains(codigo) || repartidor == null) {
            System.out.println("El paquete no ha sido despachado ni tiene un repartidor asignado.");
            return;
        }
        codigosPendientes.remove(codigo);
        codigosAsignados.remove(codigo);
        asignaciones.remove(codigo);
        repartidor.setDisponible(true);
        historialEntregas.agregar(paquete);
        System.out.println("Entrega registrada en el historial.");
    }

    private void mostrarHistorial() {
        if (historialEntregas.estaVacia()) { System.out.println("El historial está vacío."); return; }
        for (Paquete paquete : historialEntregas) System.out.println(paquete.getCodigo());
        System.out.println("Cantidad de entregas: " + historialEntregas.tamano());
    }

    private void menuRecursividad() {
        System.out.println("1. Peso total por destino");
        System.out.println("2. Contar paquetes con prioridad mínima");
        System.out.print("Seleccione: ");
        int opcion = leerEntero();
        if (opcion == 1) {
            System.out.print("Destino: "); String destino = entrada.nextLine();
            System.out.println("Peso total: " + sumarPesoPorDestino(paquetesPorLlegada, destino, 0) + " kg");
        } else if (opcion == 2) {
            System.out.print("Prioridad mínima: "); int prioridad = leerEntero();
            System.out.println("Cantidad: " + contarPrioridadMinima(paquetesPorLlegada, prioridad, 0));
        } else System.out.println("Opción inválida.");
    }

    private double sumarPesoPorDestino(List<Paquete> lista, String destino, int indice) {
        if (indice >= lista.size()) return 0.0;
        double pesoActual = 0.0;
        if (lista.get(indice).getDestino().equalsIgnoreCase(destino)) pesoActual = lista.get(indice).getPeso();
        return pesoActual + sumarPesoPorDestino(lista, destino, indice + 1);
    }

    private int contarPrioridadMinima(List<Paquete> lista, int prioridadMinima, int indice) {
        if (indice >= lista.size()) return 0;
        int cuentaActual = 0;
        if (lista.get(indice).getPrioridad() >= prioridadMinima) cuentaActual = 1;
        return cuentaActual + contarPrioridadMinima(lista, prioridadMinima, indice + 1);
    }

    private void menuReportes() {
        System.out.println("1. Buscar paquete mediante búsqueda binaria");
        System.out.println("2. Ordenar paquetes por prioridad");
        System.out.println("3. Ordenar paquetes por peso");
        System.out.println("4. Ordenar paquetes por tiempo estimado");
        System.out.println("5. Paquete de mayor peso (divide y vencerás)");
        System.out.print("Seleccione: ");
        int opcion = leerEntero();
        List<Paquete> copia = new ArrayList<>(paquetesPorLlegada);
        if (opcion == 1) {
            Collections.sort(copia);
            System.out.print("Código a buscar: "); String codigo = entrada.nextLine().trim().toUpperCase();
            int posicion = busquedaBinaria(copia, codigo, 0, copia.size() - 1);
            if (posicion == -1) System.out.println("Paquete no encontrado."); else System.out.println(copia.get(posicion));
        } else if (opcion == 2) {
            copia.sort(new ComparadorPrioridad()); mostrarLista(copia);
        } else if (opcion == 3) {
            copia.sort(new ComparadorPeso()); mostrarLista(copia);
        } else if (opcion == 4) {
            copia.sort(new ComparadorTiempo()); mostrarLista(copia);
        } else if (opcion == 5) {
            Paquete mayor = paqueteMayorPeso(copia, 0, copia.size() - 1);
            System.out.println("Paquete de mayor peso: " + mayor);
        } else System.out.println("Opción inválida.");
    }

    private int busquedaBinaria(List<Paquete> lista, String codigo, int izquierda, int derecha) {
        if (izquierda > derecha) return -1;
        int medio = izquierda + (derecha - izquierda) / 2;
        int comparacion = lista.get(medio).getCodigo().compareToIgnoreCase(codigo);
        if (comparacion == 0) return medio;
        if (comparacion < 0) return busquedaBinaria(lista, codigo, medio + 1, derecha);
        return busquedaBinaria(lista, codigo, izquierda, medio - 1);
    }

    private Paquete paqueteMayorPeso(List<Paquete> lista, int izquierda, int derecha) {
        if (izquierda == derecha) return lista.get(izquierda);
        int medio = izquierda + (derecha - izquierda) / 2;
        Paquete mayorIzquierdo = paqueteMayorPeso(lista, izquierda, medio);
        Paquete mayorDerecho = paqueteMayorPeso(lista, medio + 1, derecha);
        if (mayorIzquierdo.getPeso() >= mayorDerecho.getPeso()) return mayorIzquierdo;
        return mayorDerecho;
    }

    private void mostrarLista(List<Paquete> lista) {
        for (Paquete paquete : lista) System.out.println(paquete);
    }
}