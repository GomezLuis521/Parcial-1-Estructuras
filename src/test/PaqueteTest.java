package test;

import main.Paquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaqueteTest {

    private Paquete paquete;

    @BeforeEach
    void setUp() {
        paquete = new Paquete(" pq105 ", " Armenia ", 4.5, 3, 30);
    }

    @Test
    void testNormalizaCodigoYConservaDatos() {
        assertEquals("PQ105", paquete.getCodigo());
        assertEquals("Armenia", paquete.getDestino());
        assertEquals(4.5, paquete.getPeso());
        assertEquals(3, paquete.getPrioridad());
        assertEquals(30, paquete.getTiempoEstimado());
    }

    @Test
    void testRechazaCodigoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paquete("  ", "Armenia", 1, 1, 10));
    }

    @Test
    void testRechazaPesoNoPositivo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paquete("PQ1", "Armenia", 0, 1, 10));
    }

    @Test
    void testRechazaPrioridadFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paquete("PQ1", "Armenia", 1, 6, 10));
    }

    @Test
    void testRechazaTiempoNoPositivo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paquete("PQ1", "Armenia", 1, 1, 0));
    }

    @Test
    void testOrdenNaturalPorCodigo() {
        Paquete otroPaquete = new Paquete("PQ142", "Calarca", 2, 2, 20);
        assertTrue(paquete.compareTo(otroPaquete) < 0);
    }

    @Test
    void testToStringIncluyeDatos() {
        String resultadoToString = paquete.toString();
        assertTrue(resultadoToString.contains("PQ105"));
        assertTrue(resultadoToString.contains("Armenia"));
    }
}