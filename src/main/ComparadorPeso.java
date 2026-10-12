package main;

import java.util.Comparator;

public class ComparadorPeso implements Comparator<Paquete> {
    @Override
    public int compare(Paquete primero, Paquete segundo) {
        return Double.compare(segundo.getPeso(), primero.getPeso());
    }
}
