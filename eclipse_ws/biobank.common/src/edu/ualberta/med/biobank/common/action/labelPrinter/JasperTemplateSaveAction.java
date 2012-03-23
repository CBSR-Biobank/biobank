package edu.ualberta.med.biobank.common.action.labelPrinter;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.JasperTemplate;

public class JasperTemplateSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private String name;
    private String xml;

    public JasperTemplateSaveAction(JasperTemplate jTemplate) {
        this.id = jTemplate.getId();
        this.name = jTemplate.getName();
        this.xml = jTemplate.getXml();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        JasperTemplate jTemplate =
            context.get(JasperTemplate.class, id, new JasperTemplate());

        jTemplate.setId(id);
        jTemplate.setName(name);
        jTemplate.setXml(xml);

        context.getSession().saveOrUpdate(jTemplate);
        context.getSession().flush();

        return new IdResult(jTemplate.getId());
    }

}
