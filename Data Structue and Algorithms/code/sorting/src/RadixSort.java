import java.util.ArrayList;
import java.util.Arrays;

/**
 * 基数排序
 * @author Jianjun Chen
 * @create 2020-10-18
 */
public class RadixSort {
    public static void radixSort(int[] array) {
        System.out.println("*******开始进行基数排序********");
        if (array == null || array.length < 2)
            return;
        // 1.先算出最大数的位数；
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            max = Math.max(max, array[i]);
        }
        int maxDigit = 0;
        while (max != 0) {
            max /= 10;
            maxDigit++;
        }
        int mod = 10, div = 1;
        ArrayList<ArrayList<Integer>> bucketList = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 10; i++)
            bucketList.add(new ArrayList<Integer>());
        for (int i = 0; i < maxDigit; i++, mod *= 10, div *= 10) {
            for (int j = 0; j < array.length; j++) {
                int num = (array[j] % mod) / div;
                bucketList.get(num).add(array[j]);
            }
            int index = 0;
            for (int j = 0; j < bucketList.size(); j++) {
                for (int k = 0; k < bucketList.get(j).size(); k++)
                    array[index++] = bucketList.get(j).get(k);
                bucketList.get(j).clear();
            }
        }
    }



    public static void main(String[] args) {
        int[] arr = { 9, 5, 21, 23, 2, 10, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        radixSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
