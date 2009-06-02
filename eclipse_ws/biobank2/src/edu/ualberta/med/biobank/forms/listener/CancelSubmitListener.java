package edu.ualberta.med.biobank.forms.listener;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.forms.CancelConfirmForm;

/**
 * KeyListener for cancel/submit field used with the barcode scanner
 */
public class CancelSubmitListener implements KeyListener {

	public CancelConfirmForm form;

	public CancelSubmitListener(CancelConfirmForm form) {
		this.form = form;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == 13) {
			String text = ((Text) e.widget).getText();
			try {
				if (isConfirmCode(text) && form.isConfirmEnabled()) {
					form.confirm();
				} else if (isCancelCode(text)) {
					form.cancel();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private boolean isCancelCode(String text) {
		// FIXME get the cancel bar code
		return false;
	}

	private boolean isConfirmCode(String text) {
		// FIXME get the confirm bar code
		return false;
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
