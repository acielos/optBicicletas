package main.java.Controlador;

import main.java.DataTypes.*;
import main.java.Modelo.*;
import java.io.IOException;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        // Abrimos scaner
        Scanner sc = new Scanner(System.in);

        // Para los tres casos que vamos a tener
        Casos casos = new Casos();

        // Variables qie lo mismo usamos en varios sitios
        String ruta = "././datasetBicis.tsp";
        List<Estacion> dataset = new ArrayList<>();

        int opcion;
        do {
            System.out.println(" ");
            System.out.println("    Aplicación Gestión de Estaciones");
            System.out.println("==========================================");
            System.out.println("    1. Cargar Dataset");
            System.out.println("    2. Mostrar Dataset");
            System.out.println("    3. Probar Estrategia");
            System.out.println("    4. Probar Todas las Estrategias");
            System.out.println("    0. Salir");
            System.out.println("==========================================");
            System.out.print("    Escoga una opción -> ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1:
                    int opc1 = 5;
                    while (opc1 != 1 && opc1 != 2 && opc1 != 3) {
                        System.out.println(" ");
                        System.out.println("            Carga de Dataset");
                        System.out.println("==========================================");
                        System.out.println("    1. Cargar 'Caso 1'");
                        System.out.println("    2. Cargar 'Caso 2'");
                        System.out.println("    3. Cargar 'Caso 3'");
                        System.out.println("==========================================");
                        System.out.print("      Escoga una opción -> ");
                        opc1 = sc.nextInt();
                        if (opc1 != 1 && opc1 != 2 && opc1 != 3) {
                            System.out.println("** ERROR - POR FAVOR INTRODUZCA UN VALOR VÁLIDO **");
                        }
                    }

                    dataset = Dataset.leerFicheros(ruta);
                    casos.aplicarCaso(dataset, opc1);

                    break;
                case 2:
                    if (dataset.isEmpty()) {
                        System.out.println("\nERROR - DATASET NO CARGADO\n");
                    }else{
                        Dataset.mostrarDataset(dataset);
                    }
                    break;
                case 3:
                    int opc3;
                    do {
                        System.out.println(" ");
                        System.out.println("           Probar Estrategia ");
                        System.out.println("==========================================");
                        System.out.println("    1. Greedy");
                        System.out.println("    2. Búsqueda Local: Primer Mejor");
                        System.out.println("    3. Grasp");
                        System.out.println("    4. ILS");
                        System.out.println("    5. VNS");
                        System.out.println("    6. Genético Simple");
                        System.out.println("    0. Salir");
                        System.out.println("==========================================");
                        System.out.print("      Escoga una opción -> ");
                        opc3 = sc.nextInt();
                        if (opc3 < 0 || opc3 > 6) {
                            System.out.println("** ERROR - POR FAVOR INTRODUZCA UN VALOR VÁLIDO **");
                        }

                        ejecutarAlgoritmo(opc3, dataset);

                    } while (opc3 != 0);

                    break;
                case 4:
                    for (int i = 1; i <= 6; i++){
                        ejecutarAlgoritmo(i, dataset);
                    }

                    break;
                default:
                    System.out.println("\n ***** Finalización del Programa ***** \n");
                    break;
            }
        }while(opcion!=0);
    }

    private static void ejecutarAlgoritmo(int op, List<Estacion> dataset){
        switch (op) {
            case 1:
                System.out.println(" **** Greedy **** ");
                Algoritmo greedy = new Greedy(dataset);
                greedy.run();
                break;
            case 2:
                System.out.println(" **** Búsqueda Local Primer Mejor **** ");
                Algoritmo busquedaLocalPM = new BusquedaLocalPM(dataset);
                busquedaLocalPM.run();
                break;
            case 3:
                System.out.println(" **** GRASP **** ");
                Algoritmo grasp = new GRASP(dataset);
                grasp.run();
                break;
            case 4:
                System.out.println(" **** ILS **** ");
                Algoritmo ils = new ILS(dataset);
                ils.run();
                break;
            case 5:
                System.out.println(" **** VNS **** ");
                Algoritmo vns = new VNS(dataset);
                vns.run();
                break;
            case 6:
                System.out.println(" **** Genético Simple **** ");
                Algoritmo gen = new Genetico(dataset);
                gen.run();
                break;
        }
    }

}

