import java.util.Comparator;
import java.util.Objects;

public class Paquete implements Comparable<Paquete> {
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

    public Paquete(int id, int urgencia) {
        this.id = id;
        this.codigoPaquete = null;
        this.peso = 0;
        this.conAlimentos = false;
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

    public boolean contieneAlimentos() {
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
                ", contiene alimentos=" + this.conAlimentos +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paquete otroPaquete = (Paquete) o;
        return this.id == otroPaquete.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Por default se compara por urgencia
    @Override
    public int compareTo(Paquete otroPaquete) {
        if(otroPaquete== null){
            throw new NullPointerException("Estás pasando un parametro nulo");
        }
        int resultado = Integer.compare(this.urgencia, otroPaquete.getUrgencia());
        if(resultado==0){
            resultado=Integer.compare(this.id, otroPaquete.getId());
        }
        return resultado;
    }

    // Creamos una clase comparadora de Paquete para poder ordenarlos de mayor a menor, invirtiendo parametos
    public static class CompararPorPesoInvertido implements Comparator<Paquete> {

        @Override
        public int compare(Paquete p1, Paquete p2) {
            if(p1 == null || p2 == null){
                throw new NullPointerException("Estás pasando un parametro nulo");
            }
            return Integer.compare(p2.getPeso(), p1.getPeso());
        }
    }
}
