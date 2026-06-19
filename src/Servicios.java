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
    private PaqueteTree paquetesPorUrgencia;
    private List<Camion> camiones;
    private List<Paquete> paquetes;
    private int candidatosConsiderados = 0;
    private int estadosGenerados;
    private int pesoNoAsignadoGreedy;

    /*
     * O(M + N log N) caso promedio, O(M + N^2) peor caso. donde M=camiones
     * N=Paquetes
     */
    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>();
        conAlimentos = new ArrayList<>();
        sinAlimentos = new ArrayList<>();
        paquetesPorUrgencia = new PaqueteTree();
        camiones = new ArrayList<>();
        paquetes = new ArrayList<>();
        this.cargarCamiones(pathCamiones);
        this.cargarPaquetes(pathPaquetes);
    }

    // O(1)
    public Paquete servicio1(String codigoPaquete) {
        return paquetesPorCodigo.get(codigoPaquete);
    }

    // O(1) 
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if (contieneAlimentos)
            return this.conAlimentos;
        return this.sinAlimentos;
    }

    // O(log n + k) caso promedio. O(N) peor caso (arbol desbalanceado o todos los
    // nodos estan dentro del rango)
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        return paquetesPorUrgencia.searchRange(urgenciaMinima, urgenciaMaxima);
    }

    /*
     * Estrategia Backtracking: se explora el espacio de soluciones asignando cada
     * paquete a algún camión disponible o dejándolo sin asignar. Para cada paquete
     * se
     * prueban todas las opciones válidas (respetando capacidad y refrigeración),
     * actualizando la mejor solución cuando se minimiza el peso no asignado.
     * Poda: si el pesoNoAsignado actual ya supera el mejor encontrado, se corta la
     * rama.
     * Complejidad: O((M+1)^N) peor caso, donde N=paquetes y M=camiones.
     */
    private int mejorPesoNoAsignado;
    private List<Camion> mejorSolucion;

    public List<Camion> backtracking() {
        this.estadosGenerados = 0;
        this.mejorSolucion = new ArrayList<>();
        int pesoTotalPaquetes = 0;
        for (Paquete paquete : paquetes) {
            pesoTotalPaquetes += paquete.getPeso();
        }
        this.mejorPesoNoAsignado = pesoTotalPaquetes;
        asignarPaquetesRecursivo(0, 0);
        return mejorSolucion;
    }

    private void asignarPaquetesRecursivo(int indicePaqueteActual, int pesoNoAsignadoAcumulado) {
        this.estadosGenerados++;

        if (indicePaqueteActual == paquetes.size()) {
            if (pesoNoAsignadoAcumulado < mejorPesoNoAsignado) {
                mejorPesoNoAsignado = pesoNoAsignadoAcumulado;
                mejorSolucion = copiarSolucion();
            }
            return;
        }

        Paquete paqueteActual = paquetes.get(indicePaqueteActual);
        for (Camion camion : camiones) {
            if (pesoNoAsignadoAcumulado >= mejorPesoNoAsignado)
                break; 
            if (puedeAsignarsePorRefrigeracion(camion, paqueteActual)) {
                if (camion.asignarPaquete(paqueteActual)) {
                    asignarPaquetesRecursivo(indicePaqueteActual + 1, pesoNoAsignadoAcumulado);
                    camion.removerPaquete(paqueteActual);
                }
            }
        }

        int pesoSiNoAsignado = pesoNoAsignadoAcumulado + paqueteActual.getPeso();
        if (pesoSiNoAsignado < mejorPesoNoAsignado) {
            asignarPaquetesRecursivo(indicePaqueteActual + 1, pesoSiNoAsignado);
        }
    }

    private boolean puedeAsignarsePorRefrigeracion(Camion camion, Paquete paquete) {
        return !paquete.isConAlimentos() || camion.isRefrigerado();
    }

    /*
     * Estrategia Greedy: se ordenan los paquetes de mayor a menor peso para asignar
     * primero los más difíciles de ubicar (mas pesados). Para cada paquete se
     * selecciona el camión con menor espacio disponible que aún pueda recibir el
     * paquete actual,
     * aprovechando el espacio restante en camiones con mayor capacidad.
     * En vez de recorrer linealmente todos los camiones (O(M)) para cada paquete,
     * los camiones se organizan en arboles binarios de busqueda ordenados por su
     * espacio
     * disponible (capacidadMaxima - capacidadActual).
     * Se usan dos ABB: uno para camiones refrigerados y otro para camiones
     * normales, de manera que la restriccion de
     * "paquete con alimentos -> camion refrigerado"
     * se resuelve eligiendo directamente el arbol correcto, sin perder la
     * complejidad logaritmica.
     * Como la capacidad disponible de un camion cambia cada vez que recibe un
     * paquete, una vez elegido el mejor camion se lo quita del arbol, se le
     * asigna el paquete (lo que modifica su espacio disponible) y se lo vuelve
     * a insertar en su nueva posicion correcta, conservando la propiedad de ABB.
     * Complejidad: O(N log N) para ordenar los paquetes + O(M log M) para
     * construir los arboles + O(N log M) para las busquedas/actualizaciones.
     */
    public List<Camion> greedyConArboles() {
        candidatosConsiderados = 0;
        pesoNoAsignadoGreedy = 0;

        CamionTree arbolNormales = new CamionTree();
        CamionTree arbolRefrigerados = new CamionTree();

        for (Camion c : camiones) {
            if (c.isRefrigerado())
                arbolRefrigerados.add(c);
            else
                arbolNormales.add(c);
        }

        List<Paquete> paquetesOrdenados = new ArrayList<>(paquetes);
        paquetesOrdenados.sort((a, b) -> b.getPeso() - a.getPeso());

        for (Paquete p : paquetesOrdenados) {
            Camion mejorCamion;

            if (p.isConAlimentos()) {
                mejorCamion = arbolRefrigerados.buscarMejorAjuste(p.getPeso());
            } else {
                Camion candidatoNormal = arbolNormales.buscarMejorAjuste(p.getPeso());
                Camion candidatoRefrigerado = arbolRefrigerados.buscarMejorAjuste(p.getPeso());
                mejorCamion = elegirMenorEspacio(candidatoNormal, candidatoRefrigerado);
            }

            if (mejorCamion != null) {
                CamionTree arbolDestino = mejorCamion.isRefrigerado() ? arbolRefrigerados : arbolNormales;
                arbolDestino.remove(mejorCamion);
                mejorCamion.asignarPaquete(p);
                arbolDestino.add(mejorCamion);
            } else {
                pesoNoAsignadoGreedy += p.getPeso();
            }
        }

        candidatosConsiderados = arbolNormales.getNodosVisitados() + arbolRefrigerados.getNodosVisitados();
        return camiones;
    }

    private Camion elegirMenorEspacio(Camion a, Camion b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        int espacioA = a.getCapacidadLibre();
        int espacioB = b.getCapacidadLibre();
        return (espacioA <= espacioB) ? a : b;
    }

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

    public int getCandidatosConsideradosGreedy() {
        return candidatosConsiderados;
    }

    public int getEstadosGenerados() {
        return estadosGenerados;
    }

    public int getPesoNoAsignadoGreedy() {
        return pesoNoAsignadoGreedy;
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