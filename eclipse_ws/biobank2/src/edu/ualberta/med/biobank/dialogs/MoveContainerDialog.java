package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.Container;

/**
 * Allows the user to move a container and its contents to a new location
 */

public class MoveContainerDialog extends BiobankDialog {

    private Container container;
    private Text text;
    private String txt;

    public MoveContainerDialog(Shell parent, Container container) {
        super(parent);
        Assert.isNotNull(container);
        this.container = container;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = "Moving Container: ";
        int id = container.getId();
        title += id;
        shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Control c = createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Source Address: ", new String[0], PojoObservables
                .observeValue(container, "label"), null, null);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;

        c.setLayoutData(gd);
        c.setEnabled(false);
        GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd2.widthHint = 250;

        Label label = new Label(contents, SWT.FILL);
        label.setText("Destination Address:");
        text = new Text(contents, SWT.FILL);
        text.setLayoutData(gd2);
        text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                txt = text.getText();
            }
        });

        return contents;
    }

    public Container getContainer() {
        return container;
    }

    public String getAddress() {
        return txt;
    }

}
