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
    private Tree paquetesPorUrgencia;
    private List<Camion> camiones;
    private List<Paquete> paquetes;
    private int mejorPesoNoAsignado;

    public int estadosGenerados = 0;
    public int candidatosConsiderados = 0;

    //ver
    //yo
    // Complejidad constructor: O(n²) si está completamente desbalanceado, O(n log n) si está balanceado.
   //gero
    /* O(M + N log N) caso promedio, O(M + N^2) peor caso. donde M=camiones N=Paquetes */
    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>();
        conAlimentos = new ArrayList<>();
        sinAlimentos = new ArrayList<>();
        paquetesPorUrgencia = new Tree();
        camiones = new ArrayList<>();
        paquetes = new ArrayList<>();
        this.cargarCamiones(pathCamiones);
        this.cargarPaquetes(pathPaquetes);
    }

    /**
     * Devuelve el paquete asociado a un código específico
     * @implNote Complejidad: O(1) promedio, considerando posibles colisiones de hash
     * @param codigoPaquete Identificador único del paquete a buscar
     * @return El objeto Paquete correspondiente al código, o null si no lo encuentra
     */
    public Paquete servicio1(String codigoPaquete) {
        return paquetesPorCodigo.get(codigoPaquete);
    }

    /**
     * Devuelve una colección de paquetes filtrada por si tienen o no alimentos
     * @implNote Complejidad: O(n), se crea y devuelve una copia
     * @param contieneAlimentos El tipo de colección solicitada
     *                          True: con alimentos
     *                          False: sin alimentos
     * @return Una nueva lista con los paquetes que cumplen la condición
     */
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if (contieneAlimentos) {
            return new ArrayList<>(this.conAlimentos);
        }
        return new ArrayList<>(this.sinAlimentos);
    }

    /**
     * Devuelve una colección de peques según un rango de urgencia
     * @implNote Complejidad: O(n), el rango puede abarcar todo el arbol
     * @param urgenciaMinima Límite inferior del rango
     * @param urgenciaMaxima Límite superior del rango
     * @return Colección de paquetes dentro del rango
     */
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        return paquetesPorUrgencia.searchRange(urgenciaMinima, urgenciaMaxima);
    }

    /*
     * Estrategia Backtracking: se explora el espacio de soluciones asignando cada
     * paquete
     * a algún camión disponible o dejándolo sin asignar. Para cada paquete se
     * prueban
     * todas las opciones válidas (respetando capacidad y refrigeración),
     * actualizando
     * la mejor solución cuando se minimiza el peso no asignado.
     * Poda: si el pesoNoAsignado actual ya supera el mejor encontrado, se corta la
     * rama.
     * Complejidad: O((M+1)^N) peor caso, donde N=paquetes y M=camiones.
     * La poda reduce drásticamente los estados en la práctica (13 estados en el
     * ejemplo).
     */
    public List<Camion> backtracking() {
        estadosGenerados = 0;
        int pesoTotal = 0;
        for (Paquete p : paquetes)
            pesoTotal += p.getPeso();

        mejorPesoNoAsignado = pesoTotal;
        int[] mejorPesoArr = { pesoTotal };
        List<Camion> mejorSolucion = new ArrayList<>();

        backtrackingHelper(0, pesoTotal, mejorPesoArr, mejorSolucion);
        mejorPesoNoAsignado = mejorPesoArr[0];
        return mejorSolucion;
    }

    private void backtrackingHelper(int indexPaquete, int pesoNoAsignado,
            int[] mejorPesoNoAsignado, List<Camion> mejorSolucion) {
        estadosGenerados++;
        if (indexPaquete == paquetes.size()) {
            if (pesoNoAsignado < mejorPesoNoAsignado[0]) {
                mejorPesoNoAsignado[0] = pesoNoAsignado;
                mejorSolucion.clear();
                mejorSolucion.addAll(copiarSolucion());
            }
            return;
        }
        if (pesoNoAsignado > mejorPesoNoAsignado[0])
            return;

        Paquete p = paquetes.get(indexPaquete);

        for (Camion c : camiones) {
            if (p.isConAlimentos() && !c.isRefrigerado())
                continue; // camion invalido para el paquete, salto al proximo
            if (c.asignarPaquete(p)) {

                backtrackingHelper(indexPaquete + 1, pesoNoAsignado - p.getPeso(),
                        mejorPesoNoAsignado, mejorSolucion);
                c.removerPaquete(p);
            }
        }

        backtrackingHelper(indexPaquete + 1, pesoNoAsignado, mejorPesoNoAsignado, mejorSolucion);
    }

    /*
     * Estrategia Greedy: se ordenan los paquetes de mayor a menor peso para asignar
     * primero los más difíciles de ubicar (mas pesados). Para cada paquete se
     * selecciona el camión
     * con menor espacio disponible que aún pueda recibir el paquete actual,
     * aprovechando el espacio restante en camiones con mayor capacidad.
     * Complejidad: O(N log N + N*M) donde N=paquetes y M=camiones.
     */
    public List<Camion> greedy() {
        candidatosConsiderados = 0;

        List<Paquete> paquetesOrdenados = new ArrayList<>(paquetes);
        paquetesOrdenados.sort((a, b) -> b.getPeso() - a.getPeso());

        for (Paquete p : paquetesOrdenados) {
            Camion mejorCamion = null;
            int menorEspacioDisponible = Integer.MAX_VALUE;

            for (Camion c : camiones) {

                candidatosConsiderados++;
                if (p.isConAlimentos() && !c.isRefrigerado())
                    continue;
                int espacioDisponible = c.getCapacidadMaxima() - c.getCapacidadActual();
                if (espacioDisponible >= p.getPeso() && espacioDisponible < menorEspacioDisponible) {
                    menorEspacioDisponible = espacioDisponible;
                    mejorCamion = c;
                }
            }

            if (mejorCamion != null)
                mejorCamion.asignarPaquete(p);
        }

        return camiones;
    }

    /**
     * Carga camiones desde un archivo CSV
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
                int capacidadMaxima = Integer.parseInt(partes[3].trim());
                Camion c = new Camion(id, patente, refrigerado, capacidadMaxima);
                camiones.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga Paquetes desde un archivo CSV
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
                paquetes.add(p);
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

    private List<Camion> copiarSolucion() {
        List<Camion> copia = new ArrayList<>();

        for (Camion c : camiones) {
            copia.add(new Camion(c));
        }

        return copia;
    }

    public List<Camion> getCamiones() {
        return camiones;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public int getMejorPesoNoAsignado() {
        return mejorPesoNoAsignado;
    }

}
