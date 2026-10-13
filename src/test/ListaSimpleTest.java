package test;

import main.ColeccionGenerica;
import main.ListaSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;

class ListaSimpleTest {
    private ColeccionGenerica<String> lista;
    @BeforeEach void setUp() { lista = new ListaSimple<>(); }
    @Test void testListaIniciaVacia() { assertTrue(lista.estaVacia()); assertEquals(0, lista.tamano()); }
    @Test void testAgregarConservaOrden() {
        lista.agregar("A"); lista.agregar("B"); lista.agregar("C");
        assertEquals(3, lista.tamano()); assertEquals("A", lista.obtener(0));
        assertEquals("B", lista.obtener(1)); assertEquals("C", lista.obtener(2));
    }
    @Test void testEliminarPrimeroMedioYUltimo() {
        lista.agregar("A"); lista.agregar("B"); lista.agregar("C");
        assertTrue(lista.eliminar("B")); assertTrue(lista.eliminar("A")); assertTrue(lista.eliminar("C"));
        assertTrue(lista.estaVacia()); assertEquals(0, lista.tamano());
    }
    @Test void testEliminarInexistenteNoModificaLista() {
        lista.agregar("A"); assertFalse(lista.eliminar("X")); assertEquals(1, lista.tamano());
        assertEquals("A", lista.obtener(0));
    }
    @Test void testObtenerIndiceFueraDeRangoLanzaExcepcion() {
        lista.agregar("A"); assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(1));
    }
    @Test void testIteradorRecorreTodosLosElementos() {
        lista.agregar("A"); lista.agregar("B"); Iterator<String> it = lista.iterator();
        assertTrue(it.hasNext()); assertEquals("A", it.next()); assertEquals("B", it.next());
        assertFalse(it.hasNext()); assertThrows(java.util.NoSuchElementException.class, it::next);
    }
}