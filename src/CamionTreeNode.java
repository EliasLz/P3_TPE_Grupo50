public class CamionTreeNode {
    private Camion camion;
    private CamionTreeNode left;
    private CamionTreeNode right;

    public CamionTreeNode(Camion camion) {
        this.camion = camion;
    }

    public int getEspacioDisponible() {
        return camion.getCapacidadMaxima() - camion.getCapacidadActual();
    }

    public Camion getCamion() { return camion; }
    public void setCamion(Camion camion) { this.camion = camion; }
    public CamionTreeNode getLeft() { return left; }
    public void setLeft(CamionTreeNode left) { this.left = left; }
    public CamionTreeNode getRight() { return right; }
    public void setRight(CamionTreeNode right) { this.right = right; }
}