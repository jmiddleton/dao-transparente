package ar.com.tunuyan.dao.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.springframework.aop.support.AopUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import ar.com.tunuyan.dao.FinderExecutor;
import ar.com.tunuyan.dao.GenericDao;
import ar.com.tunuyan.dao.GenericDaoException;
import ar.com.tunuyan.strategy.FinderNamingStrategy;

/**
 * Implementacion Hibernate del DAO Generico {@link GenericDao} y de
 * {@link FinderExecutor}.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 6, 2010
 */
@Repository
public class HibernateGenericDaoImpl<T, PK extends Serializable> extends HibernateDaoSupport implements
		GenericDao<T, PK>, FinderExecutor {
	private Class<T> entityClass;

	private FinderNamingStrategy namingStrategy;

	private Object delegate;

	private int fetchSize = 0;

	private int maxResults = 0;

	private String queryCacheRegion;

	private boolean cacheQueries;

	public HibernateGenericDaoImpl() {
	}

	public void init() {
		super.afterPropertiesSet();

		if (namingStrategy == null) {
			throw new IllegalArgumentException("'namingStrategy' is required");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("'entityClass' is required");
		}
	}

	public void persist(T newEntity) throws GenericDaoException {
		getHibernateTemplate().persist(newEntity);
	}

	public T get(PK id) {
		return getHibernateTemplate().get(entityClass, id);
	}

	public void update(T entity) throws GenericDaoException {
		getHibernateTemplate().update(entity);
	}

	/**
	 * You cannot call EntityManager.persist() or EntityManager.remove() on a
	 * Detached object. {@link http://openejb.apache.org/3.0/jpa-concepts.html}
	 */
	public void remove(T entity) throws GenericDaoException {
		T toRemove = getHibernateTemplate().merge(entity);
		getHibernateTemplate().delete(toRemove);
	}

	public void removeById(PK id) throws GenericDaoException {
		T toRemove = get(id);
		getHibernateTemplate().delete(toRemove);
	}

	public int count() {
		return (Integer) getHibernateTemplate().find("select count(*) from " + entityClass.getName()).iterator().next();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAllEntries(final int firstResult, final int maxResults, final String sort) {

		return getHibernateTemplate().execute(new HibernateCallback<List<T>>() {
			public List<T> doInHibernate(Session session) throws HibernateException {
				Criteria criteria = session.createCriteria(entityClass);

				criteria.setFirstResult(firstResult);
				if (maxResults > 0) {
					criteria.setMaxResults(maxResults);
					criteria.setFetchSize(maxResults);
				}

				if (sort != null) {
					StringTokenizer token = new StringTokenizer(sort, ",");
					while (token.hasMoreTokens()) {
						String column = token.nextToken();
						if (column.contains("null"))
							continue;

						if (column.startsWith("+")) {
							criteria.addOrder(Order.asc(column.substring(1)));
						} else {
							criteria.addOrder(Order.desc(column.substring(1)));
						}
					}
				}

				return criteria.list();
			}
		});
	}

	/*
	 * La consulta no soporta parametros nombrados, solo se puede utilizar el ?
	 */
	// TODO: primero buscar una query nombrada, segundo buscar si existe el
	// metodo en el delegate y sino en tercer lugar crear la query on the fly
	// utilizando el nombre del metodo, por ejemplo: findByName donde name es un
	// atributo de la entidad en cuestion.
	@SuppressWarnings("unchecked")
	public List<T> executeFinder(String methodName, Object... queryArgs) {
		boolean isNotExist = false;

		if (this.namingStrategy == null) {
			throw new IllegalStateException("Setting the property 'namingStrategy' is required");
		}

		// buscamos si hay una query parametrizada.
		final String queryName = namingStrategy.getQueryName(entityClass, methodName);
		try {
			return getHibernateTemplate().findByNamedQuery(queryName, queryArgs);
		} catch (DataAccessException iae) {
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

	public List<T> findByExample(final T entity, final int firstResult, final int maxResults) {
		return getHibernateTemplate().execute(new HibernateCallback<List<T>>() {
			public List<T> doInHibernate(Session session) throws HibernateException {
				Criteria criteria = session.createCriteria(entityClass);

				criteria.setFirstResult(firstResult);
				if (maxResults > 0) {
					criteria.setMaxResults(maxResults);
					criteria.setFetchSize(maxResults);
				}

				criteria.add(Example.create(entity).ignoreCase());

				return criteria.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<T> executeScaffoldingQuery(String methodName, final Object[] queryArgs) {
		// TODO: ver de agregar soporte para GenericPaginator
		final String query = namingStrategy.generateScaffoldQuery(entityClass, methodName);

		getHibernateTemplate().setFetchSize(fetchSize);
		getHibernateTemplate().setMaxResults(maxResults);
		getHibernateTemplate().setCacheQueries(cacheQueries);
		getHibernateTemplate().setQueryCacheRegion(queryCacheRegion);

		return getHibernateTemplate().find(query, queryArgs);
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
	@SuppressWarnings("unchecked")
	private List<T> executeDelegateMethod(String methodName, Object[] queryArgs) throws SecurityException,
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

	public void setNamingStrategy(FinderNamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	/**
	 * Delegado que implementa metodos de la interface definida en la propiedad
	 * "proxyInterfaces" del org.springframework.aop.framework.ProxyFactoryBean
	 * 
	 * @param delegate
	 */
	public void setDelegate(Object delegate) {
		this.delegate = delegate;
	}

	/**
	 * Set the fetch size for this HibernateTemplate. This is important for
	 * processing large result sets: Setting this higher than the default value
	 * will increase processing speed at the cost of memory consumption; setting
	 * this lower can avoid transferring row data that will never be read by the
	 * application.
	 * <p>
	 * Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Set the maximum number of rows for this HibernateTemplate. This is
	 * important for processing subsets of large result sets, avoiding to read
	 * and hold the entire result set in the database or in the JDBC driver if
	 * we're never interested in the entire result in the first place (for
	 * example, when performing searches that might return a large number of
	 * matches).
	 * <p>
	 * Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Set the name of the cache region for queries executed by this template.
	 * <p>
	 * If this is specified, it will be applied to all Query and Criteria
	 * objects created by this template (including all queries through find
	 * methods).
	 * <p>
	 * The cache region will not take effect unless queries created by this
	 * template are configured to be cached via the "cacheQueries" property.
	 * 
	 * @see #setCacheQueries
	 * @see org.hibernate.Query#setCacheRegion
	 * @see org.hibernate.Criteria#setCacheRegion
	 */
	public void setQueryCacheRegion(String queryCacheRegion) {
		this.queryCacheRegion = queryCacheRegion;
	}

	/**
	 * Set whether to cache all queries executed by this template.
	 * <p>
	 * If this is "true", all Query and Criteria objects created by this
	 * template will be marked as cacheable (including all queries through find
	 * methods).
	 * <p>
	 * To specify the query region to be used for queries cached by this
	 * template, set the "queryCacheRegion" property.
	 * 
	 * @see #setQueryCacheRegion
	 * @see org.hibernate.Query#setCacheable
	 * @see org.hibernate.Criteria#setCacheable
	 */
	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public Query createQuery(String query, Object[] queryArgs) {
		// TODO Auto-generated method stub
		return null;
	}
}
