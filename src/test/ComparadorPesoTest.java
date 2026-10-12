package test;

import main.ComparadorPeso;
import main.Paquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ComparadorPesoTest {
    private ComparadorPeso comparador;
    @BeforeEach void setUp() { comparador = new ComparadorPeso(); }
    @Test void testOrdenaPesoDeMayorAMenor() {
        Paquete pesado = new Paquete("PQ1", "A", 18.7, 1, 10), liviano = new Paquete("PQ2", "A", 2, 1, 10);
        assertTrue(comparador.compare(pesado, liviano) < 0);
    }
    @Test void testOrdenaListaCompleta() {
        List<Paquete> paquetes = new ArrayList<>(List.of(new Paquete("PQ1", "A", 4.5, 1, 10),
                new Paquete("PQ2", "A", 18.7, 1, 10), new Paquete("PQ3", "A", 2, 1, 10)));
        paquetes.sort(comparador);
        assertEquals(List.of(18.7, 4.5, 2.0), paquetes.stream().map(Paquete::getPeso).toList());
    }
}
