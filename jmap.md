```bash

bogon:jvm_g1源码分析与调优 kate$ jmap -heap 1323
Attaching to process ID 1323, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.131-b11

using thread-local object allocation.
Parallel GC with 4 thread(s) ##  GC方式 ：采用Parallel GC 
# - 老年代是新生代的两倍
# - 一般eden是survivor区的8倍
Heap Configuration: ## 堆内存初始化配置
   MinHeapFreeRatio         = 0 ## 对应jvm启动参数-XX:MinHeapFreeRatio设置JVM堆最小空闲比率(default 40)
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 2147483648 (2048.0MB) ## 对应jvm启动参数-XX:MaxHeapSize=设置JVM堆的最大大小
   NewSize                  = 44564480 (42.5MB) ## (NewSize = Eden + survivor1 + survivor2) 对应jvm启动参数-XX:NewSize=设置JVM堆的‘新生代’的默认大小 
   MaxNewSize               = 715653120 (682.5MB) ## 对应jvm启动参数-XX:MaxNewSize=设置JVM堆的‘新生代’的最大大小
   OldSize                  = 89653248 (85.5MB) ## 对应jvm启动参数-XX:OldSize=<value>:设置JVM堆的‘老生代’的大小
   # NewRatio //新生代：老生代（的大小）=1:2 可由-XX:NewRatio=<n>参数指定New Generation与Old Generation heap size的比例。
   NewRatio                 = 2 ## 对应jvm启动参数-XX:NewRatio=:‘新生代’和‘老生代’的大小比率
   # survivor:eden = 1:8,即survivor space是新生代大小的1/(8+2)[因为有两个survivor区域] 可由-XX:SurvivorRatio=<n>参数设置
   SurvivorRatio            = 8  ## 对应jvm启动参数-XX:SurvivorRatio=设置年轻代中Eden区与Survivor区的大小比值
   MetaspaceSize            = 21807104 (20.796875MB) ## 元空间的默认大小，超过此值就会触发Full GC 可由-XX:MetaspaceSize=<n>参数设置
   CompressedClassSpaceSize = 1073741824 (1024.0MB)  ## 类指针压缩空间的默认大小 可由-XX:CompressedClassSpaceSize=<n>参数设置
   MaxMetaspaceSize         = 17592186044415 MB  ## 元空间的最大大小 可由-XX:MaxMetaspaceSize=<n>参数设置
   G1HeapRegionSize         = 0 (0.0MB) ## 使用G1垃圾收集器的时候，堆被分割的大小 可由-XX:G1HeapRegionSize=<n>参数设置

Heap Usage: ## 堆内存分布
PS Young Generation
Eden Space: ## Eden区内存分布
   capacity = 26738688 (25.5MB) ## Eden区总容量
   used     = 534824 (0.5100479125976562MB) ## Eden区已经使用
   free     = 26203864 (24.989952087402344MB) ## Eden区剩余容量
   2.0001878925398286% used ## Eden区使用比例
From Space: ## 其中一个Survivor区的内存分布
   capacity = 1572864 (1.5MB)
   used     = 0 (0.0MB)
   free     = 1572864 (1.5MB)
   0.0% used
To Space: ## 另一个一个Survivor区的内存分布
   capacity = 2621440 (2.5MB)
   used     = 0 (0.0MB)
   free     = 2621440 (2.5MB)
   0.0% used
PS Old Generation ## 老年代内存分布
   capacity = 89653248 (85.5MB)
   used     = 1614816 (1.540008544921875MB)
   free     = 88038432 (83.95999145507812MB)
   1.8011795847039473% used

2908 interned Strings occupying 239456 bytes.


```


## java8去掉永久代，使用元空间时间方法区
https://www.jianshu.com/p/258fd5b6734a
### 7. Metaspace（元空间）-- 元空间并不在虚拟机中，而是使用本地内存
元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过元空间与永久代之间最大的区别在于：
元空间并不在虚拟机中，而是使用本地内存。因此，默认情况下，元空间的大小仅受本地内存限制，
但可以通过以下参数来指定元空间的大小：

### 8. 为什么从永久代切换到元空间？

1）字符串存在永久代中，容易出现性能问题和内存溢出。

2）类及方法的信息等比较难确定其大小，因此对于永久代的大小指定比较困难，太小容易出现永久代溢出，太大则容易导致老年代溢出。

3）永久代会为 GC 带来不必要的复杂度，并且回收效率偏低。
