package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class Specimen  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	private Integer id;
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
	private String inventoryId;
	/**
	* Retrieves the value of the inventoryId attribute
	* @return inventoryId
	**/

	public String getInventoryId(){
		return inventoryId;
	}

	/**
	* Sets the value of inventoryId attribute
	**/

	public void setInventoryId(String inventoryId){
		this.inventoryId = inventoryId;
	}
	
	/**
	* 
	**/
	
	public Collection<Comment> commentCollection;
	/**
	* Retrieves the value of the comment attribute
	* @return comment
	**/

	public Collection<Comment> getCommentCollection(){
		return commentCollection;
	}

	/**
	* Sets the value of comment attribute
	**/

	public void setCommentCollection(Collection<Comment> comments){
		this.commentCollection = comments;
	}
	
	/**
	* 
	**/
	
	public Double quantity;
	/**
	* Retrieves the value of the quantity attribute
	* @return quantity
	**/

	public Double getQuantity(){
		return quantity;
	}

	/**
	* Sets the value of quantity attribute
	**/

	public void setQuantity(Double quantity){
		this.quantity = quantity;
	}
	
	/**
	* 
	**/
	
	@NotNull
	public java.util.Date createdAt;
	/**
	* Retrieves the value of the createdAt attribute
	* @return createdAt
	**/

	public java.util.Date getCreatedAt(){
		return createdAt;
	}

	/**
	* Sets the value of createdAt attribute
	**/

	public void setCreatedAt(java.util.Date createdAt){
		this.createdAt = createdAt;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.RequestSpecimen object's collection 
	**/
			
	private Collection<RequestSpecimen> requestSpecimenCollection = new HashSet<RequestSpecimen>();

	/**
	* Retrieves the value of the requestSpecimenCollection attribute
	* @return requestSpecimenCollection
	**/

	public Collection<RequestSpecimen> getRequestSpecimenCollection(){
		return requestSpecimenCollection;
	}

	/**
	* Sets the value of requestSpecimenCollection attribute
	**/

	public void setRequestSpecimenCollection(Collection<RequestSpecimen> requestSpecimenCollection){
		this.requestSpecimenCollection = requestSpecimenCollection;
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
	* An associated edu.ualberta.med.biobank.model.Specimen object's collection 
	**/
			
	private Collection<Specimen> childSpecimenCollection = new HashSet<Specimen>();

	/**
	* Retrieves the value of the childSpecimenCollection attribute
	* @return childSpecimenCollection
	**/

	public Collection<Specimen> getChildSpecimenCollection(){
		return childSpecimenCollection;
	}

	/**
	* Sets the value of childSpecimenCollection attribute
	**/

	public void setChildSpecimenCollection(Collection<Specimen> childSpecimenCollection){
		this.childSpecimenCollection = childSpecimenCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.OriginInfo object
	**/
			
	private OriginInfo originInfo;
	/**
	* Retrieves the value of the originInfo attribute
	* @return originInfo
	**/
	
	public OriginInfo getOriginInfo(){
		return originInfo;
	}
	/**
	* Sets the value of originInfo attribute
	**/

	public void setOriginInfo(OriginInfo originInfo){
		this.originInfo = originInfo;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.DispatchSpecimen object's collection 
	**/
			
	private Collection<DispatchSpecimen> dispatchSpecimenCollection = new HashSet<DispatchSpecimen>();

	/**
	* Retrieves the value of the dispatchSpecimenCollection attribute
	* @return dispatchSpecimenCollection
	**/

	public Collection<DispatchSpecimen> getDispatchSpecimenCollection(){
		return dispatchSpecimenCollection;
	}

	/**
	* Sets the value of dispatchSpecimenCollection attribute
	**/

	public void setDispatchSpecimenCollection(Collection<DispatchSpecimen> dispatchSpecimenCollection){
		this.dispatchSpecimenCollection = dispatchSpecimenCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Center object
	**/
			
	private Center currentCenter;
	/**
	* Retrieves the value of the currentCenter attribute
	* @return currentCenter
	**/
	
	public Center getCurrentCenter(){
		return currentCenter;
	}
	/**
	* Sets the value of currentCenter attribute
	**/

	public void setCurrentCenter(Center currentCenter){
		this.currentCenter = currentCenter;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.CollectionEvent object
	**/
			
	private CollectionEvent originalCollectionEvent;
	/**
	* Retrieves the value of the originalCollectionEvent attribute
	* @return originalCollectionEvent
	**/
	
	public CollectionEvent getOriginalCollectionEvent(){
		return originalCollectionEvent;
	}
	/**
	* Sets the value of originalCollectionEvent attribute
	**/

	public void setOriginalCollectionEvent(CollectionEvent originalCollectionEvent){
		this.originalCollectionEvent = originalCollectionEvent;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ProcessingEvent object
	**/
			
	private ProcessingEvent processingEvent;
	/**
	* Retrieves the value of the processingEvent attribute
	* @return processingEvent
	**/
	
	public ProcessingEvent getProcessingEvent(){
		return processingEvent;
	}
	/**
	* Sets the value of processingEvent attribute
	**/

	public void setProcessingEvent(ProcessingEvent processingEvent){
		this.processingEvent = processingEvent;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.SpecimenPosition object
	**/
			
	private SpecimenPosition specimenPosition;
	/**
	* Retrieves the value of the specimenPosition attribute
	* @return specimenPosition
	**/
	
	public SpecimenPosition getSpecimenPosition(){
		return specimenPosition;
	}
	/**
	* Sets the value of specimenPosition attribute
	**/

	public void setSpecimenPosition(SpecimenPosition specimenPosition){
		this.specimenPosition = specimenPosition;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.CollectionEvent object
	**/
			
	private CollectionEvent collectionEvent;
	/**
	* Retrieves the value of the collectionEvent attribute
	* @return collectionEvent
	**/
	
	public CollectionEvent getCollectionEvent(){
		return collectionEvent;
	}
	/**
	* Sets the value of collectionEvent attribute
	**/

	public void setCollectionEvent(CollectionEvent collectionEvent){
		this.collectionEvent = collectionEvent;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.SpecimenType object
	**/
			
	private SpecimenType specimenType;
	/**
	* Retrieves the value of the specimenType attribute
	* @return specimenType
	**/
	
	public SpecimenType getSpecimenType(){
		return specimenType;
	}
	/**
	* Sets the value of specimenType attribute
	**/

	public void setSpecimenType(SpecimenType specimenType){
		this.specimenType = specimenType;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Specimen) 
		{
			Specimen c =(Specimen)obj; 			 
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


	private Specimen parentSpecimen;
	public Specimen getParentSpecimen(){
		return parentSpecimen;
	}
	public void setParentSpecimen(Specimen parentSpecimen){
		this.parentSpecimen = parentSpecimen;
	}
	
	private Specimen topSpecimen;
	public Specimen getTopSpecimen(){
		return topSpecimen;
	}
	public void setTopSpecimen(Specimen topSpecimen){
		this.topSpecimen = topSpecimen;
	}
}
