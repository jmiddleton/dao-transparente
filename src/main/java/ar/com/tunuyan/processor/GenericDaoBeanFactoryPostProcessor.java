package ar.com.tunuyan.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import ar.com.tunuyan.dao.GenericDao;

/**
 * BeanFactoryPostProcessor que genera DAOs genericos del tipo
 * {@link GenericDao}.
 * <p>
 * Para buscar las clases anotadas, se debe especificar
 * <code>basePackages</code>. Opcionalmente se puede especificar filtros para
 * incluir/excluir clases que requieren o no Dao.
 * <p>
 * Se debe especificar el bean padre <code>parentBeanName</code> que define la
 * implementacion de {@link GenericDao} a utilizar.
 * <p>
 * Por ultimo, mediante la propiedad <code>prefix</code> se puede especificar el
 * prefijo del nombre del bean DAO a generar.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 12, 2010
 */
public abstract class GenericDaoBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor, Ordered {

	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	private final Logger logger = LoggerFactory
			.getLogger(GenericDaoBeanFactoryPostProcessor.class);

	private int order = Ordered.LOWEST_PRECEDENCE;

	protected String prefix = "dao";

	protected String parentBeanName = "abstractDaoTarget";

	protected String[] basePackages;

	protected List<TypeFilter> includeFilters = new ArrayList<TypeFilter>();

	protected List<TypeFilter> excludeFilters = new ArrayList<TypeFilter>();

	protected ConfigurableListableBeanFactory beanFactory;

	protected ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	protected MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
			this.resourcePatternResolver);

	public GenericDaoBeanFactoryPostProcessor() {

	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

		for (String basePackage : basePackages) {
			Set<MetadataReader> candidates = findCandidateComponents(basePackage);
			for (MetadataReader candidate : candidates) {
				String beanName = prefix
						+ ClassUtils.getShortName(candidate.getClassMetadata()
								.getClassName());
				if (checkCandidate(beanName)) {
					BeanDefinition beanDefinition = createBeanDefinition(candidate);
					if (logger.isDebugEnabled()) {
						logger.debug("Registering transparent dao '" + beanName
								+ "' for entity: '"
								+ candidate.getClassMetadata().getClassName()
								+ "'");
					}
					((BeanDefinitionRegistry) this.beanFactory)
							.registerBeanDefinition(beanName, beanDefinition);
				}
			}
		}
	}

	private BeanDefinition createBeanDefinition(MetadataReader candidate) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition("org.springframework.aop.framework.ProxyFactoryBean");
		builder.setScope(BeanDefinition.SCOPE_SINGLETON);
		builder.setAutowireMode(0);

		GenericBeanDefinition parentBeanDefinition = new GenericBeanDefinition(
				this.beanFactory.getBeanDefinition(parentBeanName));
		parentBeanDefinition.getPropertyValues().add(
				"entityClass",
				new TypedStringValue(candidate.getClassMetadata()
						.getClassName()));

		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(
				parentBeanDefinition, parentBeanDefinition.toString());

		builder.addPropertyValue("target", beanDefinitionHolder);// this.beanFactory.getBeanDefinition(parentBeanName)
		return builder.getBeanDefinition();
	}

	private boolean checkCandidate(String beanName) {
		if (!this.beanFactory.containsBeanDefinition(beanName)) {
			return true;
		}
		return false;
	}

	/**
	 * Scan the class path for candidate components.
	 * 
	 * @param basePackage
	 *            the package to check for annotated classes
	 * @return a corresponding Set of autodetected bean definitions
	 */
	public Set<MetadataReader> findCandidateComponents(String basePackage) {
		Set<MetadataReader> candidates = new LinkedHashSet<MetadataReader>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ basePackage.replace('.', '/')
					+ "/"
					+ DEFAULT_RESOURCE_PATTERN;
			Resource[] resources = this.resourcePatternResolver
					.getResources(packageSearchPath);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					try {
						MetadataReader metadataReader = this.metadataReaderFactory
								.getMetadataReader(resource);
						if (isCandidateComponent(metadataReader)) {
							candidates.add(metadataReader);
						}
					} catch (Throwable ex) {
						throw new BeanDefinitionStoreException(
								"Failed to read candidate component class: "
										+ resource, ex);
					}
				}
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"I/O failure during classpath scanning", ex);
		}
		return candidates;
	}

	/**
	 * Determine whether the given class does not match any exclude filter and
	 * does match at least one include filter.
	 * 
	 * @param metadataReader
	 *            the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	protected abstract boolean isCandidateComponent(
			MetadataReader metadataReader) throws IOException;

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setBasePackages(String... basePackages) {
		this.basePackages = basePackages;
	}

	public void setIncludeFilters(List<String> filters) {
		for (String value : filters) {
			if (value.startsWith("regex:"))
				this.includeFilters.add(new RegexPatternTypeFilter(Pattern
						.compile(value.substring(6))));
		}
	}

	public void setExcludeFilters(List<String> filters) {
		for (String value : filters) {
			if (value.startsWith("regex:"))
				this.excludeFilters.add(new RegexPatternTypeFilter(Pattern
						.compile(value.substring(6))));
		}
	}

	/**
	 * El id del bean que implementa un dao generico. Puede ser JPA o Hibernate.
	 * 
	 * @param parentBeanName
	 */
	public void setParentBeanName(String parentBeanName) {
		this.parentBeanName = parentBeanName;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
