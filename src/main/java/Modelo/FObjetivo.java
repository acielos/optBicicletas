package main.java.Modelo;

public class FObjetivo {
    private float distanciaManhattan;
    private float alpha;
    private float entropia;

    public float calculaFuncion(float distanciaManhattan, float alpha, float entropia) {
        float funcionObjetivo = distanciaManhattan + alpha + entropia;
        return funcionObjetivo;
    }
}
