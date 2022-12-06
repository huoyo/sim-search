package cn.langpy.simsearch.model;



public class IndexItem {
    private Class entitySource;
    private String name;
    private String value;

    public Class getEntitySource() {
        return entitySource;
    }

    public void setEntitySource(Class entitySource) {
        this.entitySource = entitySource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
