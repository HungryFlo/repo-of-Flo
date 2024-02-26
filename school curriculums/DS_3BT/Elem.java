public class Elem {
    private int key;
    private Object value;

    public Elem() {
    }

    public Elem(int key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取
     * @return key
     */
    public int getKey() {
        return key;
    }

    /**
     * 设置
     * @param key
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * 获取
     * @return value
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return "Elem{key = " + key + ", value = " + value + "}";
    }
}
