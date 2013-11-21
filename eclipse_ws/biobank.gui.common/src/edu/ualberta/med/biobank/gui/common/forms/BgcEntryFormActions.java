package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class BgcEntryFormActions {
    private static final I18n i18n = I18nFactory
        .getI18n(BgcEntryFormActions.class);

    @SuppressWarnings("nls")
    private static final String CONFIRM = i18n.tr("Confirm");
    @SuppressWarnings("nls")
    private static final String RESET = i18n.tr("Reset");
    @SuppressWarnings("nls")
    private static final String CANCEL = i18n.tr("Cancel");
    @SuppressWarnings("nls")
    private static final String ERROR_PRINTING = i18n.tr("Error printing.");
    @SuppressWarnings("nls")
    // text that appears as an option in a GUI menu to print
    private static final String PRINT_ACTION_TEXT = i18n.tr("Print");

    private static ImageDescriptor printActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.PRINTER));

    private static ImageDescriptor reloadActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.RELOAD_FORM));

    private static ImageDescriptor cancelActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.CANCEL_FORM));

    private static ImageDescriptor confirmActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.CONFIRM_FORM));

    private final IBgcEntryForm entryForm;

    private Action confirmAction;

    private CommandContributionItem reset;

    private CommandContributionItem cancel;

    private ActionContributionItem printAction;

    public BgcEntryFormActions(IBgcEntryForm form) {
        this.entryForm = form;
        this.confirmAction = null;
        this.reset = null;
        this.cancel = null;
        this.printAction = null;
    }

    public Action getConfirmAction() {
        return confirmAction;
    }

    public void addConfirmAction(String commandId) {
        if (confirmAction != null)
            return;

        confirmAction = new Action() {
            @Override
            public void run() {
                entryForm.confirm();
            }
        };
        confirmAction.setActionDefinitionId(commandId);
        confirmAction.setImageDescriptor(confirmActionImage);
        confirmAction.setToolTipText(CONFIRM);
        entryForm.getScrolledForm().getToolBarManager().add(confirmAction);
    }

    public void addResetAction(String commandId) {
        if (reset != null)
            return;

        reset =
            new CommandContributionItem(
                new CommandContributionItemParameter(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow(),
                    RESET, commandId, null,
                    reloadActionImage, null, null,
                    RESET,
                    RESET,
                    RESET,
                    SWT.NONE, RESET, true));
        entryForm.getScrolledForm().getToolBarManager().add(reset);
    }

    public void addCancelAction(String commandId) {
        if (cancel != null)
            return;

        cancel =
            new CommandContributionItem(
                new CommandContributionItemParameter(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow(),
                    CANCEL, commandId, null,
                    cancelActionImage, null, null,
                    CANCEL,
                    CANCEL,
                    CANCEL,
                    SWT.NONE, CANCEL, true));
        entryForm.getScrolledForm().getToolBarManager().add(cancel);
    }

    public void addPrintAction() {
        if (printAction != null)
            return;

        Action action = new Action(PRINT_ACTION_TEXT) {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            entryForm.print();
                        } catch (Exception ex) {
                            BgcPlugin.openAsyncError(
                                ERROR_PRINTING,
                                ex);
                        }
                    }
                });
            }
        };

        action.setImageDescriptor(printActionImage);
        printAction = new ActionContributionItem(action);
        entryForm.getScrolledForm().getToolBarManager().add(printAction);
    }

    public void setEnablePrintAction(boolean enabled) {
        if (printAction == null)
            return;
        printAction.getAction().setEnabled(enabled);
    }
}
