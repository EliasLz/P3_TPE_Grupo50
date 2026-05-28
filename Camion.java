import java.util.ArrayList;
import java.util.List;

public class Camion{
    private int id;
    private String patente;
    private boolean refrigerado;
    private int capacidadMaxima;
    private int capacidadActual;
   
    private List<Paquete> paquetesAsignados;
    public Camion(int id, String patente, boolean refrigerado, int capacidadMaxima){
        this.id=id;
        this.patente=patente;
        this.refrigerado=refrigerado;
        this.capacidadMaxima=capacidadMaxima;
        this.capacidadActual=0;
        this.paquetesAsignados=new ArrayList<>();
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
    public boolean  asignarPaquete(Paquete paquete) {
        if(capacidadActual + paquete.getPeso() > capacidadMaxima) {
          return false;
        }
        paquetesAsignados.add(paquete);
        capacidadActual += paquete.getPeso();
        return true;
    }
    public void removerPaquete(Paquete paquete){
        paquetesAsignados.remove(paquete); /* podria mejorarse ya que arraylist 
                                            recorre todos los paquetes para borrar O(n)*/
        capacidadActual-=paquete.getPeso();
    }
     public int getCapacidadActual() {
        return capacidadActual;
    }
}
