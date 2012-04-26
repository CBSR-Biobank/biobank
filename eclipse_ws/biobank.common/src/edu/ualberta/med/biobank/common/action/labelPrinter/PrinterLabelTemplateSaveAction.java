package edu.ualberta.med.biobank.common.action.labelPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.JasperTemplate;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;

public class PrinterLabelTemplateSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(PrinterLabelTemplateSaveAction.class.getName());

    private Integer id = null;
    private final String name;
    private final String printerName;
    private final String configData;
    private final JasperTemplate jasperTemplate;

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
        log.debug("run: id={} name={}", id, name);

        PrinterLabelTemplate printerLabelTemplate =
            context.get(PrinterLabelTemplate.class, id,
                new PrinterLabelTemplate());

        printerLabelTemplate.setId(id);
        printerLabelTemplate.setName(name);
        printerLabelTemplate.setPrinterName(printerName);
        printerLabelTemplate.setConfigData(configData);
        printerLabelTemplate.setJasperTemplate(jasperTemplate);

        context.getSession().saveOrUpdate(printerLabelTemplate);
        return new IdResult(printerLabelTemplate.getId());
    }

}
