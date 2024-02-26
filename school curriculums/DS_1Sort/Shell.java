
public class Shell extends SortAlgorithm {
    public void sort(Comparable[] objs){
        int N = objs.length;
        //计算每一轮的增量
        for(int i = N / 3; i > 0; i /= 3){
            //选择每一组的开始元素
            for( int j = 0; j < i; j ++){
                //开始插入排序，由于下面是和前一个元素比较，所以要从每组的第二个元素开始遍历
                for (int k = i + j; k < N; k += i) {
                    //将每一个元素放到该放的位置上去
                    for (int l = k; l >= i && less(objs[l],objs[l-i]); l -= i) {
                        exchange(objs,l,l-i);
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        Double[] testData = GenerateData.getRandomData(5);
        Double[] test = new Double[6];
        for (int i = 0; i < 5; i++) {
            test[i] = testData[i];
        }
        SortAlgorithm alg = new Shell();
        alg.sort(testData);
        System.out.println(test.equals(testData));
        System.out.println(alg.isSorted(testData));
    }
}
