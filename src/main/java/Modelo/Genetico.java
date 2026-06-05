package main.java.Modelo;

import main.java.DataTypes.*;
import java.util.*;


public class Genetico extends Algoritmo{
    // Parámetros

    public Genetico(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
        this.probCruce = 0.9;
        this.torneo = 3;
        this.poblacion = new ArrayList<>();
    }


    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            Random rand = new Random(this.semilla[i]);
            this.mejorIndividuo = null;
            this.numEvaluaciones = 0;
            int generacion = 0;
            this.historialExplotacion.clear();

            inicializarPoblacion(rand);
            evaluarPoblacion(this.poblacion);

            // Nos quedamos con el mejor individuo hasta ahora
            for (Individuo indi : this.poblacion) {
                if (this.mejorIndividuo == null || indi.fitnessIndividuo < this.mejorIndividuo.fitnessIndividuo) {
                    this.mejorIndividuo = indi.clonarIndividuo();
                }
            }

            // Materia
            while (generacion < this.maxGeneraciones) {
                List<Individuo> hijos = cruzarPoblacion(rand);
                reemplazo(hijos, rand);
                actualizar();
                generacion++;
            }

            this.mejorFuncionObjetivo = this.mejorIndividuo.fitnessIndividuo;
            this.distanciaRecorrida = this.mejorIndividuo.distanciaIndividuo;
            this.entropiaFinal = this.mejorIndividuo.entropiaIndividuo;
            this.recorrido = recomponer(this.mejorIndividuo.cromosoma);

            mostrarResultados("Genético Simple");
            guardarDatos("Genético", i, numCaso);

        }
    }
}
