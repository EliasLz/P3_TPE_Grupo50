import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        
        Servicios servicios = new Servicios("resources/camiones.csv", "resources/paquetes.csv");

    
        System.out.println("=== BACKTRACKING ===");
        List<Camion> solucionBT = servicios.backtracking();
        for (Camion c : solucionBT)
            System.out.println("Camion " + c.getId() + " (" + c.getPatente() + "): " + c.getPaquetesAsignados());
        System.out.println("Peso no asignado: " + servicios.getMejorPesoNoAsignado() + " kg.");
        System.out.println("Estados generados: " + servicios.estadosGenerados);

    
        System.out.println("\n=== GREEDY ===");
        List<Camion> solucionGreedy = servicios.greedy();
        int pesoNoAsignadoGreedy = 0;
        HashSet<Paquete> asignados = new HashSet<>();
        for (Camion c : solucionGreedy) {
            System.out.println("Camion " + c.getId() + " (" + c.getPatente() + "): " + c.getPaquetesAsignados());
            asignados.addAll(c.getPaquetesAsignados());
        }


        for (Paquete p : servicios.getPaquetes())
            if (!asignados.contains(p))
                pesoNoAsignadoGreedy += p.getPeso();
        System.out.println("Peso no asignado: " + pesoNoAsignadoGreedy + " kg.");
        System.out.println("Candidatos considerados: " + servicios.candidatosConsiderados);
    }
}
