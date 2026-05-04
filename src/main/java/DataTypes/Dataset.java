package main.java.DataTypes;


import java.io.*;
import java.util.*;

public class Dataset {
    // Método para leer los datasets
    public static List<Estacion> leerFicheros(String nombre) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(nombre))) {
            String linea;
            int dimension = -1;

            List<Estacion> lecturaDataset = new ArrayList<>();

            // Leer cabecera hasta NODE_COORD_SECTION
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                if (linea.startsWith("DIMENSION")) {
                    String[] partes = linea.split(":");
                    if (partes.length > 1) {
                        dimension = Integer.parseInt(partes[1].trim());
                    }
                }

                if (linea.startsWith("NODE_COORD_SECTION")) {
                    break; // Empezar a leer coordenadas a partir de aquí
                }
            }

            Estacion[] dataset = new Estacion[dimension];
            int count = 0;

            // Leer coordenadas hasta EOF o hasta 'dimension' líneas válidas
            while ((linea = br.readLine()) != null && count < dimension) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (linea.equalsIgnoreCase("EOF")) break;

                // TSPLIB separa por uno o más espacios; usar regex de espacio en blanco
                String[] tokens = linea.split("\\s+");
                if (tokens.length >= 3) {
                    int indice = Integer.parseInt(tokens[0]);
                    double latitud = Double.parseDouble(tokens[1]);
                    double longitud = Double.parseDouble(tokens[2]);

                    lecturaDataset.add(new Estacion(indice, latitud, longitud));
                }
            }

            // Si hubiese menos puntos que la DIMENSION declarada, recortar el array
            if (count != dimension) {
                dataset = java.util.Arrays.copyOf(dataset, count);
            }
            return lecturaDataset;
        }
    }

    // Método para mostrar el dataset cargado
    public static void mostrarDataset(List<Estacion> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            System.out.println("ERROR - DATASET NO CARGADO");
            return;
        }

        System.out.println("\n\n *** DATASET CARGADO CORRECTAMENTE *** \n");
        System.out.println("Nº de Estaciones: " + dataset.size());
        System.out.println("Listado de estaciones:");

        for (Estacion e : dataset) {
            System.out.println("  " + e);
        }
    }

    // Método para copiar los datasets
    // public static List<Estacion> copiaDataset(List<Estacion> dataset) { return new ArrayList<>(dataset);}

    public static List<Estacion> copiaDataset(List<Estacion> dataset) {
        List<Estacion> copia = new ArrayList<>(dataset.size());
        for (Estacion e : dataset) {
            Estacion nueva = new Estacion(e.id, e.latitud, e.longitud);
            nueva.carga     = e.carga;
            nueva.capacidad = e.capacidad;
            copia.add(nueva);
        }
        return copia;
    }



}
