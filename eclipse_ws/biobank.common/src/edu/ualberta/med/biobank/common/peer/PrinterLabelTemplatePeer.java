package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.JasperTemplate;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;

public class PrinterLabelTemplatePeer {
	public static final Property<Integer, PrinterLabelTemplate> ID = Property.create(
		"id" //$NON-NLS-1$
		, PrinterLabelTemplate.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, PrinterLabelTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(PrinterLabelTemplate model) {
				return model.getId();
			}
			@Override
			public void set(PrinterLabelTemplate model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, PrinterLabelTemplate> CONFIG_DATA = Property.create(
		"configData" //$NON-NLS-1$
		, PrinterLabelTemplate.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PrinterLabelTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PrinterLabelTemplate model) {
				return model.getConfigData();
			}
			@Override
			public void set(PrinterLabelTemplate model, String value) {
				model.setConfigData(value);
			}
		});

	public static final Property<String, PrinterLabelTemplate> PRINTER_NAME = Property.create(
		"printerName" //$NON-NLS-1$
		, PrinterLabelTemplate.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PrinterLabelTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PrinterLabelTemplate model) {
				return model.getPrinterName();
			}
			@Override
			public void set(PrinterLabelTemplate model, String value) {
				model.setPrinterName(value);
			}
		});

	public static final Property<String, PrinterLabelTemplate> NAME = Property.create(
		"name" //$NON-NLS-1$
		, PrinterLabelTemplate.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PrinterLabelTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PrinterLabelTemplate model) {
				return model.getName();
			}
			@Override
			public void set(PrinterLabelTemplate model, String value) {
				model.setName(value);
			}
		});

	public static final Property<JasperTemplate, PrinterLabelTemplate> JASPER_TEMPLATE = Property.create(
		"jasperTemplate" //$NON-NLS-1$
		, PrinterLabelTemplate.class
		, new TypeReference<JasperTemplate>() {}
		, new Property.Accessor<JasperTemplate, PrinterLabelTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public JasperTemplate get(PrinterLabelTemplate model) {
				return model.getJasperTemplate();
			}
			@Override
			public void set(PrinterLabelTemplate model, JasperTemplate value) {
				model.setJasperTemplate(value);
			}
		});

   public static final List<Property<?, ? super PrinterLabelTemplate>> PROPERTIES;
   static {
      List<Property<?, ? super PrinterLabelTemplate>> aList = new ArrayList<Property<?, ? super PrinterLabelTemplate>>();
      aList.add(ID);
      aList.add(CONFIG_DATA);
      aList.add(PRINTER_NAME);
      aList.add(NAME);
      aList.add(JASPER_TEMPLATE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
