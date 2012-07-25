package edu.ualberta.med.biobank.action.labelPrinter;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;

public class PrinterLabelTemplateDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer templateId;

    public PrinterLabelTemplateDeleteAction(
        PrinterLabelTemplate printerLabelTemplate) {
        if (printerLabelTemplate == null) {
            throw new IllegalArgumentException();
        }
        this.templateId = printerLabelTemplate.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        PrinterLabelTemplate printerLabelTemplate =
            context.load(PrinterLabelTemplate.class, templateId);
        context.getSession().delete(printerLabelTemplate);
        return new EmptyResult();
    }
}
