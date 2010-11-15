package ar.com.tunuyan.processor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ar.com.tunuyan.dao.GenericDao;

public class HbmGenericDaoBFPPTestCase {

	@Test
	public void testBean() {
		HibernateMetadataBeanFactoryPostProcessor rg = new HibernateMetadataBeanFactoryPostProcessor();

		rg.setPrefix("dao");
		rg.setBasePackages(new String[] { "ar.com.anita.model.definition", "ar.com.tunuyan" });

		List<String> filter = new ArrayList<String>(2);
		// filter.add("regex:.*Process.*");
		filter.add("regex:.*SipAsegurabilidad.*");

		rg.setExcludeFilters(filter);

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:META-INF/hbmProcessorContext.xml", "classpath*:META-INF/hbmApplicationContext.xml" });

		rg.setSessionFactory((SessionFactory) context.getBean("sessionFactory"));
		rg.postProcessBeanFactory(context.getBeanFactory());

		String idBean = "daoProcessDefinition";
		Assert.assertNotNull("Bean no encontrado", context.getBean(idBean));
		Object proxy = context.getBean(idBean);
		Assert.assertTrue("El bean encontrado no es un dao generico.", GenericDao.class.isAssignableFrom(proxy
				.getClass()));

		try {
			Assert.assertNotNull("Bean excluido incorrectamente", context.getBean("daoProcessInstance"));
		} catch (NoSuchBeanDefinitionException e) {
			Assert.assertTrue("Bean excluido correctamente", true);
		}
	}

}
