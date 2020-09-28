package com.huoyo.luceneannotation.service;

import com.huoyo.luceneannotation.entity.Student;
import org.apache.lucene.document.Document;

import java.util.List;

public interface StudentService {
    List<Student> search(String studentName);
    boolean add(Student student);
    List<Student> searchTest(String student);
}
