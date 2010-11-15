package ar.com.tunuyan.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Definicion de un Dao generico.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $Rev$
 * @date $Date$
 */
public interface GenericDao<T, PK extends Serializable> {

	Class<T> getEntityClass();

	/**
	 * Persiste la nueva entidad a la base de datos.
	 * 
	 * @param newEntity
	 * @throws GenericDaoException
	 */
	void persist(T newEntity) throws GenericDaoException;

	/**
	 * Consulta una entidad previamente guardado en la base de datos.
	 * 
	 * @param id
	 * @return entity or null.
	 */
	T get(PK id);

	/**
	 * Actualiza una entidad.
	 * 
	 * @param entity
	 * @throws GenericDaoException
	 */
	void update(T entity) throws GenericDaoException;

	/**
	 * Elimina una entidad.
	 * 
	 * @param entity
	 * @throws GenericDaoException
	 */
	void remove(T entity) throws GenericDaoException;

	/**
	 * Elimina una entidad con un id dado.
	 * 
	 * @param id
	 * @throws GenericDaoException
	 */
	void removeById(PK id) throws GenericDaoException;

	/**
	 * Devuelte todas las entidades dentro de los parametros especificados. Si
	 * firstResult y maxResults son menores de cero, entonces se hace un
	 * findAll.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param sort
	 *            : a separated comma order properties: +=ASC, -=DESC, for
	 *            example: +id,-processName
	 * @return
	 */
	List<T> findAllEntries(int firstResult, int maxResults, String sort);

	/**
	 * Realize una consulta basada en un ejemplo. Ver:
	 * http://docs.jboss.org/hibernate
	 * /core/3.3/reference/en/html/querycriteria.html#querycriteria-examples
	 * 
	 * @param entity
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	List<T> findByExample(T entity, int firstResult, int maxResults);

	/**
	 * Devuelve la cantidad de registros.
	 * 
	 * @return long
	 */
	int count();

}
