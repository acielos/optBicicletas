package main.java.Modelo;

import java.util.*;

import main.java.DataTypes.*;

public abstract class Algoritmo {

    // Dataset de estaciones y estado del camión
    protected List<Estacion> listaEstaciones;
    protected Camion camion = new Camion();

    public List<double[]> historialExplotacion = new ArrayList<>();

    // Semillas para los algoritmos
    protected long[] semilla = {12345L, 67890L, 11111L, 54321L, 99999L};

    protected double mejorFuncionObjetivo = Double.POSITIVE_INFINITY;

    // Matriz de distancias entre todas las estaciones
    protected Double[][] distancias;

    protected DistanciaManhattan distanciaManhattan = new DistanciaManhattan();

    // Resultado de la ejecución
    public List<Estacion> recorrido = new ArrayList<>();
    public double distanciaRecorrida = 0.0;
    public double entropiaFinal = 0.0;
    public double fObjetivo = 0.0;
    public int numEvaluaciones = 0;

    // Genéticos...
    protected List<Individuo> poblacion;
    protected int tamPoblacion = 30;
    protected int maxGeneraciones = 1000;
    protected Individuo mejorIndividuo;
    protected int torneo = 3;
    protected double probCruce;
    protected double porMutacion;
    protected double radioNicho = 4;
    protected double expSh = 1;

    // Método que cada algoritmo debe implementar
    public abstract void run();

    protected void calcularDistancias() {
        for (int i = 0; i < listaEstaciones.size(); i++) {
            distancias[i][i] = Double.POSITIVE_INFINITY;
            for (int j = 0; j < i; j++) {
                distancias[i][j] = distanciaManhattan.calculaDistancia(
                        listaEstaciones.get(i), listaEstaciones.get(j));
                distancias[j][i] = distancias[i][j];
            }
        }
    }

    protected double calcularFObjetivo(double kms, List<Estacion> estaciones) {
        double alpha = 1.5;
        numEvaluaciones++;
        double entropia  = calcularEntropiaTotal(estaciones);
        double nEstaciones = estaciones.size() -1;

        double fobj = kms + alpha * (nEstaciones - entropia);
        //historialExplotacion.add(new double[]{numEvaluaciones, fobj, mejorFuncionObjetivo});
        return fobj;
    }

    protected double calcularEntropiaTotal(List<Estacion> estaciones) {
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
        this.camion.reset();

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

    // Método para hacer una mutacion
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
    protected ArrayList<Estacion> generarSolucionInicial(Random rand) {
        // Trabajamos con una copia
        List<Estacion> copia = Dataset.copiaDataset(listaEstaciones);

        // Partimos para que la primera no se mueva
        ArrayList<Estacion> resto = new ArrayList<>(copia.subList(1, copia.size()));

        // Mezclamos
        Collections.shuffle(resto, rand);

        // rejuntamos
        resto.addFirst(copia.getFirst());

        // Devolvemos
        return resto;
    }

    // Método para mostrar los resultados obtenidos
    protected void mostrarResultados(String algoritmo){
        System.out.println("\n--- Resultado " + algoritmo + " ---");
        System.out.printf("Recorrido: ");
        for (Estacion e : this.recorrido) System.out.print(e.id + " ");
        System.out.println("-> 0");

        System.out.printf("Kilómetros recorridos : %.4f km%n", this.distanciaRecorrida);
        System.out.printf("Función objetivo      : %.4f%n", this.mejorFuncionObjetivo);
        System.out.printf("Evaluaciones          : %d%n", this.numEvaluaciones);
        System.out.printf("%nCarga final del camión: %d/%d bicis%n", this.camion.carga, this.camion.getCapacidad());


        System.out.println("\nEstado final de las estaciones:");
        System.out.printf("%-6s %-10s %-10s %-8s%n", "ID", "Carga", "Capacidad", "% ocup.");
        for (Estacion e : recorrido) {
            double pct = 100.0 * e.carga / e.capacidad;
            System.out.printf("%-6d %-10d %-10d %.1f%%%n", e.id, e.carga, e.capacidad, pct);
        }
    }

    protected void guardarDatos(String algoritmo, int semilla) {
        String nombreFichero = "historial_" + algoritmo + "_semilla" + semilla + "_Caso " + ".txt";
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(nombreFichero))) {
            pw.println("eval;fObjActual;mejorFObj");
            for (double[] punto : historialExplotacion) {
                pw.printf("%.0f;%.4f;%.4f%n", punto[0], punto[1], punto[2]);
            }
            System.out.println("[HISTORIAL guardado en: " + nombreFichero + "]");
        } catch (java.io.IOException e) {
            System.err.println("Error guardando historial: " + e.getMessage());
        }
    }

    protected void inicializarPoblacion(Random rand){
        this.poblacion.clear();
        for (int i = 0; i < this.tamPoblacion; i++) {
            ArrayList<Estacion> cromosoma = generarSolucionInicial(rand);
            Individuo individuo = new Individuo(cromosoma);
            this.poblacion.add(individuo);
        }
    }

    protected void evaluarPoblacion(List<Individuo> poblacion){
        for (Individuo indi:poblacion) {
            if (indi != null) {
                this.camion.reset();
                List<Estacion> equilibrado = recomponer(indi.cromosoma);
                indi.distanciaIndividuo = distanciaManhattan.calculaCompleto(equilibrado);
                indi.fitnessIndividuo = calcularFObjetivo(indi.distanciaIndividuo, equilibrado);
                indi.entropiaIndividuo = calcularEntropiaTotal(equilibrado);
            }
        }
    }

    protected boolean tiene(List<Estacion> lista, Estacion e){
        for (Estacion est:lista){
            if (est != null && est.id == e.id){return true;}
        }
        return false;
    }

    protected void actualizar(){
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

    protected int distanciaArco(Individuo a, Individuo b) {
        Set<String> arcosB = new HashSet<>();
        int tam = b.cromosoma.size();

        for (int i = 0; i < tam; i++) {
            int x = b.cromosoma.get(i).id;
            int y = b.cromosoma.get((i + 1)%tam).id;
            arcosB.add(x + "-" + y);
        }

        int diferencias = 0;
        for (int i = 0; i < tam; i++) {
            int x = a.cromosoma.get(i).id;
            int y = a.cromosoma.get((i + 1)%tam).id;
            if (!arcosB.contains(x + "-" + y)) {
                diferencias++;
            }
        }

        return diferencias;
    }

    protected void reemplazo(List<Individuo> herederos, Random rand) {
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

    protected boolean completo(List<Estacion> lista){
        for (Estacion est:lista){
            if (est == null){return false;}
        }
        return true;
    }

    protected Individuo mutarIndividuo(Individuo padre, Random rand) {

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

    protected List<Individuo> cruzarOX(Individuo padre, Individuo madre, Random rand){
        List<Individuo> resultado = new ArrayList<>();
        int tamanno = padre.cromosoma.size();
        int inicio = 1 + rand.nextInt(tamanno - 2);
        int fin = inicio + rand.nextInt(tamanno - inicio);
        if (fin >= tamanno) {
            fin = tamanno - 1;
        }

        // Primer hijo
        ArrayList<Estacion> cromosomaHijo = new ArrayList<>(Collections.nCopies(tamanno, null));

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
        ArrayList<Estacion> cromosomaHija = new ArrayList<>(Collections.nCopies(tamanno, null));

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

    protected List<Individuo> cruzarPoblacion(Random rand){
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
        evaluarPoblacion(hijos);

        return hijos;
    }

    protected Individuo seleccionTornero(Random rand) {
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




}