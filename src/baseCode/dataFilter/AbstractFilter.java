/*
 * Created on Jun 16, 2004
 *
 */
package baseCode.dataFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import baseCode.dataStructure.NamedMatrix;

/**
 * @author Owner
 * 
 */
public class AbstractFilter implements Filter {

	protected static Log log = LogFactory.getLog(Filter.class);


	public NamedMatrix filter(NamedMatrix data) {
		return null;
	}

}
