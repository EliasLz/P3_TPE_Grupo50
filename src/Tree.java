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
        List<Paquete> result= new ArrayList<>();
        searchRange(this.root, min, max, result);
        return result;
    }

    // searchRange - busca todos los nodos dentro de un rango [min, max] inclusive
   private  void searchRange(TreeNode n, int min, int max, List<Paquete> result) {
    if (n == null) return;
    if (n.getUrgencia() > max)
        searchRange(n.getLeft(), min, max, result);
    else if (n.getUrgencia() < min)
        searchRange(n.getRight(), min, max, result);
    else {
        searchRange(n.getLeft(), min, max, result);
        result.add(n.getPaquete());
        searchRange(n.getRight(), min, max, result);
    }
}

}