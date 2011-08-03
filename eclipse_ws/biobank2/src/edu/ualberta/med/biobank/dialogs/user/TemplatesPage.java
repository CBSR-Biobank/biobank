package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;

public class TemplatesPage extends BgcDialogPage {

    public TemplatesPage(BgcDialogWithPages dialog) {
        super(dialog);
    }

    @Override
    public String getTitle() {
        return "Groups Templates";
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        setControl(content);
    }

    @Override
    public void runAddAction() {
        // TODO Auto-generated method stub

    }

}
