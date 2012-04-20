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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
    private static final I18n i18n = I18nFactory
        .getI18n(MoveSpecimensToDialog.class);

    private final ContainerWrapper oldContainer;

    private final HashMap<String, ContainerWrapper> map =
        new HashMap<String, ContainerWrapper>();

    private ListViewer lv;

    private BgcBaseText newLabelText;

    private ISWTObservableValue listObserveSelection;

    private WritableValue selectedValue;

    public MoveSpecimensToDialog(Shell parent, ContainerWrapper oldContainer) {
        super(parent);
        Assert.isNotNull(oldContainer);
        this.oldContainer = oldContainer;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: dialog shell title
        return i18n.tr("Move specimens from one container to another");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: dialog title area message
        return i18n
            .tr("Select the new container that can hold the specimens.\n It should be initialized, empty, as big as the previous one, and should accept these specimens.");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: dialog title area title
        return i18n.tr("Move specimens from container {0} to another",
            oldContainer.getLabel());
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        buildContainersMap();
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        newLabelText = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.FILL,
            // TR: label
            i18n.tr("New Container Label"),
            null, null, null,
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
            // TR: label
            i18n.tr("Available containers"));
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

        // TR: validation error message
        String errorMessage = i18n.tr("A label should be selected");
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            errorMessage);
        validator.setControlDecoration(BgcBaseWidget.createDecorator(listLabel,
            errorMessage));
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
        selectedValue = new WritableValue("", String.class);
        listObserveSelection = SWTObservables.observeSelection(lv.getList());
        widgetCreator.bindValue(listObserveSelection, selectedValue, uvs, uvs);
    }

    @SuppressWarnings("nls")
    protected void buildContainersMap() {
        map.clear();
        List<SpecimenTypeWrapper> typesFromOlContainer = oldContainer
            .getContainerType().getSpecimenTypeCollection();
        List<ContainerWrapper> conts = new ArrayList<ContainerWrapper>();
        try {
            conts =
                ContainerWrapper.getEmptyContainersHoldingSpecimenType(
                    SessionManager.getAppService(), oldContainer.getSite(),
                    typesFromOlContainer, oldContainer.getRowCapacity(),
                    oldContainer.getColCapacity());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Error"),
                // TR: dialog message
                i18n.tr("Failed to retrieve empty containers."));
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

}
