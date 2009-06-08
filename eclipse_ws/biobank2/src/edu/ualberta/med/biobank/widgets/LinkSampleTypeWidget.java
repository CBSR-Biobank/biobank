package edu.ualberta.med.biobank.widgets;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.forms.FormUtils;
import edu.ualberta.med.biobank.model.SampleType;

/**
 * Create 3 widgets to show types selection for sample in scan link : one label,
 * one combo with different types and one text showing total number of samples
 * found
 */
public class LinkSampleTypeWidget {
	private Combo combo;
	private ComboViewer cv;
	private ControlDecoration controlDecoration;
	private Label textNumber;
	private Integer number;

	private IObservableValue selectionDone = new WritableValue(Boolean.TRUE,
		Boolean.class);

	public LinkSampleTypeWidget(Composite parent, char letter,
			List<SampleType> types, FormToolkit toolkit) {

		toolkit.createLabel(parent, String.valueOf(letter), SWT.LEFT);

		createCombo(parent, types);
		toolkit.adapt(combo, true, true);

		textNumber = toolkit.createLabel(parent, "", SWT.RIGHT | SWT.BORDER);
		GridData data = new GridData();
		data.widthHint = 20;
		textNumber.setLayoutData(data);

		controlDecoration = FormUtils.createDecorator(combo,
			"A sample type should be selected");
	}

	private void createCombo(Composite parent, List<SampleType> types) {
		combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

		cv = new ComboViewer(combo);
		cv.setContentProvider(new ArrayContentProvider());
		cv.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((SampleType) element).getName();
			}
		});
		cv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() == null
						|| ((IStructuredSelection) event.getSelection()).size() == 0) {
					selectionDone.setValue(false);
				} else {
					selectionDone.setValue(true);
				}
			}
		});
		cv.setComparer(new IElementComparer() {
			@Override
			public boolean equals(Object a, Object b) {
				if (a instanceof SampleType && b instanceof SampleType) {
					return ((SampleType) a).getId().equals(
						((SampleType) b).getId());
				}
				return false;
			}

			@Override
			public int hashCode(Object element) {
				return element.hashCode();
			}

		});
		cv.setComparator(new ViewerComparator());
		cv.setInput(types);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		cv.addSelectionChangedListener(listener);
	}

	public void setNumber(Integer number) {
		this.number = number;
		String text = "";
		if (number != null) {
			text = number.toString();
		}
		if (number == null || number == 0) {
			combo.setEnabled(false);
			selectionDone.setValue(true);
		} else {
			combo.setEnabled(true);
			selectionDone.setValue(false);
		}
		textNumber.setText(text);
	}

	public boolean needToSave() {
		return number > 0;
	}

	public SampleType getSelection() {
		return (SampleType) ((StructuredSelection) cv.getSelection())
			.getFirstElement();
	}

	public void addBinding(DataBindingContext dbc) {
		WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
		UpdateValueStrategy uvs = new UpdateValueStrategy();
		uvs.setAfterGetValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Boolean && !(Boolean) value) {
					controlDecoration.show();
					return ValidationStatus.error("Types should be selected");
				} else {
					controlDecoration.hide();
					return Status.OK_STATUS;
				}
			}

		});
		dbc.bindValue(wv, selectionDone, uvs, uvs);
	}

	public void resetValues() {
		cv.setSelection(null);
		setNumber(null);
	}

}
