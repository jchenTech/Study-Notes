import java.util.Arrays;

/**
 * 堆排序
 * @author Jianjun Chen
 * @create 2020-10-18
 */
public class HeapSort {
    public static void heapSort(int[] arr) {
        System.out.println("*******开始进行堆排序********");
        int len = arr.length;
        //1. 构建一个最大堆
        buildMaxHeap(arr, len);
        //2. 循环将堆首位（最大值）与末位进行交换，然后重新调整最大堆
        for (int i = len - 1; i > 0; i--) {
            swap(arr, 0, i);
            len--;
            adjustHeap(arr, 0, len);
        }
    }

    private static void buildMaxHeap(int[] arr, int len) {
        //从最后一个非叶子节点开始从下到上，从右到左构造最大堆
        //for循环这样写会更好一点：i的左子树和右子树分别2i+1和2(i+1)
        for (int i = (len / 2 - 1); i >= 0; i--) {
            adjustHeap(arr, i, len);
        }
    }

    private static void adjustHeap(int[] arr, int i, int len) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int maxIndex = i;

        //如果有左子树，且左子树大于父节点，则将最大指针指向左子树
        if (left < len && arr[left] > arr[maxIndex]) {
            maxIndex = left;
        }
        //如果有右子树，且右子树大于父节点，则将最大指针指向右子树
        if (right < len && arr[right] > arr[maxIndex]) {
            maxIndex = right;
        }
        //如果父节点不是最大值，则将父节点与最大值交换，并且递归调整与父节点交换的位置。
        if (maxIndex != i) {
            swap(arr, i, maxIndex);
            adjustHeap(arr, maxIndex, len);
        }
    }


    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        heapSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(arr));
    }
}
