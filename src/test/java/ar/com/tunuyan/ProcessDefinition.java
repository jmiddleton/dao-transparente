package ar.com.tunuyan;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * Definicion de un proceso.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion Jan 4, 2010
 */
@Entity
@NamedQuery(name = "ProcessDefinition.findByNombre", query = "SELECT OBJECT(pd) FROM ProcessDefinition pd WHERE pd.name = ?")
@Table(name = "PROCESS_DEFINITIONS")
@IdClass(ProcessDefinitionId.class)
public class ProcessDefinition {

	public static int DEFAULT_VERSION = 1;

	private String id;

	private int version = DEFAULT_VERSION;

	private String name;

	/** Definitions in BPMN */
	private String deploymentUnit;

	private String state;

	private String description;

	private String xml;

	private transient Process bpmnProcess;

	private Date created;

	private Date lastModified;

	private Date validFrom;

	private Date validTo;

	private String author;

	private String publicationStatus; // UNDER_REVISION, RELEASED, UNDER_TEST

	private Long optVersion;

	/**
	 * Is this a <em>inMemory</em> process? InMemory processes are not persisted
	 * in the store.
	 */
	private boolean inMemory;

	private String initialActivity;

	private String expressionLanguage;

	private String typeLanguage;

	public ProcessDefinition() {
	}

	public ProcessDefinition(String xmlDefinition) {
		this.xml = xmlDefinition;
	}

	@Id
	public String getId() {
		return id;
	}

	@Id
	public int getVersion() {
		return version;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getState() {
		return state;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreated() {
		return created;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setState(String status) {
		this.state = status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreated(Date createdDate) {
		this.created = createdDate;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	@Lob
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Long getOptVersion() {
		return optVersion;
	}

	@Version
	public void setOptVersion(Long timestamp) {
		this.optVersion = timestamp;
	}

	public String getAuthor() {
		return author;
	}

	public String getPublicationStatus() {
		return publicationStatus;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public String getDeploymentUnit() {
		return deploymentUnit;
	}

	public void setDeploymentUnit(String definition) {
		this.deploymentUnit = definition;
	}

	public Date getLastModified() {
		return lastModified;
	}

	@Temporal(TemporalType.DATE)
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isInMemory() {
		return inMemory;
	}

	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
	}

	@Transient
	public Process getBpmnProcess() {
		return bpmnProcess;
	}

	public void setBpmnProcess(Process bpmnProcess) {
		this.bpmnProcess = bpmnProcess;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessDefinition [id=").append(id).append(", version=").append(version).append(", created=").append(created).append(", definition=")
				.append(deploymentUnit).append(", description=").append(description).append(", lastModified=").append(lastModified).append(", name=").append(name)
				.append(", author=").append(author).append(", optVersion=").append(optVersion).append(", publicationStatus=").append(publicationStatus).append(", state=")
				.append(state).append(", validFrom=").append(validFrom).append(", validTo=").append(validTo).append("]");
		return builder.toString();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + version;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessDefinition other = (ProcessDefinition) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public String getInitialActivity() {
		return initialActivity;
	}

	public void setInitialActivity(String initialActivity) {
		this.initialActivity = initialActivity;
	}

	public String getExpressionLanguage() {
		return expressionLanguage;
	}

	public void setExpressionLanguage(String expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}

	public String getTypeLanguage() {
		return typeLanguage;
	}

	public void setTypeLanguage(String typeLanguage) {
		this.typeLanguage = typeLanguage;
	}

}