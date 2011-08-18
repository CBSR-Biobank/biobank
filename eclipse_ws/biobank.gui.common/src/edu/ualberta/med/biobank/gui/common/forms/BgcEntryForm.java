package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import edu.ualberta.med.biobank.gui.common.BgcLogger;

public class BgcEntryForm extends BgcFormBase implements IBgcEntryForm {

    private static BgcLogger logger = BgcLogger.getLogger(BgcEntryForm.class
        .getName());

    private boolean dirty = false;

    protected BgcEntryFormActions formActions;

    protected KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
                setDirty(true);
            }
        }
    };

    @Override
    protected void init() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void performDoubleClick(DoubleClickEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Image getFormImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void createFormContent() throws Exception {
        addToolbarButtons();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean d) {
        dirty = d;
        firePropertyChange(ISaveablePart.PROP_DIRTY);
    }

    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addResetAction();
        addCancelAction();
        addConfirmAction();
        form.updateToolBar();
    }

    protected void addConfirmAction() {
        formActions.addConfirmAction(Actions.GUI_COMMON_CONFIRM);
    }

    protected void addResetAction() {
        formActions.addResetAction(Actions.GUI_COMMON_RESET);
    }

    protected void addCancelAction() {
        formActions.addCancelAction(Actions.GUI_COMMON_CANCEL);
    }

    protected void addPrintAction() {
        formActions.addPrintAction();
    }

    @Override
    public ScrolledForm getScrolledForm() {
        return form;
    }

    @Override
    public void confirm() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().saveEditor(this, false);
        } catch (Exception e) {
            logger.error("Can't save the form", e); //$NON-NLS-1$
        }
    }

    @Override
    public void cancel() {
        // override me
    }

    @Override
    public void reset() {
        setDirty(false);
    }

    @Override
    public boolean print() {
        // override me
        return false;
    }

}
