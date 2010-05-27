package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.widgets.BiobankText;

//aicml-med.cs.ualberta.ca:8443
//XXX PYRX logging view -- orginally a clone of ReportsView
public class LoggingView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.forms.LoggingView";
    public static LoggingView loggingView;

    private BiobankText searchText;
    private ComboViewer searchTypeCombo;
    private Composite top;
    private Label userLabel, formLabel, actionLabel;
    Text userTextInput;
    Combo formCombo, actionCombo;

    public LoggingView() {
        loggingView = this;
    }

    /* TODO implement Auto-fill text/combo box hybrid */
    @Override
    public void createPartControl(Composite parent) {
        GridLayout gridlayout = new GridLayout();
        gridlayout.makeColumnsEqualWidth = false;
        gridlayout.numColumns = 2;
        gridlayout.verticalSpacing = 5;
        gridlayout.horizontalSpacing = 5;
        gridlayout.marginLeft = 1;
        gridlayout.marginRight = 1;

        GridData griddata = new GridData();// SWT.LEFT, SWT.LEFT, true, true
        griddata.grabExcessHorizontalSpace = true;
        griddata.grabExcessVerticalSpace = true;
        griddata.exclude = false;

        Color colorWhite = new Color(parent.getDisplay(), 255, 255, 255);

        top = new Composite(parent, SWT.BORDER);
        top.setLayout(gridlayout);
        top.setLayoutData(griddata);
        top.setBackground(colorWhite);
        top.setVisible(true);

        userLabel = new Label(top, SWT.NO_BACKGROUND);
        userLabel.setText("User:");
        userLabel.setAlignment(SWT.LEFT);
        userLabel.setBackground(colorWhite);
        userLabel.setVisible(true);

        userTextInput = new Text(top, SWT.SINGLE | SWT.BORDER);
        userTextInput.setVisible(true);
        userTextInput.setFocus();

        formLabel = new Label(top, SWT.NO_BACKGROUND);
        formLabel.setText("Form:");
        formLabel.setAlignment(SWT.LEFT);
        formLabel.setBackground(colorWhite);
        formLabel.setVisible(true);

        formCombo = new Combo(top, SWT.READ_ONLY);
        String items[] = { "Item One", "Item Two", "Item Three", "Item Four",
            "Item Five" };
        formCombo.setItems(items); // makes personal copy
        formCombo.setVisible(true);

        actionLabel = new Label(top, SWT.NO_BACKGROUND);
        actionLabel.setText("Action:");
        actionLabel.setAlignment(SWT.LEFT);
        actionLabel.setBackground(colorWhite);
        actionLabel.setVisible(true);

        actionCombo = new Combo(top, SWT.READ_ONLY);
        String items2[] = { "Item One", "Item Love Two", "Item Three",
            "Item Four", "Item Five" };
        actionCombo.setItems(items2);
        actionCombo.setVisible(true);

        /*
         * FormInput input = new FormInput(null, "Logging Form Input");
         * 
         * try { PlatformUI.getWorkbench().getActiveWorkbenchWindow()
         * .getActivePage().openEditor(input, LoggingForm.ID, false);
         * 
         * } catch (PartInitException e) {
         * 
         * e.printStackTrace(); }
         */
    }

    @Override
    public void setFocus() {
    }

}
