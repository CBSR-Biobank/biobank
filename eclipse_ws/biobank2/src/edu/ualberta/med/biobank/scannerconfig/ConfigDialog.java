package edu.ualberta.med.biobank.scannerconfig;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.scanlib.ScanLib;
import edu.ualberta.med.scanlib.ScanLibFactory;

public class ConfigDialog extends org.eclipse.swt.widgets.Dialog {

    private Shell dialogShell;

    private Text textDpi;
    private Text textBrightness;
    private Text textContrast;
    private Text platesText[][];

    private Button buttonCancel;
    private Button buttonConfig;
    private Button buttonPreview;
    private Button twainBtn;
    private Button wiaBtn;

    private Label labels[];
    private Label platelabels[];

    private Group[] groups;

    public ConfigDialog(Shell parent, int style) {
        super(parent, style);
    }

    public void open() {
        try {
            Shell parent = getParent();
            dialogShell = new Shell(parent, SWT.DIALOG_TRIM
                | SWT.APPLICATION_MODAL);
            GridLayout dialogShellLayout = new GridLayout();
            dialogShellLayout.makeColumnsEqualWidth = true;
            groups = new Group[3 + ConfigSettings.PLATENUM + 2];
            platesText = new Text[ConfigSettings.PLATENUM + 1][4];// left,top,right,bottom
            labels = new Label[ConfigSettings.PLATENUM * 5 + 10 + 1];
            platelabels = new Label[ConfigSettings.PLATENUM * 4];
            int label_it = 0;
            int groups_it = 0;
            dialogShell.setLayout(dialogShellLayout);
            dialogShell.setText("Scanner Configuration");
            dialogShell
                .setFont(new Font(Display.getDefault(), "Tahoma", 10, 0));
            {
                groups[++groups_it] = new Group(dialogShell, SWT.NONE);
                RowLayout group1Layout = new RowLayout(
                    org.eclipse.swt.SWT.HORIZONTAL);
                groups[groups_it].setLayout(group1Layout);
                GridData group1LData = new GridData();
                group1LData.widthHint = 140;
                group1LData.heightHint = 21;
                groups[groups_it].setLayoutData(group1LData);
                groups[groups_it].setText("Driver Type:");
                RowData rd = new RowData();
                {
                    twainBtn = new Button(groups[groups_it], SWT.RADIO
                        | SWT.LEFT);
                    twainBtn.setLayoutData(rd);
                    twainBtn.setText("Twain");
                    twainBtn.setSelection(true);
                    twainBtn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseUp(MouseEvent evt) {
                            for (int i = 0; i < ConfigSettings.PLATENUM; i++) {
                                platelabels[4 * i + 2].setText("     Bottom:");
                                platelabels[4 * i + 3].setText("      Right:");
                            }
                        }
                    });

                    wiaBtn = new Button(groups[groups_it], SWT.RADIO | SWT.LEFT);
                    wiaBtn.setLayoutData(rd);
                    wiaBtn.setText("Wia");
                    wiaBtn.setSelection(false);
                    wiaBtn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseUp(MouseEvent evt) {
                            for (int i = 0; i < ConfigSettings.PLATENUM; i++) {
                                platelabels[4 * i + 2].setText("    Height:");
                                platelabels[4 * i + 3].setText("     Width:");
                            }
                        }
                    });
                }
            }
            {
                groups[++groups_it] = new Group(dialogShell, SWT.NONE);
                FillLayout group1Layout = new FillLayout(
                    org.eclipse.swt.SWT.HORIZONTAL);
                GridData group1LData = new GridData();
                group1LData.widthHint = 421;
                group1LData.heightHint = 16;
                groups[groups_it].setLayoutData(group1LData);
                groups[groups_it].setLayout(group1Layout);
                groups[groups_it].setText("Scanner Settings");
                {
                    labels[++label_it] = new Label(groups[groups_it], SWT.NONE);
                    labels[label_it].setText("Dots Per Inch:");
                }
                {
                    textDpi = new Text(groups[groups_it], SWT.BORDER);
                    textDpi.setTextLimit(3);
                    textDpi.setText("300");
                }
                {
                    labels[++label_it] = new Label(groups[groups_it], SWT.NONE);
                    labels[label_it].setText("    Brightness:");
                    labels[label_it].setBounds(12, 21, 58, 13);
                }
                {
                    textBrightness = new Text(groups[groups_it], SWT.BORDER);
                    textBrightness.setText("0");
                    textBrightness.setTextLimit(5);
                    textBrightness.setBounds(68, 22, 33, 14);
                }
                {
                    labels[++label_it] = new Label(groups[groups_it], SWT.NONE);
                    labels[label_it].setText("       Contrast:");
                    labels[label_it].setBounds(105, 22, 49, 13);
                }
                {
                    textContrast = new Text(groups[groups_it], SWT.BORDER);
                    textContrast.setText("0");
                    textContrast.setTextLimit(5);
                    textContrast.setBounds(153, 22, 35, 20);
                }
            }
            for (int plate = 0; plate < ConfigSettings.PLATENUM; plate++) {
                groups[++groups_it] = new Group(dialogShell, SWT.NONE);
                FillLayout group2Layout = new FillLayout(
                    org.eclipse.swt.SWT.HORIZONTAL);
                groups[groups_it].setLayout(group2Layout);
                GridData group2LData = new GridData();
                group2LData.heightHint = 16;
                group2LData.horizontalAlignment = GridData.FILL;
                groups[groups_it].setLayoutData(group2LData);
                groups[groups_it].setText(String.format("Plate %d Position",
                    plate + 1));
                {
                    platelabels[4 * plate + 0] = new Label(groups[groups_it],
                        SWT.NONE);
                    platelabels[4 * plate + 0].setText("Top:");
                }
                {
                    platesText[plate][1] = new Text(groups[groups_it],
                        SWT.BORDER);
                    platesText[plate][1].setText("0");
                    platesText[plate][1].setTextLimit(6);
                    platesText[plate][1].setOrientation(SWT.HORIZONTAL);
                    platesText[plate][1].setDoubleClickEnabled(false);

                }
                {
                    platelabels[4 * plate + 1] = new Label(groups[groups_it],
                        SWT.NONE);
                    platelabels[4 * plate + 1].setText("      Left:");
                }
                {
                    platesText[plate][0] = new Text(groups[groups_it],
                        SWT.BORDER);
                    platesText[plate][0].setText("0");
                    platesText[plate][0].setTextLimit(6);
                }
                {
                    platelabels[4 * plate + 2] = new Label(groups[groups_it],
                        SWT.NONE);
                    platelabels[4 * plate + 2].setText("   Bottom:");
                }
                {
                    platesText[plate][3] = new Text(groups[groups_it],
                        SWT.BORDER);
                    platesText[plate][3].setText("0");
                    platesText[plate][3].setTextLimit(6);
                }
                {
                    platelabels[4 * plate + 3] = new Label(groups[groups_it],
                        SWT.NONE);
                    platelabels[4 * plate + 3].setText("    Right:");
                }
                {
                    platesText[plate][2] = new Text(groups[groups_it],
                        SWT.BORDER);
                    platesText[plate][2].setText("0");
                    platesText[plate][2].setTextLimit(6);
                }
                {
                    buttonPreview = new Button(groups[groups_it], SWT.PUSH);
                    buttonPreview.setText("Edit");
                    if (plate < ConfigSettings.getInstance().getPlatemode()) {
                        switch (plate + 1) {// TODO find a better way of doing
                        // this (BELOW)
                        case (1):
                            buttonPreview.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseUp(MouseEvent evt) {
                                    buttonPlateImageDialog(1);
                                }
                            });
                            break;
                        case (2):
                            buttonPreview.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseUp(MouseEvent evt) {
                                    buttonPlateImageDialog(2);
                                }
                            });
                            break;
                        case (3):
                            buttonPreview.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseUp(MouseEvent evt) {
                                    buttonPlateImageDialog(3);
                                }
                            });
                            break;
                        case (4):
                            buttonPreview.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseUp(MouseEvent evt) {
                                    buttonPlateImageDialog(4);
                                }
                            });
                            break;
                        default:
                            break;
                        }
                    }
                }
                {
                    Color c;
                    switch (plate) {
                    case (0):
                        c = new Color(Display.getDefault(), 185, 255, 185);
                        break;
                    case (1):
                        c = new Color(Display.getDefault(), 255, 185, 185);
                        break;
                    case (2):
                        c = new Color(Display.getDefault(), 255, 255, 185);
                        break;
                    case (3):
                        c = new Color(Display.getDefault(), 170, 255, 255);
                        break;
                    default:
                        c = new Color(Display.getDefault(), 255, 255, 255);
                        break;
                    }
                    for (int i = 0; i < 4; i++) {
                        if (plate >= ConfigSettings.getInstance()
                            .getPlatemode()) {
                            platesText[plate][i].setBackground(new Color(
                                Display.getDefault(), 0, 0, 0));
                            platesText[plate][i].setEnabled(false);
                        } else {
                            platesText[plate][i].setBackground(c);
                        }
                    }
                }

            }
            {
                groups[++groups_it] = new Group(dialogShell, SWT.NONE);
                RowLayout group5Layout = new RowLayout(
                    org.eclipse.swt.SWT.HORIZONTAL);
                groups[groups_it].setLayout(group5Layout);
                GridData group5LData = new GridData();
                groups[groups_it].setLayoutData(group5LData);
                {
                    buttonConfig = new Button(groups[groups_it], SWT.PUSH
                        | SWT.CENTER);
                    buttonConfig.setText(" Apply ");
                    buttonConfig.setSize(250, 70);
                    buttonConfig.setFont(new Font(Display.getDefault(),
                        "Tahoma", 10, SWT.BOLD));
                    RowData buttonConfigLData = new RowData();
                    buttonConfig.setLayoutData(buttonConfigLData);
                    buttonConfig.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseUp(MouseEvent evt) {
                            buttonConfigMouseUp();
                        }
                    });
                }

                {
                    buttonCancel = new Button(groups[groups_it], SWT.PUSH
                        | SWT.CENTER);
                    buttonCancel.setText(" Cancel ");
                    RowData button1LData = new RowData();
                    buttonCancel.setSize(250, 70);
                    buttonCancel.setLayoutData(button1LData);
                    buttonCancel.setFont(new Font(Display.getDefault(),
                        "Tahoma", 10, 0));
                    buttonCancel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseUp(MouseEvent evt) {
                            buttonCancelMouseUp();
                        }
                    });
                }
            }
            this.loadFromConfigSettings();
            dialogShell.layout();
            dialogShell.pack();
            dialogShell.setLocation(getParent().toDisplay(100, 100));
            dialogShell.open();
            Display display = dialogShell.getDisplay();

            while (!dialogShell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void readPlatesIntoArray(double plateArray[][]) {
        for (int plate = 0; plate < ConfigSettings.PLATENUM; plate++) {
            for (int side = 0; side < 4; side++) {
                plateArray[plate][side] = Double
                    .valueOf(platesText[plate][side].getText());
            }
        }
    }

    private int loadFromConfigSettings() {
        ConfigSettings configSettings = ConfigSettings.getInstance();
        configSettings.loadFromFile();

        if (configSettings.getDriverType().equals("WIA")) {
            twainBtn.setSelection(false);
            wiaBtn.setSelection(true);

            for (int i = 0; i < ConfigSettings.PLATENUM; i++) {
                platelabels[4 * i + 2].setText("    Height:");
                platelabels[4 * i + 3].setText("     Width:");
            }

        } else {
            twainBtn.setSelection(true);
            wiaBtn.setSelection(false);
        }

        textDpi.setText(String.valueOf(configSettings.getDpi()));
        textBrightness.setText(String.valueOf(configSettings.getBrightness()));
        textContrast.setText(String.valueOf(configSettings.getContrast()));

        for (int plate = 0; plate < ConfigSettings.PLATENUM; plate++) {
            for (int side = 0; side < 4; side++) {
                platesText[plate][side].setText(String.valueOf(configSettings
                    .getPlate(plate + 1)[side]));
            }
        }
        return 0;
    }

    private void buttonCancelMouseUp() {
        dialogShell.dispose();
    }

    private void buttonPlateImageDialog(int plate) {
        double nplates[][] = new double[ConfigSettings.PLATENUM][4];
        readPlatesIntoArray(nplates);
        if (!(new File(PlateImageDialog.alignFile).exists())
            || MessageDialog.openConfirm(dialogShell, "Re-Scan Plate Image?",
                "If you have moved the plates since the last calibration press OK.\n"
                    + "")) {
            ScanLibFactory.getScanLib().slScanImage(
                (int) PlateImageDialog.alignDpi, 0, 0, 0, 0,
                PlateImageDialog.alignFile);
        }

        PlateImageDialog pid = new PlateImageDialog(dialogShell, SWT.NONE);
        Color c;
        switch (plate - 1) {
        case (0):
            c = new Color(Display.getDefault(), 0, 0xFF, 0);
            break;
        case (1):
            c = new Color(Display.getDefault(), 0xFF, 0, 0);
            break;
        case (2):
            c = new Color(Display.getDefault(), 0xFF, 0xFF, 0);
            break;
        case (3):
            c = new Color(Display.getDefault(), 0, 0xFF, 0xFF);
            break;
        default:
            c = new Color(Display.getDefault(), 0xFF, 0xFF, 0xFF);
            break;
        }

        double plateset[] = pid.open(nplates[plate - 1], twainBtn
            .getSelection(), c);

        for (int i = 0; i < 4; i++) {
            platesText[plate - 1][i].setText(String.valueOf(plateset[i]));
        }
        dialogShell.redraw();
    }

    private void errorMsg(String Identifier, int code) {
        MessageDialog.openError(dialogShell, "Error", String.format(
            "%s\nReturned Error Code: %d\n", Identifier, code));
    }

    private void buttonConfigMouseUp() {
        int configSettingsReturn;
        try {
            ConfigSettings configSettings = ConfigSettings.getInstance();
            if (twainBtn.getSelection()) {
                configSettingsReturn = configSettings.setDriverType("TWAIN");
            } else if (wiaBtn.getSelection()) {
                configSettingsReturn = configSettings.setDriverType("WIA");
            } else {
                configSettingsReturn = ConfigSettings.CS_INVALID_INPUT;
            }
            switch (configSettingsReturn) {
            case (ConfigSettings.CS_SUCCESS):
                break;
            case (ConfigSettings.CS_NOCHANGE):
                break;
            case (ConfigSettings.CS_INVALID_INPUT):
                MessageDialog.openError(dialogShell,
                    "configSettings.setDriverType",
                    "Please enter a valid input");
                return;

            case (ConfigSettings.CS_FILE_ERROR):
                MessageDialog.openError(dialogShell,
                    "configSettings.setDriverType",
                    "Could not find scanlib.ini file");
                dialogShell.dispose();
                return;
            }

            configSettingsReturn = configSettings.setDpi(textDpi.getText());
            switch (configSettingsReturn) {
            case (ConfigSettings.CS_SUCCESS):
                break;
            case (ConfigSettings.CS_FILE_ERROR):
                MessageDialog.openError(dialogShell, "configSettings.setDpi",
                    "Could not find scanlib.ini file");
                dialogShell.dispose();
                return;
            case (ConfigSettings.CS_INVALID_INPUT):
                MessageDialog.openError(dialogShell, "configSettings.setDpi",
                    "Dpi must be greater than 0 and no greater than 600");
                return;
            case (ConfigSettings.CS_NOCHANGE):
                break;
            }
            configSettingsReturn = configSettings.setBrightness(textBrightness
                .getText());
            switch (configSettingsReturn) {
            case (ConfigSettings.CS_SUCCESS):
                int scanlibReturn = ScanLibFactory.getScanLib()
                    .slConfigScannerBrightness(configSettings.getBrightness());
                switch (scanlibReturn) {
                case (ScanLib.SC_SUCCESS):
                    break;
                case (ScanLib.SC_INVALID_VALUE):
                    this.errorMsg("Brightness: Invalid Value", scanlibReturn);
                    dialogShell.dispose();
                    return;

                case (ScanLib.SC_INI_FILE_ERROR):
                    this.errorMsg(
                        "Brightness: Could not find scanlib.ini file",
                        scanlibReturn);
                    dialogShell.dispose();
                    return;
                }
                break;
            case (ConfigSettings.CS_INVALID_INPUT):
                MessageDialog.openError(dialogShell,
                    "configSettings.setBrightness",
                    "Brightness ranges from -1000 to 1000\n"
                        + "Please enter a valid input value");
                return;
            case (ConfigSettings.CS_NOCHANGE):
                break;
            }

            configSettingsReturn = configSettings.setContrast(textContrast
                .getText());
            switch (configSettingsReturn) {
            case (ConfigSettings.CS_SUCCESS):
                int scanlibReturn = ScanLibFactory.getScanLib()
                    .slConfigScannerContrast(configSettings.getContrast());
                switch (scanlibReturn) {
                case (ScanLib.SC_SUCCESS):
                    break;
                case (ScanLib.SC_INVALID_VALUE):
                    this.errorMsg("Contrast: Invalid Value", scanlibReturn);
                    dialogShell.dispose();
                    return;

                case (ScanLib.SC_INI_FILE_ERROR):
                    this.errorMsg("Contrast: Could not find scanlib.ini file",
                        scanlibReturn);
                    dialogShell.dispose();
                    return;
                }
                break;
            case (ConfigSettings.CS_INVALID_INPUT):
                MessageDialog.openError(dialogShell,
                    "configSettings.setContrast",
                    "Contrast ranges from -1000 to 1000\n"
                        + "Please enter a valid input value");
                return;
            case (ConfigSettings.CS_NOCHANGE):
                break;
            }

            double nplates[][] = new double[ConfigSettings.PLATENUM][4];
            readPlatesIntoArray(nplates);

            for (int plate = 0; plate < ConfigSettings.PLATENUM; plate++) {
                int setPlateReturn = configSettings.setPlate(plate + 1,
                    nplates[plate][0], nplates[plate][1], nplates[plate][2],
                    nplates[plate][3]);
                if (setPlateReturn == ConfigSettings.CS_SUCCESS
                    || setPlateReturn == ConfigSettings.CS_CLEARDATA) {
                    int scanlibReturn = ScanLibFactory.getScanLib()
                        .slConfigPlateFrame(plate + 1,
                            configSettings.getPlate(plate + 1)[0],
                            configSettings.getPlate(plate + 1)[1],
                            configSettings.getPlate(plate + 1)[2],
                            configSettings.getPlate(plate + 1)[3]);
                    switch (scanlibReturn) {
                    case (ScanLib.SC_SUCCESS):
                        break;
                    case (ScanLib.SC_INI_FILE_ERROR):
                        this
                            .errorMsg(
                                "ConfigPlateFrame: Could not find scanlib.ini file",
                                scanlibReturn);
                        dialogShell.dispose();
                        return;
                    }

                    if (setPlateReturn == ConfigSettings.CS_SUCCESS) {
                        scanlibReturn = ScanLibFactory.getScanLib()
                            .slCalibrateToPlate(configSettings.getDpi(),
                                plate + 1);
                        if (scanlibReturn != ScanLib.SC_SUCCESS) {
                            ScanLibFactory.getScanLib().slConfigPlateFrame(
                                plate + 1, 0, 0, 0, 0);
                            dialogShell.redraw();
                        }
                        switch (scanlibReturn) {

                        case (ScanLib.SC_SUCCESS):
                            MessageDialog.openInformation(dialogShell,
                                "Successful Calibration", String.format(
                                    "Plate %d has been successfully setup",
                                    plate + 1));
                            break;
                        case (ScanLib.SC_INVALID_DPI):
                            this.errorMsg("Calibratation: Invalid Dpi",
                                scanlibReturn);
                            return;
                        case (ScanLib.SC_INVALID_PLATE_NUM):
                            this.errorMsg(
                                "Calibratation: Invalid Plate Number",
                                scanlibReturn);
                            dialogShell.dispose();
                            return;

                        case (ScanLib.SC_INI_FILE_ERROR):
                            this
                                .errorMsg(
                                    "Calibratation: Plate dimensions not found inside scanlib.ini",
                                    scanlibReturn);
                            dialogShell.dispose();
                            return;
                        case (ScanLib.SC_CALIBRATOR_ERROR):
                            this
                                .errorMsg(
                                    "Calibration: Could not find 8 rows and 12 columns",
                                    scanlibReturn);
                            for (int i = 0; i < 4; i++) {
                                platesText[plate][i].setBackground(new Color(
                                    Display.getDefault(), 0x44, 0x44, 0x44));
                            }
                            return;
                        case (ScanLib.SC_CALIBRATOR_NO_REGIONS):
                            this
                                .errorMsg(
                                    "menuConfigurationWidgetSelected, Calibratation",
                                    scanlibReturn);
                            return;
                        case (ScanLib.SC_FAIL):
                            this.errorMsg("Calibratation: Failed to scan",
                                scanlibReturn);
                            dialogShell.dispose();
                            return;

                        }
                    }
                } else if (setPlateReturn == ConfigSettings.CS_INVALID_INPUT) {
                    MessageDialog
                        .openError(dialogShell, "configSettings.setPlate",
                            "Please enter a valid input");
                    return;
                }

            }
            dialogShell.dispose();
        } catch (Exception e) {
            MessageDialog.openError(dialogShell, "Error", e.getMessage());
        }

    }
}
