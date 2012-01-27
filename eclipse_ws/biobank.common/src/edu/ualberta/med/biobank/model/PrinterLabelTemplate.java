package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PRINTER_LABEL_TEMPLATE")
public class PrinterLabelTemplate extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String printerName;
    private String configData;
    private JasperTemplate jasperTemplate;

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

    @Column(name = "CONFIG_DATA", columnDefinition="TEXT")
    public String getConfigData() {
        return this.configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JASPER_TEMPLATE_ID", nullable = false)
    public JasperTemplate getJasperTemplate() {
        return this.jasperTemplate;
    }

    public void setJasperTemplate(JasperTemplate jasperTemplate) {
        this.jasperTemplate = jasperTemplate;
    }
}
