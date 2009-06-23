package edu.ualberta.med.biobank.forms;

public interface CancelConfirmForm {

	public void cancel() throws Exception;

	public void confirm() throws Exception;

	public boolean isConfirmEnabled();
}
