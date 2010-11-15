package ar.com.tunuyan.dao;

/**
 * Simple paginator descriptor for dynamic finder.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * @date Jun 16, 2010
 */
public class GenericPaginator {
	private int offset;

	private int maxResults;

	private String order;

	public GenericPaginator() {

	}

	public GenericPaginator(int offset, int maxResults, String order) {
		super();
		this.offset = offset;
		this.maxResults = maxResults;
		this.order = order;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
