# sim-search

#### 介绍
基于lucene的spring项目索引构建工具，不只是简单快捷搜索：

大部分项目都会涉及简单的搜索功能，本项目提供一个简单索引构建工具，使用注解，轻便高效搜索，
避免项目中大量使用like或者其他效率低的搜索机制，相比于ES,这是一种轻量级的实现方式

优点：
> * 无缝集成spring，使用简单
> * 注解实现索引创建和搜索，与业务逻辑无耦合
> * 可用于大数据量搜索，毫秒级响应
> * 基于倒排索引，避免全量匹配和手动分词

缺点：
> * 目前仅适用于单机版，不支持分布式和集群
> * 索引存储单一，暂时不适配多种存储介质
> * 暂不支持大于小于之类的比较搜索

#### 安装教程

1.  引入依赖 或者 下载发行版本
```
 <dependency>
    <groupId>cn.langpy</groupId>
    <artifactId>simsearch</artifactId>
    <version>1.0-snapshots</version>
  </dependency>
```
2.  配置信息

在application.yml中配置
```
sim-search.dir=xxx  //索引位置，可不填，使用默认位置：当前项目下的indexs
sim-search.size.core=10  //创建索引的核心线程数量，根据cpu自行决定，可不填，默认为10
sim-search.size.max=10  //创建索引的最大线程数量，根据cpu自行决定，可不填，默认为200
sim-search.size.queue=1000 //创建索引的线程队列容量，自行决定，可不填，默认为20000
```

#### 使用说明

1.  在需要创建索引的实体上标注需要创建索引的字段
```java
import cn.langpy.simsearch.annotation.IndexColumn;
import cn.langpy.simsearch.annotation.IndexId;

public class Student {
    /*索引唯一id*/
    @IndexId 
    private String id;
    /*需要创建索引的字段*/
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
  /*加上@DeleteIndex后 异步删除索引，不影响正常业务的保存逻辑 indexParam:需要删除索引的参数*/
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
   public  List<Student> search(String studentName){
    /*方法内部什么都不需要写*/
     return null;
   }

   /*根据schoolName属性搜索Student */
   @SearchIndex(by = "schoolName")
   public  List<Student> search(String schoolName){
    /*方法内部什么都不需要写*/
     return null;
   }
}
```
注意：搜索结果仅仅是搜索出加上@IndexId和@IndexColumn的字段，具体内容自行往业务数据库查询

#### 说明

1.  本项目为个人构建，如有问题可联系本人：1729913829@qq.com
2.  本项目中使用了aspectjweaver依赖，如果引入的项目中没有该依赖，自行引入
```
 <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>xxx</version>
  </dependency>
```
