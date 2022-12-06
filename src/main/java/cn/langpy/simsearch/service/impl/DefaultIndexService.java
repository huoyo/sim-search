package cn.langpy.simsearch.service.impl;

import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import cn.langpy.simsearch.service.IndexService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultIndexService implements IndexService {

    private static String entityField = "entitySourceName";
    @Autowired
    IndexWriter indexWriter;
    @Autowired
    SearcherManager searcherManager;

    @Override
    public synchronized void createIndex(IndexContent indexContent) {
        try {
            Document doc = new Document();
            doc.add(new StringField(indexContent.getIdName(), indexContent.getIdValue(), Field.Store.YES));
            doc.add(new StringField(entityField, indexContent.getEntitySource().getSimpleName(), Field.Store.YES));
            for (IndexItem item : indexContent.getItems()) {
                doc.add(new TextField(item.getName(), item.getValue(), Field.Store.YES));
            }
            deleteIndex(indexContent.getEntitySource().getSimpleName(), indexContent.getIdName(), indexContent.getIdValue());
            indexWriter.addDocument(doc);
            indexWriter.flush();
            indexWriter.commit();
        } catch (IOException e) {
            try {
                indexWriter.rollback();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteIndex(String entityName, String idName, String idValue) {
        try {
            indexWriter.deleteDocuments(buildStrictQuery(entityName, idName, idValue));
            indexWriter.commit();
        } catch (IOException e) {
            try {
                indexWriter.rollback();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        try {
            indexWriter.deleteAll();
            indexWriter.commit();
        } catch (IOException e) {
            try {
                indexWriter.rollback();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<Document> searchIndexs(String entityName, String name, String value) {
        return searchIndexs(entityName, name, value, 50);
    }

    public BooleanQuery buildStrictQuery(String entityName, String name, String value) {
        Query query1 = new TermQuery(new Term(name, value));
        Query query2 = new TermQuery(new Term(entityField, entityName));
        BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        boolQuery.add(query1, BooleanClause.Occur.MUST);
        boolQuery.add(query2, BooleanClause.Occur.MUST);
        return boolQuery.build();
    }


    public Query buildFuzzyQuery(String entityName, String name, String value) {
        Query query1 = null;

        if (value.matches("^[a-zA-Z0-9]+$")) {
             query1=new FuzzyQuery(new Term(name,value.trim()),4);
        }else {
            QueryParser queryParser = new QueryParser(name,new StandardAnalyzer());
            try {
                query1 = queryParser.parse(value.trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Query query2 = new TermQuery(new Term(entityField, entityName));
        BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        boolQuery.add(query1, BooleanClause.Occur.MUST);
        boolQuery.add(query2, BooleanClause.Occur.MUST);
        return boolQuery.build();
    }

    @Override
    public List<Document> searchIndexs(String name, String value) {
        List<Document> documents = new ArrayList<>();
        IndexSearcher indexSearcher = null;
        try {
            searcherManager.maybeRefresh();
            indexSearcher = searcherManager.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TopDocs topDocs = null;
        QueryParser qp = new QueryParser(name, new StandardAnalyzer());
        try {
            Query q = qp.parse(value);
            topDocs = indexSearcher.search(q, 100);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc;
            try {
                doc = indexSearcher.doc(docID);
                documents.add(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return documents;
    }

    @Override
    public List<Document> searchIndexs(String entityName, String name, String value, int topn) {
        List<Document> documents = new ArrayList<>();
        IndexSearcher indexSearcher = null;
        try {
            searcherManager.maybeRefresh();
            indexSearcher = searcherManager.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TopDocs topDocs = null;
        try {
            topDocs = indexSearcher.search(buildFuzzyQuery(entityName, name, value), topn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc;
            try {
                doc = indexSearcher.doc(docID);
                documents.add(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return documents;
    }
}
