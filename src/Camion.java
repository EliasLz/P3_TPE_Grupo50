import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Camion implements Comparable<Camion> {
    private final int id;
    private final String patente;
    private final boolean refrigerado;
    private int capacidadActual;
    private final List<Paquete> paquetesAsignados;

    public Camion(int id, String patente, boolean refrigerado, int capacidadActual) {
        this.id = id;
        this.patente = patente;
        this.refrigerado = refrigerado;
        this.capacidadActual = capacidadActual;
        this.paquetesAsignados = new ArrayList<>();
    }

    public Camion(int id, int capacidadActual) {
        this.id = id;
        this.patente = null;
        this.refrigerado = false;
        this.capacidadActual = capacidadActual;
        this.paquetesAsignados = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public boolean esRefrigerado() {
        return refrigerado;
    }

    public List<Paquete> getPaquetesAsignados() {
        return new ArrayList<>(paquetesAsignados);
    }

    public boolean asignarPaquete(Paquete paquete) {
        if (this.capacidadActual < paquete.getPeso()) {
            return false;
        }
        this.paquetesAsignados.add(paquete);
        this.capacidadActual -= paquete.getPeso();
        return true;
    }

    public int getCapacidadActual() {
        return capacidadActual;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camion otroCamion = (Camion) o;
        return id == otroCamion.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // Los camiones tienen criterio de desempate para que el Tree distinga camiones del mismo peso
    @Override
    public int compareTo(Camion otroCamion){
        if(otroCamion== null){
            throw new NullPointerException("Estás pasando un parametro nulo");
        }
        int resultado = Integer.compare(this.capacidadActual, otroCamion.getCapacidadActual());
        if(resultado==0){
            resultado=Integer.compare(this.id, otroCamion.getId());
        }
        return resultado;
    }

    public static class CompararPorCapacidadActual implements Comparator<Camion> {
        @Override
        public int compare(Camion c1, Camion c2) {
            if(c1 == null || c2 == null){
               throw new NullPointerException("Estás pasando un parametro nulo");
            }
            return Integer.compare(c1.getCapacidadActual(), c2.getCapacidadActual());
        }
    }

    @Override
    public String toString() {
        return "Camion{" +
                "id=" + this.id +
                ", capacidadActual=" + this.capacidadActual +
                ", es refri=" + this.refrigerado +
                ", paquetes asignados=" + this.paquetesAsignados.size() +
                '}';
    }

    public void setCapacidadActual(int capacidadActual) {
        this.capacidadActual = capacidadActual;
    }

}
