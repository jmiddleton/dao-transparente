package ar.com.tunuyan.dao.impl;

import java.util.StringTokenizer;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import ar.com.tunuyan.dao.FinderExecutor;

/**
 * Interceptor que permite ejecutar dinamicamente finder con prefijos
 * determinados. La idea de este interceptor es ver si la invocacion que esta en
 * curso es 'findBy' definida en la interface del DAO. Si es asi, entonces se
 * invoca directamente al metodo del DAO, sino se invoca mediante un
 * {@link FinderExecutor} que realiza una consulta dinamica. <b>
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 6, 2010
 */
public class FinderInterceptor implements MethodInterceptor {
	private String prefixes;

	@SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation pjp) throws Throwable {
		Class targetClass = AopUtils.getTargetClass(pjp.getThis());

		if (implementsInterface(targetClass)) {
			FinderExecutor finder = (FinderExecutor) pjp.getThis();

			String methodName = pjp.getMethod().getName();
			if (isFinderInvocation(methodName)) {
				return finder.executeFinder(methodName, pjp.getArguments());
			} else {
				return pjp.proceed();
			}
		} else {
			return pjp.proceed();
		}
	}

	/**
	 * @param methodName
	 * @return
	 */
	private boolean isFinderInvocation(String methodName) {
		StringTokenizer tokenizer = new StringTokenizer(prefixes, ",");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (methodName.startsWith(token)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean implementsInterface(Class intf) {
		return FinderExecutor.class.isAssignableFrom(intf);
	}

	public void setPrefixes(String prefixes) {
		this.prefixes = prefixes;
	}
}
