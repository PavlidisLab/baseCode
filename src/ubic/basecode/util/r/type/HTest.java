package ubic.basecode.util.r.type;

import java.util.List;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

/**
 * Representation of the R htest class. This class is returned by the t.test and cor.test methods in R.
 * 
 * @author paul
 * @version $Id$
 */
public class HTest {

    private String alternative = "";

    private String dataname = "";

    private Double estimate = Double.NaN;

    private String method = "";

    private Double nullvalue = Double.NaN;

    private Double parameter = Double.NaN;

    private Double pvalue = Double.NaN;

    private Double statistic = Double.NaN;

    public HTest() {
    }

    @SuppressWarnings("unchecked")
    public HTest( RList rexp ) {

        if ( rexp == null || rexp.size() == 0 ) {
            return;
        }

        List<String> names = rexp.names;

        try {
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
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    public String getAlternative() {
        return alternative;
    }

    public String getDataname() {
        return dataname;
    }

    public Double getEstimate() {
        return estimate;
    }

    public String getMethod() {
        return method;
    }

    public Double getNullvalue() {
        return nullvalue;
    }

    public Double getParameter() {
        return parameter;
    }

    public Double getPvalue() {
        return pvalue;
    }

    public Double getStatistic() {
        return statistic;
    }

}
