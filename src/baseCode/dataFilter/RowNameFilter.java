/*
 * Created on Jun 16, 2004
 *
 */
package baseCode.dataFilter;

import java.util.Set;
import java.util.Vector;
import java.lang.reflect.*;

import baseCode.dataStructure.NamedMatrix;

/**
 * @author Owner
 * 
 */
public class RowNameFilter extends AbstractFilter implements Filter {

	private boolean exclude = false;
	private Set filterNames;

	private RowNameFilter() {} // so we are forced to use the other constructors.


/**
 * 
 * @param namesToFilter
 * @param exclude Set to true if you want the list to indicate items to be 
 * skipped, rather than selected.
 */
	public RowNameFilter(Set namesToFilter, boolean exclude) {
		this(namesToFilter);
		this.exclude = exclude;
	}
	
	public RowNameFilter(Set namesToFilter) {
 		filterNames = namesToFilter;
	}

	/**
	 * Filter according to row names.
	 */
	public NamedMatrix filter(NamedMatrix data) {
		Vector MTemp = new Vector();
	 Vector rowNames = new Vector();
	 int numRows = data.rows();
	 int numCols = data.columns();

	 int kept = 0;
	 for (int i = 0; i < numRows; i++) {
		String name = data.getRowName(i);

		// apply the rules.
		if (filterNames.contains(name)) {
			if (exclude) {
				continue;
			}
			MTemp.add(data.getRowObj(i));
									rowNames.add(name);
									kept++;
		}

	if (exclude) {
	
		MTemp.add(data.getRowObj(i));
						rowNames.add(name);
						kept++;
	}
	 }

	NamedMatrix returnval = null;
	try {
		Constructor cr = data.getClass().getConstructor(new Class[]{int.class, int.class});
		 returnval =
			(NamedMatrix) cr.newInstance(
				new Object[] { new Integer(MTemp.size()), new Integer(numCols)});
	} catch (SecurityException e) {
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		e.printStackTrace();
	} catch (InstantiationException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		e.printStackTrace();
	}

	 for (int i = 0; i < MTemp.size(); i++) {
		for (int j = 0; j < numCols; j++) {
		   returnval.set(i, j,  ( (Object[]) MTemp.get(i))[j]);
		}
	 }
	 returnval.setColumnNames(data.getColNames());
	 returnval.setRowNames(rowNames);

	 log.info(
		 "There are " + kept + " rows left after filtering.");

	 return (returnval);
	}

	

}
