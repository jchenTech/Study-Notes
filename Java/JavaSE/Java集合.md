## 1 数组与集合

1. 集合、数组都是对多个数据进行存储操作的结构，简称Java容器。
   说明：此时的存储，主要指的是内存层面的存储，不涉及到持久化的存储（.txt,.jpg,.avi，数据库中)

2. 数组存储的特点：
    * 一旦初始化以后，其长度就确定了。
    * 数组一旦定义好，其元素的类型也就确定了。我们也就只能操作指定类型的数据了。

  比如：String[] arr; int[] arr1; Object[] arr2;

3. 数组存储的弊端：
   * 一旦初始化以后，其长度就不可修改。
   * 数组中提供的方法非常限，对于添加、删除、插入数据等操作，非常不便，同时效率不高。
   * 获取数组中实际元素的个数的需求，数组没有现成的属性或方法可用
   * 数组存储数据的特点：有序、可重复。对于无序、不可重复的需求，不能满足。
4. 集合存储的优点：
解决数组存储数据方面的弊端。

## 2 Collection接口

![img](https://gitee.com/jchenTech/images/raw/master/img/20201228201629.png)



### 2.1 集合框架结构

Collection接口：单列集合，用来存储一个一个的对象

* List接口：存储有序的、可重复的数据。  -->“动态”数组
  * ArrayList、LinkedList、Vector
* Set接口：存储无序的、不可重复的数据   -->高中讲的“集合”
  * HashSet、LinkedHashSet、TreeSet
* Queue接口：在队列前端删除，在队列后端进行插入操作
  * ArrayQueue、LinkedList、PriorityQueue

对应图示：

![image-20201228202113900](https://gitee.com/jchenTech/images/raw/master/img/20201228202119.png)

### 2.2 Collection接口常用方法：

* add(Object obj)
* addAll(Collection coll)
* size()
* isEmpty()
* clear()
* contains(Object obj)
* containsAll(Collection coll)
* remove(Object obj)
* removeAll(Collection coll)
* retainsAll(Collection coll)
* equals(Object obj)
* hasCode()
* toArray()
* iterator()

### 2.3 Collection集合与数组间的转换

* 集合 --->数组：toArray()

  ```java
  Object[] arr = coll.toArray();
  for(int i = 0; i < arr.length; i++){
      System.out.println(arr[i]);
  }
  ```

* //拓展：数组 --->集合:调用Arrays类的静态方法`asList(T ... t)`

  ```java
  List<String> list = Arrays.asList(new String[]{"AA", "BB", "CC"});
  System.out.println(list);
  
  List arr1 = Arrays.asList(new int[]{123, 456});
  System.out.println(arr1.size());//1
  
  List arr2 = Arrays.asList(new Integer[]{123, 456});
  System.out.println(arr2.size());//2
  ```

### 2.4 Collection集合存储对象要求
**向Collection接口的实现类的对象中添加数据obj时，要求obj所在类要重写equals().**

### 2.5 掌握要求
层次一：选择合适的集合类去实现数据的保存，调用其内部的相关方法。

层次二：不同的集合类底层的数据结构为何？如何实现数据的操作的：增删改查等。



## 3 Iterator接口与foreach循环

### 3.1 遍历Collection的两种方式

* 使用迭代器Iterator

* foreach循环（或叫增强for循环）

### 3.2 java.utils包下定义的迭代器接口：Iterator

1. 说明：
   Iterator对象称为迭代器(设计模式的一种)，主要用于遍历 Collection 集合中的元素。GOF给迭代器模式的定义为：提供一种方法访问一个容器(container)对象中各个元素，而又不需暴露该对象的内部细节。迭代器模式，就是为容器而生。

2. 作用：遍历集合Collectiton元素

3. 如何获取实例：`coll.iterator()`返回一个迭代器实例

4. 遍历的代码实现：

   ```java
   Iterator iterator = coll.iterator();
   //hasNext():判断是否还下一个元素
   while(iterator.hasNext()){
       //next():①指针下移 ②将下移以后集合位置上的元素返回
       System.out.println(iterator.next());
   }
   ```

5. 图示说明：
   ![image-20201025185530922](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025185531.png)

6. remove()的使用：
   
   ```java
   //测试Iterator中的remove()
   //如果还未调用next()或在上一次调用 next 方法之后已经调用了remove方法，再调用remove都会报IllegalStateException。
   //内部定义了remove(),可以在遍历的时候，删除集合中的元素。此方法不同于集合直接调用remove()
   @Test
   public void test3(){
       Collection coll = new ArrayList();
       coll.add(123);
       coll.add(456);
       coll.add(new Person("Jerry",20));
       coll.add(new String("Tom"));
       coll.add(false);
   
       //删除集合中"Tom"
    Iterator iterator = coll.iterator();
       while (iterator.hasNext()){
   
           //            iterator.remove();
           Object obj = iterator.next();
           if("Tom".equals(obj)){
               iterator.remove();
               //                iterator.remove();
           }
       }
       //遍历集合
       iterator = coll.iterator();
       while (iterator.hasNext()){
           System.out.println(iterator.next());
       }
   }
   ```
### 3.3 增强for循环(foreach循环)

1. 遍历集合举例：

    ```java
    @Test
    public void test1(){
        Collection coll = new ArrayList();
        coll.add(123);
        coll.add(456);
        coll.add(new Person("Jerry",20));
        coll.add(new String("Tom"));
        coll.add(false);

    //for(集合元素的类型 局部变量 : 集合对象)

        for(Object obj : coll){
            System.out.println(obj);
        }
    }
    ```

   说明：内部仍然调用了迭代器。

2. 遍历数组举例：

   ```java
   @Test
   public void test2(){
       int[] arr = new int[]{1,2,3,4,5,6};
       //for(数组元素的类型 局部变量 : 数组对象)
       for(int i : arr){
           System.out.println(i);
       }
   }
   ```

## 4 List接口

### 4.1 存储的数据特点

存储序的、可重复的数据。

### 4.2 常用方法：(记住)

* 增：add(Object obj)
* 删：remove(int index) / remove(Object obj)
* 改：set(int index, Object ele)
* 查：get(int index)
* 插：add(int index, Object ele)
* 长度：size()
* 遍历：
  ① Iterator迭代器方式
  ② 增强for循环
  ③ 普通的循环

### 4.3 常用实现类：
Collection接口：单列集合，用来存储一个一个的对象

*  List接口：存储序的、可重复的数据。  -->“动态”数组,替换原的数组
   * ArrayList：作为List接口的主要实现类；线程不安全的，效率高；底层使用Object[] elementData存储
   * LinkedList：对于频繁的插入、删除操作，使用此类效率比ArrayList高；底层使用双向链表存储
   * Vector：作为List接口的古老实现类；线程安全的，效率低；底层使用Object[] elementData存储

### 4.4 源码分析(难点)
#### 4.4.1 ArrayList的源码分析：

1. jdk 7情况下

   ```java
   ArrayList list = new ArrayList();//底层创建了长度是10的Object[]数组elementData
   
   list.add(123);//elementData[0] = new Integer(123);
   ...
   list.add(11);//如果此次的添加导致底层elementData数组容量不够，则扩容。
   
   //默认情况下，扩容为原来的容量的1.5倍，同时需要将原有数组中的数据复制到新的数组中。
   ```


   结论：建议开发中使用带参的构造器：`ArrayList list = new ArrayList(int capacity)`

2. jdk8中ArrayList的变化：

   ```java
   ArrayList list = new ArrayList();//底层Object[] elementData初始化为{}.并没创建长度为10的数组
   
   list.add(123);//第一次调用add()时，底层才创建了长度10的数组，并将数据123添加到elementData[0]
   ...
   ```

   后续的添加和扩容操作与jdk 7 无异。

3. 小结：jdk7中的ArrayList的对象的创建类似于单例的饿汉式，而jdk8中的ArrayList的对象的创建类似于单例的懒汉式，延迟了数组的创建，节省内存。

#### 4.4.2 LinkedList的源码分析：

```java
LinkedList list = new LinkedList(); //内部声明了Node类型的first和last属性，默认值为null

list.add(123);//将123封装到Node中，创建了Node对象。
```

其中，Node定义为：体现了LinkedList的双向链表的说法

```java
private static class Node<E> {
     E item;
     Node<E> next;
     Node<E> prev;

 Node(Node<E> prev, E element, Node<E> next) {
     this.item = element;
     this.next = next;
     this.prev = prev;
 }
 }
```

#### 4.4.3 Vector的源码分析：

jdk7和jdk8中通过Vector()构造器创建对象时，底层都创建了长度为10的数组。在扩容方面，默认扩容为原来的数组长度的2倍。

### 4.5 存储的元素的要求
添加的对象，所在的类要重写equals()方法
面试题：

*  面试题：ArrayList、LinkedList、Vector者的异同？
*  同：三个类都是实现了List接口，存储数据的特点相同：存储有序的、可重复的数据
*  不同：见上（第3部分+第4部分）

## 5 Set接口

### 5.1 存储的数据特点

无序的、不可重复的元素

具体的以HashSet为例说明：

1. 无序性：不等于随机性。存储的数据在底层数组中并非照数组索引的顺序添加，而是根据数据的哈希值决定的。
2. 不可重复性：保证添加的元素照equals()判断时，不能返回true.即：相同的元素只能添加一个。

### 5.2 元素添加过程(以HashSet为例)

我们向HashSet中添加元素a,首先调用元素a所在类的hashCode()方法，计算元素a的哈希值，此哈希值接着通过某种算法计算出在HashSet底层数组中的存放位置（即为：索引位置），判断数组此位置上是否已经有元素：

* 如果此位置上没其他元素，则元素a添加成功。————情况1
* 如果此位置上有其他元素b(或以链表形式存在的多个元素，则比较元素a与元素b的hash值：
  * 如果hash值不相同，则元素a添加成功。————情况2
  * 如果hash值相同，进而需要调用元素a所在类的equals()方法：
    * equals()返回true,元素a添加失败
    * equals()返回false,则元素a添加成功。————情况3

对于添加成功的情况2和情况3而言：元素a 与已经存在指定索引位置上数据以链表的方式存储。

* jdk 7 :元素a放到数组中，指向原来的元素。
* jdk 8 :原来的元素在数组中，指向元素a
  总结：七上八下

HashSet底层：数组+链表的结构。（前提：jdk7)

### 5.3 常用方法

Set接口中没有额外定义新的方法，使用的都是Collection中声明过的方法。

### 5.4 常用实现类：

Collection接口：单列集合，用来存储一个一个的对象

* Set接口：存储无序的、不可重复的数据   -->高中讲的“集合”
	* HashSet：作为Set接口的主要实现类；线程不安全的；可以存储null值
		* LinkedHashSet：作为HashSet的子类；遍历其内部数据时，可以按照添加的顺序遍历；在添加数据的同时，每个数据还维护了两个引用，记录此数据前一个数据和后一个数据；对于频繁的遍历操作，LinkedHashSet效率高于HashSet。
	* TreeSet：可以照添加对象的指定属性，进行排序。

### 5.5 存储对象所在类的要求

HashSet/LinkedHashSet

1. 向Set(主要指：HashSet、LinkedHashSet)中添加的数据，其所在的类一定要重写hashCode()和equals()
2. 重写的hashCode()和equals()尽可能保持一致性：相等的对象必须具有相等的散列码
3. 重写两个方法的小技巧：对象中用作 equals() 方法比较的 Field，都应该用来计算 hashCode 值。

TreeSet:

1. 自然排序中，比较两个对象是否相同的标准为：compareTo()返回0。不再是equals()。
2. 定制排序中，比较两个对象是否相同的标准为：compare()返回0.不再是equals().

### 5.6 TreeSet的使用

####  5.6.1 使用说明

1. 向TreeSet中添加的数据，要求是相同类的对象。
2. 两种排序方式：自然排序（实现Comparable接口 和 定制排序（Comparator））

#### 5.6.2 常用的排序方式:

1. 自然排序

   ```java
    @Test
       public void test1(){
           TreeSet set = new TreeSet();
   
           //失败：不能添加不同类的对象
   //        set.add(123);
   //        set.add(456);
   //        set.add("AA");
   //        set.add(new User("Tom",12));
   
               //举例一：
   //        set.add(34);
   //        set.add(-34);
   //        set.add(43);
   //        set.add(11);
   //        set.add(8);
   
           //举例二：
           set.add(new User("Tom",12));
           set.add(new User("Jerry",32));
           set.add(new User("Jim",2));
           set.add(new User("Mike",65));
           set.add(new User("Jack",33));
           set.add(new User("Jack",56));
   
   
           Iterator iterator = set.iterator();
           while(iterator.hasNext()){
               System.out.println(iterator.next());
           }
   
       }
   ```

2. 定制排序

   ```java
   @Test
       public void test2(){
           Comparator com = new Comparator() {
               //按照年龄从小到大排列
               @Override
               public int compare(Object o1, Object o2) {
                   if(o1 instanceof User && o2 instanceof User){
                       User u1 = (User)o1;
                       User u2 = (User)o2;
                       return Integer.compare(u1.getAge(),u2.getAge());
                   }else{
                       throw new RuntimeException("输入的数据类型不匹配");
                   }
               }
           };
   
           TreeSet set = new TreeSet(com);
           set.add(new User("Tom",12));
           set.add(new User("Jerry",32));
           set.add(new User("Jim",2));
           set.add(new User("Mike",65));
           set.add(new User("Mary",33));
           set.add(new User("Jack",33));
           set.add(new User("Jack",56));
   
   
           Iterator iterator = set.iterator();
           while(iterator.hasNext()){
               System.out.println(iterator.next());
           }
       }
   
   }
   ```

## 6 Queue接口

### 6.1 存储的数据特点

队列是一种特殊的线性表，它只允许在表的前端（front）进行删除操作，而在表的后端（rear）进行插入操作。进行插入操作的端称为队尾，进行删除操作的端称为队头。队列中没有元素时，称为空队列。

Java Queue是java.util包中提供的接口，并扩展了java.util.Collection接口。就像Java List一样，Java Queue是有序元素（或对象）的集合，但它以不同方式执行插入和删除操作。 在处理这些元素之前，我们可以使用Queue存储元素。

- java.util.Queue接口是java.util.Collection接口的子类型。
- 就像现实世界的排队（例如，在银行或ATM中）一样，Queue在队列的末尾插入元素并从队列的开头删除元素。
- Java Queue遵循FIFO顺序来插入和删除它的元素。 FIFO代表先入先出。
- Java Queue支持Collection接口的所有方法。
- 最常用的Queue实现是LinkedList，ArrayBlockingQueue和PriorityQueue。
- BlockingQueues不接受null元素。 如果我们执行任何与null相关的操作，它将抛出NullPointerException。
- BlockingQueues用于实现基于生产者/消费者的应用程序。
- BlockingQueues是线程安全的。
- java.util包中可用的所有队列都是无界队列，java.util.concurrent包中可用的队列是有界队列。
- 所有Deques都不是线程安全的。
- ConcurrentLinkedQueue 是一个基于链表的无界线程安全队列。
- 除了Deques之外，所有队列都支持在队列尾部插入并在队列的头部删除。Deques 是双端队列，它支持在队列两端插入和移除元素。

### 6.2 常用实现类

- Queue接口：
  - Deque接口：
    - **LinkedList**
    - ArrayDeque
  - AbstractQueue：
    - **PriorityQueue**
  - BlockingQueue
    - .......



### 6.3 常用方法

queue可以使用所有collections的方法，另外还有一些常用方法：

* add(E e)： 如果可以在不违反容量限制的情况下立即执行此操作，则将指定的元素插入此队列，成功时返回true，如果当前没有可用空间则抛出IllegalStateException。
* offer(E e)： 如果可以在不违反容量限制的情况下立即执行此操作，则将指定的元素插入此队列，成功时返回true，如果当前没有可用空间则返回 false。
* remove()：检索并删除此队列的头部元素。 此方法与poll的不同之处仅在于，如果此队列为空，则抛出异常 NoSuchElementException。
* poll()： 检索并删除此队列的头部，如果此队列为空，则返回null。
* element()：检索但不删除此队列的头部。 此方法与peek的不同之处仅在于，如果此队列为空，则抛出异常 NoSuchElementException。
* peek()：检索但不移除此队列的头部，如果此队列为空，则返回null。

### 6.4 队列常用操作方法的区别

#### 6.4.1 add 与 offer 区别

add 和 offer 方法都是向队列中添加一个元素。
当一个大小受限制的队列满时，使用 add 方法将会抛出一个 unchecked 异常；使用 offer 方法会返回 false。

#### 6.4.2 remove 与 poll 区别

remove() 和 poll() 方法都是删除并返回队列头部的第一个元素。确切地说，从队列中删除哪个元素是队列排序策略的一个功能，该策略因实现而异。

remove（）和 poll（）方法的不同之处仅在于队列为空时的行为：remove（）方法抛出异常，而poll（）方法返回null。

#### 6.4.3 element 与 peek 区别

element（）和 peek（）方法返回但不删除队列的头部元素。与 remove() 方法类似，在队列为空时， element() 抛出一个异常，而 peek() 返回 null。



综上所述，Queue使用时要尽量避免Collection的add()和remove()以及element()方法，而是要使用offer()来加入元素，使用poll()来获取并移出元素，使用peek()方法查看前端而不移出该元素。它们的优点是通过返回值可以判断成功与否，add()和remove()方法在失败的时候会抛出异常。 

### 6.5 源码分析

```java
public interface Queue<E> extends Collection<E> {
    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * {@code true} upon success and throwing an {@code IllegalStateException}
     * if no space is currently available.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     *         this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this queue
     */
    boolean add(E e);

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions.
     * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     *         this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this queue
     */
    boolean offer(E e);

    /**
     * Retrieves and removes the head of this queue.  This method differs
     * from {@link #poll poll} only in that it throws an exception if this
     * queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    E remove();

    /**
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    E poll();

    /**
     * Retrieves, but does not remove, the head of this queue.  This method
     * differs from {@link #peek peek} only in that it throws an exception
     * if this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    E element();

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    E peek();
}
```



### 6.6 PriorityQueue优先队列

#### 6.6.1 定义

- 我们知道队列是遵循先进先出（First-In-First-Out）模式的，但有些时候需要在队列中基于优先级处理对象。举个例子，比方说我们有一个每日交易时段生成股票报告的应用程序，需要处理大量数据并且花费很多处理时间。客户向这个应用程序发送请求时，实际上就进入了队列。我们需要首先处理优先客户再处理普通用户。在这种情况下，Java的PriorityQueue(优先队列)会很有帮助。
- PriorityQueue类在Java1.5中引入并作为 Java Collections Framework 的一部分。PriorityQueue是基于优先堆的一个无界队列，这个优先队列中的元素可以默认自然排序（升序）或者通过提供的Comparator（比较器）在队列实例化的时排序。
- 优先队列不允许空值，而且不支持non-comparable（不可比较）的对象，比如用户自定义的类。优先队列要求使用Java Comparable和Comparator接口给对象排序，并且在排序时会按照优先级处理其中的元素。
- **优先队列的头是基于自然排序或者Comparator排序的最小元素（即从头到尾升序）。如果有多个对象拥有同样的排序，那么就可能随机地取其中任意一个。当我们获取队列时，返回队列的头对象。**
- 优先队列的大小是不受限制的，但在创建时可以指定初始大小。当我们向优先队列增加元素的时候，队列大小会自动增加。
- PriorityQueue是非线程安全的，所以Java提供了PriorityBlockingQueue（实现BlockingQueue接口）用于Java多线程环境。
- **我们有一个用户类，它没有提供任何类型的排序。当我们用它建立优先队列时，应该为其提供一个比较器对象。**



我们以一道题为例来解释优先队列的使用。

#### 6.6.2 案例理解

我们有一个由平面上的点组成的列表 points。需要从中找出 K 个距离原点 (0, 0) 最近的点。（这里，平面上两点之间的距离是欧几里德距离。）你可以按任何顺序返回答案。除了点坐标的顺序之外，答案确保是唯一的。

实例1

```
输入：points = [[1,3],[-2,2]], K = 1
输出：[[-2,2]]
解释： 
(1, 3) 和原点之间的距离为 sqrt(10)，
(-2, 2) 和原点之间的距离为 sqrt(8)，
由于 sqrt(8) < sqrt(10)，(-2, 2) 离原点更近。
我们只需要距离原点最近的 K = 1 个点，所以答案就是 [[-2,2]]。
1234567
```

实例2

```
输入：points = [[3,3],[5,-1],[-2,4]], K = 2
输出：[[3,3],[-2,4]]
（答案 [[-2,4],[3,3]] 也会被接受。）
123
```

思路

维护一个大小为K的优先队列。始终让栈顶为队列中距离远点最大的点。我们要设计一个类，并为其设计一个比较器类。注意比较器里面的compare函数中，要让比较为从大到小。（队列头到队列尾）。

代码

```java
class Solution {
    public int[][] kClosest(int[][] points, int K) {
        //使用K大小的最小堆。（优先队列）
        int [][]ps=new int[K][2];
        PriorityQueue<Point>pq=new PriorityQueue<>(pointComparator);
        for(int i=0;i<points.length;i++)
        {
            pq.offer(new Point(points[i][0],points[i][1]));
            if(pq.size()>K){
                pq.poll();
            }
        }
        for(int i=0;i<K;i++)
        {
            Point point=pq.poll();
            ps[i][0]=point.x;
            ps[i][1]=point.y;
        }
        return ps;
    }
     //匿名Comparator实现
    public static Comparator<Point> pointComparator = new Comparator<Point>(){
        //让当前距离最大的在队列，随时准备踢出去。因为队列底要放的是距离最小的
        @Override
        public int compare(Point p1, Point p2) {
            return (int) ((p2.x*p2.x+p2.y*p2.y)-(p1.x*p1.x+p1.y*p1.y));
        }
    };
    class Point{
        int x;
        int y;
        public Point(int x,int y){
            this.x=x;
            this.y=y;
        }
    }
}
```



## 7 Map接口

双列集合框架：Map

### 7.1 常用实现类

1.常用实现类结构
Map:双列数据，存储key-value对的数据   ---类似于高中的函数：y = f(x)

* HashMap:作为Map的主要实现类；线程不安全的，效率高；存储null的key和value
	* LinkedHashMap:保证在遍历map元素时，可以照添加的顺序实现遍历。原因：在原的HashMap底层结构基础上，添加了一对指针，指向前一个和后一个元素。对于频繁的遍历操作，此类执行效率高于HashMap。
* TreeMap: 保证照添加的key-value对进行排序，实现排序遍历。此时考虑key的自然排序或定制排序；底层使用红黑树
* Hashtable: 作为古老的实现类；线程安全的，效率低；不能存储null的key和value
	* Properties: 常用来处理配置文件。key和value都是String类型



HashMap的底层：数组+链表  （jdk7及之前)

数组+链表+红黑树 （jdk 8)



[面试题]

1. HashMap的底层实现原理？
2. HashMap 和 Hashtable的异同？
3. CurrentHashMap 与 Hashtable的异同？（暂时不讲)

### 7.2 存储结构

* Map中的key: 无序的、不可重复的，使用Set存储所有的key  ---> key所在的类要重写equals()和hashCode() （以HashMap为例)
* Map中的value: 无序的、可重复的，使用Collection存储所有的value --->value所在的类要重写equals()
* 一个键值对：key-value构成了一个Entry对象。
* Map中的entry:无序的、不可重复的，使用Set存储所有的entry

图示：

![HashMap存储结构](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025205910.png)

### 7.3 常用方法

* 添加：put(Object key,Object value)
* 删除：remove(Object key)
* 修改：put(Object key,Object value)
* 查询：get(Object key)
* 长度：size()
* 遍历：keySet() / values() / entrySet()

### 7.4 内存结构说明（难点）

#### 7.4.1 HashMap在jdk7中实现原理

```java
HashMap map = new HashMap();
```

在实例化以后，底层创建了长度是16的一维数组Entry[] table。

...可能已经执行过多次put后...

```java
map.put(key1,value1);
```

首先，调用key1所在类的hashCode()计算key1哈希值，此哈希值经过某种算法计算以后，得到在Entry数组中的存放位置。

* 如果此位置上的数据为空，此时的key1-value1添加成功。 ----情况1

* 如果此位置上的数据不为空，(意味着此位置上存在一个或多个数据(以链表形式存在))，比较key1和已经存在的一个或多个数据的哈希值：

  * 如果key1的哈希值与已经存在的数据的哈希值都不相同，此时key1-value1添加成功。----情况2

  * 如果key1的哈希值和已经存在的某一个数据(key2-value2)的哈希值相同，继续比较：调用key1所在类的equals(key2)方法，比较：

    * 如果equals()返回false:此时key1-value1添加成功。----情况3

    * 如果equals()返回true:使用value1替换value2。

补充：关于情况2和情况3：此时key1-value1和原来的数据以链表的方式存储。


在不断的添加过程中，会涉及到扩容问题，当超出临界值(且要存放的位置非空)时，扩容。默认的扩容方式：扩容为原来容量的2倍，并将原的数据复制过来。

#### 7.4.2 HashMap在jdk8中的不同

1. `new HashMap()`：底层没创建一个长度为16的数组
   jdk8底层的数组是：`Node[]`,而非`Entry[]`
3. 首次调用`put()`方法时，底层创建长度为16的数组
4. jdk7底层结构：数组+链表。jdk8中底层结构：数组+链表+红黑树。
* 形成链表时，七上八下（jdk7:新的元素指向旧的元素。jdk8：旧的元素指向新的元素）
*  当数组的某一个索引位置上的元素以链表形式存在的数据个数 > 8 且当前数组的长度 > 64时，此时此索引位置上的所数据改为使用红黑树存储。

#### 7.4.3 HashMap底层典型属性

* `DEFAULT_INITIAL_CAPACITY` : HashMap的默认容量，16
* `DEFAULT_LOAD_FACTOR`：HashMap的默认加载因子：0.75
* `threshold`：扩容的临界值，=容量*填充因子：16 * 0.75 => 12
* `TREEIFY_THRESHOLD`：Bucket中链表长度大于该默认值，转化为红黑树:8
* `MIN_TREEIFY_CAPACITY`：桶中的Node被树化时最小的hash表容量:64

#### 7.4.4 LinkedHashMap的底层实现原理（了解）

LinkedHashMap底层使用的结构与HashMap相同，因为LinkedHashMap继承于HashMap。

区别就在于：LinkedHashMap内部提供了Entry，替换HashMap中的Node.

![HashMap和LinkedHashMap](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025210952.png)

### 7.5 TreeMap的使用

向TreeMap中添加key-value，要求key必须是由同一个类创建的对象，因为要照key进行排序：自然排序 、定制排序

### 7.6 使用Properties读取配置文件

```java
//Properties:常用来处理配置文件。key和value都是String类型
public static void main(String[] args)  {
    FileInputStream fis = null;
    try {
        Properties pros = new Properties();

        fis = new FileInputStream("jdbc.properties");
        pros.load(fis);//加载流对应的文件

        String name = pros.getProperty("name");
        String password = pros.getProperty("password");

        System.out.println("name = " + name + ", password = " + password);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if(fis != null){
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
```

## 8 Collections工具类

作用：操作Collection和Map的工具类

### 8.1 常用方法

* reverse(List)：反转 List 中元素的顺序
* shuffle(List)：对 List 集合元素进行随机排序
* sort(List)：根据元素的自然顺序对指定 List 集合元素升序排序
* sort(List，Comparator)：根据指定的 Comparator 产生的顺序对 List 集合元素进行排序
* swap(List，int， int)：将指定 list 集合中的 i 处元素和 j 处元素进行交换
* Object max(Collection)：根据元素的自然顺序，返回给定集合中的最大元素
* Object max(Collection，Comparator)：根据 Comparator 指定的顺序，返回给定集合中的最大元素
* Object min(Collection)
* Object min(Collection，Comparator)
* int frequency(Collection，Object)：返回指定集合中指定元素的出现次数
* void copy(List dest,List src)：将src中的内容复制到dest中
* boolean replaceAll(List list，Object oldVal，Object newVal)：使用新值替换 List 对象的所旧值

面试题：Collection 和 Collections的区别？