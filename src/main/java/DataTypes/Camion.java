package main.java.DataTypes;

public class Camion {
    private int capacidad = 20;
    public int carga = 0;

    // Getter para la capacidad, que no la vamos a modificar
    public int getCapacidad() {
        return capacidad;
    }

    public void reset() {this.carga = 7;}
}
