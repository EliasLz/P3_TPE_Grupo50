import java.util.ArrayList;
import java.util.List;

public class Camion {
    private int id;
    private String patente;
    private boolean refrigerado;
    private int capacidadMaxima;
    private int capacidadActual;
    private List<Paquete> paquetesAsignados;

    public Camion(int id, String patente, boolean refrigerado, int capacidadMaxima) {
        this.id = id;
        this.patente = patente;
        this.refrigerado = refrigerado;
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadActual = 0;
        this.paquetesAsignados = new ArrayList<>();
    }

    public Camion(Camion otro) {
        this.id = otro.id;
        this.patente = otro.patente;
        this.refrigerado = otro.refrigerado;
        this.capacidadMaxima = otro.capacidadMaxima;
        this.capacidadActual = otro.capacidadActual;
        this.paquetesAsignados = new ArrayList<>(otro.paquetesAsignados);
    }

    public int getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public boolean isRefrigerado() {
        return refrigerado;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public List<Paquete> getPaquetesAsignados() {
        return new ArrayList<>(paquetesAsignados);
    }

    public boolean asignarPaquete(Paquete paquete) {
        if (capacidadActual + paquete.getPeso() > capacidadMaxima) {
            return false;
        }
        paquetesAsignados.add(paquete);
        capacidadActual += paquete.getPeso();
        return true;
    }

    public void removerPaquete(Paquete paquete) {
        paquetesAsignados.remove(paquete);
        capacidadActual -= paquete.getPeso();
    }

    public int getCapacidadActual() {
        return capacidadActual;
    }

    public int getCapacidadLibre() {
        return this.getCapacidadMaxima() - this.getCapacidadActual();
    }

    public String toString() {
        return "Camion " + this.getId() + " (" + this.getPatente() + "): " + this.getPaquetesAsignados();
    }
}
