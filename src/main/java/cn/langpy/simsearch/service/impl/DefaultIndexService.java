package cn.langpy.simsearch.service.impl;

import cn.langpy.simsearch.config.SimSearchConfig;
import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import cn.langpy.simsearch.service.IndexService;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DefaultIndexService implements IndexService {

    private static String entityField = "entitySourceName";
    @Autowired
    IndexWriter indexWriter;
    @Autowired
    Directory directory;
    @Autowired
    SearcherManager searcherManager;
    @Autowired
    SimSearchConfig searchConfig;

    @Override
    public void batchCreateIndex(List<IndexContent> indexContents) {
        try {
            for (IndexContent indexContent : indexContents) {
                preDeleteIndex(indexContent.getEntitySource().getSimpleName(), indexContent.getIdName(), indexContent.getIdValue());
                preCreateIndex(indexContent);
            }
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

    private void preCreateIndex(IndexContent indexContent) throws IOException {
        try {
            Document doc = new Document();
            doc.add(new StringField(indexContent.getIdName(), indexContent.getIdValue(), Field.Store.YES));
            doc.add(new StringField(entityField, indexContent.getEntitySource().getSimpleName(), Field.Store.YES));
            for (IndexItem item : indexContent.getItems()) {
                doc.add(new TextField(item.getName(), item.getValue(), Field.Store.YES));
            }
            indexWriter.addDocument(doc);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void createIndex(IndexContent indexContent) {
        try {
            Document doc = new Document();
            doc.add(new StringField(indexContent.getIdName(), indexContent.getIdValue(), Field.Store.YES));
            doc.add(new StringField(entityField, indexContent.getEntitySource().getSimpleName(), Field.Store.YES));
            for (IndexItem item : indexContent.getItems()) {
                doc.add(new TextField(item.getName(), item.getValue(), Field.Store.YES));
            }
            preDeleteIndex(indexContent.getEntitySource().getSimpleName(), indexContent.getIdName(), indexContent.getIdValue());
            indexWriter.addDocument(doc);
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
    private void preDeleteIndex(String entityName, String idName, String idValue) throws IOException {
        try {
            indexWriter.deleteDocuments(buildStrictQuery(entityName, idName, idValue));
        } catch (IOException e) {
            throw e;
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
        return searchIndexs(entityName, name, value, searchConfig.getResultSize());
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
            int maxEdit = 2;
            if (value.length()<3) {
                maxEdit = 0;
            }
            query1 = new FuzzyQuery(new Term(name, value), maxEdit);
        } else {
            QueryParser queryParser = new QueryParser(name, new StandardAnalyzer());
            try {
                query1 = queryParser.parse(value);
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
        if (value == null || value.length() == 0 || name == null) {
            return Collections.emptyList();
        }
        value = value.trim();
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
            topDocs = indexSearcher.search(q, searchConfig.getResultSize());
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
        if (value == null || value.length() == 0 || name == null) {
            return Collections.emptyList();
        }
        value = value.trim();
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
