    import java.util.List;

    public class Main {

        public static void main(String[] args) {

            Servicios servicios = new Servicios("resources/camiones.csv", "resources/paquetes.csv");
            
        
            System.out.println("=== BACKTRACKING ===");
            List<Camion> solucionBT = servicios.backtracking();
             solucionBT.forEach(System.out::println);
            System.out.println("Peso no asignado: " + servicios.getMejorPesoNoAsignado() + " kg.");
            System.out.println("Estados generados: " + servicios.getEstadosGenerados());

        
            System.out.println("\n=== GREEDY ===");
            List<Camion> solucionGreedy = servicios.greedy();
            int pesoNoAsignadoGreedy = servicios.getPesoNoAsignadoGreedy();
            solucionGreedy.forEach(System.out::println);
            System.out.println("Peso no asignado: " + pesoNoAsignadoGreedy + " kg.");
            System.out.println("Candidatos considerados: " + servicios.candidatosConsiderados);
        }
    }
