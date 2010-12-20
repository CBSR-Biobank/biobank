package edu.ualberta.med.biobank.dialogs;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class RequestShippedDialog extends BiobankDialog {

    private static final String name = "Mark as Shipped";
    private RequestWrapper request;
    private BiobankText waybillField;
    private DateTimeWidget date;

    public RequestShippedDialog(Shell parentShell, RequestWrapper request) {
        super(parentShell);
        this.request = request;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Please add shipping information";
    }

    @Override
    protected String getTitleAreaTitle() {
        return name;
    }

    @Override
    protected String getDialogShellTitle() {
        return name;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        GridLayout gl = new GridLayout(2, false);
        parent.setLayout(gl);
        waybillField = (BiobankText) widgetCreator.createLabelledWidget(parent,
            BiobankText.class, SWT.NONE, "Waybill", null);
        widgetCreator.createLabel(parent, "Date Shipped");
        date = new DateTimeWidget(parent, SWT.DATE | SWT.TIME, new Date());
    }

    @Override
    protected void okPressed() {
        request.setWaybill(waybillField.getText());
        request.setShipped(date.getDate());
        super.okPressed();
    }
}
