package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;

public class ContainerReport1Editor extends ReportsEditor {
    private static final I18n i18n = I18nFactory
        .getI18n(ContainerReport1Editor.class);

    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.ContainerCapacityEditor";

    @Override
    protected void createOptionSection(Composite parent) {
        //
    }

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            Container.NAME.format(1).toString(),
            Capacity.NAME.format(1).toString(),
            // table column name
            i18n.tr("# In Use"),
            // table column name
            i18n.tr("% In Use") };
    }

    @Override
    protected List<String> getParamNames() {
        return new ArrayList<String>();
    }

    @Override
    protected void initReport() throws Exception {
        //
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        return new ArrayList<Object>();
    }

    @Override
    public void setValues() throws Exception {
    }

}
