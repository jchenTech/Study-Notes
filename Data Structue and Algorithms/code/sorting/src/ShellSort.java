import java.util.Arrays;

/**
 * 希尔排序（插入排序的改良版）
 * @author Jianjun Chen
 * @create 2020-10-17
 */
public class ShellSort {

    /**
     * Donald Shell增量（折半直到1）
     * @param arr
     */
    public static void shellSort(int[] arr) {
        System.out.println("*******开始进行希尔排序********");

        int h = arr.length / 2;
        while (h > 0) {
            //由于每一组的第一个元素不需要比较，因此i初始值为h
            for (int i = h; i < arr.length; i++) {
                // 此处与直接插入排序类似，不过增量变为h而不是1
                int temp = arr[i];
                int j = i;
                while (j - h >= 0 && arr[j - h] > temp) {
                    arr[j] = arr[j - h];
                    j -= h;
                }
                if (j != i) {
                    arr[j] = temp;
                }
            }
            h = h / 2;
        }
    }

    /**
     * O(n3/2) Knuth增量
     * @param arr
     */
    public static void shellSort2(int[] arr) {
        System.out.println("*******开始进行希尔排序********");

        int h = 1;
        while (h < arr.length / 3) {
            h = h * 3 +1;
        }

        while (h > 0) {
            //由于每一组的第一个元素不需要比较，因此i初始值为h
            for (int i = h; i < arr.length; i++) {
                // 此处与直接插入排序类似，不过增量变为h而不是1
                int temp = arr[i];
                int j = i;
                while (j - h >= 0 && arr[j - h] > temp) {
                    arr[j] = arr[j - h];
                    j -= h;
                }
                if (j != i) {
                    arr[j] = temp;
                }
            }
            h = h / 2;
        }
    }


    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        shellSort2(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
