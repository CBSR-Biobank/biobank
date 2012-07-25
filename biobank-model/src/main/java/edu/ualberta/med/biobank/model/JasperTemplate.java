package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "JASPER_TEMPLATE")
@Unique(properties = "name", groups = PrePersist.class)
public class JasperTemplate extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String xml;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.JasperTemplate.name.NotEmpty}")
    @Column(name = "NAME", unique = true, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.JasperTemplate.xml.NotEmpty}")
    @Column(name = "XML", columnDefinition = "TEXT")
    public String getXml() {
        return this.xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
