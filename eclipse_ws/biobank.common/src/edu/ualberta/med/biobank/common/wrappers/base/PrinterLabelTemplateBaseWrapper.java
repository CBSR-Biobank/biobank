/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.PrinterLabelTemplatePeer;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.JasperTemplateBaseWrapper;

public class PrinterLabelTemplateBaseWrapper extends ModelWrapper<PrinterLabelTemplate> {

    public PrinterLabelTemplateBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PrinterLabelTemplateBaseWrapper(WritableApplicationService appService,
        PrinterLabelTemplate wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<PrinterLabelTemplate> getWrappedClass() {
        return PrinterLabelTemplate.class;
    }

    @Override
    public Property<Integer, ? super PrinterLabelTemplate> getIdProperty() {
        return PrinterLabelTemplatePeer.ID;
    }

    @Override
    protected List<Property<?, ? super PrinterLabelTemplate>> getProperties() {
        return PrinterLabelTemplatePeer.PROPERTIES;
    }

    public String getConfigData() {
        return getProperty(PrinterLabelTemplatePeer.CONFIG_DATA);
    }

    public void setConfigData(String configData) {
        String trimmed = configData == null ? null : configData.trim();
        setProperty(PrinterLabelTemplatePeer.CONFIG_DATA, trimmed);
    }

    public String getPrinterName() {
        return getProperty(PrinterLabelTemplatePeer.PRINTER_NAME);
    }

    public void setPrinterName(String printerName) {
        String trimmed = printerName == null ? null : printerName.trim();
        setProperty(PrinterLabelTemplatePeer.PRINTER_NAME, trimmed);
    }

    public String getName() {
        return getProperty(PrinterLabelTemplatePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(PrinterLabelTemplatePeer.NAME, trimmed);
    }

    public JasperTemplateWrapper getJasperTemplate() {
        JasperTemplateWrapper jasperTemplate = getWrappedProperty(PrinterLabelTemplatePeer.JASPER_TEMPLATE, JasperTemplateWrapper.class);
        return jasperTemplate;
    }

    public void setJasperTemplate(JasperTemplateBaseWrapper jasperTemplate) {
        setWrappedProperty(PrinterLabelTemplatePeer.JASPER_TEMPLATE, jasperTemplate);
    }

    void setJasperTemplateInternal(JasperTemplateBaseWrapper jasperTemplate) {
        setWrappedProperty(PrinterLabelTemplatePeer.JASPER_TEMPLATE, jasperTemplate);
    }

}
