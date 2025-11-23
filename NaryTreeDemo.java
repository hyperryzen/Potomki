import java.util.*;
import java.util.stream.Collectors;

// 1. Интерфейс дерева с произвольным количеством потомков
interface NaryTree<T> {
    /**
     * Добавляет элемент как корень дерева
     * @param element элемент для добавления
     */
    void addRoot(T element);
    
    /**
     * Добавляет элемент как потомка родительского элемента
     * @param parent родительский элемент
     * @param element добавляемый элемент
     * @return true если элемент добавлен, false если родитель не найден
     */
    boolean addChild(T parent, T element);
    
    /**
     * Удаляет элемент и всех его потомков
     * @param element элемент для удаления
     * @return true если элемент удален, false если элемент не найден
     */
    boolean remove(T element);
    
    /**
     * Проверяет, содержится ли элемент в дереве
     * @param element искомый элемент
     * @return true если элемент найден, false в противном случае
     */
    boolean contains(T element);
    
    /**
     * Проверяет, пусто ли дерево
     * @return true если дерево пусто, false в противном случае
     */
    boolean isEmpty();
    
    /**
     * Возвращает количество элементов в дереве
     * @return количество элементов
     */
    int size();
    
    /**
     * Очищает дерево
     */
    void clear();
    
    /**
     * Возвращает высоту дерева
     * @return высота дерева
     */
    int height();
    
    /**
     * Возвращает глубину элемента
     * @param element элемент
     * @return глубина элемента или -1 если элемент не найден
     */
    int depth(T element);
    
    /**
     * Возвращает потомков элемента
     * @param element родительский элемент
     * @return список потомков или пустой список если элемент не найден
     */
    List<T> getChildren(T element);
    
    /**
     * Возвращает родительский элемент
     * @param element элемент
     * @return родительский элемент или null если элемент не найден или это корень
     */
    T getParent(T element);
    
    /**
     * Проверяет, является ли элемент листом
     * @param element элемент
     * @return true если элемент является листом, false в противном случае
     */
    boolean isLeaf(T element);
    
    /**
     * Проверяет, является ли элемент корнем
     * @param element элемент
     * @return true если элемент является корнем, false в противном случае
     */
    boolean isRoot(T element);
    
    /**
     * Обход в глубину (pre-order)
     * @return список элементов в порядке pre-order
     */
    List<T> preOrder();
    
    /**
     * Обход в ширину (level-order)
     * @return список элементов в порядке level-order
     */
    List<T> levelOrder();
    
    /**
     * Возвращает все листья дерева
     * @return список листьев
     */
    List<T> getLeaves();
    
    /**
     * Возвращает путь от корня до элемента
     * @param element целевой элемент
     * @return список элементов от корня до элемента или пустой список если элемент не найден
     */
    List<T> getPath(T element);
}

// 2. Реализация дерева с произвольным количеством потомков
class GeneralTree<T> implements NaryTree<T> {
    
    private static class TreeNode<T> {
        T data;
        List<TreeNode<T>> children;
        TreeNode<T> parent;
        
        TreeNode(T data) {
            this.data = data;
            this.children = new ArrayList<>();
            this.parent = null;
        }
        
        void addChild(TreeNode<T> child) {
            child.parent = this;
            children.add(child);
        }
        
        boolean removeChild(TreeNode<T> child) {
            return children.remove(child);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TreeNode<?> that = (TreeNode<?>) obj;
            return Objects.equals(data, that.data);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(data);
        }
        
        @Override
        public String toString() {
            return data.toString();
        }
    }
    
    private TreeNode<T> root;
    private int size;
    
    public GeneralTree() {
        this.root = null;
        this.size = 0;
    }
    
    @Override
    public void addRoot(T element) {
        if (root != null) {
            throw new IllegalStateException("Дерево уже имеет корень");
        }
        root = new TreeNode<>(element);
        size = 1;
    }
    
    @Override
    public boolean addChild(T parent, T element) {
        if (root == null) {
            return false;
        }
        
        TreeNode<T> parentNode = findNode(root, parent);
        if (parentNode != null) {
            TreeNode<T> newNode = new TreeNode<>(element);
            parentNode.addChild(newNode);
            size++;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean remove(T element) {
        if (root == null) {
            return false;
        }
        
        if (root.data.equals(element)) {
            root = null;
            size = 0;
            return true;
        }
        
        TreeNode<T> nodeToRemove = findNode(root, element);
        if (nodeToRemove != null && nodeToRemove.parent != null) {
            int subtreeSize = countNodes(nodeToRemove);
            nodeToRemove.parent.removeChild(nodeToRemove);
            size -= subtreeSize;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean contains(T element) {
        return root != null && findNode(root, element) != null;
    }
    
    @Override
    public boolean isEmpty() {
        return root == null;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public void clear() {
        root = null;
        size = 0;
    }
    
    @Override
    public int height() {
        return root == null ? -1 : heightRecursive(root);
    }
    
    private int heightRecursive(TreeNode<T> node) {
        if (node.children.isEmpty()) {
            return 0;
        }
        
        int maxHeight = 0;
        for (TreeNode<T> child : node.children) {
            maxHeight = Math.max(maxHeight, heightRecursive(child));
        }
        
        return maxHeight + 1;
    }
    
    @Override
    public int depth(T element) {
        TreeNode<T> node = findNode(root, element);
        return node == null ? -1 : depthRecursive(node);
    }
    
    private int depthRecursive(TreeNode<T> node) {
        if (node.parent == null) {
            return 0;
        }
        return depthRecursive(node.parent) + 1;
    }
    
    @Override
    public List<T> getChildren(T element) {
        TreeNode<T> node = findNode(root, element);
        if (node != null) {
            return node.children.stream()
                    .map(child -> child.data)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    @Override
    public T getParent(T element) {
        TreeNode<T> node = findNode(root, element);
        return (node != null && node.parent != null) ? node.parent.data : null;
    }
    
    @Override
    public boolean isLeaf(T element) {
        TreeNode<T> node = findNode(root, element);
        return node != null && node.children.isEmpty();
    }
    
    @Override
    public boolean isRoot(T element) {
        return root != null && root.data.equals(element);
    }
    
    @Override
    public List<T> preOrder() {
        List<T> result = new ArrayList<>();
        if (root != null) {
            preOrderRecursive(root, result);
        }
        return result;
    }
    
    private void preOrderRecursive(TreeNode<T> node, List<T> result) {
        result.add(node.data);
        for (TreeNode<T> child : node.children) {
            preOrderRecursive(child, result);
        }
    }
    
    @Override
    public List<T> levelOrder() {
        List<T> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode<T> current = queue.poll();
            result.add(current.data);
            
            for (TreeNode<T> child : current.children) {
                queue.offer(child);
            }
        }
        
        return result;
    }
    
    @Override
    public List<T> getLeaves() {
        List<T> leaves = new ArrayList<>();
        if (root != null) {
            findLeavesRecursive(root, leaves);
        }
        return leaves;
    }
    
    private void findLeavesRecursive(TreeNode<T> node, List<T> leaves) {
        if (node.children.isEmpty()) {
            leaves.add(node.data);
        } else {
            for (TreeNode<T> child : node.children) {
                findLeavesRecursive(child, leaves);
            }
        }
    }
    
    @Override
    public List<T> getPath(T element) {
        List<T> path = new ArrayList<>();
        TreeNode<T> node = findNode(root, element);
        
        while (node != null) {
            path.add(0, node.data); // Добавляем в начало
            node = node.parent;
        }
        
        return path;
    }
    
    // Вспомогательные методы
    private TreeNode<T> findNode(TreeNode<T> current, T element) {
        if (current == null) {
            return null;
        }
        
        if (current.data.equals(element)) {
            return current;
        }
        
        for (TreeNode<T> child : current.children) {
            TreeNode<T> found = findNode(child, element);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    private int countNodes(TreeNode<T> node) {
        if (node == null) {
            return 0;
        }
        
        int count = 1; // Текущий узел
        for (TreeNode<T> child : node.children) {
            count += countNodes(child);
        }
        
        return count;
    }
    
    // Визуализация дерева
    public void printTree() {
        if (root == null) {
            System.out.println("Дерево пусто");
            return;
        }
        printTreeRecursive(root, 0, "");
    }
    
    private void printTreeRecursive(TreeNode<T> node, int level, String prefix) {
        if (node == null) {
            return;
        }
        
        if (level == 0) {
            System.out.println(node.data);
        } else {
            System.out.println(prefix + "└── " + node.data);
        }
        
        String newPrefix = prefix + (level == 0 ? "" : "    ");
        for (int i = 0; i < node.children.size(); i++) {
            boolean isLast = i == node.children.size() - 1;
            String childPrefix = newPrefix + (isLast ? "    " : "│   ");
            printTreeRecursive(node.children.get(i), level + 1, childPrefix);
        }
    }
    
    @Override
    public String toString() {
        return levelOrder().toString();
    }
}

// 3. Демонстрация работы
public class NaryTreeDemo {
    public static void main(String[] args) {
        System.out.println("=== Демонстрация GeneralTree ===");
        demoGeneralTree();
        
        System.out.println("\n=== Демонстрация операций с деревом ===");
        demoTreeOperations();
        
        System.out.println("\n=== Демонстрация обходов дерева ===");
        demoTreeTraversals();
        
        System.out.println("\n=== Демонстрация дополнительных методов ===");
        demoAdditionalMethods();
    }
    
    private static void demoGeneralTree() {
        NaryTree<String> tree = new GeneralTree<>();
        
        System.out.println("Создаем дерево структуры компании:");
        tree.addRoot("CEO");
        
        tree.addChild("CEO", "CTO");
        tree.addChild("CEO", "CFO");
        tree.addChild("CEO", "COO");
        
        tree.addChild("CTO", "Dev Manager");
        tree.addChild("CTO", "QA Manager");
        
        tree.addChild("Dev Manager", "Senior Dev");
        tree.addChild("Dev Manager", "Junior Dev");
        
        tree.addChild("QA Manager", "Senior QA");
        tree.addChild("QA Manager", "Junior QA");
        
        tree.addChild("CFO", "Accountant");
        tree.addChild("CFO", "Financial Analyst");
        
        tree.addChild("COO", "HR Manager");
        tree.addChild("COO", "Operations Manager");
        
        System.out.println("Размер дерева: " + tree.size());
        System.out.println("Высота дерева: " + tree.height());
        System.out.println("Пустое ли дерево: " + tree.isEmpty());
        
        System.out.println("\nВизуализация дерева:");
        ((GeneralTree<String>) tree).printTree();
    }
    
    private static void demoTreeOperations() {
        NaryTree<Integer> tree = new GeneralTree<>();
        
        System.out.println("Создаем числовое дерево:");
        tree.addRoot(1);
        
        tree.addChild(1, 2);
        tree.addChild(1, 3);
        tree.addChild(1, 4);
        
        tree.addChild(2, 5);
        tree.addChild(2, 6);
        
        tree.addChild(3, 7);
        tree.addChild(3, 8);
        tree.addChild(3, 9);
        
        tree.addChild(4, 10);
        
        System.out.println("Исходное дерево: " + tree.levelOrder());
        System.out.println("Размер: " + tree.size());
        
        // Поиск элементов
        System.out.println("\nПоиск элементов:");
        System.out.println("Содержит 5: " + tree.contains(5));
        System.out.println("Содержит 99: " + tree.contains(99));
        
        // Получение детей
        System.out.println("\nДети элементов:");
        System.out.println("Дети 1: " + tree.getChildren(1));
        System.out.println("Дети 3: " + tree.getChildren(3));
        System.out.println("Дети 10: " + tree.getChildren(10));
        
        // Удаление
        System.out.println("\nУдаляем узел 3 и его поддерево:");
        System.out.println("Удален 3: " + tree.remove(3));
        System.out.println("Дерево после удаления: " + tree.levelOrder());
        System.out.println("Размер: " + tree.size());
        System.out.println("Содержит 7: " + tree.contains(7)); // Должно быть false
        
        // Проверка листьев и корня
        System.out.println("\nПроверка узлов:");
        System.out.println("1 является корнем: " + tree.isRoot(1));
        System.out.println("5 является листом: " + tree.isLeaf(5));
        System.out.println("2 является листом: " + tree.isLeaf(2));
    }
    
    private static void demoTreeTraversals() {
        NaryTree<Character> tree = new GeneralTree<>();
        
        System.out.println("Создаем алфавитное дерево:");
        tree.addRoot('A');
        
        tree.addChild('A', 'B');
        tree.addChild('A', 'C');
        tree.addChild('A', 'D');
        
        tree.addChild('B', 'E');
        tree.addChild('B', 'F');
        
        tree.addChild('C', 'G');
        
        tree.addChild('D', 'H');
        tree.addChild('D', 'I');
        tree.addChild('D', 'J');
        
        tree.addChild('F', 'K');
        tree.addChild('F', 'L');
        
        System.out.println("Pre-order: " + tree.preOrder());
        System.out.println("Level-order: " + tree.levelOrder());
        
        System.out.println("\nВизуализация дерева:");
        ((GeneralTree<Character>) tree).printTree();
    }
    
    private static void demoAdditionalMethods() {
        NaryTree<String> tree = new GeneralTree<>();
        
        System.out.println("Создаем дерево для демонстрации дополнительных методов:");
        tree.addRoot("Root");
        
        tree.addChild("Root", "A");
        tree.addChild("Root", "B");
        tree.addChild("A", "A1");
        tree.addChild("A", "A2");
        tree.addChild("B", "B1");
        tree.addChild("B1", "B1a");
        tree.addChild("B1", "B1b");
        
        // Глубина элементов
        System.out.println("Глубина элементов:");
        System.out.println("Глубина Root: " + tree.depth("Root"));
        System.out.println("Глубина A: " + tree.depth("A"));
        System.out.println("Глубина B1a: " + tree.depth("B1a"));
        
        // Родительские элементы
        System.out.println("\nРодительские элементы:");
        System.out.println("Родитель A: " + tree.getParent("A"));
        System.out.println("Родитель B1a: " + tree.getParent("B1a"));
        System.out.println("Родитель Root: " + tree.getParent("Root"));
        
        // Листья
        System.out.println("\nЛистья дерева: " + tree.getLeaves());
        
        // Пути
        System.out.println("\nПути от корня:");
        System.out.println("Путь до A: " + tree.getPath("A"));
        System.out.println("Путь до B1a: " + tree.getPath("B1a"));
        System.out.println("Путь до несуществующего элемента: " + tree.getPath("XYZ"));
        
        // Очистка
        System.out.println("\nОчищаем дерево:");
        tree.clear();
        System.out.println("Пустое ли дерево: " + tree.isEmpty());
        System.out.println("Размер: " + tree.size());
    }
}