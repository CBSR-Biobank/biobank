package edu.ualberta.med.biobank.forms;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;

/**
 * The base class for all BioBank2 Java Client view forms. The forms are usually
 * created when the user selects a node in the <code>SessionView</code> tree
 * view.
 */
public abstract class BiobankViewForm extends BiobankFormBase {

    protected String sessionName;

    private static ImageDescriptor reloadActionImage = ImageDescriptor
        .createFromImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RELOAD_FORM));

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

    @Override
    public void setFocus() {
    }

    protected void setWidgetsValues(ListOrderedMap fieldsMap, Object bean) {
        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            FieldInfo fi = (FieldInfo) it.getValue();
            IObservableValue ov = BeansObservables.observeValue(bean, key);
            Object value = ov.getValue();
            if (value != null) {
                Control control = controls.get(key);
                if ((fi.widgetClass == Combo.class)
                    || (fi.widgetClass == Text.class)
                    || (fi.widgetClass == Label.class)) {
                    ((Label) control).setText((String) value);
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
                            SessionManager.getLogger().error(
                                "BioBankFormBase.createPartControl Error", e);
                        }
                    }
                });
            }
        };

        reloadAction.setImageDescriptor(reloadActionImage);
        form.getToolBarManager().add(reloadAction);

        ControlContribution edit = new ControlContribution("Edit") {
            @Override
            protected Control createControl(Composite parent) {
                final Button editButton = new Button(parent, SWT.PUSH);
                editButton.setText("Edit");
                editButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getSite().getPage().closeEditor(BiobankViewForm.this,
                            false);
                        try {
                            getSite().getPage().openEditor(
                                new FormInput(adapter), getEntryFormId(), true);
                        } catch (PartInitException exp) {
                            SessionManager.getLogger().error(
                                "Can't open the entry form", exp);
                        }
                    }
                });
                return editButton;
            }
        };
        form.getToolBarManager().add(edit);
        form.updateToolBar();
    }

    protected abstract void reload() throws Exception;

    protected abstract String getEntryFormId();
}
