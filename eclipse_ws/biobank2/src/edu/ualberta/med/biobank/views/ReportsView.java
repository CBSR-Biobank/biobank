package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.reports.QueryObject;
import edu.ualberta.med.biobank.forms.ReportsEditor;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;

public class ReportsView extends ViewPart {

    public static BiobankLogger logger = BiobankLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";

    private Composite top;

    private TreeViewer querySelect;

    public ReportsView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new Composite(parent, SWT.BORDER);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        querySelect = new TreeViewer(top, SWT.BORDER);
        querySelect
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    try {
                        Class<? extends QueryObject> q = QueryObject
                            .getQueryObjectByName((String) ((IStructuredSelection) event
                                .getSelection()).getFirstElement());
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage().openEditor(new ReportInput(q),
                                ReportsEditor.ID);
                    } catch (Exception ex) {
                        BioBankPlugin.openAsyncError("Error",
                            "There was an error while building page.");
                    }
                }
            });
        querySelect.setContentProvider(new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {

            }

            @Override
            public void dispose() {
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return (Object[]) inputElement;
            }

            @Override
            public boolean hasChildren(Object element) {
                return false;
            }

            @Override
            public Object getParent(Object element) {
                return null;
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return new Object[] {};
            }
        });
        querySelect.setLabelProvider(new ILabelProvider() {
            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                return element.toString();
            }

            @Override
            public void addListener(ILabelProviderListener listener) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
            }
        });
        querySelect.setInput(QueryObject.getQueryObjectNames());
        GridData qgd = new GridData();
        qgd.verticalAlignment = SWT.FILL;
        qgd.horizontalAlignment = SWT.FILL;
        qgd.grabExcessHorizontalSpace = true;
        qgd.grabExcessVerticalSpace = true;
        querySelect.getTree().setLayoutData(qgd);
    }

    @Override
    public void setFocus() {

    }
}
