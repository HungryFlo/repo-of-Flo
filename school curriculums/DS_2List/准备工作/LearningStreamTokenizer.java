package 准备工作;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

//这个类中的代码主要是帮助同学们学会简单使用Java的StreamTokenizer
//StreamTokenizer主要用来对输入数据进行分类读取，下面的代码采用的StreamTokenizer默认的对字符的分类原则，有兴趣的同学可以在课余时间深入了解。
public class LearningStreamTokenizer {
    public static void main(String[] args) throws IOException {
        //先建立StreamTokenizer和外部输入数据的关联，在这里的外部数据存储在文件中。
        //输入文件中的数据格式如下，+符号代表入栈操作 -符号代表出栈操作
        //因为元素有两种类型（Double和String），所以在出栈操作时，在-符号后面用跟i代表Double类型的栈出栈，s代表String类型的栈出栈
        FileReader fis = new FileReader("input.txt");
        BufferedReader br = new BufferedReader(fis);
        StreamTokenizer st = new StreamTokenizer(br);
        AStack<Double> doubleStack = new AStack<>();
        LStack<String> stringStack = new LStack<>();
        int token = st.nextToken();
        while (token != StreamTokenizer.TT_EOF){ //token有四种状态，TT_EOF是文件结束状态
            char command = (char)st.ttype; //因为文件中的格式很规矩，每一个操作都是一个操作符紧跟数据，而操作符是由非字母字符和非数字字符构成，所以在StreamTokenizer默认会把这类字符都当做一类处理
            switch(command){
                case '+' : {
                    st.nextToken();//一旦读到+符号，就调用nextToken获得真正的数据内容
                    if (st.ttype == StreamTokenizer.TT_WORD) //StreamTokenizer会自动识别内容是数字还是字符串；如果是数字，就会设定ttype的值为TT_NUMBER；如果是字符串，就会设定ttype的值为TT_WORD
                        stringStack.push(st.sval); //如果是TT_WORD类型，那么就需要从sval中获得字符串内容
                    if (st.ttype == StreamTokenizer.TT_NUMBER)
                        doubleStack.push(st.nval); //如果是TT_NUMBER类型，那么就需要从nval中获得数字内容
                    break;
                }
                case '-' : {
                    st.nextToken();
                    if (st.sval.equals("i"))
                        doubleStack.pop();
                    if (st.sval.equals("s"))
                        stringStack.pop();
                }
            }
            token = st.nextToken();
        }
        System.out.println(doubleStack);
        System.out.println(stringStack);
    }
}
