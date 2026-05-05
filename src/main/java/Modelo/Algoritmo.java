package main.java.Modelo;

import java.util.*;
import main.java.DataTypes.*;

public abstract class Algoritmo {

    // Dataset de estaciones y estado del camión
    protected List<Estacion> listaEstaciones;
    public Camion camion = new Camion();

    protected Intercambio inter = new Intercambio();

    // Semillas para los algoritmos
    protected long[] semilla = {12345L, 67890L, 11111L, 54321L, 99999L};

    protected double mejorDistancia = Double.POSITIVE_INFINITY;
    protected double mejorFuncionObjetivo = Double.POSITIVE_INFINITY;

    // Matriz de distancias entre todas las estaciones
    protected Double[][] distancias;

    protected DistanciaManhattan distanciaManhattan = new DistanciaManhattan();

    // Resultado de la ejecución
    public List<Estacion> recorrido = new ArrayList<>();
    public double distanciaRecorrida = 0.0;
    public double entropiaFinal      = 0.0;
    public double fObjetivo          = 0.0;
    public int numEvaluaciones = 0;

    // Alpha para la función objetivo: Fobj = Kms + alpha * (N - Entropia)
    protected double alpha = 1.5;

    // Método que cada algoritmo debe implementar
    public abstract void run();

    public void calcularDistancias() {
        for (int i = 0; i < listaEstaciones.size(); i++) {
            distancias[i][i] = Double.POSITIVE_INFINITY;
            for (int j = 0; j < i; j++) {
                distancias[i][j] = distanciaManhattan.calculaDistancia(
                        listaEstaciones.get(i), listaEstaciones.get(j));
                distancias[j][i] = distancias[i][j];
            }
        }
    }

    public double calcularFObjetivo(double kms, List<Estacion> estaciones) {
        numEvaluaciones++;
        double entropia  = calcularEntropiaTotal(estaciones);
        double nEstaciones = estaciones.size();
        return kms + alpha * (nEstaciones - entropia);
    }

    public double calcularEntropiaTotal(List<Estacion> estaciones) {
        double total = 0.0;
        for (Estacion e : estaciones) {
            if (e.carga == 0 || e.carga == e.capacidad) continue; // entropía 0
            double p  = (double) e.carga / e.capacidad;
            double hi = -p * (Math.log(p) / Math.log(2))
                    - (1 - p) * (Math.log(1 - p) / Math.log(2));
            total += hi;
        }
        return total;
    }

    protected void equilibrarEstacion(Estacion est) {
        int objetivo = (int) Math.ceil(est.capacidad / 2.0);

        if (est.carga > objetivo) {
            int puedeRecoger = camion.getCapacidad() - camion.carga;
            int debeRecoger = est.carga - objetivo;
            int recoge = Math.min(puedeRecoger, debeRecoger);
            est.carga -= recoge;
            camion.carga += recoge;

        } else if (est.carga < objetivo) {
            int puedeDar = camion.carga;
            int debeDar = objetivo - est.carga;
            int realmenteDa = Math.min(puedeDar, debeDar);
            est.carga += realmenteDa;
            camion.carga -= realmenteDa;
        }
    }

    protected List<Estacion> recomponer(List<Estacion> dataset){
        // Aseguramos la carga del camión correcta
        this.camion.carga = 7;

        // Realizamos una copia del dataset
        List<Estacion> copia = Dataset.copiaDataset(this.listaEstaciones);

        // Reordenamos para que el orden sea el correcto de las visitas
        List<Estacion> resultado = new ArrayList<>();

        for (Estacion orden : dataset){
            for (Estacion estacion : copia){
                if (estacion.id == orden.id){
                    resultado.add(estacion);
                }
            }
        }

        for (Estacion e : resultado){
            equilibrarEstacion(e);
        }

        return resultado;
    }

    // Método para usar la Busqueda Local en el grasp
    protected List<Estacion> aplicarBusquedaLocal(List<Estacion> dataset){
        List<Estacion> mejorVecino = Dataset.copiaDataset(dataset);

        // Recomponemos la solución para que no haya problemas
        mejorVecino = recomponer(mejorVecino);
        double mejorFO = calcularFObjetivo(distanciaManhattan.calculaCompleto(mejorVecino), mejorVecino);

        boolean mejoro = true;

        int numEvaluacionesLocal = 0;

        // Copiamos y pegamos de BusquedaLocalPM
        while (numEvaluacionesLocal < 3000 && mejoro) {

            // por si a caso
            mejoro = false;
            this.camion.carga = 7;

            // Para salir aqui cuando sea 1
            primero:
            for (int l = 1; l < mejorVecino.size(); l++) {
                for (int m = l+1; m < mejorVecino.size(); m++) {

                    List<Estacion> vecinoOrden = new ArrayList<>(mejorVecino);
                    Collections.swap(vecinoOrden, l, m);

                    // Para cada iteracion
                    this.camion.carga = 7;

                    // Reconstruimos como antes para la sestaciones
                    List<Estacion> copia = Dataset.copiaDataset(this.listaEstaciones);
                    List<Estacion> vecinoEquilibrado = new ArrayList<>();
                    for (Estacion estacion : vecinoOrden) {
                        for (Estacion estacionEquilibrado : copia) {
                            if (estacion.id == estacionEquilibrado.id) {
                                vecinoEquilibrado.add(estacionEquilibrado);
                            }
                        }
                    }

                    // Equilibramos nuestras estaciones
                    for (Estacion e : vecinoEquilibrado) {
                        equilibrarEstacion(e);
                    }

                    // Hacemos los calculos de este vecino
                    double distanciaVecino = distanciaManhattan.calculaCompleto(vecinoEquilibrado);
                    double funcionObjetivoVecino = calcularFObjetivo(distanciaVecino, vecinoEquilibrado);
                    numEvaluacionesLocal++;

                    if (funcionObjetivoVecino < mejorFO) {
                        mejorFO = funcionObjetivoVecino;
                        mejorVecino = vecinoEquilibrado;
                        mejoro = true;

                        break primero;
                    }
                }
            }
        }

        // Devolvemos la mejor sol que nos qe
        return mejorVecino;
    }

    // Método para hacer una mutacion "fuerte"?
    protected List<Estacion> mutacionFuerte(List<Estacion> dataset, int tamano, Random rand) {
        // Hacemos una copia de nuestro dataset
        List<Estacion> copia = Dataset.copiaDataset(dataset);

        int tamLista = copia.size();

        // Generamos un número aleatorio entre 1 y tamaño de nuestra lista
        int pos = 1 + rand.nextInt(tamLista - 1);

        // Listas auxiliares
        List<Integer> listaEstaciones = new ArrayList<>();
        List<Estacion> elementos = new ArrayList<>();

        // Bucle para quedarnos con las posiciones necesarias
        for (int i = 0; i < tamano; i++) {
            int indice = ((pos + i - 1) % (tamLista-1)) + 1;
            listaEstaciones.add(indice);
        }

        for (int i: listaEstaciones) {
            elementos.add(copia.get(i));
        }

        // Remezclamos
        Collections.shuffle(elementos, rand);

        // Introducimos los elementos de vuelta en la lista
        for (int i = 0; i < listaEstaciones.size(); i++) {
            copia.set(listaEstaciones.get(i), elementos.get(i));
        }

        // Devolvemos la modificada
        return copia;
    }

    // Método para generar una solución inicial
    protected List<Estacion> generarSolucionInicial(Random rand) {
        // Trabajamos con una copia
        List<Estacion> copia = Dataset.copiaDataset(listaEstaciones);

        // Partimos para que la primera no se mueva
        List<Estacion> resto = new ArrayList<>(copia.subList(1, copia.size()));

        // Mezclamos
        Collections.shuffle(resto, rand);

        // rejuntamos
        resto.addFirst(copia.getFirst());

        // Devolvemos
        return resto;
    }
}