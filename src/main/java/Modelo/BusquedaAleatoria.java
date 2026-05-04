package main.java.Modelo;

import main.java.DataTypes.*;
import java.util.*;

public class BusquedaAleatoria extends Algoritmo {

    public BusquedaAleatoria(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
    }

    @Override
    public void run() {

        for (int i = 0; i < 5; i++) {

            Random rand = new Random(this.semilla[i]);

            this.mejorFuncionObjetivo = Double.POSITIVE_INFINITY;
            this.numEvaluaciones = 0;

            List<Estacion> mejorRecorrido = null;
            double mejorKms = 0.0;
            double mejorEntropia = 0.0;

            for (int j = 0; j < 100; j++) {

                List<Estacion> copia = Dataset.copiaDataset(listaEstaciones);

                List<Estacion> resto = new ArrayList<>(copia.subList(1, copia.size()));
                Collections.shuffle(resto, rand);
                resto.addFirst(copia.getFirst());

                this.camion.carga = 7;

                for (Estacion e : resto) {
                    equilibrarEstacion(e);
                }

                double kms = distanciaManhattan.calculaCompleto(resto);
                double fObj = calcularFObjetivo(kms, resto);

                if (fObj < this.mejorFuncionObjetivo) {
                    this.mejorFuncionObjetivo = fObj;
                    mejorRecorrido = resto;
                    mejorKms = kms;
                    mejorEntropia = calcularEntropiaTotal(resto);
                }
            }

            this.recorrido = mejorRecorrido;
            this.distanciaRecorrida = mejorKms;
            this.entropiaFinal = mejorEntropia;
            this.fObjetivo = this.mejorFuncionObjetivo;

            System.out.printf("%n--- Búsqueda Aleatoria | Semilla %d ---%n", semilla[i]);
            System.out.print("Recorrido: ");
            for (Estacion e : recorrido) System.out.print(e.id + " ");
            System.out.println("-> 0");
            System.out.printf("Kilómetros recorridos : %.4f km%n", distanciaRecorrida);
            System.out.printf("Entropía final        : %.4f  (máx. %d)%n",
                    entropiaFinal, recorrido.size());
            System.out.printf("Función objetivo      : %.4f%n", fObjetivo);
            System.out.printf("Evaluaciones          : %d%n", numEvaluaciones);
            System.out.printf("Carga final camión    : %d/%d bicis%n",
                    camion.carga, camion.getCapacidad());
        }
    }
}
