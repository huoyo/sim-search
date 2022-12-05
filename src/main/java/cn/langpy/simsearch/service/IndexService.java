package cn.langpy.simsearch.service;


import cn.langpy.simsearch.model.IndexContent;
import org.apache.lucene.document.Document;

import java.util.List;


public interface IndexService {
    /**
     * create index by idName and idValue (it will delete existed index)
     */
    void createIndex(IndexContent indexContent);
    /**
     * delete index  by idName and idValue
     */
    void deleteIndex(String idName, String idValue);

    List<Document> searchIndex(String name, String value, int topn);

    List<Document> searchIndex(String name, String value);

}
