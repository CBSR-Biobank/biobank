package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientTableSimple
    extends DefaultAbstractInfoTableWidget<Patient> {
    public static final I18n i18n = I18nFactory.getI18n(PatientTableSimple.class);

    public static final int ROWS_PER_PAGE = 10;

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] { i18n.tr("Patient Number") };

    public PatientTableSimple(Composite parent, List<Patient> patients) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(patients);

        this.addClickListener(new IInfoTableDoubleClickItemListener<Patient>() {
            @Override
            public void doubleClick(InfoTableEvent<Patient> event) {
                SearchedPatientInfo spi = new SearchedPatientInfo();
                spi.patient = ((Patient) ((InfoTableSelection) event.getSelection()).getObject());
                AdapterBase.openForm(new FormInput(new PatientAdapter(null, spi)),
                    PatientViewForm.ID);
            }
        });
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Patient patient = (Patient) element;
                switch (columnIndex) {
                case 0:
                    return patient.getPnumber();
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }

    @Override
    protected Boolean canEdit(Patient target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(Patient target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canView(Patient target)
        throws ApplicationException {
        return true;
    }
}
