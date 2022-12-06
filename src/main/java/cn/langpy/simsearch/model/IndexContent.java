package cn.langpy.simsearch.model;


import java.util.List;

public class IndexContent {
    private Class entitySource;
    private String idName;
    private String idValue;
    private List<IndexItem> items;

    public Class getEntitySource() {
        return entitySource;
    }

    public void setEntitySource(Class entitySource) {
        this.entitySource = entitySource;
    }

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
