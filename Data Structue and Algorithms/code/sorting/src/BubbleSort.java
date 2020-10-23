import java.util.Arrays;

/**
 * 冒泡排序
 * @author Jianjun Chen
 * @create 2020-10-17
 */
public class BubbleSort {
    public static void bubbleSort(int[] arr) {
        System.out.println("*******开始进行冒泡排序********");

        //循环次数位arr.length-1，因为最后一位时不需要排序
        for (int i = 0; i < arr.length - 1; i++) {
            //每个数字的需要遍历的长度，先前遍历过的到顶的i个数字不需要再排序
            for (int j = 0; j < arr.length - 1 - i; j++) {
                //两两元素交换顺序，使大的元素到最右边
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void bubbleSort1(int[] arr) {
        System.out.println("*******开始进行优化冒泡排序********");

        //循环次数位arr.length-1，因为最后一位时不需要排序
        for (int i = 0; i < arr.length - 1; i++) {
            boolean flag = false;
            //每个数字的需要遍历的长度，先前遍历过的到顶的i个数字不需要再排序
            for (int j = 0; j < arr.length - 1 - i; j++) {
                //两两元素交换顺序，使大的元素到最右边
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    flag = true;
                }
            }
            //若flag为false表明当前数组为有序，不需要在进行排序
            if (!flag) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        bubbleSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
