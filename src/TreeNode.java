public class TreeNode<T extends Comparable<T>> {
    private T valor;
    private TreeNode<T> left;
    private TreeNode<T> right;

    public TreeNode(T valor) {
        this.valor = valor;
        this.left = null;
        this.right = null;
    }

    public T getValor() { return this.valor; }
    public TreeNode<T> getLeft() { return this.left; }
    public TreeNode<T> getRight() { return this.right; }
    public void setValor(T nuevoValor) { this.valor = nuevoValor; }
    public void setLeft(TreeNode<T> left) { this.left = left; }
    public void setRight(TreeNode<T> right) { this.right = right; }
}