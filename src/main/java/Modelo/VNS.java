package main.java.Modelo;

import main.java.DataTypes.*;

import java.util.*;

public class VNS extends Algoritmo {

    public VNS(List<Estacion> dataset) {
        this.listaEstaciones = dataset;
        this.distancias = new Double[dataset.size()][dataset.size()];
        calcularDistancias();
    }

    @Override
    public void run() {

    }
}
