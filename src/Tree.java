import java.util.ArrayList;
import java.util.List;

public class Tree {

    private TreeNode root;

    public Tree() {
        this.root = null;
    }

    public void add(Paquete paquete) {
        if (this.root == null)
            this.root = new TreeNode(paquete);
        else
            add(this.root, paquete);
    }

    private void add(TreeNode actual, Paquete paquete) {
        if (actual.getClaveUrgencia() > paquete.getUrgencia()) {
            if (actual.getLeft() == null)
                actual.setLeft(new TreeNode(paquete));
            else
                add(actual.getLeft(), paquete);
        } else {
            if (actual.getRight() == null)
                actual.setRight(new TreeNode(paquete));
            else
                add(actual.getRight(), paquete);
        }
    }

    /**
     * Busca nodos dentro de un rango
     * @param min Límite inferior del rango (inclusive)
     * @param max Límite superior del rango (inclusive)
     * @return Collección de paquetes dentro del rango (ordenada)
     */
    public List<Paquete> searchRange(int min, int max) {
        List<Paquete> result = new ArrayList<>();
        searchRange(this.root, min, max, result);
        return result;
    }

    /**
     * Busqueda recursiva en el BTS con poda según el rango (ordenada)
     * @param n      Nodo actal
     * @param min    Minimo del rango
     * @param max    Máximo del rango
     * @param result Colección de valores dentro de rango hallados (acumulador)
     */
    private void searchRange(TreeNode n, int min, int max, List<Paquete> result) {
        if (n == null) return;
        int clave = n.getClaveUrgencia();
        if (clave > min) {
            searchRange(n.getLeft(), min, max, result);
        }
        if (clave >= min && clave <= max){
            result.add(n.getPaquete());
        }
        if (clave < max) {
            searchRange(n.getRight(), min, max, result);
        }
    }

}