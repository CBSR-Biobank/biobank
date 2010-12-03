package edu.ualberta.med.biobank.widgets.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.PropertyType;
import edu.ualberta.med.biobank.model.ReportColumn;

public class ColumnSelectWidget extends Composite {
    private static final ViewerSorter REPORT_COLUMN_VIEWER_SORTER = new ReportColumnViewerSorter();
    private static final LabelProvider ENTITY_COLUMN_LABEL_PROVIDER = new EntityColumnLabelProvider();
    private static final LabelProvider REPORT_COLUMN_LABEL_PROVIDER = new ReportColumnLabelProvider();

    // @see
    // http://blog.subshell.com/devblog/2010/09/eclipse-rcp-using-a-tableviewer-with-comboboxes.html

    private final ReportWrapper report;
    private Composite container;
    private TableViewer available, displayed;
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
        available = createTableViewer(container, "Available Columns");

        available.setSorter(new ViewerSorter());
        available.setLabelProvider(ENTITY_COLUMN_LABEL_PROVIDER);

        for (EntityColumn entityColumn : report.getEntityColumnCollection()) {
            addAvailable(entityColumn);
        }
    }

    private void createDisplayedTable() {
        displayed = createTableViewer(container, "Displayed Columns");

        Table table = displayed.getTable();

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));

        table.setLinesVisible(false);
        table.setLayout(tableLayout);

        TableViewerColumn editColumn = new TableViewerColumn(displayed,
            SWT.NONE);

        ColumnViewer viewer = editColumn.getViewer();
        EditingSupport editingSupport = new ComboEditingSupport(viewer);
        editColumn.setEditingSupport(editingSupport);

        displayed.setContentProvider(new ArrayContentProvider());
        displayed.setSorter(REPORT_COLUMN_VIEWER_SORTER);
        displayed.setLabelProvider(REPORT_COLUMN_LABEL_PROVIDER);

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

    private static TableViewer createTableViewer(Composite parent,
        String labelText) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLabel(container, labelText);

        // TODO: pass in SWT.FULL_SELECTION?
        TableViewer tableViewer = new TableViewer(container, SWT.FULL_SELECTION
            | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL
            | SWT.V_SCROLL);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = tableViewer.getTable().getItemHeight() * 4;
        layoutData.widthHint = 180;
        tableViewer.getTable().setLayoutData(layoutData);

        return tableViewer;
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
                EntityColumnWrapper entityColumnWrapper = (EntityColumnWrapper) it
                    .next();
                displayColumn(entityColumnWrapper);
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

    private static int getElementIndex(TableViewer tableViewer, Object needle) {
        int numItems = tableViewer.getTable().getItemCount();
        for (int i = 0; i < numItems; i++) {
            if (needle.equals(tableViewer.getElementAt(i))) {
                return i;
            }
        }
        return -1;
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

    private void removeColumn(ReportColumnWrapper reportColumnWrapper) {
        removeDisplayed(reportColumnWrapper);
        addAvailable(reportColumnWrapper.getReportColumn().getEntityColumn());
    }

    private void addAvailable(EntityColumn entityColumn) {
        EntityColumnWrapper entityColumnWrapper = new EntityColumnWrapper(
            entityColumn);

        // only add an available EntityColumn if it doesn't already exist
        int index = getElementIndex(available, entityColumnWrapper);
        if (index == -1) {
            available.add(entityColumnWrapper);
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

        public EntityColumnWrapper(EntityColumn entityColumn) {
            this.entityColumn = entityColumn;
            this.entityColumnId = entityColumn.getId();
        }

        public EntityColumn getEntityColumn() {
            return entityColumn;
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
    }

    private static class ReportColumnViewerSorter extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object o1, Object o2) {
            ReportColumn rc1 = ((ReportColumnWrapper) o1).getReportColumn();
            ReportColumn rc2 = ((ReportColumnWrapper) o2).getReportColumn();

            return rc1.getPosition().compareTo(rc2.getPosition());
        }
    }

    private static class EntityColumnLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof EntityColumnWrapper) {
                return ((EntityColumnWrapper) element).getEntityColumn()
                    .getName();
            }
            return "";
        }
    }

    private static class ReportColumnLabelProvider extends LabelProvider
        implements ITableLabelProvider {
        @Override
        public String getText(Object element) {
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
            return "";
        }

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
                    text += ", " + reportColumn.getPropertyModifier().getName();
                }
                return text;

            }
            return null;
        }
    }

    public static class ComboEditingSupport extends EditingSupport {
        private ComboEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }

        private ComboBoxViewerCellEditor createCellEditor() {
            ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(
                (Composite) getViewer().getControl(), SWT.READ_ONLY);
            cellEditor.setLabelProvider(new LabelProvider());
            cellEditor.setContenProvider(new ArrayContentProvider());
            return cellEditor;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            if (element instanceof ReportColumnWrapper) {
                ReportColumnWrapper wrapper = (ReportColumnWrapper) element;

                ReportColumn reportColumn = wrapper.getReportColumn();
                EntityColumn entityColumn = reportColumn.getEntityColumn();
                EntityProperty entityProperty = entityColumn
                    .getEntityProperty();
                PropertyType propertyType = entityProperty.getPropertyType();

                // TODO: extract "Date" to somewhere?
                if (propertyType.getName().equals("Date")) {
                    ComboBoxViewerCellEditor cellEditor = createCellEditor();
                    cellEditor.setInput(new String[] { "Date Drawn", "b" });

                    return cellEditor;
                }
            }
            return null;
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected Object getValue(Object element) {
            if (element instanceof ReportColumnWrapper) {
                ReportColumnWrapper wrapper = (ReportColumnWrapper) element;
                return wrapper.getReportColumn().getEntityColumn().getName();
            }
            return null;
        }

        @Override
        protected void setValue(Object element, Object value) {
            // if (element instanceof ReportColumnWrapper && value instanceof
            // Value) {
            // ExampleData data = (ExampleData) element;
            // Value newValue = (Value) value;
            // /* only set new value if it differs from old one */
            // if (!data.getData().equals(newValue)) {
            // data.setData(newValue);
            // }
            // }
        }

    }
}
