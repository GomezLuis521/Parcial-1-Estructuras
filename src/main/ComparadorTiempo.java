package main;

import java.util.Comparator;

public class ComparadorTiempo implements Comparator<Paquete> {
    @Override
    public int compare(Paquete primero, Paquete segundo) {
        return Integer.compare(primero.getTiempoEstimado(), segundo.getTiempoEstimado());
    }
}

