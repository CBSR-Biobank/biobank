package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class ContainerEmptyLocationsEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.ContainerEmptyLocationsEditor";

    private BiobankText containerLabel;
    private ComboViewer topCombo;
    private IObservableValue comboStatus = new WritableValue(Boolean.FALSE,
        Boolean.class);

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100 };
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(containerLabel.getText());
        params.add(topCombo.getCombo().getText());
        return params;
    }

    @Override
    protected void createOptionSection(Composite parameterSection) {
        containerLabel = createCustomText("Container Label", parameterSection);
        topCombo = createCustomCombo("Top Container Type", parameterSection);
        topCombo.getCombo().setEnabled(false);
    }

    private ComboViewer createCustomCombo(String labelText, Composite parent) {
        ComboViewer widget = widgetCreator
            .createComboViewerWithNoSelectionValidator(parent, labelText, null,
                null, "Pallet not found");
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), comboStatus, "Pallet not found", IStatus.ERROR);
        return widget;
    }

    protected BiobankText createCustomText(String labelText, Composite parent) {
        final BiobankText widget = (BiobankText) widgetCreator
            .createLabelledWidget(parent, BiobankText.class, SWT.NONE,
                labelText, "");
        widget.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR)
                    populateTopCombos(widget.getText());
            }
        });
        widget.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB)
                    populateTopCombos(widget.getText());
            }

        });
        widget.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                topCombo.getCombo().removeAll();
                topCombo.getCombo().setEnabled(false);
                comboStatus.setValue(false);
            }
        });
        return widget;
    }

    protected void populateTopCombos(String label) {
        appService = SessionManager.getAppService();
        List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
        List<String> topContainerTypes = new ArrayList<String>();
        boolean enable = true;
        try {
            List<SiteWrapper> sites = SiteWrapper.getSites(appService);
            for (SiteWrapper site : sites) {
                containers.addAll(ContainerWrapper.getContainersInSite(
                    appService, site, label));
            }
            for (ContainerWrapper c : containers) {
                for (int i = 0; i < (label.length() / 2) - 1; i++)
                    c = c.getParent();
                topContainerTypes.add(c.getContainerType().getNameShort());
            }
        } catch (Exception e) {
            enable = false;
        }
        if (topContainerTypes.size() < 1)
            enable = false;
        if (enable) {
            topCombo.setInput(topContainerTypes.toArray(new String[] {}));
            topCombo.getCombo().select(0);
            topCombo.getCombo().setEnabled(true);
            comboStatus.setValue(true);
        } else {
            topCombo.getCombo().removeAll();
            topCombo.getCombo().setEnabled(false);
            comboStatus.setValue(false);
        }
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Location", "Pallet Type" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Container Label");
        paramNames.add("Top Container Type");
        return paramNames;
    }

}
