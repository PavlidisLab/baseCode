package baseCode.bio.sequence;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalAlphabetException;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public interface Probe {

    public Overlap overlap( Probe compare ) throws IllegalAlphabetException;

    public Overlap overlap( Sequence compare ) throws IllegalAlphabetException;

    public String getIdentifier();

    public Sequence getSequence();

    public int length();

}