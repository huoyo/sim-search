# sim-search

#### 介绍
基于lucene的spring项目索引构建工具

#### 软件架构
软件架构说明


#### 安装教程

1.  引入依赖
```properties
 <dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queryparser</artifactId>
    <version>${simsearch.version}</version>
  </dependency>
```
2.  配置信息
在application.yml中配置
```properties
sim-search.dir=xxx  //索引位置，可不填，使用默认位置：当前项目下的indexs
sim-search.size.core=10  //创建索引的核心线程数量，根据cpu自行决定，可不填，默认为10
sim-search.size.max=10  //创建索引的最大线程数量，根据cpu自行决定，可不填，默认为200
sim-search.size.queue=1000 //创建索引的线程队列容量，自行决定，可不填，默认为20000
```

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx


#### 说明

1.  本项目为个人构建，如有问题可联系本人：1729913829@qq.com
