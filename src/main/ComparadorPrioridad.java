package main;

import java.util.Comparator;

public class ComparadorPrioridad implements Comparator<Paquete> {
    @Override
    public int compare(Paquete primero, Paquete segundo) {
        if (primero.getPrioridad() != segundo.getPrioridad()) {
            return Integer.compare(segundo.getPrioridad(), primero.getPrioridad());
        }
        if (primero.getTiempoEstimado() != segundo.getTiempoEstimado()) {
            return Integer.compare(primero.getTiempoEstimado(), segundo.getTiempoEstimado());
        }
        return primero.getCodigo().compareToIgnoreCase(segundo.getCodigo());
    }
}
