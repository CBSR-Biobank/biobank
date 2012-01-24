package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class Container  implements IBiobankModel
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
	
	public String productBarcode;
	/**
	* Retrieves the value of the productBarcode attribute
	* @return productBarcode
	**/

	public String getProductBarcode(){
		return productBarcode;
	}

	/**
	* Sets the value of productBarcode attribute
	**/

	public void setProductBarcode(String productBarcode){
		this.productBarcode = productBarcode;
	}
	
	/**
	* 
	**/
	
	@NotEmpty
	public String label;
	/**
	* Retrieves the value of the label attribute
	* @return label
	**/

	public String getLabel(){
		return label;
	}

	/**
	* Sets the value of label attribute
	**/

	public void setLabel(String label){
		this.label = label;
	}
	
	/**
	* 
	**/
	
	public Double temperature;
	/**
	* Retrieves the value of the temperature attribute
	* @return temperature
	**/

	public Double getTemperature(){
		return temperature;
	}

	/**
	* Sets the value of temperature attribute
	**/

	public void setTemperature(Double temperature){
		this.temperature = temperature;
	}
	
	/**
	* 
	**/
	
	public String path;
	/**
	* Retrieves the value of the path attribute
	* @return path
	**/

	public String getPath(){
		return path;
	}

	/**
	* Sets the value of path attribute
	**/

	public void setPath(String path){
		this.path = path;
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
	* An associated edu.ualberta.med.biobank.model.ContainerPosition object's collection 
	**/
			
	private Collection<ContainerPosition> childPositionCollection = new HashSet<ContainerPosition>();

	/**
	* Retrieves the value of the childPositionCollection attribute
	* @return childPositionCollection
	**/

	public Collection<ContainerPosition> getChildPositionCollection(){
		return childPositionCollection;
	}

	/**
	* Sets the value of childPositionCollection attribute
	**/

	public void setChildPositionCollection(Collection<ContainerPosition> childPositionCollection){
		this.childPositionCollection = childPositionCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Container object
	**/
			
	private Container topContainer;
	/**
	* Retrieves the value of the topContainer attribute
	* @return topContainer
	**/
	
	public Container getTopContainer(){
		return topContainer;
	}
	/**
	* Sets the value of topContainer attribute
	**/

	public void setTopContainer(Container topContainer){
		this.topContainer = topContainer;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.SpecimenPosition object's collection 
	**/
			
	private Collection<SpecimenPosition> specimenPositionCollection = new HashSet<SpecimenPosition>();

	/**
	* Retrieves the value of the specimenPositionCollection attribute
	* @return specimenPositionCollection
	**/

	public Collection<SpecimenPosition> getSpecimenPositionCollection(){
		return specimenPositionCollection;
	}

	/**
	* Sets the value of specimenPositionCollection attribute
	**/

	public void setSpecimenPositionCollection(Collection<SpecimenPosition> specimenPositionCollection){
		this.specimenPositionCollection = specimenPositionCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.ContainerType object
	**/
			
	private ContainerType containerType;
	/**
	* Retrieves the value of the containerType attribute
	* @return containerType
	**/
	
	public ContainerType getContainerType(){
		return containerType;
	}
	/**
	* Sets the value of containerType attribute
	**/

	public void setContainerType(ContainerType containerType){
		this.containerType = containerType;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ContainerPosition object
	**/
			
	private ContainerPosition position;
	/**
	* Retrieves the value of the position attribute
	* @return position
	**/
	
	public ContainerPosition getPosition(){
		return position;
	}
	/**
	* Sets the value of position attribute
	**/

	public void setPosition(ContainerPosition position){
		this.position = position;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Container) 
		{
			Container c =(Container)obj; 			 
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
