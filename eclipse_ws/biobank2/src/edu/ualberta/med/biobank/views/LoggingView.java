package edu.ualberta.med.biobank.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.LoggingForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.LogQuery;
import edu.ualberta.med.biobank.widgets.BiobankText;

//DateTimeWidget
//aicml-med.cs.ualberta.ca:8443
//XXX PYRX logging view -- orginally a clone of ReportsView
public class LoggingView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.forms.LoggingView";
    public static LoggingView loggingView;

    private Composite top;

    private Label userLabel, formLabel, actionLabel, patientNumLabel,
        inventoryIdLabel, containerTypeLabel, containerLabelLabel,
        startDateLabel, stopDateLabel, detailsLabel;

    BiobankText patientNumTextInput, inventoryIdTextInput,
        containerLabelTextInput, detailsTextInput;

    Combo userCombo, formCombo, actionCombo, containerTypeCombo,
        startDateCombo, stopDateCombo;

    Button clearButton, searchButton;

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
        gridlayout.marginRight = 5;

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

        userCombo = new Combo(top, SWT.READ_ONLY);
        userCombo.setItems(this.loadUserList()); // makes personal copy
        userCombo.select(0);
        userCombo.setVisible(true);
        userCombo.setFocus();
        userCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        formLabel = new Label(top, SWT.NO_BACKGROUND);
        formLabel.setText("Form:");
        formLabel.setAlignment(SWT.LEFT);
        formLabel.setBackground(colorWhite);
        formLabel.setVisible(true);

        formCombo = new Combo(top, SWT.READ_ONLY);
        formCombo.setItems(this.loadFormList()); // makes personal copy
        formCombo.select(0);
        formCombo.setVisible(true);
        formCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        actionLabel = new Label(top, SWT.NO_BACKGROUND);
        actionLabel.setText("Action:");
        actionLabel.setAlignment(SWT.LEFT);
        actionLabel.setBackground(colorWhite);
        actionLabel.setVisible(true);

        actionCombo = new Combo(top, SWT.READ_ONLY);
        actionCombo.setItems(this.loadActionList());
        actionCombo.select(0);
        actionCombo.setVisible(true);
        actionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);

        patientNumLabel = new Label(top, SWT.NO_BACKGROUND);
        patientNumLabel.setText("Patient #:");
        patientNumLabel.setAlignment(SWT.LEFT);
        patientNumLabel.setBackground(colorWhite);
        patientNumLabel.setVisible(true);

        patientNumTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        patientNumTextInput.setVisible(true);
        patientNumTextInput
            .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        patientNumTextInput.addListener(SWT.Verify, new Listener() {
            public void handleEvent(Event e) {
                String string = e.text;
                for (int i = 0; i < string.length(); i++) {
                    // input must be alpha numeric
                    if (!(string.matches("\\p{Alnum}+"))) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });

        inventoryIdLabel = new Label(top, SWT.NO_BACKGROUND);
        inventoryIdLabel.setText("Inventory ID:");
        inventoryIdLabel.setAlignment(SWT.LEFT);
        inventoryIdLabel.setBackground(colorWhite);
        inventoryIdLabel.setVisible(true);

        inventoryIdTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        inventoryIdTextInput.setVisible(true);
        inventoryIdTextInput.setLayoutData(new GridData(
            GridData.FILL_HORIZONTAL));
        inventoryIdTextInput.addListener(SWT.Verify, new Listener() {
            public void handleEvent(Event e) {
                String string = e.text;
                for (int i = 0; i < string.length(); i++) {
                    // input must be alpha numeric
                    if (!(string.matches("\\p{Alnum}+"))) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });

        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);

        containerTypeLabel = new Label(top, SWT.NO_BACKGROUND);
        containerTypeLabel.setText("Container Type:");
        containerTypeLabel.setAlignment(SWT.LEFT);
        containerTypeLabel.setBackground(colorWhite);
        containerTypeLabel.setVisible(true);

        containerTypeCombo = new Combo(top, SWT.READ_ONLY);
        containerTypeCombo.setItems(this.loadContainerTypeList());
        containerTypeCombo.select(0);
        containerTypeCombo.setVisible(true);
        containerTypeCombo
            .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        containerLabelLabel = new Label(top, SWT.NO_BACKGROUND);
        containerLabelLabel.setText("Container Label:");
        containerLabelLabel.setAlignment(SWT.LEFT);
        containerLabelLabel.setBackground(colorWhite);
        containerLabelLabel.setVisible(true);

        containerLabelTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        containerLabelTextInput.setVisible(true);
        containerLabelTextInput.setLayoutData(new GridData(
            GridData.FILL_HORIZONTAL));
        containerLabelTextInput.addListener(SWT.Verify, new Listener() {
            public void handleEvent(Event e) {
                String string = e.text;
                for (int i = 0; i < string.length(); i++) {
                    // input must be alpha numeric
                    if (!(string.matches("\\p{Alnum}+"))) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });

        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);

        startDateLabel = new Label(top, SWT.NO_BACKGROUND);
        startDateLabel.setText("Start Date:");
        startDateLabel.setAlignment(SWT.LEFT);
        startDateLabel.setBackground(colorWhite);
        startDateLabel.setVisible(true);

        startDateCombo = new Combo(top, SWT.READ_ONLY);
        startDateCombo.setItems(this.loadStartDateList());
        startDateCombo.select(0);
        startDateCombo.setVisible(true);
        startDateCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        stopDateLabel = new Label(top, SWT.NO_BACKGROUND);
        stopDateLabel.setText("Stop Date:");
        stopDateLabel.setAlignment(SWT.LEFT);
        stopDateLabel.setBackground(colorWhite);
        stopDateLabel.setVisible(true);

        stopDateCombo = new Combo(top, SWT.READ_ONLY);
        stopDateCombo.setItems(this.loadStopDateList());
        stopDateCombo.select(0);
        stopDateCombo.setVisible(true);
        stopDateCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);

        detailsLabel = new Label(top, SWT.NO_BACKGROUND);
        detailsLabel.setText("Details:");
        detailsLabel.setAlignment(SWT.LEFT);
        detailsLabel.setBackground(colorWhite);
        detailsLabel.setVisible(true);

        detailsTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        detailsTextInput.setVisible(true);
        detailsTextInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        detailsTextInput.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                // Traverse to searchButton from detailsTextInput
                switch (e.detail) {
                case SWT.TRAVERSE_TAB_NEXT:
                case SWT.TRAVERSE_TAB_PREVIOUS: {
                    searchButton.setFocus();
                    e.doit = false;
                }
                }
            }
        });

        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);

        GridLayout gridlayoutButton = new GridLayout();
        gridlayoutButton.makeColumnsEqualWidth = false;
        gridlayoutButton.numColumns = 2;
        gridlayoutButton.verticalSpacing = 0;
        gridlayoutButton.horizontalSpacing = 5;
        gridlayoutButton.marginLeft = 0;
        gridlayoutButton.marginRight = 0;

        Composite buttonComposite = new Composite(top, SWT.NONE);
        buttonComposite.setLayout(gridlayoutButton);
        buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        buttonComposite.setBackground(colorWhite);
        buttonComposite.setVisible(true);

        clearButton = new Button(buttonComposite, SWT.PUSH);
        clearButton.setText("Clear");
        clearButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        clearButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                case SWT.Selection:
                    userCombo.select(0);
                    formCombo.select(0);
                    actionCombo.select(0);
                    patientNumTextInput.setText("");
                    inventoryIdTextInput.setText("");
                    containerTypeCombo.select(0);
                    containerLabelTextInput.setText("");
                    startDateCombo.select(0);
                    stopDateCombo.select(0);
                    detailsTextInput.setText("");
                    break;
                }
            }
        });

        searchButton = new Button(buttonComposite, SWT.PUSH);
        searchButton.setText("Search Logs");
        searchButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        searchButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                case SWT.Selection:
                    System.out.println("Search Logs");
                    FormInput input = new FormInput(null, "Logging Form Input");
                    try {
                        LogQuery.getInstance().setSearchQueryItem("user",
                            userCombo.getText());
                        LogQuery.getInstance().setSearchQueryItem("form",
                            formCombo.getText());
                        LogQuery.getInstance().setSearchQueryItem("action",
                            actionCombo.getText());
                        LogQuery.getInstance().setSearchQueryItem(
                            "patientNumber", patientNumTextInput.getText());
                        LogQuery.getInstance().setSearchQueryItem(
                            "inventoryId", inventoryIdTextInput.getText());
                        LogQuery.getInstance().setSearchQueryItem(
                            "containerType", containerTypeCombo.getText());
                        LogQuery.getInstance().setSearchQueryItem("details",
                            detailsTextInput.getText());
                        LogQuery.getInstance().setSearchQueryItem("startDate",
                            startDateCombo.getText());
                        LogQuery.getInstance().setSearchQueryItem("stopDate",
                            stopDateCombo.getText());
                        LogQuery.getInstance()
                            .setSearchQueryItem("containerLabel",
                                containerLabelTextInput.getText());

                        /* creates logging view */
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage().openEditor(input, LoggingForm.ID);
                    } catch (Exception ex) {
                        BioBankPlugin.openAsyncError("Error",
                            "There was an error opening: LoggingForm.\n"
                                + ex.getLocalizedMessage());
                    }
                    break;
                }
            }
        });
        searchButton.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                userCombo.setFocus();
                e.doit = false;
            }
        });
    }

    @Override
    public void setFocus() {
    }

    private String[] loadUserList() {
        String list[] = { "Bobby", "Margret", "James" };
        return list;
    }

    private String[] loadFormList() {
        String list[] = { "Item One", "Item Two", "Item Three", "Item Four" };
        return list;
    }

    private String[] loadActionList() {
        String list[] = { "Item One", "Item Two", "Item Three", "Item Four" };
        return list;
    }

    private String[] loadContainerTypeList() {
        String list[] = { "Item One", "Item Two", "Item Three", "Item Four" };
        return list;
    }

    private String[] loadStartDateList() {
        String list[] = { "Item One", "Item Two", "Item Three", "Item Four" };
        return list;
    }

    private String[] loadStopDateList() {
        String list[] = { "Item One", "Item Two", "Item Three", "Item Four" };
        return list;
    }
}
