package edu.ualberta.med.biobank.action.labelPrinter;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.JasperTemplate;

public class JasperTemplateDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer templateId;

    public JasperTemplateDeleteAction(JasperTemplate jasperTemplate) {
        if (jasperTemplate == null) {
            throw new IllegalArgumentException();
        }
        this.templateId = jasperTemplate.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        JasperTemplate jasperTemplate =
            context.load(JasperTemplate.class, templateId);
        context.getSession().delete(jasperTemplate);
        return new EmptyResult();
    }
}
