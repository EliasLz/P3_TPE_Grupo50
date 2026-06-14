import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Tree<T extends Comparable<T>> {

    private TreeNode<T> root;
    private int cantElementos;

    public Tree() {
        this.root = null;
    }

    public void add(T elemento) {
        if (this.root == null) {
            this.root = new TreeNode<T>(elemento);
        } else {
            this.add(this.root, elemento);
        }
        this.cantElementos++;
    }

    private void add(TreeNode<T> nodoActual, T elemento) {
        int comparacion = elemento.compareTo(nodoActual.getValor());
        if (comparacion < 0) {
            if (nodoActual.getLeft() == null)
                nodoActual.setLeft(new TreeNode<T>(elemento));
            else
                add(nodoActual.getLeft(), elemento);
        } else if (comparacion > 0) {
            if (nodoActual.getRight() == null)
                nodoActual.setRight(new TreeNode<T>(elemento));
            else
                add(nodoActual.getRight(), elemento);
        } else {
            nodoActual.setValor(elemento);
            this.cantElementos--;
        }
    }

    /**
     * Busca nodos dentro de un rango
     *
     * @param min Límite inferior del rango (inclusive)
     * @param max Límite superior del rango (inclusive)
     * @return Collección de paquetes dentro del rango (ordenada)
     */
    public List<T> searchRange(T min, T max) {
        List<T> result = new ArrayList<>();
        searchRange(this.root, min, max, result);
        return result;
    }

    /**
     * DFS inorder con poda
     *
     * @param n          Nodo actal
     * @param min        Minimo del rango
     * @param max        Máximo del rango
     * @param acumulador Colección de valores dentro de rango hallados
     */
    private void searchRange(TreeNode<T> n, T min, T max, List<T> acumulador) {
        if (n == null) return;
        T elementoNodoActual = n.getValor();
        if (elementoNodoActual.compareTo(min) >= 0) {
            searchRange(n.getLeft(), min, max, acumulador);
        }
        if (elementoNodoActual.compareTo(min) >= 0 && elementoNodoActual.compareTo(max) <= 0) {
            acumulador.add(n.getValor());
        }
        if (elementoNodoActual.compareTo(max) <= 0) {
            searchRange(n.getRight(), min, max, acumulador);
        }
    }

    public void remove(T elemento) {
        if (elemento == null || this.root == null) {
            System.out.println("No se puede eliminar, árbol vacío o parametro invalido");
            return;
        }
        boolean[] encontrado = {false};
        this.root = removeRecursivo(this.root, elemento, encontrado);
        if (encontrado[0]) {
            this.cantElementos--;
        } else {
            System.out.println("El elemento que quiere eliminar no se encuentra el árbol");
        }
    }

    private TreeNode<T> removeRecursivo(TreeNode<T> nodoActual, T elemento, boolean[] encontrado) {
        if (nodoActual == null) {
            return null;
        }

        // 1. Es distinto: se decide lado recorrido
        if (elemento.compareTo(nodoActual.getValor()) < 0) {
            nodoActual.setLeft(removeRecursivo(nodoActual.getLeft(), elemento, encontrado));
        } else if (elemento.compareTo(nodoActual.getValor()) > 0) {
            nodoActual.setRight(removeRecursivo(nodoActual.getRight(), elemento, encontrado));
        }
        // 2. Es igual: se busca el sucesor
        else {
            encontrado[0] = true;
            // CASO 1: El nodo es hoja (sin hijos)
            if (nodoActual.getLeft() == null && nodoActual.getRight() == null) {
                return null;
            }

            // CASO 2: El nodo tiene UN HIJO
            if (nodoActual.getLeft() == null) {
                return nodoActual.getRight();
            } else if (nodoActual.getRight() == null) {
                return nodoActual.getLeft();
            }

            // CASO 3: El nodo tiene DOS HIJOS
            // Designamos como sucesor al descendiente el más chico del hijo grande (hijo derecho)
            TreeNode<T> sucesor = obtenerMasChico(nodoActual.getRight());

            // Pasamos los datos del sucesor al nodo actual
            nodoActual.setValor(sucesor.getValor());

            // Eliminamos el nodo sucesor original de manera recursiva
            nodoActual.setRight(removeRecursivo(nodoActual.getRight(), sucesor.getValor(), encontrado));
        }
        return nodoActual;
    }

    // Busca el más descendiente más a la izquierda
    private TreeNode<T> obtenerMasChico(TreeNode<T> actual) {
        while (actual.getLeft() != null) {
            actual = actual.getLeft();
        }
        return actual;
    }

    /**
     * Busca el elemento que es igual o el mayor más cercano al valor de referencia.
     *
     * @param referencia Objeto ficticio que contiene el valor que queremos ajustar (ej Camion: CapacidadActual 500, ID 20)
     * @return El elemento del árbol igual o el mayor más cercano al valor de referencia, null si no hay ninguno que sea mayor/igual.
     */
    public T obtenerIgualGrandeMasCercano(T referencia, Comparator<T> comparadorSinDesempate) {
        return obtenerIgualGrandeMasCercano(this.root, referencia, null, comparadorSinDesempate);
    }

    private T obtenerIgualGrandeMasCercano(TreeNode<T> nodoActual, T referencia, T mejorCandidato, Comparator<T> comparadorSinDesempate) {
        if (nodoActual == null) {
            return mejorCandidato;
        }
        // FASE 1: Verificamos si es candidato
        int comparacion = comparadorSinDesempate.compare(nodoActual.getValor(), referencia);

        // 1. Caso ideal
        if (comparacion == 0) {
            return nodoActual.getValor();
        }

        // 2. Es candidato
        if (comparacion > 0) {
            if (mejorCandidato == null || comparadorSinDesempate.compare(nodoActual.getValor(), mejorCandidato) < 0) {
                mejorCandidato = nodoActual.getValor();
            }
        }

        //FASE 2: Verificamos por donde debe seguir buscando respetando el orden del árbol
        // Comparamos nuevamente utilizando el mismo comparador que utiliza el add del árbol
        int comparacionNativa = nodoActual.getValor().compareTo(referencia);

        if (comparacionNativa > 0) {
            return obtenerIgualGrandeMasCercano(nodoActual.getLeft(), referencia, mejorCandidato, comparadorSinDesempate);
        } else {
            return obtenerIgualGrandeMasCercano(nodoActual.getRight(), referencia, mejorCandidato, comparadorSinDesempate);
        }
    }


}