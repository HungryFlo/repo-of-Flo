import java.io.IOException;
import java.io.PrintWriter;

public interface BST<K extends Comparable<K>, V> {
    public void insert(K key, V value);
    public V remove(K key);
    public V search(K key);
    public boolean update(K key, V value);
    public boolean isEmpty();
    public void clear();
    public void showStructure(PrintWriter pw) throws IOException;
    public void printInorder(PrintWriter pw) throws IOException;
}
