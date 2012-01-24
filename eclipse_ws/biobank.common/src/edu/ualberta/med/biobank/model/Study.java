package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class Study  implements IBiobankModel
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
	private String name;
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
	private String nameShort;
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
	* An associated edu.ualberta.med.biobank.model.AliquotedSpecimen object's collection 
	**/
			
	private Collection<AliquotedSpecimen> aliquotedSpecimenCollection = new HashSet<AliquotedSpecimen>();

	/**
	* Retrieves the value of the aliquotedSpecimenCollection attribute
	* @return aliquotedSpecimenCollection
	**/

	public Collection<AliquotedSpecimen> getAliquotedSpecimenCollection(){
		return aliquotedSpecimenCollection;
	}

	/**
	* Sets the value of aliquotedSpecimenCollection attribute
	**/

	public void setAliquotedSpecimenCollection(Collection<AliquotedSpecimen> aliquotedSpecimenCollection){
		this.aliquotedSpecimenCollection = aliquotedSpecimenCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Patient object's collection 
	**/
			
	private Collection<Patient> patientCollection = new HashSet<Patient>();

	/**
	* Retrieves the value of the patientCollection attribute
	* @return patientCollection
	**/

	public Collection<Patient> getPatientCollection(){
		return patientCollection;
	}

	/**
	* Sets the value of patientCollection attribute
	**/

	public void setPatientCollection(Collection<Patient> patientCollection){
		this.patientCollection = patientCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Site object's collection 
	**/
			
	private Collection<Site> siteCollection = new HashSet<Site>();

	/**
	* Retrieves the value of the siteCollection attribute
	* @return siteCollection
	**/

	public Collection<Site> getSiteCollection(){
		return siteCollection;
	}

	/**
	* Sets the value of siteCollection attribute
	**/

	public void setSiteCollection(Collection<Site> siteCollection){
		this.siteCollection = siteCollection;
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
	* An associated edu.ualberta.med.biobank.model.StudyEventAttr object's collection 
	**/
			
	private Collection<StudyEventAttr> studyEventAttrCollection = new HashSet<StudyEventAttr>();

	/**
	* Retrieves the value of the studyEventAttrCollection attribute
	* @return studyEventAttrCollection
	**/

	public Collection<StudyEventAttr> getStudyEventAttrCollection(){
		return studyEventAttrCollection;
	}

	/**
	* Sets the value of studyEventAttrCollection attribute
	**/

	public void setStudyEventAttrCollection(Collection<StudyEventAttr> studyEventAttrCollection){
		this.studyEventAttrCollection = studyEventAttrCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Contact object's collection 
	**/
			
	private Collection<Contact> contactCollection = new HashSet<Contact>();

	/**
	* Retrieves the value of the contactCollection attribute
	* @return contactCollection
	**/

	public Collection<Contact> getContactCollection(){
		return contactCollection;
	}

	/**
	* Sets the value of contactCollection attribute
	**/

	public void setContactCollection(Collection<Contact> contactCollection){
		this.contactCollection = contactCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.ResearchGroup object
	**/
			
	private ResearchGroup researchGroup;
	/**
	* Retrieves the value of the researchGroup attribute
	* @return researchGroup
	**/
	
	public ResearchGroup getResearchGroup(){
		return researchGroup;
	}
	/**
	* Sets the value of researchGroup attribute
	**/

	public void setResearchGroup(ResearchGroup researchGroup){
		this.researchGroup = researchGroup;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.SourceSpecimen object's collection 
	**/
			
	private Collection<SourceSpecimen> sourceSpecimenCollection = new HashSet<SourceSpecimen>();

	/**
	* Retrieves the value of the sourceSpecimenCollection attribute
	* @return sourceSpecimenCollection
	**/

	public Collection<SourceSpecimen> getSourceSpecimenCollection(){
		return sourceSpecimenCollection;
	}

	/**
	* Sets the value of sourceSpecimenCollection attribute
	**/

	public void setSourceSpecimenCollection(Collection<SourceSpecimen> sourceSpecimenCollection){
		this.sourceSpecimenCollection = sourceSpecimenCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Study) 
		{
			Study c =(Study)obj; 			 
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
