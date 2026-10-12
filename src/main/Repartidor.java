package main;

public class Repartidor {
    private int identificacion;
    private String nombre;
    private String zona;
    private boolean disponible;

    public Repartidor(int identificacion, String nombre, String zona, boolean disponible) {
        if (identificacion <= 0) throw new IllegalArgumentException("La identificación debe ser positiva.");
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (zona == null || zona.trim().isEmpty()) throw new IllegalArgumentException("La zona no puede estar vacía.");
        this.identificacion = identificacion;
        this.nombre = nombre.trim();
        this.zona = zona.trim();
        this.disponible = disponible;
    }
    public int getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getZona() { return zona; }
    public boolean estaDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    @Override
    public String toString() {
        String estado;
        if (disponible) estado = "Sí";
        else estado = "No";
        return identificacion + " | " + nombre + " | Zona: " + zona + " | Disponible: " + estado;
    }
}

