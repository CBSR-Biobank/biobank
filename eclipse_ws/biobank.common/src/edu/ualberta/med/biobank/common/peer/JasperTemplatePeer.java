package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.JasperTemplate;

public class JasperTemplatePeer {
	public static final Property<Integer, JasperTemplate> ID = Property.create(
		"id" //$NON-NLS-1$
		, JasperTemplate.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, JasperTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(JasperTemplate model) {
				return model.getId();
			}
			@Override
			public void set(JasperTemplate model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, JasperTemplate> NAME = Property.create(
		"name" //$NON-NLS-1$
		, JasperTemplate.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, JasperTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(JasperTemplate model) {
				return model.getName();
			}
			@Override
			public void set(JasperTemplate model, String value) {
				model.setName(value);
			}
		});

	public static final Property<String, JasperTemplate> XML = Property.create(
		"xml" //$NON-NLS-1$
		, JasperTemplate.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, JasperTemplate>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(JasperTemplate model) {
				return model.getXml();
			}
			@Override
			public void set(JasperTemplate model, String value) {
				model.setXml(value);
			}
		});

   public static final List<Property<?, ? super JasperTemplate>> PROPERTIES;
   static {
      List<Property<?, ? super JasperTemplate>> aList = new ArrayList<Property<?, ? super JasperTemplate>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(XML);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
