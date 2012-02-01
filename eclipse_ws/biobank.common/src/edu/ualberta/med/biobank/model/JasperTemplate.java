package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "JASPER_TEMPLATE")
public class JasperTemplate extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String xml;

    // TODO: unique
    @NotEmpty
    @Column(name = "NAME", unique = true, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    @Column(name = "XML", columnDefinition="TEXT")
    public String getXml() {
        return this.xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
