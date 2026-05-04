package main.java.DataTypes;

import java.util.List;

public class Casos {

    public int[] caso1Bicis = {5, 7, 13, 6, 8, 13, 8, 9, 6, 10, 10, 18, 8, 13, 15, 14};
    public int[] caso1Capacidad = {16, 16, 23, 13, 21, 17, 14, 13, 17, 16, 27, 18, 12, 18, 18, 19};
    public int[] caso2Bicis = {15, 14, 3, 10, 2, 1, 8, 13, 17, 1, 20, 9, 6, 6, 15, 14};
    public int[] caso2Capacidad = {16, 16, 23, 13, 21, 17, 14, 13, 17, 16, 27, 18, 12, 18, 18, 19};
    public int[] caso3Bicis = {15, 14, 3, 10, 2, 1, 8, 13, 10, 0, 0, 0, 0, 0, 0, 0};
    public int[] caso3Capacidad = {15, 14, 3, 10, 2, 1, 8, 13, 17, 16, 27, 18, 12, 18, 18, 19};

    public void aplicarCaso(List<Estacion> dataset, int numeroCaso) {
        int[] bicis;
        int[] capacidades;

        switch (numeroCaso) {
            case 1 -> { bicis = caso1Bicis;     capacidades = caso1Capacidad; }
            case 2 -> { bicis = caso2Bicis;     capacidades = caso2Capacidad; }
            case 3 -> { bicis = caso3Bicis;     capacidades = caso3Capacidad; }
            default -> throw new IllegalArgumentException("Caso no válido: " + numeroCaso);
        }

        for (int i = 0; i < dataset.size(); i++) {
            dataset.get(i).carga    = bicis[i];
            dataset.get(i).capacidad = capacidades[i];
        }
    }
}
