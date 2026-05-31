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

    public List<Paquete> searchRange(int min, int max) {
        return searchRange(this.root, min, max);
    }

    // searchRange - busca todos los nodos dentro de un rango [min, max] inclusive
    private List<Paquete> searchRange(TreeNode n, int min, int max) {
        List<Paquete> result = new ArrayList<>();
        if (n == null) return result;
        if (n.getUrgencia() > max)
            result.addAll(searchRange(n.getLeft(), min, max));
        else if (n.getUrgencia() < min)
            result.addAll(searchRange(n.getRight(), min, max));
        else {
            result.addAll(searchRange(n.getLeft(), min, max));
            result.add(n.getPaquete());
            result.addAll(searchRange(n.getRight(), min, max));
        }
        return result;
    }

}