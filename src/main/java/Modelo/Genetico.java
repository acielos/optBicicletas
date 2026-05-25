package main.java.Modelo;

import main.java.DataTypes.*;
import java.util.*;


public class Genetico extends Algoritmo{
    // Parámetros

    private int tamPoblacion;
    private double probCruce;
    private int torneo;
    private int maxGeneraciones;
    private double porMutacion;
    private List<Individuo> poblacion;
    private Individuo mejorIndividuo;

    public Genetico(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
        this.tamPoblacion = 30;
        this.probCruce = 0.9;
        this.torneo = 3;
        this.maxGeneraciones = 100;
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
            guardarDatos("Genético", i);

        }
    }

    /*
    ===============================================================================
    */


    private void inicializarPoblacion(Random rand){
        this.poblacion.clear();
        for (int i = 0; i < this.tamPoblacion; i++) {
            List<Estacion> cromosoma = generarSolucionInicial(rand);
            Individuo individuo = new Individuo(cromosoma);
            this.poblacion.add(individuo);
        }
    }

    private void evaluarPoblacion(List<Individuo> poblacion){
        for (Individuo indi:poblacion) {
            this.camion.reset();
            List<Estacion> equilibrado = recomponer(indi.cromosoma);
            indi.distanciaIndividuo = distanciaManhattan.calculaCompleto(equilibrado);
            indi.fitnessIndividuo = calcularFObjetivo(indi.distanciaIndividuo, equilibrado);
            indi.entropiaIndividuo = calcularEntropiaTotal(equilibrado);
        }
    }

    private Individuo seleccionTornero(Random rand) {
        Individuo mejor = null;
        for (int i = 0; i < torneo; i++) {
            int indice = rand.nextInt(this.poblacion.size());
            Individuo candidato = this.poblacion.get(indice);
            if (mejor == null || candidato.fitnessIndividuo < mejor.fitnessIndividuo) {
                mejor = candidato;
            }
        }

        return mejor.clonarIndividuo();
    }

    private List<Individuo> cruzarPoblacion(Random rand){
        List<Individuo> hijos = new ArrayList<>();

        for (int i = 0; i < this.tamPoblacion/2; i++) {
            Individuo padre = seleccionTornero(rand);
            Individuo madre = seleccionTornero(rand);

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
        evaluarPoblacion(hijos);

        return hijos;
    }

    private List<Individuo> cruzarOX(Individuo padre, Individuo madre, Random rand){
        List<Individuo> resultado = new ArrayList<>();
        int tamanno = padre.cromosoma.size();
        int inicio = 1 + rand.nextInt(tamanno - 2);
        int fin = inicio + rand.nextInt(tamanno - inicio);
        if (fin >= tamanno) {
            fin = tamanno - 1;
        }

        // Primer hijo
        List<Estacion> cromosomaHijo = new ArrayList<>(Collections.nCopies(tamanno, null));

        // Rellenamos con el padre
        for (int i = inicio; i <= fin; i++) {
            cromosomaHijo.set(i, padre.cromosoma.get(i));
        }

        // Rellenamos con la madre
        for (int i = 0; i < tamanno; i++) {
            if (cromosomaHijo.get(i) == null) {
                Estacion gen = madre.cromosoma.get(i);
                if (!tiene(cromosomaHijo, gen)){
                    cromosomaHijo.set(i, gen);
                }
            }
        }

        // Comrpobamos no haber dejado ninguna posición libre
        int indice = (fin + 1) % tamanno;
        int escritura = (fin + 1) % tamanno;
        while(!completo(cromosomaHijo)) {
            Estacion gen = madre.cromosoma.get(indice);
            if (!tiene(cromosomaHijo, gen)){
                while (cromosomaHijo.get(escritura) != null) {
                    escritura = (escritura + 1) % tamanno;
                }
                cromosomaHijo.set(escritura, gen);
            }
            indice = (indice + 1) % tamanno;
        }

        Individuo hijo = new Individuo(cromosomaHijo);
        resultado.add(hijo);

        // Segundo hijo que es casi igual
        List<Estacion> cromosomaHija = new ArrayList<>(Collections.nCopies(tamanno, null));

        // Rellenamos con la madre
        for (int i = inicio; i <= fin; i++) {
            cromosomaHija.set(i, madre.cromosoma.get(i));
        }

        // Rellenamos con el padre
        for (int i = 0; i < tamanno; i++) {
            if (cromosomaHija.get(i) == null) {
                Estacion gen = padre.cromosoma.get(i);
                if (!tiene(cromosomaHija, gen)){
                    cromosomaHija.set(i, gen);
                }
            }
        }

        // Comrpobamos no haber dejado ninguna posición libre
        indice = (fin + 1) % tamanno;
        int escrituraHija = (fin + 1) % tamanno;
        while(!completo(cromosomaHija)) {
            Estacion gen = padre.cromosoma.get(indice);
            if (!tiene(cromosomaHija, gen)){
                while (cromosomaHija.get(escrituraHija) != null) {
                    escrituraHija = (escrituraHija + 1) % tamanno;
                }
                cromosomaHija.set(escrituraHija, gen);
            }
            indice = (indice + 1) % tamanno;
        }

        Individuo hija = new Individuo(cromosomaHija);

        resultado.add(hija);

        return resultado;
    }

    private Individuo mutarIndividuo(Individuo padre, Random rand) {

        int tamPadre = padre.cromosoma.size();
        int nuMutaciones = (int) Math.ceil((tamPadre -1) * porMutacion);
        Individuo copia = padre.clonarIndividuo();

        for (int i = 0; i < nuMutaciones; i++) {
            int posicion1 = 1 + rand.nextInt(tamPadre-1);
            int posicion2 = 1 + rand.nextInt(tamPadre-1);
            while(posicion1 == posicion2){
                posicion2 = 1 + rand.nextInt(tamPadre-1);
            }

            if (posicion1 < posicion2){
                while(posicion1 < posicion2){
                    Collections.swap(copia.cromosoma, posicion1, posicion2);
                    posicion1++;
                    posicion2--;
                }
            } else {
                while(posicion1 > posicion2){
                    Collections.swap(copia.cromosoma, posicion1, posicion2);
                    posicion1--;
                    posicion2++;
                }
            }
        }
        return copia;
    }

    private void reemplazo(List<Individuo> herederos, Random rand) {
        for (Individuo individuo : herederos) {
            Individuo peor = null;
            int indicePeor = -1;

            // Nos quedamos con el peor candidato
            for (int i = 0; i < torneo; i++){
                int indice = rand.nextInt(this.poblacion.size());
                Individuo candidato = this.poblacion.get(indice);
                if (peor == null || candidato.fitnessIndividuo >  peor.fitnessIndividuo) {
                    peor = candidato;
                    indicePeor = indice;
                }
            }

            // Nos vamos quedando con los mejores individuos
            if (individuo.fitnessIndividuo < peor.fitnessIndividuo) {
                this.poblacion.set(indicePeor, individuo);
            }
        }

        for (Individuo individuo : this.poblacion) {
            if (this.mejorIndividuo == null || individuo.fitnessIndividuo < this.mejorIndividuo.fitnessIndividuo) {
                this.mejorIndividuo = individuo.clonarIndividuo();
            }
        }
    }

    private void actualizar(){
        double mejorFitnessGeneracion = Double.POSITIVE_INFINITY;

        for (Individuo indi : this.poblacion) {
            if (indi.fitnessIndividuo < mejorFitnessGeneracion) {
                mejorFitnessGeneracion = indi.fitnessIndividuo;
            }
        }

        this.historialExplotacion.add(
                new double[]{this.numEvaluaciones, mejorFitnessGeneracion, this.mejorIndividuo.fitnessIndividuo}
        );

    }

    private boolean tiene(List<Estacion> lista, Estacion e){
        for (Estacion est:lista){
            if (est != null && est.id == e.id){return true;}
        }
        return false;
    }

    private boolean completo(List<Estacion> lista){
        for (Estacion est:lista){
            if (est == null){return false;}
        }
        return true;
    }

}
