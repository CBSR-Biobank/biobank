package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public abstract class Center  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	public Integer id;
	/**
	* Retrieves the value of the id attribute
	* @return id
	**/

	public Integer getId(){
		return id;
	}

	/**
	* Sets the value of id attribute
	**/

	public void setId(Integer id){
		this.id = id;
	}
	
	/**
	* 
	**/
	
	@NotEmpty
	public String name;
	/**
	* Retrieves the value of the name attribute
	* @return name
	**/

	public String getName(){
		return name;
	}

	/**
	* Sets the value of name attribute
	**/

	public void setName(String name){
		this.name = name;
	}
	
	/**
	* 
	**/
	
	@NotEmpty
	public String nameShort;
	/**
	* Retrieves the value of the nameShort attribute
	* @return nameShort
	**/

	public String getNameShort(){
		return nameShort;
	}

	/**
	* Sets the value of nameShort attribute
	**/

	public void setNameShort(String nameShort){
		this.nameShort = nameShort;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Address object
	**/
			
	private Address address;
	/**
	* Retrieves the value of the address attribute
	* @return address
	**/
	
	public Address getAddress(){
		return address;
	}
	/**
	* Sets the value of address attribute
	**/

	public void setAddress(Address address){
		this.address = address;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ProcessingEvent object's collection 
	**/
			
	private Collection<ProcessingEvent> processingEventCollection = new HashSet<ProcessingEvent>();

	/**
	* Retrieves the value of the processingEventCollection attribute
	* @return processingEventCollection
	**/

	public Collection<ProcessingEvent> getProcessingEventCollection(){
		return processingEventCollection;
	}

	/**
	* Sets the value of processingEventCollection attribute
	**/

	public void setProcessingEventCollection(Collection<ProcessingEvent> processingEventCollection){
		this.processingEventCollection = processingEventCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Membership object's collection 
	**/
			
	private Collection<Membership> membershipCollection = new HashSet<Membership>();

	/**
	* Retrieves the value of the membershipCollection attribute
	* @return membershipCollection
	**/

	public Collection<Membership> getMembershipCollection(){
		return membershipCollection;
	}

	/**
	* Sets the value of membershipCollection attribute
	**/

	public void setMembershipCollection(Collection<Membership> membershipCollection){
		this.membershipCollection = membershipCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Dispatch object's collection 
	**/
			
	private Collection<Dispatch> srcDispatchCollection = new HashSet<Dispatch>();

	/**
	* Retrieves the value of the srcDispatchCollection attribute
	* @return srcDispatchCollection
	**/

	public Collection<Dispatch> getSrcDispatchCollection(){
		return srcDispatchCollection;
	}

	/**
	* Sets the value of srcDispatchCollection attribute
	**/

	public void setSrcDispatchCollection(Collection<Dispatch> srcDispatchCollection){
		this.srcDispatchCollection = srcDispatchCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Dispatch object's collection 
	**/
			
	private Collection<Dispatch> dstDispatchCollection = new HashSet<Dispatch>();

	/**
	* Retrieves the value of the dstDispatchCollection attribute
	* @return dstDispatchCollection
	**/

	public Collection<Dispatch> getDstDispatchCollection(){
		return dstDispatchCollection;
	}

	/**
	* Sets the value of dstDispatchCollection attribute
	**/

	public void setDstDispatchCollection(Collection<Dispatch> dstDispatchCollection){
		this.dstDispatchCollection = dstDispatchCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.OriginInfo object's collection 
	**/
			
	private Collection<OriginInfo> originInfoCollection = new HashSet<OriginInfo>();

	/**
	* Retrieves the value of the originInfoCollection attribute
	* @return originInfoCollection
	**/

	public Collection<OriginInfo> getOriginInfoCollection(){
		return originInfoCollection;
	}

	/**
	* Sets the value of originInfoCollection attribute
	**/

	public void setOriginInfoCollection(Collection<OriginInfo> originInfoCollection){
		this.originInfoCollection = originInfoCollection;
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
			
	private Collection<Comment> commentCollection = new HashSet<Comment>();

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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Center) 
		{
			Center c =(Center)obj; 			 
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
