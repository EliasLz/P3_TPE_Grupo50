import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Servicios {

    private HashMap<String, Paquete> paquetesPorCodigo;
    private List<Paquete> conAlimentos;
    private List<Paquete> sinAlimentos;
    private Tree<Paquete> paquetesPorUrgencia;
    private List<Camion> camiones;
    private Tree<Camion> btsCamionesRefri;
    private Tree<Camion> btsCamionesNoRefri;


    //ver al final
    //yo
    // Complejidad constructor: O(n²) si está completamente desbalanceado, O(n log n) si está balanceado.
    //gero
    /* O(M + N log N) caso promedio, O(M + N^2) peor caso. donde M=camiones N=Paquetes */
    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>(); //para serv1
        conAlimentos = new ArrayList<>();    //para serv2
        sinAlimentos = new ArrayList<>();    //para serv2
        paquetesPorUrgencia = new Tree<>();  //para serv3
        camiones = new ArrayList<>();        //para parte2
        btsCamionesRefri = new Tree<>();    //para parte2
        btsCamionesNoRefri = new Tree<>();  //para parte2
        this.cargarCamiones(pathCamiones);
        this.cargarPaquetes(pathPaquetes);
        this.conAlimentos.sort(new Paquete.CompararPorPesoInvertido());
        this.sinAlimentos.sort(new Paquete.CompararPorPesoInvertido());

    }

    /**
     * Devuelve el paquete asociado a un código específico
     *
     * @param codigoPaquete Identificador único del paquete a buscar
     * @return El objeto Paquete correspondiente al código, o null si no lo encuentra
     * @implNote Complejidad: O(1) promedio, considerando posibles colisiones de hash
     */
    public Paquete servicio1(String codigoPaquete) {
        return paquetesPorCodigo.get(codigoPaquete);
    }

    /**
     * Devuelve una colección de paquetes filtrada por si tienen o no alimentos
     *
     * @param contieneAlimentos El tipo de colección solicitada
     *                          True: con alimentos
     *                          False: sin alimentos
     * @return Una nueva lista con los paquetes que cumplen la condición
     * @implNote Complejidad: O(n), se crea y devuelve una copia
     */
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if (contieneAlimentos) {
            return new ArrayList<>(this.conAlimentos);
        }
        return new ArrayList<>(this.sinAlimentos);
    }

    /**
     * Devuelve una colección de peques según un rango de urgencia
     *
     * @param urgenciaMinima Límite inferior del rango
     * @param urgenciaMaxima Límite superior del rango
     * @return Colección de paquetes dentro del rango
     * @implNote Complejidad: O(n), el rango puede abarcar todo el arbol
     */
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        // Se agregan valores id minimos y maximos para asegurarnos de que siempre pierdan el desempate al comparar,
        // a fin de que busque en ambos hijos si el primer criterio (urgencia) da empate
        return paquetesPorUrgencia.searchRange(new Paquete(Integer.MIN_VALUE, urgenciaMinima), new Paquete(Integer.MAX_VALUE, urgenciaMinima));
    }

    /**
     *
     * @return retornar peso total rechazado
     * @implNote Metodo Greedy: Comportamiento
     * para cada paquete de una lista de paquetes con alimento:
     * → buscar en bstRefrigerados
     * → asignar o acumular en lista de rechazados
     * para cada paquete de una lista de paquetes sin alimento::
     * → buscar en el bst de Camiones no refrigedaros y en el bst de los refrigerados
     * → comparar y asignar al mejor resultado o acumular en lista de rechazados
     * Complejidad: O(???)
     */
    public metricasAsignacion asignarGreedy() {
        ArrayList<Paquete> paquetesSinAsignar = new ArrayList<>();
        int contadorIterasionesPropiasDelMotodo = 0;

        //1. Asignamos paquetes con alimentos (ordenados de mayor a menor)
        for (Paquete p : this.conAlimentos) {
            // Seleccionar: Buscamos el camion que tenga una capacidadActual igual o la mayor más cercana a peso del paquete
            // Se utiliza como referencia comparacion un Camion de capacidadActual igual al peso del paquete candidato,
            // el valor id es indifirente, es solo para asegurar que cuando calcula la ruta de busqueda (compareTo) no pueda dar error
            Camion c = this.btsCamionesRefri.obtenerIgualGrandeMasCercano(new Camion(0, p.getPeso()), new Camion.CompararPorCapacidadActual());
            // Factible: Si existe un camion con la capacidad necesaria y cumple con los requerimientos de refrigeracion, se asigna
            if (c != null && this.requerimientoRefrigeracion(p, c)) {
                c.asignarPaquete(p);
            } else {
                paquetesSinAsignar.add(p);
            }
            contadorIterasionesPropiasDelMotodo++;
        }

        //2. Asignamos paquetes sin alimentos (ordenados de mayor a menor)
        for (Paquete p : this.sinAlimentos) {
            Camion cnr = this.btsCamionesNoRefri.obtenerIgualGrandeMasCercano(new Camion(0, p.getPeso()), new Camion.CompararPorCapacidadActual());
            Camion cr = this.btsCamionesRefri.obtenerIgualGrandeMasCercano(new Camion(0, p.getPeso()), new Camion.CompararPorCapacidadActual());
            if (cnr != null && cr != null) {
                //Asignamos al camimon que menos le falte para completar su capacidad
                if (cnr.getCapacidadActual() <= cr.getCapacidadActual()) {
                    cnr.asignarPaquete(p);
                } else {
                    cr.asignarPaquete(p);
                }
            } else if (cnr != null) {
                cnr.asignarPaquete(p);
            } else if (cr != null) {
                cr.asignarPaquete(p);
            } else {
                paquetesSinAsignar.add(p);
            }
            contadorIterasionesPropiasDelMotodo++;
        }
        return calcularMetricasAsignacion(paquetesSinAsignar, contadorIterasionesPropiasDelMotodo);
    }

    //Ver: está al pedo
    // Es un poco innecesario, porque siempre se llama para asignar un Paquete con alimento. Pero por si acaso
    private boolean requerimientoRefrigeracion(Paquete p, Camion c) {

        return !p.contieneAlimentos() || c.esRefrigerado();
    }
    

    /*
    public resultadoAsignacion asignarBT(){

    FALTA IMLEMENTAR EL BACKTACKING

*/

    public record metricasAsignacion(
            List<Paquete> paquetesSinAsignar,
            int pesoSinAsignar,
            int cantSinAsignar,
            int cantAsignados,
            int cantIteracionesAsignador
    ) {
    }

    private metricasAsignacion calcularMetricasAsignacion(List<Paquete> sinAsignar,
                                                          int iteracionesAsignador) {
        int pesoTotalSinAsignar = 0;
        for (Paquete p : sinAsignar) {
            pesoTotalSinAsignar += p.getPeso();
        }
        int cantAsignados = paquetesPorCodigo.size() - sinAsignar.size();
        // La variable "iteracionesAsignador" solo indica las propias del metodo BT o Greedy a modo de referencia, NO LAS TOTALES
        return new metricasAsignacion(sinAsignar, pesoTotalSinAsignar, sinAsignar.size(), cantAsignados, iteracionesAsignador);
    }

    /**
     * Carga camiones desde un archivo CSV
     *
     * @param pathCamiones Ruta del archivo CSV
     */
    private void cargarCamiones(String pathCamiones) {
        try (BufferedReader br = new BufferedReader(new FileReader(pathCamiones))) {
            String linea;
            boolean first = true;
            while ((linea = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (linea.isBlank())
                    continue;
                String[] partes = linea.split(";", -1);
                int id = Integer.parseInt(partes[0].trim());
                String patente = partes[1].trim();
                boolean refrigerado = partes[2].trim().equals("1");
                int capacidadActual = Integer.parseInt(partes[3].trim());
                Camion c = new Camion(id, patente, refrigerado, capacidadActual);
                camiones.add(c);
                if (c.esRefrigerado())
                    btsCamionesRefri.add(c);
                else
                    btsCamionesNoRefri.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga Paquetes desde un archivo CSV
     *
     * @param pathPaquetes Ruta del archivo CSV
     */
    private void cargarPaquetes(String pathPaquetes) {
        try (BufferedReader br = new BufferedReader(new FileReader(pathPaquetes))) {
            String linea;
            boolean first = true;
            while ((linea = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (linea.isBlank())
                    continue;
                String[] partes = linea.split(";", -1);
                int id = Integer.parseInt(partes[0].trim());
                String codigo = partes[1].trim();
                int peso = Integer.parseInt(partes[2].trim());
                boolean tieneAlimentos = partes[3].trim().equals("1");
                int urgencia = Integer.parseInt(partes[4].trim());
                Paquete p = new Paquete(id, codigo, peso, tieneAlimentos, urgencia);
                paquetesPorCodigo.put(codigo, p);
                paquetesPorUrgencia.add(p);
                if (tieneAlimentos)
                    conAlimentos.add(p);
                else
                    sinAlimentos.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Camion> getCamiones() {
        return camiones;
    }

    public Iterable<Paquete> getPaquetes() {
        return this.paquetesPorCodigo.values();
    }


}
