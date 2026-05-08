package main.java.Modelo;

import main.java.DataTypes.*;
import java.util.*;

public class BusquedaLocalPM extends Algoritmo {

    // Constructor de la clase
    public BusquedaLocalPM(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
    }

    // Método run() porque hereda
    @Override
    public void run() {

        for (int i = 0; i < 5; i++) {
            // Vamos a trabajar con una copia del dataset, para que no se lie
            List<Estacion> datasetCopiado = Dataset.copiaDataset(this.listaEstaciones);

            // Reseteamos
            this.historialExplotacion.clear();
            this.mejorFuncionObjetivo = Double.POSITIVE_INFINITY;
            this.numEvaluaciones = 0;
            this.camion.reset();

            // Para la semilla
            Random rand = new Random(this.semilla[i]);

            // Vamos a mezclarlo (a excepción de la primera estación) para nuestra solución inicial
            List<Estacion> mezclado = new ArrayList<>(datasetCopiado.subList(1, this.listaEstaciones.size()));

            // Mezclamos todas menos la primera
            Collections.shuffle(mezclado, rand);

            // Unimos de nuevo todo
            mezclado.addFirst(datasetCopiado.getFirst());

            // Hacemos una copia del dataset donde iremos guardando el mejor vecino hasta el momento
            List<Estacion> mejorVecino = new ArrayList<>(mezclado);

            //Recomponemos la solución para asegurar que estaciones y cargas vayan en conjunto
            List<Estacion> inicialOrdenado = recomponer(mezclado);

            // Equilibramos nuestra solucion inicial
            for (Estacion e : inicialOrdenado) {
                equilibrarEstacion(e);
            }

            // Empezamos a calcular/guardar valores de nuestra sol inicial
            double kmsInicial = distanciaManhattan.calculaCompleto(inicialOrdenado);
            this.mejorFuncionObjetivo = calcularFObjetivo(kmsInicial, inicialOrdenado);
            this.recorrido = inicialOrdenado;
            this.distanciaRecorrida = kmsInicial;
            this.entropiaFinal = calcularEntropiaTotal(inicialOrdenado);

            // Para comprobar si mejora o no
            boolean mejoro = true;

            // Una vez lo tenemos todo, vamos a proceder a la parte interesante del algoritmo
            while (numEvaluaciones < 3000 && mejoro) {

                // por si a caso
                mejoro = false;
                this.camion.reset();

                // Para salir aqui cuando sea 1
                primero:
                for (int l = 1; l < mejorVecino.size(); l++) {
                    for (int m = l+1; m < mejorVecino.size(); m++) {

                        List<Estacion> vecinoOrden = new ArrayList<>(mejorVecino);
                        Collections.swap(vecinoOrden, l, m);

                        // Para cada iteracion
                        this.camion.reset();

                        // Reconstruimos como antes para la sestaciones
                        List<Estacion> vecinoEquilibrado = recomponer(vecinoOrden);

                        // Hacemos los calculos de este vecino
                        double distanciaVecino = distanciaManhattan.calculaCompleto(vecinoEquilibrado);
                        double funcionObjetivoVecino = calcularFObjetivo(distanciaVecino, vecinoEquilibrado);

                        if (funcionObjetivoVecino < this.mejorFuncionObjetivo) {
                            this.mejorFuncionObjetivo = funcionObjetivoVecino;
                            this.recorrido = vecinoEquilibrado;
                            this.distanciaRecorrida = distanciaVecino;
                            this.entropiaFinal = calcularEntropiaTotal(vecinoEquilibrado);
                            mejorVecino = vecinoEquilibrado;
                            mejoro = true;

                            break primero;
                        }
                    }
                }
            }

            // Mostramos por pantalla la distancia calculada con cada una de las 5 semillas
            mostrarResultados("Busqueda Local PM");

            // guardarDatos("BL-PM", i);
        }
    }
}