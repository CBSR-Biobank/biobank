package edu.ualberta.med.biobank.views;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.LogWrapper;
import edu.ualberta.med.biobank.forms.LoggingForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.LogQuery;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LoggingView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.forms.LoggingView";

    private ISourceProviderListener siteStateListener;

    private static enum ComboListType {
        USER, TYPE, ACTION
    }

    private Composite top;

    private Label userLabel, typeLabel, actionLabel, patientNumLabel,
        inventoryIdLabel, startDateLabel, endDateLabel, detailsLabel,
        locationLabel;
    // containerTypeLabel, containerLabelLabel

    BiobankText patientNumTextInput, inventoryIdTextInput, detailsTextInput,
        locationTextInput;
    // containerLabelTextInput

    Combo userCombo, typeCombo, actionCombo;

    DateTimeWidget startDateWidget, endDateWidget;

    // containerTypeCombo

    Button clearButton, searchButton;

    private final Listener alphaNumericListener = new Listener() {
        public void handleEvent(Event e) {
            /* The user can only enter in alphanumeric */
            /* Applied to Patient#, Inventory ID, Location */
            e.text = e.text.replaceAll("^(?:(?!\\p{Alnum}+).)*$", "");
        }
    };

    private final KeyListener enterListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.keyCode == SWT.CR) {
                searchDatabase();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    public LoggingView() {
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
        userCombo.setVisible(true);
        userCombo.setFocus();
        userCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        userCombo.addKeyListener(enterListener);

        typeLabel = new Label(top, SWT.NO_BACKGROUND);
        typeLabel.setText("Type:");
        typeLabel.setAlignment(SWT.LEFT);
        typeLabel.setBackground(colorWhite);
        typeLabel.setVisible(true);

        typeCombo = new Combo(top, SWT.READ_ONLY);
        typeCombo.setVisible(true);
        typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        typeCombo.addKeyListener(enterListener);

        actionLabel = new Label(top, SWT.NO_BACKGROUND);
        actionLabel.setText("Action:");
        actionLabel.setAlignment(SWT.LEFT);
        actionLabel.setBackground(colorWhite);
        actionLabel.setVisible(true);

        actionCombo = new Combo(top, SWT.READ_ONLY);
        actionCombo.setVisible(true);
        actionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        actionCombo.addKeyListener(enterListener);

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
        patientNumTextInput.addListener(SWT.Verify, alphaNumericListener);
        patientNumTextInput.addKeyListener(enterListener);

        inventoryIdLabel = new Label(top, SWT.NO_BACKGROUND);
        inventoryIdLabel.setText("Inventory ID:");
        inventoryIdLabel.setAlignment(SWT.LEFT);
        inventoryIdLabel.setBackground(colorWhite);
        inventoryIdLabel.setVisible(true);

        inventoryIdTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        inventoryIdTextInput.setVisible(true);
        inventoryIdTextInput.setLayoutData(new GridData(
            GridData.FILL_HORIZONTAL));
        inventoryIdTextInput.addListener(SWT.Verify, alphaNumericListener);
        inventoryIdTextInput.addKeyListener(enterListener);

        locationLabel = new Label(top, SWT.NO_BACKGROUND);
        locationLabel.setText("Location:");
        locationLabel.setAlignment(SWT.LEFT);
        locationLabel.setBackground(colorWhite);
        locationLabel.setVisible(true);

        locationTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        locationTextInput.setVisible(true);
        locationTextInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        locationTextInput.addListener(SWT.Verify, alphaNumericListener);
        locationTextInput.addKeyListener(enterListener);

        /*
         * new Label(top, SWT.NONE); new Label(top, SWT.NONE);
         * 
         * containerTypeLabel = new Label(top, SWT.NO_BACKGROUND);
         * containerTypeLabel.setText("Container Type:");
         * containerTypeLabel.setAlignment(SWT.LEFT);
         * containerTypeLabel.setBackground(colorWhite);
         * containerTypeLabel.setVisible(true);
         * 
         * containerTypeCombo = new Combo(top, SWT.READ_ONLY);
         * containerTypeCombo.setItems(this.loadContainerTypeList());
         * containerTypeCombo.select(0); containerTypeCombo.setVisible(true);
         * containerTypeCombo .setLayoutData(new
         * GridData(GridData.FILL_HORIZONTAL));
         * 
         * containerLabelLabel = new Label(top, SWT.NO_BACKGROUND);
         * containerLabelLabel.setText("Container Label:");
         * containerLabelLabel.setAlignment(SWT.LEFT);
         * containerLabelLabel.setBackground(colorWhite);
         * containerLabelLabel.setVisible(true);
         * 
         * containerLabelTextInput = new BiobankText(top, SWT.SINGLE |
         * SWT.BORDER); containerLabelTextInput.setVisible(true);
         * containerLabelTextInput.setLayoutData(new GridData(
         * GridData.FILL_HORIZONTAL));
         * containerLabelTextInput.addListener(SWT.Verify, new Listener() {
         * public void handleEvent(Event e) { String string = e.text; for (int i
         * = 0; i < string.length(); i++) { // input must be alpha numeric if
         * (!(string.matches("\\p{Alnum}+"))) { e.doit = false; return; } } }
         * });
         */
        detailsLabel = new Label(top, SWT.NO_BACKGROUND);
        detailsLabel.setText("Details:");
        detailsLabel.setAlignment(SWT.LEFT);
        detailsLabel.setBackground(colorWhite);
        detailsLabel.setVisible(true);

        detailsTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
        detailsTextInput.setVisible(true);
        detailsTextInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        detailsTextInput.addKeyListener(enterListener);

        new Label(top, SWT.NONE);
        new Label(top, SWT.NONE);

        startDateLabel = new Label(top, SWT.NO_BACKGROUND);
        startDateLabel.setText("Start Date:");
        startDateLabel.setAlignment(SWT.LEFT);
        startDateLabel.setBackground(colorWhite);
        startDateLabel.setVisible(true);

        startDateWidget = new DateTimeWidget(top, SWT.DATE, null);
        startDateWidget.setBackground(colorWhite);

        endDateLabel = new Label(top, SWT.NO_BACKGROUND);
        endDateLabel.setText("End Date:");
        endDateLabel.setAlignment(SWT.LEFT);
        endDateLabel.setBackground(colorWhite);
        endDateLabel.setVisible(true);

        endDateWidget = new DateTimeWidget(top, SWT.DATE, null);
        endDateWidget.setBackground(colorWhite);

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
                    clearFields();
                }
            }
        });

        searchButton = new Button(buttonComposite, SWT.PUSH);
        searchButton.setText("Search Logs");
        searchButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        searchButton.addKeyListener(enterListener);
        searchButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                case SWT.Selection:
                    searchDatabase();
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
        setSiteManagement();
        clearFields();

        if (SessionManager.getInstance().isConnected()
            && SessionManager.getInstance().getCurrentSite() != null
            && !SessionManager.getInstance().isAllSitesSelected()) {
            setEnableAllFields(true);
            loadComboFields();
        } else
            setEnableAllFields(false);

    }

    private void setSiteManagement() {
        ISourceProvider siteSelectionStateSourceProvider = getSiteSelectionStateSourceProvider();

        siteStateListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {

                if (sourceValue == null || (Integer) sourceValue < 0) {
                    setEnableAllFields(false);
                    return;
                }

                if (sourceName.equals(SiteSelectionState.SITE_SELECTION_ID)) {
                    if (SessionManager.getInstance().isAllSitesSelected()) {
                        setEnableAllFields(false);
                    } else {
                        loadComboFields();
                        setEnableAllFields(true);
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void sourceChanged(int sourcePriority, Map sourceValuesByName) {
            }
        };

        siteSelectionStateSourceProvider
            .addSourceProviderListener(siteStateListener);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (siteStateListener != null) {
            getSiteSelectionStateSourceProvider().removeSourceProviderListener(
                siteStateListener);
        }
    }

    private ISourceProvider getSiteSelectionStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        return siteSelectionStateSourceProvider;
    }

    @Override
    public void setFocus() {
    }

    private void setEnableAllFields(boolean enabled) {
        userCombo.setEnabled(enabled);
        typeCombo.setEnabled(enabled);
        actionCombo.setEnabled(enabled);
        patientNumTextInput.setEnabled(enabled);
        inventoryIdTextInput.setEnabled(enabled);
        locationTextInput.setEnabled(enabled);
        detailsTextInput.setEnabled(enabled);
        startDateWidget.setEnabled(enabled);
        endDateWidget.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
    }

    private void loadComboFields() {
        userCombo.setItems(this.loadComboList(ComboListType.USER));
        userCombo.select(0);
        typeCombo.setItems(this.loadComboList(ComboListType.TYPE));
        typeCombo.select(0);
        actionCombo.setItems(this.loadComboList(ComboListType.ACTION));
        actionCombo.select(0);
    }

    private void clearFields() {
        userCombo.select(0);
        typeCombo.select(0);
        actionCombo.select(0);
        patientNumTextInput.setText("");
        inventoryIdTextInput.setText("");
        locationTextInput.setText("");
        detailsTextInput.setText("");

        Calendar now = new GregorianCalendar();
        Calendar dayBefore = new GregorianCalendar();
        dayBefore.setTimeInMillis(dayBefore.getTimeInMillis() - 1000 * 60 * 60
            * 24);
        startDateWidget.setDate(null);
        endDateWidget.setDate(null);

    }

    private void searchDatabase() {

        if (startDateWidget.getDate() != null
            && endDateWidget.getDate() != null
            && startDateWidget.getDate().after(endDateWidget.getDate())) {
            BioBankPlugin.openAsyncError("Error",
                "Error: start date cannot be ahead end date.");
            return;
        }

        FormInput input = new FormInput(null, "Logging Form Input");
        try {
            LogQuery.getInstance().setSearchQueryItem("user",
                userCombo.getText());
            LogQuery.getInstance().setSearchQueryItem("type",
                typeCombo.getText());
            LogQuery.getInstance().setSearchQueryItem("action",
                actionCombo.getText());
            LogQuery.getInstance().setSearchQueryItem("patientNumber",
                patientNumTextInput.getText());
            LogQuery.getInstance().setSearchQueryItem("inventoryId",
                inventoryIdTextInput.getText());
            LogQuery.getInstance().setSearchQueryItem("location",
                locationTextInput.getText());
            LogQuery.getInstance().setSearchQueryItem("details",
                detailsTextInput.getText());
            Date startDateDate = startDateWidget.getDate();
            Date endDateDate = endDateWidget.getDate();

            LogQuery.getInstance().setSearchQueryItem("startDate",
                DateFormatter.formatAsDate(startDateDate));
            LogQuery.getInstance().setSearchQueryItem("endDate",
                DateFormatter.formatAsDate(endDateDate));
            /*
             * LogQuery.getInstance().setSearchQueryItem( "containerType",
             * containerTypeCombo.getText()); LogQuery.getInstance()
             * .setSearchQueryItem("containerLabel",
             * containerLabelTextInput.getText());
             */

            /* creates logging view */
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, LoggingForm.ID);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Error",
                "There was an error opening: LoggingForm.\n"
                    + ex.getLocalizedMessage());
        }
    }

    private static String[] arrayListStringToStringList(
        List<String> listArrayString) {

        if (listArrayString == null)
            return null;

        int listArraySize = listArrayString.size();

        String listString[] = new String[listArraySize + 1];
        listString[0] = "ALL";
        for (int i = 1; i <= listArraySize; i++) {
            listString[i] = listArrayString.get(i - 1);
            listString[i] = listString[i].equals("") ? "NONE" : listString[i];
        }
        return listString;
    }

    private String[] loadComboList(ComboListType possibleList) {
        try {
            List<String> arrayList = null;

            if (!SessionManager.getInstance().isConnected()) {
                return new String[] { "ERROR" };
            }

            switch (possibleList) {
            case USER:
                arrayList = LogWrapper.getPossibleUsernames(SessionManager
                    .getAppService());
                break;

            case TYPE:
                arrayList = LogWrapper.getPossibleTypes(SessionManager
                    .getAppService());
                break;

            case ACTION:
                arrayList = LogWrapper.getPossibleActions(SessionManager
                    .getAppService());
                break;
            }
            return arrayListStringToStringList(arrayList);

        } catch (ApplicationException ex) {
            BioBankPlugin.openAsyncError("Error", "There was an error: \n"
                + ex.getLocalizedMessage());
        }
        return null;
    }

}
