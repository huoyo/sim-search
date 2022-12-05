package cn.langpy.simsearch.model;

import org.apache.lucene.document.Field;

import java.util.List;

public class IndexContent {
    private String idName;
    private String idValue;
    private List<IndexItem> items;

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public List<IndexItem> getItems() {
        return items;
    }

    public void setItems(List<IndexItem> items) {
        this.items = items;
    }
}
