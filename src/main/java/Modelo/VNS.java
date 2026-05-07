package main.java.Modelo;

import main.java.DataTypes.*;

import java.util.*;

public class VNS extends Algoritmo {

    public VNS(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
        this.distancias = new Double[dataset.size()][dataset.size()];
        calcularDistancias();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            // Reseteamos
            this.mejorFuncionObjetivo = Double.POSITIVE_INFINITY;
            this.numEvaluaciones = 0;
            this.camion.reset();

            // Usamos las semillas para la reproductibilidad
            Random rand = new Random(this.semilla[i]);

            // Generamos una solución inicial
            List<Estacion> solInicial = recomponer(generarSolucionInicial(rand));

            // Aplicamos BL a la solución inicial
            List<Estacion> solActual = aplicarBusquedaLocal(solInicial);

            // pa luego
            List<Estacion> mejorVecino = solActual;

            // Nuestras mejores resultados seran
            double actualFO = calcularFObjetivo(distanciaManhattan.calculaCompleto(solActual), solActual);
            double mejorFO = actualFO;

            // Inicializamos los contadores que necesitaremos
            int k = 1;
            int bl = 0;

            // Bucle principal
            while (bl < 10) {
                // Primero comprobamos que k no supere 4
                if (k > 4) {
                    k = 1;
                }

                // Calculamos el tamaño de la sublista
                int tamSubLista = Math.max(2, (solActual.size()) / (7-k));

                // Hacemos una mutación sobre nuestra solución con el tamaño elegido
                List<Estacion> solMutada = mutacionFuerte(solActual, tamSubLista, rand);

                // Recomponemos
                List<Estacion> solMutadaRec = recomponer(solMutada);

                // Aplicamos la búsqueda local a nuestra recomposicion
                List<Estacion> solBL = aplicarBusquedaLocal(solMutadaRec);

                // Calculamos la FO de nuestra BL
                double foBl = calcularFObjetivo(distanciaManhattan.calculaCompleto(solBL), solBL);

                // Comprobamos si mejoramos o no localmente
                if (foBl < actualFO) {
                    actualFO = foBl;
                    solActual = solBL;
                    k = 1;
                } else {
                    k++;
                }

                // Comprobamos si mejoramos o no globalmente
                if (foBl < mejorFO) {
                    mejorFO = foBl;
                    mejorVecino = solBL;
                }

                // Aumentamos
                bl++;
            }

            this.distanciaRecorrida = distanciaManhattan.calculaCompleto(mejorVecino);
            this.mejorFuncionObjetivo = mejorFO;
            this.recorrido = mejorVecino;
            this.entropiaFinal = calcularEntropiaTotal(mejorVecino);

            // mostramos los resultados
            mostrarResultados("VNS");
        }
    }
}
