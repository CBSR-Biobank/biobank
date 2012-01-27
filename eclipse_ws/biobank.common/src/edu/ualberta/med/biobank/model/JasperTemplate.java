package edu.ualberta.med.biobank.model;

public class JasperTemplate extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String xml;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
