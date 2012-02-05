package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreInsert;
import edu.ualberta.med.biobank.validator.group.PreUpdate;

@Entity
@Table(name = "PRINTER_LABEL_TEMPLATE")
@Unique(properties = { "name" },
    groups = { PreInsert.class, PreUpdate.class },
    message = "{edu.ualberta.med.biobank.model.PrinterLabelTemplate.name.Unique}")
public class PrinterLabelTemplate extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String printerName;
    private String configData;
    private JasperTemplate jasperTemplate;

    @NotEmpty
    @Column(name = "NAME", unique = true, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PRINTER_NAME", length = 255)
    public String getPrinterName() {
        return this.printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    @Column(name = "CONFIG_DATA", columnDefinition = "TEXT")
    public String getConfigData() {
        return this.configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JASPER_TEMPLATE_ID", nullable = false)
    public JasperTemplate getJasperTemplate() {
        return this.jasperTemplate;
    }

    public void setJasperTemplate(JasperTemplate jasperTemplate) {
        this.jasperTemplate = jasperTemplate;
    }
}
