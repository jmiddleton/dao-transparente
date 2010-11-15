package ar.com.tunuyan.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ar.com.tunuyan.ProcessDefinition;

public class ProcessDaoImpl {
	private EntityManager entityManager;

	public ProcessDaoImpl() {
	}

	public List<ProcessDefinition> findByVersionDelProceso(String version) {
		// aqui se puede hacer de todo y al final devolver el resultado.
		return Collections.emptyList();
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
