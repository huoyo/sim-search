package cn.langpy.simsearch.service.impl;

import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import cn.langpy.simsearch.service.IndexService;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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
    @Autowired
    IndexWriter indexWriter;
    @Autowired
    SearcherManager searcherManager;

    @Override
    public void createIndex(IndexContent indexContent) {
        try {
            Document doc = new Document();
            doc.add(new StringField(indexContent.getIdName(), indexContent.getIdValue(), Field.Store.YES));
            for (IndexItem item : indexContent.getItems()) {
                doc.add(new TextField(item.getName(), item.getValue(), Field.Store.YES));
            }
            indexWriter.addDocument(doc);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteIndex(String idName, String idValue) {
        try {
            indexWriter.deleteDocuments(new Term(idName, idValue));
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        try {
            indexWriter.deleteAll();
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Document> searchIndex(String name, String value) {
        return searchIndex(name, value, 10);
    }

    @Override
    public List<Document> searchIndex(String name, String value, int topn) {
        List<Document> documents = new ArrayList<>();
        IndexSearcher indexSearcher = null;
        try {
            searcherManager.maybeRefresh();
            indexSearcher = searcherManager.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryParser parser = new QueryParser(name, new StandardAnalyzer());
        Query query;
        TopDocs topDocs = null;
        try {
            query = parser.parse(value);
            topDocs = indexSearcher.search(query, topn);
        } catch (ParseException e) {
            e.printStackTrace();
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
