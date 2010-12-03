package edu.ualberta.med.biobank.widgets.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.ReportColumn;

public class ColumnSelectWidget extends Composite {
    private static final ViewerSorter DISPLAYED_COLUMNS_VIEWER_SORTER = new DisplayedColumnsViewerSorter();
    private static final LabelProvider DISPLAYED_COLUMNS_LABEL_PROVIDER = new DisplayedColumnsLabelProvider();
    private static final LabelProvider AVAILABLE_COLUMNS_LABEL_PROVIDER = new AvailableColumnsLabelProvider();
    private static final ITreeContentProvider AVAILABLE_COLUMNS_TREE_CONTENT_PROVIDER = new AvailableColumnsTreeContentProvider();
    private static final Object AVAILABLE_COLUMNS_ROOT_OBJECT = new String(
        "root");

    // @see
    // http://blog.subshell.com/devblog/2010/09/eclipse-rcp-using-a-tableviewer-with-comboboxes.html

    private final ReportWrapper report;
    private Composite container;
    private TreeViewer available;
    private TableViewer displayed;
    private Button leftButton, rightButton, upButton, downButton;

    public ColumnSelectWidget(Composite parent, int style, ReportWrapper report) {
        super(parent, style);

        this.report = report;

        init();
        createContainer();

        report.addPropertyChangeListener(
            ReportWrapper.PROPERTY_REPORT_COLUMN_COLLECTION,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent arg0) {
                    if (!isDisposed()) {
                        createContainer();
                    }
                }
            });
    }

    private void init() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 15;
        setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumHeight = 0;
        setLayoutData(layoutData);
    }

    private void disposeContainer() {
        if (container != null && !container.isDisposed()) {
            container.dispose();
        }
    }

    private void createContainer() {
        disposeContainer();

        container = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        container.setLayout(layout);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.horizontalIndent = 0;
        layoutData.verticalIndent = 0;
        container.setLayoutData(layoutData);

        createAvailableTable();
        createSwitchButtons();
        createDisplayedTable();
        createRepositionButtons();
    }

    private void createAvailableTable() {
        Composite subContainer = new Composite(container, SWT.NONE);
        subContainer.setLayout(new GridLayout(1, false));
        subContainer
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLabel(subContainer, "Available Columns");

        available = new TreeViewer(subContainer, SWT.MULTI | SWT.READ_ONLY
            | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = available.getTree().getItemHeight() * 4;
        layoutData.widthHint = 180;
        available.getControl().setLayoutData(layoutData);
        available.setSorter(new ViewerSorter());
        available.setLabelProvider(AVAILABLE_COLUMNS_LABEL_PROVIDER);
        available.setContentProvider(AVAILABLE_COLUMNS_TREE_CONTENT_PROVIDER);
        available.setInput(AVAILABLE_COLUMNS_ROOT_OBJECT);

        for (EntityColumn entityColumn : report.getEntityColumnCollection()) {
            addAvailable(entityColumn);
        }
    }

    private void createDisplayedTable() {
        Composite subContainer = new Composite(container, SWT.NONE);
        subContainer.setLayout(new GridLayout(1, false));
        subContainer
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLabel(subContainer, "Displayed Columns");

        displayed = new TableViewer(subContainer, SWT.MULTI | SWT.READ_ONLY
            | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = displayed.getTable().getItemHeight() * 4;
        layoutData.widthHint = 180;
        displayed.getControl().setLayoutData(layoutData);

        displayed.setContentProvider(new ArrayContentProvider());
        displayed.setSorter(DISPLAYED_COLUMNS_VIEWER_SORTER);
        displayed.setLabelProvider(DISPLAYED_COLUMNS_LABEL_PROVIDER);

        for (ReportColumn reportColumn : report.getReportColumnCollection()) {
            displayColumn(reportColumn);
        }

        updateDisplayedColumnPositions();
    }

    private void createSwitchButtons() {
        Composite subContainer = new Composite(container, SWT.NONE);
        subContainer.setLayout(new GridLayout(1, false));
        subContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
            false, true));

        createLabel(subContainer, "");

        rightButton = createButton(subContainer, BioBankPlugin.IMG_ARROW_RIGHT);
        leftButton = createButton(subContainer, BioBankPlugin.IMG_ARROW_LEFT);

        rightButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                displayColumns(available.getSelection());
            }
        });

        leftButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeColumns(displayed.getSelection());
            }
        });
    }

    private void createRepositionButtons() {
        Composite subContainer = new Composite(container, SWT.NONE);
        subContainer.setLayout(new GridLayout(1, false));
        subContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
            false, true));

        createLabel(subContainer, "");

        upButton = createButton(subContainer, BioBankPlugin.IMG_UP);
        downButton = createButton(subContainer, BioBankPlugin.IMG_DOWN);
    }

    private static Button createButton(Composite parent, String imageName) {
        Button button = new Button(parent, SWT.PUSH);
        button.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(imageName));

        return button;
    }

    private static Label createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        label.setFont(new Font(null, "sans-serif", 8, SWT.BOLD));
        return label;
    }

    private void displayColumns(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Iterator<?> it = structuredSelection.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof EntityColumnWrapper) {
                    EntityColumnWrapper entityColumnWrapper = (EntityColumnWrapper) o;
                    displayColumn(entityColumnWrapper);
                } else if (o instanceof PropertyModifierWrapper) {
                    PropertyModifierWrapper propertyModifierWrapper = (PropertyModifierWrapper) o;
                    displayColumn(propertyModifierWrapper);
                }
            }
        }
    }

    private void removeColumns(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Iterator<?> it = structuredSelection.iterator();
            while (it.hasNext()) {
                ReportColumnWrapper reportColumnWrapper = (ReportColumnWrapper) it
                    .next();
                removeColumn(reportColumnWrapper);
            }
        }
    }

    private void updateDisplayedColumnPositions() {
        int numItems = displayed.getTable().getItemCount();
        for (int i = 0; i < numItems; i++) {
            ReportColumnWrapper wrapper = (ReportColumnWrapper) displayed
                .getElementAt(i);
            ReportColumn reportColumn = wrapper.getReportColumn();
            reportColumn.setPosition(i);
        }
    }

    private static Object getElement(TreeViewer treeViewer, Object needle) {
        for (TreeItem item : treeViewer.getTree().getItems()) {
            if (needle.equals(item.getData())) {
                return item.getData();
            }
        }
        return null;
    }

    private void displayColumn(ReportColumn reportColumn) {
        removeAvailable(reportColumn.getEntityColumn());
        addDisplayed(reportColumn);
    }

    private void displayColumn(EntityColumnWrapper entityColumnWrapper) {
        EntityColumn entityColumn = entityColumnWrapper.getEntityColumn();
        removeAvailable(entityColumn);
        addDisplayed(entityColumn);
    }

    private void displayColumn(PropertyModifierWrapper propertyModifierWrapper) {
        EntityColumn entityColumn = propertyModifierWrapper
            .getEntityColumnWrapper().getEntityColumn();
        removeAvailable(entityColumn);

        ReportColumn reportColumn = new ReportColumn();
        reportColumn.setEntityColumn(entityColumn);
        reportColumn.setPropertyModifier(propertyModifierWrapper
            .getPropertyModifier());

        addDisplayed(reportColumn);
    }

    private void removeColumn(ReportColumnWrapper reportColumnWrapper) {
        removeDisplayed(reportColumnWrapper);
        addAvailable(reportColumnWrapper.getReportColumn().getEntityColumn());
    }

    private void addAvailable(EntityColumn entityColumn) {
        EntityColumnWrapper entityColumnWrapper = new EntityColumnWrapper(
            entityColumn);

        // only add an available EntityColumn if it doesn't already exist
        Object o = getElement(available, entityColumnWrapper);
        if (o == null) {
            available.add(AVAILABLE_COLUMNS_ROOT_OBJECT, entityColumnWrapper);
        }
    }

    private void removeAvailable(EntityColumn entityColumn) {
        available.remove(new EntityColumnWrapper(entityColumn));
    }

    private void addDisplayed(ReportColumn reportColumn) {
        int position = displayed.getTable().getItemCount();
        reportColumn.setPosition(position);
        displayed.add(new ReportColumnWrapper(reportColumn));
    }

    private void addDisplayed(EntityColumn entityColumn) {
        ReportColumn reportColumn = new ReportColumn();
        reportColumn.setEntityColumn(entityColumn);
        addDisplayed(reportColumn);
    }

    private void removeDisplayed(ReportColumnWrapper reportColumnWrapper) {
        displayed.remove(reportColumnWrapper);
    }

    private static class ReportColumnWrapper {
        private final ReportColumn reportColumn;

        public ReportColumnWrapper(ReportColumn reportColumn) {
            this.reportColumn = reportColumn;
        }

        public ReportColumn getReportColumn() {
            return reportColumn;
        }
    }

    private static class EntityColumnWrapper {
        private final EntityColumn entityColumn;
        private final Integer entityColumnId;
        private final Collection<PropertyModifierWrapper> modifiers;

        public EntityColumnWrapper(EntityColumn entityColumn) {
            this.entityColumn = entityColumn;
            this.entityColumnId = entityColumn.getId();
            this.modifiers = getModifiers(entityColumn);
        }

        public EntityColumn getEntityColumn() {
            return entityColumn;
        }

        public Collection<PropertyModifierWrapper> getPropertyModifierCollection() {
            return modifiers;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof EntityColumnWrapper) {
                if (((EntityColumnWrapper) o).entityColumnId
                    .equals(entityColumnId)) {
                    return true;
                }
            }
            return false;
        }

        private Collection<PropertyModifierWrapper> getModifiers(
            EntityColumn entityColumn) {
            List<PropertyModifierWrapper> result = new ArrayList<PropertyModifierWrapper>();

            for (PropertyModifier modifier : entityColumn.getEntityProperty()
                .getPropertyType().getPropertyModifierCollection()) {
                PropertyModifierWrapper wrapper = new PropertyModifierWrapper(
                    this, modifier);
                result.add(wrapper);
            }

            return result;
        }
    }

    private static class PropertyModifierWrapper {
        private final PropertyModifier propertyModifier;
        private final EntityColumnWrapper entityColumnWrapper;

        public PropertyModifierWrapper(EntityColumnWrapper entityColumnWrapper,
            PropertyModifier propertyModifier) {
            this.propertyModifier = propertyModifier;
            this.entityColumnWrapper = entityColumnWrapper;
        }

        public PropertyModifier getPropertyModifier() {
            return propertyModifier;
        }

        public EntityColumnWrapper getEntityColumnWrapper() {
            return entityColumnWrapper;
        }
    }

    private static class DisplayedColumnsViewerSorter extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object o1, Object o2) {
            ReportColumn rc1 = ((ReportColumnWrapper) o1).getReportColumn();
            ReportColumn rc2 = ((ReportColumnWrapper) o2).getReportColumn();

            return rc1.getPosition().compareTo(rc2.getPosition());
        }
    }

    private static class AvailableColumnsLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof EntityColumnWrapper) {
                return ((EntityColumnWrapper) element).getEntityColumn()
                    .getName();
            } else if (element instanceof PropertyModifierWrapper) {
                return ((PropertyModifierWrapper) element)
                    .getPropertyModifier().getName();
            }
            return "";
        }
    }

    private static class AvailableColumnsTreeContentProvider implements
        ITreeContentProvider {
        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (AVAILABLE_COLUMNS_ROOT_OBJECT.equals(inputElement)) {
                return new Object[] {};
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof EntityColumnWrapper) {
                EntityColumnWrapper wrapper = (EntityColumnWrapper) parentElement;
                return wrapper.getPropertyModifierCollection().toArray();
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof PropertyModifierWrapper) {
                PropertyModifierWrapper wrapper = (PropertyModifierWrapper) element;
                return wrapper.getEntityColumnWrapper();
            }
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof EntityColumnWrapper) {
                EntityColumnWrapper wrapper = (EntityColumnWrapper) element;
                return !wrapper.getPropertyModifierCollection().isEmpty();
            }
            return false;
        }
    }

    private static class DisplayedColumnsLabelProvider extends LabelProvider
        implements ITableLabelProvider {
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ReportColumnWrapper) {
                ReportColumn reportColumn = ((ReportColumnWrapper) element)
                    .getReportColumn();

                String text = reportColumn.getEntityColumn().getName();
                if (reportColumn.getPropertyModifier() != null) {
                    text += " (" + reportColumn.getPropertyModifier().getName()
                        + ")";
                }
                return text;

            }
            return null;
        }
    }
}
