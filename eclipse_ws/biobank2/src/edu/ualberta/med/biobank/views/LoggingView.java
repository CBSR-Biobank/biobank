package edu.ualberta.med.biobank.views;

import java.util.ArrayList;
import java.util.Date;
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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.common.wrappers.LogWrapper;
import edu.ualberta.med.biobank.forms.LoggingForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.LogQuery;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LoggingView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.LoggingView";

    private ISourceProviderListener siteStateListener;

    private static enum ComboListType {
        CENTER, USER, TYPE, ACTION
    }

    private BiobankText patientNumTextInput, inventoryIdTextInput,
        detailsTextInput, locationTextInput;

    private Combo centerCombo, userCombo, typeCombo, actionCombo;

    private DateTimeWidget startDateWidget, endDateWidget;

    private Button clearButton, searchButton;

    private Color colorWhite;

    private String[] siteComboOptions;

    private final Listener alphaNumericListener = new Listener() {
        @Override
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

    @Override
    public void createPartControl(Composite parent) {
        colorWhite = new Color(parent.getDisplay(), 255, 255, 255);

        GridLayout gridlayout = new GridLayout(2, false);
        gridlayout.verticalSpacing = 2;
        gridlayout.horizontalSpacing = 5;
        gridlayout.marginLeft = 1;
        gridlayout.marginRight = 5;
        gridlayout.marginBottom = 10;

        parent.setLayout(gridlayout);
        parent.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        parent.setBackground(colorWhite);

        Label label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Center:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        centerCombo = new Combo(parent, SWT.READ_ONLY);
        centerCombo.setFocus();
        centerCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        centerCombo.addKeyListener(enterListener);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("User:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        userCombo = new Combo(parent, SWT.READ_ONLY);
        userCombo.setFocus();
        userCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        userCombo.addKeyListener(enterListener);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Type:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        typeCombo = new Combo(parent, SWT.READ_ONLY);
        typeCombo.setVisible(true);
        typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        typeCombo.addKeyListener(enterListener);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Action:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        actionCombo = new Combo(parent, SWT.READ_ONLY);
        actionCombo.setVisible(true);
        actionCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        actionCombo.addKeyListener(enterListener);

        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Patient #:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        patientNumTextInput = new BiobankText(parent, SWT.SINGLE | SWT.BORDER);
        patientNumTextInput.setVisible(true);
        patientNumTextInput
            .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        patientNumTextInput.addListener(SWT.Verify, alphaNumericListener);
        patientNumTextInput.addKeyListener(enterListener);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Inventory ID:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        inventoryIdTextInput = new BiobankText(parent, SWT.SINGLE | SWT.BORDER);
        inventoryIdTextInput.setVisible(true);
        inventoryIdTextInput.setLayoutData(new GridData(
            GridData.FILL_HORIZONTAL));
        inventoryIdTextInput.addListener(SWT.Verify, alphaNumericListener);
        inventoryIdTextInput.addKeyListener(enterListener);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Location:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        locationTextInput = new BiobankText(parent, SWT.SINGLE | SWT.BORDER);
        locationTextInput.setVisible(true);
        locationTextInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        locationTextInput.addListener(SWT.Verify, alphaNumericListener);
        locationTextInput.addKeyListener(enterListener);

        /*
         * new Label(top, SWT.NONE); new Label(top, SWT.NONE);
         * 
         * label = new Label(top, SWT.NO_BACKGROUND);
         * label.setText("Container Type:"); label.setAlignment(SWT.LEFT);
         * 
         * containerTypeCombo = new Combo(top, SWT.READ_ONLY);
         * containerTypeCombo.setItems(this.loadContainerTypeList());
         * containerTypeCombo.select(0); containerTypeCombo.setVisible(true);
         * containerTypeCombo .setLayoutData(new
         * GridData(GridData.FILL_HORIZONTAL));
         * 
         * label = new Label(top, SWT.NO_BACKGROUND);
         * label.setText("Container Label:"); label.setAlignment(SWT.LEFT);
         * 
         * labelTextInput = new BiobankText(top, SWT.SINGLE | SWT.BORDER);
         * labelTextInput.setVisible(true); labelTextInput.setLayoutData(new
         * GridData( GridData.FILL_HORIZONTAL));
         * labelTextInput.addListener(SWT.Verify, new Listener() { public void
         * handleEvent(Event e) { String string = e.text; for (int i = 0; i <
         * string.length(); i++) { // input must be alpha numeric if
         * (!(string.matches("\\p{Alnum}+"))) { e.doit = false; return; } } }
         * });
         */
        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Details:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        detailsTextInput = new BiobankText(parent, SWT.SINGLE | SWT.BORDER);
        detailsTextInput.setVisible(true);
        detailsTextInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        detailsTextInput.addKeyListener(enterListener);

        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("Start Date:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        startDateWidget = new DateTimeWidget(parent, SWT.DATE, null);
        startDateWidget.setBackground(colorWhite);

        label = new Label(parent, SWT.NO_BACKGROUND);
        label.setText("End Date:");
        label.setAlignment(SWT.LEFT);
        label.setBackground(colorWhite);

        endDateWidget = new DateTimeWidget(parent, SWT.DATE, null);
        endDateWidget.setBackground(colorWhite);

        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);

        GridLayout gridlayoutButton = new GridLayout();
        gridlayoutButton.makeColumnsEqualWidth = false;
        gridlayoutButton.numColumns = 2;
        gridlayoutButton.verticalSpacing = 0;
        gridlayoutButton.horizontalSpacing = 5;
        gridlayoutButton.marginLeft = 0;
        gridlayoutButton.marginRight = 0;

        Composite buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setLayout(gridlayoutButton);
        buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        buttonComposite.setBackground(colorWhite);
        buttonComposite.setVisible(true);

        clearButton = new Button(buttonComposite, SWT.PUSH);
        clearButton.setText("Clear");
        clearButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        clearButton.addListener(SWT.Selection, new Listener() {
            @Override
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
            @Override
            public void handleEvent(Event e) {
                switch (e.type) {
                case SWT.Selection:
                    searchDatabase();
                    break;
                }
            }
        });
        searchButton.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                centerCombo.setFocus();
                e.doit = false;
            }
        });

        sessionMonitor();
        clearFields();

        // if logged in and select the site selected in "working site" combo
        // box, only if not "All Sites" are selected
        if (SessionManager.getInstance().isConnected()) {
            setEnableAllFields(true);
            loadComboFields();
        } else
            setEnableAllFields(false);
    }

    // monitors the logged in / out state
    private void sessionMonitor() {
        ISourceProvider siteSelectionStateSourceProvider = getSessionStateSourceProvider();

        siteStateListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceValue.equals(SessionState.LOGGED_OUT)) {
                    setEnableAllFields(false);
                } else if (sourceValue.equals(SessionState.LOGGED_IN)) {
                    loadComboFields();
                    setEnableAllFields(true);
                }
            }

            @SuppressWarnings("rawtypes")
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
            getSessionStateSourceProvider().removeSourceProviderListener(
                siteStateListener);
        }
    }

    private ISourceProvider getSessionStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SessionState.LOGIN_STATE_SOURCE_NAME);
        return siteSelectionStateSourceProvider;
    }

    @Override
    public void setFocus() {
    }

    private void setEnableAllFields(boolean enabled) {
        centerCombo.setEnabled(enabled);
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
        siteComboOptions = loadComboList(ComboListType.CENTER);
        centerCombo.setItems(siteComboOptions);
        centerCombo.select(0);
        userCombo.setItems(loadComboList(ComboListType.USER));
        userCombo.select(0);
        typeCombo.setItems(loadComboList(ComboListType.TYPE));
        typeCombo.select(0);
        actionCombo.setItems(loadComboList(ComboListType.ACTION));
        actionCombo.select(0);
    }

    private void clearFields() {
        centerCombo.select(0);
        userCombo.select(0);
        typeCombo.select(0);
        actionCombo.select(0);
        patientNumTextInput.setText("");
        inventoryIdTextInput.setText("");
        locationTextInput.setText("");
        detailsTextInput.setText("");
        startDateWidget.setDate(null);
        endDateWidget.setDate(null);

    }

    private void searchDatabase() {

        if (startDateWidget.getDate() != null
            && endDateWidget.getDate() != null
            && startDateWidget.getDate().after(endDateWidget.getDate())) {
            BiobankPlugin.openAsyncError("Error",
                "Error: start date cannot be ahead end date.");
            return;
        }

        FormInput input = new FormInput(null, "Logging Form Input");
        try {
            LogQuery.getInstance().setSearchQueryItem(LogPeer.CENTER.getName(),
                centerCombo.getText());
            LogQuery.getInstance().setSearchQueryItem(
                LogPeer.USERNAME.getName(), userCombo.getText());
            LogQuery.getInstance().setSearchQueryItem(LogPeer.TYPE.getName(),
                typeCombo.getText());
            LogQuery.getInstance().setSearchQueryItem(LogPeer.ACTION.getName(),
                actionCombo.getText());
            LogQuery.getInstance()
                .setSearchQueryItem(LogPeer.PATIENT_NUMBER.getName(),
                    patientNumTextInput.getText());
            LogQuery.getInstance().setSearchQueryItem(
                LogPeer.INVENTORY_ID.getName(), inventoryIdTextInput.getText());
            LogQuery.getInstance().setSearchQueryItem(
                LogPeer.LOCATION_LABEL.getName(), locationTextInput.getText());
            LogQuery.getInstance().setSearchQueryItem(
                LogPeer.DETAILS.getName(), detailsTextInput.getText());
            Date startDateDate = startDateWidget.getDate();
            Date endDateDate = endDateWidget.getDate();

            String startDateStr = DateFormatter.formatAsDate(startDateDate);
            if (startDateStr != null && startDateStr.length() > 0) {
                LogQuery.getInstance().setSearchQueryItem(
                    LogQuery.START_DATE_KEY, startDateStr);
            }

            String endDateStr = DateFormatter.formatAsDate(endDateDate);
            if (endDateStr != null && endDateStr.length() > 0) {
                LogQuery.getInstance().setSearchQueryItem(
                    LogQuery.END_DATE_KEY, endDateStr);
            }
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
            BiobankPlugin.openAsyncError("Error",
                "There was an error opening: LoggingForm.", ex);
        }
    }

    private String[] loadComboList(ComboListType possibleList) {
        try {
            List<String> arrayList = null;

            if (!SessionManager.getInstance().isConnected()) {
                return new String[] { "ERROR" };
            }

            switch (possibleList) {
            case CENTER:
                arrayList = LogWrapper.getPossibleCenters(SessionManager
                    .getAppService());
                break;

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
            arrayList.remove(null);
            List<String> result = new ArrayList<String>();
            result.add(LogQuery.ALL);
            result.add(LogQuery.NONE);
            for (String item : arrayList) {
                if (item != null)
                    result.add(item);
            }
            return result.toArray(new String[0]);

        } catch (ApplicationException ex) {
            BiobankPlugin.openAsyncError("Error",
                "There was an error loading combo values.", ex);
        }
        return null;
    }

}
