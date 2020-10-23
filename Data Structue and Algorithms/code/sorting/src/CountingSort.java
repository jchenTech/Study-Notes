import java.util.Arrays;

/**
 * 计数排序
 * @author Jianjun Chen
 * @create 2020-10-18
 */
public class CountingSort {

    public static void countingSort(int[] arr) {
        System.out.println("*******开始进行计数排序********");
        //1. 获取数组的最大值与最小值
        int maxValue = getMaxValue(arr);
        int minValue = getMinValue(arr);

        //2. 存放数组元素出现次数的bucket的长度为maxValue - minValue + 1
        int bias = -minValue;
        int bucketLen = maxValue - minValue + 1;
        int[] bucket = new int[bucketLen];

        //3. bucket记录数组元素出现次数，此处索引为value+bias，因为创建时省略了最小值minValue以下的元素
        for (int value : arr) {
            bucket[value + bias]++;
        }

        //4. 反向填充数组，此处填充的值应为j-bias
        int sortedIndex = 0;
        for (int j = 0; j < bucketLen; j++) {
            while (bucket[j] > 0) {
                arr[sortedIndex++] = j - bias;
                bucket[j]--;
            }
        }

    }

    private static int getMaxValue(int[] arr) {
        int maxValue = arr[0];
        for (int value : arr) {
            if (maxValue < value) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    private static int getMinValue(int[] arr) {
        int minValue = arr[0];
        for (int value : arr) {
            if (minValue > value) {
                minValue = value;
            }
        }
        return minValue;
    }

    public static void main(String[] args) {
        int[] arr = { 9, 5, 21, 23, 2, 10, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        countingSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
