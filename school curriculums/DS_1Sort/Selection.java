
public class Selection extends SortAlgorithm {
    public void sort(Comparable[] objs) {
        int N = objs.length;
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i; j < N; j++) {
                if(less(objs[j],objs[min])){
                    min = j;
                }
            }
            exchange(objs,i,min);
        }
    }
    public static void main(String[] args){
        Double[] testData = GenerateData.getRandomData(5);
        Double[] test = new Double[6];
        for (int i = 0; i < 5; i++) {
            test[i] = testData[i];
        }
        SortAlgorithm alg = new Insertion();
        alg.sort(testData);
        System.out.println(test.equals(testData));
        System.out.println(alg.isSorted(testData));
    }
}