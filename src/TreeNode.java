public class TreeNode {
    private int urgencia;
    private Paquete paquete;
    private TreeNode left;
    private TreeNode right;

    public TreeNode(Paquete paquete) {
        this.urgencia = paquete.getUrgencia();
        this.paquete = paquete;
        this.left = null;
        this.right = null;
    }

    public int getUrgencia() { return urgencia; }
    public Paquete getPaquete() { return paquete; }
    public TreeNode getLeft() { return left; }
    public TreeNode getRight() { return right; }
    public void setLeft(TreeNode left) { this.left = left; }
    public void setRight(TreeNode right) { this.right = right; }
}