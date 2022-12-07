package cn.langpy.simsearchdemo.controller;

import cn.langpy.simsearchdemo.entity.Result;
import cn.langpy.simsearchdemo.entity.Test1;
import cn.langpy.simsearchdemo.service.Test1Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/test1")
public class Test1Controller {
    @Resource
    private Test1Service test1Service;
    /**
     * 为了方能改变测试  全部都是GET
     */
    @GetMapping("/add")
    public Result add(Test1 test1) {
        test1Service.add(test1);
        return Result.success();
    }
    @GetMapping("/update")
    public Result update(Test1 test1) {
        test1Service.update(test1);
        return Result.success();
    }
    @GetMapping("/delete")
    public Result delete(Test1 test1) {
        test1Service.delete(test1);
        return Result.success();
    }

    @GetMapping("/query")
    public Result query(Test1 test1) {
        List<Test1> searchList = test1Service.fuzzyQuery(test1);
        /*搜索结果的内容仅仅包含标有@IndexId和@IndexColumn注解的字段*/
        /*所以需要使用索引出来的id去数据库查询对应的内容*/
        return Result.success(searchList);
    }
}
