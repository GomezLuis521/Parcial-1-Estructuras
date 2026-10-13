package test;

import main.ListaSimple;
import main.Paquete;
import main.SistemaQuindioExpress; // 1. Importar la clase real de tu proyecto

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// 2. Se elimina <SistemaQuindioExpress> del nombre de la clase
class SistemaQuindioExpressTest {

    private SistemaQuindioExpress sistema;
    private final PrintStream salidaOriginal = System.out;

    @BeforeEach
    void setUp() throws Exception {
        sistema = crearSistema("");
    }

    @Test
    void testCargaTreintaPaquetesYSeisRepartidores() throws Exception {
        assertEquals(30, ((Map<?, ?>) campo(sistema, "paquetesPorCodigo")).size());
        assertEquals(6, ((Map<?, ?>) campo(sistema, "repartidores")).size());
    }

    @Test
    void testBusquedaBinariaEncuentraCodigoExistente() throws Exception {
        List<Paquete> paquetes = new ArrayList<>(campo(sistema, "paquetesPorLlegada"));
        Collections.sort(paquetes);

        int indice = (int) invocar(
                sistema,
                "busquedaBinaria",
                new Class<?>[]{List.class, String.class, int.class, int.class},
                paquetes, "PQ179", 0, paquetes.size() - 1
        );

        assertTrue(indice >= 0);
        assertEquals("PQ179", paquetes.get(indice).getCodigo());
    }

    @Test
    void testBusquedaBinariaDevuelveMenosUnoSiNoExiste() throws Exception {
        List<Paquete> paquetes = new ArrayList<>(campo(sistema, "paquetesPorLlegada"));
        Collections.sort(paquetes);

        int indice = (int) invocar(
                sistema,
                "busquedaBinaria",
                new Class<?>[]{List.class, String.class, int.class, int.class},
                paquetes, "PQ999", 0, paquetes.size() - 1
        );

        assertEquals(-1, indice);
    }

    @Test
    void testConsultasRecursivasDevuelvenValoresEsperados() throws Exception {
        List<Paquete> paquetes = campo(sistema, "paquetesPorLlegada");

        double peso = (double) invocar(
                sistema,
                "sumarPesoPorDestino",
                new Class<?>[]{List.class, String.class, int.class},
                paquetes, "Armenia", 0
        );

        int cantidad = (int) invocar(
                sistema,
                "contarPrioridadMinima",
                new Class<?>[]{List.class, int.class, int.class},
                paquetes, 5, 0
        );

        assertEquals(24.3, peso, 0.000001);
        assertEquals(9, cantidad);
    }

    @Test
    void testDespachoPorLlegadaAsignaPrimerPaquete() throws Exception {
        invocar(sistema, "despacharPorLlegada");
        assertTrue(((Set<?>) campo(sistema, "codigosAsignados")).contains("PQ105"));
    }

    @Test
    void testDespachoPorPrioridadAsignaPQ253Primero() throws Exception {
        invocar(sistema, "despacharPorPrioridad");
        assertTrue(((Set<?>) campo(sistema, "codigosAsignados")).contains("PQ253"));
    }

    @Test
    void testNoDebeRegistrarEntregaSinDespacho() throws Exception {
        sistema = crearSistema("PQ105\n");
        invocar(sistema, "registrarEntrega");

        assertTrue(
                ((Set<?>) campo(sistema, "codigosPendientes")).contains("PQ105"),
                "El paquete debe seguir pendiente si nunca fue asignado"
        );
        assertEquals(0, ((ListaSimple<?>) campo(sistema, "historialEntregas")).tamano());
    }

    @Test
    void testAgrupaDestinoAunqueCambieMayusculas() throws Exception {
        invocar(
                sistema,
                "registrarPaquete",
                new Class<?>[]{Paquete.class},
                new Paquete("PX001", "armenia", 1, 1, 10)
        );

        String salida = capturar(() -> invocar(sistema, "mostrarPaquetesPorDestino")).toString();
        assertTrue(salida.contains("PX001"), "El grupo Armenia debe incluir PX001 aunque se registró como armenia");
    }

    private SistemaQuindioExpress crearSistema(String entrada) throws Exception {
        System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
        SistemaQuindioExpress instancia = new SistemaQuindioExpress();
        invocar(instancia, "cargarDatosIniciales");
        return instancia;
    }

    private Object capturar(Accion accion) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            accion.ejecutar();
        } finally {
            System.setOut(salidaOriginal);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private Object invocar(Object objeto, String nombre) throws Exception {
        return invocar(objeto, nombre, new Class<?>[0]);
    }

    private Object invocar(Object objeto, String nombre, Class<?>[] tipos, Object... argumentos) throws Exception {
        Method metodo = objeto.getClass().getDeclaredMethod(nombre, tipos);
        metodo.setAccessible(true);
        try {
            return metodo.invoke(objeto, argumentos);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T campo(Object objeto, String nombre) throws Exception {
        Field campo = objeto.getClass().getDeclaredField(nombre);
        campo.setAccessible(true);
        return (T) campo.get(objeto);
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws Exception;
    }
}