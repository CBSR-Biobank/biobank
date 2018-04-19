package edu.ualberta.med.biobank.widgets.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.SpecimenEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcClipboard;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;

public class SpecimensTreeTable extends BgcBaseWidget {

    private static final I18n i18n = I18nFactory.getI18n(DispatchSpecimensTreeTable.class);

    private final TreeViewer tv;

    private final Menu menu;

    private final List<SpecimenInfo> sourceSpecimenInfos;

    private final List<SpecimenInfo> aliquotedSpecimenInfos;

    private final Map<Integer, List<SpecimenInfo>> specimenTree;

    private final ITreeContentProvider contentProvider = new ITreeContentProvider() {
        @Override
        public void dispose() {
            // do nothing
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof String) {
                return sourceSpecimenInfos.toArray();
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof SpecimenInfo) {
                SpecimenInfo parentInfo = (SpecimenInfo) parentElement;
                List<SpecimenInfo> result = specimenTree.get(parentInfo.specimen.getId());
                if (result != null) {
                    return result.toArray();
                }
            }
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof SpecimenInfo) {
                SpecimenInfo info = (SpecimenInfo) element;
                List<SpecimenInfo> children = specimenTree.get(info.specimen.getId());
                return ((children != null) && !children.isEmpty());
            }
            return false;
        }
    };

    private final BgcLabelProvider labelProvider = new BgcLabelProvider() {
        @Override
        public String getColumnText(Object element, int columnIndex) {
            SpecimenInfo info = (SpecimenInfo) element;
            switch (columnIndex) {
            case 0: return info.specimen.getInventoryId();
            case 1: return info.specimen.getSpecimenType().getNameShort();
            case 2: return info.getPositionString(true, true);
            case 3:
                Specimen parentSpecimen = info.specimen.getParentSpecimen();
                ProcessingEvent processingEvent;

                if (parentSpecimen != null) {
                    processingEvent = parentSpecimen.getProcessingEvent();
                } else {
                    processingEvent = info.specimen.getProcessingEvent();
                }

                if (processingEvent != null) {
                    return processingEvent.getWorksheet();
                }
                break;

            case 4: return DateFormatter.formatAsDateTime(info.specimen.getCreatedAt());
            case 5: return NumberFormatter.format(info.specimen.getQuantity());
            case 6: return info.specimen.getActivityStatus().getName();
            case 7:
                OriginInfo oi = info.specimen.getOriginInfo();
                return oi == null ? StringUtil.EMPTY_STRING : oi.getCenter().getNameShort();
            case 8: return info.specimen.getCurrentCenter().getNameShort();
            case 9: return info.comment;
            }
            return StringUtil.EMPTY_STRING;
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    };

    private final IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            SpecimenInfo info = getSelectedSpecimen();
            if (info != null) {
                SpecimenWrapper wrapper = new SpecimenWrapper(SessionManager.getAppService(),
                                                              info.specimen);
                SessionManager.openViewForm(wrapper);
            }
        }
    };

    @SuppressWarnings("nls")
    public SpecimensTreeTable(Composite parent,
                              List<SpecimenInfo> sourceSpecimenInfos,
                              List<SpecimenInfo> aliquotedSpecimenInfos) {
        super(parent, SWT.NONE);

        this.sourceSpecimenInfos = sourceSpecimenInfos;
        this.aliquotedSpecimenInfos = aliquotedSpecimenInfos;
        this.specimenTree = createSpecimenTree();

        setLayout(new FillLayout());
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 400;
        setLayoutData(gd);

        tv = new TreeViewer(this, SWT.MULTI | SWT.BORDER);
        Tree tree = tv.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeColumn tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Specimen.PropertyName.INVENTORY_ID.toString());
        tc.setWidth(140);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(i18n.tr("Type"));
        tc.setWidth(110);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(i18n.tr("Position"));
        tc.setWidth(110);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(ProcessingEvent.PropertyName.WORKSHEET.toString());
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Specimen.PropertyName.CREATED_AT.toString());
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Specimen.PropertyName.QUANTITY.toString());
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(ActivityStatus.NAME.singular().toString());
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(i18n.tr("Origin center"));
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(i18n.tr("Current center"));
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Comment.NAME.plural().toString());
        tc.setWidth(60);

        tv.setContentProvider(contentProvider);
        tv.setLabelProvider(labelProvider);
        tv.addDoubleClickListener(doubleClickListener);

        menu = new Menu(parent);
        tv.getTree().setMenu(menu);
        tv.setInput("root");

        BgcClipboard.addClipboardCopySupport(tv, menu, labelProvider, 5);
        addEditMenuItem();
    }

    private Map<Integer, List<SpecimenInfo>> createSpecimenTree() {
        Map<Integer, List<SpecimenInfo>> result = new HashMap<>();

        // add source specimens as parents
        for (SpecimenInfo parentInfo : sourceSpecimenInfos) {
            result.put(parentInfo.specimen.getId(), new ArrayList<SpecimenInfo>());;
        }

        // add aliquots that are parents
        for (SpecimenInfo info : aliquotedSpecimenInfos) {
            Specimen parent = info.specimen.getParentSpecimen();
            if (parent != null) {
                result.put(parent.getId(), new ArrayList<SpecimenInfo>());
            }
        }

        // add the children
        for (SpecimenInfo info : aliquotedSpecimenInfos) {
            Integer parentId = info.specimen.getParentSpecimen().getId();
            result.get(parentId).add(info);
        }

        return result;
    }

    private SpecimenInfo getSelectedSpecimen() {
        IStructuredSelection selection = (IStructuredSelection) tv.getSelection();

        if ((selection != null) && (selection.size() > 0) &&
            (selection.getFirstElement() instanceof SpecimenInfo)) {
            return (SpecimenInfo) selection.getFirstElement();
        }
        return null;
    }

    @SuppressWarnings("nls")
    private void addEditMenuItem() {
        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(i18n.tr("Edit"));
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                SpecimenInfo info = getSelectedSpecimen();
                if (info != null) {
                    SpecimenWrapper wrapper = new SpecimenWrapper(SessionManager.getAppService(),
                                                                  info.specimen);
                    AdapterBase.openForm(new FormInput(new SpecimenAdapter(null, wrapper)),
                                         SpecimenEntryForm.ID);
                }
            }
        });
    }
}
