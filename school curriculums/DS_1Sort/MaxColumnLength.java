import java.awt.*;
import java.io.IOException;

public class MaxColumnLength {
    public static void main(String[] args) throws IOException {
        Picture testPicture = new Picture("F:\\1学校课程\\3\\数据结构\\数据结构实验1\\homework1(1)\\tomography.png");
        int max = 0;
        for(int c = 0; c < testPicture.getWidth(); c++){
            int len = 0;
            if(testPicture.getColor(c,0).equals(testPicture.getColor(c, testPicture.getHeight()-1))){
                continue;
            }
            int low = 0;
            int high = testPicture.getHeight()-1;
            while(true){
                int mid = low + (high - low) / 2;
                if(testPicture.getColor(c,low).equals(testPicture.getColor(c,mid))){
                    low = mid;
                }else{
                    high = mid;
                }
                if(high - low <= 1){
                    if(testPicture.getColor(c,0).equals(Color.white)){
                        len = testPicture.getHeight() - high;
                        break;
                    }else{
                        len = low + 1;
                        break;
                    }
                }
            }
            if(len > max){
                max = len;
            }
        }
        System.out.println(max);
        System.out.println(testPicture.getHeight());
    }
    public int getLength(Picture pic){
        return 0;
    }

}
