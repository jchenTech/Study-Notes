# 排序算法说明

## 排序的定义
  对一序列对象根据某个关键字进行排序。

## 术语说明

- **稳定** ：如果a原本在b前面，而a=b，排序之后a仍然在b的前面；
- **不稳定** ：如果a原本在br的前面，而a=b，排序之后a可能会出现在b的后面；
- **内排序** ：所有排序操作都在内存中完成；
- **外排序** ：由于数据太大，因此把数据放在磁盘中，而排序通过磁盘和内存的数据传输才能进行；
- **时间复杂度** ： 一个算法执行所耗费的时间。
- **空间复杂度** ：运行完一个程序所需内存的大小。

## 算法总结

![算法总结](https://raw.githubusercontent.com/jchenTech/images/main/img/20201010171044.png)

![算法总结](https://raw.githubusercontent.com/jchenTech/images/main/img/20201010171045.png)



## 算法分类

![算法分类](https://raw.githubusercontent.com/jchenTech/images/main/img/20201010171046.png)

## 比较与非比较的区别

常见的**快速排序、归并排序、堆排序、冒泡排序** 等属于**比较排序** 。在排序的最终结果里，元素之间的次序依赖于它们之间的比较。每个数都必须和其他数进行比较，才能确定自己的位置 。

在冒泡排序之类的排序中，问题规模为n，又因为需要比较n次，所以平均时间复杂度为O(n²)。在归并排序、快速排序之类的排序中，问题规模通过分治法消减为logN次，所以时间复杂度平均O(nlogn)。

比较排序的优势是，适用于各种规模的数据，也不在乎数据的分布，都能进行排序。可以说，比较排序适用于一切需要排序的情况。

**计数排序、基数排序、桶排序**则属于**非比较排序** 。非比较排序是通过确定每个元素之前，应该有多少个元素来排序。针对数组arr，计算arr[i]之前有多少个元素，则唯一确定了arr[i]在排序后数组中的位置 。

非比较排序只要确定每个元素之前的已有的元素个数即可，所有一次遍历即可解决。算法时间复杂度O(n)。

非比较排序时间复杂度底，但由于非比较排序需要占用空间来确定唯一位置。所以对数据规模和数据分布有一定的要求。



# 冒泡排序

冒泡排序（Bubble Sort）是一种简单直观的排序算法。它重复地走访过要排序的数列，一次比较两个元素，如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成。这个算法的名字由来是因为越小的元素会经由交换慢慢“浮”到数列的顶端。

作为最简单的排序算法之一，冒泡排序给我的感觉就像 Abandon 在单词书里出现的感觉一样，每次都在第一页第一位，所以最熟悉。冒泡排序还有一种优化算法，就是立一个 flag，当在一趟序列遍历中元素没有发生交换，则证明该序列已经有序。但这种改进对于提升性能来说并没有什么太大作用。


## 算法步骤

1. 比较相邻的元素。如果第一个比第二个大，就交换他们两个。

2. 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。这步做完后，最后的元素会是最大的数。

3. 针对所有的元素重复以上的步骤，除了最后一个。

4. 持续每次对越来越少的元素重复上面的步骤，依次将2，3。

## 动图演示

![冒泡排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017124103.gif)

## 算法实现

```java
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
```

## 稳定性

当相邻元素相等时，它们并不会交换顺序，所以冒泡排序是稳定的

## 算法分析

- 最佳情况：O(n)
- 最差情况：O(n2)
- 平均情况：O(n2)

冒泡排序思路简单，代码也简单，特别适合小数据的排序。但是，由于算法复杂度较高，在数据量大的时候不适合使用。

## 优化算法

- **针对问题：**
  数据的顺序排好之后，冒泡算法仍然会继续进行下一轮的比较，直到`arr.length-1`次，后面的比较没有意义的。
- **方案：**
  设置标志位`flag`，如果发生了交换`flag`设置为`true`；如果没有交换就设置为`false`。
  这样当一轮比较结束后如果`flag`仍为`false`，即：这一轮没有发生交换，说明数据的顺序已经排好，没有必要继续进行下去。

```java
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
```

# 选择排序

选择排序(Selection-sort)是一种简单直观的排序算法。它的工作原理：首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。以此类推，直到所有元素均排序完毕。 

## 算法步骤

1. 在未排序序列中找到最小（大）元素，存放到排序序列的起始位置
2. 从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。
3. 重复第二步，直到所有元素均排序完毕。

## 动图演示

![(简单)选择排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017135618.gif)

## 算法实现

```java
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
```

## 稳定性

举个例子：[5 8 5 2 9]，我们知道第一遍选择第1个元素5会和2交换，那么原序列中2个5的相对前后顺序就被破坏了，所以选择排序不是一个稳定的排序算法。

用数组实现的选择排序是不稳定的，用链表实现的选择排序是稳定的。
不过，一般提到排序算法时，大家往往会默认是数组实现，所以选择排序是不稳定的。

## 算法分析

- 最佳情况：O(n2)
- 最差情况：O(n2)
- 平均情况：O(n2)

选择排序实现也比较简单，并且由于在各种情况下复杂度波动小，因此一般是优于冒泡排序的。在所有的完全交换排序中，选择排序也是比较不错的一种算法。但是，由于固有的O(n2)复杂度，选择排序在海量数据面前显得力不从心。因此，它适用于简单数据排序。

# 插入排序

插入排序是一种简单直观的排序算法。它的工作原理是通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。

## 算法步骤

一般来说，插入排序都采用in-place在数组上实现。具体算法描述如下：

- 步骤1: 从第一个元素开始，该元素可以认为已经被排序；
- 步骤2: 取出下一个元素，在已经排序的元素序列中从后向前扫描；
- 步骤3: 如果该元素（已排序）大于新元素，将该元素移到下一位置；
- 步骤4: 重复步骤3，直到找到已排序的元素小于或者等于新元素的位置；
- 步骤5: 将新元素插入到该位置后；
- 步骤6: 重复步骤2~5。

## 动图演示

![相同的场景](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017152354)

![(直接)插入排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017142107.gif)

## 算法实现

```java
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
```

## 稳定性

由于只需要找到不大于当前数的位置而并不需要交换，因此，直接插入排序是稳定的排序方法。

## 算法分析

- 最佳情况：O(n)
- 最坏情况：O(n2)
- 平均情况：O(n2)

插入排序由于O( n2 )的复杂度，在数组较大的时候不适用。但是，在数据比较少的时候，是一个不错的选择，一般做为快速排序的扩充。如，在JDK 7 `java.util.Arrays`所用的sort方法的实现中，当待排数组长度小于47时，会使用插入排序。

# 希尔排序(插入排序的改良版)

在希尔排序出现之前，计算机界普遍存在“排序算法不可能突破O(n2)”的观点。希尔排序是第一个突破O(n2)的排序算法，它是简单插入排序的改进版。希尔排序的提出，主要基于以下两点：

1. 插入排序算法在数组基本有序的情况下，可以近似达到O(n)复杂度，效率极高。
2. 但插入排序每次只能将数据移动一位，在数组较大且基本无序的情况下性能会迅速恶化。

希尔排序的基本思想是：先将整个待排序的记录序列分割成为若干子序列分别进行直接插入排序，待整个序列中的记录“基本有序”时，再对全体记录进行依次直接插入排序。

## 算法步骤

先将整个待排序的记录序列分割成为若干子序列分别进行直接插入排序，具体算法描述：

- 选择一个增量序列t1，t2，…，tk，其中ti>tj，tk=1；
- 按增量序列个数k，对序列进行 k 趟排序；
- 每趟排序，根据对应的增量ti，将待排序列分割成若干长度为m 的子序列，分别对各子表进行直接插入排序。仅增量因子为1 时，整个序列作为一个表来处理，表长度即为整个序列的长度。

## 动图演示

![希尔排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017154129.gif)

## 算法实现

Donald Shell增量

```Java
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
```

O(n3/2) Knuth增量

```java
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
```



## **希尔排序的增量**

希尔排序的增量数列可以任取，需要的唯一条件是最后一个一定为1（因为要保证按1有序）。但是，不同的数列选取会对算法的性能造成极大的影响。上面的代码演示了两种增量。
切记：增量序列中每两个元素最好不要出现1以外的公因子！（很显然，按4有序的数列再去按2排序意义并不大）。
下面是一些常见的增量序列。

* 第一种增量是最初Donald Shell提出的增量，即折半降低直到1。据研究，使用希尔增量，其时间复杂度还是O(n2)。

* 第二种增量Hibbard：`{1, 3, ..., 2k-1`}。该增量序列的时间复杂度大约是O(n1.5)。

* 第三种增量Sedgewick增量：`(1, 5, 19, 41, 109,...)`，其生成序列或者是`9*4i* *- 9*2i + 1`或者是`4i - 3*2i + 1`。

## 稳定性

我们都知道插入排序是稳定算法。但是，Shell排序是一个多次插入的过程。在一次插入中我们能确保不移动相同元素的顺序，但在多次的插入中，相同元素完全有可能在不同的插入轮次被移动，最后稳定性被破坏，因此，Shell排序不是一个稳定的算法。

## 算法分析

- 最佳情况：O(nlog n)
- 最坏情况：O(nlog2 n)
- 平均情况：O(nlog2n)

Shell排序虽然快，但是毕竟是插入排序，其数量级并没有后起之秀--快速排序O(nlogn)快。在大量数据面前，Shell排序不是一个好的算法。但是，中小型规模的数据完全可以使用它。



# 归并排序

归并排序是建立在归并操作上的一种有效的排序算法。该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。将已有序的子序列合并，得到完全有序的序列；即先使每个子序列有序，再使子序列段间有序。若将两个有序表合并成一个有序表，称为2-路归并。 

和选择排序一样，归并排序的性能不受输入数据的影响，但表现比选择排序好的多，因为始终都是 O(nlogn) 的时间复杂度。代价是需要额外的内存空间。

## 算法步骤

- 步骤1：把长度为n的输入序列分成两个长度为n/2的子序列；
- 步骤2：对这两个子序列分别采用归并排序；
- 步骤3：将两个排序好的子序列合并成一个最终的排序序列。

## 动图演示

![归并排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017190452.gif)

## 算法实现

```java
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
```

## 稳定性

因为我们在遇到相等的数据的时候必然是按顺序“抄写”到辅助数组上的，所以，归并排序同样是稳定算法。

## 算法分析

- 最佳情况：O(nlogn)
- 最差情况：O(nlogn)
- 平均情况：O(nlogn)

归并排序在数据量比较大的时候也有较为出色的表现（效率上），但是，其空间复杂度O(n)使得在数据量特别大的时候（例如，1千万数据）几乎不可接受。而且，考虑到有的机器内存本身就比较小，因此，采用归并排序一定要注意。

# 快速排序

快速排序是由东尼·霍尔所发展的一种排序算法。在平均状况下，排序 n 个项目要 Ο(nlogn) 次比较。在最坏状况下则需要 Ο(n2) 次比较，但这种状况并不常见。事实上，快速排序通常明显比其他 Ο(nlogn) 算法更快，因为它的内部循环（inner loop）可以在大部分的架构上很有效率地被实现出来。

快速排序使用分治法（Divide and conquer）策略来把一个串行（list）分为两个子串行（sub-lists）。

快速排序又是一种分而治之思想在排序算法上的典型应用。本质上来看，快速排序应该算是在冒泡排序基础上的递归分治法。

快速排序的名字起的是简单粗暴，因为一听到这个名字你就知道它存在的意义，就是快，而且效率高！它是处理大数据最快的排序算法之一了。虽然 Worst Case 的时间复杂度达到了 O(n²)，但是人家就是优秀，在大多数情况下都比平均时间复杂度为 O(n logn) 的排序算法表现要更好，这是因为：

> 快速排序的最坏运行情况是 O(n²)，比如说顺序数列的快排。但它的平摊期望时间是 O(nlogn)，且 O(nlogn) 记号中隐含的常数因子很小，比复杂度稳定等于 O(nlogn) 的归并排序要小很多。所以，对绝大多数顺序性较弱的随机数列而言，快速排序总是优于归并排序。

## 算法步骤

1. 从数列中挑出一个元素，称为"基准"（pivot），
2. 重新排序数列，所有比基准值小的元素摆放在基准前面，所有比基准值大的元素摆在基准后面（相同的数可以到任何一边）。在这个分区结束之后，该基准就处于数列的中间位置。这个称为分区（partition）操作。
3. 递归地（recursively）把小于基准值元素的子数列和大于基准值元素的子数列排序。

## 动图演示

![快速排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201017221740.gif)

## 算法实现

```java
/**
* 通过分区好后的基准位置划分为左数组和右数组，递归调用sort方法，
* 对左右数组进行分区和排序
* @param arr
* @param left
* @param right
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
* @param arr
* @param left
* @param right
* @return 基准值位置
*/
private static int partition(int[] arr, int left, int right) {
    int pivot = arr[left];
    int pivotIndex = left;
    while (left < right) {
        //从右到左进行循环，当元素值大于pivot时下标-1，当小于pivot时停下等待进行交换
        while (left < right && arr[right] >= pivot) {
            right--;
        }
        //从左到右进行循环，当元素值小于pivot时下标+1，当大于pivot时停下等待进行交换
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
* @param arr
* @param i 
* @param j
*/
private static void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}
```

## 稳定性

快速排序并不是稳定的。这是因为我们无法保证相等的数据按顺序被扫描到和按顺序存放。

## 算法分析

- 最佳情况：O(nlogn)
- 最差情况：O(n2)
- 平均情况：O(nlogn)

快速排序在大多数情况下都是适用的，尤其在数据量大的时候性能优越性更加明显。但是在必要的时候，需要考虑下优化以提高其在最坏情况下的性能。

# 堆排序

堆排序(Heapsort)是指利用堆积树（堆）这种数据结构所设计的一种排序算法，它是选择排序的一种。可以利用数组的特点快速定位指定索引的元素。堆排序就是把最大堆堆顶的最大数取出，将剩余的堆继续调整为最大堆，再次将堆顶的最大数取出，这个过程持续到剩余数只有一个时结束。

## 堆的概念

堆是一种特殊的完全二叉树（complete binary tree）。完全二叉树的一个“优秀”的性质是，除了最底层之外，每一层都是满的，这使得堆可以利用数组来表示（普通的一般的二叉树通常用链表作为基本容器表示），每一个结点对应数组中的一个元素。
如下图，是一个堆和数组的相互关系：

![堆的数组表示](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018132729.jpg)

对于给定的某个结点的下标 i，可以很容易的计算出这个结点的父结点、孩子结点的下标：

- `Parent(i) = floor(i/2)`，i 的父节点下标
- `Left(i) = 2i`，i 的左子节点下标
- `Right(i) = 2i + 1`，i 的右子节点下标

二叉堆一般分为两种：最大堆和最小堆。
**最大堆：**
最大堆中的最大元素值出现在根结点（堆顶）
堆中每个父节点的元素值都大于等于其孩子结点（如果存在）

![最大堆](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018133324.jpg)

**最小堆：**
最小堆中的最小元素值出现在根结点（堆顶）
堆中每个父节点的元素值都小于等于其孩子结点（如果存在）

![最小堆](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018133333.jpg)

## 堆排序原理

堆排序就是把最大堆堆顶的最大数取出，将剩余的堆继续调整为最大堆，再次将堆顶的最大数取出，这个过程持续到剩余数只有一个时结束。在堆中定义以下几种操作：

- 最大堆调整（adjustHeap）：将堆的末端子节点作调整，使得子节点永远小于父节点
- 创建最大堆（buildMaxHeap）：将堆所有数据重新排序，使其成为最大堆
- 堆排序（heapSort）：移除位在第一个数据的根节点，并做最大堆调整的递归运算 继续进行下面的讨论前，需要注意的一个问题是：数组都是 Zero-Based，这就意味着我们的堆数据结构模型要发生改变



![堆排序原理](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018133737.jpg)


相应的，几个计算公式也要作出相应调整：

- `Parent(i) = floor((i-1)/2)`，i 的父节点下标
- `Left(i) = 2i + 1`，i 的左子节点下标
- `Right(i) = 2(i + 1)`，i 的右子节点下标

## 堆的建立和维护

堆可以支持多种操作，但现在我们关心的只有两个问题：

1. 给定一个无序数组，如何建立为堆？
2. 删除堆顶元素后，如何调整数组成为新堆？

先看第二个问题。假定我们已经有一个现成的大根堆。现在我们删除了根元素，但并没有移动别的元素。想想发生了什么：根元素空了，但其它元素还保持着堆的性质。我们可以把**最后一个元素**（代号A）移动到根元素的位置。如果不是特殊情况，则堆的性质被破坏。但这仅仅是由于A小于其某个子元素。于是，我们可以把A和这个子元素调换位置。如果A大于其所有子元素，则堆调整好了；否则，重复上述过程，A元素在树形结构中不断“下沉”，直到合适的位置，数组重新恢复堆的性质。上述过程一般称为“筛选”，方向显然是自上而下。

> 删除后的调整，是把最后一个元素放到堆顶，自上而下比较

删除一个元素是如此，插入一个新元素也是如此。不同的是，我们把新元素放在**末尾**，然后和其父节点做比较，即自下而上筛选。

> 插入是把新元素放在末尾，自下而上比较

那么，第一个问题怎么解决呢？

常规方法是从第一个非叶子结点向下筛选，直到根元素筛选完毕。这个方法叫“筛选法”，需要循环筛选n/2个元素。

但我们还可以借鉴“插入排序”的思路。我们可以视第一个元素为一个堆，然后不断向其中添加新元素。这个方法叫做“插入法”，需要循环插入(n-1)个元素。

由于筛选法和插入法的方式不同，所以，相同的数据，它们建立的堆一般不同。大致了解堆之后，堆排序就是水到渠成的事情了。

## 算法步骤

- 步骤1：将初始待排序关键字序列`(R1,R2…,Rn)`构建成大顶堆，此堆为初始的无序区；
- 步骤2：将堆顶元素R[1]与最后一个元素R[n]交换，此时得到新的无序区(R1,R2,……Rn-1)和新的有序区(Rn),且满足R[1,2…n-1]<=R[n]；
- 步骤3：由于交换后新的堆顶R[1]可能违反堆的性质，因此需要对当前无序区(R1,R2,……Rn-1)调整为新堆，然后再次将R[1]与无序区最后一个元素交换，得到新的无序区(R1,R2….Rn-2)和新的有序区(Rn-1,Rn)。不断重复此过程直到有序区的元素个数为n-1，则整个排序过程完成。

## 动图演示

![堆排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018140209.gif)

## 算法实现

```java
public static void heapSort(int[] arr) {
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
    //从最后一个非叶子节点开始向上构造最大堆
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
```

## 稳定性

我们知道堆的结构是节点i的孩子为2 * i和2 * i + 1节点，大顶堆要求父节点大于等于其2个子节点，小顶堆要求父节点小于等于其2个子节点。在一个长为n 的序列，堆排序的过程是从第n / 2开始和其子节点共3个值选择最大（大顶堆）或者最小（小顶堆），这3个元素之间的选择当然不会破坏稳定性。但当为n / 2 - 1， n / 2 - 2， ... 1这些个父节点选择元素时，就会破坏稳定性。有可能第n / 2个父节点交换把后面一个元素交换过去了，而第n / 2 - 1个父节点把后面一个相同的元素没 有交换，那么这2个相同的元素之间的稳定性就被破坏了。所以，堆排序不是稳定的排序算法。

## 算法分析

- 最佳情况：O(nlogn)
- 最差情况：O(nlogn)
- 平均情况：O(nlogn)

堆排序在建立堆和调整堆的过程中会产生比较大的开销，在元素少的时候并不适用。但是，在元素比较多的情况下，还是不错的一个选择。尤其是在解决诸如“前n大的数”一类问题时，几乎是首选算法。

# 计数排序

计数排序不是基于比较的排序算法，其核心在于将输入的数据值转化为键存储在额外开辟的数组空间中。 作为一种线性时间复杂度的排序，计数排序要求输入的数据必须是有确定范围的整数。

## 算法步骤

1. 找出待排序的数组中最大和最小的元素；
2. 统计数组中每个值为i的元素出现的次数，存入数组C的第i项；
3. 对所有的计数累加（从C中的第一个元素开始，每一项和前一项相加）；
4. 反向填充目标数组：将每个元素i放在新数组的第C(i)项，每放一个元素就将C(i)减去1。

## 动图演示

![计数排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018162313.gif)

## 算法实现

```java
public static void countingSort(int[] arr) {
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
```

## 稳定性

最后给 b 数组赋值是倒着遍历的，而且放进去一个就将C数组对应的值（表示前面有多少元素小于或等于A[i]）减去一。如果有相同的数x1,x2，那么相对位置后面那个元素x2放在（比如下标为4的位置），相对位置前面那个元素x1下次进循环就会被放在x2前面的位置3。从而保证了稳定性。

## 算法分析

当输入的元素是n 个0到k之间的整数时，它的运行时间是 O(n + k)。计数排序不是比较排序，排序的速度快于任何比较排序算法。由于用来计数的数组C的长度取决于待排序数组中数据的范围（等于待排序数组的最大值与最小值的差加上1），这使得计数排序对于数据范围很大的数组，需要大量时间和内存。

- 最佳情况：O(n+k)
- 最差情况：O(n+k)
- 平均情况：O(n+k)

排序目标要能够映射到整数域，其最大值最小值应当容易辨别。例如高中生考试的总分数，显然用0-750就OK啦；又比如一群人的年龄，用个0-150应该就可以了，再不济就用0-200喽。另外，计数排序需要占用大量空间，它比较适用于数据比较集中的情况。

# 桶排序

 **桶排序** 是计数排序的升级版。它利用了函数的映射关系，高效与否的关键就在于这个映射函数的确定。

  **桶排序 (Bucket sort)的工作的原理：**
假设输入数据服从均匀分布，将数据分到有限数量的桶里，每个桶再分别排序（有可能再使用别的排序算法或是以递归方式继续使用桶排序进行排，最后将各个桶中的数据有序的合并起来。

## 算法步骤

- 步骤1：人为设置一个BucketSize，作为每个桶所能放置多少个不同数值（例如当BucketSize==5时，该桶可以存放｛1,2,3,4,5｝这几种数字，但是容量不限，即可以存放100个3）；

- 步骤2：遍历输入数据，并且把数据一个一个放到对应的桶里去；

- 步骤3：对每个不是空的桶进行排序，可以使用其它排序方法，也可以递归使用桶排序；

- 步骤4：从不是空的桶里把排好序的数据拼接起来。 

  注意，如果递归使用桶排序为各个桶排序，则当桶数量为1时要手动减小BucketSize增加下一循环桶的数量，否则会陷入死循环，导致内存溢出。

## 动图演示

![桶排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018165933.jpg)

## 算法实现

```java
public static void bucketSort(int[] arr){

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
```



## 稳定性

可以看出，在分桶和从桶依次输出的过程是稳定的。但是，由于我们在对每个桶进行排序时使用了其他算法，所以，桶排序的稳定性依赖于这一步。如果我们使用了快排，显然，算法是不稳定的。

## 算法分析

- 最佳情况：O(n+k)
- 最差情况：O(n+k)
- 平均情况：O(n2)

桶排序可用于最大最小值相差较大的数据情况，但桶排序要求数据的分布必须均匀，否则可能导致数据都集中到一个桶中。比如[104,150,123,132,20000], 这种数据会导致前4个数都集中到同一个桶中。导致桶排序失效。

# 基数排序

基数排序(Radix Sort)是桶排序的扩展，它的基本思想是：将整数按位数切割成不同的数字，然后按每个位数分别比较。
排序过程：将所有待比较数值（正整数）统一为同样的数位长度，数位较短的数前面补零。然后，从最低位开始，依次进行一次排序。这样从最低位排序一直到最高位排序完成以后, 数列就变成一个有序序列。

## 算法步骤

1. 取得数组中的最大数，并取得位数；
2. arr为原始数组，从最低位开始取每个位组成radix数组；
3. 对radix进行计数排序（利用计数排序适用于小范围数的特点）；

## 动图演示

![基数排序](https://raw.githubusercontent.com/jchenTech/images/main/img/20201018183053.gif)

## 算法实现

```java
public static void radixSort(int[] array) {
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
```

## 稳定性

通过上面的排序过程，我们可以看到，每一轮映射和收集操作，都保持从左到右的顺序进行，如果出现相同的元素，则保持他们在原始数组中的顺序。可见，基数排序是一种稳定的排序。

## 算法分析

- 最佳情况：O(n * k)
- 最差情况：O(n * k)
- 平均情况：O(n * k)

基数排序要求较高，元素必须是整数，整数时长度10W以上，最大值100W以下效率较好，但是基数排序比其他排序好在可以适用字符串，或者其他需要根据多个条件进行排序的场景，例如日期，先排序日，再排序月，最后排序年 ，其它排序算法可是做不了的。



**基数排序** vs **计数排序** vs **桶排序**

这三种排序算法都利用了桶的概念，但对桶的使用方法上有明显差异：

- **基数排序：** 根据键值的每位数字来分配桶
- **计数排序：** 每个桶只存储单一键值
- **桶排序：** 每个桶存储一定范围的数值

# 参考

1. [[算法总结] 十大排序算法](https://zhuanlan.zhihu.com/p/42586566)

2. [超详细十大经典排序算法总结（java代码）](https://blog.csdn.net/weixin_41190227/article/details/86600821)

3. [十大经典排序算法](https://sort.hust.cc/)