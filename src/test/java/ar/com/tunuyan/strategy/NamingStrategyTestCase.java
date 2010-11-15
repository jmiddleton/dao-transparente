package ar.com.tunuyan.strategy;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.tunuyan.ProcessDefinition;
import ar.com.tunuyan.strategy.impl.BaseFinderNamingStrategy;

public class NamingStrategyTestCase {

	@Test
	public void testGenerateScaffoldQuery() {
		BaseFinderNamingStrategy strategy = new BaseFinderNamingStrategy();
		strategy.setPrefixes("findBy, buscar");

		Class<ProcessDefinition> clazz = ProcessDefinition.class;
		String query = "select e from " + clazz.getName() + " e where";

		Assert.assertEquals("prueba de palabra compuesta", query + " e.publicationStatus= ?", strategy.generateScaffoldQuery(clazz,
				"findByPublicationStatus"));

		Assert.assertEquals("1", query + " e.name= ?", strategy.generateScaffoldQuery(clazz, "findByName"));
		Assert.assertEquals("2", query + " e.name= ? and e.version= ?", strategy.generateScaffoldQuery(clazz, "findByNameAndVersion"));
		Assert.assertEquals("3", query + " e.name like ?", strategy.generateScaffoldQuery(clazz, "findByNameLike"));
		Assert.assertEquals("4", query + " e.name like ? and e.created between ? and ?", strategy.generateScaffoldQuery(clazz,
				"findByNameLikeAndCreatedBetween"));
		Assert.assertEquals("5", query + " e.created > ?", strategy.generateScaffoldQuery(clazz, "findByCreatedGreaterThan"));
		Assert.assertEquals("6", query + " e.created < ?", strategy.generateScaffoldQuery(clazz, "findByCreatedLessThan"));

		Assert.assertEquals("7", query + " e.state in(?)", strategy.generateScaffoldQuery(clazz, "findByStateIn"));

		Assert.assertEquals("8", query + " e.name= ? and e.publicationStatus= ?", strategy.generateScaffoldQuery(clazz,
				"findByNameAndPublicationStatus"));
		Assert.assertEquals("9", query + " e.name!= ?", strategy.generateScaffoldQuery(clazz, "findByNameNotEqual"));
		Assert.assertEquals("10", query + " e.name is not null", strategy.generateScaffoldQuery(clazz, "findByNameIsNotNull"));
	}

}
