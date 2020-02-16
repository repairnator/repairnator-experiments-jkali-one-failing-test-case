# 1. 概述

分代版的jmap（新生代，存活区，老生代），是排查内存缓慢泄露，老生代增长过快原因的利器。因为`jmap -histo PID`  打印的是整个Heap的对象统计信息，而为了定位上面的问题，我们需要专门查看OldGen对象，和Survivor区大龄对象的工具。

vjmap的原始思路来源于R大的[TBJMap](https://github.com/alibaba/TBJMap) ，翻新后支持JDK8，支持Survivor区大龄对象过滤，以及大天秤对输出结果不要看歪脖子的执着。


这里有一篇实战：[【唯实践】JVM老生代增长过快问题排查](https://mp.weixin.qq.com/s/6cJ5JuEgEWmMBzJFBDsSMg)，最后定位到是Jedis的锅。


注意：因为vjmap的原理，只支持CMS和ParallelGC，不支持G1。


# 2.使用说明

[Maven Central 下载](http://repo1.maven.org/maven2/com/vip/vjtools/vjmap/1.0.1/vjmap-1.0.1.zip)

注意：vjmap在执行过程中，会完全停止应用一段时间，必须摘流量执行！！！！

必须设定JAVA_HOME环境变量，必须与目标JVM使用相同的JDK版本运行，必须与目标JVM使用相同用户运行，或root用户执行 (sudo -E vjmap.sh ...，)

vjmap的运行需要一段时间，如果中途需要停止执行，请使用kill vjmap的进程号，让vjmap从目标进程退出。如果错用了kill -9 ，目标java进程会保持在阻塞状态不再工作，此时必须执行两次 kill -18 目标进程PID来重新唤醒目标java进程。



    
## 2.1 常用指令

```
// 打印整个堆中对象的统计信息，按对象的total size排序:
./vjmap.sh -all PID > /tmp/histo.log

// 打印老年代的对象统计信息，按对象的oldgen size排序，暂时只支持CMS:  
./vjmap.sh -old PID > /tmp/histo-old.log

// 打印Survivor区的对象统计信息，默认age>=3: 
./vjmap.sh -sur PID > /tmp/histo-sur.log

// 打印Survivor区的对象统计信息，age>=10
// 先增大晋升阈值-XX:MaxTenuringThreshold=xx, 通过查询age较高的对象，即必定会逃逸到cms区的对象。
./vjmap.sh -sur:minage=10 PID > /tmp/histo-sur.log
```

> 其中PID为目标java进程的进程号。



## 2.2 过滤对象大小，不显示过小的对象:

```
// 按对象的total size进行过滤，只打印占用超过1K的数据
./vjmap.sh -all:minsize=1024 PID > /tmp/histo.log

// 按对象的oldgen size进行过滤，只打印OldGen占用超过1K的数据
./vjmap.sh -old:minsize=1024 PID > /tmp/histo-old.log

// 按对象的survivor size进行过滤，只打印Survivor占用超过1K的数据
./vjmap.sh -sur:minsize=1024 PID > /tmp/histo-sur.log
```

## 2.3 按class name排序，配合大小过滤生成用于定时比较的报表:

```
./vjmap.sh -all:minsize=1024,byname PID > /tmp/histo.log

./vjmap.sh -old:minsize=1024,byname PID > /tmp/histo-old.log

./vjmap.sh -sur:minsize=1024,byname PID > /tmp/histo-sur.log
```

# 3.输出示例


## 3.1 Survivor区年龄大于N的对象统计

```
Survivor Object Histogram:

 #num  #count     #bytes #Class description
-----------------------------------------------------------------------------------
   1:      37         1k io.netty.buffer.PoolThreadCache$MemoryRegionCache$Entry
   2:       2         64 java.util.concurrent.locks.AbstractQueuedSynchronizer$Node
Total: 39/    1k over age 2

Heap traversal took 1.3 seconds.
```

# 4. 使用Eclipse MAT进一步分析

如果只依靠对象统计信息，不足以定位问题，需要使用完整HeapDump，计算对象关联关系来进一步分析时，可以在MAT中使用OQL过滤出老生代的对象。

假设，OldGen地址范围是"0xfbd4c000" ～ "0xfce94050"

```
SELECT * FROM INSTANCEOF java.lang.Object t WHERE (toHex(t.@objectAddress) >= "0xfbd4c000" AND toHex(t.@objectAddress) <= "0xfce94050")
```

用如下方式可获得老生代地址：

第一种方式是在启动参数增加 -XX:+PrintHeapAtGC

第二种方式是使用vjmap的命令，在-old, -sur, -address 中，都会打印出区间的地址。 

```
./vjmap.sh -address PID

``` 

输出如下：
```
  eden [0x0000000119000000,0x0000000119c4a258,0x0000000121880000) space capacity = 143130624, 9.003395387977907 used
  from [0x0000000121880000,0x0000000121880000,0x0000000122990000) space capacity = 17891328, 0.0 used
  to   [0x0000000122990000,0x0000000122990000,0x0000000123aa0000) space capacity = 17891328, 0.0 used
concurrent mark-sweep generation
free-list-space[ 0x0000000123aa0000 , 0x0000000139000000 ) space capacity = 357957632 used(4%)= 17024696 free= 340932936
```


# 5. 与TBJMap的对比

* 兼容JDK8
* 新功能：Survivor区 age大于N的对象统计
* 新功能：打印各分代的地址区间，用于MAT进一步分析
* 性能提升：直接访问Survivor或OldGen区，而不是以Heap Visitor回调的方式访问整个Heap
* 新配置项：按对象的大小进行过滤，不显示过小的对象
* 新配置项：按对象的名称进行排序，用于生成定时比较的报表
* 输出改进：报表数字的单位化(k,m,g)与对齐，OldGen报表默认按对象在OldGen的大小排序

```