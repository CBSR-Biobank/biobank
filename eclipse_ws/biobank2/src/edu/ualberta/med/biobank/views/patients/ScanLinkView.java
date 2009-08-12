package edu.ualberta.med.biobank.views.patients;

import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.widgets.AddSamplesScanPalletWidget;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ScanLinkView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.patients.ScanLink";
    private Composite top = null;
    private Label labelPatientNumber;
    private Text textPatientNumber;
    private Label labelPlateToScan;
    private Text textPlateToScan;
    private Label labelVisit;
    private CCombo comboVisits;
    private ComboViewer viewerVisits;
    private Button buttonScan;

    private AddSamplesScanPalletWidget spw;

    private WritableApplicationService appService;

    private Patient currentPatient;

    @Override
    public void createPartControl(Composite parent) {
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 2;
        top = new Composite(parent, SWT.NONE);
        createCompositeFields();
        top.setLayout(gridLayout1);
        createCompositeVisualisation();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /**
     * This method initializes compositeFields
     * 
     */
    private void createCompositeFields() {
        Composite compositeFields = new Composite(top, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        compositeFields.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = 300;
        compositeFields.setLayoutData(gridData);

        labelPatientNumber = new Label(compositeFields, SWT.NONE);
        labelPatientNumber.setText("Patient Number:");
        textPatientNumber = new Text(compositeFields, SWT.BORDER);
        textPatientNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 13) {
                    setVisitsList();
                }
            }
        });
        textPatientNumber.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setVisitsList();
            }
        });

        labelVisit = new Label(compositeFields, SWT.NONE);
        labelVisit.setText("Visit:");

        createVisitCombo(compositeFields);

        labelPlateToScan = new Label(compositeFields, SWT.NONE);
        labelPlateToScan.setText("Plate to scan:");
        textPlateToScan = new Text(compositeFields, SWT.BORDER);

        buttonScan = new Button(compositeFields, SWT.NONE);
        buttonScan.setText("Scan");
    }

    private void createVisitCombo(Composite compositeFields) {
        comboVisits = new CCombo(compositeFields, SWT.READ_ONLY | SWT.BORDER
            | SWT.FLAT);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        comboVisits.setLayoutData(gridData);

        viewerVisits = new ComboViewer(comboVisits);
        viewerVisits.setContentProvider(new ArrayContentProvider());
        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisit pv = (PatientVisit) element;
                return BioBankPlugin.getDateTimeFormatter().format(
                    pv.getDateDrawn());
            }
        });
    }

    protected void setVisitsList() {
        String pNumber = textPatientNumber.getText();
        currentPatient = ModelUtils.getObjectWithAttr(getAppService(),
            Patient.class, "number", String.class, pNumber);
        if (currentPatient != null) {
            // show visits list
            Collection<PatientVisit> collection = currentPatient
                .getPatientVisitCollection();
            viewerVisits.setInput(collection);
            comboVisits.setEnabled(true);
            if (collection.size() == 1) {
                // comboVisi/ts.getSelectionIndex()Selection(new Point())
            }
        }
    }

    /**
     * This method initializes compositeVisualisation
     * 
     */
    private void createCompositeVisualisation() {
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        gridData1.verticalAlignment = GridData.FILL;
        Composite compositeVisualisation = new Composite(top, SWT.NONE);
        compositeVisualisation.setLayout(new GridLayout());
        compositeVisualisation.setLayoutData(gridData1);
        spw = new AddSamplesScanPalletWidget(compositeVisualisation);
        spw.setVisible(true);
        spw.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
    }

    private WritableApplicationService getAppService() {
        if (appService == null) {
            appService = SessionManager.getInstance().getSessionAdapter()
                .getAppService();
        }
        return appService;
    }

}
