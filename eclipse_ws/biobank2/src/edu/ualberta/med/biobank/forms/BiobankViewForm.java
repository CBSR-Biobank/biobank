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
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;
import edu.ualberta.med.biobank.widgets.BiobankText;

/**
 * The base class for all BioBank Java Client view forms. The forms are usually
 * created when the user selects a node in the <code>SessionView</code> tree
 * view.
 */
public abstract class BiobankViewForm extends BiobankFormBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BiobankViewForm.class.getName());

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
                        logger.error("Error sending event", e);
                    }
                }
            };
            adapter.addChangedListener(adapterChangedListener);
        }
    }

    @Override
    public void dispose() {
        if (adapter != null) {
            Assert.isNotNull(adapterChangedListener);
            adapter.removeChangedListener(adapterChangedListener);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        addToolbarButtons();
        IContextService contextService = (IContextService) getSite()
            .getService(IContextService.class);
        contextService.activateContext("biobank2.context.viewForm");
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
                    || (fi.widgetClass == BiobankText.class)
                    || (fi.widgetClass == Label.class)) {
                    ((BiobankText) widget).setText((String) value);
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
                    .getActiveWorkbenchWindow(), "Edit",
                    "edu.ualberta.med.biobank.commands.edit", null,
                    BiobankPlugin
                        .getImageDescriptor(BiobankPlugin.IMG_EDIT_FORM), null,
                    null, "Edit", "Edit", "Edit", SWT.NONE, "Edit", true));
            form.getToolBarManager().add(edit);
        }
    }

    protected void addReloadAction() {
        CommandContributionItem reload = new CommandContributionItem(
            new CommandContributionItemParameter(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                "Reload",
                "edu.ualberta.med.biobank.commands.reloadViewForm",
                null,
                BiobankPlugin.getImageDescriptor(BiobankPlugin.IMG_RELOAD_FORM),
                null, null, "Reload", "Reload", "Reload", SWT.NONE, "Reload",
                true));
        form.getToolBarManager().add(reload);
    }

    public abstract void reload() throws Exception;

}
