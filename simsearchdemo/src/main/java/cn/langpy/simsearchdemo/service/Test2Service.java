package cn.langpy.simsearchdemo.service;

import cn.langpy.simsearchdemo.entity.Test1;
import cn.langpy.simsearchdemo.entity.Test2;

import java.util.List;

public interface Test2Service {
    void add(Test2 test1);
    void update(Test2 test1);
    void delete(Test2 test1);
    List<Test2> fuzzyQuery(Test2 test1);
}
