package main.java.Modelo;

import main.java.DataTypes.Dataset;
import main.java.DataTypes.Estacion;

import java.util.ArrayList;
import java.util.List;

public class Individuo {
    // Parámetros que usaremos para evaluar el individuo
    public List<Estacion> cromosoma;
    public double distanciaIndividuo;
    public double entropiaIndividuo;
    public double fitnessIndividuo;
    public double fitnessSeleccion;

    public Individuo(List<Estacion> dataset) {
        cromosoma = Dataset.copiaDataset(dataset);
        distanciaIndividuo = 0;
        entropiaIndividuo = 0;
        fitnessIndividuo = 0;
        fitnessSeleccion = 0;
    }

    public Individuo(Individuo individuo) {
        cromosoma = Dataset.copiaDataset(individuo.cromosoma);
        distanciaIndividuo = individuo.distanciaIndividuo;
        entropiaIndividuo = individuo.entropiaIndividuo;
        fitnessIndividuo = individuo.fitnessIndividuo;
        fitnessSeleccion = individuo.fitnessSeleccion;
    }

    public Individuo clonarIndividuo() {
        return new Individuo(this);
    }

}
