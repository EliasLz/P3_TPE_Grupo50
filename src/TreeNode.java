public class TreeNode {
    private int getClaveUrgencia;
    private Paquete paquete;
    private TreeNode left;
    private TreeNode right;

    public TreeNode(Paquete paquete) {
        this.getClaveUrgencia = paquete.getUrgencia();
        this.paquete = paquete;
        this.left = null;
        this.right = null;
    }

    public int getClaveUrgencia() { return getClaveUrgencia; }
    public Paquete getPaquete() { return paquete; }
    public TreeNode getLeft() { return left; }
    public TreeNode getRight() { return right; }
    public void setLeft(TreeNode left) { this.left = left; }
    public void setRight(TreeNode right) { this.right = right; }
}