package cn.langpy.simsearchdemo.controller;

import cn.langpy.simsearch.util.IndexManager;
import cn.langpy.simsearchdemo.entity.Result;
import cn.langpy.simsearchdemo.entity.Test1;
import cn.langpy.simsearchdemo.entity.Test2;
import cn.langpy.simsearchdemo.service.Test1Service;
import cn.langpy.simsearchdemo.service.Test2Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/test2")
public class Test2Controller {
    @Resource
    private Test2Service test2Service;
    /**
     * 为了方能改变测试  全部都是GET
     */
    @GetMapping("/add")
    public Result add(Test2 test2) {
        test2Service.add(test2);
        return Result.success();
    }
    @GetMapping("/update")
    public Result update(Test2 test2) {
        test2Service.update(test2);
        return Result.success();
    }
    @GetMapping("/delete")
    public Result delete(Test2 test2) {
        test2Service.delete(test2);
//        IndexManager.deleteIndex("id",test2.getId()+"",Test2.class);
        return Result.success();
    }

    @GetMapping("/query")
    public Result query(Test2 test2) {
        /*也可以使用内置的工具类进行搜索*/
        /*搜索Test2的name字段 返回该对象的id*/
        List<Long> searchList = IndexManager.searchIndexIds("name",test2.getName(),Test2.class);
//        List<Test2> searchList = IndexManager.searchIndexObjects("name",test2.getName(),Test2.class);
        return Result.success(searchList);
    }
}
