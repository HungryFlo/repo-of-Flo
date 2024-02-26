
import java.util.Random;

public class GenerateData {
    // 生成一个长度为N的均匀分布的数据序列
    public static Double[] getRandomData(int N){
        Double[] numbers = getSortedData(N);
        shuffle(numbers, 0, numbers.length);
        return numbers;
    }
    // 生成一个长度为N的正序的数据序列
    public static Double[] getSortedData(int N){
        Double[] numbers = new Double[N];
        double t = 0.0;
        for (int i = 0; i < N; i++){
            numbers[i] = t;
            t = t + 1.0/N;
        }
        return numbers;
    }
    // 生成一个长度为N的逆序的数据序列
    public static Double[] getInversedData(int N){
        Double[] numbers = new Double[N];
        double t = 1.0;
        for (int i = 0; i < N; i++){
            t = t - 1.0/N;
            numbers[i] = t;
        }
        return numbers;
    }

    //任务5：生成分布不均匀的序列
    public static Double[] getNovelData(int N){
        Double[] numbers = new Double[N];
        for(int i = 0; i < N/2; i++){
            numbers[i] = 0.0;
        }
        for(int i = N/2; i < 3 * N / 4; i++){
            numbers[i] = 1.0;
        }
        for(int i = 3 * N / 4; i < 7 * N / 8; i++){
            numbers[i] = 2.0;
        }
        for(int i = 7 * N / 8; i < N; i++){
            numbers[i] = 3.0;
        }
        shuffle(numbers, 0, numbers.length - 1);
        return numbers;
    }

    private static Double[] getRepetitiveData(int N, int rpt){
        Double[] numbers = new Double[N];
        Random rand = new Random();
        Double num = rand.nextDouble();
        int border = N * rpt / 100;
        for (int i = 0; i < border; i++) {
            numbers[i] = num;
        }
        double t = 0.0;
        for (int i = border; i < N; i++){
            numbers[i] = t;
            t = t + 1.0/N;
        }
        shuffle(numbers, 0, numbers.length - 1);
        return numbers;
    }

    public static Double[] get50Data(int N){
        return getRepetitiveData(N,50);
    }

    public static Double[] get60Data(int N){
        return getRepetitiveData(N,60);
    }

    public static Double[] get80Data(int N){
        return getRepetitiveData(N,80);
    }

    public static Double[] get100Data(int N){
        return getRepetitiveData(N,100);
    }

    // 将数组numbers中的[left,right)范围内的数据随机打乱
    private static void shuffle(Double[] numbers, int left, int right){
        int N = right - left;
        Random rand = new Random();
        for(int i = 0; i < N; i++){
            int j = i + rand.nextInt(N-i);
            exchange(numbers, i+left, j+left);
        }
    }


    private static void exchange(Double[] numbers, int i, int j){
        double temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }

    public static void main(String[] args) {
//        Double[] numbers = getRandomData(1000);
//        for(int i = 0; i < 100; i++)
//            System.out.printf("%5.3f ", numbers[i]);
        Double[] numbers2 = get50Data(10);
        for(int i = 0; i < 10; i++)
            System.out.printf("%5.3f ", numbers2[i]);
    }
}
