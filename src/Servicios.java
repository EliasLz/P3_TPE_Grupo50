import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

    /*
     Complejidad del constructor: O(C log C + P log P)
     C = camines, por c/u (n) se hace un add de un BTS (log n)
     P = paquetes, por c/u (n) se hace un add de un BTS (log n)
     Aclaración: Se asume que los arboles no se encuentran extremadamente desbalanceados
    */
    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>(); //usado por: serv1
        conAlimentos = new ArrayList<>();    //usado por: serv2
        sinAlimentos = new ArrayList<>();    //usado por: serv2
        paquetesPorUrgencia = new Tree<>();  //usado por: serv3
        camiones = new ArrayList<>();        //usado por: parte2  (greedy no lo usa - bt?)
        btsCamionesRefri = new Tree<>();     //usado por: parte2
        btsCamionesNoRefri = new Tree<>();   //usado por: parte2
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
     * @implNote Complejidad: O(1) en promedio, considerando posibles colisiones de hash.
     * en un caso extremo donde justo todas las claves generaron el mismo indice hash, sería O(n).
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
     * @implNote Complejidad: O(n), devuelve copia
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
        /* Aclaración: Se pasan las referencias con valores id tipo entero minimo y maximo para asegurarnos de
         que, en la busqueda el nodo, siempre gane los desempates contra el minimo y los pierda contra el maximo,
         a fin de que alcance todos los nodos del mismo valor del primer critero de comparacion (urgencia) */
        Paquete paqueteReferenciaMin = new Paquete(Integer.MIN_VALUE, urgenciaMinima);
        Paquete paqueteReferenciaMax = new Paquete(Integer.MAX_VALUE, urgenciaMaxima);

        return paquetesPorUrgencia.searchRange(paqueteReferenciaMin, paqueteReferenciaMax);
    }

    /**
     * Asigna todos los paquetes a los camiones indicados.
     * Retorna el resultado del proceso: paquetes no asignados y métricas de la ejecución
     *
     * @return retornar peso total rechazado
     * @implNote Metodo Greedy: Estrategia
     * para cada paquete de una lista de paquetes con alimento:
     * → buscar en bstRefrigerados
     * → asignar o acumular en lista de rechazados
     * para cada paquete de una lista de paquetes sin alimento:
     * → buscar en el bst de camiones no refrigedaros y en el bst de los refrigerados
     * → comparar y asignar al mejor resultado o acumular en lista de rechazados.
     * Complejidad: O(P log C)  Donde P = Cantidad paquetes, C = Cantidad camiones
     */
    // Aclaración: esta solución asume que los camiones refrigerados pueden contener paquetes con y sin alimentos
    public metricasAsignacion asignarGreedy() {
        // Declaramos valores resultado
        ArrayList<Paquete> paquetesSinAsignar = new ArrayList<>();
        int contadorPasosTotalesDelMetodo = 0;

        /* Aclaración: Se realiza en 2 pasos para evitar llenar los camiones refrigerados
         de paquetes sin alimentos prematuramente */

        //1. Asignamos paquetes con alimentos (ordenados de mayor a menor)
        for (Paquete p : this.conAlimentos) {

            // Seleccion de candidato: Buscamos el camion que tenga una capacidadActual igual o la mayor más cercana a peso del paquete
            Camion camionIdealReferencia = new Camion(0, p.getPeso());
            Comparator<Camion> comparadorSinDesempate = new Camion.CompararPorCapacidadActual();

            Camion c = this.btsCamionesRefri.obtenerIgualGrandeMasCercano(camionIdealReferencia, comparadorSinDesempate);
            /* Aclaración: Se utiliza como referencia de comparacion un Camion de capacidadActual igual al peso del
            paquete actual, porque el arbol es generico de comparables, usa el compareTo() que desempata por id para
            ordenar y calcular ruta de busqueda y un Comparator sin desempate en funciones especificas que necesitan
            identificar iguales. En este caso nunca recorre luego de un igual, porque corta y devuelve, no hay desempate
            en el recorrido. El valor id (0) que se da es indiferente para este metodo, se agrega porque hashCodeporque al encontrar un igual corta este metodo calcula la ruta de busqueda (compareTo)
            pueda con que desempatar, es decir la funcion utiliza ambos comparadores */

            //Verificamos si el camion candidato es factible
            if (c != null && c.esRefrigerado()) {
                this.ejecutarAsignacion(p,c);
                contadorPasosTotalesDelMetodo += this.btsCamionesRefri.getCantPasosUltimoAdd();
                contadorPasosTotalesDelMetodo += this.btsCamionesRefri.getCantPasosUltimoRemove();
            } else {
                paquetesSinAsignar.add(p);
            }
            contadorPasosTotalesDelMetodo += this.btsCamionesRefri.getCantPasosUltimoGrandeMasCercano();
            contadorPasosTotalesDelMetodo++;
        }

        //2. Asignamos paquetes sin alimentos (ordenados de mayor a menor)
        for (Paquete p : this.sinAlimentos) {
            Camion cnr = this.btsCamionesNoRefri.obtenerIgualGrandeMasCercano(new Camion(0, p.getPeso()), new Camion.CompararPorCapacidadActual());
            Camion cr = this.btsCamionesRefri.obtenerIgualGrandeMasCercano(new Camion(0, p.getPeso()), new Camion.CompararPorCapacidadActual());

            //Asignamos al camimon que menos le falte para completar su capacidad
            Camion mejorCandidato = this.camionConCapacidadOptima(cnr, cr);
            if (mejorCandidato != null) {
                this.ejecutarAsignacion(p, mejorCandidato);
                if(mejorCandidato.esRefrigerado()) {
                    contadorPasosTotalesDelMetodo += this.btsCamionesRefri.getCantPasosUltimoAdd();
                    contadorPasosTotalesDelMetodo += this.btsCamionesRefri.getCantPasosUltimoRemove();
                } else {
                    contadorPasosTotalesDelMetodo += this.btsCamionesNoRefri.getCantPasosUltimoAdd();
                    contadorPasosTotalesDelMetodo += this.btsCamionesNoRefri.getCantPasosUltimoRemove();
                }
            } else {
                paquetesSinAsignar.add(p);
            }
            contadorPasosTotalesDelMetodo += this.btsCamionesRefri.getCantPasosUltimoGrandeMasCercano();
            contadorPasosTotalesDelMetodo += this.btsCamionesNoRefri.getCantPasosUltimoGrandeMasCercano();
            contadorPasosTotalesDelMetodo++;
        }
        return calcularMetricasAsignacion(paquetesSinAsignar, contadorPasosTotalesDelMetodo);
    }

    private Camion camionConCapacidadOptima(Camion noRefri, Camion refri) {
        if (noRefri == null)
            return refri;
        if (refri == null)
            return noRefri;
        Camion camionOptimo = noRefri;
        if (refri.getCapacidadActual() <= noRefri.getCapacidadActual()){
            camionOptimo = refri;
        }
        return camionOptimo;
    }

    //Al asiganar un paquete, se elimina y vuelve a agregar el camion para que el árbol se mantenga ordenado
    private void ejecutarAsignacion(Paquete p, Camion c){
        if(c.esRefrigerado()){
            this.btsCamionesRefri.remove(c);
            c.asignarPaquete(p);
            this.btsCamionesRefri.add(c);
        } else {
            this.btsCamionesNoRefri.remove(c);
            c.asignarPaquete(p);
            this.btsCamionesNoRefri.add(c);
        }
    }

    /*
    public resultadoAsignacion asignarBT(){

    FALTA IMLEMENTAR EL BACKTACKING

*/

    private metricasAsignacion calcularMetricasAsignacion(List<Paquete> sinAsignar, int cantPasosAsignador) {
        int pesoTotalSinAsignar = 0;
        for (Paquete p : sinAsignar) {
            pesoTotalSinAsignar += p.getPeso();
        }
        int cantSinAsignar = sinAsignar.size();
        int cantAsignados = paquetesPorCodigo.size() - sinAsignar.size();
        return new metricasAsignacion(sinAsignar, pesoTotalSinAsignar, cantSinAsignar, cantAsignados, cantPasosAsignador);
    }

    public record metricasAsignacion(
            List<Paquete> paquetesSinAsignar,
            int pesoSinAsignar,
            int cantSinAsignar,
            int cantAsignados,
            int cantPasosAsignador
    ) {
    }

    /**
     * Carga camiones desde un archivo CSV
     *
     * @param pathCamiones Ruta del archivo CSV
     * @implNote Complejidad: O(n)
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
     * @implNote Complejidad: O(n)
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

    @Override
    public String toString() {
        return "Servicios{" +
                "\n paquetesPorCodigo=" + paquetesPorCodigo +
                "\n conAlimentos=" + conAlimentos +
                "\n sinAlimentos=" + sinAlimentos +
                "\n paquetesPorUrgencia=" + paquetesPorUrgencia +
                "\n camiones=" + camiones +
                "\n btsCamionesRefri=" + btsCamionesRefri +
                "\n btsCamionesNoRefri=" + btsCamionesNoRefri +
                '}';
    }

    public List<Camion> getCamionesRefriBTS() {
        return this.btsCamionesRefri.getElementosPreorder();
    }

    public List<Camion> getCamionesNoRefriBTS() {
        return this.btsCamionesNoRefri.getElementosPreorder();
    }

    public List<Paquete> getPaquetesBTS() {
        return paquetesPorUrgencia.getElementosPreorder();
    }


}
