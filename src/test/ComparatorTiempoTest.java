package test;

import main.ComparadorTiempo;
import main.Paquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ComparatorTiempoTest {
    private ComparadorTiempo comparador;
    @BeforeEach void setUp() { comparador = new ComparadorTiempo(); }
    @Test void testOrdenaTiempoDeMenorAMayor() {
        Paquete rapido = new Paquete("PQ1", "A", 1, 1, 10), lento = new Paquete("PQ2", "A", 1, 1, 50);
        assertTrue(comparador.compare(rapido, lento) < 0);
    }
    @Test void testOrdenaListaCompleta() {
        List<Paquete> paquetes = new ArrayList<>(List.of(new Paquete("PQ1", "A", 1, 1, 30),
                new Paquete("PQ2", "A", 1, 1, 10), new Paquete("PQ3", "A", 1, 1, 20)));
        paquetes.sort(comparador);
        assertEquals(List.of(10, 20, 30), paquetes.stream().map(Paquete::getTiempoEstimado).toList());
    }
}