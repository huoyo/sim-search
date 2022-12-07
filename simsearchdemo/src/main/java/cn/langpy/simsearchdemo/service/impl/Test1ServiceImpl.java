package cn.langpy.simsearchdemo.service.impl;

import cn.langpy.simsearch.annotation.CreateIndex;
import cn.langpy.simsearch.annotation.DeleteIndex;
import cn.langpy.simsearch.annotation.SearchIndex;
import cn.langpy.simsearchdemo.entity.Test1;
import cn.langpy.simsearchdemo.service.Test1Service;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Test1ServiceImpl implements Test1Service {
    @Override
    @CreateIndex
    public void add(Test1 test1) {
        System.out.println("插入："+test1);
    }

    @Override
    @CreateIndex
    public void update(Test1 test1) {
        System.out.println("更新："+test1);
    }

    @Override
    @DeleteIndex
    public void delete(Test1 test1) {
        System.out.println("删除："+test1);

    }

    @Override
    @SearchIndex(by = "name")
    /*by = "name"指定搜索的参数为Test1对象name属性*/
    public List<Test1> fuzzyQuery(Test1 test1) {
        /*方法内部什么都不需要写*/
        /*如果再索引中未查到对应信息，可通过该方法设置默认查询，比如往数据库进行like模糊匹配*/
        return null;
    }

    @Override
    @SearchIndex(by = "name",indexParam = "test1")
    /*by = "name"指定搜索的参数为Test1对象name属性*/
    /*方法中有多个参数的情况下需要指定搜索的参数*/
    public List<Test1> fuzzyQuery(String xxx, Test1 test1) {
        return null;
    }
}
