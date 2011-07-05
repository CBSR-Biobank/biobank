package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.jface.action.Action;
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

    public BgcEntryFormActions(IBgcEntryForm form) {
        this.entryForm = form;
    }

    public Action getConfirmAction() {
        return confirmAction;
    }

    public void addConfirmAction(String commandId) {
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
        CommandContributionItem reset = new CommandContributionItem(
            new CommandContributionItemParameter(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow(), "Reset", commandId, null,
                resetActionImage, null, null, "Reset", "Reset", "Reset",
                SWT.NONE, "Reset", true));
        entryForm.getScrolledForm().getToolBarManager().add(reset);
    }

    public void addCancelAction(String commandId) {
        CommandContributionItem cancel = new CommandContributionItem(
            new CommandContributionItemParameter(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow(), "Cancel", commandId, null,
                cancelActionImage, null, null, "Cancel", "Cancel", "Cancel",
                SWT.NONE, "Cancel", true));
        entryForm.getScrolledForm().getToolBarManager().add(cancel);
    }

    public void addPrintAction() {
        Action print = new Action("Print") {
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
        print.setImageDescriptor(printActionImage);
        entryForm.getScrolledForm().getToolBarManager().add(print);
    }

}
