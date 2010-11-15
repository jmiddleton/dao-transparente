package ar.com.tunuyan;

import java.io.Serializable;

/**
 * Define la clave primaria de una definicion de procesos.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion Oct 6, 2009
 */
public class ProcessDefinitionId implements Serializable {

	private static final long serialVersionUID = 3859082789783714342L;

	private String id;

	private int version;

	public ProcessDefinitionId() {
	}

	public ProcessDefinitionId(String id, int version) {
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessDefinitionId other = (ProcessDefinitionId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}