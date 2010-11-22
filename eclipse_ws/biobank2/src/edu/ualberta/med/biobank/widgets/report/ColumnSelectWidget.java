package edu.ualberta.med.biobank.widgets.report;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;

public class ColumnSelectWidget extends Composite {
    private static final ViewerSorter REPORT_COLUMN_VIEWER_SORTER = new ReportFilterViewerSorter();
    private static final LabelProvider ENTITY_COLUMN_LABEL_PROVIDER = new EntityColumnLabelProvider();
    private static final LabelProvider REPORT_COLUMN_LABEL_PROVIDER = new ReportColumnLabelProvider();

    // @see
    // http://blog.subshell.com/devblog/2010/09/eclipse-rcp-using-a-tableviewer-with-comboboxes.html

    private Composite container;
    private TableViewer available;
    private TableViewer displayed;
    private final ReportWrapper report;

    public ColumnSelectWidget(Composite parent, int style, ReportWrapper report) {
        super(parent, style);

        this.report = report;

        createContainer(parent);
        createAvailableTable(container);
        createSwitchButtons(container);
        createDisplayedTable(container);
        // createRepositionButtons();
    }

    private void showColumn(EntityColumn entityCol) {
        available.remove(entityCol);

        Integer position = displayed.getTable().getItemCount();

        ReportColumn reportCol = new ReportColumn();
        reportCol.setEntityColumn(entityCol);
        reportCol.setPosition(position);

        displayed.add(reportCol);
    }

    private void hideColumn(ReportColumn reportCol) {
        available.add(reportCol.getEntityColumn());
        displayed.remove(reportCol);
    }

    private void moveColumn(ReportColumn reportCol, Integer position) {
        reportCol.setPosition(position);
        displayed.refresh();
    }

    private void createContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        container.setLayout(layout);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(layoutData);
    }

    private void createAvailableTable(Composite parent) {
        available = createTableViewer(parent, "Available Columns");

        available.setSorter(new ViewerSorter());
        available.setLabelProvider(ENTITY_COLUMN_LABEL_PROVIDER);

        for (EntityColumn col : report.getEntityColumnCollection()) {
            available.add(col);
        }
    }

    private void createSwitchButtons(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.verticalAlignment = SWT.VERTICAL;
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
            true));

        Button moveRightButton = new Button(container, SWT.PUSH);
        moveRightButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ARROW_RIGHT));

        Button moveLeftButton = new Button(container, SWT.PUSH);
        moveLeftButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ARROW_LEFT));
    }

    private void createDisplayedTable(Composite parent) {
        displayed = createTableViewer(parent, "Displayed Columns");

        displayed.setSorter(REPORT_COLUMN_VIEWER_SORTER);
        displayed.setLabelProvider(REPORT_COLUMN_LABEL_PROVIDER);
    }

    private static TableViewer createTableViewer(Composite parent,
        String labelText) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(container, SWT.NONE);
        label.setText(labelText);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        label.setFont(new Font(null, "sans-serif", 8, SWT.BOLD));

        TableViewer tableViewer = new TableViewer(container, SWT.BORDER);
        tableViewer.getTable().setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true));
        return tableViewer;
    }

    private static class ReportFilterViewerSorter extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object o1, Object o2) {
            ReportFilter rf1 = (ReportFilter) o1;
            ReportFilter rf2 = (ReportFilter) o2;

            return rf1.getPosition().compareTo(rf2.getPosition());
        }
    }

    private static class EntityColumnLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof EntityColumn) {
                return ((EntityColumn) element).getName();
            }
            return "";
        }
    }

    private static class ReportColumnLabelProvider extends LabelProvider
        implements ITableLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof EntityColumn) {
                return ((ReportColumn) element).getEntityColumn().getName();
            }
            return "";
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ReportColumn) {
                ReportColumn reportCol = (ReportColumn) element;

                switch (columnIndex) {
                case 0:
                    return reportCol.getEntityColumn().getName();
                case 1:
                    return "Asdf";
                }
            }
            return null;
        }
    }
}
