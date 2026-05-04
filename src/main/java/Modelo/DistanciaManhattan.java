package main.java.Modelo;

import main.java.DataTypes.Estacion;
import java.util.*;

public class DistanciaManhattan {
    private double distancia_total;
    private double radio = 6371.0;

    public double calculaDistancia(Estacion estacion1, Estacion estacion2) {
        this.distancia_total = 0;
        // Calculamos en radianes los variables que necesitamos
        double dlat_rad = Math.toRadians(Math.abs(estacion2.latitud - estacion1.latitud));
        double dlon_rad = Math.toRadians(Math.abs(estacion2.longitud - estacion1.longitud));
        double lat_media_rad = Math.toRadians((estacion1.latitud + estacion2.latitud) / 2.0);
        double dist_norte_sur = radio * dlat_rad;
        double dist_este_oeste = radio * dlon_rad * Math.cos(lat_media_rad);

        // Calculamos la distancia total entre dos puntos
        distancia_total = dist_norte_sur + dist_este_oeste;

        // Evidentemente devolvemos la distancia total entre los dos puntos
        return distancia_total;
    }

    public double calculaCompleto (List<Estacion> dataset) {
        // Var. local para sumar
        double distancia_total = 0;

        // Bucle para calcular (en el orden dado) las distancias
        for (int i = 0; i < dataset.size()-1; i++) {
            Estacion estacion1 = dataset.get(i);
            Estacion estacion2 = dataset.get(i+1);
            distancia_total += calculaDistancia(estacion1, estacion2);
        }

        // Calcular vuelta de la ultima a la 0
        distancia_total += calculaDistancia(dataset.getLast(), dataset.getFirst());

        // Devolvemos el total de la distancia
        return distancia_total;
    }
}
