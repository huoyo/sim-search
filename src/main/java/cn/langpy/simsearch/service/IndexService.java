package cn.langpy.simsearch.service;


import cn.langpy.simsearch.model.IndexContent;
import org.apache.lucene.document.Document;

import java.util.List;


public interface IndexService {
    /**
     * create index by idName and idValue (it will delete existed index,so you can think of this method as insertOrUpdate())
     */
    void createIndex(IndexContent indexContent);
    /**
     * delete index  by idName and idValue
     */
    void deleteIndex(String entityName,String idName, String idValue);

    /**
     * delete all indexs
     */
    void deleteAll();

    /**
     * search Documents by field name and its value
     */
    List<Document> searchIndexs(String entityName,String name, String value, int topn);
    List<Document> searchIndexs(String name, String value);
    List<Document> searchIndexs(String entityName,String name, String value);

}
