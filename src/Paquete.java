import java.util.HashMap;

public class Paquete {
    private int id;
    private String codigoPaquete;
    private int peso;
    private boolean conAlimentos;
    private int urgencia;

    public Paquete(int id, String codigo, int peso, boolean conAlimentos, int urgencia) {
        this.id = id;
        this.codigoPaquete = codigo;
        this.peso = peso;
        this.conAlimentos = conAlimentos;
        if(urgencia<1){
            urgencia=1;
        }
        if(urgencia>100){
            urgencia=100;
        }
        this.urgencia = urgencia;
    }

    public int getId() {
        return id;
    }

    public String getCodigoPaquete() {
        return codigoPaquete;
    }

    public int getPeso() {
        return peso;
    }

    public boolean isConAlimentos() {
        return conAlimentos;
    }

    public int getUrgencia() {
        return urgencia;
    }


}

