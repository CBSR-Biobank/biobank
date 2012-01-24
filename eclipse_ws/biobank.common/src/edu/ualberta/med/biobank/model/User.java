package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.NotEmpty;
import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/
	

public class User extends Principal implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	@NotEmpty
	public String login;
	/**
	* Retrieves the value of the login attribute
	* @return login
	**/

	public String getLogin(){
		return login;
	}

	/**
	* Sets the value of login attribute
	**/

	public void setLogin(String login){
		this.login = login;
	}
	
	/**
	* 
	**/
	
	@NotNull
	public Long csmUserId;
	/**
	* Retrieves the value of the csmUserId attribute
	* @return csmUserId
	**/

	public Long getCsmUserId(){
		return csmUserId;
	}

	/**
	* Sets the value of csmUserId attribute
	**/

	public void setCsmUserId(Long csmUserId){
		this.csmUserId = csmUserId;
	}
	
	/**
	* 
	**/
	
	public Boolean recvBulkEmails;
	/**
	* Retrieves the value of the recvBulkEmails attribute
	* @return recvBulkEmails
	**/

	public Boolean getRecvBulkEmails(){
		return recvBulkEmails;
	}

	/**
	* Sets the value of recvBulkEmails attribute
	**/

	public void setRecvBulkEmails(Boolean recvBulkEmails){
		this.recvBulkEmails = recvBulkEmails;
	}
	
	/**
	* 
	**/
	
	public String fullName;
	/**
	* Retrieves the value of the fullName attribute
	* @return fullName
	**/

	public String getFullName(){
		return fullName;
	}

	/**
	* Sets the value of fullName attribute
	**/

	public void setFullName(String fullName){
		this.fullName = fullName;
	}
	
	/**
	* 
	**/
	
	public String email;
	/**
	* Retrieves the value of the email attribute
	* @return email
	**/

	public String getEmail(){
		return email;
	}

	/**
	* Sets the value of email attribute
	**/

	public void setEmail(String email){
		this.email = email;
	}
	
	/**
	* 
	**/
	
	public Boolean needPwdChange;
	/**
	* Retrieves the value of the needPwdChange attribute
	* @return needPwdChange
	**/

	public Boolean getNeedPwdChange(){
		return needPwdChange;
	}

	/**
	* Sets the value of needPwdChange attribute
	**/

	public void setNeedPwdChange(Boolean needPwdChange){
		this.needPwdChange = needPwdChange;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.ActivityStatus object
	**/
			
	private ActivityStatus activityStatus;
	/**
	* Retrieves the value of the activityStatus attribute
	* @return activityStatus
	**/
	
	public ActivityStatus getActivityStatus(){
		return activityStatus;
	}
	/**
	* Sets the value of activityStatus attribute
	**/

	public void setActivityStatus(ActivityStatus activityStatus){
		this.activityStatus = activityStatus;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Comment object's collection 
	**/
			
	private Collection<Comment> commentCollection;
	/**
	* Retrieves the value of the commentCollection attribute
	* @return commentCollection
	**/

	public Collection<Comment> getCommentCollection(){
		return commentCollection;
	}

	/**
	* Sets the value of commentCollection attribute
	**/

	public void setCommentCollection(Collection<Comment> commentCollection){
		this.commentCollection = commentCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.BbGroup object's collection 
	**/
			
	private Collection<BbGroup> groupCollection;
	/**
	* Retrieves the value of the groupCollection attribute
	* @return groupCollection
	**/

	public Collection<BbGroup> getGroupCollection(){
		return groupCollection;
	}

	/**
	* Sets the value of groupCollection attribute
	**/

	public void setGroupCollection(Collection<BbGroup> groupCollection){
		this.groupCollection = groupCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof User) 
		{
			User c =(User)obj; 			 
			if(getId() != null && getId().equals(c.getId()))
				return true;
		}
		return false;
	}
		
	/**
	* Returns hash code for the primary key of the object
	**/
	public int hashCode()
	{
		if(getId() != null)
			return getId().hashCode();
		return 0;
	}
	
}