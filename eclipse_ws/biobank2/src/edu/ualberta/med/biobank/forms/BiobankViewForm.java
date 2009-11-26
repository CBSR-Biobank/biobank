package edu.ualberta.med.biobank.forms;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;

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
                            LOGGER.error(
                                "BioBankFormBase.createPartControl Error", e);
                        }
                    }
                });
            }
        };
        reloadAction.setImageDescriptor(reloadActionImage);
        form.getToolBarManager().add(reloadAction);

        // ControlContribution edit = new ControlContribution("Edit") {
        // @Override
        // protected Control createControl(Composite parent) {
        // final Button editButton = new Button(parent, SWT.PUSH);
        // editButton.setText("Edit");
        // editButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // getSite().getPage().closeEditor(BiobankViewForm.this,
        // false);
        // try {
        // getSite().getPage().openEditor(
        // new FormInput(adapter), getEntryFormId(), true);
        // } catch (PartInitException exp) {
        // LOGGER.error("Can't open the entry form", exp);
        // }
        // }
        // });
        // return editButton;
        // }
        // };
        Action edit = new Action("Edit") {
            @Override
            public void run() {
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        getSite().getPage().closeEditor(BiobankViewForm.this,
                            false);
                        try {
                            getSite().getPage().openEditor(
                                new FormInput(adapter), getEntryFormId(), true);
                        } catch (PartInitException exp) {
                            LOGGER.error("Can't open the entry form", exp);
                        }
                    }
                });
            }
        };
        edit.setImageDescriptor(editActionImage);
        form.getToolBarManager().add(edit);

        form.updateToolBar();
    }

    protected abstract void reload() throws Exception;

    protected abstract String getEntryFormId();
}
