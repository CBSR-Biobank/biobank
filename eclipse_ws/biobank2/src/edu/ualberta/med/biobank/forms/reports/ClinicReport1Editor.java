package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;

public class ClinicReport1Editor extends ReportsEditor {
    private static final I18n i18n = I18nFactory
        .getI18n(ClinicReport1Editor.class);

    @Override
    protected void createOptionSection(Composite parent) {
        //
    }

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            Study.NAME.format(1).toString(),
            Center.NAME.format(1).toString(),
            // table column name
            i18n.tr("First Specimen Time Drawn"),
            // table column name
            i18n.tr("Last Specimen Time Drawn") };
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
    protected List<Object> getPrintParams() {
        return new ArrayList<Object>();
    }

}
