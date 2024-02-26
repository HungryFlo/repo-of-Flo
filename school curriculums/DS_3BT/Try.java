import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class Try {
    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader("homework3_testcases.txt");
        BufferedReader br = new BufferedReader(fr);
        StreamTokenizer st = new StreamTokenizer(br);
//        st.eolIsSignificant(true);
//        st.ordinaryChar('+');
//        st.ordinaryChar('-');
//        st.wordChars('#','#');
//        st.wordChars('?','?');
//        st.wordChars('"', '"'); // 确保能识别到引号

        for (int i = 0; i < 100; i++) {
            st.nextToken();
            System.out.println("st.ttype = " + (char)st.ttype);
            System.out.println("st.sval = " + st.sval);
            System.out.println();
        }
    }
}
