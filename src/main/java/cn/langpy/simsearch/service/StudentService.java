package cn.langpy.simsearch.service;

import cn.langpy.simsearch.entity.Student;

import java.util.List;

public interface StudentService {
    boolean add(Student student);
    boolean del(Student student);
    List<Student> searchTest(String student);
    List<Student> searchaddress(String student);
}
