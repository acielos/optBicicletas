package main.java.Modelo;

import main.java.DataTypes.Estacion;
import java.util.*;

public class Multimodal extends Algoritmo {

    public Multimodal(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
        this.probCruce = 0.9;
        this.torneo = 3;
        this.porMutacion = 0.07;
        this.poblacion = new ArrayList<>();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            Random rand = new Random(this.semilla[i]);
            this.mejorIndividuo = null;
            this.numEvaluaciones = 0;
            int generacion = 0;

            inicializarPoblacion(rand);
            evaluarPoblacionMulti(this.poblacion);

            // Nos quedamos con el mejor individuo hasta ahora
            for (Individuo indi : this.poblacion) {
                if (this.mejorIndividuo == null || indi.fitnessIndividuo < this.mejorIndividuo.fitnessIndividuo) {
                    this.mejorIndividuo = indi.clonarIndividuo();
                }
            }

            // Materia
            while (generacion < this.maxGeneraciones) {
                List<Individuo> hijos = cruzarPoblacionMulti(rand);
                reemplazo(hijos, rand);
                actualizar();
                generacion++;
            }

            double tam = this.listaEstaciones.size() - 1;
            this.mejorFuncionObjetivo = this.mejorIndividuo.distanciaIndividuo + 1.5 * (tam - this.mejorIndividuo.entropiaIndividuo);
            this.distanciaRecorrida = this.mejorIndividuo.distanciaIndividuo;
            this.entropiaFinal = this.mejorIndividuo.entropiaIndividuo;
            this.recorrido = recomponer(this.mejorIndividuo.cromosoma);

            mostrarResultados("Multimodal");
            guardarDatos("Multimodal", i);

        }
    }

    protected void evaluarPoblacionMulti(List<Individuo> hijos){
        evaluarPoblacion(hijos);
        Set<Individuo> todos = new HashSet<>();
        todos.addAll(hijos);
        todos.addAll(this.poblacion);

        for (Individuo indi:hijos) {
            double nicho = 0.0;
            if (indi != null) {
                for (Individuo otro:todos) {
                    if (otro == indi || otro == null) {continue;}
                    if (otro != null) {
                        double distArc = distanciaArco(indi, otro);
                        if (distArc < radioNicho) {
                            nicho += 1 - Math.pow(distArc / radioNicho, expSh);
                        }
                    }
                }

                if (nicho > 1) {
                    indi.fitnessIndividuo *= nicho;
                }
            }
        }
    }

    protected List<Individuo> cruzarPoblacionMulti(Random rand){
        List<Individuo> hijos = new ArrayList<>();

        for (int i = 0; i < this.tamPoblacion/2; i++) {
            Individuo padre = seleccionTornero(rand);
            Individuo madre = seleccionTornero(rand);

            while (madre == padre) {
                madre = seleccionTornero(rand);
            }
            if (rand.nextDouble() < probCruce) {
                List<Individuo> resultado = cruzarOX(padre, madre, rand);
                hijos.addAll(resultado);
            } else {
                Individuo hijo = mutarIndividuo(padre, rand);
                Individuo hija = mutarIndividuo(madre, rand);
                hijos.add(hijo);
                hijos.add(hija);
            }
        }
        evaluarPoblacionMulti(hijos);

        return hijos;
    }

}
