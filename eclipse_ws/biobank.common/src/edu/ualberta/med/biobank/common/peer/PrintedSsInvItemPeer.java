package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.PrintedSsInvItem;

public class PrintedSsInvItemPeer {
	public static final Property<Integer, PrintedSsInvItem> ID = Property.create(
		"id" //$NON-NLS-1$
		, PrintedSsInvItem.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, PrintedSsInvItem>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(PrintedSsInvItem model) {
				return model.getId();
			}
			@Override
			public void set(PrintedSsInvItem model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, PrintedSsInvItem> TXT = Property.create(
		"txt" //$NON-NLS-1$
		, PrintedSsInvItem.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PrintedSsInvItem>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PrintedSsInvItem model) {
				return model.getTxt();
			}
			@Override
			public void set(PrintedSsInvItem model, String value) {
				model.setTxt(value);
			}
		});

   public static final List<Property<?, ? super PrintedSsInvItem>> PROPERTIES;
   static {
      List<Property<?, ? super PrintedSsInvItem>> aList = new ArrayList<Property<?, ? super PrintedSsInvItem>>();
      aList.add(ID);
      aList.add(TXT);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
