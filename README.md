
> 本项目由于之前的架构有些问题，关闭了一段时间，经过重构进行开放，也很期待各位提出宝贵的建议

[别拦我，我要支持作者](https://afdian.net/a/huoyo)

<div align="center">
    <h1 >SimSearch<img src='https://shields.io/badge/forJava-r.svg'></h1>  
</div>

<div align="center">
    <img src='https://shields.io/badge/version-2.0.0-green.svg'>
    <img src='https://shields.io/badge/author-Chang Zhang-dbab09.svg'>
    <img src='https://shields.io/badge/dependencies-Spring|aspectjweaver|lucene-r.svg'>
</div>

#### 介绍

一个轻量级的springboot项目索引构建工具，实现快速模糊搜索：

大部分项目都会涉及模糊搜索功能，本项目提供一个简单索引构建工具，使用注解即可实现索引自动创建与搜索，而不需要你手写该过程，
避免项目中大量使用like或者其他效率低的搜索机制，相比于ES（专业搜索引擎）,这是一种轻量级的实现方式

特点：

- [x] 无缝集成springboot，使用简单
- [x] 注解实现索引创建和搜索，与业务逻辑无耦合
- [x] 可用于大数据量模糊搜索，毫秒级响应
- [x] 基于倒排索引，避免全量匹配和手动分词

#### 索引测试

使用[100万行中文](https://gitee.com/huoyo/sim-search/blob/master/test/text.txt) 创建索引后进行模糊搜索测试

`i7 16g`

| 存储模式（saver） | 平均耗时 | 最大耗时 | 最小耗时 |
|:-|:-:|:-:|:-:|
| nio-fs([nio](https://baike.baidu.com/item/java.nio/5180052?fr=aladdin)文件模式)      | 45.93ms   | 144.5ms  | 15.04ms  |
| base-fs(常规文件系统)      | 25.93ms   | 97.32ms  | 12.24ms  |
| memory-fs([nmap文件模式](https://baike.baidu.com/item/mmap/1322217?fr=aladdin))      | 23.75ms   | 114.12ms  | 8.4ms  |
| memory(内存模式)      | 11.56ms   | 42.35ms  | 7.04ms  |

使用1000万行类似的中文创建索引后进行模糊搜索测试

`i7 16g`

| 存储模式（saver） | 平均耗时 | 最大耗时 | 最小耗时 |
|:-|:-:|:-:|:-:|
| nio-fs([nio](https://baike.baidu.com/item/java.nio/5180052?fr=aladdin)文件模式)      | 55.33ms   | 151.5ms  | 20.65ms  |
| base-fs(常规文件系统)      | 63.02ms   | 102.73ms  | 15.34ms  |
| memory-fs([nmap文件模式](https://baike.baidu.com/item/mmap/1322217?fr=aladdin))      | 51.44ms   | 180.38ms  | 7.15ms  |
| memory(内存模式)      | 12.98ms   | 50.26ms  | 8.04ms  |

`相对于文件系统，内存性能最好的无疑的，前提是内存有余，或者数据量不是很过分`

`测试的文本相对来说较短，如果是长文本，理论上上来说性能会下降一些`

`另外，以上结果仅仅作为一个参考`



#### 安装教程

1.  引入依赖

```
 <dependency>
    <groupId>cn.langpy</groupId>
    <artifactId>simsearch</artifactId>
    <version>2.0.0</version>
  </dependency>
```

`使用了@Aspect注解，未引入的自行引入，如aspectj或者spring-boot-starter-aop`

2.  配置信息

在application.properties中配置

```properties
#索引存储器 默认为内存 [memory,memory-fs,base-fs,nio-fs]
#内存富裕的情况下使用memory，如果是百万以上数据量选用fs系列
sim-search.saver=memory
#索引位置，saver!=memory时需配置
sim-search.dir=/data/indexlocation
#创建索引的核心线程数量，根据cpu自行决定，可不填，默认为5
sim-search.thread-core-size=5
#创建索引的最大线程数量，根据cpu自行决定，可不填，默认为200
sim-search.thread-max-size=10
#创建索引的线程队列容量，自行决定，可不填，默认为200000
sim-search.thread-queue-size=200000
#重启时是否要对之前的索引进行删除，默认为false
sim-search.index.init=true
#最大返回的搜索结果数量
sim-search.result.size=50
```

#### 使用说明

>也可参考[demo项目](https://gitee.com/huoyo/sim-search/tree/master/simsearchdemo)

1.  在需要创建索引的实体上标注需要创建索引的字段

```java
import cn.langpy.simsearch.annotation.IndexColumn;
import cn.langpy.simsearch.annotation.IndexId;

public class Student {
    /*索引唯一id 必须*/
    @IndexId 
    private String id;
    /*需要创建索引的字段：用来模糊搜索*/
    @IndexColumn
    private String studentName;
    @IndexColumn
    private String schoolName;
    private String age;
}
```

2.  在需要创建索引的方法上加上创建索引的注解

```java
import cn.langpy.simsearch.annotation.CreateIndex;
import cn.langpy.simsearch.annotation.DeleteIndex;
import cn.langpy.simsearch.annotation.SearchIndex;

@Service
public class StudentServiceImpl implements StudentService {
    /*加上@CreateIndex后 异步创建索引，不影响正常业务的保存逻辑 indexParam:需要创建索引的参数*/
    /*该注解包含了更新操作 有则更新 无则创建*/
    @CreateIndex(indexParam = "student")
   public  boolean insert(Student student){
     /*业务逻辑*/
   }
}
```

3.  在需要删除索引的方法上加上删除索引的注解

```java
@Service
public class StudentServiceImpl implements StudentService {
  /*加上@DeleteIndex后 异步删除索引，不影响正常业务的删除逻辑 indexParam:需要删除索引的参数*/
   @DeleteIndex(indexParam = "student")
   public  boolean delete(Student student){
     /*业务逻辑*/
   }
}
```

4.  搜索的时候自定义一个空的方法，加上注解即可

```java
@Service
public class StudentServiceImpl implements StudentService {
   /*根据studentName属性搜索Student 搜索的属性要和实体的属性保持一致  */
   @SearchIndex(by = "studentName")
   public  List<Student> search(Student student){
    /*方法内部什么都不需要写*/
     return null;
   }

   /*根据schoolName属性搜索Student */
   @SearchIndex(by = "schoolName")
   public  List<Student> search(Student Student){
    /*方法内部什么都不需要写*/
    /*如果再索引中未查到对应信息，可通过该方法设置默认查询，比如往数据库进行like模糊匹配*/
     return searchWithLikeByName(schoolName);
   }
}
```

`注意：搜索结果仅仅是搜索出加上@IndexId和@IndexColumn的字段，具体内容自行往业务数据库查询`

#### 工具类

```java
public class IndexManager{
    /*为对象创建索引*/
    public static void createIndex(Object entity);
    public static void createIndexs(List<Object> entities);
    /*创建索引*/
    public static void createIndex(IndexContent indexContent);
    /*删除索引*/
    public static void deleteIndex(String idName, String idValue,Class entityClass);
    /*搜索 详见源码的demo项目*/
    public static <T> List<T> searchIndexIds(String name, String value,Class<?> entityClass);
    /*搜索 详见源码的demo项目*/
    public static <T> List<T> searchIndexObjects(String name, String value,Class entityClass);
    public static void deleteAll();
}
```

#### 版本说明

> V1.0-snapshots：提供基础索引创建、删除和检索功能

> V1.1：增加重启索引初始化功能

> V1.2：搜索时，如果未找到搜索，可走默认模式

> V1.3：修复启动时索引目录未创建报错的bug

> V2.0.0.BETA：全新的版本

#### 问题说明

1.  本项目中使用了aspectjweaver依赖，如果项目中没有该依赖（或者spring-boot-starter-aop），自行引入

```
 <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>xxx</version>
  </dependency>
```

2. 分布式项目中的索引同步，可以自行从数据库加载数据(或者使用canal进行同步)，然后创建索引

```java
public class Student {
    /*索引唯一id 必须*/
    @IndexId
    private String id;
    /*需要创建索引的字段：用来模糊搜索*/
    @IndexColumn
    private String studentName;
}
```
```java
Student student = new Student();
        ...
IndexManager.createIndex(student);
```

#### 技术栈

- lucene
- aop
- spring