# sim-search

#### 介绍
基于lucene的spring项目索引构建工具
对于小型项目而言
大部分项目都会涉及简单的搜索功能，本项目提供一个简单索引构建工具，轻便搜索，
避免项目中大量使用like，或者ES之类的重量级软件

#### 软件架构
索引基于lucene的倒排索引创建


#### 安装教程

1.  引入依赖
```
 <dependency>
    <groupId>cn.langpy</groupId>
    <artifactId>simsearch</artifactId>
    <version>${simsearch.version}</version>
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
    private String name;
    private String age;
}
```
2.  在需要创建索引的方法上加上创建索引的注解
```java
import cn.langpy.simsearch.annotation.CreateIndex;
import cn.langpy.simsearch.annotation.DeleteIndex;
import cn.langpy.simsearch.annotation.SearchIndex;
import java.util.List;
public class StudentServiceImpl {
    /*加上@CreateIndex后 异步创建索引，不影响正常业务的保存逻辑 indexParam:需要创建索引的参数*/
    @CreateIndex(indexParam = "student")
   public  boolean insert(Student student){
     /*业务逻辑*/
   }

    /*加上@DeleteIndex后 异步删除索引，不影响正常业务的保存逻辑 indexParam:需要删除索引的参数*/
   @DeleteIndex(indexParam = "student")
   public  boolean delete(Student student){
     /*业务逻辑*/
   }

   /*根据name属性查询Student  */
   @SearchIndex(by = "name")
   public  List<Student> search(String name){
    /*方法内部什么都不需要写*/
     return null;
   }
}
```

#### 说明

1.  本项目为个人构建，如有问题可联系本人：1729913829@qq.com
