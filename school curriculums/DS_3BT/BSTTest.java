import java.io.*;

public class BSTTest {
    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader("homework3_testcases.txt");
        BufferedReader br = new BufferedReader(fr);
        StreamTokenizer st = new StreamTokenizer(br);
        PrintWriter pw = new PrintWriter("homework3_test_result.txt");
        BST<String, String> bst = new BSTPtr<>();

        int token = st.nextToken();
        while (token != StreamTokenizer.TT_EOF) {
            char command = (char) st.ttype;
            switch (command) {
                case '+': {
                    st.nextToken(); // (
                    st.nextToken(); // key
                    String key = st.sval;
                    st.nextToken(); // ,
                    st.nextToken(); // value
                    String value = st.sval;
                    bst.insert(key, value);
                    st.nextToken(); // )
                    break;
                } case '-': {
                    st.nextToken(); // (
                    st.nextToken(); // key
                    String key = st.sval;
                    Object value = bst.remove(key); // 应该是 V 类型，这里先用Object代替了
                    if (value == null) {
                        pw.println("remove unsuccess ---" + key);
                    } else {
                        pw.println("remove success ---" + key + " " + value);
                    }
                    st.nextToken(); // )
                    break;
                } case '?': {
                    st.nextToken(); // (
                    st.nextToken(); // key
                    String key = st.sval;
                    Object value = bst.search(key);
                    if (value == null) {
                        pw.println("search unsuccess ---" + key);
                    } else {
                        pw.println("search success ---" + key + " " + value);
                    }
                    st.nextToken(); // )
                    break;
                } case '=': {
                    st.nextToken();
                    st.nextToken();
                    String key = st.sval;
                    st.nextToken();
                    st.nextToken();
                    String value = st.sval;
                    boolean flag = bst.update(key, value);
                    if (flag) {
                        pw.println("update success ---" + key + " " + value);
                    } else {
                        pw.println("update unsuccess");
                    }
                    st.nextToken();
                    break;
                } case '#': {
                    bst.showStructure(pw);
                    break;
                }
            }
            token = st.nextToken(); // 下一个操作符
            // 注意这里一定要更新 token 的值，否则就会导致程序无法结束
        }
        pw.flush();
        pw.close();
    }
}
