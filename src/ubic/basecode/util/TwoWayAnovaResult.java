package ubic.basecode.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A simple (flat) structure to hold Two Way Anova results. The user must know how they are storing the results for two
 * way anova with and without interactions. For example, for two way anova with interactions, the user may wish to store
 * the pvalue for main effect 1, main effect 2, and interaction effect like:
 * <p>
 * pvalue[0] -> p value for main effect 1
 * <p>
 * pvalue[1] -> p value for main effect 2
 * <p>
 * pvalue[2] -> p value for interaction effect
 * 
 * @author keshav
 * @version $Id$
 */
public class TwoWayAnovaResult {

    private double[] pvalues = null;
    private double[] statistics = null;

    /**
     * @param pvalues
     * @param statistics
     */
    public TwoWayAnovaResult( LinkedHashMap<String, double[]> pvalues, LinkedHashMap<String, double[]> statistics ) {

        if ( pvalues.size() != statistics.size() ) {
            throw new RuntimeException( "Number of keys for pvalues and statistics must match." );
        }

        Set<String> keys = pvalues.keySet();
        ArrayList<Double> pvalsAsList = new ArrayList<Double>();
        ArrayList<Double> statisticsAsList = new ArrayList<Double>();
        for ( String key : keys ) {
            double[] pvs = pvalues.get( key );
            double[] sts = statistics.get( key );
            for ( int j = 0; j < pvs.length; j++ ) {
                pvalsAsList.add( pvs[j] );
                statisticsAsList.add( sts[j] );
            }
        }

        this.pvalues = new double[pvalsAsList.size()];
        this.statistics = new double[statisticsAsList.size()];
        Iterator<Double> pIter = pvalsAsList.iterator();
        Iterator<Double> sIter = statisticsAsList.iterator();
        for ( int i = 0; i < this.pvalues.length; i++ ) {
            this.pvalues[i] = pIter.next();
            this.statistics[i] = sIter.next();
        }
    }

    /**
     * @return
     */
    public double[] getPvalues() {
        return this.pvalues;
    }

    /**
     * @return
     */
    public double[] getStatistics() {
        return this.statistics;
    }
}
