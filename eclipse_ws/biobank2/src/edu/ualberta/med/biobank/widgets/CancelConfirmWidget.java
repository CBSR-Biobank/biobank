package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.BiobankEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CancelConfirmWidget extends BiobankWidget {

    private Text confirmCancelText;

    private Button confirmButton;

    private Button cancelButton;

    private BiobankEntryForm form;

    public CancelConfirmWidget(Composite parent, BiobankEntryForm form) {
        this(parent, form, false);
    }

    public CancelConfirmWidget(Composite parent, BiobankEntryForm form,
        boolean showTextField) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout(3, false));
        this.form = form;

        createContents();

        showTextField(showTextField);
    }

    private void createContents() {
        confirmCancelText = form.getToolkit().createText(this, "");
        confirmCancelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData gd = new GridData();
        gd.widthHint = 100;
        confirmCancelText.setLayoutData(gd);
        confirmCancelText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 13) {
                    String text = ((Text) e.widget).getText();
                    try {
                        if (BioBankPlugin.getDefault().isConfirmBarcode(text)
                            && confirmButton.isEnabled()) {
                            confirm();
                        } else if (BioBankPlugin.getDefault().isCancelBarcode(
                            text)) {
                            cancel();
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        confirmCancelText.setVisible(false);

        cancelButton = form.getToolkit().createButton(this, "Cancel", SWT.PUSH);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancel();
            }
        });

        confirmButton = form.getToolkit().createButton(this, "Confirm",
            SWT.PUSH);
        confirmButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                confirm();
            }
        });
        adaptToToolkit(form.getToolkit(), true);
    }

    private void confirm() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .saveEditor(form, false);
        if (!form.isDirty()) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().closeEditor(form, false);
            if (form.getNextOpenedFormID() != null) {
                AdapterBase.openForm(new FormInput(form.getAdapter()), form
                    .getNextOpenedFormID());
            }
        }
    }

    private void cancel() {
        form.cancelForm();
    }

    public void showTextField(boolean show) {
        ((GridData) confirmCancelText.getLayoutData()).exclude = !show;
        confirmCancelText.setVisible(show);
    }

    public void setConfirmEnabled(boolean enabled) {
        confirmButton.setEnabled(enabled);
    }

    public void setCancelEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    public void setTextEnabled(boolean enabled) {
        confirmCancelText.setEnabled(enabled);
    }

}
