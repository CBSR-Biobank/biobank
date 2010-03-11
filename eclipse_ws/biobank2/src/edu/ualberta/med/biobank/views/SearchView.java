package edu.ualberta.med.biobank.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

public class SearchView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.search";

    private WidgetCreator widgetCreator;

    public SearchView() {
        widgetCreator = new WidgetCreator(null);
        widgetCreator.initDataBinding();
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        Text searchText = (Text) widgetCreator.createBoundWidgetWithLabel(
            parent, Text.class, SWT.NONE, "Barcode", null, null, null);
    }

    @Override
    public void setFocus() {

    }

}
