package baseCode.dataFilter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import baseCode.dataStructure.NamedMatrix;
import baseCode.dataStructure.StringMatrix2DNamed;

/**
 * Filter a data matrix according to flags given in a separate matrix.
 * <p>
 * The flags can be 'A', 'P' or 'M', for absent, present and marginal, following
 * the Affymetrix convention.
 * 
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution:: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class AbsentFilter extends AbstractFilter implements Filter {

    private StringMatrix2DNamed flags = null;

    private double minPresentFraction = 0.0;

    private int minPresentCount = 0;

    private boolean keepMarginal = false;

    private boolean fractionIsSet = false;

    private boolean countIsSet = false;

    private boolean flagsSet = false;

    /**
     * 
     * @param f
     *            the matrix containing the flags.
     */
    public void setFlagMatrix(StringMatrix2DNamed f) {
        flags = f;
        flagsSet = true;
    }

    /**
     * 
     * @param k
     *            the minimum fraction of present values that there must be, in
     *            order to keep the row.
     */
    public void setMinPresentFraction(double k) {
        minPresentFraction = k;
        fractionIsSet = true;
    }

    /**
     * 
     * @param k
     *            the minimum number of present values there must be in order to
     *            keep the row.
     */
    public void setMinPresentCount(int k) {
        minPresentCount = k;
        countIsSet = false;
    }

    /**
     * 
     * @param k
     *            whether to count 'marginal' as 'present'. Default is false.
     */
    public void setKeepMarginal(boolean k) {
        keepMarginal = k;
    }

    /**
     * The data is going to be filtered in accordance to strings in 'flags'.
     * These are either 'A', 'P' or 'M' for absent, present and marginal.
     * 
     * @param data
     *            The input matrix
     * @return Matrix after filtering.
     * @todo finish implementing this.
     */
    public NamedMatrix filter(NamedMatrix data) {

        int numRows = data.rows();
        int numCols = data.columns();

        if (flags == null) {
            throw new IllegalArgumentException("Flag matrix has not been set");
        }

        // no filtering requested.
        if (!fractionIsSet && !countIsSet) {
            return data;
        }

        if (!flagsSet) {
            return data;
        }

        if (flags == null || flags.rows() < numRows
                || flags.columns() < numCols) {
            throw new IllegalStateException("Flags do not match up with data");
        }

        // nothing will happen.
        if (minPresentFraction == 0.0 && minPresentCount == 0) {
            return data;
        }

        Vector MTemp = new Vector();
        Vector rowNames = new Vector();

        int kept = 0;
        for (int i = 0; i < numRows; i++) {
            String name = data.getRowName(i);
            int numPresent = 0;
            for (int j = 0; j < numCols; j++) {
                String colName = data.getColName(j);

                // count missing values in the data as "absent", whatever the
                // flag really is.
                if (data.isMissing(flags.getRowIndexByName(name), flags
                        .getColIndexByName(colName))) {
                    continue;
                }

                String flag = (String) flags.get(flags.getRowIndexByName(name),
                        flags.getColIndexByName(colName));

                if (flag.equals("A")) {
                    continue;
                }

                if (flag.equals("M") && !keepMarginal) {
                    continue;
                }

                numPresent++;
            }

            if (( countIsSet && numPresent >= minPresentCount )
                    || ( fractionIsSet && (double) numPresent / numCols >= minPresentFraction )) {
                MTemp.add(data.getRowObj(i));
                rowNames.add(name);
                kept++;
            }
        }

        NamedMatrix returnval = null;
        try {
            Constructor cr = data.getClass().getConstructor(
                    new Class[] { int.class, int.class });
            returnval = (NamedMatrix) cr.newInstance(new Object[] {
                    new Integer(MTemp.size()), new Integer(numCols) });
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < MTemp.size(); i++) {
            for (int j = 0; j < numCols; j++) {
                returnval.set(i, j, ( (Object[]) MTemp.get(i) )[j]);
            }
        }
        returnval.setColumnNames(data.getColNames());
        returnval.setRowNames(rowNames);

        log.debug("There are " + kept + " rows left after filtering.");

        return ( returnval );
    }

}