import java.io.*;

public class RadixSort {
    public static void main(String[] args) throws IOException {
        RadixSort rs = new RadixSort();

        // Construct BufferedReader from FileReader
        BufferedReader br = new BufferedReader(new FileReader("radixSort2.txt"));
        String line = null;

        PrintStream ps = new PrintStream("radixSort2_result.txt");
        //可能会出现异常，直接throws就行了
        System.setOut(ps);
        //把创建的打印输出流赋给系统。即系统下次向 ps输出

        while ((line = br.readLine()) != null) {
            String[] dataStr = line.split(" ");
//            int[] data = new int[dataStr.length];
//            for(int i = 0; i < dataStr.length; i++){
//                data[i] = Integer.parseInt(dataStr[i]);
//            }
            rs.sort_string(dataStr);
            for (String element :
                    dataStr) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
        br.close();

    }

    public void sort_int(int[] data){
        LQueue<Integer>[] b = new LQueue[10];
        //创建对象数组后，还需要初始化对象数组才可以使用！
        for(int i = 0; i < 10; i++){
            b[i] = new LQueue<>();
        }

        //find max num
        int max = data[0];
        for (int i:
            data ) {
            if(i>max){
                max = i;
            }
        }
        //get value of l
        int l = 0;
        do{
            l++;
            max = max / 10;
        }while(max!=0);

        //sort
        for(int i = 1; i <= l; i++){
            for (int element :
                    data) {
                b[element / (int) Math.pow(10, i - 1) % 10].enqueue(element);
            }
            //copy back
            //注意每次排完都需要copy back，否则下一次的排序将不能正常进行
            int j = 0;
            for (LQueue<Integer> q :
                    b) {
                while (!q.isEmpty()) {
                    data[j] = q.dequeue().intValue();
                    j++;
                }
            }
        }
    }

    public void sort_string(String[] data){
        LQueue<String>[] b = new LQueue[26];
        for(int i = 0; i < 26; i++){
            b[i] = new LQueue<>();
        }

        //get value of l
        int l = data[0].length();

        //sort
        int index=0;
        char curr;
        for(int i = 1; i <= l; i++){
            for (String element :
                    data) {
                curr = element.charAt(l-i);
                if(curr - 'a' >= 0 && curr - 'z' <= 0){
                    index = curr-'a';
                } else if (curr - 'A' >= 0 && curr - 'Z' <= 0){
                    index = curr-'A';
                } else {
                    System.out.println("invalid char: "+ curr);
                }
                b[index].enqueue(element);
            }
            //copy back
            int j = 0;
            for (LQueue<String> q :
                    b) {
                while (!q.isEmpty()) {
                    data[j] = q.dequeue();
                    j++;
                }
            }
        }
    }
}

//        FileReader fis = new FileReader("radixSort1.txt");
//        BufferedReader br = new BufferedReader(fis);
//        StreamTokenizer st = new StreamTokenizer(br);
//        st.eolIsSignificant(true);
//        PrintWriter pw = new PrintWriter("radixSort1_result.txt");
//
//        int token = st.nextToken();
//        int i = -1;
//        while(token != StreamTokenizer.TT_EOF){
//            int num = (int) st.nval;
//            i++;
//            data[i] = num;
//
//            if(token == StreamTokenizer.TT_EOL){
//                rs.sort_int(data);
//            }
//        }
