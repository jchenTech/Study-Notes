import java.util.Arrays;

/**
 * 归并排序
 * @author Jianjun Chen
 * @create 2020-10-17
 */
public class MergeSort {

    public static int[] mergeSort(int[] arr) {
        if (arr.length < 2) {
            return arr;
        }
        int mid = arr.length / 2;
        int[] left = Arrays.copyOfRange(arr, 0, mid);
        int[] right = Arrays.copyOfRange(arr, mid, arr.length);

        //递归调用mergeSort方法，将数组划分为长度小于等于2的子数组，在进行合并
        return merge(mergeSort(left), mergeSort(right));
    }

    private static int[] merge(int[] left, int[] right) {
        int[] res = new int[left.length + right.length];
        int i = 0;
        int j = 0;
        for (int index = 0; index < res.length; index++) {
            if (i >= left.length) {//左数组已全部合并
                res[index] = right[j++];
            }else if (j >= right.length){//右数组已全部合并
                res[index] = left[i++];
            }else if (left[i] < right[j]) {
                res[index] = left[i++];
            }else {
                res[index] = right[j++];
            }
        }
        return res;
    }



    public static void main(String[] args) {
        int[] arr = { 9, -16, 21, 23, -30, -49, 21, 30, 30 };
        System.out.println("排序之前：\n" + Arrays.toString(arr));
        int[] sorts = mergeSort(arr);
        System.out.println("排序之后：\n" + Arrays.toString(sorts));
    }
}
