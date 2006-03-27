package ubic.basecode.math.distribution;

/**
 * An interface that describes objects that can produce probabilities according to some distribution.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public interface ProbabilityComputer {

    /**
     * Return the probability associated with a certain value.The upper tail of the associated distribution is returned.
     * 
     * @param value
     * @return
     */
    public double probability( double value );

    /**
     * Return the probability associated with a certain value, with choice of tail.
     * 
     * @param value
     * @param upperTail
     * @return
     */
    public double probability( double value, boolean upperTail );

}