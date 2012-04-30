package edu.ualberta.med.biobank.common.action.labelPrinter;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.labelPrinter.JasperTemplateGetInfoAction.JasperTemplateInfo;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.JasperTemplate;

public class JasperTemplateGetInfoAction implements
    Action<JasperTemplateInfo> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(JasperTemplateGetInfoAction.class.getName());

    @SuppressWarnings("nls")
    private static final String HQL_QRY =
        "FROM " + JasperTemplate.class.getName() + " WHERE name=?";

    public static class JasperTemplateInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public JasperTemplate jasperTemplate;
    }

    public final String name;

    public JasperTemplateGetInfoAction(String name) {
        this.name = name;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @Override
    public JasperTemplateInfo run(ActionContext context)
        throws ActionException {
        log.debug("run: name={}", name);

        Query query = context.getSession().createQuery(HQL_QRY);
        query.setParameter(0, name);

        JasperTemplateInfo info = new JasperTemplateInfo();
        info.jasperTemplate = (JasperTemplate) query.uniqueResult();
        if (info.jasperTemplate == null) {
            throw new IllegalStateException("printer label template is null");
        }

        return info;
    }
}
