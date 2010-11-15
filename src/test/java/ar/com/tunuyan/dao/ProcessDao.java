package ar.com.tunuyan.dao;

import java.util.Date;
import java.util.List;

import ar.com.tunuyan.ProcessDefinition;
import ar.com.tunuyan.ProcessDefinitionId;

public interface ProcessDao extends GenericDao<ProcessDefinition, ProcessDefinitionId>, FinderExecutor {
	// esta query se resuelve con una namedQuery en el objeto processdefinition
	List<ProcessDefinition> findByNombre(String nombre);

	// esta query se ejecuta mediante un metodo implementado en el DAO
	// especifico de processDefinition
	//List<ProcessDefinition> findByVersionDelProceso(String version);

	// estas queries se calculan sola con base al nombre del metodo.
	List<ProcessDefinition> findByNameAndVersion(String name, int version);

	List<ProcessDefinition> findByNameLikeAndCreatedGreaterThan(String name, Date desde);
	List<ProcessDefinition> findByNameLikeAndCreatedBetween(String name, Date desde, Date hasta);
}
