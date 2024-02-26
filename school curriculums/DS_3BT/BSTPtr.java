import java.io.IOException;
import java.io.PrintWriter;

public class BSTPtr<K extends Comparable<K>,V> implements BST<K,V> {
    private class BinNode {
        private K key;
        private V value;
        private BinNode left;
        private BinNode right;

        public BinNode() {
            left = right = null;
        }

        public BinNode(K key, V value, BinNode left, BinNode right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public BinNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer("[");
            stringBuffer.append(key);
            stringBuffer.append(" --- < ");
            stringBuffer.append(value);
            stringBuffer.append(" >]");
            return stringBuffer.toString();
        }
    }

    private BinNode root;

    @Override
    public void insert(K key, V value) {
        // Precondition
        if (key==null || value==null) return;
        // Postcondition
        root = insertHelp(root, key, value);
    }

//    private void insertHelp1(BinNode root, K key, V value) {
//        if (root == null) {
//            root = new BinNode(key, value);
//            num ++;
//        } else if (root.key == key) {
//            root.value = value;
//        } else if (root.key.compareTo(key) < 0) {
//            insertHelp1(root.right, key, value);
//        } else if (root.key.compareTo(key) > 0) {
//            insertHelp1(root.left, key, value);
//        }
//    }
    // 这样的插入操作是行不通的，因为子节点中并没有父节点的信息，
    // 在一个null上创建一个对象，是无法与父节点产生链接的。

    // 解决办法是，让insertHelp函数返回一个BinNode引用。
    private BinNode insertHelp(BinNode root, K key, V value) {
        if (root == null) {
            return new BinNode(key, value);
        } else if (root.key.equals(key)) {
            root.value = value;
        } else if (root.key.compareTo(key) < 0) {
            root.right = insertHelp(root.right, key, value);
        } else if (root.key.compareTo(key) > 0) {
            root.left = insertHelp(root.left, key, value);
        }
        return root;
    }

    @Override
    public V remove(K key) {
        if (key == null) return null;
        else {
            V value = search(key);
            if(value == null) return null;
            else {
                removeHelp(root, key);
                return value;
            }
        }
    }

//    public V remove(K key) {
//        if (key == null || !find(key)) return null;
//        else {
//            V value = search(key);
//            removeHelp(root, key);
//            return value;
//        }
//    }

//    public boolean find(K key) {
//        if (key == null) return false;
//        return findHelp(root, key);
//    }
//
//    private boolean findHelp(BinNode root, K key) {
//        if (root == null) return false;
//        if (root.key.compareTo(key) == 0) return true;
//        else if (root.key.compareTo(key) > 0) {
//            return findHelp(root.left, key);
//        } else return findHelp(root.right, key);
//    }

    private BinNode removeHelp(BinNode root, K key) {
        // 删除的前提是找到
        // 没找到
        if (root == null) return null;
        // 正在找
        if (root.key.compareTo(key) > 0)
            root.left = removeHelp(root.left, key);
        else if (root.key.compareTo(key) < 0)
            root.right = removeHelp(root.right, key);
        // 找到了
        else {
            // 度为 0 或 1
            if (root.right == null) root = root.left;
            else if (root.left == null) {
                root = root.right;
            } else {
                // 度为 2
                BinNode temp = getMin(root.right);
                // 只交换元素
                root.key = temp.key;
                root.value = temp.value;
                root.right = deleteMin(root.right);
            }
        }
        // 返回“同级结点”
        return root;
    }

    // 在写 deleteMin 这种递归函数时，一定要想清楚：
    // 1. 要进行什么操作
    // 2. 要返回一个什么样的值以便下一次操作
    // 在这里就是：
    // 1. 要对参数中给出的二叉树做 删除最小值 的操作，
    // 具体表现为：
    // 当二叉树左子树为空时，让右子节点代替根节点的位置；
    // 当二叉树左右子树皆空时，设根节点为空结点；
    // 当二叉树左子树不空时，删除左子树中的最小结点。
    // 前两种情况可以统一成第一种情况。
    // 2. 由于二叉树的父节点存储有子节点的信息，
    // 而子节点中没有父节点的信息，
    // 所以在递归到父节点（也就是真正操作对象的前一层）时就要注意维护这种关系，
    // 只要涉及到修改，就要一定一定防止关系的丢失。
    // 这就要求使用 setLeft 进行操作，也就要求递归函数返回和参数“同级”的结点
    // “同级” 就是关键所在！

    private BinNode deleteMin(BinNode root) {
        if (root.left != null) {
            root.left = deleteMin(root.left);
            return root;
        } else {
            return root.right;
        }
    }

    // 除了 deleteMin ，一定还需要一个 getMin
    // 因为 deleteMin 的返回值根本不是树的最小节点，而是删除了最小值结点之后的根节点！
    // 而且，绝对不能把 最小值“结点” 与 根“结点” 交换位置，否则根节点会直接变成叶结点，只能交换元素的值！

    // 但是为了一次性传回两个参数，这里仍然是把 getMin 的返回值设计成 BinNode 类型，而在使用时只对元素进行交换
    private BinNode getMin(BinNode root) {
        if ( root.left == null ) return root;
        else return getMin(root.left);
    }
    // getMin 函数 和 deleteMin 函数 虽然返回类型都是 BinNode，但是意义完全不同：
    // getMin 返回子树中的最小节点，而 deleteMin 返回子树删去最小节点后的根节点

    // 全都是胡写
    // 递归哪有这么简单
//    private V removeHelp(BinNode root, K key) {
//        if (root == null) return null;
//        if (root.key.compareTo(key) > 0) return removeHelp(root.left, key);
//        else if (root.key.compareTo(key) < 0) return removeHelp(root.right, key);
//        else {
//            num --;
//            return root.value;
//        }
//    }

    // 首先，应该写考虑到删除不同度的结点的情况
    // 如果是叶结点，则只需要直接将父节点相关指针改成null
    // 如果是度为 1 的结点，则需将子节点挂载到自己的位置上
    // 如果是度为 2 的结点，则比较复杂，
    // 需要将右子树的最小值与这个结点做交换，再将被交换过的叶结点删去
    // 由于这里的 BST 中并没有相同的元素，所以也可以对左子树的最大值进行操作
    // 其次，removeHelp 也需要以 BinNode 引用作为返回值，
    // 以保证父节点与子节点的关系不会丢失

    @Override
    public V search(K key) {
        if (key == null) return null;
        return searchHelp(root, key);
    }

    private V searchHelp(BinNode root, K key) {
        if (root == null) return null;
        if (root.key.compareTo(key) == 0) return root.value;
        else if (root.key.compareTo(key) > 0) {
            return searchHelp(root.left, key);
        } else return searchHelp(root.right, key);
    }

    @Override
    public boolean update(K key, V value) {
        if (key==null || value == null) return false;
        return updateHelp(root, key, value);
    }

    private boolean updateHelp(BinNode root, K key, V value) {
        if (root == null) return false;
        if (root.key.compareTo(key) == 0) {
            root.value = value;
            return true;
        }
        else if (root.key.compareTo(key) > 0) {
            return updateHelp(root.left, key, value);
        } else return updateHelp(root.right, key, value);
    }

    @Override
    public boolean isEmpty() {
        return (root == null);
    }

    @Override
    public void clear() {
        root = null;
    }

    private int getNum(BinNode root) {
        if(root == null) return 0;
        else return (getNum(root.left) + getNum(root.right) + 1);
    }
    private int getHeight(BinNode root) {
        if (root == null) return 0;
        else return (Math.max(getHeight(root.left), getHeight(root.right)) + 1);
    }

    @Override
    public void showStructure(PrintWriter pw) throws IOException {
        if (pw == null) return;
        pw.println("-----------------------------");
        pw.println("There are " + getNum(root) + " nodes in this BST.");
        pw.println("The height of this BST is " + getHeight(this.root) + ".");
        pw.println("-----------------------------");
    }

    @Override
    public void printInorder(PrintWriter pw) throws IOException {
        if (pw == null) return;
        traverse(root, pw);
    }

    private void traverse(BinNode root, PrintWriter pw) {
        if (root==null) return;
        traverse(root.left, pw);
        pw.println(root);
        traverse(root.right, pw);
    }
}
