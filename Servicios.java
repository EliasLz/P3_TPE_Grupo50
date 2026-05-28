import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Servicios {
//Completar con las estructuras y métodos privados que se requieran

/*
* Expresar la complejidad temporal del constructor. O(N*N)
*/
    private HashMap<String, Paquete> paquetesPorCodigo;
    private List<Paquete> conAlimentos;
    private List<Paquete> sinAlimentos;
    private Tree paquetesPorUrgencia; 
    /*/PROBLEMA DE MI CLASE  TREE: 
    NO TIENE TECNICA DE BALANCEO, CASO DE QUE LOS PAQUETES SE INSERTEN EN ORDEN, PODRIA GENERAR UNA ENREDADERA */

    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>();
        conAlimentos = new ArrayList<>();
        sinAlimentos = new ArrayList<>();
        paquetesPorUrgencia = new Tree();

        try {
            BufferedReader br = new BufferedReader(new FileReader(pathPaquetes));
            int total = Integer.parseInt(br.readLine());
            String linea;
            while((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0]);
                String codigo = partes[1];
                int peso = Integer.parseInt(partes[2]);
                boolean tieneAlimentos = partes[3].equals("1");
                int urgencia = Integer.parseInt(partes[4]);

                Paquete p = new Paquete(id, codigo, peso, tieneAlimentos, urgencia);
                paquetesPorCodigo.put(codigo, p);
                paquetesPorUrgencia.add(p);
                if(tieneAlimentos) conAlimentos.add(p);
                else sinAlimentos.add(p);
            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
/*
* Expresar la complejidad temporal del servicio 1. ---> O(1)

*/
    public Paquete servicio1(String codigoPaquete) {
        return paquetesPorCodigo.get(codigoPaquete);
    }

    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if(contieneAlimentos) return conAlimentos;
        return sinAlimentos;
    }

    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        return paquetesPorUrgencia.searchRange(urgenciaMinima, urgenciaMaxima);
    }

    public List<Camion> backtracking(List<Camion> camiones, List<Paquete> paquetes){
        HashSet<Paquete> asignados= new HashSet<>();
        List<Camion> solucion= new ArrayList<Camion>();
        for(Paquete p: paquetes){
            backtrackingHelper(camiones,paquetes,p,asignados,solucion,0,0,0);
        }
        /*faltaria que retorne el precio no asignado y la metrica para analizar el costo de la solucion */
        return solucion;
    }
    private void backtrackingHelper(List<Camion> camiones,List<Paquete> paquetes, Paquete p, HashSet<Paquete> asignados, List<Camion> solucion, int pesoActual, int pesoTotal, int indexPaquete){
        if(indexPaquete==paquetes.size()){
            pesoActual=getPesoNoAsignados(asignados, paquetes);
            if(pesoActual<pesoTotal){
                pesoTotal=pesoActual;
                solucion.addAll(camiones);
            }
        }else{
            for(Camion c: camiones){
                if(p.isConAlimentos()){
                    if(c.isRefrigerado()){
                        if(c.asignarPaquete(p)){
                        asignados.add(p);
                        backtrackingHelper(camiones, paquetes, p, asignados, solucion, pesoActual, pesoTotal, indexPaquete+1);
                        asignados.remove(p);
                        c.removerPaquete(p);
                        }
                    }
                }else{
                    if(c.asignarPaquete(p)){
                        asignados.add(p);
                        backtrackingHelper(camiones, paquetes, p, asignados, solucion, pesoActual, pesoTotal, indexPaquete+1);
                        asignados.remove(p);
                        c.removerPaquete(p);
                    }
                }
            }
        }
}
private int getPesoNoAsignados(HashSet<Paquete> asignados, List<Paquete> paquetes){
    int peso=0;
    for (Paquete p : paquetes) {
        if(!asignados.contains(p)){
            peso+=p.getPeso();
        }
    }
    return peso;
}
}