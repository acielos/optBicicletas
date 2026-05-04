package main.java.DataTypes;

public class Estacion {
    // Atributos de la clase punto
    public int id, capacidad, carga;
    public double latitud;
    public double longitud;

    // Constructor de la clase
    public Estacion(int id, double latitud, double longitud) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Damos formato a la visualización de la estación (en la terminal, en mapa ya buscamos si eso)
    public String toString(){
        return String.format("%d (%.10f, %.10f), %d, %d", id, latitud, longitud, carga, capacidad);
    }
}
