package test;

import main.ColeccionGenerica;
import main.ListaSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColeccionGenericaTest {
    private ColeccionGenerica<String> coleccion;
    @BeforeEach void setUp() { coleccion = new ListaSimple<>(); }
    @Test void testAgregarYConsultarPorPosicion() {
        coleccion.agregar("uno"); coleccion.agregar("dos");
        assertEquals(2, coleccion.tamano()); assertEquals("uno", coleccion.obtener(0));
        assertEquals("dos", coleccion.obtener(1));
    }
    @Test void testEliminarActualizaTamanoYEstado() {
        coleccion.agregar("uno");
        assertTrue(coleccion.eliminar("uno")); assertTrue(coleccion.estaVacia());
        assertEquals(0, coleccion.tamano());
    }
}
