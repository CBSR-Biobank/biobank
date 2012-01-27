package edu.ualberta.med.biobank.model;

public class PrinterLabelTemplate extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String printerName;
    private String configData;
    private JasperTemplate jasperTemplate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public JasperTemplate getJasperTemplate() {
        return jasperTemplate;
    }

    public void setJasperTemplate(JasperTemplate jasperTemplate) {
        this.jasperTemplate = jasperTemplate;
    }
}
