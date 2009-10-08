package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.views.ReportsView;

public class AttributeQueryClause extends AbstractQueryClause {
    private Composite attributeQueries;
    private Composite buttons;
    private Label where;
    private Button andButton;
    private Button deleteButton;

    private SelectionAdapter addListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            createFormContent();
            view.updateScrollBars();
            if (whereCombos.size() == 1)
                deleteButton.setEnabled(true);
        }
    };

    private SelectionAdapter deleteListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            whereCombos.get(whereCombos.size() - 1).getCombo().dispose();
            operatorCombos.get(operatorCombos.size() - 1).getCombo().dispose();
            searchFields.get(searchFields.size() - 1).dispose();
            whereCombos.remove(whereCombos.size() - 1);
            operatorCombos.remove(operatorCombos.size() - 1);
            searchFields.remove(searchFields.size() - 1);
            view.updateScrollBars();
            if (whereCombos.size() == 0)
                deleteButton.setEnabled(false);
        }
    };

    public AttributeQueryClause(Composite parent, Class<?> modelObjectClass,
        String alias, ReportsView view) {
        super(modelObjectClass, alias, view);

        where = new Label(parent, SWT.None);
        where.setText("Where: ");

        attributeQueries = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        attributeQueries.setLayout(layout);
        createFormContent();

        buttons = new Composite(parent, SWT.NONE);
        GridLayout buttonLayout = new GridLayout(2, false);
        buttons.setLayout(buttonLayout);

        andButton = new Button(buttons, SWT.NONE);
        andButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_ADD));
        andButton.addSelectionListener(addListener);

        deleteButton = new Button(buttons, SWT.NONE);
        deleteButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_DELETE));
        deleteButton.addSelectionListener(deleteListener);

    }

    @Override
    protected void createFormContent() {
        // variable
        ComboViewer whereCombo = AttributeQueryClause.createCombo(
            attributeQueries, attributes);
        whereCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            int index = whereCombos.size();

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateOperatorCombo(index);
            }
        });
        whereCombos.add(whereCombo);

        // operator
        ComboViewer operatorCombo = AttributeQueryClause.createCombo(
            attributeQueries, null);
        operatorCombos.add(operatorCombo);
        // value
        Text searchField = new Text(attributeQueries, SWT.BORDER);
        searchFields.add(searchField);

        GridData gd = new GridData();
        gd.widthHint = 100;
        searchField.setLayoutData(gd);

        if (attributes.size() > 0)
            whereCombo.setSelection(new StructuredSelection(attributes.get(0)));

    }

    @Override
    public Boolean getInternalEnabled() {
        return where.getEnabled();
    }

    @Override
    public void setInternalEnabled(Boolean enable) {
        where.setEnabled(enable);
        andButton.setEnabled(enable);
        deleteButton.setEnabled(enable);
    }

}
