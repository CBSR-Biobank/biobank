package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class StudyEventAttr  implements IBiobankModel
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
	
	private String permissible;
	/**
	* Retrieves the value of the permissible attribute
	* @return permissible
	**/

	public String getPermissible(){
		return permissible;
	}

	/**
	* Sets the value of permissible attribute
	**/

	public void setPermissible(String permissible){
		this.permissible = permissible;
	}
	
	/**
	* 
	**/
	
	public Boolean required;
	/**
	* Retrieves the value of the required attribute
	* @return required
	**/

	public Boolean getRequired(){
		return required;
	}

	/**
	* Sets the value of required attribute
	**/

	public void setRequired(Boolean required){
		this.required = required;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.GlobalEventAttr object
	**/
			
	private GlobalEventAttr globalEventAttr;
	/**
	* Retrieves the value of the globalEventAttr attribute
	* @return globalEventAttr
	**/
	
	public GlobalEventAttr getGlobalEventAttr(){
		return globalEventAttr;
	}
	/**
	* Sets the value of globalEventAttr attribute
	**/

	public void setGlobalEventAttr(GlobalEventAttr globalEventAttr){
		this.globalEventAttr = globalEventAttr;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Study object
	**/
			
	private Study study;
	/**
	* Retrieves the value of the study attribute
	* @return study
	**/
	
	public Study getStudy(){
		return study;
	}
	/**
	* Sets the value of study attribute
	**/

	public void setStudy(Study study){
		this.study = study;
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
		if(obj instanceof StudyEventAttr) 
		{
			StudyEventAttr c =(StudyEventAttr)obj; 			 
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
