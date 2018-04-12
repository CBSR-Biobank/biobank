package edu.ualberta.med.scannerconfig.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class AdvancedRadioGroupFieldEditor extends FieldEditor {

    private String[][] labelsAndValues;

    private int numColumns;

    private int indent = HORIZONTAL_GAP;

    private String value;

    private Composite radioBox;

    private Button[] radioButtons;

    private boolean useGroup;

    protected AdvancedRadioGroupFieldEditor() {
    }

    public AdvancedRadioGroupFieldEditor(String name, String labelText,
        int numColumns, String[][] labelAndValues, Composite parent) {
        this(name, labelText, numColumns, labelAndValues, parent, false);
    }

    public AdvancedRadioGroupFieldEditor(String name, String labelText,
        int numColumns, String[][] labelAndValues, Composite parent,
        boolean useGroup) {
        init(name, labelText);
        Assert.isTrue(checkArray(labelAndValues));
        this.labelsAndValues = labelAndValues;
        this.numColumns = numColumns;
        this.useGroup = useGroup;
        createControl(parent);
    }

    @Override
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        if (control != null) {
            ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        }
        ((GridData) radioBox.getLayoutData()).horizontalSpan = numColumns;
    }

    private boolean checkArray(String[][] table) {
        if (table == null) {
            return false;
        }
        for (int i = 0; i < table.length; i++) {
            String[] array = table[i];
            if (array == null || array.length != 2) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        if (useGroup) {
            Control control = getRadioBoxControl(parent);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            control.setLayoutData(gd);
        } else {
            Control control = getLabelControl(parent);
            GridData gd = new GridData();
            gd.horizontalSpan = numColumns;
            control.setLayoutData(gd);
            control = getRadioBoxControl(parent);
            gd = new GridData();
            gd.horizontalSpan = numColumns;
            gd.horizontalIndent = indent;
            control.setLayoutData(gd);
        }

    }

    @Override
    public void doLoad() {
        updateValue(getPreferenceStore().getString(getPreferenceName()));
    }

    @Override
    protected void doLoadDefault() {
        updateValue(getPreferenceStore().getDefaultString(getPreferenceName()));
    }

    @Override
    protected void doStore() {
        if (value == null) {
            getPreferenceStore().setToDefault(getPreferenceName());
            return;
        }

        getPreferenceStore().setValue(getPreferenceName(), value);
    }

    @Override
    public int getNumberOfControls() {
        return 1;
    }

    public Composite getRadioBoxControl(Composite parent) {
        if (radioBox == null) {

            Font font = parent.getFont();

            if (useGroup) {
                Group group = new Group(parent, SWT.NONE);
                group.setFont(font);
                String text = getLabelText();
                if (text != null) {
                    group.setText(text);
                }
                radioBox = group;
                GridLayout layout = new GridLayout();
                layout.horizontalSpacing = HORIZONTAL_GAP;
                layout.numColumns = numColumns;
                radioBox.setLayout(layout);
            } else {
                radioBox = new Composite(parent, SWT.NONE);
                GridLayout layout = new GridLayout();
                layout.marginWidth = 0;
                layout.marginHeight = 0;
                layout.horizontalSpacing = HORIZONTAL_GAP;
                layout.numColumns = numColumns;
                radioBox.setLayout(layout);
                radioBox.setFont(font);
            }

            radioButtons = new Button[labelsAndValues.length];
            for (int i = 0; i < labelsAndValues.length; i++) {
                Button radio = new Button(radioBox, SWT.RADIO | SWT.LEFT);
                radioButtons[i] = radio;
                String[] labelAndValue = labelsAndValues[i];
                radio.setText(labelAndValue[0]);
                radio.setData(labelAndValue[1]);
                radio.setFont(font);
                radio.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        String oldValue = value;
                        value = (String) event.widget.getData();
                        setPresentsDefaultValue(false);
                        fireValueChanged(VALUE, oldValue, value);
                    }
                });

            }
            radioBox.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    radioBox = null;
                    radioButtons = null;
                }
            });
        } else {
            checkParent(radioBox, parent);
        }
        return radioBox;
    }

    public void setIndent(int indent) {
        if (indent < 0) {
            this.indent = 0;
        } else {
            this.indent = indent;
        }
    }

    private void updateValue(String selectedValue) {
        value = selectedValue;
        if (radioButtons == null) {
            return;
        }

        if (value != null) {
            boolean found = false;
            for (int i = 0; i < radioButtons.length; i++) {
                Button radio = radioButtons[i];
                boolean selection = false;
                if (((String) radio.getData()).equals(value)) {
                    selection = true;
                    found = true;
                }
                radio.setSelection(selection);
            }
            if (found) {
                return;
            }
        }

        // We weren't able to find the value. So we select the first
        // radio button as a default.
        if (radioButtons.length > 0) {
            radioButtons[0].setSelection(true);
            value = (String) radioButtons[0].getData();
        }
        return;
    }

    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        if (!useGroup) {
            super.setEnabled(enabled, parent);
        }

        boolean boolArray[] = new boolean[radioButtons.length];

        for (int i = 0; i < radioButtons.length; i++) {
            boolArray[i] = enabled;
        }
        setEnabledArray(boolArray, -1, parent);
    }

    public int setEnabledArray(boolean[] enabled, int defaultSelectionIndex,
        Composite parent) {

        Assert.isTrue(enabled.length > 0);

        if (!useGroup) {
            super.setEnabled(enabled[0], parent);
            return getRadioSelected();
        }

        Assert.isTrue(enabled.length == radioButtons.length);

        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setEnabled(enabled[i]);
        }

        /*
         * If a selected radio button is disabled, the next best radio button is
         * selected.
         */
        for (int i = 0; i < radioButtons.length; i++) {
            if (!radioButtons[i].isEnabled() && radioButtons[i].getSelection()) {
                radioButtons[i].setSelection(false);

                if (defaultSelectionIndex >= 0
                    && radioButtons[defaultSelectionIndex].isEnabled()) {
                    radioButtons[defaultSelectionIndex].setSelection(true);
                    break;
                }

                for (int ii = 0; ii < radioButtons.length; ii++) {
                    if (radioButtons[ii].isEnabled()) {
                        radioButtons[ii].setSelection(true);
                        break;
                    }
                }
                break;
            }
        }
        return getRadioSelected();
    }

    public void setSelectionArray(boolean[] enabled) {

        Assert.isTrue(enabled.length > 0);

        if (!useGroup) {
            return;
        }

        Assert.isTrue(enabled.length == radioButtons.length);

        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setSelection(enabled[i]);
        }

    }

    public int getRadioSelected() {

        if (!useGroup) {
            return -1;
        }

        for (int i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].isEnabled() && radioButtons[i].getSelection()) {
                return i;
            }
        }

        return -1;
    }
}
