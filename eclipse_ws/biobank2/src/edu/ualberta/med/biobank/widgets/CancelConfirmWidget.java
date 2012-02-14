package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.forms.linkassign.AbstractSpecimenAdminForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;

public class CancelConfirmWidget extends BgcBaseWidget {

    private static BgcLogger logger = BgcLogger
        .getLogger(CancelConfirmWidget.class.getName());

    private BgcBaseText confirmCancelText;

    private Button confirmButton;

    private Button cancelButton;

    private AbstractSpecimenAdminForm form;

    public CancelConfirmWidget(Composite parent, AbstractSpecimenAdminForm form) {
        this(parent, form, false);
    }

    public CancelConfirmWidget(Composite parent,
        AbstractSpecimenAdminForm form, boolean showTextField) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout(4, false));
        this.form = form;

        createContents();

        showTextField(showTextField);
    }

    private void createContents() {
        form.getToolkit().createLabel(this,
            Messages.CancelConfirmWidget_cancelconfirm_label);
        confirmCancelText = new BgcBaseText(this, SWT.NONE, form.getToolkit());
        confirmCancelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData gd = new GridData();
        gd.widthHint = 100;
        confirmCancelText.setLayoutData(gd);

        confirmCancelText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                String text = confirmCancelText.getText();
                if (BiobankPlugin.getDefault().isConfirmBarcode(text)
                    && confirmButton.isEnabled()) {
                    form.confirm();
                    // form will be closed. Default following behaviours
                    // will send a disposed error if we don't set this to
                    // false
                    e.doit = false;
                } else if (BiobankPlugin.getDefault().isCancelBarcode(text)) {
                    try {
                        form.setValues();
                        form.setAfterKeyCancel();
                    } catch (Exception ex) {
                        logger.error("Error while reseting pallet values", ex); //$NON-NLS-1$
                    }
                }
            }
        });

        cancelButton = form.getToolkit().createButton(this,
            Messages.CancelConfirmWidget_cancel_label, SWT.PUSH);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    form.setValues();
                } catch (Exception ex) {
                    logger.error("Error while reseting pallet values", ex); //$NON-NLS-1$
                }
            }
        });

        confirmButton = form.getToolkit().createButton(this,
            Messages.CancelConfirmWidget_confirm_label, SWT.PUSH);
        confirmButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                form.confirm();
            }
        });

        adaptToToolkit(form.getToolkit(), true);
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

    public void reset() {
        confirmCancelText.setText(""); //$NON-NLS-1$
    }

    @Override
    public boolean setFocus() {
        return confirmCancelText.setFocus();
    }

}
