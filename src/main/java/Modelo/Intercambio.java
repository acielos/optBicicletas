package main.java.Modelo;

import main.java.DataTypes.*;
import java.util.*;

public class Intercambio {

    public List<Estacion> cambiar(List<Estacion> lista){
        // Rango para la generación de números aleatorios
        int min = 1;
        int max = lista.size()-1;

        // Nuestras primeras posiciones a cambiar
        int[] num = {0, 0};

        // Generamos las dos posiciones que vamos a cambiar
        for (int i = 0; i < 2; i++) {
            num[i] = (int)(Math.random() * (max - min + 1) + min);
        }

        while (num[0] == num[1]) {
            num[1] = (int)(Math.random() * (max - min + 1) + min);
        }

        // Asignamos a estas variables los valores que vamos a cambiar
        Estacion pos1 = lista.get(num[1]);
        Estacion pos2 = lista.get(num[0]);

        // Cambiamos las posiciones
        lista.set(num[0], pos1);
        lista.set(num[1], pos2);

        return lista;

    }

}
