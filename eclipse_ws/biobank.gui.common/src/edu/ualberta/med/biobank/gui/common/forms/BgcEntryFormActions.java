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

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class BgcEntryFormActions {

    private static ImageDescriptor printActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_PRINTER));

    private static ImageDescriptor resetActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESET_FORM));

    private static ImageDescriptor cancelActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_CANCEL_FORM));

    private static ImageDescriptor confirmActionImage = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_CONFIRM_FORM));

    private IBgcEntryForm entryForm;

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
        confirmAction.setToolTipText("Confirm");
        entryForm.getScrolledForm().getToolBarManager().add(confirmAction);
    }

    public void addResetAction(String commandId) {
        if (reset != null)
            return;

        reset = new CommandContributionItem(
            new CommandContributionItemParameter(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow(), "Reset", commandId, null,
                resetActionImage, null, null, "Reset", "Reset", "Reset",
                SWT.NONE, "Reset", true));
        entryForm.getScrolledForm().getToolBarManager().add(reset);
    }

    public void addCancelAction(String commandId) {
        if (cancel != null)
            return;

        cancel = new CommandContributionItem(
            new CommandContributionItemParameter(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow(), "Cancel", commandId, null,
                cancelActionImage, null, null, "Cancel", "Cancel", "Cancel",
                SWT.NONE, "Cancel", true));
        entryForm.getScrolledForm().getToolBarManager().add(cancel);
    }

    public void addPrintAction() {
        if (printAction != null)
            return;

        Action action = new Action("Print") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            entryForm.print();
                        } catch (Exception ex) {
                            BgcPlugin.openAsyncError("Error printing.", ex);
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
