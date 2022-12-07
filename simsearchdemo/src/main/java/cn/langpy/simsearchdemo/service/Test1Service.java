package cn.langpy.simsearchdemo.service;

import cn.langpy.simsearchdemo.entity.Test1;

import java.util.List;

public interface Test1Service {
    void add(Test1 test1);
    void update(Test1 test1);
    void delete(Test1 test1);
    List<Test1> fuzzyQuery(Test1 test1);
    List<Test1> fuzzyQuery(String xxx,Test1 test1);
}
