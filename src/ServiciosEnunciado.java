import java.util.List;

public interface ServiciosEnunciado {

    public Paquete servicio1(String codigoPaquete);

    public List<Paquete> servicio2(boolean contieneAlimentos);

    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima);

}
