package com.huoyo.luceneannotation.service.impl;

import com.huoyo.luceneannotation.annotation.CreateIndex;
import com.huoyo.luceneannotation.annotation.SearchIndex;
import com.huoyo.luceneannotation.entity.Student;
import com.huoyo.luceneannotation.service.StudentService;
import lombok.extern.java.Log;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
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

    @SearchIndex(by = "studentName",searchEntity=Student.class)
    public List<Student> searchTest(String studentName) {
        return null;
    }
}
