package baseCode.dataFilter;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import baseCode.dataStructure.NamedMatrix;

/**
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public abstract class AbstractFilter implements Filter {

    protected static Log log = LogFactory.getLog(Filter.class);

    public NamedMatrix filter(NamedMatrix data) {
        return null;
    }

    protected NamedMatrix getOutputMatrix(NamedMatrix data, int numRows,
            int numCols) {
        NamedMatrix returnval = null;
        try {
            Constructor cr = data.getClass().getConstructor(
                    new Class[] { int.class, int.class });
            returnval = (NamedMatrix) cr.newInstance(new Object[] {
                    new Integer(numRows), new Integer(numCols) });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnval;
    }

}