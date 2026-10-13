package test;

import main.Repartidor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepartitionTest {

    private Repartidor repartidor;

    @BeforeEach
    void setUp() {
        repartidor = new Repartidor(101, " Laura Gómez ", " Armenia ", true);
    }

    @Test
    void testConservaDatosYRecortaEspacios() {
        assertEquals(101, repartidor.getIdentificacion());
        assertEquals("Laura Gómez", repartidor.getNombre());
        assertEquals("Armenia", repartidor.getZona());
        assertTrue(repartidor.estaDisponible());
    }

    @Test
    void testCambiarDisponibilidad() {
        repartidor.setDisponible(false);
        assertFalse(repartidor.estaDisponible());

        repartidor.setDisponible(true);
        assertTrue(repartidor.estaDisponible());
    }

    @Test
    void testRechazaIdentificacionNoPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> new Repartidor(0, "Ana", "Armenia", true));
    }

    @Test
    void testRechazaNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Repartidor(1, " ", "Armenia", true));
    }

    @Test
    void testRechazaZonaVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new Repartidor(1, "Ana", " ", true));
    }

    @Test
    void testToStringMuestraDisponibilidad() {
        assertTrue(repartidor.toString().contains("Disponible: Sí"));

        repartidor.setDisponible(false);
        assertTrue(repartidor.toString().contains("Disponible: No"));
    }
}