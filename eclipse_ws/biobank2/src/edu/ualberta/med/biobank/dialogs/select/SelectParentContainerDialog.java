package edu.ualberta.med.biobank.dialogs.select;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

/**
 * Allows the user to select a parent container when more than one is available
 */

public class SelectParentContainerDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SelectParentContainerDialog.class);

    private final Collection<Container> containers;
    private ComboViewer comboViewer;
    protected Container selectedContainer;

    public SelectParentContainerDialog(Shell parent,
        Collection<Container> containers) {
        super(parent);
        Assert.isNotNull(containers);
        this.containers = containers;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // select parent container dialog title
        return i18n.tr("Select Parent Container");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // select parent container dialog title area message
        return i18n.tr("Select the appropriate parent container");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // select parent container dialog title area title
        return i18n.tr("Multiple parent containers are possible");
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        comboViewer = getWidgetCreator().createComboViewer(contents,
            // select parent container combo box label
            i18n.tr("Select parent"),
            containers,
            null,
            // parent container required validation message
            i18n.tr("A container should be selected"),
            null, new BiobankLabelProvider());

        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                ContainerWrapper container =
                    new ContainerWrapper(SessionManager.getAppService(),
                        (Container) element);
                StringBuffer text = new StringBuffer();
                text.append(container.getFullInfoLabel());
                ContainerWrapper parent = container.getParentContainer();
                boolean hasParents = parent != null;
                if (hasParents)
                    text.append(" (")
                        .append(
                            i18n.trc("Select Parent Container Option Label",
                                "Parents"))
                        .append(": ");
                while (parent != null) {
                    text.append(parent.getFullInfoLabel());
                    parent = parent.getParentContainer();
                    if (parent != null)
                        text.append("; ");
                }
                if (hasParents)
                    text.append(")");
                return text.toString();
            }
        });
        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    saveSelectedContainer();
                }
            });
    }

    private void saveSelectedContainer() {
        selectedContainer =
            (Container) ((IStructuredSelection) comboViewer
                .getSelection()).getFirstElement();
    }

    public Container getSelectedContainer() {
        return selectedContainer;
    }

}
