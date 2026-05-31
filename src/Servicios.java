import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Servicios implements ServiciosEnunciado {

    // Complejidad constructor: O(n²) si está completamente desbalanceado, O(n log n) si está balanceado.
    private HashMap<String, Paquete> paquetesPorCodigo;
    private List<Paquete> conAlimentos;
    private List<Paquete> sinAlimentos;
    private Tree paquetesPorUrgencia;

    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>();
        conAlimentos = new ArrayList<>();
        sinAlimentos = new ArrayList<>();
        paquetesPorUrgencia = new Tree();
        this.cargarCamiones(pathCamiones);
        this.cargarPaquetes(pathPaquetes);
    }

    // Complejidad servicio 1: O(1)
    public Paquete servicio1(String codigoPaquete) {
        return paquetesPorCodigo.get(codigoPaquete);
    }

    // Complejidad servicio 2: O(n)
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if (contieneAlimentos) return new ArrayList<>(this.conAlimentos);
        return new ArrayList<>(this.sinAlimentos);
    }

    // Complejidad servicio 3: O(n) si es lo resolvemos con acumulador, O(n²) como está
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        return paquetesPorUrgencia.searchRange(urgenciaMinima, urgenciaMaxima);
    }

    public List<Camion> backtracking(List<Camion> camiones, List<Paquete> paquetes) {
        HashSet<Paquete> asignados = new HashSet<>();
        List<Camion> solucion = new ArrayList<>();
        for (Paquete p : paquetes) {
            backtrackingHelper(camiones, paquetes, p, asignados, solucion, 0, 0, 0);
        }
        /*faltaria que retorne el precio no asignado y la metrica para analizar el costo de la solucion */
        return solucion;
    }

    private void backtrackingHelper(List<Camion> camiones, List<Paquete> paquetes, Paquete p, HashSet<Paquete> asignados, List<Camion> solucion, int pesoActual, int pesoTotal, int indexPaquete) {
        if (indexPaquete == paquetes.size()) {
            pesoActual = getPesoNoAsignados(asignados, paquetes);
            if (pesoActual < pesoTotal) {
                pesoTotal = pesoActual;
                solucion.addAll(camiones);
            }
        } else {
            for (Camion c : camiones) {
                if (p.isConAlimentos()) {
                    if (c.isRefrigerado()) {
                        if (c.asignarPaquete(p)) {
                            asignados.add(p);
                            backtrackingHelper(camiones, paquetes, p, asignados, solucion, pesoActual, pesoTotal, indexPaquete + 1);
                            asignados.remove(p);
                            c.removerPaquete(p);
                        }
                    }
                } else {
                    if (c.asignarPaquete(p)) {
                        asignados.add(p);
                        backtrackingHelper(camiones, paquetes, p, asignados, solucion, pesoActual, pesoTotal, indexPaquete + 1);
                        asignados.remove(p);
                        c.removerPaquete(p);
                    }
                }
            }
        }
    }

    private int getPesoNoAsignados(HashSet<Paquete> asignados, List<Paquete> paquetes) {
        int peso = 0;
        for (Paquete p : paquetes) {
            if (!asignados.contains(p)) {
                peso += p.getPeso();
            }
        }
        return peso;
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
                if (linea.isBlank()) {
                    continue;
                }
                String[] partes = linea.split(";", -1);
                int id = Integer.parseInt(partes[0].trim());
                String patente = partes[1].trim();
                boolean refrigerado = partes[3].trim().equals("1");
                int capacidadMaxima = Integer.parseInt(partes[4].trim());

                Camion c = new Camion(id, patente, refrigerado, capacidadMaxima);
                // Para despues: Agregar aquí el add a las estructuas de datos para camiones...
                //...
                //...
                //...

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
                //Salta la primera fila deberia guardar la cantidad de elementos para algo?
                if (first) {
                    first = false;
                    continue;
                }
                if (linea.isBlank()) {
                    continue;
                }
                String[] partes = linea.split(";", -1);
                int id = Integer.parseInt(partes[0].trim());
                String codigo = partes[1].trim();
                int peso = Integer.parseInt(partes[2].trim());
                boolean tieneAlimentos = partes[3].trim().equals("1");
                int urgencia = Integer.parseInt(partes[4].trim());

                Paquete p = new Paquete(id, codigo, peso, tieneAlimentos, urgencia);
                paquetesPorCodigo.put(codigo, p);
                paquetesPorUrgencia.add(p);
                if (tieneAlimentos) conAlimentos.add(p);
                else sinAlimentos.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}