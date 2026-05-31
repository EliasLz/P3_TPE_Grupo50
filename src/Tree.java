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
        if (actual.getUrgencia() > paquete.getUrgencia()) {
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

    // searchRange - busca todos los nodos dentro de un rango [min, max] inclusive
    public List<Paquete> searchRange(int min, int max) {
        ArrayList<Paquete> result = new ArrayList<>();
        searchRange(result, this.root, min, max);
        return result;
    }

    private void searchRange(List<Paquete> acumulador, TreeNode n, int min, int max) {
        if (n == null) return;
        if (n.getUrgencia() > max) {
            searchRange(acumulador, n.getLeft(), min, max);
        } else if (n.getUrgencia() < min) {
            searchRange(acumulador, n.getRight(), min, max);
        } else {
            searchRange(acumulador, n.getLeft(), min, max);
            acumulador.add(n.getPaquete());
            searchRange(acumulador, n.getRight(), min, max);
        }
    }

}