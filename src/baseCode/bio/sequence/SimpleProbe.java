package baseCode.bio.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.biojava.bio.seq.Sequence;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class SimpleProbe implements Probe {
    private String probeId;
    private Sequence targetSequence;
    protected static final Log log = LogFactory.getLog( SimpleProbe.class );

    /**
     * @param probeSetId
     * @param ns
     */
    public SimpleProbe( String probeId, Sequence ns ) {
        this.probeId = probeId;
        this.targetSequence = ns;
    }

    /**
     * Compute just any overlap the compare sequence has with the target on the right side.
     * 
     * @param query
     * @return The index of the end of the overlap. If zero, there is no overlap. In other words, this is the amount
     *         that needs to be trimmed off the compare sequence if we are going to join it on to the target without
     *         generating redundancy.
     */
    public int rightHandOverlap( Sequence target ) {
        return this.rightHandOverlap( target, this.getSequence() );
    }

    /**
     * Compute just any overlap the compare sequence has with the target on the right side.
     * 
     * @param query
     * @param target
     * @return The index of the end of the overlap. If zero, there is no overlap. In other words, this is the amount
     *         that needs to be trimmed off the compare sequence if we are going to join it on to the target without
     *         generating redundancy.
     */
    private int rightHandOverlap( Sequence target, Sequence query   ) {

        String targetString = target.seqString();
        String queryString = query.seqString();

        // match the end of the target with the beginning of the query. We start with the whole thing
        for ( int i = 0; i < targetString.length(); i++ ) {
            String targetSub = targetString.substring( i );

            if ( queryString.indexOf( targetSub ) == 0 ) {
                return targetSub.length();
            }
        }

        return 0;
    }

    public Overlap overlap( Probe compare ) {
        return this.overlap( compare.getSequence() );
    }

    public Overlap overlap( Sequence compare ) {
        return computeOverlap( compare, targetSequence );
    }

    /**
     * Overlap (perhaps overhang is a better term) is defined as a common substring that is at the end of one of the two
     * strings. If a string is contained entirely within another longer string, there is no overlap. This does NOT
     * search the reverse complement of the sequence. Searches are undertaken from the end (right-hand side) of the
     * longerSequence
     * 
     * @throws IllegalAlphabetException
     * @param pattern
     * @param target - the sequence to search
     * @return and Overlap object describing the results.
     */
    private Overlap computeOverlap( Sequence pattern, Sequence target ) {

        int patternLength = pattern.length();

        String targetString = target.seqString();
        String patternString = pattern.seqString();

        for ( int i = patternLength - 1; i > 0; i-- ) {

            String patternSubString = patternString.substring( 0, i ); // hack of the right-hand side of
            // the sequence.

            int matchIndexInTarget = targetString.lastIndexOf( patternSubString );

            if ( matchIndexInTarget < 0 ) continue;

            // There are two main cases: First, the match is at the left side.
            if ( matchIndexInTarget == 0 ) {
                // then the start index for the match in the query is i.
                // return new Overlap( i, matchIndexInTarget, patternLength, targetString.length() );
            } else {
                // otherwise, the match must be at the right edge.
                return new Overlap( 0, matchIndexInTarget, patternLength, targetString.length() );
            }

        }

        // now try from the other end
        for ( int i = 0; i < patternLength; i++ ) {

            String patternSubString = patternString.substring( i ); // progressively hack of the left-hand side
            int matchIndexInTarget = targetString.lastIndexOf( patternSubString );
            if ( matchIndexInTarget < 0 ) continue;

            // There are two main cases: First, the match is at the left side.
            if ( matchIndexInTarget == 0 ) {
                // then the start index for the match in the query is i.
                return new Overlap( i, matchIndexInTarget, patternLength, targetString.length() );
            }
            // otherwise, the match must be at the right edge.
            // return new Overlap( 0, matchIndexInTarget, patternLength, targetString.length() );

        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.bio.sequence.Probe#getIdentifier()
     */
    public String getIdentifier() {
        return this.probeId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.bio.sequence.Probe#getSequence()
     */
    public Sequence getSequence() {
        return this.targetSequence;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.bio.sequence.Probe#length()
     */
    public int length() {
        return targetSequence.length();
    }

}

/**
 * Helper class.
 */
class Overlap {
    private int sourceStartIndex;
    private int targetStartIndex;
    private final int targetLength;
    private final int sourceLength;
    protected static final Log log = LogFactory.getLog( Overlap.class );

    /**
     * @param sourceStartIndex
     * @param targetStartIndex
     */
    public Overlap( int sourceStartIndex, int targetStartIndex, int sourceLength, int targetLength ) {
        super();

        this.sourceStartIndex = sourceStartIndex;
        this.targetStartIndex = targetStartIndex;
        this.sourceLength = sourceLength;
        this.targetLength = targetLength;
    }

    public int getSourceStartIndex() {
        return this.sourceStartIndex;
    }

    public int getTargetStartIndex() {
        return this.targetStartIndex;
    }

    /**
     * Use in combination with overlapsEnd() and overlapsStart() to determine where the overhang, if any, is.
     * 
     * @return -1 if there is no match.
     */
    public int overlapLength() {
        assert !( overlapsEnd() && overlapsStart() ) : "Can't overlap both start and end!";
        if ( overlapsEnd() ) {
            log.debug( "overlap at right" );
            return ( targetLength - targetStartIndex + 1 );
        } else if ( overlapsStart() ) {
            log.debug( "overlap at left" );
            return sourceLength - sourceStartIndex;
        }
        log.debug( "err. no overlap" );
        return -1;
    }

    public boolean overlapsEnd() {
        return ( sourceStartIndex == 0 && targetStartIndex > 0 );
    }

    public boolean overlapsStart() {
        return ( targetStartIndex == 0 && sourceStartIndex > 0 );
    }
}
