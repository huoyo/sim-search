package com.huoyo.luceneannotation.service.impl;

import com.huoyo.luceneannotation.annotation.CreateIndex;
import com.huoyo.luceneannotation.annotation.SearchIndex;
import com.huoyo.luceneannotation.entity.Student;
import com.huoyo.luceneannotation.service.StudentService;
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
public class StudentServiceImpl implements StudentService {
    @Autowired
    IndexSearcher indexSearcher;
    @Autowired
    IndexReader indexReader;
    @Autowired
    SearcherManager searcherManager;

    @Override
    public List<Student> search(String studentName) {
        try {
            searcherManager.maybeRefresh();
            indexSearcher = searcherManager.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryParser parser = new QueryParser("studentName", new StandardAnalyzer());
        Query query = null;
        TopDocs topDocs = null;
        try {
            query = parser.parse(studentName);
            topDocs = indexSearcher.search(query, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        // 获取总条数
        System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");
        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc = null;
            try {
                doc = indexSearcher.doc(docID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("studentId: " + doc.get("studentId"));
            System.out.println("studentName: " + doc.get("studentName"));
            // 取出文档得分
//            System.out.println("得分： " + scoreDoc.score);
        }

        return null;
    }

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
