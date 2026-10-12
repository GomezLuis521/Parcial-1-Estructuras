package main;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ListaSimple<T> implements ColeccionGenerica<T> {
    private Nodo<T> primero;
    private int cantidad;

    private static class Nodo<E> {
        private E valor;
        private Nodo<E> siguiente;
        private Nodo(E valor) { this.valor = valor; }
    }

    @Override
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (primero == null) {
            primero = nuevo;
        } else {
            Nodo<T> actual = primero;
            while (actual.siguiente != null) actual = actual.siguiente;
            actual.siguiente = nuevo;
        }
        cantidad++;
    }

    @Override
    public boolean eliminar(T elemento) {
        if (primero == null) return false;
        if (Objects.equals(primero.valor, elemento)) {
            primero = primero.siguiente;
            cantidad--;
            return true;
        }
        Nodo<T> anterior = primero;
        Nodo<T> actual = primero.siguiente;
        while (actual != null) {
            if (Objects.equals(actual.valor, elemento)) {
                anterior.siguiente = actual.siguiente;
                cantidad--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    @Override
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= cantidad) throw new IndexOutOfBoundsException("Posición fuera de la lista.");
        Nodo<T> actual = primero;
        for (int i = 0; i < posicion; i++) actual = actual.siguiente;
        return actual.valor;
    }

    @Override public int tamano() { return cantidad; }
    @Override public boolean estaVacia() { return cantidad == 0; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = primero;
            @Override public boolean hasNext() { return actual != null; }
            @Override public T next() {
                if (actual == null) throw new NoSuchElementException();
                T valor = actual.valor;
                actual = actual.siguiente;
                return valor;
            }
        };
    }
}
