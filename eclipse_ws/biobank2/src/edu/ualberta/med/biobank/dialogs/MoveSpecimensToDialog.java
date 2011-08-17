package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Allows the user to choose a container to which specimens will be moved
 */
public class MoveSpecimensToDialog extends BgcBaseDialog {

    private ContainerWrapper oldContainer;

    private HashMap<String, ContainerWrapper> map = new HashMap<String, ContainerWrapper>();

    private ListViewer lv;

    private BgcBaseText newLabelText;

    private ISWTObservableValue listObserveSelection;

    private WritableValue selectedValue;

    public MoveSpecimensToDialog(Shell parent, ContainerWrapper oldContainer) {
        super(parent);
        Assert.isNotNull(oldContainer);
        this.oldContainer = oldContainer;
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.MoveSpecimensToDialog_title_shell;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.MoveSpecimensToDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return NLS.bind(Messages.MoveSpecimensToDialog_title,
            oldContainer.getLabel());
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label siteLabel = widgetCreator.createLabel(contents,
            Messages.MoveSpecimensToDialog_site_label);
        siteLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
        buildContainersMap();
        if (!SessionManager.getUser().isSuperAdmin()) {
            siteLabel
                .setToolTipText(Messages.MoveSpecimensToDialog_site_tooltip);
        }

        newLabelText = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.FILL,
            Messages.MoveSpecimensToDialog_newLabel_label, null, null, null,
            null);
        newLabelText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                lv.getList().deselectAll();
                lv.refresh();
                if (lv.getList().getItemCount() == 1) {
                    lv.getList().setSelection(0);
                }
                IStructuredSelection sel = (IStructuredSelection) lv
                    .getSelection();
                String currentSelection = null;
                if (sel.size() == 1)
                    currentSelection = (String) sel.getFirstElement();
                // to trigger the binding when we modify the selection by
                // code:
                listObserveSelection.setValue(currentSelection);
            }
        });

        Label listLabel = widgetCreator.createLabel(contents,
            Messages.MoveSpecimensToDialog_available_containers_label);
        lv = new ListViewer(contents);
        lv.setContentProvider(new ArrayContentProvider());
        lv.setLabelProvider(new BiobankLabelProvider());
        lv.setInput(map.keySet());
        lv.setComparator(new ViewerComparator());
        lv.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                return ((String) element).startsWith(newLabelText.getText());
            }
        });
        GridData gd = new GridData();
        gd.heightHint = 150;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lv.getControl().setLayoutData(gd);

        // "Destination container should accept these specimens, "
        // + "must be initialized but empty, "
        // + " and as big as the previous one.") {

        String errorMessage = Messages.MoveSpecimensToDialog_newLabel_validation_msg;
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            errorMessage);
        validator.setControlDecoration(BgcBaseWidget.createDecorator(listLabel,
            errorMessage));
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
        selectedValue = new WritableValue("", String.class); //$NON-NLS-1$
        listObserveSelection = SWTObservables.observeSelection(lv.getList());
        widgetCreator.bindValue(listObserveSelection, selectedValue, uvs, uvs);
    }

    protected void buildContainersMap() {
        map.clear();
        List<SpecimenTypeWrapper> typesFromOlContainer = oldContainer
            .getContainerType().getSpecimenTypeCollection();
        List<ContainerWrapper> conts = new ArrayList<ContainerWrapper>();
        try {
            conts = ContainerWrapper.getEmptyContainersHoldingSpecimenType(
                SessionManager.getAppService(), SessionManager.getUser()
                    .getCurrentWorkingSite(), typesFromOlContainer,
                oldContainer.getRowCapacity(), oldContainer.getColCapacity());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.MoveSpecimensToDialog_getEmptyContainers_error_title,
                Messages.MoveSpecimensToDialog_getEmptyContainers_error_msg);
        }
        for (ContainerWrapper cont : conts) {
            map.put(cont.getLabel(), cont);
        }
        if (lv != null) {
            lv.setInput(map.keySet());
        }
    }

    public ContainerWrapper getNewContainer() {
        return map.get(selectedValue.getValue());
    }

    @Override
    public void okPressed() {
        boolean sure = BgcPlugin.openConfirm(
            Messages.MoveSpecimensToDialog_move_confirm_title,
            Messages.MoveSpecimensToDialog_move_confirm_msg);
        if (sure)
            super.okPressed();
    }

}
