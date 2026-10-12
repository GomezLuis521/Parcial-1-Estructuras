package main;

public class Paquete implements Comparable<Paquete> {
    private String codigo;
    private String destino;
    private double peso;
    private int prioridad;
    private int tiempoEstimado;

    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("El código no puede estar vacío.");
        if (destino == null || destino.trim().isEmpty()) throw new IllegalArgumentException("El destino no puede estar vacío.");
        if (peso <= 0) throw new IllegalArgumentException("El peso debe ser mayor que cero.");
        if (prioridad < 1 || prioridad > 5) throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5.");
        if (tiempoEstimado <= 0) throw new IllegalArgumentException("El tiempo estimado debe ser positivo.");
        this.codigo = codigo.trim().toUpperCase();
        this.destino = destino.trim();
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getCodigo() { return codigo; }
    public String getDestino() { return destino; }
    public double getPeso() { return peso; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoEstimado() { return tiempoEstimado; }

    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareToIgnoreCase(otro.codigo);
    }

    @Override
    public String toString() {
        return codigo + " | Destino: " + destino + " | Peso: " + peso + " kg | Prioridad: " + prioridad + " | Tiempo: " + tiempoEstimado + " min";
    }
}
