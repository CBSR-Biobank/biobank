package edu.ualberta.med.biobank.forms;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.menus.IMenuService;

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.forms.FieldInfo;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;

/**
 * The base class for all BioBank Java Client view forms. The forms are usually
 * created when the user selects a node in the <code>SessionView</code> tree
 * view.
 */
public abstract class BiobankViewForm extends BiobankFormBase {

    @SuppressWarnings("nls")
    private static final String CONTEXT_VIEW_FORM = "biobank.context.viewForm";

    private static BgcLogger logger = BgcLogger.getLogger(BiobankViewForm.class.getName());

    protected String sessionName;

    private AdapterChangedListener adapterChangedListener;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
        super.init(editorSite, input);

        if (adapter != null) {
            adapterChangedListener = new AdapterChangedListener() {
                @SuppressWarnings("nls")
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
        addToolbarDefaultButtons();
        IContextService contextService =
            (IContextService) getSite().getService(IContextService.class);
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
                if ((fi.widgetClass == Combo.class) || (fi.widgetClass == BgcBaseText.class)
                    || (fi.widgetClass == Label.class)) {
                    ((BgcBaseText) widget).setText((String) value);
                }
            }
        }
    }

    /**
     * @param location The starting location to begin populating this
     *            contribution manager. The format is the Menu API URI format.
     */
    protected void addToolbarButtons(String location) {
        ToolBarManager manager = (ToolBarManager) form.getToolBarManager();

        IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
        menuService.populateContributionManager(manager, location);
        manager.update(true);
    }

    @SuppressWarnings("nls")
    protected void addToolbarDefaultButtons() {
        addToolbarButtons("popup:viewFormsRefreshToolBar");
        if ((adapter != null) && adapter.isEditable()) {
            addToolbarButtons("popup:viewFormsEditToolBar");
        }
    }

    public void edit() {
        getSite().getPage().closeEditor(BiobankViewForm.this, false);
        adapter.openEntryForm();
    }

}
