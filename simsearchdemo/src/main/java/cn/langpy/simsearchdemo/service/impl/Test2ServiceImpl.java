package cn.langpy.simsearchdemo.service.impl;

import cn.langpy.simsearch.annotation.CreateIndex;
import cn.langpy.simsearch.annotation.DeleteIndex;
import cn.langpy.simsearch.annotation.SearchIndex;
import cn.langpy.simsearchdemo.entity.Test1;
import cn.langpy.simsearchdemo.entity.Test2;
import cn.langpy.simsearchdemo.service.Test1Service;
import cn.langpy.simsearchdemo.service.Test2Service;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Test2ServiceImpl implements Test2Service {
    @Override
    @CreateIndex
    public void add(Test2 test1) {
        System.out.println("插入："+test1);
    }

    @Override
    @CreateIndex
    public void update(Test2 test1) {
        System.out.println("更新："+test1);
    }

    @Override
    @DeleteIndex
    public void delete(Test2 test1) {
        System.out.println("删除："+test1);

    }

    @Override
    @SearchIndex(by = "name")
    public List<Test2> fuzzyQuery(Test2 test1) {
        /*方法内部什么都不需要写*/
        /*如果再索引中未查到对应信息，可通过该方法设置默认查询，比如往数据库进行like模糊匹配*/
        return null;
    }
}
