//import java.io.*;
//
//public class ListTest {
//    public static void main(String[] args) throws IOException, ListException {
//        FileReader fis = new FileReader("list_testcase.txt");
//        BufferedReader br = new BufferedReader(fis);
//        StreamTokenizer st = new StreamTokenizer(br);
//        st.eolIsSignificant(true);
//        PrintWriter pw = new PrintWriter("alist_test_result.txt");
//        List<Character> characterList = new AList<>();
////        List<Character> characterList = new ResizingAList<>();
////        List<Character> characterList = new LListpro<>();
////        List<Character> characterList = new DList<>();
//        int token = st.nextToken();
//        while (token != StreamTokenizer.TT_EOF){
//            char command = (char)st.ttype;
//            switch(command){
//                case '+' :{
//                    st.nextToken();
//                    characterList.insert(st.sval.charAt(0));
//                    break;
//                }
//                case '-' :{
//                    characterList.remove();
//                    break;
//                }
//                case '=' :{
//                    st.nextToken();
//                    characterList.replace(st.sval.charAt(0));
//                    break;
//                }
//                case '#' :{
//                    characterList.gotoBeginning();
//                    break;
//                }
//                case '*' :{
//                    characterList.gotoEnd();
//                    break;
//                }
//                case '>' :{
//                    characterList.gotoNext();
//                    break;
//                }
//                case '<' :{
//                    characterList.gotoPrev();
//                    break;
//                }
//                case '~' :{
//                    characterList.clear();
//                    break;
//                }
//            }
//            if(token == StreamTokenizer.TT_EOL || (char) token == '\n'){
////            if(true){
//                characterList.showStructure(pw);
//                pw.flush();
////                characterList.clear();
//            }
//            token = st.nextToken();
//        }
//        characterList.showStructure(pw);
////        characterList.showLengthDCapacity(pw);
//        pw.flush();
//        characterList.clear();//需要在文件结束后再做一次输出，因为最后一行的结尾并没有EOL
//        pw.close();
//        fis.close();
//        br.close();
//    }
//}
