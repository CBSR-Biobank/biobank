package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentPeer {
	public static final Property<Integer, Comment> ID = Property.create(
		"id" //$NON-NLS-1$
		, Comment.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Comment>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Comment model) {
				return model.getId();
			}
			@Override
			public void set(Comment model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Comment> MESSAGE = Property.create(
		"message" //$NON-NLS-1$
		, Comment.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Comment>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Comment model) {
				return model.getMessage();
			}
			@Override
			public void set(Comment model, String value) {
				model.setMessage(value);
			}
		});

	public static final Property<Date, Comment> CREATED_AT = Property.create(
		"createdAt" //$NON-NLS-1$
		, Comment.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, Comment>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(Comment model) {
				return model.getCreatedAt();
			}
			@Override
			public void set(Comment model, Date value) {
				model.setCreatedAt(value);
			}
		});

	public static final Property<User, Comment> USER = Property.create(
		"user" //$NON-NLS-1$
		, Comment.class
		, new TypeReference<User>() {}
		, new Property.Accessor<User, Comment>() { private static final long serialVersionUID = 1L;
			@Override
			public User get(Comment model) {
				return model.getUser();
			}
			@Override
			public void set(Comment model, User value) {
				model.setUser(value);
			}
		});

   public static final List<Property<?, ? super Comment>> PROPERTIES;
   static {
      List<Property<?, ? super Comment>> aList = new ArrayList<Property<?, ? super Comment>>();
      aList.add(ID);
      aList.add(MESSAGE);
      aList.add(CREATED_AT);
      aList.add(USER);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
