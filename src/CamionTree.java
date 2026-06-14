public class CamionTree {

    private CamionTreeNode root;
    private int nodosVisitados;

    public int getNodosVisitados() { return nodosVisitados; }

    public void add(Camion camion) {
        root = add(root, camion);
    }

    private CamionTreeNode add(CamionTreeNode actual, Camion camion) {
        if (actual == null) return new CamionTreeNode(camion);

        int espacioNuevo = camion.getCapacidadLibre();

        if (espacioNuevo < actual.getCapacidadLibre())
            actual.setLeft(add(actual.getLeft(), camion));
        else
            actual.setRight(add(actual.getRight(), camion));

        return actual;
    }

    public Camion buscarMejorAjuste(int peso) {
        CamionTreeNode resultado = buscarMejorAjuste(root, peso);
        return (resultado != null) ? resultado.getCamion() : null;
    }

    private CamionTreeNode buscarMejorAjuste(CamionTreeNode actual, int peso) {
        if (actual == null) return null;
        nodosVisitados++;

        if (actual.getCapacidadLibre() < peso) {
            return buscarMejorAjuste(actual.getRight(), peso);
        } else {
            CamionTreeNode izquierda = buscarMejorAjuste(actual.getLeft(), peso);
            return (izquierda != null) ? izquierda : actual;
        }
    }

    public void remove(Camion camion) {
        int espacio = camion.getCapacidadMaxima() - camion.getCapacidadActual();
        root = remove(root, camion, espacio);
    }

    private CamionTreeNode remove(CamionTreeNode actual, Camion camion, int espacio) {
        if (actual == null) return null;

        int espacioActual = actual.getCapacidadLibre();

        if (espacio < espacioActual) {
            actual.setLeft(remove(actual.getLeft(), camion, espacio));
            return actual;
        }

        if (espacio == espacioActual && actual.getCamion() == camion) {
            if (actual.getLeft() == null) return actual.getRight();
            if (actual.getRight() == null) return actual.getLeft();

            CamionTreeNode sucesor = minimo(actual.getRight());
            Camion camionSucesor = sucesor.getCamion();
            int espacioSucesor = sucesor.getCapacidadLibre();
            actual.setCamion(camionSucesor);
            actual.setRight(remove(actual.getRight(), camionSucesor, espacioSucesor));
            return actual;
        }

        // espacio > espacioActual, o es un duplicado de otro camion -> va a la derecha
        actual.setRight(remove(actual.getRight(), camion, espacio));
        return actual;
    }

    private CamionTreeNode minimo(CamionTreeNode n) {
        while (n.getLeft() != null) n = n.getLeft();
        return n;
    }
}