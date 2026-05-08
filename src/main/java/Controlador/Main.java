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
            System.out.println("    5. Generar Gráficas (No usar)");
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
                        System.out.println("    0. Salir");
                        System.out.println("==========================================");
                        System.out.print("      Escoga una opción -> ");
                        opc3 = sc.nextInt();
                        if (opc3 < 0 || opc3 > 5) {
                            System.out.println("** ERROR - POR FAVOR INTRODUZCA UN VALOR VÁLIDO **");
                        }

                        switch (opc3) {
                            case 1:
                                ejecutarAlgoritmo(opc3, dataset);
                                break;
                            case 2:
                                ejecutarAlgoritmo(opc3, dataset);
                                break;
                            case 3:
                                ejecutarAlgoritmo(opc3, dataset);
                                break;
                            case 4:
                                ejecutarAlgoritmo(opc3, dataset);
                                break;
                            case 5:
                                ejecutarAlgoritmo(opc3, dataset);
                                break;
                        }
                    } while (opc3 != 0);

                    break;
                case 4:
                    for (int i = 0; i <= 5; i++){
                        ejecutarAlgoritmo(i, dataset);
                    }

                    break;
                case 5:
                    System.out.println("Opción no disponible");
                    //generarGraficas();
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
        }
    }

//    private static void generarGraficas() throws Exception {
//        String[] algoritmos  = {"BLPM", "GRASP", "ILS", "VNS"};
//        String[] algoBoxplot = {"GRASP", "ILS", "VNS"};
//        String[] casos       = {"Caso1", "Caso2", "Caso3"};
//        int numSemillas      = 5;
//
//        String raiz = new java.io.File(".").getAbsolutePath();
//        raiz = raiz.endsWith(".") ? raiz.substring(0, raiz.length() - 1) : raiz;
//        System.out.println("Raíz detectada: " + raiz);
//
//        String sep       = java.io.File.separator;
//        String dirSalida = raiz + "out" + sep + "graficas" + sep;
//        new java.io.File(dirSalida).mkdirs();
//
//        java.awt.Color[] coloresSemilla = {
//                new java.awt.Color(0x1565C0), new java.awt.Color(0x388E3C),
//                new java.awt.Color(0xF57C00), new java.awt.Color(0x7B1FA2),
//                new java.awt.Color(0xC62828)
//        };
//        java.awt.Color[] coloresAlgo = {
//                new java.awt.Color(0xE53935), new java.awt.Color(0x43A047),
//                new java.awt.Color(0xFB8C00), new java.awt.Color(0x8E24AA)
//        };
//
//        // ── GRÁFICA 1: Historial de búsqueda por algoritmo+caso ──────────────
//        for (int a = 0; a < algoritmos.length; a++) {
//            for (String caso : casos) {
//                org.jfree.data.xy.XYSeriesCollection dataset = new org.jfree.data.xy.XYSeriesCollection();
//
//                for (int s = 0; s < numSemillas; s++) {
//                    String fichero = raiz + "Casos" + sep + caso + sep
//                            + "historial_" + algoritmos[a] + "_semilla" + s + "_" + caso + ".txt";
//
//                    org.jfree.data.xy.XYSeries serieActual = new org.jfree.data.xy.XYSeries("F. Actual S" + (s+1), false);
//                    org.jfree.data.xy.XYSeries serieMejor  = new org.jfree.data.xy.XYSeries("F. Mejor S"  + (s+1), false);
//
//                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichero))) {
//                        br.readLine(); // cabecera
//                        String line;
//                        double mejorAcumulado = Double.MAX_VALUE;
//                        while ((line = br.readLine()) != null) {
//                            String[] p = line.trim().replace(",", ".").split(";");
//                            if (p.length < 3) continue;
//                            if (p[1].equals("Infinity")) continue;
//                            double ev      = Double.parseDouble(p[0]);
//                            double fActual = Double.parseDouble(p[1]);
//                            double fMejor;
//                            if (p[2].equals("Infinity")) {
//                                fMejor = mejorAcumulado;
//                            } else {
//                                fMejor = Double.parseDouble(p[2]);
//                                if (fMejor < mejorAcumulado) mejorAcumulado = fMejor;
//                            }
//                            if (mejorAcumulado == Double.MAX_VALUE) continue;
//                            serieActual.add(ev, fActual);
//                            serieMejor .add(ev, fMejor);
//                        }
//                        System.out.println(algoritmos[a] + " S" + s + " " + caso
//                                + " → " + serieMejor.getItemCount() + " puntos");
//                    } catch (java.io.IOException e) {
//                        System.out.println("No encontrado: " + fichero);
//                    }
//                    dataset.addSeries(serieActual);
//                    dataset.addSeries(serieMejor);
//                }
//
//                org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart(
//                        "Historial de Búsqueda — " + algoritmos[a] + " (" + caso + ")",
//                        "Evaluaciones", "Función Objetivo",
//                        dataset, org.jfree.chart.plot.PlotOrientation.VERTICAL, true, false, false
//                );
//
//                org.jfree.chart.plot.XYPlot plot = chart.getXYPlot();
//                plot.setBackgroundPaint(java.awt.Color.WHITE);
//                chart.setBackgroundPaint(java.awt.Color.WHITE);
//                plot.setRangeGridlinePaint(new java.awt.Color(0xDDDDDD));
//                plot.setDomainGridlinePaint(new java.awt.Color(0xDDDDDD));
//                plot.setOutlineVisible(false);
//
//                org.jfree.chart.renderer.xy.XYLineAndShapeRenderer renderer =
//                        new org.jfree.chart.renderer.xy.XYLineAndShapeRenderer(true, false);
//                for (int s = 0; s < numSemillas; s++) {
//                    java.awt.Color base = coloresSemilla[s];
//                    renderer.setSeriesPaint(s * 2,
//                            new java.awt.Color(base.getRed(), base.getGreen(), base.getBlue(), 55));
//                    renderer.setSeriesStroke(s * 2, new java.awt.BasicStroke(0.9f));
//                    renderer.setSeriesVisibleInLegend(s * 2, false);
//                    renderer.setSeriesPaint(s * 2 + 1, coloresSemilla[s]);
//                    renderer.setSeriesStroke(s * 2 + 1, new java.awt.BasicStroke(2.2f));
//                }
//                plot.setRenderer(renderer);
//                chart.getLegend().setBackgroundPaint(java.awt.Color.WHITE);
//
//                String salida = dirSalida + "historial_" + algoritmos[a] + "_" + caso + ".png";
//                org.jfree.chart.ChartUtils.saveChartAsPNG(new java.io.File(salida), chart, 1000, 580);
//                System.out.println("Generada: " + salida);
//            }
//        }
//
//        // ── GRÁFICA 2: Convergencia media por caso (4 algoritmos juntos) ─────
//        for (String caso : casos) {
//            org.jfree.data.xy.XYSeriesCollection dataset = new org.jfree.data.xy.XYSeriesCollection();
//
//            for (int a = 0; a < algoritmos.length; a++) {
//                List<List<Double>> todasSemillas = new ArrayList<>();
//
//                for (int s = 0; s < numSemillas; s++) {
//                    String fichero = raiz + "Casos" + sep + caso + sep
//                            + "historial_" + algoritmos[a] + "_semilla" + s + "_" + caso + ".txt";
//                    List<Double> mejores = new ArrayList<>();
//                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichero))) {
//                        br.readLine();
//                        String line;
//                        double mejorAcumulado = Double.MAX_VALUE;
//                        while ((line = br.readLine()) != null) {
//                            String[] p = line.trim().replace(",", ".").split(";");
//                            if (p.length < 3) continue;
//                            double fMejor;
//                            if (p[2].equals("Infinity")) {
//                                fMejor = mejorAcumulado;
//                            } else {
//                                fMejor = Double.parseDouble(p[2]);
//                                if (fMejor < mejorAcumulado) mejorAcumulado = fMejor;
//                            }
//                            if (mejorAcumulado == Double.MAX_VALUE) continue;
//                            mejores.add(fMejor);
//                        }
//                    } catch (java.io.IOException ignored) {}
//                    if (!mejores.isEmpty()) todasSemillas.add(mejores);
//                }
//                if (todasSemillas.isEmpty()) continue;
//
//                int minLen = todasSemillas.stream().mapToInt(List::size).min().getAsInt();
//                org.jfree.data.xy.XYSeries serie = new org.jfree.data.xy.XYSeries(algoritmos[a], false);
//                for (int i = 0; i < minLen; i++) {
//                    final int idx = i;
//                    double media = todasSemillas.stream()
//                            .mapToDouble(l -> l.get(idx)).average().orElse(0);
//                    serie.add((double) i, media);
//                }
//                dataset.addSeries(serie);
//            }
//
//            org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart(
//                    "Convergencia Media — " + caso,
//                    "Evaluaciones", "F. Objetivo (media)",
//                    dataset, org.jfree.chart.plot.PlotOrientation.VERTICAL, true, false, false
//            );
//
//            org.jfree.chart.plot.XYPlot plot = chart.getXYPlot();
//            plot.setBackgroundPaint(java.awt.Color.WHITE);
//            chart.setBackgroundPaint(java.awt.Color.WHITE);
//            plot.setRangeGridlinePaint(new java.awt.Color(0xDDDDDD));
//            plot.setDomainGridlinePaint(new java.awt.Color(0xDDDDDD));
//            plot.setOutlineVisible(false);
//
//            org.jfree.chart.renderer.xy.XYLineAndShapeRenderer renderer =
//                    new org.jfree.chart.renderer.xy.XYLineAndShapeRenderer(true, false);
//            for (int a = 0; a < algoritmos.length; a++) {
//                renderer.setSeriesPaint(a,  coloresAlgo[a]);
//                renderer.setSeriesStroke(a, new java.awt.BasicStroke(2.5f));
//            }
//            plot.setRenderer(renderer);
//            chart.getLegend().setBackgroundPaint(java.awt.Color.WHITE);
//
//            String salida = dirSalida + "convergencia_media_" + caso + ".png";
//            org.jfree.chart.ChartUtils.saveChartAsPNG(new java.io.File(salida), chart, 1000, 580);
//            System.out.println("Generada: " + salida);
//        }
//
//        // ── GRÁFICA 3: Boxplot por caso (GRASP vs ILS vs VNS) ────────────────
//        for (String caso : casos) {
//            org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset dataset =
//                    new org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset();
//
//            for (String algo : algoBoxplot) {
//                List<Double> valoresFinales = new ArrayList<>();
//
//                for (int s = 0; s < numSemillas; s++) {
//                    String fichero = raiz + "Casos" + sep + caso + sep
//                            + "historial_" + algo + "_semilla" + s + "_" + caso + ".txt";
//                    double ultimoMejor = Double.NaN;
//                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichero))) {
//                        br.readLine();
//                        String line;
//                        String lastValid = null;
//                        while ((line = br.readLine()) != null) {
//                            String[] p = line.trim().replace(",", ".").split(";");
//                            if (p.length == 3 && !p[2].equals("Infinity"))
//                                lastValid = line;
//                        }
//                        if (lastValid != null) {
//                            String[] p = lastValid.trim().replace(",", ".").split(";");
//                            ultimoMejor = Double.parseDouble(p[2]);
//                        }
//                    } catch (java.io.IOException ignored) {}
//                    if (!Double.isNaN(ultimoMejor)) valoresFinales.add(ultimoMejor);
//                }
//
//                if (!valoresFinales.isEmpty()) {
//                    dataset.add(valoresFinales, algo, caso);
//                    System.out.println("Boxplot " + algo + " " + caso + " → " + valoresFinales);
//                }
//            }
//
//            org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createBoxAndWhiskerChart(
//                    "Boxplot Función Objetivo Final — " + caso,
//                    "Algoritmo", "F. Objetivo Final",
//                    dataset, true
//            );
//
//            org.jfree.chart.plot.CategoryPlot plot = (org.jfree.chart.plot.CategoryPlot) chart.getPlot();
//            plot.setBackgroundPaint(java.awt.Color.WHITE);
//            chart.setBackgroundPaint(java.awt.Color.WHITE);
//            plot.setRangeGridlinePaint(new java.awt.Color(0xDDDDDD));
//            plot.setOutlineVisible(false);
//
//            org.jfree.chart.renderer.category.BoxAndWhiskerRenderer renderer =
//                    new org.jfree.chart.renderer.category.BoxAndWhiskerRenderer();
//            renderer.setFillBox(true);
//            renderer.setMeanVisible(true);
//            renderer.setMedianVisible(true);
//
//            java.awt.Color[] coloresBoxplot = {
//                    new java.awt.Color(0x43A047, true),
//                    new java.awt.Color(0xFB8C00, true),
//                    new java.awt.Color(0x8E24AA, true)
//            };
//            for (int i = 0; i < algoBoxplot.length; i++) {
//                renderer.setSeriesPaint(i, coloresBoxplot[i]);
//                renderer.setSeriesOutlinePaint(i, coloresBoxplot[i].darker());
//            }
//            plot.setRenderer(renderer);
//            plot.getRangeAxis().setAutoRange(true);
//            chart.getLegend().setBackgroundPaint(java.awt.Color.WHITE);
//
//            String salida = dirSalida + "boxplot_" + caso + ".png";
//            org.jfree.chart.ChartUtils.saveChartAsPNG(new java.io.File(salida), chart, 800, 580);
//            System.out.println("Generada: " + salida);
//        }
//
//        System.out.println("\n✓ Todas las gráficas en: " + dirSalida);
//    }
}

