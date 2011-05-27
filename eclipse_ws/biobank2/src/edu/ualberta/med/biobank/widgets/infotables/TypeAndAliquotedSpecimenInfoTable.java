package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.trees.infos.InfoTreeWidget;

public class TypeAndAliquotedSpecimenInfoTable extends
    InfoTreeWidget<TypeAndAliquotedSpecimenData> {

    private static final int PAGE_SIZE_ROWS = 15;

    private static final String[] HEADINGS = new String[] {
        Messages.getString("AliquotedSpecimen.field.type.label"),
        Messages.getString("AliquotedSpecimen.field.volume.label"),
        Messages.getString("AliquotedSpecimen.field.quantity.label"),
        Messages.getString("label.activity") };

    private class VolumeEditingSupport extends EditingSupport {

        public VolumeEditingSupport() {
            super(treeViewer);
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            TextCellEditor editor = new TextCellEditor(treeViewer.getTree());
            editor.setValidator(new ICellEditorValidator() {
                // FIXME validator not prevent closing ?
                private final Pattern pattern = Pattern
                    .compile("^[0-9\\.\\+-]*$");
                private final String msg = "Volume should be set";

                @Override
                public String isValid(Object value) {
                    System.out.println("isValid:" + value);
                    if (value != null && !(value instanceof Double)) {
                        if (((String) value).length() == 0)
                            return msg;

                        Matcher m = pattern.matcher((String) value);
                        if (!m.matches())
                            return msg;
                    }
                    System.out.println("isvalid true");
                    return null;
                }
            });
            return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            return ((TypeAndAliquotedSpecimenData) model.o).isUsed();
        }

        @Override
        protected Object getValue(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            Double vol = ((TypeAndAliquotedSpecimenData) model.o).getVolume();
            return vol == null ? "" : vol.toString();
        }

        @Override
        protected void setValue(Object element, Object value) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            ((TypeAndAliquotedSpecimenData) model.o).setVolume(Double
                .valueOf(String.valueOf(value)));
            treeViewer.refresh();
        }
    }

    private class QuantityEditingSupport extends EditingSupport {

        public QuantityEditingSupport() {
            super(treeViewer);
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            TextCellEditor editor = new TextCellEditor(treeViewer.getTree());
            editor.setValidator(new ICellEditorValidator() {
                // FIXME validator not prevent closing ?
                private final Pattern pattern = Pattern.compile("^[0-9\\+-]*$");
                private final String msg = "Quantity should be set";

                @Override
                public String isValid(Object value) {
                    System.out.println("isValid:" + value);
                    if (value != null && !(value instanceof Integer)) {
                        if (((String) value).length() == 0)
                            return msg;

                        Matcher m = pattern.matcher((String) value);
                        if (!m.matches())
                            return msg;
                    }
                    System.out.println("isvalid true");
                    return null;
                }
            });
            return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            return ((TypeAndAliquotedSpecimenData) model.o).isUsed();
        }

        @Override
        protected Object getValue(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            Integer q = ((TypeAndAliquotedSpecimenData) model.o).getQuantity();
            return q == null ? "" : q.toString();
        }

        @Override
        protected void setValue(Object element, Object value) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            ((TypeAndAliquotedSpecimenData) model.o).setQuantity(Integer
                .valueOf(String.valueOf(value)));
            treeViewer.refresh();
        }
    }

    private class SelectionEditingSupport extends EditingSupport {

        public SelectionEditingSupport() {
            super(treeViewer);
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            CheckboxCellEditor editor = new CheckboxCellEditor();
            return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected Object getValue(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            return ((TypeAndAliquotedSpecimenData) model.o).isUsed();
        }

        @Override
        protected void setValue(Object element, Object value) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            ((TypeAndAliquotedSpecimenData) model.o).setUsed((Boolean) value);
            treeViewer.refresh();
        }
    }

    private class StatusEditingSupport extends EditingSupport {

        public StatusEditingSupport() {
            super(treeViewer);
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            ComboBoxCellEditor editor = new ComboBoxCellEditor(
                treeViewer.getTree(), new String[] { "Active", "Closed" });
            return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            return ((TypeAndAliquotedSpecimenData) model.o).isUsed();
        }

        @Override
        protected Object getValue(Object element) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            ActivityStatusWrapper s = ((TypeAndAliquotedSpecimenData) model.o)
                .getStatus();
            // FIXME
            return 0; // s == null ? "" : s.getName();
        }

        @Override
        protected void setValue(Object element, Object value) {
            BiobankCollectionModel model = (BiobankCollectionModel) element;
            // FIXME
            // ((TypeAndAliquotedSpecimenData) model.o).setQuantity(Integer
            // .valueOf(String.valueOf(value)));
            treeViewer.refresh();
        }
    }

    public TypeAndAliquotedSpecimenInfoTable(Composite parent,
        List<TypeAndAliquotedSpecimenData> typeAndAliquotedList) {
        super(parent, typeAndAliquotedList, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected EditingSupport getEditingSupport(int index) {
        switch (index) {
        case 0:
            return new SelectionEditingSupport();
        case 1:
            return new VolumeEditingSupport();
        case 2:
            return new QuantityEditingSupport();
        case 3:
            return new StatusEditingSupport();
        }
        return null;
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                TypeAndAliquotedSpecimenData item = (TypeAndAliquotedSpecimenData) ((BiobankCollectionModel) element).o;
                if (item != null && columnIndex == 0)
                    return item.isUsed() ? BiobankPlugin.getDefault()
                        .getImageRegistry().get(BiobankPlugin.IMG_CHECK)
                        : BiobankPlugin.getDefault().getImageRegistry()
                            .get(BiobankPlugin.IMG_UNCHECK);
                return null;
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                TypeAndAliquotedSpecimenData item = (TypeAndAliquotedSpecimenData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.getType().getName();
                case 1:
                    return item.getVolume() == null ? "" : item.getVolume()
                        .toString();
                case 2:
                    return item.getQuantity() == null ? "" : item.getQuantity()
                        .toString();
                case 3:
                    return item.getStatus() == null ? "" : item.getStatus()
                        .getName();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TypeAndAliquotedSpecimenData) o).toString();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
