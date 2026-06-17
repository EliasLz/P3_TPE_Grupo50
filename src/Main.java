    import java.util.HashSet;
    import java.util.List;

    public class Main {

        public static void main(String[] args) {

            Servicios serviciosTest123 = new Servicios ("resources/camiones.csv", "resources/paquetes.csv");

        //    System.out.println("=== TEST CARGA VALORES CSV y BTS ===");
        //    System.out.println(servicios);
        //    System.out.println("Valores preorder del btsPaquetes para ver si ordena bien: " + serviciosTest123.getPaquetesBTS());


        // TEST SERVICIO 1
            Paquete resultadoPOO1 = serviciosTest123.servicio1("P001");
            Paquete resultadoCodInv = serviciosTest123.servicio1("Este codigo paquete no existe");

            System.out.println("\n" + "=== TEST SERVICIO 1 ===");
            System.out.println("Llamado con código valido: " + resultadoPOO1);
            System.out.println("Llamado con código invalido: " + resultadoCodInv);


        // TEST SERVICIO 2
            List<Paquete> resultadoTrue = serviciosTest123.servicio2(true);
            List<Paquete> resultadoFalse = serviciosTest123.servicio2(false);

            System.out.println("\n" + "=== TEST SERVICIO 2 ===");
            System.out.println("Llamado con true: Cantidad Paquetes = " + resultadoTrue.size() + " || Detalle: " + resultadoTrue);
            System.out.println("Llamado con false: Cantidad Paquetes = " + resultadoFalse.size() + " || Detalle: " + resultadoFalse);


        // TEST SERVICIO 3
            List<Paquete> resultadoRangoConCoincidencias = serviciosTest123.servicio3(10, 80);
            List<Paquete> resultadoRangoCompleto = serviciosTest123.servicio3(0, 100);

            System.out.println("\n" + "=== TEST SERVICIO 3 ===");
            System.out.println("Llamado con rango valido: " + resultadoRangoConCoincidencias);
            System.out.println("Llamado con rango invalido: " + resultadoRangoCompleto);


        // TEST SOLUCION GREEDY
            Servicios serviciosTestGreedy = new Servicios("resources/camionesParaGreedy.csv", "resources/paquetesParaGreedy.csv");

            System.out.println("\n" + "=== TEST GREEDY ===");
            System.out.println("\n VALORES PREVIOS A LA ASIGNACION: " + serviciosTestGreedy.getPaquetesBTS().size() + " Paquetes - " + (serviciosTestGreedy.getCamionesRefriBTS().size() + serviciosTestGreedy.getCamionesNoRefriBTS().size()) + " Camiones" );
            System.out.println("Paquetes con alimentos de mayor a menor peso: " + serviciosTestGreedy.servicio2(true));
            System.out.println("Paquetes sin alimentos de mayor a menor peso: " + serviciosTestGreedy.servicio2(false));
            System.out.println("Estado capacidad Camiones Refrigerados: " + serviciosTestGreedy.getCamionesRefriBTS());
            System.out.println("Estado capacidad Camiones No-refrigerados: " + serviciosTestGreedy.getCamionesNoRefriBTS());

            Servicios.metricasAsignacion datosSobreLaAsignacion = serviciosTestGreedy.asignarGreedy();

            System.out.println("\n VALORES TRAS LA ASIGNACION:");
            System.out.println("Datos asignacion Greedy: " + datosSobreLaAsignacion);
            System.out.println("Estado capacidad Camiones Refrigerados: " + serviciosTestGreedy.getCamionesRefriBTS());
            System.out.println("Estado capacidad Camiones No-refrigerados: " + serviciosTestGreedy.getCamionesNoRefriBTS());







            // TEST SOLUCION BACKTRACKING
            System.out.println("\n" + "=== TEST BACKTRACKING ===");


        }


    }