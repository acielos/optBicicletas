package main.java.Modelo;

import main.java.DataTypes.*;
import java.util.*;

public class Greedy extends Algoritmo {

    public Greedy(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
    }


    public void run(){

        List<Estacion> copia = Dataset.copiaDataset(this.listaEstaciones);

        // Reseteamos por si se hacen varias ejecuciones
        this.distanciaRecorrida = 0.0;
        this.numEvaluaciones = 0;
        this.recorrido.clear();

        // Preparamos el camión con 7 bicicletas
        this.camion.carga = 7;

        // Generamos la matriz de distancias
        this.distancias = new Double[listaEstaciones.size()][listaEstaciones.size()];
        calcularDistancias();

        // Declaramos la primera estación del recorrido
        int estActual = 0;
        this.recorrido.addFirst(copia.get(estActual));
        equilibrarEstacion(this.recorrido.getFirst());  // Solo tenemos 1 por ahora, estqria bien asi

        // Actualizamos nuestra matriz de distancias para no volver a coger la est. 0
        for(int i = 0; i < listaEstaciones.size(); i++){
            this.distancias[i][0] = Double.POSITIVE_INFINITY;
        }

        while (this.recorrido.size() < listaEstaciones.size()){
            // Variables locales que usaremos para encontrar la est. mas cercana
            double mejorDistanciaLocal = Double.POSITIVE_INFINITY;
            int estSiguiente = estActual;

            // Bucle para encontrar la menor distancia
            for(int i = 0; i < listaEstaciones.size(); i++){
                if (this.distancias[estActual][i] < mejorDistanciaLocal){
                    mejorDistanciaLocal = this.distancias[estActual][i];
                    estSiguiente = i;
                }
            }

            // Añadimos la estación a nuestro recorrido
            this.recorrido.addLast(copia.get(estSiguiente));

            // Actualizamos variables
            estActual = estSiguiente;

            // Equilibramos la estación si es posible
            equilibrarEstacion(this.recorrido.getLast());

            // Actualizamos nuestra matriz de distancias para no volver a coger la estActual
            for(int i = 0; i < listaEstaciones.size(); i++){
                this.distancias[i][estActual] = Double.POSITIVE_INFINITY;
            }
        }

        // Una vez visitemos todas, tenemos que volver al origen
        //this.distanciaRecorrida += distanciaManhattan.calculaDistancia(this.recorrido.getFirst(),  this.recorrido.getLast());
        this.distanciaRecorrida = distanciaManhattan.calculaCompleto(this.recorrido);

        // Calculamos la Función Objetivo de nuestro resultado
        double entropia = calcularEntropiaTotal(this.recorrido);
        double funObjetivo = calcularFObjetivo(this.distanciaRecorrida, this.recorrido);

        this.mejorFuncionObjetivo = funObjetivo;
        this.fObjetivo = funObjetivo;


        System.out.println("\n--- Resultado Greedy ---");
        System.out.printf("Recorrido: ");
        for (Estacion e : recorrido) System.out.print(e.id + " ");
        System.out.println("-> 0");

        System.out.printf("Kilómetros recorridos : %.4f km%n", distanciaRecorrida);
        System.out.printf("Función objetivo      : %.4f%n", funObjetivo);
        System.out.printf("Evaluaciones          : %d%n", numEvaluaciones);

//        System.out.println("\nEstado final de las estaciones:");
//        System.out.printf("%-6s %-10s %-10s %-8s%n", "ID", "Carga", "Capacidad", "% ocup.");
//        for (Estacion e : recorrido) {
//            double pct = 100.0 * e.carga / e.capacidad;
//            System.out.printf("%-6d %-10d %-10d %.1f%%%n", e.id, e.carga, e.capacidad, pct);
//        }
        System.out.printf("%nCarga final del camión: %d/%d bicis%n", camion.carga, camion.getCapacidad());
    }
}