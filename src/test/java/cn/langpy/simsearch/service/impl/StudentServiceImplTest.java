package cn.langpy.simsearch.service.impl;

import cn.langpy.simsearch.entity.Student;
import cn.langpy.simsearch.service.StudentService;
import lombok.extern.java.Log;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
class StudentServiceImplTest {
    @Autowired
    StudentService studentService;
    @Test
    void add() throws IOException {
        List<String> lines = Files.lines(Paths.get("C:\\project\\analyze\\k12message\\name.mobile")).collect(Collectors.toList());
        lines = lines.subList(2000,5000);
        AtomicInteger i= new AtomicInteger();
        lines.forEach(line->{
            i.getAndIncrement();
            log.info(i.get()+"");
            String[] linesSplit = line.split(",");
            String name = linesSplit[0];
            String mobile = linesSplit[1].replace("\n","");
            String addre = linesSplit[2].replace("\n","");
            Student student = new Student();
            student.setStudentId(new Random().nextLong()+"");
            student.setStudentName(name);
            student.setStudentNum(mobile);
            student.setAddress(addre);
            studentService.add(student);

        });
        System.out.println(0000);
    }
}