package edu.ualberta.med.biobank.forms;

public class FieldInfo {
	public String label;
	public Class<?> widgetClass;
	public Class<?> validatorClass;
	public String errMsg;
	
	public FieldInfo(String l, Class<?> w, Class<?> v, String msg) {
		label = l;
		widgetClass = w;
		validatorClass = v; 
		errMsg = msg;
	}
}
