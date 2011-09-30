package edu.ualberta.med.biobank.forms;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.forms.FieldInfo;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;

/**
 * The base class for all BioBank Java Client view forms. The forms are usually
 * created when the user selects a node in the <code>SessionView</code> tree
 * view.
 */
public abstract class BiobankViewForm extends BiobankFormBase {

    private static final String RELOAD_COMMAND_ID = "edu.ualberta.med.biobank.commands.reloadViewForm"; //$NON-NLS-1$

    private static final String EDIT_COMMAND_ID = "edu.ualberta.med.biobank.commands.edit"; //$NON-NLS-1$

    private static final String CONTEXT_VIEW_FORM = "biobank.context.viewForm"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger.getLogger(BiobankViewForm.class
        .getName());

    protected String sessionName;

    private AdapterChangedListener adapterChangedListener;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        if (adapter != null) {
            adapterChangedListener = new AdapterChangedListener() {
                @Override
                public void changed(AdapterChangedEvent event) {
                    try {
                        reload();
                    } catch (Exception e) {
                        logger.error("Error sending event", e); //$NON-NLS-1$
                    }
                }
            };
            // FIXME
            if (adapter instanceof AdapterBase)
                ((AdapterBase) adapter)
                    .addChangedListener(adapterChangedListener);
        }
    }

    @Override
    public void dispose() {
        if (adapter != null) {
            Assert.isNotNull(adapterChangedListener);
            // FIXME
            if (adapter instanceof AdapterBase)
                ((AdapterBase) adapter)
                    .removeChangedListener(adapterChangedListener);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        addToolbarButtons();
        IContextService contextService = (IContextService) getSite()
            .getService(IContextService.class);
        contextService.activateContext(CONTEXT_VIEW_FORM);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    protected void setWidgetValues(Map<String, FieldInfo> fieldsMap, Object bean) {
        for (Entry<String, FieldInfo> entry : fieldsMap.entrySet()) {
            String label = entry.getKey();
            FieldInfo fi = entry.getValue();
            IObservableValue ov = BeansObservables.observeValue(bean, label);
            Object value = ov.getValue();
            if (value != null) {
                Control widget = getWidget(label);
                if ((fi.widgetClass == Combo.class)
                    || (fi.widgetClass == BgcBaseText.class)
                    || (fi.widgetClass == Label.class)) {
                    ((BgcBaseText) widget).setText((String) value);
                }
            }
        }
    }

    protected void addToolbarButtons() {
        addReloadAction();
        addEditAction();
        form.updateToolBar();
    }

    public void edit() {
        getSite().getPage().closeEditor(BiobankViewForm.this, false);
        adapter.openEntryForm();
    }

    protected void addEditAction() {
        if ((adapter != null) && adapter.isEditable()) {
            CommandContributionItem edit = new CommandContributionItem(
                new CommandContributionItemParameter(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow(),
                    Messages.BiobankViewForm_edit_label, EDIT_COMMAND_ID, null,
                    BiobankPlugin.getImageDescriptor(BgcPlugin.IMG_EDIT_FORM),
                    null, null, Messages.BiobankViewForm_edit_label,
                    Messages.BiobankViewForm_edit_label,
                    Messages.BiobankViewForm_edit_label, SWT.NONE,
                    Messages.BiobankViewForm_edit_label, true));
            form.getToolBarManager().add(edit);
        }
    }

    protected void addReloadAction() {
        CommandContributionItem reload = new CommandContributionItem(
            new CommandContributionItemParameter(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow(),
                Messages.BiobankViewForm_reload_label, RELOAD_COMMAND_ID, null,
                BiobankPlugin.getImageDescriptor(BgcPlugin.IMG_RELOAD_FORM),
                null, null, Messages.BiobankViewForm_reload_label,
                Messages.BiobankViewForm_reload_label,
                Messages.BiobankViewForm_reload_label, SWT.NONE,
                Messages.BiobankViewForm_reload_label, true));
        form.getToolBarManager().add(reload);
    }

    public abstract void reload() throws Exception;

}
