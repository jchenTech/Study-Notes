import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 桶排序
 * @author Jianjun Chen
 * @create 2020-10-18
 */
public class BucketSort {
    public static void bucketSort(int[] arr){
        System.out.println("*******开始进行桶排序********");
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < arr.length; i++){
            max = Math.max(max, arr[i]);
            min = Math.min(min, arr[i]);
        }

        //桶数
        int bucketNum = (max - min) / arr.length + 1;
        ArrayList<ArrayList<Integer>> bucketArr = new ArrayList<>(bucketNum);
        for(int i = 0; i < bucketNum; i++){
            bucketArr.add(new ArrayList<Integer>());
        }

        //将每个元素放入桶
        for(int i = 0; i < arr.length; i++){
            int num = (arr[i] - min) / (arr.length);
            bucketArr.get(num).add(arr[i]);
        }

        //对每个桶进行排序
        for(int i = 0; i < bucketArr.size(); i++){
            Collections.sort(bucketArr.get(i));
        }

        System.out.println(bucketArr.toString());

    }


    public static void main(String[] args) {
        int[] arr = { 9, 5, -1, 8, 5, 7, 3, -3, 1, 3 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        bucketSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
