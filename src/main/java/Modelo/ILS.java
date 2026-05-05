package main.java.Modelo;

import main.java.DataTypes.*;

import java.util.*;

public class ILS extends Algoritmo{

    public ILS(List<Estacion> dataset){
        this.listaEstaciones = dataset;
        this.distancias = new Double[dataset.size()][dataset.size()];
        calcularDistancias();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++){

            // Reseteamos por si
            this.mejorFuncionObjetivo = Double.POSITIVE_INFINITY;
            this.numEvaluaciones = 0;
            this.camion.carga = 7;

            // Generamos un numero aleatorio con nuestras semillas
            Random rand = new Random(this.semilla[i]);

            // Generamos una solución inicial aleatoria
            List<Estacion> listaInicial = recomponer(generarSolucionInicial(rand));

            // Aplicamos una búsqueda local a nuestra lista recompuesta
            List<Estacion> solActual = aplicarBusquedaLocal(listaInicial);
            List<Estacion> solMejor = Dataset.copiaDataset(solActual);

            // Sacamos las métricas de esta lista, pues hasta el momento será la mejor solución
            double distanciaMejor = distanciaManhattan.calculaCompleto(solMejor);
            double funcionMejor = calcularFObjetivo(distanciaMejor, solMejor);
            double entropiaMejor = calcularEntropiaTotal(solMejor);
            List<Estacion> mejorVecinoGlobal = Dataset.copiaDataset(solMejor);

            // Para usarlo mas adelante
            double funcionLocalMejor;

            List<Estacion> solucionLocal;

            // Ahora usaremos el bucle de 10 iteraciones para aplicar nuevas BL
            for (int j = 0; j < 10; j++){
                // Vemos cual es la solucion a mutar
                funcionLocalMejor = calcularFObjetivo(distanciaManhattan.calculaCompleto(solActual), solActual);

                // Comprobamos que FO es menor
                if (funcionLocalMejor <= funcionMejor){
                    solucionLocal = Dataset.copiaDataset(solActual);
                } else {
                    solucionLocal = Dataset.copiaDataset(solMejor);
                }

                // Hacemos la mutación
                List<Estacion> solucionLocalMutada = mutacionFuerte(solucionLocal, 4, rand);

                // Recomponemos nuestra sol mutada
                List<Estacion> solucionLocalMutadaRecompuesta = recomponer(solucionLocalMutada);

                // Aplicamos BL a la solucion mutada
                List<Estacion> solucionLocalMutadaBL = aplicarBusquedaLocal(solucionLocalMutadaRecompuesta);

                // Calculamos su FO
                double distancia_Mutada = distanciaManhattan.calculaCompleto(solucionLocalMutadaBL);
                double FO_Mutada = calcularFObjetivo(distancia_Mutada, solucionLocalMutadaBL);

                // Comprobamos cual es mejor
                if (FO_Mutada < funcionMejor){
                    mejorVecinoGlobal = Dataset.copiaDataset(solucionLocalMutadaBL);
                    funcionMejor = FO_Mutada;
                    distanciaMejor = distancia_Mutada;
                    entropiaMejor = calcularEntropiaTotal(solucionLocalMutadaBL);
                }

                if (FO_Mutada < funcionLocalMejor) {
                    solActual =  Dataset.copiaDataset(solucionLocalMutadaBL);
                }

            }

            this.mejorFuncionObjetivo = funcionMejor;
            this.recorrido = mejorVecinoGlobal;
            this.distanciaRecorrida = distanciaMejor;
            this.entropiaFinal = entropiaMejor;


            System.out.println("\n--- Resultado ILS ---");
            System.out.printf("Recorrido: ");
            for (Estacion e : this.recorrido) System.out.print(e.id + " ");
            System.out.println("-> 0");

            System.out.printf("Kilómetros recorridos : %.4f km%n", this.distanciaRecorrida);
            System.out.printf("Función objetivo      : %.4f%n", this.mejorFuncionObjetivo);
            System.out.printf("Evaluaciones          : %d%n", this.numEvaluaciones);
            System.out.printf("%nCarga final del camión: %d/%d bicis%n", this.camion.carga, this.camion.getCapacidad());

        }


    }
}
