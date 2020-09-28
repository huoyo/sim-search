package com.huoyo.luceneannotation.controller;

import com.huoyo.luceneannotation.entity.Student;
import com.huoyo.luceneannotation.service.StudentService;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @name：
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 11:15
 */
@RestController
@RequestMapping("/student")
public class IndexController {
    @Autowired
    StudentService studentService;

    @RequestMapping("/add")
    public String function(@RequestBody Student student) {
        studentService.add(student);
        return "成功";
    }
    @RequestMapping("/del")
    public String del(@RequestBody Student student) {
        studentService.del(student);
        return "成功";
    }

    @RequestMapping("/search")
    public List<Student> function(String studentName) {
        List<Student> docs = studentService.searchTest(studentName);
        return docs;
    }
}
