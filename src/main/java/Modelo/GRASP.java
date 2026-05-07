package main.java.Modelo;

import main.java.DataTypes.*;

import java.util.*;

public class GRASP extends Algoritmo{

    // Constructor de la clase
    public GRASP(List<Estacion> dataset){
        this.listaEstaciones = dataset;
        this.distancias = new Double[dataset.size()][dataset.size()];
        calcularDistancias();
    }

    @Override
    public void run() {
        for(int i = 0; i < 5; i++){
            // Reseteamos por si a caso
            this.mejorFuncionObjetivo = Double.POSITIVE_INFINITY;
            this.numEvaluaciones = 0;
            this.camion.reset();

            // Variables locales
            double mejorFOSemilla = Double.POSITIVE_INFINITY;
            List<Estacion> mejorLista = null;
            double mejorDistanciaLocal = 0;
            double mejorEntropiaLocal = 0;

            // Generamos con el rand para las semillas
            Random rand =  new Random(this.semilla[i]);

            // Bucle de iteraciones de GRASP
            int iteraciones = 10;
            for (int j = 0; j < iteraciones; j++) {
                // Generamos un greedy probabilistico y le aplicamos la BL
                List<Estacion> solucionGreedy = greedyProbabilistico(rand);
                List<Estacion> solucionMejorada = aplicarBusquedaLocal(solucionGreedy);

                double distanciaLocal = distanciaManhattan.calculaCompleto(solucionMejorada);
                double FOLocal = calcularFObjetivo(distanciaLocal, solucionMejorada);
                double entropiaLocal = calcularEntropiaTotal(solucionMejorada);

                // Comprobamos si es mejor o no que lo que tenemos
                if (FOLocal < mejorFOSemilla) {
                    mejorFOSemilla = FOLocal;
                    mejorDistanciaLocal = distanciaLocal;
                    mejorEntropiaLocal = entropiaLocal;
                    mejorLista = solucionMejorada;
                }
            }

            this.mejorFuncionObjetivo = mejorFOSemilla;
            this.distanciaRecorrida = mejorDistanciaLocal;
            this.entropiaFinal = mejorEntropiaLocal;
            this.recorrido = mejorLista;

            // mosrtamos los resultados
            mostrarResultados("GRASP");
        }
    }

    private List<Estacion> greedyProbabilistico(Random rand) {
        // Generamos una copia con la que vamos a trabajar
        List<Estacion> copiaGreedy = Dataset.copiaDataset(this.listaEstaciones);

        // Reseteamos por si a caso
        this.camion.reset();

        // Las listas cpm las que vamos a trabjaar
        List<Estacion> solucion = new ArrayList<>();
        List<Estacion> noVisitadas = new ArrayList<>(copiaGreedy.subList(1, copiaGreedy.size()));

        // Declaramos la primera estación del recorrido
        int estActual = 0;
        solucion.addFirst(copiaGreedy.get(estActual));
        equilibrarEstacion(solucion.getFirst());

        // Bucle principal de nuestro greedyPro
        while(!noVisitadas.isEmpty()){
            // Para guardar las puntuaciones que obtiene cada una de las estaciones
            List<estacionCandidata> puntos = new ArrayList<>();

            // Hacemos unn bucle para recorrer las no visitadas
            for (Estacion candidata:noVisitadas) {

                // Usamos la inversa de la distancia, para incluirla o no en la lista de candidatas
                double distanciaInversa = (1.0) / (distancias[estActual][candidata.id]);

                // Usaremos también la entropía de cada estación, una qu eno esté muy equilibrada, será mas importante vigilarla
                // para que la entropía final sea mínima
                double objetivoCapacidad = Math.ceil(candidata.capacidad/2.0);
                double deficitCapacidad = Math.abs(candidata.carga - objetivoCapacidad) / candidata.capacidad;

                double heuristica = 0.5 * distanciaInversa + 0.5 * deficitCapacidad;

                // Añadimos nuestra estación para poder estudiarla más adelante
                puntos.add(new estacionCandidata(candidata.id, heuristica));
            }

            // Ordenamos nuestra lista de puntos para quedarnos con las 3 primeras
            puntos.sort((a,b) ->  Double.compare(b.heuristica, a.heuristica));

            // Variables que usaremos
            int tamLista = 3;
            int tamannoRCL = Math.min(puntos.size(), tamLista);

            // Nos quedaremos con las 3 primeras, siempre que haya al menos 3
            List<estacionCandidata> puntosRCL = puntos.subList(0, tamannoRCL);

            // El greedy probabilístico usa una ruleta ponderada;

            // Suma de la heuristica de cada uno de los elementos
            double sumaTotal = 0;
            for (estacionCandidata entrada : puntosRCL) {
                sumaTotal += entrada.heuristica;
            }

            // Generamos el numero para la ruleta
            double numAleatorio = rand.nextDouble() * sumaTotal;

            // Recorremos nuestra ruleta
            double resultado = 0;

            // Por si a caso hubiera error, elegiríamos la última estación de las guardadas
            int estacionElegida = puntosRCL.getLast().id;

            // Hacemos el bucle ahora si para nuestra ruleta
            for (estacionCandidata entrada : puntosRCL) {
                resultado += entrada.heuristica;
                if (numAleatorio <= resultado) {
                    estacionElegida = entrada.id;
                    break;
                }
            }

            // Buscamos toda la información de nuestra estación seleccionada
            Estacion elegida = null;
            for (Estacion e : noVisitadas) {
                if (e.id == estacionElegida) {
                    elegida = e;
                    break;
                }
            }

            // Guardamos
            solucion.add(elegida);

            // Por si hay errores
            assert elegida != null;

            equilibrarEstacion(elegida);
            noVisitadas.remove(elegida);
            estActual = elegida.id;

        }
        return solucion;
    }
    private record estacionCandidata(int id, double heuristica){}
}

