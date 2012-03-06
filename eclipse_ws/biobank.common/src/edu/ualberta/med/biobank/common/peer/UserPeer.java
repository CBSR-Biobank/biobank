package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.BbGroup;
import java.util.Collection;
import edu.ualberta.med.biobank.model.User;

public class UserPeer  extends PrincipalPeer {
	public static final Property<String, User> EMAIL = Property.create(
		"email" //$NON-NLS-1$
		, User.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, User>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(User model) {
				return model.getEmail();
			}
			@Override
			public void set(User model, String value) {
				model.setEmail(value);
			}
		});

	public static final Property<Boolean, User> NEED_PWD_CHANGE = Property.create(
		"needPwdChange" //$NON-NLS-1$
		, User.class
		, new TypeReference<Boolean>() {}
		, new Property.Accessor<Boolean, User>() { private static final long serialVersionUID = 1L;
			@Override
			public Boolean get(User model) {
				return model.getNeedPwdChange();
			}
			@Override
			public void set(User model, Boolean value) {
				model.setNeedPwdChange(value);
			}
		});

	public static final Property<Long, User> CSM_USER_ID = Property.create(
		"csmUserId" //$NON-NLS-1$
		, User.class
		, new TypeReference<Long>() {}
		, new Property.Accessor<Long, User>() { private static final long serialVersionUID = 1L;
			@Override
			public Long get(User model) {
				return model.getCsmUserId();
			}
			@Override
			public void set(User model, Long value) {
				model.setCsmUserId(value);
			}
		});

	public static final Property<String, User> LOGIN = Property.create(
		"login" //$NON-NLS-1$
		, User.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, User>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(User model) {
				return model.getLogin();
			}
			@Override
			public void set(User model, String value) {
				model.setLogin(value);
			}
		});

	public static final Property<String, User> FULL_NAME = Property.create(
		"fullName" //$NON-NLS-1$
		, User.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, User>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(User model) {
				return model.getFullName();
			}
			@Override
			public void set(User model, String value) {
				model.setFullName(value);
			}
		});

	public static final Property<Boolean, User> RECV_BULK_EMAILS = Property.create(
		"recvBulkEmails" //$NON-NLS-1$
		, User.class
		, new TypeReference<Boolean>() {}
		, new Property.Accessor<Boolean, User>() { private static final long serialVersionUID = 1L;
			@Override
			public Boolean get(User model) {
				return model.getRecvBulkEmails();
			}
			@Override
			public void set(User model, Boolean value) {
				model.setRecvBulkEmails(value);
			}
		});

	public static final Property<Collection<Comment>, User> COMMENT_COLLECTION = Property.create(
		"commentCollection" //$NON-NLS-1$
		, User.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, User>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(User model) {
				return model.getComments();
			}
			@Override
			public void set(User model, Collection<Comment> value) {
				model.getComments().clear();
				model.getComments().addAll(value);
			}
		});

	public static final Property<ActivityStatus, User> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, User.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, User>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(User model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(User model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

	public static final Property<Collection<BbGroup>, User> GROUP_COLLECTION = Property.create(
		"groupCollection" //$NON-NLS-1$
		, User.class
		, new TypeReference<Collection<BbGroup>>() {}
		, new Property.Accessor<Collection<BbGroup>, User>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<BbGroup> get(User model) {
				return model.getGroups();
			}
			@Override
			public void set(User model, Collection<BbGroup> value) {
				model.getGroups().clear();
				model.getGroups().addAll(value);
			}
		});

   public static final List<Property<?, ? super User>> PROPERTIES;
   static {
      List<Property<?, ? super User>> aList = new ArrayList<Property<?, ? super User>>();
      aList.add(EMAIL);
      aList.add(NEED_PWD_CHANGE);
      aList.add(CSM_USER_ID);
      aList.add(LOGIN);
      aList.add(FULL_NAME);
      aList.add(RECV_BULK_EMAILS);
      aList.add(COMMENT_COLLECTION);
      aList.add(ACTIVITY_STATUS);
      aList.add(GROUP_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
