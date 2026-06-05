package main.java.Modelo;

import main.java.DataTypes.*;

import java.util.*;

public class CHC extends Algoritmo {
    // Atributos
    private int distUmbral;
    private int distUmbralIni;
    private int numReinicios;

    public CHC(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
        this.poblacion = new ArrayList<>();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            Random rand = new Random(this.semilla[i]);
            this.mejorIndividuo = null;
            this.numEvaluaciones = 0;
            distUmbral = this.listaEstaciones.size() / 4;
            distUmbralIni = distUmbral;
            this.historialExplotacion.clear();

            this.numReinicios = 0;
            int generacion = 0;

            inicializarPoblacion(rand);
            evaluarPoblacion(this.poblacion);

            for (Individuo indi:this.poblacion) {
                if (this.mejorIndividuo == null || this.mejorIndividuo.fitnessIndividuo > indi.fitnessIndividuo) {
                    this.mejorIndividuo = indi.clonarIndividuo();
                }
            }

            // Generamos generaciones
            while (generacion < this.maxGeneraciones) {
                ArrayList<Individuo> hijos = generarHijos(rand);
                reemplazoCHC(hijos, rand);
                if (distUmbral <= 0) {
                    reinicializar(rand);
                }
                actualizar();
                generacion++;
            }

            // Actualizamos datos
            this.mejorFuncionObjetivo = this.mejorIndividuo.fitnessIndividuo;
            this.distanciaRecorrida = this.mejorIndividuo.distanciaIndividuo;
            this.entropiaFinal = this.mejorIndividuo.entropiaIndividuo;
            this.recorrido = recomponer(this.mejorIndividuo.cromosoma);

            mostrarResultados("CHC");
            guardarDatos("CHC", i, numCaso);



        }
    }

    private Individuo cruzarIndividuo(Individuo padre, Individuo madre, Random rand) {
        int tam = padre.cromosoma.size();

        ArrayList<Estacion> hijo = new ArrayList<>(Collections.nCopies(tam, null));
        hijo.set(0, padre.cromosoma.get(0)); // depósito fijo

        // Donde coinciden, copiamos
        for (int i = 1; i < tam; i++) {
            if (padre.cromosoma.get(i).id == madre.cromosoma.get(i).id) {
                hijo.set(i, padre.cromosoma.get(i));
            }
        }

        // Donde son distintos, aleatoriamente
        for (int i = 1; i < tam; i++) {
            if (hijo.get(i) != null) continue;

            boolean tomarPadre = rand.nextBoolean();
            Estacion gen = tomarPadre ? padre.cromosoma.get(i) : madre.cromosoma.get(i);

            if (!tiene(hijo, gen)) {
                hijo.set(i, gen);
            } else {
                gen = tomarPadre ? madre.cromosoma.get(i) : padre.cromosoma.get(i);
                if (!tiene(hijo, gen)) {
                    hijo.set(i, gen);
                }
            }
        }

        // Rellenamos huecos  con genes si faltan
        for (int i = 1; i < tam; i++) {
            if (hijo.get(i) == null) {
                for (int j = 1; j < tam; j++) {
                    Estacion gen = padre.cromosoma.get(j);
                    if (!tiene(hijo, gen)) {
                        hijo.set(i, gen);
                        break;
                    }
                }
            }
        }

        return new Individuo(hijo);
    }

    private ArrayList<Individuo> generarHijos(Random rand){
        ArrayList<Individuo> hijos = new ArrayList<>();

        // Variedad
        Collections.shuffle(this.poblacion, rand);

        for (int i = 0; i < this.tamPoblacion; i+=2) {
            Individuo padre = poblacion.get(i);
            Individuo madre = poblacion.get(i+1);
            int distancia = distanciaArco(padre, madre);

            if (distancia > this.distUmbral) {
                hijos.add(cruzarIndividuo(padre, madre, rand));
            } else {
                hijos.add(null);
            }
        }

        // Para el fitness
        evaluarPoblacion(hijos);

        return hijos;
    }

    private void reemplazoCHC(ArrayList<Individuo> hijos, Random rand) {
        // 1. Combinar padres + hijos no nulos en pool de 2N
        List<Individuo> pool = new ArrayList<>(this.poblacion);
        for (Individuo h : hijos) {
            if (h != null) pool.add(h);
        }

        // 2. Eliminar duplicados (misma secuencia de arcos)
        List<Individuo> unicos = new ArrayList<>();
        for (Individuo ind : pool) {
            boolean duplicado = false;
            for (Individuo u : unicos) {
                if (distanciaArco(ind, u) == 0) {
                    duplicado = true;
                    break;
                }
            }
            if (!duplicado) unicos.add(ind);
        }

        // 3. Ordenar por fitness (menor = mejor en minimización)
        unicos.sort(Comparator.comparingDouble(i -> i.fitnessIndividuo));

        // 4. Seleccionar los N mejores
        this.poblacion.clear();
        for (int i = 0; i < this.tamPoblacion && i < unicos.size(); i++) {
            this.poblacion.add(unicos.get(i));
        }

        // 5. Rellenar con aleatorios si faltan
        while (this.poblacion.size() < this.tamPoblacion) {
            List<Estacion> cromo = generarSolucionInicial(rand);
            this.poblacion.add(new Individuo(cromo));
        }
        evaluarPoblacion(this.poblacion);

        // 6. Determinar si entró algún hijo (para umbral)
        boolean entro = false;
        for (Individuo h : hijos) {
            if (h != null && this.poblacion.contains(h)) {
                entro = true;
                break;
            }
        }
        if (!entro) distUmbral--;

        // 7. Actualizar mejor individuo
        for (Individuo indi : this.poblacion) {
            if (this.mejorIndividuo == null || indi.fitnessIndividuo < this.mejorIndividuo.fitnessIndividuo) {
                this.mejorIndividuo = indi.clonarIndividuo();
            }
        }
    }

    private void reinicializar(Random rand) {
        Individuo mejor = this.mejorIndividuo.clonarIndividuo();

        // Nos quedamos solo con el mejor
        this.poblacion.clear();
        this.poblacion.add(mejor);

        // El resto, aleatorios
        for (int i = 0; i < this.tamPoblacion-1; i++) {
            List<Estacion> cromo = generarSolucionInicial(rand);
            this.poblacion.add(new Individuo(cromo));
        }

        // Calculamos la poblacion
        evaluarPoblacion(this.poblacion);

        // Reseteamos
        distUmbral = distUmbralIni;
        numReinicios++;
    }


}
