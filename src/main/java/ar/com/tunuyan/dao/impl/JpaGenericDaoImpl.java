package ar.com.tunuyan.dao.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Repository;

import ar.com.tunuyan.dao.FinderExecutor;
import ar.com.tunuyan.dao.GenericDao;
import ar.com.tunuyan.dao.GenericDaoException;
import ar.com.tunuyan.dao.GenericPaginator;
import ar.com.tunuyan.strategy.FinderNamingStrategy;

/**
 * Implementacion JPA del DAO Generico {@link GenericDao} y de {@link FinderExecutor}.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 6, 2010
 */

// TODO:
// agregar OrderBy
@Repository
public class JpaGenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK>, FinderExecutor {
	private Class<T> entityClass;

	private FinderNamingStrategy namingStrategy;

	private Object delegate;

	private int fetchSize = 0;

	private int maxResults = 0;

	private Object queryCacheRegion;

	private boolean cacheQueries;

	private EntityManager entityManager;

	public JpaGenericDaoImpl() {
	}

	public void init() {
		if (entityManager == null) {
			throw new IllegalArgumentException("'entityManager' is required");
		}
		if (namingStrategy == null) {
			throw new IllegalArgumentException("'namingStrategy' is required");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("'entityClass' is required");
		}
	}

	public void persist(T newEntity) throws GenericDaoException {
		entityManager.persist(newEntity);
	}

	public T get(PK id) {
		return entityManager.find(entityClass, id);
	}

	public void update(T entity) throws GenericDaoException {
		entityManager.merge(entity);
	}

	/**
	 * You cannot call EntityManager.persist() or EntityManager.remove() on a Detached object. {@link http
	 * ://openejb.apache.org/3.0/jpa-concepts.html}
	 */
	public void remove(T entity) throws GenericDaoException {
		Object toRemove = entityManager.merge(entity);
		entityManager.remove(toRemove);
	}

	public void removeById(PK id) throws GenericDaoException {
		T toRemove = entityManager.find(entityClass, id);
		entityManager.remove(toRemove);
	}

	public int count() {
		Long result = (Long) createQuery("select count(o) from " + entityClass.getName() + " o", (Object[]) null)
				.getSingleResult();
		return result != null ? result.intValue() : 0;
	}

	@SuppressWarnings("unchecked")
	public List<T> findAllEntries(int firstResult, int maxResults, String sort) {
		Query query = createQuery("from " + entityClass.getName(), (Object[]) null);
		if (firstResult >= 0) {
			query.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}

	/*
	 * La consulta no soporta parametros nombrados, solo se puede utilizar el ?
	 */
	// TODO: primero buscar una query nombrada, segundo buscar si existe el
	// metodo en el delegate y sino en tercer lugar crear la query on the fly
	// utilizando el nombre del metodo, por ejemplo: findByName donde name es un
	// atributo de la entidad en cuestion.
	@SuppressWarnings("unchecked")
	public List<T> executeFinder(String methodName, final Object... queryArgs) {
		boolean isNotExist = false;

		if (this.namingStrategy == null) {
			throw new IllegalStateException("Setting the property 'namingStrategy' is required");
		}

		// buscamos si hay una query parametrizada.
		final String queryName = namingStrategy.getQueryName(entityClass, methodName);
		try {
			Query queryObject = createNamedQuery(queryName, queryArgs);
			return queryObject.getResultList();
		} catch (IllegalArgumentException iae) {
			isNotExist = true;
		}

		// buscamos y ejecutamos un metodo en el delegado;
		if (isNotExist && this.delegate != null) {
			isNotExist = false;
			try {
				return executeDelegateMethod(methodName, queryArgs);
			} catch (NoSuchMethodException e) {
				isNotExist = true;
			} catch (Exception e) {
			}
		}

		// tratamos de generar la query con base al nombre del metodo.
		if (isNotExist) {
			return executeScaffoldingQuery(methodName, queryArgs);
		}

		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	private List<T> executeScaffoldingQuery(String methodName, final Object[] queryArgs) {

		final String query = namingStrategy.generateScaffoldQuery(entityClass, methodName);

		Query queryObject = createQuery(query, queryArgs);
		return queryObject.getResultList();
	}

	/**
	 * Busca un metodo implementado directamente en el servicio.
	 * 
	 * @param methodName
	 * @param queryArgs
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings({ "unchecked" })
	private List<T> executeDelegateMethod(String methodName, Object... queryArgs) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class[] parameterTypes = new Class[0];

		if (queryArgs != null) {
			parameterTypes = new Class[queryArgs.length];
			for (int i = 0; i < queryArgs.length; i++) {
				parameterTypes[i] = queryArgs[i].getClass();
			}
		}

		Method method = AopUtils.getTargetClass(delegate).getDeclaredMethod(methodName, parameterTypes);
		if (method != null) {
			return (List<T>) method.invoke(delegate, queryArgs);
		}

		return Collections.emptyList();
	}

	/**
	 * Crea una query.
	 * 
	 * @param query
	 * @param queryArgs
	 * @return
	 */
	public Query createQuery(final String query, final Object[] queryArgs) {
		GenericPaginator paginator = null;

		Query queryObject = entityManager.createQuery(query);

		if (queryArgs != null) {
			for (int i = 0; i < queryArgs.length; i++) {
				if (i == queryArgs.length - 1
						&& (queryArgs[i] == null || GenericPaginator.class.isAssignableFrom(queryArgs[i].getClass()))) {
					paginator = (GenericPaginator) queryArgs[i];
					break; // tiene que ser el ultimo.
				} else {
					queryObject.setParameter(i + 1, queryArgs[i]);
				}
			}

			if (paginator != null) {
				prepareQuery(queryObject, paginator);
			} else {
				prepareQuery(queryObject, null);
			}
		}
		return queryObject;
	}

	/**
	 * Crea una named query.
	 * 
	 * @param queryName
	 * @param queryArgs
	 * @return
	 */
	private Query createNamedQuery(String queryName, Object[] queryArgs) {
		GenericPaginator paginator = null;

		Query queryObject = entityManager.createNamedQuery(queryName);
		if (queryArgs != null) {
			for (int i = 0; i < queryArgs.length; i++) {
				if (i == queryArgs.length - 1
						&& (queryArgs[i] == null || GenericPaginator.class.isAssignableFrom(queryArgs[i].getClass()))) {
					paginator = (GenericPaginator) queryArgs[i];
					break; // tiene que ser el ultimo.
				} else {
					queryObject.setParameter(i + 1, queryArgs[i]);
				}
			}

			if (paginator != null) {
				prepareQuery(queryObject, paginator);
			} else {
				prepareQuery(queryObject, null);
			}
		}
		return queryObject;
	}

	protected void prepareQuery(Query query, GenericPaginator paginator) {
		/*
		 * EntityManagerFactory emf = super.getJpaTemplate().getEntityManagerFactory(); if (emf != null) {
		 * EntityManagerFactoryUtils.applyTransactionTimeout(query, super.getJpaTemplate().getEntityManagerFactory()); }
		 */

		if (cacheQueries) {
			query.setHint("org.hibernate.cacheable", Boolean.TRUE);
			if (queryCacheRegion != null) {
				query.setHint("org.hibernate.cacheRegion", queryCacheRegion);
			}
		}

		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		if (fetchSize > 0) {
			query.setHint("org.hibernate.fetchSize", fetchSize);
		}

		if (paginator != null) {
			if (paginator.getMaxResults() > 0) {
				query.setMaxResults(paginator.getMaxResults());
			}
			if (paginator.getOffset() >= 0) {
				query.setFirstResult(paginator.getOffset());
			}
		}

		query.setHint("org.hibernate.readOnly", Boolean.TRUE);
	}

	public void setNamingStrategy(FinderNamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	/**
	 * Delegado que implementa metodos de la interface definida en la propiedad "proxyInterfaces" del
	 * org.springframework.aop.framework.ProxyFactoryBean
	 * 
	 * @param delegate
	 */
	public void setDelegate(Object delegate) {
		this.delegate = delegate;
	}

	/**
	 * Set the fetch size for this HibernateTemplate. This is important for processing large result sets: Setting this
	 * higher than the default value will increase processing speed at the cost of memory consumption; setting this
	 * lower can avoid transferring row data that will never be read by the application.
	 * <p>
	 * Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Set the maximum number of rows for this HibernateTemplate. This is important for processing subsets of large
	 * result sets, avoiding to read and hold the entire result set in the database or in the JDBC driver if we're never
	 * interested in the entire result in the first place (for example, when performing searches that might return a
	 * large number of matches).
	 * <p>
	 * Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Set the name of the cache region for queries executed by this template.
	 * <p>
	 * If this is specified, it will be applied to all Query and Criteria objects created by this template (including
	 * all queries through find methods).
	 * <p>
	 * The cache region will not take effect unless queries created by this template are configured to be cached via the
	 * "cacheQueries" property.
	 * 
	 * @see #setCacheQueries
	 * @see org.hibernate.Query#setCacheRegion
	 * @see org.hibernate.Criteria#setCacheRegion
	 */
	public void setQueryCacheRegion(Object queryCacheRegion) {
		this.queryCacheRegion = queryCacheRegion;
	}

	/**
	 * Set whether to cache all queries executed by this template.
	 * <p>
	 * If this is "true", all Query and Criteria objects created by this template will be marked as cacheable (including
	 * all queries through find methods).
	 * <p>
	 * To specify the query region to be used for queries cached by this template, set the "queryCacheRegion" property.
	 * 
	 * @see #setQueryCacheRegion
	 * @see org.hibernate.Query#setCacheable
	 * @see org.hibernate.Criteria#setCacheable
	 */
	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public List<T> findByExample(T entity, int firstResult, int maxResults) {
		throw new UnsupportedOperationException("No soportado en JPA");
	}

}
