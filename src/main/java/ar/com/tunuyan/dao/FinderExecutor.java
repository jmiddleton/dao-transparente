package ar.com.tunuyan.dao;

/**
 * Invocador de consultas dinamicas. Este componente permite ejecutar consultas
 * de acuerdo al nombre del metodo especificado.
 * <p>
 * Ejemplo: metodo: findByNameAndVersion
 * Resultado: WHERE NAME=? AND VERSION=?
 * 
 * La consulta resultante filtrara por Name y Version. Es importante aclarar que
 * estos nombres deben ser atributos de una entidad mapeada a la Base de Datos
 * ya sea por Hiberante o mediante la anotacion {@link javax.persistence.Entity
 * @Entity}.
 * <p>
 * Si se quiere paginacion, se debe agregar como ultimo argumento un objeto de tipo {@link GenericPaginator} 
 * el cual se utilizara para paginar la consulta.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 6, 2010
 */
public interface FinderExecutor {

	/**
	 * Invoca una consulta con base al methodName pasado como parametro.
	 * 
	 * @param methodName
	 *            : nombre del metodo a ejecutar.
	 * @param args
	 *            : argumentos para invocar la consulta.
	 * @return
	 */
	Object executeFinder(String methodName, Object... args);

}
