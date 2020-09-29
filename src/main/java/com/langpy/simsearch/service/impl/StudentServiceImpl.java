package com.langpy.simsearch.service.impl;

import com.langpy.simsearch.annotation.CreateIndex;
import com.langpy.simsearch.annotation.DeleteIndex;
import com.langpy.simsearch.annotation.SearchIndex;
import com.langpy.simsearch.entity.Student;
import com.langpy.simsearch.service.StudentService;
import lombok.extern.java.Log;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @name：
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 11:18
 */
@Service
@Log
public class StudentServiceImpl implements StudentService {
    @Autowired
    IndexSearcher indexSearcher;
    @Autowired
    IndexReader indexReader;
    @Autowired
    SearcherManager searcherManager;

    @Override
    @CreateIndex(indexParam = "student")
    public boolean add(Student student) {
        return false;
    }
    @Override
    @DeleteIndex(indexParam = "student")
    public boolean del(Student student) {
        return false;
    }

    @SearchIndex(by = "studentName",searchEntity=Student.class)
    public List<Student> searchTest(String studentName) {
        return null;
    }
    @SearchIndex(by = "address",searchEntity=Student.class)
    public List<Student> searchaddress(String address) {
        return null;
    }
}
