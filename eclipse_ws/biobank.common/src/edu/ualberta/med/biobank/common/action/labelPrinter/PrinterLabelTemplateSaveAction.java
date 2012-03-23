package edu.ualberta.med.biobank.common.action.labelPrinter;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.JasperTemplate;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;

public class PrinterLabelTemplateSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private String name;
    private String printerName;
    private String configData;
    private JasperTemplate jasperTemplate;

    public PrinterLabelTemplateSaveAction(
        PrinterLabelTemplate printerLabelTemplate) {

        this.id = printerLabelTemplate.getId();
        this.name = printerLabelTemplate.getName();
        this.printerName = printerLabelTemplate.getPrinterName();
        this.configData = printerLabelTemplate.getConfigData();
        this.jasperTemplate = printerLabelTemplate.getJasperTemplate();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        PrinterLabelTemplate printerLabelTemplate =
            context.get(PrinterLabelTemplate.class, id,
                new PrinterLabelTemplate());

        printerLabelTemplate.setId(id);
        printerLabelTemplate.setName(name);
        printerLabelTemplate.setPrinterName(printerName);
        printerLabelTemplate.setConfigData(configData);
        printerLabelTemplate.setJasperTemplate(jasperTemplate);

        context.getSession().saveOrUpdate(printerLabelTemplate);
        context.getSession().flush();

        return new IdResult(printerLabelTemplate.getId());
    }

}
