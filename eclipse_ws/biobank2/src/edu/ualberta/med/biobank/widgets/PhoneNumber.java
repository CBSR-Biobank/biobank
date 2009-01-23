package edu.ualberta.med.biobank.widgets;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Listener;

public class PhoneNumber extends Composite {
	
	private Text areaCode;
	private Text exchange;
	private Text digits;
	private Text extension;
	
	private IObservableValue areaCodeModel = new WritableValue();
	private IObservableValue exchangeModel = new WritableValue();
	private IObservableValue digitsModel = new WritableValue();
	private IObservableValue extensionModel = new WritableValue();

	public PhoneNumber(Composite parent, int style) {
		super(parent, style);
		
		areaCode = new Text(this, SWT.SINGLE);		
        areaCode.setTextLimit(3);
        areaCode.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        areaCode.addListener(SWT.Verify, new DigitListener());
		new Label(this, SWT.LEFT).setText(" - ");
		exchange = new Text(this, SWT.SINGLE);
        exchange.setTextLimit(3);
        exchange.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        exchange.addListener(SWT.Verify, new DigitListener());
		new Label(this, SWT.LEFT).setText(" - ");
		digits = new Text(this, SWT.SINGLE);
        digits.setTextLimit(4);
        digits.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        digits.addListener(SWT.Verify, new DigitListener());
		new Label(this, SWT.LEFT).setText(" ext.");
		extension = new Text(this, SWT.SINGLE);
        extension.setTextLimit(6);
        extension.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        extension.addListener(SWT.Verify, new DigitListener());
        
        setLayout(new GridLayout(7, false));
	}
	
	public void bindValues(DataBindingContext dbc) {	
		
		final IObservableValue areaCodeOv = new WritableValue();		
		final IObservableValue exchangeOv = new WritableValue();		
		final IObservableValue digitsOv = new WritableValue();		
		final IObservableValue extensionOv = new WritableValue();		

		dbc.bindValue(SWTObservables.observeText(areaCode, SWT.Modify), areaCodeOv, null, null);
		dbc.bindValue(SWTObservables.observeText(exchange, SWT.Modify), exchangeOv, null, null);
		dbc.bindValue(SWTObservables.observeText(digits, SWT.Modify), digitsOv, null, null);
		dbc.bindValue(SWTObservables.observeText(extension, SWT.Modify), extensionOv, null, null);
    	
		MultiValidator validator = new MultiValidator() {
			protected IStatus validate() {
				int areaCodeLen  = areaCodeOv.getValue().toString().length();
				int exchangeLen  = exchangeOv.getValue().toString().length();
				int digitsLen    = digitsOv.getValue().toString().length();
				int extensionLen = extensionOv.getValue().toString().length();
				
				if (((areaCodeLen == 3) && (exchangeLen == 3) && (digitsLen == 4))
						|| ((areaCodeLen == 0) && (exchangeLen == 0) && (digitsLen == 0) && (extensionLen == 0)))
					return null;
				
				return ValidationStatus	.error("Invalid phone number");
			}
		};
		dbc.addValidationStatusProvider(validator);				
		
		dbc.bindValue(areaCodeOv,  areaCodeModel, null, null);
		dbc.bindValue(exchangeOv,  exchangeModel, null, null);	
		dbc.bindValue(digitsOv,    digitsModel, null, null);	
		dbc.bindValue(extensionOv, extensionModel, null, null);	
		
		dbc.bindValue(validator.observeValidatedValue(areaCodeOv),  areaCodeModel, null, null);
		dbc.bindValue(validator.observeValidatedValue(exchangeOv),  exchangeModel, null, null);	
		dbc.bindValue(validator.observeValidatedValue(digitsOv),    digitsModel, null, null);	
		dbc.bindValue(validator.observeValidatedValue(extensionOv), extensionModel, null, null);	
	}
	
	public String getText() {
		return areaCode.getText() + exchange.getText() + digits.getText()
		+ extension.getText(); 
		
	}
	
	private class DigitListener implements Listener {
		public void handleEvent(Event event) {	
			String string = event.text;
			char[] chars = new char[string.length()];
			string.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++) {
				if (!('0' <= chars[i] && chars[i] <= '9')) {
					event.doit = false;
					return;
				}
			}		
		}
	}

}
