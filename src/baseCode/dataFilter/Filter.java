package baseCode.dataFilter;

import baseCode.dataStructure.matrix.NamedMatrix;

/**
 * An interface representing the functionality of a class that can filter 2-d matrix-based data by row-oriented
 * criteria.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Pavlidis
 * @version $Id$
 */
public interface Filter {

   /**
    * Filter the data
    * 
    * @param data a NamedMatrix. Some types of filters require that this be of a particular type of implementation of
    *        the Filter interface.
    * @return The resulting filtered matrix
    */
   public NamedMatrix filter( NamedMatrix data );
}