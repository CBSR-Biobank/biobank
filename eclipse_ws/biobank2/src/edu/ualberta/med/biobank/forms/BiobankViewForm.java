package edu.ualberta.med.biobank.forms;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;

/**
 * The base class for all BioBank2 Java Client view forms. The forms are usually
 * created when the user selects a node in the <code>SessionView</code> tree
 * view.
 */
public abstract class BiobankViewForm extends BiobankFormBase {

    private static Logger LOGGER = Logger.getLogger(BiobankViewForm.class
        .getName());

    protected String sessionName;

    private static ImageDescriptor reloadActionImage = ImageDescriptor
        .createFromImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RELOAD_FORM));

    private static ImageDescriptor editActionImage = ImageDescriptor
        .createFromImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_EDIT_FORM));

    private AdapterChangedListener adapterChangedListener;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);
        adapterChangedListener = new AdapterChangedListener() {
            @Override
            public void changed(AdapterChangedEvent event) {
                try {
                    reload();
                } catch (Exception e) {
                    LOGGER.error("Error sending event", e);
                }
            }
        };
        adapter.addChangedListener(adapterChangedListener);
    }

    @Override
    public void dispose() {
        Assert.isNotNull(adapterChangedListener);
        adapter.removeChangedListener(adapterChangedListener);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        addToolbarButtons();
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    protected void setWidgetsValues(Map<String, FieldInfo> fieldsMap,
        Object bean) {
        for (String label : fieldsMap.keySet()) {
            FieldInfo fi = fieldsMap.get(label);
            IObservableValue ov = BeansObservables.observeValue(bean, label);
            Object value = ov.getValue();
            if (value != null) {
                Control control = controls.get(label);
                if ((fi.widgetClass == Combo.class)
                    || (fi.widgetClass == Text.class)
                    || (fi.widgetClass == Label.class)) {
                    ((Text) control).setText((String) value);
                }
            }
        }
    }

    protected void addToolbarButtons() {
        Action reloadAction = new Action("Reload") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        try {
                            reload();
                        } catch (final RemoteConnectFailureException exp) {
                            BioBankPlugin.openRemoteConnectErrorMessage();
                        } catch (Exception e) {
                            LOGGER.error(
                                "BioBankFormBase.createPartControl Error", e);
                        }
                    }
                });
            }
        };
        reloadAction.setImageDescriptor(reloadActionImage);
        form.getToolBarManager().add(reloadAction);

        if (adapter.isEditable()) {
            Action edit = new Action("Edit") {
                @Override
                public void run() {
                    BusyIndicator.showWhile(Display.getDefault(),
                        new Runnable() {
                            public void run() {
                                getSite().getPage().closeEditor(
                                    BiobankViewForm.this, false);
                                AdapterBase.openForm(new FormInput(adapter),
                                    getEntryFormId());
                            }
                        });
                }
            };
            edit.setImageDescriptor(editActionImage);
            form.getToolBarManager().add(edit);
        }

        form.updateToolBar();
    }

    protected abstract void reload() throws Exception;

    protected abstract String getEntryFormId();
}
