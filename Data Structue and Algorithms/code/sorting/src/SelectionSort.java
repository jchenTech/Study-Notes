import java.util.Arrays;

/**
 * 选择排序
 * @author Jianjun Chen
 * @create 2020-10-17
 */
public class SelectionSort {
    public static void selectionSort(int[] arr) {
        System.out.println("*******开始进行选择排序********");

        //由于最后只剩一个数字时不需要排序，因此比较arr.length - 1次即可
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    //记录目前能找到的最小值元素的下标
                    minIndex = j;
                }
            }
            //将未排序列中的最小值与i位置元素交换
            if (i != minIndex) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        selectionSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
