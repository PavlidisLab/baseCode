package ubic.basecode.util.r;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

import java.util.List;

/**
 * Representation of the R htest class. This class is returned by the t.test and cor.test methods in R.
 *
 * @author paul
 *
 */
public class HTest {

    private String dataname = "";
    private String method = "";
    private double parameter = Double.NaN;
    private double pvalue = Double.NaN;
    private double statistic = Double.NaN;

    public HTest() {
    }

    public HTest( RList rexp ) throws REXPMismatchException {
        if ( rexp == null || rexp.size() == 0 ) {
            return;
        }
        List<String> names = rexp.names;
        for ( String n : names ) {
            REXP o = ( REXP ) rexp.get( n );
            if ( n.equals( "method" ) ) {
                this.method = o.asString();
            } else if ( n.equals( "statistic" ) ) {
                this.statistic = o.asDouble();
            } else if ( n.equals( "parameter" ) ) {
                this.parameter = o.asDouble();
            } else if ( n.equals( "p.value" ) ) {
                this.pvalue = o.asDouble();
            } else if ( n.equals( "conf.inf" ) ) {
                // attribute conf.level and two-element vector of the limits.
            } else if ( n.equals( "data.name" ) ) {
                this.dataname = o.asString();
            }
        }
    }

    public String getDataname() {
        return dataname;
    }

    public String getMethod() {
        return method;
    }

    public double getParameter() {
        return parameter;
    }

    public double getPvalue() {
        return pvalue;
    }

    public double getStatistic() {
        return statistic;
    }
}
