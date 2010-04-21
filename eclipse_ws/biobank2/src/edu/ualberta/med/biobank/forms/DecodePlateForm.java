package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.grids.ScanLinkPalletWidget;

public class DecodePlateForm extends BiobankViewForm {

    private ScanLinkPalletWidget spw;

    @Override
    protected void init() throws Exception {
        //setPartName(Messages.getFormattedString("ScanLink.tabTitle", plateId)); //$NON-NLS-1$
        setPartName(Messages.getString("ScanLink.tabTitle")); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);

        spw = new ScanLinkPalletWidget(client);
        spw.setVisible(true);
        toolkit.adapt(spw);
        spw.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
    }

    @Override
    protected void reload() throws Exception {
    }

}
