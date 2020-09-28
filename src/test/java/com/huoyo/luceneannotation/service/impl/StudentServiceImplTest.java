package com.huoyo.luceneannotation.service.impl;

import com.huoyo.luceneannotation.entity.Student;
import com.huoyo.luceneannotation.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest
class StudentServiceImplTest {
    @Autowired
    StudentService studentService;
    @Test
    void add() throws IOException {
        List<String> lines = Files.lines(Paths.get("C:\\project\\analyze\\k12message\\name.mobile")).collect(Collectors.toList());
        lines.forEach(line->{
            String[] linesSplit = line.split(",");
            String name = linesSplit[0];
            String mobile = linesSplit[1].replace("\n","");
            Student student = new Student();
            student.setStudentId(new Random().nextLong()+"");
            student.setStudentName(name);
            student.setStudentNum(mobile);
            studentService.add(student);

        });
    }
}