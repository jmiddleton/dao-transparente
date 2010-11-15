package ar.com.tunuyan.strategy.impl;

import java.util.HashMap;
import java.util.Map;

import ar.com.tunuyan.strategy.FinderNamingStrategy;

/**
 * Naming Strategy que cachea las consultas resueltas.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 7, 2010
 */
@SuppressWarnings("unchecked")
public class CacheFinderNamingStrategy extends BaseFinderNamingStrategy implements FinderNamingStrategy {
	Map<String, String> queryCache = new HashMap<String, String>(20);
	Map<String, String> namedQueryCache = new HashMap<String, String>(20);

	@Override
	public String generateScaffoldQuery(Class entityClass, String methodName) {

		String key = entityClass.getSimpleName() + "." + methodName;
		if (!queryCache.containsKey(key)) {
			String query = super.generateScaffoldQuery(entityClass, methodName);
			queryCache.put(key, query);
			return query;
		}
		return queryCache.get(key);
	}

	@Override
	public String getQueryName(Class entityClass, String methodName) {

		String key = entityClass.getSimpleName() + "." + methodName;
		if (!namedQueryCache.containsKey(key)) {
			String query = super.getQueryName(entityClass, methodName);
			namedQueryCache.put(key, query);
			return query;
		}
		return namedQueryCache.get(key);
	}

}
