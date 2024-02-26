import java.io.*;

public class IndexTable {
    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader("homework3_article.txt");
        BufferedReader br = new BufferedReader(fr);
        StreamTokenizer st = new StreamTokenizer(br);
        st.eolIsSignificant(true);
        st.whitespaceChars(32, 64);
        st.whitespaceChars(91, 96);
        st.whitespaceChars(123,127);
        PrintWriter pw = new PrintWriter("index_result.txt");
        BST<String, LListpro<Integer>> indexBST = new BSTPtr<>();

        int line = 1;
        int token = st.nextToken();
        while (token != StreamTokenizer.TT_EOF) {

            // 要在检测字符之前先检测有无换行，避免开头有空行
            // 而且空行的个数是不确定的，所以要用 while 而不是 if
            while (token == StreamTokenizer.TT_EOL) {
                line ++;
                token = st.nextToken();
            }

            LListpro indexList = indexBST.search(st.sval);
            if(indexList == null) {
                indexList = new LListpro();
                indexBST.insert(st.sval, indexList);
            }
            indexList.insert(line);
            token = st.nextToken(); // 下一个操作符
            // 注意这里一定要更新 token 的值，否则就会导致程序无法结束
        }
        indexBST.printInorder(pw);
        pw.flush();
        pw.close();
    }
}
