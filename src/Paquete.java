

public class Paquete {
    private final int id;
    private final String codigoPaquete;
    private final int peso;
    private final boolean conAlimentos;
    private final int urgencia;

    public Paquete(int id, String codigo, int peso, boolean conAlimentos, int urgencia) {
        this.id = id;
        this.codigoPaquete = codigo;
        this.peso = peso;
        this.conAlimentos = conAlimentos;
        if (urgencia < 1) {
            urgencia = 1;
        }
        if (urgencia > 100) {
            urgencia = 100;
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

    @Override
    public String toString() {
        return "Paquete{" +
                "id=" + this.id +
                ", codigo='" + this.codigoPaquete + '\'' +
                ", peso=" + this.peso +
                ", urgencia=" + this.urgencia +
                '}';
    }

}
