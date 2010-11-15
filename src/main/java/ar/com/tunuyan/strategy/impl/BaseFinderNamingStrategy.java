package ar.com.tunuyan.strategy.impl;

import java.util.StringTokenizer;

import ar.com.tunuyan.strategy.FinderNamingStrategy;

/**
 * Default estrategia para generar un identificador de consulta parametrizadas
 * (NamedQuery) o en un archivo de Hiberante. <br>
 * Para el caso del identificador de la consulta, esta estrategia devuelve
 * NombreClase.nombreMetodo. <br>
 * Para el caso de la consulta scaffold, se devuelve una consulta segun el
 * nombre del metodo.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 6, 2010
 */
public class BaseFinderNamingStrategy implements FinderNamingStrategy {

	private String prefixes;

	private String reservedKeyAsString;

	private static String[] reservedKey = { "And", "Or", "Like", "In", "Between", "GreaterThan", "LessThan", "NotEqual", "IsNotNull", "IsNull" };

	private static String[] reservedKeySQL = { " and", " or", " like ?", " in(?)", " between ? and ?", " > ?", " < ?", "!= ?", " is not null", " is null" };

	public BaseFinderNamingStrategy() {
		for (int i = 0; i < reservedKey.length; i++) {
			reservedKeyAsString = reservedKeyAsString + "," + reservedKey[i];
		}

	}

	public String getQueryName(Class entityClass, String methodName) {
		return entityClass.getSimpleName() + "." + methodName;
	}

	// TODO desarmar el nombre del metodo para obtener los atributos por los
	// cuales consultar, luego generar una query de la entidad filtrando por
	// los atributos encontrados.
	public String generateScaffoldQuery(Class entityClass, String methodName) {

		StringBuffer query = new StringBuffer("select e from ");
		query.append(entityClass.getName());
		query.append(" e where");

		String startWith = getStartWith(methodName);
		String resto = methodName.substring(startWith.length());

		while (resto.length() > 0) {
			String word = getWord(resto);

			if (isOperator(word)) {
				if (!("And".equals(word) || "Or".equals(word))) {
					query.delete(query.length() - 3, query.length());
				}
				query.append(getReservedSql(word));
			} else {
				query.append(" e.").append(Character.toLowerCase(word.charAt(0)) + word.substring(1));
				query.append("= ?");
			}

			if (resto.length() > 0)
				resto = resto.substring(word.length());
		}
		return query.toString();
	}

	/**
	 * Busca el nombre del atributo.
	 * 
	 * @param str
	 */
	private String getWord(String str) {
		StringBuffer word = new StringBuffer();

		if (isOperator(str)) {
			return str;
		}

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (i > 0 && Character.isUpperCase(ch) && (startIsOperator(str.substring(i)) || isOperator(word.toString()))) {
				break;
			} else {
				word.append(ch);
			}
		}

		return word.toString();
	}

	private boolean startIsOperator(String str) {
		if (str.length() > 0) {
			for (int i = 0; i < reservedKey.length; i++) {
				if (str.startsWith(reservedKey[i]))
					return true;
			}
		}
		return false;
	}

	private boolean isOperator(String str) {
		return reservedKeyAsString.indexOf(str) >= 0;
	}

	private String getReservedSql(String str) {
		// si la palabra es reservada, entonces devuelvo true.
		if (str.length() > 0) {
			for (int i = 0; i < reservedKey.length; i++) {
				if (str.equals(reservedKey[i]))
					return reservedKeySQL[i];
			}
		}
		return "";
	}

	private String getStartWith(String methodName) {
		StringTokenizer tokenizer = new StringTokenizer(prefixes, ",");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (methodName.startsWith(token)) {
				return token;
			}
		}
		return "";
	}

	public void setPrefixes(String prefixes) {
		this.prefixes = prefixes;
	}

}
