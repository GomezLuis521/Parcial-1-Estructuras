package test;

import main.ComparadorPrioridad;
import main.Paquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ComparatorPrioritisedTest {
    private ComparadorPrioridad comparador;
    @BeforeEach void setUp() { comparador = new ComparadorPrioridad(); }
    @Test void testOrdenaPrioridadMayorAMenor() {
        Paquete alta = new Paquete("PQ1", "A", 1, 5, 50), baja = new Paquete("PQ2", "A", 1, 2, 10);
        assertTrue(comparador.compare(alta, baja) < 0);
    }
    @Test void testDesempataPorMenorTiempo() {
        Paquete rapido = new Paquete("PQ1", "A", 1, 5, 10), lento = new Paquete("PQ2", "A", 1, 5, 20);
        assertTrue(comparador.compare(rapido, lento) < 0);
    }
    @Test void testDesempataPorCodigo() {
        Paquete primero = new Paquete("PQ1", "A", 1, 5, 10), segundo = new Paquete("PQ2", "A", 1, 5, 10);
        assertTrue(comparador.compare(primero, segundo) < 0);
    }
    @Test void testOrdenaListaCompleta() {
        List<Paquete> paquetes = new ArrayList<>(List.of(new Paquete("PQ3", "A", 1, 2, 10),
                new Paquete("PQ2", "A", 1, 5, 20), new Paquete("PQ1", "A", 1, 5, 10)));
        paquetes.sort(comparador);
        assertEquals(List.of("PQ1", "PQ2", "PQ3"), paquetes.stream().map(Paquete::getCodigo).toList());
    }
}