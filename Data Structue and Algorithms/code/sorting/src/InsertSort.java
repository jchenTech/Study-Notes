import java.util.Arrays;

/**
 * 插入排序
 * @author Jianjun Chen
 * @create 2020-10-17
 */

public class InsertSort {
    public static void insertSort(int[] arr) {
        System.out.println("*******开始进行插入排序********");

        for (int i = 1; i < arr.length; i++){
            //标记当前要插入的元素为temp
            int temp = arr[i];

            //从已经排好序的序列最右边与temp比较，比temp大的元素向右移动一位
            int j = i;
            while (j > 0 && temp < arr[j - 1]) {
                arr[j] = arr[j - 1];
                j--;
            }
            //存在比当前要插入元素更小的数，插入
            if (j != i) {
                arr[j] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        insertSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
