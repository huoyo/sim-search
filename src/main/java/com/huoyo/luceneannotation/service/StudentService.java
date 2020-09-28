package com.huoyo.luceneannotation.service;

import com.huoyo.luceneannotation.entity.Student;
import org.apache.lucene.document.Document;

import java.util.List;

public interface StudentService {
    boolean add(Student student);
    boolean del(Student student);
    List<Student> searchTest(String student);
}
