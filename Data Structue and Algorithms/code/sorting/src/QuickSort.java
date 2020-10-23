import java.util.Arrays;

/**
 * 快速排序
 * @author Jianjun Chen
 * @create 2020-10-17
 */
public class QuickSort {
    public static void quickSort(int[] arr) {
        System.out.println("*******开始快速冒泡排序********");
        sort(arr, 0, arr.length - 1);

    }

    /**
     * 通过分区好后的基准位置划分为左数组和右数组，递归调用sort方法，
     * 对左右数组进行分区和排序
     * @param arr 待排数组
     * @param left 左分区左下标
     * @param right 右分区下标
     */
    private static void sort(int[] arr, int left, int right) {
        if (right < left) {
            return;
        }
        int pivotIndex = partition(arr, left, right);
        sort(arr, left, pivotIndex - 1);
        sort(arr, pivotIndex + 1, right);
    }

    /**
     * 将给定范围的数组进行分区，设定一个基准值，使得数组左边元素
     * 都小于基准值，数组右边元素都大于基准值
     * @param arr 待排数组
     * @param left 左分区左下标
     * @param right 右分区下标
     * @return 基准值位置
     */
    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[left];
        int pivotIndex = left;
        while (left < right) {
            //从右到左进行循环，当元素值大于pivot时下标-1，当值小于pivot时停下等待进行交换
            while (left < right && arr[right] >= pivot) {
                right--;
            }
            //从左到右进行循环，当元素值小于pivot时下标+1，当值大于pivot时停下等待进行交换
            while (left < right && arr[left] <= pivot) {
                left++;
            }
            //当左边元素大，右边元素小于pivot时，对这两个下标的元素进行交换
            swap(arr, left, right);
        }
        //将pivot基准元素放到正确位置，此时pivot左边元素小于pivot，右边大于pivot
        swap(arr, pivotIndex, left);
        return left; //返回pivot当前的下标
    }

    /**
     * 交换数组中两位置的元素
     * @param arr 待排序列
     * @param i 待交换元素下标i
     * @param j 待交换元素下标j
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        quickSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
