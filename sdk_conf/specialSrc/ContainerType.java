package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class ContainerType  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
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
	* 
	**/
	
	public Boolean topLevel;
	/**
	* Retrieves the value of the topLevel attribute
	* @return topLevel
	**/

	public Boolean getTopLevel(){
		return topLevel;
	}

	/**
	* Sets the value of topLevel attribute
	**/

	public void setTopLevel(Boolean topLevel){
		this.topLevel = topLevel;
	}
	
	/**
	* 
	**/
	
	public Double defaultTemperature;
	/**
	* Retrieves the value of the defaultTemperature attribute
	* @return defaultTemperature
	**/

	public Double getDefaultTemperature(){
		return defaultTemperature;
	}

	/**
	* Sets the value of defaultTemperature attribute
	**/

	public void setDefaultTemperature(Double defaultTemperature){
		this.defaultTemperature = defaultTemperature;
	}
	
	/**
	* 
	**/
	
	public Collection<Comment> commentCollection = new HashSet<Comment>();
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
	* An associated edu.ualberta.med.biobank.model.SpecimenType object's collection 
	**/
			
	private Collection<SpecimenType> specimenTypeCollection = new HashSet<SpecimenType>();
	/**
	* Retrieves the value of the specimenTypeCollection attribute
	* @return specimenTypeCollection
	**/

	public Collection<SpecimenType> getSpecimenTypeCollection(){
		return specimenTypeCollection;
	}

	/**
	* Sets the value of specimenTypeCollection attribute
	**/

	public void setSpecimenTypeCollection(Collection<SpecimenType> specimenTypeCollection){
		this.specimenTypeCollection = specimenTypeCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.ContainerType object's collection 
	**/
			
	private Collection<ContainerType> childContainerTypeCollection = new HashSet<ContainerType>();
	/**
	* Retrieves the value of the childContainerTypeCollection attribute
	* @return childContainerTypeCollection
	**/

	public Collection<ContainerType> getChildContainerTypeCollection(){
		return childContainerTypeCollection;
	}

	/**
	* Sets the value of childContainerTypeCollection attribute
	**/

	public void setChildContainerTypeCollection(Collection<ContainerType> childContainerTypeCollection){
		this.childContainerTypeCollection = childContainerTypeCollection;
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
	* An associated edu.ualberta.med.biobank.model.Capacity object
	**/
			
	private Capacity capacity;
	/**
	* Retrieves the value of the capacity attribute
	* @return capacity
	**/
	
	public Capacity getCapacity(){
		return capacity;
	}
	/**
	* Sets the value of capacity attribute
	**/

	public void setCapacity(Capacity capacity){
		this.capacity = capacity;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Site object
	**/
			
	private Site site;
	/**
	* Retrieves the value of the site attribute
	* @return site
	**/
	
	public Site getSite(){
		return site;
	}
	/**
	* Sets the value of site attribute
	**/

	public void setSite(Site site){
		this.site = site;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ContainerLabelingScheme object
	**/
			
	private ContainerLabelingScheme childLabelingScheme;
	/**
	* Retrieves the value of the childLabelingScheme attribute
	* @return childLabelingScheme
	**/
	
	public ContainerLabelingScheme getChildLabelingScheme(){
		return childLabelingScheme;
	}
	/**
	* Sets the value of childLabelingScheme attribute
	**/

	public void setChildLabelingScheme(ContainerLabelingScheme childLabelingScheme){
		this.childLabelingScheme = childLabelingScheme;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ContainerType) 
		{
			ContainerType c =(ContainerType)obj; 			 
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
	

	private Collection<ContainerType> parentContainerTypeCollection = new HashSet<ContainerType>();
	public Collection<ContainerType> getParentContainerTypeCollection(){
		return parentContainerTypeCollection;
	}
	public void setParentContainerTypeCollection(Collection<ContainerType> parentContainerTypeCollection){
		this.parentContainerTypeCollection = parentContainerTypeCollection;
	}
}
